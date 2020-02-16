Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
   '請你先勾選「Libs頁籤」中的SQL函數庫(正式版才有此功能)
    Dim SQLCmd As SQL
    Dim CursorDBPoint As Cursor
End Sub

Sub Globals
	Dim edtSQL As EditText
	Dim strSQL As String
	Dim livSQLView As ListView
	Dim btnRunSQL As Button
	Dim strSqlViewTitle As String =""
	Dim strSqlViewContent As String =""
	Dim strLine As String =""
	Dim j As Int 
	Dim btnExitQuery As Button
	Dim lblResult As Label
	Dim lblTitle As Label
	Dim spnSQL As Spinner
	Dim btnWriteSQL As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)   
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyeSchoolDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("Query")
    Activity.Title ="查詢作業"

	lblResult.Text ="===您尚未執行SQL指令==="
	spnSQL.Add("全部學生選課記錄")
	spnSQL.Add("科系代碼表記錄")
	spnSQL.Add("課程資料表記錄")
	spnSQL.Add("學生資料表記錄")
	spnSQL.Add("學號與成績排序")
	spnSQL.Add("每位學生選修科目")
	spnSQL.Add("每位學生選修成績")
	spnSQL.Add("每門科目選修人數")
End Sub
Sub spnSQL_ItemClick (Position As Int, Value As Object)
  Select Position
    Case 0: 
		strSQL="Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號"
    Case 1: 
		strSQL="Select * FROM 科系代碼表"
    Case 2: 
		strSQL="Select * FROM 課程資料表"
    Case 3: 
		strSQL="Select * FROM 學生資料表"
    Case 4: 
		strSQL="Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號 ORDER BY A.學號,成績"
    Case 5: 
		strSQL="SELECT 學號, Count(*) AS 選科目數 FROM 選課資料表 GROUP BY 學號"
    Case 6: 
		strSQL="Select 學號, AVG(成績) As 平均成績 FROM 選課資料表 GROUP BY 學號"
    Case 7: 
		strSQL="SELECT 課號, Count(*) AS 選課學生人數 FROM 選課資料表 GROUP BY 課號 ORDER BY 課號 DESC"		
	End Select
	edtSQL.Text =strSQL
	lblResult.Text ="===您尚未執行SQL指令==="
	livSQLView.Clear '清單清空
End Sub

Sub Activity_Resume
    If WriteSQL.YourSQL.Length > 0 Then
	   edtSQL.Text =""
       edtSQL.Text =WriteSQL.YourSQL
    End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
Sub btnRunSQL_Click
If edtSQL.Text="" Then 
   Msgbox("您尚未選擇查詢之下拉式選項哦！","查詢錯誤")
Else
   QueryDBList(edtSQL.Text)
End If
End Sub
Sub QueryDBList(strSQLite As String)
    livSQLView.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livSQLView.SingleLineLayout.Label.TextSize=16
	livSQLView.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "       " 
		strLine=strLine & "======"              '設定「分隔水平線之每一小段的長度」
	Next	
    livSQLView.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」
    livSQLView.AddSingleLine (strLine)          '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livSQLView.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("符合條件的記錄共有: " & CursorDBPoint.RowCount & " 筆!", True)
	lblResult.Text ="==符合條件的記錄共有( " & CursorDBPoint.RowCount & " 筆)=="
End Sub

Sub btnWriteSQL_Click
	' 開啟WriteSQL活動APP
    StartActivity(WriteSQL)
End Sub

Sub btnExitQuery_Click
	Activity.Finish()
	StartActivity(Main)
End Sub

