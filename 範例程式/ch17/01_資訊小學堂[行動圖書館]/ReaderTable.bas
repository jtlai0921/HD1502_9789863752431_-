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
End Sub

Sub Globals
	Dim btnReturn As Button
	Dim livReaderTable As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
       SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("ReaderTable")
    Activity.Title ="讀者清單查詢(全部)"
	SQL_Query="Select 帳號,密碼,姓名 FROM 讀者資料表"
	QueryBookStoreTable(SQL_Query)
End Sub

Sub QueryBookStoreTable(strSQL As String)
    livReaderTable.Clear()
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    livReaderTable.SingleLineLayout.Label.TextSize = 16
	
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        livReaderTable.AddTwoLines("第" & (i+1) & ".位讀者姓名：" & CursorDBPoint.GetString("姓名"),"    帳號：" & CursorDBPoint.GetString("帳號") & "  密碼：" &  CursorDBPoint.GetString("密碼"))
    Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
	Activity.Finish()                 '活動完成
	StartActivity(ReaderManager)   '回讀者資料管理系統
End Sub
