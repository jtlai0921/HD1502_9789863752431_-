Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL
	Dim SQL_Query_PG As String
	Dim SQL_Query_DB As String
	Dim SQL_Query_DS As String
	Dim SQL_Query_PC As String
	Dim SQL_Query_EL As String
    Dim CursorDBPoint As Cursor
	Dim PicBook As Bitmap 
	Dim BookID_3 As String     '書籍編號
	Dim SQL_Query As String

End Sub

Sub Globals
	Dim TabHost1 As TabHost
	Dim livQuery_PG As ListView
	Dim livQuery_DB As ListView
	Dim livQuery_DS As ListView
	Dim livQuery_PC As ListView
	Dim livQuery_EL As ListView
	'Dim livQuery_SA As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False)
    End If
    Activity.LoadLayout("KeywordQuery")
    Activity.Title ="歡迎使用「書籍分類系統」"
	TabHost1.AddTab("程式","Query_PG")
	TabHost1.AddTab("資料庫","Query_DB")
	TabHost1.AddTab("資結","Query_DS")
	TabHost1.AddTab("計概","Query_PC")
	TabHost1.AddTab("數位","Query_EL")
	SQL_Query_PG="Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='程式設計'"
	CallShow_BookClassQuery_PG(SQL_Query_PG)  '呼叫顯示「程式設計」類別書籍之副程式
	SQL_Query_DB="Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料庫系統'"
	CallShow_BookClassQuery_DB(SQL_Query_DB)  '呼叫顯示「資料庫系統」類別書籍之副程式
	SQL_Query_DS="Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料結構'"
	CallShow_BookClassQuery_DS(SQL_Query_DS)  '呼叫顯示「資料結構」類別書籍之副程式
	SQL_Query_PC="Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='計算機概論'"
	CallShow_BookClassQuery_PC(SQL_Query_PC)  '呼叫顯示「計算機概論」類別書籍之副程式
	SQL_Query_EL="Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='數位學習'"
	CallShow_BookClassQuery_EL(SQL_Query_EL)  '呼叫顯示「數位學習」類別書籍之副程式

End Sub
Sub CallShow_BookClassQuery_PG(strSQL As String)  '顯示「程式設計」類別書籍之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
	livQuery_PG.TwoLinesLayout.Label.TextSize = 10
	livQuery_PG.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If		
		If CursorDBPoint.GetString("書名").Length >=20 Then
           livQuery_PG.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,20) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
           livQuery_PG.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
End Sub

Sub CallShow_BookClassQuery_DB(strSQL As String)  '顯示「資料庫系統」類別書籍之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
	livQuery_DB.TwoLinesLayout.Label.TextSize = 10
	livQuery_DB.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If
		If CursorDBPoint.GetString("書名").Length >=15 Then
          livQuery_DB.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,15) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livQuery_DB.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
End Sub

Sub CallShow_BookClassQuery_DS(strSQL As String)  '顯示「資料結構」類別書籍之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
	livQuery_DS.TwoLinesLayout.Label.TextSize = 10
	livQuery_DS.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
        PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
		If CursorDBPoint.GetString("書名").Length >=15 Then
          livQuery_DS.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,15) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livQuery_DS.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
End Sub
Sub CallShow_BookClassQuery_PC(strSQL As String)  '顯示「計算機概論」類別書籍之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
	livQuery_PC.TwoLinesLayout.Label.TextSize = 10
	livQuery_PC.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
		
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If
		If CursorDBPoint.GetString("書名").Length >=15 Then
          livQuery_PC.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,15) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livQuery_PC.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
End Sub

Sub CallShow_BookClassQuery_EL(strSQL As String)  '顯示「數位學習」類別書籍之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
	livQuery_EL.TwoLinesLayout.Label.TextSize = 10
	livQuery_EL.TwoLinesLayout.ItemHeight=30dip
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i
	    If File.Exists(File.DirAssets ,CursorDBPoint.GetString("書號") & ".jpg") Then
           PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString("書號") & ".jpg")
	    Else
           PicBook=LoadBitmap(File.DirAssets,"0000000.jpg")
	    End If		
		If CursorDBPoint.GetString("書名").Length >=15 Then
          livQuery_EL.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名").SubString2(0,15) & "...",CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
		Else 
          livQuery_EL.AddTwoLinesAndBitmap2((i+1) & "." & CursorDBPoint.GetString("書名"),CursorDBPoint.GetString("出版社名稱"),PicBook,CursorDBPoint.GetString("書號"))
        End If
	Next
End Sub

Sub livQuery_PG_ItemClick (Position As Int, Value As Object)
	BookID_3 = Value    '書籍編號
    StartActivity("Myebook")
End Sub

Sub livQuery_DB_ItemClick (Position As Int, Value As Object)
	BookID_3 = Value    '書籍編號
    StartActivity("Myebook")
End Sub
Sub livQuery_DS_ItemClick (Position As Int, Value As Object)
	BookID_3 = Value    '書籍編號
    StartActivity("Myebook")
End Sub

Sub livQuery_PC_ItemClick (Position As Int, Value As Object)
	BookID_3 = Value    '書籍編號
    StartActivity("Myebook")
End Sub
Sub livQuery_EL_ItemClick (Position As Int, Value As Object)
	BookID_3 = Value    '書籍編號
   StartActivity("Myebook")
End Sub


Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

