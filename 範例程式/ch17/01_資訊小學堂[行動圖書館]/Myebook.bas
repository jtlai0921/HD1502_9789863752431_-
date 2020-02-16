Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL
	Dim SQL_Query As String
    Dim CursorDBPoint As Cursor
	Dim DNS As String
	Dim Check_MyloveBooks_Exist As Boolean=False   '檢查我的最愛書籍是否重複
End Sub

Sub Globals
	Dim btnReturn As Button
	Dim ImageView1 As ImageView
	Dim Button1 As Button
	Dim ImageView2 As ImageView
	
	Dim lblBookID As Label
	Dim lblBookName As Label
	Dim lblAuthor As Label
	Dim lblPubDate As Label
	Dim lblPrice As Label
	Dim lblBookClass As Label
	Dim lblBookStore As Label
	Dim btnMyLove As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("Myebook")
	If UserAPP.ChangeQuery =1 Then
	    Activity.Title ="您正查詢「" & BookTable.BookName_1  & "」電子書"
	Else If UserAPP.ChangeQuery =2 Then
	    Activity.Title ="您正查詢「" & KeywordQuery.BookName_2  & "」電子書"
	End If
	Show_BookPicture '呼叫顯示書籍的封面照片之副程式
End Sub

Sub Activity_Resume
 If UserAPP.ChangeQuery =1 Then
	CheckMyloveBooks_Exists(BookTable.BookID_1)
 Else If UserAPP.ChangeQuery =2 Then
    CheckMyloveBooks_Exists(KeywordQuery.BookID_2)
 Else If UserAPP.ChangeQuery =3 Then
    CheckMyloveBooks_Exists(BookClassQuery.BookID_3)
 Else If UserAPP.ChangeQuery =4 Then
    CheckMyloveBooks_Exists( MyloveBooks.BookID_4)
 End If
 
 If Check_MyloveBooks_Exist=True Then
    btnMyLove.Text="移除書籤"
 Else If Check_MyloveBooks_Exist=False Then
    btnMyLove.Text="加入書籤"
 End If
End Sub

Sub CheckMyloveBooks_Exists(strBookID As String)
	Dim SQL_Query As String
	Dim Account As String =UserLogin.UserAccount
	SQL_Query="Select * FROM 借閱記錄表 Where 書號='" & strBookID &"' And 帳號='" & Account  & "'"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    If CursorDBPoint.RowCount=1 Then
	   Check_MyloveBooks_Exist=True
	   btnMyLove.Text="移除書籤"
    Else
	   Check_MyloveBooks_Exist=False
	   btnMyLove.Text="加入書籤"
    End If
End Sub
Sub Show_BookPicture  '顯示書籍的封面照片之副程式
 Dim BookID As String
 Dim BookName As String 
 Dim Author As String
 Dim PubDate As String
 Dim Price As String
 Dim BookClass As String
 Dim BookStore As String
 If UserAPP.ChangeQuery =1 Then
    SQL_Query="Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='" & BookTable.BookID_1 & "' "
 Else If UserAPP.ChangeQuery =2 Then
    SQL_Query="Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='" & KeywordQuery.BookID_2 & "' "
 Else If UserAPP.ChangeQuery =3 Then
    SQL_Query="Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='" & BookClassQuery.BookID_3 & "' "
  Else If UserAPP.ChangeQuery =4 Then
    SQL_Query="Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='" & MyloveBooks.BookID_4 & "' "
 End If
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	  If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
     	ImageView1.Bitmap = LoadBitmapSample(File.DirAssets, CursorDBPoint.GetString("書號") & ".jpg", 100, 100)
	  Else
     	ImageView1.Bitmap = LoadBitmapSample(File.DirAssets, "0000000.jpg", 100, 100)
	  End If
        BookID=CursorDBPoint.GetString("書號")
        BookName=CursorDBPoint.GetString("書名")
        Author=CursorDBPoint.GetString("作者")
        PubDate=CursorDBPoint.GetString("出版日期")
        Price=CursorDBPoint.GetString("定價")
        BookStore=CursorDBPoint.GetString("出版社名稱")
        BookClass=CursorDBPoint.GetString("分類名稱")
        DNS=CursorDBPoint.GetString("官方網站")		
    Next 
	lblBookID.Text=BookID
	lblBookName.Text=BookName
	lblAuthor.Text=Author
	lblPubDate.Text=PubDate
	lblPrice.Text=Price
	lblBookClass.Text=BookClass
	lblBookStore.Text=BookStore
End Sub	

Sub btnMyLove_Click  '加入書籤或移除書籤
 Dim now As Long
 now=DateTime.now
 Dim strSQL As String
 Dim Account As String =UserLogin.UserAccount
 
 If btnMyLove.Text="加入書籤" AND Check_MyloveBooks_Exist=False Then   '加入書籤
    If UserAPP.ChangeQuery =1 Then
       strSQL = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('" & BookTable.BookID_1 & "','" & UserLogin.UserAccount & "','1','" & DateTime.Date(now) & "')"
    Else If UserAPP.ChangeQuery =2 Then
       strSQL = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('" & KeywordQuery.BookID_2 & "','" & UserLogin.UserAccount & "','1','" & DateTime.Date(now) & "')"
    Else If UserAPP.ChangeQuery =3 Then
       strSQL = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('" & BookClassQuery.BookID_3 & "','" & UserLogin.UserAccount & "','1','" & DateTime.Date(now) & "')"
    Else If UserAPP.ChangeQuery =4 Then
       strSQL = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('" & MyloveBooks.BookID_4 & "','" & UserLogin.UserAccount & "','1','" & DateTime.Date(now) & "')"
    End If
	SQLCmd.ExecNonQuery(strSQL)
    ToastMessageShow("加入書籤...!", False)
    btnMyLove.Text="移除書籤"
	
 Else If btnMyLove.Text="移除書籤" AND Check_MyloveBooks_Exist=True Then  '移除書籤
    If UserAPP.ChangeQuery =1 Then
       strSQL = "DELETE FROM 借閱記錄表 WHERE 書號='" & BookTable.BookID_1 &"' And 帳號='" & Account  & "'"
    Else If UserAPP.ChangeQuery =2 Then
       strSQL = "DELETE FROM 借閱記錄表 WHERE 書號='" & KeywordQuery.BookID_2 &"' And 帳號='" & Account  & "'"
    Else If UserAPP.ChangeQuery =3 Then
       strSQL = "DELETE FROM 借閱記錄表 WHERE 書號='" & BookClassQuery.BookID_3 &"' And 帳號='" & Account  & "'"
    Else If UserAPP.ChangeQuery =4 Then
       strSQL = "DELETE FROM 借閱記錄表 WHERE 書號='" & MyloveBooks.BookID_4 &"' And 帳號='" & Account  & "'"
    End If
    SQLCmd.ExecNonQuery(strSQL)
	ToastMessageShow("移除書籤...!", False)
	btnMyLove.Text="加入書籤"
 End If
End Sub 
   
Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
    Activity.Finish()    '活動完成
	If UserAPP.ChangeQuery =1 Then
	    StartActivity(BookTable)              ' 返回BookTable主活動 
	Else If UserAPP.ChangeQuery =2 Then
	    StartActivity(KeywordQuery)           ' 返回KeywordQuery主活動 
	Else If UserAPP.ChangeQuery =3 Then
	    StartActivity(BookClassQuery)         ' 返回BookClassQuery主活動 
	Else If UserAPP.ChangeQuery =4 Then
	    StartActivity(MyloveBooks)            ' 返回MyloveBooks主活動 
	End If
End Sub
Sub Button1_Click
	Dim i As Intent  '宣告「意圖物件」來啟動Android內建的APP
    i.Initialize(i.ACTION_VIEW, "https://play.google.com/store/apps/details?id=tw.idv.myebook.infoclass")
    StartActivity(i)
End Sub
Sub ImageView2_Click
	Dim i As Intent
    i.Initialize(i.ACTION_VIEW, DNS)
	StartActivity(i)
End Sub
