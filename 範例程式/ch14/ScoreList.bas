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
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyMathScoreDB.sqlite", True) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("ScoreList")
    Activity.Title = UserLogin.UserName & " 您好：心算測驗成績清單如下："  
    QueryDBList("Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='" & UserLogin.UserAccount & "'")
End Sub
Sub QueryDBList(strSQLite As String)
    livScoreList.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)     
	livScoreList.SingleLineLayout.Label.TextSize=14
	livScoreList.SingleLineLayout.ItemHeight = 17dip
	For i = 0 To CursorDBPoint.ColumnCount-1     '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "        " 
		strLine=strLine & "======="               '設定「分隔水平線之每一小段的長度」
	Next	
    livScoreList.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」
    livScoreList.AddSingleLine (strLine)           '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數
        CursorDBPoint.Position = i                 '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1       '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "  "
	  Next	
	  	 livScoreList.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("您目前心算的測驗歷程記錄共有: " & CursorDBPoint.RowCount & " 筆!",False)
End Sub

Sub btnReturn_Click
	Activity.Finish()  '活動完成
	StartActivity(MathTest)	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub livScoreList_ItemClick (Position As Int, Value As Object)
  Dim Msg_Value As String
  Dim strSQL As String
  Msg_Value = Msgbox2("您確定要「刪除」此全部成績記錄嗎?", "確認「刪除」對話方塊", "確認", "", "取消", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
  	strSQL="DELETE FROM 測驗成績表 Where 帳號='" & UserLogin.UserAccount & "' "
     SQLCmd.ExecNonQuery(strSQL)
     livScoreList.RemoveAt(Position)
     ToastMessageShow("刪除「心算成績」記錄...", False)
	 QueryDBList("Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='" & UserLogin.UserAccount & "'")
  End If 
End Sub