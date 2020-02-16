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
	Dim Check_BookNo_Exist As Boolean=False    '檢查新增「書號」時是否重複
End Sub

Sub Globals
    '宣告輸入「書籍資料表」的相關資料
	Dim edtBookNo As EditText           '書號
	Dim edtBookName As EditText         '書名
	
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button             '新增功能
	Dim btnUpdate As Button             '修改功能
	Dim btnDelete As Button             '刪除功能
	Dim btnSelect As Button             '查詢功能
	
	Dim livBookStore As ListView          '顯示「書籍資料表」清單元件
	Dim btnExitBookStore As Button        '離開功能
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
	Dim lblResult As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyeBookDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("BookStore")
	Activity.Title ="【書籍管理系統】"
	edtBookNo.Text="B0007"
	edtBookName.Text="手機與機器人程式設計"
	QueryDBList("Select * FROM 書籍資料表")      '呼叫顯示目前存在的書籍資料表之副程式
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnInsert_Click  '新增「書籍」資料
 Dim strSQL As String
 Dim strBookNo As String =edtBookNo.Text             '將「書號」欄位內容指定給變數
 Dim strBookName As String = edtBookName.Text        '將「書名」欄位內容指定給變數
 '判斷使用者是否有完整的填入三個欄位內容
 If strBookNo="" OR strBookName="" Then 
    Msgbox("您尚未完整輸入「書籍資料」哦！","錯誤訊息回報!!!")
 Else
    strSQL = "INSERT INTO 書籍資料表(書號,書名)" & _
             "VALUES('" & strBookNo & "','" & strBookName & "')"
    Check_BookNo_Exists(strBookNo)        '呼叫檢查「書號」是否有重複新增之副程式
	If Check_BookNo_Exist=True Then    '檢查是否有重複新增
	    Msgbox("您已經重複新增「書號」了！","新增錯誤訊息!!!")
    Else                               '沒有重複新增
	  SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
      ToastMessageShow("您新增成功「書籍資料」記錄!", True)   '顯示新增成功狀態
    End If
	Check_BookNo_Exist=False                                   '設定沒有重複新增「書號」
  End If
	QueryDBList("Select * FROM 書籍資料表")      '呼叫顯示目前存在的書籍資料表之副程式  
End Sub

Sub Check_BookNo_Exists(strBookNo As String)       '檢查「書號」是否有重複新增之副程式
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 書籍資料表 Where 書號='" & strBookNo & "' "
		Msgbox(SQL_Query,"SQL")
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「書籍資料表」是否已經存在一筆了
	   Check_BookNo_Exist=True                    '代表重複新增「書號」了!
	End If
End Sub
	
Sub btnUpdate_Click   '修改「書籍」資料
 If edtBookNo.Text="" Then 
    Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!")
 Else
   Dim strBookNo As String =edtBookNo.Text
   Dim strSQL As String
   strSQL = "UPDATE 書籍資料表 SET 書名 ='" & edtBookName.Text & "' WHERE 書號 = '" & strBookNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆「書籍資料」記錄...", False)
	QueryDBList("Select * FROM 書籍資料表")      '呼叫顯示目前存在的書籍資料表之副程式
 End If   
End Sub

Sub btnDelete_Click   '刪除「書籍」資料
 If edtBookNo.Text="" Then 
    Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!")
 Else
   Dim strBookNo As String =edtBookNo.Text
   Dim strSQL As String
   strSQL = "DELETE FROM 書籍資料表"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除全部「書籍資料」記錄...", False)
	QueryDBList("Select * FROM 書籍資料表")      '呼叫顯示目前存在的書籍資料表之副程式
 End If   
End Sub
Sub btnSelect_Click
 If edtBookNo.Text="" Then 
    Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!")
 Else
   Dim strBookNo As String =edtBookNo.Text
   Dim strSQL As String
   strSQL = "DELETE FROM 書籍資料表 WHERE 書號 = '" & strBookNo & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆「書籍資料」記錄...", False)
	QueryDBList("Select * FROM 書籍資料表")      '呼叫顯示目前存在的書籍資料表之副程式
 End If   
End Sub
Sub QueryDBList(strSQLite As String)
    livBookStore.Clear '清單清空
	strSqlViewTitle =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livBookStore.SingleLineLayout.Label.TextSize=16
	livBookStore.SingleLineLayout.ItemHeight = 20dip
	For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & "                " 
		strLine=strLine & "================"              '設定「分隔水平線之每一小段的長度」
	Next	
    livBookStore.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」
    livBookStore.AddSingleLine (strLine)          '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livBookStore.AddSingleLine ((i+1) & ". " & strSqlViewContent )   '顯示「每一筆記錄的內容」
		 strSqlViewContent =""
	Next	
    ToastMessageShow("目前的書籍記錄共有: " & CursorDBPoint.RowCount & " 筆!",False)
	lblResult.Text ="===書籍清單共有( " & CursorDBPoint.RowCount & " 筆)==="
End Sub

Sub btnExitBookStore_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub


