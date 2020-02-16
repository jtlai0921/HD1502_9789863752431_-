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
	Dim BookID_4 As String     '書籍編號
End Sub

Sub Globals
	Dim btnReturn As Button
	Dim livBookTable As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("MyloveBooks")
    Activity.Title ="我的最愛書籍"

End Sub

Sub Activity_Resume
	Dim Account As String =UserLogin.UserAccount
    SQL_Query="Select 出版社名稱,B.書號,書名 FROM 出版社資料表 As A ,書籍資料表 As B,借閱記錄表 As C Where A.出版社編號=B.出版社編號 AND B.書號=C.書號 And 帳號='" & Account  & "'"
	QueryBookTable(SQL_Query)
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
Sub QueryBookTable(strSQL As String)
    livBookTable.Clear()
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    livBookTable.SingleLineLayout.Label.TextSize = 16
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        livBookTable.AddTwoLines2((i+1) & "." & CursorDBPoint.GetString("出版社名稱") &  "  " & CursorDBPoint.GetString("書號"),CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("書號"))
    Next
End Sub

Sub livBookTable_ItemClick (Position As Int, Value As Object)
    BookID_4 = Value    '書籍編號
	StartActivity("Myebook")
End Sub

Sub btnReturn_Click
    Activity.Finish()         '活動完成
	StartActivity(UserAPP)  ' 返回UserAPP主活動 
End Sub