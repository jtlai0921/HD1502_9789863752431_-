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
	Dim livBookStoreTable As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
       SQLCmd.Initialize(File.DirInternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("BookStoreTable")
    Activity.Title ="出版社清單查詢(全部)"
	SQL_Query="Select 出版社編號,出版社名稱,DNS FROM 出版社資料表"
	QueryBookStoreTable(SQL_Query)
End Sub

Sub QueryBookStoreTable(strSQL As String)
    livBookStoreTable.Clear()
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    livBookStoreTable.SingleLineLayout.Label.TextSize = 16
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        livBookStoreTable.AddTwoLines2((i+1) & "." & CursorDBPoint.GetString("出版社編號") &  "  " & CursorDBPoint.GetString("出版社名稱"),CursorDBPoint.GetString("DNS"),CursorDBPoint.GetString("DNS"))
    Next
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
	Activity.Finish()                 '活動完成
	StartActivity(BookStoreManager)   '回出版社資料管理系統
End Sub
Sub livBookStoreTable_ItemClick (Position As Int, Value As Object)
	Dim i As Intent
    i.Initialize(i.ACTION_VIEW, Value)
	StartActivity(i)
End Sub