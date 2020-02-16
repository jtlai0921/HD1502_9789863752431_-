Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL                         '宣告SQL物件
    Dim CursorDBPoint As Cursor               '資料庫記錄指標
End Sub

Sub Globals
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
	Dim livScoreList As ListView
	Dim btnReturn As Button
	Dim livHistoryList As ListView
	Dim btnImportScoreToHistory As Button
	Dim NowPlay(2,4) As Int
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyGameScore.sqlite", True) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("ScoreList")
    Activity.Title = "查詢戰況"  
    QueryDBList1("Select * FROM 遊戲歷程表 Where 對戰方式 In('兩人對戰','電腦對戰')")
    QueryDBList2("Select * FROM 遊戲歷程表 Where 對戰方式 In('兩人對戰_H','電腦對戰_H')")
End Sub

Sub btnImportScoreToHistory_Click  '匯入目前成績為歷史成績之副程式
   Dim strSQL1 As String
   strSQL1 = "UPDATE 遊戲歷程表 SET O贏=O贏+" & NowPlay(0,1)& ",X贏=X贏+" & NowPlay(0,2)& ",平手=平手+" & NowPlay(0,3)& " Where 對戰方式='兩人對戰_H'"
   SQLCmd.ExecNonQuery(strSQL1)                             '執行SQL指令，以成功的新增記錄
   Dim strSQL2 As String
   strSQL2 = "UPDATE 遊戲歷程表 SET O贏=O贏+" & NowPlay(1,1)& ",X贏=X贏+" & NowPlay(1,2)& ",平手=平手+" & NowPlay(1,3)& " Where 對戰方式='電腦對戰_H'"
   SQLCmd.ExecNonQuery(strSQL2)                             '執行SQL指令，以成功的新增記錄
   QueryDBList2("Select * FROM 遊戲歷程表 Where 對戰方式 In('兩人對戰_H','電腦對戰_H')")
   ClearNowPlay  '呼叫清空最新戰況的成績之副程式
End Sub
Sub ClearNowPlay() '清空最新戰況的成績之副程式
   Dim strSQL1 As String
   strSQL1 = "UPDATE 遊戲歷程表 SET O贏=0,X贏=0,平手=0 Where 對戰方式='兩人對戰'"
   SQLCmd.ExecNonQuery(strSQL1)                             '執行SQL指令，以成功的新增記錄
   Dim strSQL2 As String
   strSQL2 = "UPDATE 遊戲歷程表 SET O贏=0,X贏=0,平手=0 Where 對戰方式='電腦對戰'"
   SQLCmd.ExecNonQuery(strSQL2)                             '執行SQL指令，以成功的新增記錄
   QueryDBList1("Select * FROM 遊戲歷程表 Where 對戰方式 In('兩人對戰','電腦對戰')")
End Sub

Sub QueryDBList1(strSQLite As String)
    livScoreList.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)     
	livScoreList.SingleLineLayout.Label.TextSize=14
	livScoreList.SingleLineLayout.ItemHeight = 25dip
	For i = 0 To CursorDBPoint.ColumnCount-1     '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "       " 
		strLine=strLine & "======="               '設定「分隔水平線之每一小段的長度」
	Next	
    livScoreList.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」
    livScoreList.AddSingleLine (strLine)           '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數
        CursorDBPoint.Position = i                 '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1       '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "         "
		 If j>=1 Then
		    NowPlay(i,j)=CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j))
		 End If
	  Next	
	  	 livScoreList.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
End Sub

Sub QueryDBList2(strSQLite As String)
    livHistoryList.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)     
	livHistoryList.SingleLineLayout.Label.TextSize=14
	livHistoryList.SingleLineLayout.ItemHeight = 25dip
	For i = 0 To CursorDBPoint.ColumnCount-1         '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "        " 
		strLine=strLine & "======="                  '設定「分隔水平線之每一小段的長度」
	Next	
    livHistoryList.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」
    livHistoryList.AddSingleLine (strLine)           '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1          '控制「記錄」的筆數
        CursorDBPoint.Position = i                   '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1         '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "        "
	  Next	
	  	 livHistoryList.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
End Sub
Sub btnReturn_Click
	Activity.Finish()  '活動完成
	StartActivity(Main)	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


