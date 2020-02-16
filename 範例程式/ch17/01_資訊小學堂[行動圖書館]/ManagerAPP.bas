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
	Dim Button1 As Button
	Dim Button2 As Button
	Dim Button3 As Button
	Dim Button4 As Button
	Dim Button5 As Button
	Dim Button6 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("ManagerAPP")
    Activity.Title = "資訊小學堂【管理者專區】"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
	StartActivity("BookStoreManager")  '「出版社」資料管理
End Sub
Sub Button2_Click
	StartActivity("BookClassManager")  '「書籍分類」資料管理
End Sub
Sub Button3_Click
	StartActivity("BooksManager")      '「書籍」資料管理
End Sub
Sub Button4_Click
    Activity.Finish()  '活動完成
	StartActivity("ManagerLogin")	     '回主畫面
End Sub
Sub Button5_Click                       '「讀者」資料管理
	StartActivity("ReaderManager")  
End Sub
Sub Button6_Click                      '「統計分析」報表
	StartActivity("DataAnalysis")
End Sub