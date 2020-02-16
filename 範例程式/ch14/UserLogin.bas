Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL
	Dim SQL_Query As String
    Dim CursorDBPoint As Cursor
    Dim UserAccount As String
	Dim UserName As String 
	Dim ScoreLevel As Int=0
End Sub

Sub Globals
	Dim edtUserAccount As EditText
	Dim btnLogin As Button
	Dim btnReturn As Button
	Dim strSQL As String 
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("UserLogin")
    Activity.Title="考生登入介面"
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyMathScoreDB.sqlite", True)
    End If
	edtUserAccount.Text="11111"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnLogin_Click
Dim Welcome As String 
If edtUserAccount.Text="" Then
   Msgbox("您尚未輸入的帳號，請輸入！","系統回覆")
Else
	UserAccount = edtUserAccount.Text
	SQL_Query="Select * FROM 學生資料表 Where 帳號='" & UserAccount & "'"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
    Next	
    If CursorDBPoint.Position=0 Then
	  UserName=CursorDBPoint.GetString("姓名")
	  QueryScoreLevel '呼叫查詢考生目前的級數之副程式
	  Welcome=UserName & "考生您好：" & CRLF & "歡迎使用【心算測驗系統】!"
      Msgbox(Welcome,"系統回覆")
	  Activity.Finish()  '活動完成
	  StartActivity(MathTest)	
	Else
	  Msgbox("您非考生，無法登入本系統","系統回覆")
	End If
End If
End Sub

Sub QueryScoreLevel()
 Dim LevelPicture As Bitmap 	
 strSQL="Select * FROM 測驗成績表 Where 帳號='" & UserAccount & "'"
 CursorDBPoint = SQLCmd.ExecQuery(strSQL)       '執行SQL指令
 For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數
    CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)
  	ScoreLevel=CursorDBPoint.GetString("級數")  '取得考生目前的級數
 Next
 If ScoreLevel>0 Then
   LevelPicture=LoadBitmap(File.DirAssets,"Pass" & ScoreLevel & ".jpg")     
   Msgbox2(UserName & "您目前已通過第" & ScoreLevel & "關", "公佈結果", "OK", "", "", LevelPicture)
End If
End Sub	
Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)	
End Sub