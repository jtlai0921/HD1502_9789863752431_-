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
	Dim edtSQL As EditText
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("WriteSQL")
    Activity.Title="請您撰寫SQL指令"
	
End Sub

Sub btnReturn_Click
	YourSQL=edtSQL.Text 
	Activity.Finish()	
	StartActivity(Query)    ' 返回Query活動 
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



