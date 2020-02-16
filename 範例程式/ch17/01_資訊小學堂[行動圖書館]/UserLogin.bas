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
    Dim Username As String
	Dim UserAccount As String 
End Sub

Sub Globals
	Dim edtUserAccount As EditText
	Dim edtPassword As EditText
	Dim btnLogin As Button
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("UserLogin")
    Activity.Title="讀者登入介面"
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
	edtUserAccount.Text="11111"
    edtPassword.Text="11111"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnLogin_Click
Dim Welcome As String 
If edtUserAccount.Text="" OR edtPassword.Text="" Then
   Msgbox("您尚未輸入的帳號或密碼，請輸入！","系統回覆")
Else
	UserAccount = edtUserAccount.Text
    Dim Password As String = edtPassword.Text
	SQL_Query="Select * FROM 讀者資料表 Where 帳號='" & UserAccount & "' And 密碼='" & Password & "'"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
    Next	
	   If CursorDBPoint.Position=0 Then
		  UserAccount=CursorDBPoint.GetString("帳號")
		  Username=CursorDBPoint.GetString("姓名")
		  Welcome=Username & "讀者您好：" & CRLF & "歡迎使用【資訊小學堂】!"
          Msgbox(Welcome,"系統回覆")
		  Activity.Finish()  '活動完成
	      StartActivity(UserAPP)	
	   Else
	      Msgbox("您輸入的帳號或密碼有誤，請重新輸入！","系統回覆")
	   End If
End If
End Sub
Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)	
End Sub