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
	Dim Check_BookStoreID_Exist As Boolean=False   '檢查新增「出版社」時的編號是否重複

End Sub

Sub Globals
    '宣告輸入「書籍資料表」的相關資料
	Dim edtBookStore_ID As EditText     '出版社編號
	Dim edtBookStore_Name As EditText   '出版社名稱
	Dim edtBookStore_DNS As EditText    '出版社的官方網站
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelectBookStoreID As Button    '依照出版社編號查詢
	Dim btnSelectBookStoreName As Button  '依照出版社名稱查詢

	Dim SpinQueryBookStore_ID As Spinner    '查詢出版社編號
	Dim SpinQueryBookStore_Name As Spinner  '查詢出版社名稱
	Dim btnReturn As Button

	Dim btnBookStoreList As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("BookStoreManager")
    Activity.Title = "【出版社資料管理系統】"
	SpinQueryBookStore_ID.Visible =False
	SpinQueryBookStore_Name.Visible =False
    edtBookStore_ID.Text = "BS007"
    edtBookStore_Name.Text = "松崗圖書"
    edtBookStore_DNS.Text = "http://myebook.idv.tw"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause(UserClosed As Boolean)

End Sub
Sub btnInsert_Click   '新增出版社資料
 Dim BookStore_ID As String =edtBookStore_ID.Text 
 Dim BookStore_Name As String = edtBookStore_Name.Text
 Dim BookStore_DNS As String= edtBookStore_DNS.Text
 If BookStore_ID="" OR BookStore_Name="" Then 
    Msgbox("您尚未輸入「出版社」相關哦！","錯誤訊息回報!!!")
 Else
    Dim strSQL As String
    strSQL = "INSERT INTO 出版社資料表(出版社編號,出版社名稱,DNS)" & _
             "VALUES('" & BookStore_ID & "','" & BookStore_Name & "','" & BookStore_DNS & "')"
    Check_BookStoreID_Exists(BookStore_ID)
	If Check_BookStoreID_Exist=True Then
	    Msgbox("您已經重複新增這家出版社了！","新增錯誤訊息!!!")
    Else
	  SQLCmd.ExecNonQuery(strSQL)
	  Dim dbChange As Int
	  dbChange = SQLCmd.ExecQuerySingleResult("SELECT changes() FROM 出版社資料表")
      ToastMessageShow("新增出版社記錄: " & dbChange & " 筆", False)
    End If
	Check_BookStoreID_Exist=False
  End If
End Sub
Sub Check_BookStoreID_Exists(strBookStore_ID As String)
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 出版社資料表 Where 出版社編號='" & strBookStore_ID & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
		If i=0 Then
	      Check_BookStoreID_Exist=True
	    End If
    Next
End Sub

Sub btnUpdate_Click    '修改出版社資料
   Dim BookStore_ID As String =edtBookStore_ID.Text 
   Dim strSQL As String
   strSQL = "UPDATE 出版社資料表 SET 出版社名稱 ='" & edtBookStore_Name.Text & "',DNS ='" & edtBookStore_DNS.Text & "' WHERE 出版社編號 = '" & BookStore_ID & "'"
   ' Msgbox(strSQL,"查詢結果")
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆出版社記錄...", False)
End Sub

Sub btnDelete_Click    '刪除出版社資料
   Dim BookStore_ID As String =edtBookStore_ID.Text 
   Dim strSQL As String
   strSQL = "DELETE FROM 出版社資料表 WHERE 出版社編號 = '" & BookStore_ID & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆出版社記錄...", False)
End Sub


Sub btnSelectBookStoreID_Click
  If SpinQueryBookStore_Name.Visible =True Then
     SpinQueryBookStore_Name.Visible =False
     SpinQueryBookStore_ID.Visible =True
  Else
     SpinQueryBookStore_ID.Visible =True
  End If
  	edtBookStore_ID.Visible =False
	edtBookStore_Name.Visible =True
    SpinQueryBookStore_ID.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 出版社編號 FROM 出版社資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookStore_ID.Add(CursorDBPoint.GetString("出版社編號"))
    Next
	
End Sub

Sub SpinQueryBookStore_ID_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryBookStore_ID As String =SpinQueryBookStore_ID.SelectedItem
	SQL_Query="Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社編號='" & QueryBookStore_ID & "' "
	CallShow_BookStoreData(SQL_Query)
	SpinQueryBookStore_ID.Visible =False
	edtBookStore_ID.Visible =True
End Sub

Sub CallShow_BookStoreData(strSQL As String)  '顯示「出版社名稱」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtBookStore_ID.Text = CursorDBPoint.GetString("出版社編號")
    edtBookStore_Name.Text=CursorDBPoint.GetString("出版社名稱")
    edtBookStore_DNS.Text=CursorDBPoint.GetString("DNS")
End Sub

Sub btnSelectBookStoreName_Click
  If SpinQueryBookStore_ID.Visible =True Then
     SpinQueryBookStore_ID.Visible =False
	 SpinQueryBookStore_Name.Visible =True
  Else
     SpinQueryBookStore_Name.Visible =True
  End If
    edtBookStore_Name.Visible =False
  	edtBookStore_ID.Visible =True	
	SpinQueryBookStore_Name.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 出版社名稱 FROM 出版社資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookStore_Name.Add(CursorDBPoint.GetString("出版社名稱"))
    Next	
End Sub
Sub SpinQueryBookStore_Name_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryBookStore_Name As String =SpinQueryBookStore_Name.SelectedItem
	SQL_Query="Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社名稱='" & QueryBookStore_Name & "' "
	CallShow_BookStoreData(SQL_Query)
	SpinQueryBookStore_Name.Visible =False
	edtBookStore_ID.Visible =True
	edtBookStore_Name.Visible =True
End Sub
Sub btnBookStoreList_Click
	StartActivity("BookStoreTable")
End Sub

Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity("ManagerAPP")
End Sub