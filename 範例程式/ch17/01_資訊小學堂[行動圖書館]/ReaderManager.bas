Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
    Dim SQLCmd As SQL                                   '宣告SQL物件
    Dim CursorDBPoint As Cursor                         '資料庫記錄指標
	Dim Check_ReaderAccount_Exist As Boolean=False      '檢查新增「讀者」時的帳號是否重複

End Sub

Sub Globals
    '宣告輸入「書籍資料表」的相關資料
	Dim edtReader_Account As EditText         '帳號    
	Dim edtReader_Password As EditText    '密碼
	Dim edtReader_Name As EditText            '讀者的姓名
	'宣告SQL的DML(四種操作語言)
	Dim btnInsert As Button                   '新增功能
	Dim btnUpdate As Button                   '修改功能
	Dim btnDelete As Button                   '刪除功能
	Dim btnSelectReaderAccount As Button      '依照「帳號」查詢
	Dim btnSelectReaderName As Button         '依照「姓名」查詢

	Dim SpinQueryReader_Account As Spinner    '查詢帳號
	Dim SpinQueryReader_Name As Spinner       '查詢姓名
	Dim btnReturn As Button

	Dim btnReaderList As Button

End Sub

Sub Activity_Create(FirstTime As Boolean)
    If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False) '將SQL物件初始化資料庫
    End If
    Activity.LoadLayout("ReaderManager")
    Activity.Title = "【讀者資料管理系統】"
	SpinQueryReader_Account.Visible =False
	SpinQueryReader_Name.Visible =False
    edtReader_Account.Text = "44444"
    edtReader_Password.Text = "44444"
    edtReader_Name.Text = "四維"
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause(UserClosed As Boolean)

End Sub
Sub btnInsert_Click   '新增讀者資料
 Dim Reader_Account As String =edtReader_Account.Text 
 Dim Reader_Password As String = edtReader_Password.Text
 Dim Reader_Name As String= edtReader_Name.Text
 If Reader_Account="" OR Reader_Password="" Then 
    Msgbox("您尚未輸入「讀者」相關哦！","錯誤訊息回報!!!")
 Else
    Dim strSQL As String
    strSQL = "INSERT INTO 讀者資料表(帳號,密碼,姓名)" & _
             "VALUES('" & Reader_Account & "','" & Reader_Password & "','" & Reader_Name & "')"
    Check_ReaderAccount_Exists(Reader_Account)
	If Check_ReaderAccount_Exist=True Then
	    Msgbox("您已經重複新增這位讀者了！","新增錯誤訊息!!!")
    Else
	  SQLCmd.ExecNonQuery(strSQL)
	  Dim dbChange As Int
	  dbChange = SQLCmd.ExecQuerySingleResult("SELECT changes() FROM 讀者資料表")
      ToastMessageShow("新增讀者記錄: " & dbChange & " 筆", False)
    End If
	Check_ReaderAccount_Exist=False
  End If
End Sub
Sub Check_ReaderAccount_Exists(strReader_Account As String)
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 讀者資料表 Where 帳號='" & strReader_Account & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
	If CursorDBPoint.RowCount=1 Then  
	   Check_ReaderAccount_Exist=True  '代表讀者已經新增過了(存在)
	End If
End Sub

Sub btnUpdate_Click    '修改讀者資料
   Dim Reader_Account As String =edtReader_Account.Text 
   Dim strSQL As String
   strSQL = "UPDATE 讀者資料表 SET 密碼 ='" & edtReader_Password.Text & "',姓名 ='" & edtReader_Name.Text & "' WHERE 帳號 = '" & Reader_Account & "'"
   ' Msgbox(strSQL,"查詢結果")
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("更新一筆讀者記錄...", False)
End Sub

Sub btnDelete_Click    '刪除讀者資料
   Dim Reader_Account As String =edtReader_Account.Text 
   Dim strSQL As String
   strSQL = "DELETE FROM 讀者資料表 WHERE 帳號 = '" & Reader_Account & "'"
   SQLCmd.ExecNonQuery(strSQL)
   ToastMessageShow("刪除一筆讀者記錄...", False)
End Sub


Sub btnSelectReaderAccount_Click
  If SpinQueryReader_Name.Visible =True Then
     SpinQueryReader_Name.Visible =False
     SpinQueryReader_Account.Visible =True
  Else
     SpinQueryReader_Account.Visible =True
  End If
  	edtReader_Account.Visible =False
	edtReader_Name.Visible =True
    SpinQueryReader_Account.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 帳號 FROM 讀者資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryReader_Account.Add(CursorDBPoint.GetString("帳號"))
    Next
	
End Sub

Sub SpinQueryReader_Account_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryReader_Account As String =SpinQueryReader_Account.SelectedItem
	SQL_Query="Select 帳號,密碼,姓名 FROM 讀者資料表 Where 帳號='" & QueryReader_Account & "' "
	CallShow_ReaderData(SQL_Query)
	SpinQueryReader_Account.Visible =False
	edtReader_Account.Visible =True
End Sub

Sub CallShow_ReaderData(strSQL As String)  '顯示「密碼」之副程式
	CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0
	edtReader_Account.Text = CursorDBPoint.GetString("帳號")
    edtReader_Password.Text=CursorDBPoint.GetString("密碼")
    edtReader_Name.Text=CursorDBPoint.GetString("姓名")
End Sub

	
Sub btnSelectReaderName_Click
  If SpinQueryReader_Account.Visible =True Then
     SpinQueryReader_Account.Visible =False
	 SpinQueryReader_Name.Visible =True
  Else
     SpinQueryReader_Name.Visible =True
  End If
    edtReader_Name.Visible =False
  	edtReader_Account.Visible =True	
	SpinQueryReader_Name.Clear 
    CursorDBPoint = SQLCmd.ExecQuery("SELECT 姓名 FROM 讀者資料表")
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄
		SpinQueryReader_Name.Add(CursorDBPoint.GetString("姓名"))
    Next	
End Sub
Sub SpinQueryReader_Name_ItemClick (Position As Int, Value As Object)
	Dim SQL_Query As String
	Dim QueryReader_Name As String =SpinQueryReader_Name.SelectedItem
	SQL_Query="Select 帳號,密碼,姓名 FROM 讀者資料表 Where 姓名='" & QueryReader_Name & "' "
	CallShow_ReaderData(SQL_Query)
	SpinQueryReader_Name.Visible =False
	edtReader_Account.Visible =True
	edtReader_Name.Visible =True
End Sub
Sub btnReaderList_Click
	StartActivity("ReaderTable")
End Sub

Sub btnReturn_Click
    Activity.Finish()  '活動完成
	StartActivity("ManagerAPP")
End Sub