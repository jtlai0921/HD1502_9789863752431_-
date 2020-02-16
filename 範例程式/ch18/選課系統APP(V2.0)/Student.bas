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
	Dim Check_StuID_Exist As Boolean=False    '檢查新增「學號」時是否重複
End Sub

Sub Globals
    '宣告輸入「學生資料表」的相關資料
	Dim edtStudNo As EditText           '學號
	Dim edtStudName As EditText         '姓名
	Dim spnDeptNo As Spinner            '系碼
	
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelect As Button             '查詢功能
	
	Dim SpinQueryStudNo As Spinner      '查詢「學號」
	Dim livStudent As ListView          '顯示「學生資料表」清單元件

	Dim btnListData As Button           '清單功能
	Dim btnExitStudent As Button        '離開功能
	Dim DeptNo As String                '系碼(從spnDeptNo元件取得)
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
	Dim lblResult As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyeSchoolDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("Student")
	Activity.Title ="【學生資料管理系統】"
	SpinQueryStudNo.Visible=False  '查詢「科系代碼」的下拉式元件隱藏
	edtStudNo.Text="S0005"
	edtStudName.Text="五福"
	CallShow_DeptDB("SELECT * FROM 科系代碼表")  '呼叫顯示「科系代碼」之副程式
	QueryDBList("Select * FROM 學生資料表")      '呼叫顯示目前存在的學生資料表之副程式
End Sub
Sub CallShow_DeptDB(strSQL As String)            '顯示「科系代碼」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i               '設定開始游標指定第一筆記錄
		spnDeptNo.Add(CursorDBPoint.GetString("系碼") & "/" & CursorDBPoint.GetString("系名"))
    Next
End Sub
Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnInsert_Click  '新增「學生」資料
 Dim strSQL As String
 Dim StudNo As String =edtStudNo.Text             '將「學號」欄位內容指定給變數
 Dim StudName As String = edtStudName.Text        '將「姓名」欄位內容指定給變數
 DeptNo=spnDeptNo.SelectedItem.SubString2(0,4)
 '判斷使用者是否有完整的填入三個欄位內容
 If StudNo="" OR StudName="" OR DeptNo="" Then 
    Msgbox("您尚未完整輸入「學生資料」哦！","錯誤訊息回報!!!")
 Else
    strSQL = "INSERT INTO 學生資料表(學號,姓名,系碼)" & _
             "VALUES('" & StudNo & "','" & StudName & "','" & DeptNo & "')"
	Msgbox(strSQL,"SQL")
    Check_StuID_Exists(DeptNo)        '呼叫檢查「學號」是否有重複新增之副程式
	If Check_StuID_Exist=True Then    '檢查是否有重複新增
	    Msgbox("您已經重複新增「學號」了！","新增錯誤訊息!!!")
    Else                               '沒有重複新增
	  SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
      ToastMessageShow("您新增成功「學生資料」記錄!", True)   '顯示新增成功狀態
    End If
	Check_StuID_Exist=False                                   '設定沒有重複新增「學號」
  End If
	QueryDBList("Select * FROM 學生資料表")      '呼叫顯示目前存在的學生資料表之副程式  
End Sub

Sub Check_StuID_Exists(strDeptNo As String)       '檢查「學號」是否有重複新增之副程式
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 學生資料表 Where 學號='" & strDeptNo & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「學生資料表」是否已經存在一筆了
	   Check_StuID_Exist=True                    '代表重複新增「學號」了!
	End If
End Sub
	
Sub btnUpdate_Click   '修改「學生」資料
 If edtStudNo.Text="" Then 
    Msgbox("您尚未輸入「學生資料」哦！","錯誤訊息回報!!!")
 Else
   Dim StudNo As String =edtStudNo.Text
   Dim strSQL As String
   strSQL = "UPDATE 學生資料表 SET 姓名 ='" & edtStudName.Text & "'" & _
            ",系碼 ='" & spnDeptNo.SelectedItem.SubString2(0,4) & "'  WHERE 學號 = '" & StudNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆「學生資料」記錄...", False)
	QueryDBList("Select * FROM 學生資料表")      '呼叫顯示目前存在的學生資料表之副程式
 End If   
End Sub

Sub btnDelete_Click   '刪除「學生」資料
 If edtStudNo.Text="" Then 
    Msgbox("您尚未輸入「學生資料」哦！","錯誤訊息回報!!!")
 Else
   Dim StudNo As String =edtStudNo.Text
   Dim strSQL As String
   strSQL = "DELETE FROM 學生資料表 WHERE 學號 = '" & StudNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆「學生資料」記錄...", False)
	QueryDBList("Select * FROM 學生資料表")      '呼叫顯示目前存在的學生資料表之副程式
 End If   
End Sub

Sub btnSelect_Click   '查詢「學生」資料
    SpinQueryStudNo.Visible =True    '查詢「學號」的下拉式元件顯示
	edtStudNo.Visible =False         '「學號」欄位元件隱藏
    SpinQueryStudNo.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 學號 FROM 學生資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryStudNo.Add(CursorDBPoint.GetString("學號"))
    Next
End Sub

Sub SpinQueryStudNo_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryDeptNo As String =SpinQueryStudNo.SelectedItem
	SQL_Query="Select * FROM 學生資料表 As A,科系代碼表 As B Where A.系碼=B.系碼 And 學號 = '" & QueryDeptNo & "'"
	CallShow_StudData(SQL_Query)
	SpinQueryStudNo.Visible =False    '查詢「學號」的下拉式元件隱藏
	edtStudNo.Visible =True           '「學號」欄位元件顯示
End Sub

Sub CallShow_StudData(strSQL As String)  '顯示「學生資料表」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtStudNo.Text = CursorDBPoint.GetString("學號")
    edtStudName.Text=CursorDBPoint.GetString("姓名")
	spnDeptNo.Clear 
	spnDeptNo.Add(CursorDBPoint.GetString("系碼") & "/" & CursorDBPoint.GetString("系名"))
	CallShow_DeptDB("SELECT * FROM 科系代碼表")  '呼叫顯示「科系代碼」之副程式
End Sub

Sub QueryDBList(strSQLite As String)
    livStudent.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livStudent.SingleLineLayout.Label.TextSize=16
	livStudent.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "          " 
		strLine=strLine & "======="              '設定「分隔水平線之每一小段的長度」
	Next	
    livStudent.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」
    livStudent.AddSingleLine (strLine)          '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livStudent.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("目前的學生記錄共有: " & CursorDBPoint.RowCount & " 筆!",False)
	lblResult.Text ="===全校學校學生清單共有( " & CursorDBPoint.RowCount & " 筆)==="
End Sub

Sub btnListData_Click
	QueryDBList("Select * FROM 學生資料表")      '呼叫顯示目前存在的學生資料表之副程式
End Sub
Sub btnExitStudent_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub

