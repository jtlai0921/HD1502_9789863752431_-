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
	Dim Check_DeptID_Exist As Boolean=False   '檢查新增「科系代碼」時的代號是否重複
End Sub

Sub Globals
    '宣告輸入「科系代碼表」的相關資料
	Dim edtDeptNo As EditText           '科系代碼
	Dim edtDeptName As EditText         '科系名稱
	Dim edtDeptManager As EditText      '系主任
	
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelect As Button             '查詢功能
	
	Dim SpinQueryDeptNo As Spinner      '查詢「科系代碼」
	Dim livDepartment As ListView       '顯示「科系代碼表」清單元件

	Dim btnListData As Button           '清單功能
	Dim btnExitDepartment As Button     '離開功能
	
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
	Activity.LoadLayout("Department")
    Activity.Title ="【科系代碼管理系統】"
	SpinQueryDeptNo.Visible=False  '查詢「科系代碼」的下拉式元件隱藏
	edtDeptNo.Text="D003"
	edtDeptName.Text="數位系"
	edtDeptManager.Text="劉主任"
    QueryDBList("Select * FROM 科系代碼表")  '呼叫顯示目前存在的科系代碼表之副程式
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnInsert_Click  '新增「科系代碼」資料
 Dim strSQL As String
 Dim DeptNo As String =edtDeptNo.Text               '將「科系代碼」欄位內容指定給變數
 Dim DeptName As String = edtDeptName.Text          '將「科系名稱」欄位內容指定給變數
 Dim DeptManager As String = edtDeptManager.Text    '將「系主任」欄位內容指定給變數
 '判斷使用者是否有完整的填入三個欄位內容
 If DeptNo="" OR DeptName="" OR DeptManager="" Then 
    Msgbox("您尚未完整輸入「科系代碼」相關哦！","錯誤訊息回報!!!")
 Else
    strSQL = "INSERT INTO 科系代碼表(系碼,系名,系主任)" & _
             "VALUES('" & DeptNo & "','" & DeptName & "','" & DeptManager & "')"
    Check_DeptID_Exists(DeptNo)        '呼叫檢查「科系代碼」是否有重複新增之副程式
	If Check_DeptID_Exist=True Then    '檢查是否有重複新增
	    Msgbox("您已經重複新增「科系代碼」了！","新增錯誤訊息!!!")
    Else                               '沒有重複新增
	  SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
      ToastMessageShow("您新增成功「科系代碼」記錄!", True)   '顯示新增成功狀態
    End If
	Check_DeptID_Exist=False           '設定沒有重複新增「科系代碼」
  End If
   QueryDBList("Select * FROM 科系代碼表")  '呼叫顯示目前存在的科系代碼表之副程式
End Sub

Sub Check_DeptID_Exists(strDeptNo As String)  '檢查「科系代碼」是否有重複新增之副程式
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 科系代碼表 Where 系碼='" & strDeptNo & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then  '檢查目前資料庫中的「科系代碼表」是否已經存在一筆了
	   Check_DeptID_Exist=True        '代表重複新增「科系代碼」了!
	End If
End Sub
	
Sub btnUpdate_Click   '修改「科系代碼」資料
 If edtDeptNo.Text="" Then 
    Msgbox("您尚未輸入「科系代碼」哦！","錯誤訊息回報!!!")
 Else
   Dim DeptNo As String =edtDeptNo.Text
   Dim strSQL As String
   strSQL = "UPDATE 科系代碼表 SET 系名 ='" & edtDeptName.Text & "'" & _
            ",系主任 ='" & edtDeptManager.Text & "'  WHERE 系碼 = '" & DeptNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆「科系代碼」記錄...", False)
   QueryDBList("Select * FROM 科系代碼表")  '呼叫顯示目前存在的科系代碼表之副程式
 End If   
End Sub

Sub btnDelete_Click   '刪除「科系代碼」資料
 If edtDeptNo.Text="" Then 
    Msgbox("您尚未輸入「科系代碼」哦！","錯誤訊息回報!!!")
 Else
   Dim DeptNo As String =edtDeptNo.Text
   Dim strSQL As String
   strSQL = "DELETE FROM 科系代碼表 WHERE 系碼 = '" & DeptNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆「科系代碼」記錄...", False)
   QueryDBList("Select * FROM 科系代碼表")  '呼叫顯示目前存在的科系代碼表之副程式
 End If   
End Sub

Sub btnSelect_Click   '查詢「科系代碼」資料
    SpinQueryDeptNo.Visible =True    '查詢「科系代碼」的下拉式元件顯示
	edtDeptNo.Visible =False         '「科系代碼」欄位元件隱藏

    SpinQueryDeptNo.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 系碼 FROM 科系代碼表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryDeptNo.Add(CursorDBPoint.GetString("系碼"))
    Next
End Sub

Sub SpinQueryDeptNo_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryDeptNo As String =SpinQueryDeptNo.SelectedItem
	SQL_Query="Select * FROM 科系代碼表 Where 系碼 = '" & QueryDeptNo & "'"
	CallShow_DeptData(SQL_Query)
	SpinQueryDeptNo.Visible =False    '查詢「科系代碼」的下拉式元件隱藏
	edtDeptNo.Visible =True           '「科系代碼」欄位元件顯示
End Sub

Sub CallShow_DeptData(strSQL As String)  '顯示「科系代碼表」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtDeptNo.Text = CursorDBPoint.GetString("系碼")
    edtDeptName.Text=CursorDBPoint.GetString("系名")
    edtDeptManager.Text=CursorDBPoint.GetString("系主任")
End Sub

Sub QueryDBList(strSQLite As String) '顯示目前存在的科系代碼表之副程式
    livDepartment.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livDepartment.SingleLineLayout.Label.TextSize=16
	livDepartment.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "          " 
		strLine=strLine & "======="              '設定「分隔水平線之每一小段的長度」
	Next	
    livDepartment.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」
    livDepartment.AddSingleLine (strLine)          '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livDepartment.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("目前的科系記錄共有: " & CursorDBPoint.RowCount & " 筆!",False)
	lblResult.Text ="===本校的科系清單共有( " & CursorDBPoint.RowCount & " 筆)==="
End Sub

Sub btnListData_Click
	QueryDBList("Select * FROM 科系代碼表")  '呼叫顯示目前存在的科系代碼表之副程式
End Sub
Sub btnExitDepartment_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub
