Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL                   '宣告SQL物件
    Dim CursorDBPoint As Cursor         '宣告資料庫記錄指標
End Sub

Sub Globals
    '井字遊戲中的九個按鈕
    Dim btn1 As Button : Dim btn2 As Button
    Dim btn3 As Button : Dim btn4 As Button
    Dim btn5 As Button : Dim btn6 As Button
    Dim btn7 As Button : Dim btn8 As Button
    Dim btn9 As Button
	
    Dim Arraybtn() As Button            '宣告按鈕陣列
	Dim xo(9) As String                 '存放資料格
	
	Dim oxFlag As Boolean               '記錄O或X的目前的狀態
	Dim btnBegin As Button              '重新開始
	Dim lblResutl As Label              '顯示標題及O贏或X贏
	Dim btnColor_Gray As ColorDrawable  '灰色
	Dim btnColor_Blue As ColorDrawable  '藍色
	
	Dim btnExit As Button               '結束離開
	Dim O_Wins As Int                   'O贏場數
	Dim X_Wins As Int                   'X贏場數
	Dim Tie As Int                      '平手場數
	Dim i As Int                        '計數變數
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
       SQLCmd.Initialize(File.DirDefaultExternal, "MyGameScore.sqlite", True) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("Play1")                   '使用「Play1(版面配置檔)」來輸出
    Activity.Title = "兩人對戰"                    '本頁的標題名稱
    Arraybtn =Array As Button(btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9) '按鈕陣列
	btnColor_Gray.Initialize(Colors.Gray,10dip)    '灰色
	btnColor_Blue.Initialize(Colors.Yellow,10dip)  '黃色
End Sub

Sub Activity_Resume
    '設定井字遊戲中的九個按鈕為沒有作用
	For i=0 To 8
	   Arraybtn(i).Enabled = False
    Next 
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnBegin_Click  '重新開始
	For i=0 To 8
	  Arraybtn(i).Enabled = True            '設定井字遊戲中的九個按鈕有作用
	  Arraybtn(i).Background=btnColor_Gray  '灰色
      Arraybtn(i).Text =""                  '清除O與X的文字內容
	  xo(i)=0                               '存放資料格預設值為0
    Next 
    oxFlag = True                           '設定第一次在按下滑鼠左鍵時會得到O(第一個玩家設為O)
	btnBegin.Enabled =False                 '「重新開始」按鈕沒作用
	lblResutl.Text="==井字遊戲=="           '顯示「井字遊戲」
End Sub

'btn1~btn9的事件處理程序
Sub Button_Click  
  Dim Send As Button 
  Send=Sender   '取得事件來源
  '處理按O及X的顯示及內含值指定動作
  If xo(Send.Tag)=0 Then                '如果是空格
    If oxFlag = True Then                  
       Arraybtn(Send.Tag).Text = "O"    '第一個玩家設為O，並顯示O在按鈕上
       xo(Send.Tag) = 1                 '在按下時，填入1到指定的資料格中
    Else
       Arraybtn(Send.Tag).Text = "X"    '第二個玩家設為X，並顯示X在按鈕上
       xo(Send.Tag) = 2                 '在按下時，填入2到指定的資料格中
    End If
    oxFlag = Not(oxFlag)                '作O及X的切換動作
    CheckWin                            '檢查O或X贏
  Else
    Msgbox ("此地已下過,不可再下!!","錯誤")
  End If
End Sub

Sub CheckWin  '檢查O或X贏
  '第一列3個0
  If xo(0) * xo(1) * xo(2) = 1 Then 
	 Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(1).Background=btnColor_Blue
	 Arraybtn(2).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '第二列3個0
  If xo(3) * xo(4) * xo(5) = 1 Then 
     Arraybtn(3).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(5).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '第三列3個0
  If xo(6) * xo(7) * xo(8) = 1 Then 
     Arraybtn(6).Background=btnColor_Blue
	 Arraybtn(7).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  
  '第一行3個0
  If xo(0) * xo(3) * xo(6) = 1 Then 
     Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(3).Background=btnColor_Blue
	 Arraybtn(6).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '第二行3個0
  If xo(1) * xo(4) * xo(7) = 1 Then 
     Arraybtn(1).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(7).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '第三行3個0
  If xo(2) * xo(5) * xo(8) = 1 Then 
     Arraybtn(2).Background=btnColor_Blue
	 Arraybtn(5).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
     lblResutl.Text = "O贏!"
  End If
  '對角線(左上到右下)都是O	
  If xo(0) * xo(4) * xo(8) = 1 Then 
     Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '對角線(右上到左下)都是O		
  If xo(2) * xo(4) * xo(6) = 1 Then
     Arraybtn(2).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(6).Background=btnColor_Blue
	 lblResutl.Text = "O贏!"
  End If
  '第一列3個X
  If xo(0) * xo(1) * xo(2) = 8 Then 
     Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(1).Background=btnColor_Blue
	 Arraybtn(2).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If
  '第二列3個X
  If xo(3) * xo(4) * xo(5) = 8 Then 
     Arraybtn(3).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(5).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '第三列3個X
  If xo(6) * xo(7) * xo(8) = 8 Then 
     Arraybtn(6).Background=btnColor_Blue
	 Arraybtn(7).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '第一行3個X
  If xo(0) * xo(3) * xo(6) = 8 Then 
     Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(3).Background=btnColor_Blue
	 Arraybtn(6).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '第二行3個X
  If xo(1) * xo(4) * xo(7) = 8 Then 
	 Arraybtn(1).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(7).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '第三行3個X
  If xo(2) * xo(5) * xo(8) = 8 Then 
     Arraybtn(2).Background=btnColor_Blue
	 Arraybtn(5).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '對角線(左上到右下)都是X	
  If xo(0) * xo(4) * xo(8) = 8 Then 
     Arraybtn(0).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(8).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  '對角線(右上到左下)都是X		
  If xo(2) * xo(4) * xo(6) = 8 Then 
     Arraybtn(2).Background=btnColor_Blue
	 Arraybtn(4).Background=btnColor_Blue
	 Arraybtn(6).Background=btnColor_Blue
   	 lblResutl.Text = "X贏!"
  End If		
  If (lblResutl.Text<>"== 井字遊戲==") AND( lblResutl.Text="O贏!" OR lblResutl.Text = "X贏!" OR lblResutl.Text = "平手!" ) Then
      Msgbox(lblResutl.Text,"遊戲結果!")
      For i=0 To 8
	      Arraybtn(i).Enabled = False
      Next 
	  btnBegin.Enabled =True
	  QueryScore  '查詢目前戰狀成績
	  Select lblResutl.Text
		  Case "O贏!"
	   	     O_Wins=O_Wins+1
		  Case "X贏!"
		     X_Wins=X_Wins+1
      End Select		
	 SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。
  Else If Arraybtn(0).Text<>"" AND  Arraybtn(1).Text<>"" AND Arraybtn(2).Text<>"" AND Arraybtn(2).Text<>"" _
        AND Arraybtn(3).Text<>"" AND Arraybtn(4).Text<>"" AND Arraybtn(5).Text<>"" _
		AND Arraybtn(6).Text<>"" AND Arraybtn(7).Text<>"" AND Arraybtn(8).Text<>"" Then
    		lblResutl.Text = "平手!"
            Msgbox("平手","遊戲結果!")
			btnBegin.Enabled =True
			QueryScore     '查詢目前戰狀成績
		    Tie=Tie+1
		    SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。
   End If
End Sub

Sub QueryScore() '查詢目前戰狀成績
 Dim strSQLite As String="Select * FROM 遊戲歷程表 where 對戰方式='兩人對戰'"
 CursorDBPoint = SQLCmd.ExecQuery(strSQLite)       '執行SQL指令
 For i = 0 To CursorDBPoint.RowCount - 1           '控制「記錄」的筆數
    CursorDBPoint.Position = i                     '記錄指標從第1筆開始(索引為0)
  	O_Wins=CursorDBPoint.GetString("O贏")          '取得「O贏」的次數
  	X_Wins=CursorDBPoint.GetString("X贏")          '取得「X贏」的次數
  	Tie=CursorDBPoint.GetString("平手")            '取得「平手」的次數
 Next
End Sub	

Sub SaveScoreDB()  '儲存成績副程式
   Dim strSQL As String
   strSQL = "UPDATE 遊戲歷程表 SET O贏='" & O_Wins & "',X贏='" & X_Wins & "',平手='" & Tie & "' where 對戰方式='兩人對戰'"
   SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
   ToastMessageShow("更新最新成績資料(到資料庫中)...", False)
End Sub

Sub btnExit_Click
    Activity.Finish()      '活動完成
    StartActivity(Main)    '回首頁
End Sub