Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	Dim edtUserAccount As EditText
	Dim edtPassword As EditText
	Dim btnLogin As Button
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("ManagerLogin")
    Activity.Title="管理者登入介面"
	edtUserAccount.Text="12345"
    edtPassword.Text="00000"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnLogin_Click
	Dim UserAccount As String
    Dim Password As String
    UserAccount = edtUserAccount.text
    Password = edtPassword.Text
    If UserAccount = "12345" AND Password = "00000" Then
       Msgbox("您是合法管理者！","系統回覆")
	   StartActivity(ManagerAPP)	
	Else
	   Msgbox("您輸入的帳號或密碼有誤，請重新輸入！","系統回覆")
    End If
End Sub

Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)	
End Sub