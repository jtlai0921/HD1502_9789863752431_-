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
    Dim SaveXO(9) As Int                '記錄目前的空格狀態
	Dim xo(9) As String                 '存放資料格
	Dim counter As Int                  '目前可下的空格數目
    Dim Win(9) As Int                   '記錄八種勝利的狀態
    Dim sp As Int 
	Dim i As Int                        '計數變數
	Dim btnBegin As Button              '重新開始
	Dim lblResutl As Label              '顯示標題及O贏或X贏
	Dim btnColor_Gray As ColorDrawable  '灰色
	Dim btnColor_Blue As ColorDrawable  '藍色
	Dim btnExit As Button               '結束離開
	Dim WinFlag As Boolean              '記錄目前是否O贏或X贏或平手
	Dim O_Wins As Int                   'O贏場數
	Dim X_Wins As Int                   'X贏場數
	Dim Tie As Int                      '平手場數
	
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyGameScore.sqlite", True) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("Play2")    '使用「Play2(版面配置檔)」來輸出
    Activity.Title = "電腦對戰"     '本頁的標題名稱
    Arraybtn =Array As Button(btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9)   '按鈕陣列
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

Sub btnBegin_Click'重新開始         
	For i=0 To 8
	  Arraybtn(i).Enabled = True           '設定井字遊戲中的九個按鈕有作用
	  Arraybtn(i).Background=btnColor_Gray '灰色
      Arraybtn(i).Text =""                 '清除O與X的文字內容
	  SaveXO(i)=0                          '存放資料格預設值為0
    Next 
	btnBegin.Enabled =False                '「重新開始」按鈕沒作用
	WinFlag=False                          '預設目前對戰情況為尚未有一方贏
	lblResutl.Text="=井字遊戲="	           '顯示「井字遊戲」
End Sub

'btn1~btn9的事件處理程序
Sub Button_Click  
  Dim Send As Button 
  Send=Sender   '取得事件來源
  '處理按O及X的顯示及內含值指定動作
  If SaveXO(Send.Tag) = 0 Then        '如果是空格
     Arraybtn(Send.Tag).Text = "O"    '玩家設為O，並顯示O在按鈕上
     SaveXO(Send.Tag) = 1             '在按下時，填入1到指定的資料格中
     count_counter                    '呼叫統計目前空格數之副程式
     CheckWin                         '檢查O或X贏
    If WinFlag=False Then             '如果目前對戰情況為尚未有一方贏
      If counter>6 Then               '如果目前空格數大於6
         PcPlay1                      '呼叫前三次，電腦亂數出牌(亦即OXO)
      Else
         PcPlay2                      '呼叫後六次(阻擋及進改)
      End If
    End If
  Else
    Msgbox ("此地已下過,不可再下!!","錯誤")
  End If
  CheckError(Send.Tag)  '呼叫「檢查例外處理」之副程式
End Sub
'「檢查例外處理」之副程式
Sub CheckError(errTag As Int)
  Dim t As Int = 0
  For i = 0 To 8
      If SaveXO(i) = 0 Then
	     t = t + 1
	  End If
  Next
  If t=8 AND Arraybtn(errTag).Text = "X" Then
     Arraybtn(errTag).Text = "O"
     SaveXO(errTag) = 1
	If counter>6 Then
      PcPlay1  '呼叫前三次，電腦亂數出牌(亦即OXO)
    Else
      PcPlay2  '呼叫後六次(阻擋及進改)
    End If
  End If
End Sub

Sub count_counter()   '統計目前空格數之副程式
  '計算尚有格數
  counter = 0
  For i = 0 To 8
      If SaveXO(i) = 0 Then counter = counter + 1
  Next
End Sub

Sub PcPlay1()
  Dim R As Int 
  R = Rnd(0,9) 
  For i=0 To 8
    If SaveXO(i)=0 Then
	  Arraybtn(R).Text = "X"
	  SaveXO(R) =-1
    End If
  Next 
End Sub

Sub PcPlay2()
Dim sp As Int=0
Dim Runtimes1 As Int=1
Dim Runtimes2 As Int=1
Dim Runtimes3 As Int=1
Dim sel As String 
Dim num1,num2,num3 As String
    '設定位置
    xo(1) = "012"
    xo(2) = "345"
    xo(3) = "678"
    xo(4) = "036"
    xo(5) = "147"
    xo(6) = "258"
    xo(7) = "048"
    xo(8) = "246"
    count_counter  '檢查目前的空格數目
    '人下記錄1  電腦下記錄-1
    If counter = 6 Then
	   '第一組
       If SaveXO(4) = -1 AND SaveXO(0) = 1 AND SaveXO(8) = 1 Then
	   	 Arraybtn(3).Text = "X"
         SaveXO(3) = -1 :sp=1
       End If
       If SaveXO(4) = -1 AND SaveXO(2) = 1 AND SaveXO(6) = 1 Then
	   	 Arraybtn(3).Text = "X"
         SaveXO(3) = -1:sp=1
       End If
	   
       If SaveXO(4) = -1 AND SaveXO(1) = 1 AND SaveXO(3) = 1 Then
	   	 Arraybtn(0).Text = "X"
         SaveXO(0) = -1:sp=1
       End If
       If SaveXO(4) = -1 AND SaveXO(1) = 1 AND SaveXO(5) = 1 Then
	   	 Arraybtn(2).Text = "X"
         SaveXO(2) = -1:sp=1
       End If
	   
       If SaveXO(4) = -1 AND SaveXO(3) = 1 AND SaveXO(7) = 1 Then
	   	 Arraybtn(6).Text = "X"
         SaveXO(6) = -1:sp=1
       End If
       If SaveXO(4) = -1 AND SaveXO(5) = 1 AND SaveXO(7) = 1 Then
	   	 Arraybtn(8).Text = "X"
         SaveXO(8) = -1:sp=1
       End If
	   
	   	   '第二組
       If SaveXO(4) = 1 AND SaveXO(0) = -1 AND SaveXO(8) = 1 Then
	   	 Arraybtn(3).Text = "X"
         SaveXO(3) = -1:sp=1
       End If
       If SaveXO(4) = 1 AND SaveXO(6) = 1 AND SaveXO(2) = -1 Then
	   	 Arraybtn(5).Text = "X"
         SaveXO(5) = -1:sp=1
       End If
       If SaveXO(3) = 1 AND SaveXO(6) = 1 AND SaveXO(6) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
	   If SaveXO(0) = 1 AND SaveXO(6) = 1 AND SaveXO(3) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
       If SaveXO(0) = 1 AND SaveXO(3) = 1 AND SaveXO(6) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
	   If SaveXO(4) = 1 AND SaveXO(7) = 1 AND SaveXO(1) = -1 Then
	   	 Arraybtn(0).Text = "X"
         SaveXO(0) = -1:sp=1
       End If    
	   If SaveXO(1) = 1 AND SaveXO(7) = 1 AND SaveXO(4) = -1 Then
	   	 Arraybtn(3).Text = "X"
         SaveXO(3) = -1:sp=1
       End If 	   
	   If SaveXO(1) = 1 AND SaveXO(4) = 1 AND SaveXO(7) = -1 Then
	   	 Arraybtn(6).Text = "X"
         SaveXO(6) = -1:sp=1
       End If 	
	   If SaveXO(5) = 1 AND SaveXO(8) = 1 AND SaveXO(2) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If 	
	   If SaveXO(2) = 1 AND SaveXO(8) = 1 AND SaveXO(5) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If 	
	   If SaveXO(2) = 1 AND SaveXO(5) = 1 AND SaveXO(8) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If 	
	   
	    '第三組

       If SaveXO(1) = 1 AND SaveXO(2) = 1 AND SaveXO(0) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
       If SaveXO(0) = 1 AND SaveXO(2) = 1 AND SaveXO(1) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
       If SaveXO(0) = 1 AND SaveXO(1) = 1 AND SaveXO(2) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If	   

       If SaveXO(4) = 1 AND SaveXO(5) = 1 AND SaveXO(3) = -1 Then
	   	 Arraybtn(6).Text = "X"
         SaveXO(6) = -1:sp=1
       End If
       If SaveXO(3) = 1 AND SaveXO(5) = 1 AND SaveXO(4) = -1 Then
	   	 Arraybtn(8).Text = "X"
         SaveXO(8) = -1:sp=1
       End If
       If SaveXO(3) = 1 AND SaveXO(4) = 1 AND SaveXO(2) = -1 Then
	   	 Arraybtn(8).Text = "X"
         SaveXO(8) = -1:sp=1
       End If
	   
       If SaveXO(7) = 1 AND SaveXO(8) = 1 AND SaveXO(6) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
       If SaveXO(6) = 1 AND SaveXO(8) = 1 AND SaveXO(7) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If
       If SaveXO(6) = 1 AND SaveXO(7) = 1 AND SaveXO(8) = -1 Then
	   	 Arraybtn(4).Text = "X"
         SaveXO(4) = -1:sp=1
       End If	
		 Runtimes1=Runtimes1+1
	  '  ToastMessageShow ("pass1",False)
    End If
	count_win
	CheckWin
    '「電腦」快連成一直線了，所以先連
    For i = 1 To 8
        If Win(i) = -2 AND (Runtimes1=1) AND (Runtimes2=1) AND (Runtimes3=1) Then
		   sp=1
		   sel = xo(i)
           num1 = sel.CharAt(0)
           num2 = sel.CharAt(1)
           num3 = sel.CharAt(2)
           If SaveXO(num1) = 0 Then 
	   	      Arraybtn(num1).Text = "X"
              SaveXO(num1) = -1
           End If
           If SaveXO(num2) = 0 Then 
	   	      Arraybtn(num2).Text = "X"
              SaveXO(num2) = -1
           End If
           If SaveXO(num3) = 0 Then 
	   	      Arraybtn(num3).Text = "X"
              SaveXO(num3) = -1
           End If
		   	Runtimes2=Runtimes2+1
		 CheckWin
        End If
    Next
	count_win
    '「玩家」快連成一直線了，所以電腦阻擋
    For i = 1 To 8
        If Win(i) = 2 AND (Runtimes2=1) AND (Runtimes3=1) Then
		   sp=1
		   sel = xo(i)
           num1 = sel.CharAt(0)
           num2 = sel.CharAt(1)
           num3 = sel.CharAt(2)
           If SaveXO(num1) = 0 Then 
	   	      Arraybtn(num1).Text = "X"
              SaveXO(num1) = -1
           End If
           If SaveXO(num2) = 0 Then 
	   	      Arraybtn(num2).Text = "X"
              SaveXO(num2) = -1
           End If
           If SaveXO(num3) = 0 Then 
	   	      Arraybtn(num3).Text = "X"
              SaveXO(num3) = -1
           End If
		  Runtimes3=Runtimes3+1
        End If
    Next 
	Runtimes2=1	
	Runtimes3=1
	If sp=0 Then
	   PcPlay3
	End If
End Sub

Sub PcPlay3()
  For i=0 To 8
    If SaveXO(i)=0 Then
	  Arraybtn(i).Text = "X"
	  SaveXO(i) =-1
	  Exit
    End If
  Next 
End Sub
Sub count_win()
  Win(1) = SaveXO(0) + SaveXO(1) + SaveXO(2)  '3(您) or -3(電腦) 
  Win(2) = SaveXO(3) + SaveXO(4) + SaveXO(5)  '3(您) or -3(電腦) 
  Win(3) = SaveXO(6) + SaveXO(7) + SaveXO(8)  '3(您) or -3(電腦) 
  Win(4) = SaveXO(0) + SaveXO(3) + SaveXO(6)  '3(您) or -3(電腦)  
  Win(5) = SaveXO(1) + SaveXO(4) + SaveXO(7)  '3(您) or -3(電腦)  
  Win(6) = SaveXO(2) + SaveXO(5) + SaveXO(8)  '3(您) or -3(電腦) 
  Win(7) = SaveXO(0) + SaveXO(4) + SaveXO(8)  '3(您) or -3(電腦)  
  Win(8) = SaveXO(2) + SaveXO(4) + SaveXO(6)  '3(您) or -3(電腦) 
End Sub

Sub CheckWin
        '第一列3個0
        If SaveXO(0) + SaveXO(1) + SaveXO(2) = 3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(1).Background=btnColor_Blue
		  Arraybtn(2).Background=btnColor_Blue
		  lblResutl.Text = "O贏!" 
		End If
		'第二列3個0
        If SaveXO(3) + SaveXO(4) + SaveXO(5) = 3 Then 
		  Arraybtn(3).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(5).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		'第三列3個0
        If SaveXO(6) + SaveXO(7) + SaveXO(8) = 3 Then 
		  Arraybtn(6).Background=btnColor_Blue
		  Arraybtn(7).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		
		'第一行3個0
		If SaveXO(0) + SaveXO(3) + SaveXO(6) = 3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(3).Background=btnColor_Blue
		  Arraybtn(6).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		'第二行3個0
        If SaveXO(1) + SaveXO(4) + SaveXO(7) = 3 Then 
		  Arraybtn(1).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(7).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		'第三行3個0
        If SaveXO(2) + SaveXO(5) + SaveXO(8) = 3 Then 
		  Arraybtn(2).Background=btnColor_Blue
		  Arraybtn(5).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		'對角線(左上到右下)都是O	
        If SaveXO(0) + SaveXO(4) + SaveXO(8) = 3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
		'對角線(右上到左下)都是O		
        If SaveXO(2) + SaveXO(4) + SaveXO(6) = 3 Then
		  Arraybtn(2).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(6).Background=btnColor_Blue
		  lblResutl.Text = "O贏!"
		End If
        '第一列3個X
        If SaveXO(0) + SaveXO(1) + SaveXO(2) = -3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(1).Background=btnColor_Blue
		  Arraybtn(2).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If
        '第二列3個X
        If SaveXO(3) + SaveXO(4) + SaveXO(5) = -3 Then 
		  Arraybtn(3).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(5).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
        '第三列3個X
        If SaveXO(6) + SaveXO(7) + SaveXO(8) = -3 Then 
		  Arraybtn(6).Background=btnColor_Blue
		  Arraybtn(7).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
		'第一行3個X
        If SaveXO(0) + SaveXO(3) + SaveXO(6) = -3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(3).Background=btnColor_Blue
		  Arraybtn(6).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
		'第二行3個X
        If SaveXO(1) + SaveXO(4) + SaveXO(7) = -3 Then 
		  Arraybtn(1).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(7).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
		'第三行3個X
        If SaveXO(2) + SaveXO(5) + SaveXO(8) = -3 Then 
		  Arraybtn(2).Background=btnColor_Blue
		  Arraybtn(5).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!"
		End If		
		'對角線(左上到右下)都是X	
        If SaveXO(0) + SaveXO(4) + SaveXO(8) = -3 Then 
		  Arraybtn(0).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(8).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
		'對角線(右上到左下)都是X		
        If SaveXO(2) + SaveXO(4) + SaveXO(6) = -3 Then 
		  Arraybtn(2).Background=btnColor_Blue
		  Arraybtn(4).Background=btnColor_Blue
		  Arraybtn(6).Background=btnColor_Blue
   		  lblResutl.Text = "X贏!" 
		End If		
		
		If lblResutl.Text="O贏!" Then
	        QueryScore  '查詢目前戰狀成績
			O_Wins=O_Wins+1
			SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。
		Else If lblResutl.Text="X贏!"  Then
	        QueryScore  '查詢目前戰狀成績
		    X_Wins=X_Wins+1
			SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。
		Else If Arraybtn(0).Text<>"" AND  Arraybtn(1).Text<>"" AND Arraybtn(2).Text<>"" AND Arraybtn(2).Text<>"" _
		    AND Arraybtn(3).Text<>"" AND Arraybtn(4).Text<>"" AND Arraybtn(5).Text<>"" _
			AND Arraybtn(6).Text<>"" AND Arraybtn(7).Text<>"" AND Arraybtn(8).Text<>"" Then
			lblResutl.Text = "平手!"
	        QueryScore  '查詢目前戰狀成績
 		    Tie=Tie+1
			SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。
		End If
End Sub

Sub QueryScore() '查詢目前戰狀成績
  Msgbox(lblResutl.Text,"遊戲結果!")
  For i=0 To 8
	  Arraybtn(i).Enabled = False
  Next 
  btnBegin.Enabled =True
  WinFlag=True
 Dim strSQLite As String="Select * FROM 遊戲歷程表 where 對戰方式='電腦對戰'"
 CursorDBPoint = SQLCmd.ExecQuery(strSQLite)       '執行SQL指令
 For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數
    CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)
  	O_Wins=CursorDBPoint.GetString("O贏")   '取得「O贏」的次數
  	X_Wins=CursorDBPoint.GetString("X贏")   '取得「X贏」的次數
  	Tie=CursorDBPoint.GetString("平手")     '取得「平手」的次數
 Next
End Sub	

Sub SaveScoreDB()  '儲存成績副程式
   Dim strSQL As String
   strSQL = "UPDATE 遊戲歷程表 SET O贏='" & O_Wins & "',X贏='" & X_Wins & "',平手='" & Tie & "' where 對戰方式='電腦對戰'"
   SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
   ToastMessageShow("更新最新成績資料(到資料庫中)...", False)
End Sub

Sub btnExit_Click
    Activity.Finish()      '活動完成
    StartActivity(Main)    '回首
End Sub
