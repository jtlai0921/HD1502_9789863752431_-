Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
   
End Sub

Sub Globals
	Dim webvBookStores As WebView
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("BookStores")
    Activity.Title ="歡迎到「" & Main.BookStore & "」網路書店"
End Sub

Sub Activity_Resume
    Dim DNS As String=Main.DNS   '領域名稱
	webvBookStores.LoadUrl(DNS)  'WebView物件利用LoadUrl方法來連接網頁
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
    Activity.Finish()    '活動完成
	StartActivity(Main)  ' 返回Main主活動 
End Sub