Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim YourSQL As String 
End Sub

Sub Globals
   '請你先勾選「Libs頁籤」中的SQL函數庫(正式版才有此功能)
	Dim edtSQL As EditText
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("WriteSQL")    '使用「WriteSQL(版面配置檔)」來輸出
    Activity.Title="請您撰寫SQL指令"   '本頁的標題名稱
End Sub

Sub btnReturn_Click
	YourSQL=edtSQL.Text     '將撰寫的SQL指令傳回「Query活動」
	Activity.Finish()	    '結束離開
	StartActivity(Query)    '返回Query活動 
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



