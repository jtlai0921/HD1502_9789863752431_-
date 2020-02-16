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
	Dim Number As Int
    Dim Class_Name(10) As String
	Dim Class_No(10) As Int
	Dim Total As Int 
	Dim DataAnalysis_Title As String 
End Sub

Sub Globals
	Dim btnBookClassAnalysis As Button
	Dim btnBookStoreAnalysis As Button
	Dim btnReaderClassAnalysis As Button
	Dim ListView1 As ListView
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then
       SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If	
	Activity.LoadLayout("DataAnalysis")
    Activity.Title = "統計分析報表"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnBookClassAnalysis_Click
	SQL_Query="Select A.分類代號,分類名稱,Count(*) As 各類別數量 FROM 書籍資料表 As A,書籍分類表 As B Where A.分類代號=B.分類代號 Group By  A.分類代號,分類名稱"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	ListView1.Clear 
    For Number = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = Number
        ListView1.AddSingleLine2((Number+1) & "." & CursorDBPoint.GetString("分類名稱") &  "  數量：" & CursorDBPoint.GetString("各類別數量") ,CursorDBPoint.GetString("各類別數量"))
        Class_Name(Number)=CursorDBPoint.GetString("分類名稱")
	    Class_No(Number)=CursorDBPoint.GetString("各類別數量")
	Next
	Total=CursorDBPoint.RowCount  '記錄筆數
	DataAnalysis_Title="各「類別」書籍統計"
	StartActivity(PieChart)  ' 啟動PieChart活動
	
End Sub
Sub btnReaderClassAnalysis_Click
    SQL_Query="Select A.分類代號,分類名稱,Count(*) As 各類別讀者借閱數量 FROM 書籍資料表 As A,書籍分類表 As B,借閱記錄表 As C Where A.分類代號=B.分類代號 AND A.書號=C.書號 Group By  A.分類代號,分類名稱"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	ListView1.Clear 
    For Number = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = Number
        ListView1.AddSingleLine2((Number+1) & "." & CursorDBPoint.GetString("分類名稱") &  "  數量：" & CursorDBPoint.GetString("各類別讀者借閱數量") ,CursorDBPoint.GetString("各類別讀者借閱數量"))
        Class_Name(Number)=CursorDBPoint.GetString("分類名稱")
	    Class_No(Number)=CursorDBPoint.GetString("各類別讀者借閱數量")
	Next
	Total=CursorDBPoint.RowCount  '記錄筆數
	DataAnalysis_Title="讀者閱讀「類別」書籍統計"
	StartActivity(PieChart)  ' 啟動PieChart活動	
End Sub
Sub btnBookStoreAnalysis_Click
    SQL_Query="Select A.出版社編號,B.出版社名稱,Count(*) As 各出版社數量 FROM 書籍資料表 As A,出版社資料表 As B Where A.出版社編號=B.出版社編號 Group By  A.出版社編號,B.出版社名稱"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	ListView1.Clear 
    For Number = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = Number
        ListView1.AddSingleLine2((Number+1) & "." & CursorDBPoint.GetString("出版社名稱") &  "  數量：" & CursorDBPoint.GetString("各出版社數量") ,CursorDBPoint.GetString("各出版社數量"))
        Class_Name(Number)=CursorDBPoint.GetString("出版社名稱")
	    Class_No(Number)=CursorDBPoint.GetString("各出版社數量")
	Next
	Total=CursorDBPoint.RowCount  '記錄筆數
	DataAnalysis_Title="各「出版社」書籍統計"
	StartActivity(PieChart)  ' 啟動PieChart活動
End Sub

Sub btnReturn_Click
	Activity.Finish()               '活動完成
	StartActivity(ManagerAPP)       '回上一頁
End Sub