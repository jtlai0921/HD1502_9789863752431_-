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
	Dim Check_CourID_Exist As Boolean=False   '檢查新增「問卷題號」時的代號是否重複
End Sub

Sub Globals
    '宣告輸入「問卷題庫表」的相關資料
	Dim edtSurveyNo As EditText         '問卷題號
	Dim edtSurveyName As EditText         '問卷題目

	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelect As Button             '查詢功能
	
	Dim SpinQuerySurveyNo As Spinner      '查詢「問卷題號」
	Dim livSurvey As ListView           '顯示「問卷題庫表」清單元件

	Dim btnListData As Button           '清單功能
	Dim btnExitSurvey As Button         '離開功能
	
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
	Dim lblResult As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal,"MyeSurveyDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("Survey")
    Activity.Title ="【行動問卷管理系統】"
	SpinQuerySurveyNo.Visible=False            '查詢「問卷題號」的下拉式元件隱藏
	edtSurveyNo.Text="D0006"
	edtSurveyName.Text="請選擇您未來可能會從事那一個工作?"
	QueryDBList("Select * FROM 問卷題庫表")	  '呼叫顯示目前存在的問卷題庫表之副程式
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnInsert_Click  '新增「問卷資料」
 Dim strSQL As String
 Dim SurveyNo As String =edtSurveyNo.Text               '將「問卷題號」欄位內容指定給變數
 Dim SurveyName As String = edtSurveyName.Text          '將「問卷題目」欄位內容指定給變數
 '判斷使用者是否有完整的填入2個欄位內容
 If SurveyNo="" OR SurveyName="" Then 
    Msgbox("您尚未完整輸入「問卷資料」相關哦！","錯誤訊息回報!!!")
 Else
    strSQL = "INSERT INTO 問卷題庫表(題號,題目)VALUES('" & SurveyNo & "','" & SurveyName & "')"
    Check_CourID_Exists(SurveyNo)        '呼叫檢查「問卷題號」是否有重複新增之副程式
	If Check_CourID_Exist=True Then      '檢查是否有重複新增
	    Msgbox("您已經重複新增「問卷資料」了！","新增錯誤訊息!!!")
    Else                                 '沒有重複新增
	  SQLCmd.ExecNonQuery(strSQL)                               '執行SQL指令，以成功的新增記錄
      ToastMessageShow("您新增成功「問卷資料」記錄!", True)     '顯示新增成功狀態
    End If
	Check_CourID_Exist=False                   '設定沒有重複新增「問卷題號」
  End If
	QueryDBList("Select * FROM 問卷題庫表")	  '呼叫顯示目前存在的問卷題庫表之副程式
End Sub

Sub Check_CourID_Exists(strSurveyNo As String)  '檢查「問卷題號」是否有重複新增之副程式
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 問卷題庫表 Where 題號='" & strSurveyNo & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then  '檢查目前資料庫中的「問卷題庫表」是否已經存在一筆了
	   Check_CourID_Exist=True        '代表重複新增「問卷題號」了!
	End If
End Sub
	
Sub btnUpdate_Click   '修改「問卷資料」
 If edtSurveyNo.Text="" Then 
    Msgbox("您尚未輸入「問卷題號」哦！","錯誤訊息回報!!!")
 Else
   Dim strSQL As String
   Dim SurveyNo As String =edtSurveyNo.Text
   strSQL = "UPDATE 問卷題庫表" & _
            " SET 題目 ='" & edtSurveyName.Text & "'" & _
			" WHERE 題號 = '" & SurveyNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆「問卷資料」記錄...", False)
	QueryDBList("Select * FROM 問卷題庫表")	'呼叫顯示目前存在的問卷題庫表之副程式
 End If
End Sub

Sub btnDelete_Click   '刪除「問卷資料」
 If edtSurveyNo.Text="" Then 
    Msgbox("您尚未輸入「問卷題號」哦！","錯誤訊息回報!!!")
 Else
   Dim strSQL As String
   Dim SurveyNo As String =edtSurveyNo.Text
   strSQL = "DELETE FROM 問卷題庫表 WHERE 題號 = '" & SurveyNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆「問卷資料」記錄...", False)
	QueryDBList("Select * FROM 問卷題庫表")	'呼叫顯示目前存在的問卷題庫表之副程式
  End If
End Sub

Sub btnSelect_Click   '查詢「問卷資料」
    SpinQuerySurveyNo.Visible =True    '查詢「問卷題號」的下拉式元件顯示
	edtSurveyNo.Visible =False         '「問卷題號」欄位元件隱藏
    SpinQuerySurveyNo.Clear            '查詢「問卷題號」的下拉式元件來清空
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 題號 FROM 問卷題庫表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQuerySurveyNo.Add(CursorDBPoint.GetString("題號"))
    Next
End Sub

Sub SpinQuerySurveyNo_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QuerySurveyNo As String =SpinQuerySurveyNo.SelectedItem
	SQL_Query="Select * FROM 問卷題庫表 Where 題號 = '" & QuerySurveyNo & "'"
	CallShow_SurveyData(SQL_Query)      '呼叫顯示「問卷題庫表」之副程式
	SpinQuerySurveyNo.Visible =False    '查詢「問卷題號」的下拉式元件隱藏
	edtSurveyNo.Visible =True           '「問卷題號」欄位元件顯示
End Sub

Sub CallShow_SurveyData(strSQL As String)  '顯示「問卷題庫表」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtSurveyNo.Text = CursorDBPoint.GetString("題號")
    edtSurveyName.Text=CursorDBPoint.GetString("題目")
End Sub

Sub QueryDBList(strSQLite As String)  '顯示目前存在的問卷題庫表之副程式
    livSurvey.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livSurvey.SingleLineLayout.Label.TextSize=16
	livSurvey.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & _
		CursorDBPoint.GetColumnName(i) & "          " 
		strLine=strLine & "======="              '設定「分隔水平線之每一小段的長度」
	Next	
    livSurvey.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」
    livSurvey.AddSingleLine (strLine)           '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livSurvey.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("目前的問卷題庫共有: " & CursorDBPoint.RowCount & " 筆!",False)
	lblResult.Text ="===問卷題庫清單( " & CursorDBPoint.RowCount & " 筆)==="
End Sub

Sub btnListData_Click
	QueryDBList("Select * FROM 問卷題庫表")	'呼叫顯示目前存在的問卷題庫表之副程式
End Sub
Sub btnExitSurvey_Click
	Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub