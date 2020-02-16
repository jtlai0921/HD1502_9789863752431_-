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
	Dim BookID_2 As String     '書籍編號
	Dim BookName_2 As String   '書名
	Dim Query_Index As String  '依照查詢類別 1代表書號  2代表作者  3代表出版日期  4代表出版社
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim TabHost1 As TabHost
	Dim livBookList As ListView
	Dim btnKeyword As Button
	Dim edtKeyword As EditText
	Dim PicBook As Bitmap 
	Dim spnQueryClass As Spinner
	Dim edtQueryClass As EditText
	Dim btnClassQuery As Button
	Dim edtQueryClass_BookID As EditText
	Dim edtQueryClass_Author As EditText
	Dim edtQueryClass_PubDate As EditText
	Dim edtQueryClass_BookStore As EditText
	Dim livQuery_Class As ListView
	Query_Index=1
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("KeywordQuery")
    Activity.Title ="歡迎使用「關鍵字查詢系統」"
	TabHost1.AddTab("查詢關鍵字","Query_keyword")
	TabHost1.AddTab("查詢各類別","Query_Class")
	edtQueryClass_BookID.Visible=True
	edtQueryClass_Author.Visible=False
	edtQueryClass_PubDate.Visible=False
	edtQueryClass_BookStore.Visible=False

	spnQueryClass.Add("書號")
	spnQueryClass.Add("作者")
	spnQueryClass.Add("出版日期")
	spnQueryClass.Add("出版社名稱")
End Sub
Sub btnKeyword_Click
    livBookList.Clear()
	Dim Temp As String 
	Dim Keyword As String =edtKeyword.text
  If Keyword="" Then
     Msgbox("您尚未輸入關鍵字哦!","錯誤訊息!!!")
  Else
	SQL_Query="Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書名 Like '%" & Keyword & "%' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	livBookList.TwoLinesLayout.Label.TextSize = 10
	livBookList.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If
		If CursorDBPoint.GetString("書名").Length >=10 Then
          livBookList.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,10) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livBookList.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
	If i=0 Then
	   Temp="找不到關鍵字「" & Keyword & "」!" & CRLF & "請重新查詢!!!"
	   Msgbox(Temp,"查詢結果")
	   edtKeyword.text=""
	End If
  End If
End Sub

Sub livBookList_ItemClick (Position As Int, Value As Object)
	BookID_2 = Value    '書籍編號
  	SQL_Query="Select 書名 FROM 書籍資料表 Where 書號='" & BookID_2 & "' "
	'Msgbox(SQL_Query,"查詢結果")
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        BookName_2=CursorDBPoint.GetString("書名")
    Next
  StartActivity("Myebook")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub
Sub spnQueryClass_ItemClick (Position As Int, Value As Object)
	Select Value      '取得出版社名稱
       Case "書號": 
  	       edtQueryClass_BookID.Visible=True
		   edtQueryClass_Author.Visible=False
	       edtQueryClass_PubDate.Visible=False
		   edtQueryClass_BookStore.Visible=False
		   Query_Index=1
	   Case "作者": 	 
	       edtQueryClass_BookID.Visible=False
  	       edtQueryClass_Author.Visible=True
	       edtQueryClass_PubDate.Visible=False	
	       edtQueryClass_BookStore.Visible=False
		   Query_Index=2
       Case "出版日期"    
	   	   edtQueryClass_BookID.Visible=False
	       edtQueryClass_Author.Visible=False
	       edtQueryClass_PubDate.Visible=True
	       edtQueryClass_BookStore.Visible=False
		   Query_Index=3
       Case "出版社名稱"    
	   	   edtQueryClass_BookID.Visible=False
	       edtQueryClass_Author.Visible=False
	       edtQueryClass_PubDate.Visible=False
	       edtQueryClass_BookStore.Visible=True
		   Query_Index=4		   
    End Select 
End Sub
Sub btnClassQuery_Click
    livQuery_Class.Clear()
  If edtQueryClass_BookID.text="" AND edtQueryClass_Author.text="" AND edtQueryClass_PubDate.text="" AND edtQueryClass_BookStore.text="" Then
     Msgbox("您尚未輸入關鍵字哦!","錯誤訊息!!!")
  Else	
	If Query_Index=1 Then
	    Dim Keyword As String =edtQueryClass_BookID.text
	    SQL_Query="Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書號='" & Keyword & "' "
 	Else If Query_Index=2 Then
	    Dim Keyword As String =edtQueryClass_Author.text
	    SQL_Query="Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 作者 Like '%" & Keyword & "%' "
    Else If Query_Index=3 Then
	    Dim Keyword As String =edtQueryClass_PubDate.text
	    SQL_Query="Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版日期 Like '%" & Keyword & "%' "
    Else If Query_Index=4 Then
	    Dim Keyword As String =edtQueryClass_BookStore.text
	    SQL_Query="Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版社名稱 Like '%" & Keyword & "%' "	
	End If
	'Msgbox(SQL_Query,"查詢結果")
	CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	livQuery_Class.TwoLinesLayout.Label.TextSize = 10
	livQuery_Class.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If			
		If CursorDBPoint.GetString("書名").Length >=10 Then
          livQuery_Class.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,10) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livQuery_Class.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
	If i=0 Then
	   Temp="找不到關鍵字「" & Keyword & "」!" & CRLF & "請重新查詢!!!"
	  ' Msgbox(Temp,"查詢結果")
	   edtQueryClass_BookID.text=""
	   edtQueryClass_Author.text=""
	   edtQueryClass_PubDate.text=""
	   edtQueryClass_BookStore.text=""
	End If
  End If
End Sub
Sub livQuery_Class_ItemClick (Position As Int, Value As Object)
	BookID_2 = Value    '書籍編號
  	SQL_Query="Select 書名 FROM 書籍資料表 Where 書號='" & BookID_2 & "' "
	'Msgbox(SQL_Query,"查詢結果")
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        BookName_2=CursorDBPoint.GetString("書名")
    Next
  StartActivity("Myebook")
End Sub
Sub btnReturn_Click
    Activity.Finish()         '活動完成
	StartActivity(UserAPP)  ' 返回UserAPP主活動 
End Sub