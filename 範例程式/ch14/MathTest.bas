Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL                         '宣告SQL物件
	Dim Check_Score As Boolean
    Dim CursorDBPoint As Cursor               '資料庫記錄指標
End Sub

Sub Globals
	Dim btnRun As Button
	Dim edtResult As EditText
	Dim lblNum1 As Label
	Dim lblNum2 As Label
	Dim lblResult As Label
	Dim Score As Int
	Dim times,i As Int
	Dim lblTestTimes As Label
	Dim Num As Int=6      
	Dim ArrayList(Num) As String
	Dim btnQueryScoreList As Button
	Dim Output As String 
	Dim btnExitApp As Button
	Dim strSQL As String 
	Dim lblOP As Label
	Dim ScoreLevel As Int
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyMathScoreDB.sqlite", True) '將SQL物件初始化資料庫
    End If
    QueryScoreLevel    '呼叫查詢考生目前的級數之副程式
    Activity.LoadLayout("MathTest")
    Activity.Title = "心算測驗(5關遊戲)" 
    times=1 '第1次開始
    Check_Score=False  '尚未查詢成績
    NewQuestion
End Sub

Sub QueryScoreLevel()
 strSQL="Select * FROM 測驗成績表 Where 帳號='" & UserLogin.UserAccount & "'"
 CursorDBPoint = SQLCmd.ExecQuery(strSQL)       '執行SQL指令
 For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數
    CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)
  	ScoreLevel=CursorDBPoint.GetString("級數")  '取得考生目前的級數
 Next
End Sub	

'查詢考生目前的級數及亂數出題之副程式
Sub NewQuestion()
    QueryScoreLevel    '呼叫查詢考生目前的級數之副程式
	lblTestTimes.Text="===您目前正在測驗【第 "& (ScoreLevel+1) & " 關】第 " & times & " 題==="
	Select ScoreLevel+1
	  Case 1   '第一關：個位數相加。例如:3+6
         lblNum1.Text = Rnd(1, 10)
         lblNum2.Text = Rnd(1, 10)
		 lblOP.Text="+"
	  Case 2   '第二關：一個位數及一個十位數相加。例如:3+56
         lblNum1.Text = Rnd(1, 10)
         lblNum2.Text = Rnd(10, 100)
		 lblOP.Text="+"
	  Case 3   '第三關：二個十位數相加(結果在100之內)。例如:13+46
         lblNum1.Text = Rnd(10, 50)
         lblNum2.Text = Rnd(10, 50)
		 lblOP.Text="+"
	  Case 4   '第四關：九九乘法。例如:3*6
         lblNum1.Text = Rnd(1, 10)
         lblNum2.Text = Rnd(1, 10)
		 lblOP.Text="*"
	  Case 5   '第五關：19*19乘法。例如:15*15
         lblNum1.Text = Rnd(10, 20)
         lblNum2.Text = Rnd(10, 20)
		 lblOP.Text="*"
	End Select
   lblResult.Text = "請作答..."
   edtResult.Text = ""
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnRun_Click
 Dim LevelPicture As Bitmap 	
 Dim Result As Int
 If edtResult.Text = "" Then
     Msgbox("您尚未作答...", "錯誤訊息!!!")
 Else
 	Select ScoreLevel+1
	  Case 1   '第一關：個位數相加。例如:3+6
         Result=lblNum1.Text + lblNum2.Text
	  Case 2   '第二關：一個位數及一個十位數相加。例如:3+56
         Result=lblNum1.Text + lblNum2.Text
	  Case 3   '第三關：二個十位數相加。例如:13+56
         Result=lblNum1.Text + lblNum2.Text
	  Case 4   '第四關：九九乘法。例如:3*6
         Result=lblNum1.Text * lblNum2.Text
	  Case 5   '第五關：19*19乘法。例如:13*6
         Result=lblNum1.Text * lblNum2.Text
	End Select
    If edtResult.Text = Result Then  '如果答對
        lblResult.Text = "恭喜您！答對了..."
	 	Score=Score+20 '每題20分
		ArrayList(times) ="O"
    Else   '答錯
        lblResult.Text = "很抱歉！答錯了..."
		ToastMessageShow ("您第：" & times & " 題答錯了...",False)
      	ArrayList(times) ="X"
    End If
	times=times+1
	NewQuestion    '作下一題
End If
If times>5 Then       '判斷測驗的題數
  If Score>=60 Then   '判斷是否及格
    If Score=100 Then	   
	   ScoreLevel=ScoreLevel+1
	   LevelPicture=LoadBitmap(File.DirAssets,"Pass" & ScoreLevel & ".jpg")     
	   Msgbox2("通過第" & ScoreLevel & "關", "公佈結果", "OK", "", "", LevelPicture)
	End If
    Output="您作了5題，總共得" & Score & "分==>及格^-^"
  Else
    Output="您作了5題，總共得" & Score & "分==>不及格"
  End If
  Dim Msg_Value As String
  Msg_Value = Msgbox2(Output, "公佈成績!!!", "繼續練習", "", "查詢成績單", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
	 QueryScore     '呼叫查詢成績副程式
	 times=1        '第1次開始
     Score=0
     NewQuestion    '作下一題
  Else 	
     Check_Score=True
     QueryScore     '呼叫查詢成績副程式
  End If
End If
End Sub

Sub QueryScore()    '查詢成績副程式
  Dim OutScore As String 
  Dim OutputDB As String 
  For i=0 To 4
	 OutScore=OutScore & "第" & (i+1) & "答 " & ArrayList(i+1) & CRLF
  Next
  OutScore=OutScore & Output
  Msgbox(OutScore ,"您的成績單")
  Select Score
	  Case 20
	     OutputDB="答對1/5(不及格)"  
	  Case 40
	     OutputDB="答對2/5(不及格)"  
	  Case 60
	     OutputDB="答對3/5(及格)"  
	  Case 80
	     OutputDB="答對4/5(及格)"  
	  Case 100
	     OutputDB="答對5/5(及格)"  
  End Select		 
  SaveScoreDB(OutputDB,Score)    '儲存測驗歷程成績到「MyMathScoreDB.sqlite」資料庫中。
  If Check_Score=True Then
		Activity.Finish()  '活動完成
	    StartActivity(ScoreList)  '查詢成績單
  End If
End Sub

Sub SaveScoreDB(OutScore As String,ScoreDB As Int)  '儲存成績副程式
  Dim strSQL  As String
  strSQL = "INSERT INTO 測驗成績表(帳號,作答歷程,成績,級數) VALUES('" & UserLogin.UserAccount & "','" & OutScore & "','" & ScoreDB & "' ,'" & ScoreLevel & "')"
  SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
  ToastMessageShow("新增心算成績資料(到資料庫中)...", False)
End Sub

Sub btnQueryScore_Click
  Dim MathScore  As String   '字串變數
  Dim Msg_Value As String
  Msg_Value = Msgbox2(MathScore, "公佈成績!!!", "繼續", "", "結束", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
     times=1 '第1次開始
     Score=0
     NewQuestion    '作下一題
  Else 	
     Activity.Finish() '結束
     ExitApplication   '離開
  End If
End Sub

Sub btnQueryScoreList_Click
	StartActivity(ScoreList)  '查詢成績單
End Sub
Sub btnExitApp_Click
	Activity.Finish()  '活動完成
	StartActivity(UserLogin)	
End Sub