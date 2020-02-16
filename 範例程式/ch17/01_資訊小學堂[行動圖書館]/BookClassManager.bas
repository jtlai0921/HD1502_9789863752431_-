Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL                              '宣告SQL物件
    Dim CursorDBPoint As Cursor                    '資料庫記錄指標
	Dim Check_BookClassID_Exist As Boolean=False   '檢查新增「書籍類別」時的代號是否重複

End Sub

Sub Globals
    '宣告輸入「書籍資料表」的相關資料
	Dim edtBookClass_ID As EditText     '書籍類別編號
	Dim edtBookClass_Name As EditText   '書籍類別名稱

	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelectBookClassID As Button    '依照書籍類別編號查詢
	Dim btnSelectBookClassName As Button  '依照書籍類別名稱查詢

	Dim SpinQueryBookClass_ID As Spinner    '查詢書籍類別編號
	Dim SpinQueryBookClass_Name As Spinner  '查詢書籍類別名稱
	Dim btnReturn As Button

	Dim btnBookClassList As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("BookClassManager")
    Activity.Title = "【書籍類別管理系統】"
	SpinQueryBookClass_ID.Visible =False
	SpinQueryBookClass_Name.Visible =False
    edtBookClass_ID.Text = "B0007"
    edtBookClass_Name.Text = "網頁設計"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause(UserClosed As Boolean)

End Sub
Sub btnInsert_Click   '新增書籍分類資料
 Dim BookClass_ID As String =edtBookClass_ID.Text 
 Dim BookClass_Name As String = edtBookClass_Name.Text
 If BookClass_ID="" OR BookClass_Name="" Then 
    Msgbox("您尚未輸入「書籍分類」相關哦！","錯誤訊息回報!!!")
 Else
    Dim strSQL As String
    strSQL = "INSERT INTO 書籍分類表(分類代號,分類名稱)" & _
             "VALUES('" & BookClass_ID & "','" & BookClass_Name & "')"
    Check_BookClassID_Exists(BookClass_ID)
	If Check_BookClassID_Exist=True Then
	    Msgbox("您已經重複新增書籍分類了！","新增錯誤訊息!!!")
    Else
	  SQLCmd.ExecNonQuery(strSQL)
	  Dim dbChange As Int
	  dbChange = SQLCmd.ExecQuerySingleResult("SELECT changes() FROM 書籍分類表")
      ToastMessageShow("新增書籍分類記錄: " & dbChange & " 筆", False)
    End If
	Check_BookClassID_Exist=False
  End If
End Sub
Sub Check_BookClassID_Exists(strBookClass_ID As String)
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 書籍分類表 Where 分類代號='" & strBookClass_ID & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	If CursorDBPoint.RowCount>0 Then
	   Check_BookClassID_Exist=True
	End If
End Sub

Sub btnUpdate_Click    '修改出版社資料
   Dim BookClass_ID As String =edtBookClass_ID.Text 
   Dim strSQL As String
   strSQL = "UPDATE 書籍分類表 SET 分類名稱 ='" & edtBookClass_Name.Text & "' WHERE 分類代號 = '" & BookClass_ID & "'"
   ' Msgbox(strSQL,"查詢結果")
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆書籍分類記錄...", False)
End Sub

Sub btnDelete_Click    '刪除出版社資料
   Dim BookClass_ID As String =edtBookClass_ID.Text 
   Dim strSQL As String
   strSQL = "DELETE FROM 書籍分類表 WHERE 分類代號 = '" & BookClass_ID & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆書籍分類記錄...", False)
End Sub

Sub btnSelectBookClassID_Click
  If SpinQueryBookClass_Name.Visible =True Then
     SpinQueryBookClass_Name.Visible =False
     SpinQueryBookClass_ID.Visible =True
  Else
     SpinQueryBookClass_ID.Visible =True
  End If
  	edtBookClass_ID.Visible =False
	edtBookClass_Name.Visible =True
    SpinQueryBookClass_ID.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 分類代號 FROM 書籍分類表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookClass_ID.Add(CursorDBPoint.GetString("分類代號"))
    Next
	
End Sub

Sub SpinQueryBookClass_ID_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryBookClass_ID As String =SpinQueryBookClass_ID.SelectedItem
	SQL_Query="Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類代號='" & QueryBookClass_ID & "' "
	CallShow_BookClassData(SQL_Query)
	SpinQueryBookClass_ID.Visible =False
	edtBookClass_ID.Visible =True
End Sub

Sub CallShow_BookClassData(strSQL As String)  '顯示「分類名稱」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtBookClass_ID.Text = CursorDBPoint.GetString("分類代號")
    edtBookClass_Name.Text=CursorDBPoint.GetString("分類名稱")
End Sub

Sub btnSelectBookClassName_Click
  If SpinQueryBookClass_ID.Visible =True Then
     SpinQueryBookClass_ID.Visible =False
	 SpinQueryBookClass_Name.Visible =True
  Else
     SpinQueryBookClass_Name.Visible =True
  End If
    edtBookClass_Name.Visible =False
  	edtBookClass_ID.Visible =True	
	SpinQueryBookClass_Name.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 分類名稱 FROM 書籍分類表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookClass_Name.Add(CursorDBPoint.GetString("分類名稱"))
    Next	
End Sub
Sub SpinQueryBookClass_Name_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryBookClass_Name As String =SpinQueryBookClass_Name.SelectedItem
	SQL_Query="Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類名稱='" & QueryBookClass_Name & "' "
	CallShow_BookClassData(SQL_Query)
	SpinQueryBookClass_Name.Visible =False
	edtBookClass_ID.Visible =True
	edtBookClass_Name.Visible =True
End Sub
Sub btnBookClassList_Click
	StartActivity("BookClassTable")
End Sub
Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity("ManagerAPP")
End Sub