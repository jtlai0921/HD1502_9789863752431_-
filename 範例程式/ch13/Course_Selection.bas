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
	Dim btnDelCourse As Button
	Dim btnExitCourse_Selection As Button
	Dim spnAddCourse As Spinner
	Dim livViewAddCourse As ListView
	Dim btnClearCourse As Button
	Dim Check_StuNo_CourseNo_Exist As Boolean=False  '檢查是否有「重複選課」(預設為否)
	Dim strViewAddCourse As String
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
	Dim spnStudent As Spinner
	Dim lblResult As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyeSchoolDB.sqlite", True)
    End If
	Activity.LoadLayout("Course_Selection")
    Activity.Title ="選課作業"
	CallShow_StudentDB("SELECT * FROM 學生資料表")  '呼叫顯示「學生資料」之副程式
	CallShow_CourseDB("SELECT * FROM 課程資料表")   '呼叫顯示「課程資料」之副程式
    lblResult.Text ="===您尚未選擇欲加選學生==="
End Sub

Sub CallShow_StudentDB(strSQL As String)  '顯示「學生資料」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄
		spnStudent.Add(CursorDBPoint.GetString("學號") & "/" & CursorDBPoint.GetString("姓名"))
    Next
End Sub

Sub spnStudent_ItemClick (Position As Int, Value As Object)
	strViewAddCourse="Select A.學號,姓名,B.課號,課名,學分數 FROM 學生資料表 As A,課程資料表 As B,選課資料表 As C" & _
	       " Where A.學號=C.學號 And B.課號=C.課號 And C.學號='" & spnStudent.SelectedItem.SubString2(0,5) & "'"
	QueryDBList(strViewAddCourse)
End Sub

Sub CallShow_CourseDB(strSQL As String)  '顯示「課程資料」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄
		spnAddCourse.Add(CursorDBPoint.GetString("課號") & "/" & CursorDBPoint.GetString("課名")  & "(" & CursorDBPoint.GetString("學分數") & ")" )
    Next
End Sub
Sub spnAddCourse_ItemClick (Position As Int, Value As Object)
    Dim strSQL As String            '宣告SQL指令的字串變數
    Dim StudNo As String            '學號
	Dim CourNo As String            '課號
	Dim intRndScore As Int          '成績
	StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號
	CourNo=spnAddCourse.SelectedItem.SubString2(0,4)  '課號
	intRndScore=Rnd(60,100)                              '成績
	strSQL = "INSERT INTO 選課資料表(學號,課號,成績)" & _
             "VALUES('" & StudNo & "','" & CourNo & "','" & intRndScore & "')"
	'Msgbox(strSQL,"SQL")
    Check_StuID_Exists(StudNo,CourNo)       '呼叫檢查「重複選課」之副程式
	If Check_StuNo_CourseNo_Exist=True Then    '檢查是否有重複新增
	    Msgbox("您已經「重複選課」了！","加選錯誤訊息!!!")
    Else                               '沒有重複新增
	  SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
      ToastMessageShow("您加選成功!", True)   '顯示新增成功狀態
    End If
	Check_StuNo_CourseNo_Exist=False                                   '設定沒有重複新增「學號」
	QueryDBList(strViewAddCourse)  '顯示您已加選的課程清單
End Sub

Sub Check_StuID_Exists(strStudNo As String,strCourNo As String)    '檢查「重複選課」之副程式
	Dim SQL_Query As String    '宣告SQL指令的字串變數
  	SQL_Query="Select * FROM 選課資料表 Where 學號='" & strStudNo & "' And 課號='" & strCourNo & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「學生資料表」是否已經存在一筆了
	   Check_StuNo_CourseNo_Exist=True           '代表重複新增「學號」了!
	End If
End Sub

Sub btnDelCourse_Click    '退選
	Msgbox("請點選清單中的課程!!!","退選指引")
End Sub

Sub btnClearCourse_Click  '全退
  Dim Msg_Value As String
  Dim strSQL As String
  Msg_Value = Msgbox2("您確定要「全退」全部課程資料嗎?", "確認全退對話方塊", "確認", "", "取消", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
  	strSQL="DELETE FROM 選課資料表 Where 學號='" & spnStudent.SelectedItem.SubString2(0,5) & "'"
     SQLCmd.ExecNonQuery(strSQL)
	 livViewAddCourse.Clear 
     ToastMessageShow("您已退掉全部課程記錄...", False)
	 QueryDBList(strViewAddCourse)       '顯示您已加選的課程清單
  End If 
End Sub

Sub livViewAddCourse_ItemClick (Position As Int, Value As Object)
  Dim Msg_Value As String
  Dim strSQL As String
  Msg_Value = Msgbox2("您確定要「退選」此課程資料嗎?", "確認退選對話方塊", "確認", "", "取消", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
  	strSQL="DELETE FROM 選課資料表 Where 學號='" & spnStudent.SelectedItem.SubString2(0,5) & "' And 課號='" & livViewAddCourse.GetItem(Position) & "' "
     	Msgbox(strSQL,"strSQL")
	 SQLCmd.ExecNonQuery(strSQL)
     livViewAddCourse.RemoveAt(Position)
     ToastMessageShow("刪除一筆「學生資料」記錄...", False)
	 QueryDBList(strViewAddCourse)      '顯示您已加選的課程清單
  End If 
End Sub

Sub QueryDBList(strSQLite As String)
    livViewAddCourse.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livViewAddCourse.SingleLineLayout.Label.TextSize=16
	livViewAddCourse.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "         " 
		strLine=strLine & "======="              '設定「分隔水平線之每一小段的長度」
	Next	
    livViewAddCourse.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」
    livViewAddCourse.AddSingleLine (strLine)          '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livViewAddCourse.AddSingleLine2((i+1) & ". " & strSqlViewContent,CursorDBPoint.GetString(CursorDBPoint.GetColumnName(2)))
		 strSqlViewContent =""
	Next	
    ToastMessageShow("目前您的加選科目，共有: " & CursorDBPoint.RowCount & " 筆!",False)
	lblResult.Text ="===您已加選課程清單( " & CursorDBPoint.RowCount & " 筆)==="
End Sub

Sub btnExitCourse_Selection_Click
	Activity.Finish()
	StartActivity(Main)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
