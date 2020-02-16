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
	Dim livBookClassTable As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
       SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("BookClassTable")
    Activity.Title ="書籍分類清單查詢(全部)"
	SQL_Query="Select 分類代號,分類名稱 FROM 書籍分類表"
	QueryBookClassTable(SQL_Query)
End Sub

Sub QueryBookClassTable(strSQL As String)
    livBookClassTable.Clear()
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    livBookClassTable.SingleLineLayout.Label.TextSize = 16
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        livBookClassTable.AddSingleLine((i+1) & "." & CursorDBPoint.GetString("分類代號") &  "  " & CursorDBPoint.GetString("分類名稱"))
    Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
	Activity.Finish()                 '活動完成
	StartActivity(BookClassManager)   '回出版社資料管理系統
End Sub