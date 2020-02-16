Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL
    Dim CursorDBPoint As Cursor
	Dim Check_BookID_Exist As Boolean=False   '檢查新增書籍時的書號是否重複

End Sub

Sub Globals
    '宣告輸入「書籍資料表」的相關資料
	Dim edtBookID As EditText     '書號
	Dim edtBookName As EditText   '書名 
	Dim edtAuthor As EditText     '作者 
	Dim edtPubDate As EditText    '出版日期
    Dim edtPrice As EditText      '定價
	Dim SpinBookStore As Spinner  '出版社名稱
	Dim SpinBookClass As Spinner  '書籍分類名稱
	Dim edtDNS As EditText        '書籍的官方網站
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button       '新增功能
	Dim btnUpdate As Button       '修改功能
	Dim btnDelete As Button       '刪除功能
	Dim btnSelect As Button       '查詢功能
	Dim now As Long
    now=DateTime.now 
    
	Dim BookStoreID As String '出版社編號
	Dim BookClassID As String '書籍分類代號
	Dim btnReturn As Button
	Dim SpinQueryBookID As Spinner  '查詢書籍代號
	Dim btnSelectBookName As Button
	Dim SpinQueryBookName As Spinner
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("BooksManager")
    Activity.Title = "【書籍資料管理系統】"
	SpinQueryBookID.Visible =False
	SpinQueryBookName.Visible =False
    edtBookID.Text = "Book001"
    edtBookName.Text = "開發Android APP使用VB輕鬆學"
	edtAuthor.Text = "李春雄"
    edtPubDate.Text = DateTime.Date(now)
	edtPrice.Text = "620"
	CallShow_BookStoreDB("SELECT 出版社編號,出版社名稱 FROM 出版社資料表")  '呼叫顯示「出版社名稱」之副程式
	CallShow_BookClass("SELECT 分類代號,分類名稱 FROM 書籍分類表")          '呼叫顯示「書籍分類」之副程式
    edtDNS.Text = "http://myebook.idv.tw"
End Sub

Sub CallShow_BookStoreDB(strSQL As String)  '顯示「出版社名稱」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄
		SpinBookStore.Add(CursorDBPoint.GetString("出版社編號") & "/" & CursorDBPoint.GetString("出版社名稱"))
    Next
End Sub

Sub SpinBookStore_ItemClick (Position As Int, Value As Object)
   BookStoreID=SpinBookStore.SelectedItem.SubString2(0,5)

End Sub

Sub CallShow_BookClass(strSQL As String)    '顯示「書籍分類」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i           '設定開始游標指定第一筆記錄      
		SpinBookClass.Add(CursorDBPoint.GetString("分類代號") & "/" & CursorDBPoint.GetString("分類名稱"))
    Next
End Sub

Sub SpinBookClass_ItemClick (Position As Int, Value As Object)
	BookClassID=SpinBookClass.SelectedItem.SubString2(0,5)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause(UserClosed As Boolean)

End Sub

Sub btnInsert_Click   '新增書籍資料
 Dim BookID As String =edtBookID.Text 
 Dim BookName As String = edtBookName.Text 
 Dim Author As String= edtAuthor.Text 
 Dim PubDate As String= edtPubDate.Text
 Dim Price As String= edtPrice.Text 
 Dim DNS As String= edtDNS.Text
 If BookStoreID="" OR BookClassID="" Then 
    Msgbox("您尚未選擇「出版社」或「書籍類別」哦！","錯誤訊息回報!!!")
 Else
    Dim strSQL As String
    strSQL = "INSERT INTO 書籍資料表(書號,書名,作者,出版日期,定價,出版社編號,分類代號,官方網站)" & _
             "VALUES('" & BookID & "','" & BookName & "','" & Author & "','" & PubDate & "','" & Price & "','" & BookStoreID & "','" & BookClassID & "','" & DNS & "')"
    Check_BookID_Exists(BookID)
	If Check_BookID_Exist=True Then
	    Msgbox("您已經重複新增這本書了！","新增錯誤訊息!!!")
    Else
	  SQLCmd.ExecNonQuery(strSQL)
	  Dim dbChange As Int
	  dbChange = SQLCmd.ExecQuerySingleResult("SELECT changes() FROM 書籍資料表")
      ToastMessageShow("新增書籍記錄: " & dbChange & " 筆", False)
    End If
	Check_BookID_Exist=False
  End If
End Sub
Sub Check_BookID_Exists(strBookID As String)
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 書籍資料表 Where 書號='" & strBookID & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
		If i=0 Then
	      Check_BookID_Exist=True
	    End If
    Next
End Sub

Sub btnUpdate_Click    '修改書籍資料
	Dim BookID As String =edtBookID.Text 
    Dim strSQL As String
 If BookStoreID="" OR BookClassID="" Then 
    Msgbox("您尚未選擇「出版社」或「書籍類別」哦！","錯誤訊息回報!!!")
 Else	
    strSQL = "UPDATE 書籍資料表 SET 書名 ='" & edtBookName.Text & "',作者 ='" & edtAuthor.Text & "',出版日期 ='" & edtPubDate.Text & "',定價 ='" & edtPrice.Text & "',出版社編號 ='" & BookStoreID & "',分類代號 ='" & BookClassID & "',官方網站 ='" & edtDNS.Text & "' WHERE 書號 = '" & BookID & "'"
   ' Msgbox(strSQL,"查詢結果")
	SQLCmd.ExecNonQuery(strSQL)
    ToastMessageShow("更新一筆書籍記錄...", False)
  End If
End Sub
Sub btnDelete_Click    '刪除書籍資料
	Dim BookID As String =edtBookID.Text 
    Dim strSQL As String
    strSQL = "DELETE FROM 書籍資料表 WHERE 書號 = '" & BookID & "'"
    SQLCmd.ExecNonQuery(strSQL)
    ToastMessageShow("刪除一筆書籍記錄...", False)
End Sub
Sub btnSelect_Click
  If SpinQueryBookName.Visible =True Then
     SpinQueryBookName.Visible =False
     SpinQueryBookID.Visible =True
  Else
     SpinQueryBookID.Visible =True
  End If
    SpinQueryBookID.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 書號 FROM 書籍資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookID.Add(CursorDBPoint.GetString("書號"))
    Next
End Sub

Sub SpinQueryBookID_ItemClick (Position As Int, Value As Object)
    Dim SQL_Query As String
	Dim QueryBookID As String =SpinQueryBookID.SelectedItem
	SQL_Query="Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書號='" & QueryBookID & "' "
	CallShow_BookData(SQL_Query)
	SpinQueryBookID.Visible =False
End Sub

Sub CallShow_BookData(strSQL As String)  '顯示「出版社名稱」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
        CursorDBPoint.Position = 0
		edtBookID.Text = CursorDBPoint.GetString("書號")
        edtBookName.Text=CursorDBPoint.GetString("書名")
		edtAuthor.Text = CursorDBPoint.GetString("作者")
        edtPubDate.Text = CursorDBPoint.GetString("出版日期")
	    edtPrice.Text = CursorDBPoint.GetString("定價")
        edtDNS.Text = CursorDBPoint.GetString("官方網站")
End Sub

Sub btnSelectBookName_Click
  If SpinQueryBookID.Visible =True Then
     SpinQueryBookID.Visible =False
	 SpinQueryBookName.Visible =True
  Else
     SpinQueryBookName.Visible =True
  End If
    SpinQueryBookName.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 書名 FROM 書籍資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryBookName.Add(CursorDBPoint.GetString("書名"))
    Next
End Sub
Sub SpinQueryBookName_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryBookName As String =SpinQueryBookName.SelectedItem
	SQL_Query="Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書名='" & QueryBookName & "' "
	'Msgbox(SQL_Query,"書籍資料")
	CallShow_BookData(SQL_Query)
	SpinQueryBookName.Visible =False
End Sub

Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity("ManagerAPP")
End Sub