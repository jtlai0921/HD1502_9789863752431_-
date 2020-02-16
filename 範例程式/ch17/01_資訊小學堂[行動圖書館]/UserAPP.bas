Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
     Dim ChangeQuery As Int=0  '記錄查詢種類
End Sub

Sub Globals
	Dim Button1 As Button
	Dim Button2 As Button
	Dim Button3 As Button
	Dim Button4 As Button
	Dim Button5 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("UserAPP")
    Activity.Title = UserLogin.Username & "您好：歡迎來到【讀者專區】"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    ChangeQuery=1
	StartActivity("BookTable")  '書籍清單查詢(全部)
End Sub
Sub Button2_Click
    ChangeQuery=2
	StartActivity("KeywordQuery") '書籍關鍵字查詢
End Sub
Sub Button3_Click
    ChangeQuery=3
	StartActivity("BookClassQuery") '書籍分類查詢
End Sub
Sub Button4_Click
    Activity.Finish()  '活動完成
	StartActivity("UserLogin")      	'回上一層
End Sub

Sub Button5_Click
    ChangeQuery=4
	StartActivity("MyloveBooks")	    '連到我的最愛
End Sub