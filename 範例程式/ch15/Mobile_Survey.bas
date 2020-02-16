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
End Sub

Sub Globals
	Dim lblResult As Label
	Dim btnRun As Button
	Dim rdoOption1 As RadioButton
	Dim rdoOption2 As RadioButton
	Dim rdoOption3 As RadioButton
	Dim rdoOption4 As RadioButton
	Dim rdoOption5 As RadioButton
	Dim btnQuery As Button
	Dim CheckOptionValue As Int  '記錄問卷的選項1~5(其中1代表非常不滿意，5代表非常滿意)
	Dim lblDisplaySurveyTitle As Label
	Dim spnStudent As Spinner
	Dim StudNo As String              '學號
	Dim SurveyNo As String            '題號
	Dim btnMoveFirst As Button        '第一題
	Dim btnMovePrevious As Button     '上一題
	Dim btnMoveNext As Button         '下一題
	Dim btnMoveLast As Button         '最後一題
	Dim intSurvey As Int
	Dim btnExitSurvey As Button
	Dim Check_Survey_Exist As Boolean=False  '檢查是否有「重複填問卷」(預設為否)

	Dim livSurvey As ListView
	'ListView清單元件中，用來顯示標頭欄位名稱、分隔分平線及記錄
	Dim strSqlViewTitle1 As String =""
	Dim strSqlViewTitle2 As String =""
	Dim strLine As String =""
	Dim strSqlViewContent As String =""
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal,"MyeSurveyDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("Mobile_Survey")
    Activity.Title ="行動問卷"
	SurveyNo="D0001"
	CallShow_StudentDB("SELECT * FROM 學生資料表")    '呼叫顯示「學生資料」之副程式
	DisplaySurveyTitle("Select * FROM 問卷題庫表",SurveyNo)    '呼叫顯示行動問卷每一個題目之副程式
	QueryDBList("Select * FROM 問卷記錄表 Where 學號='" & spnStudent.SelectedItem.SubString2(0,5) & "' Order by 題號")    '顯示您已填的問卷清單
End Sub

Sub CallShow_StudentDB(strSQL As String)  '顯示「學生資料」之副程式
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    For i = 0 To CursorDBPoint.RowCount - 1
        CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄
		spnStudent.Add(CursorDBPoint.GetString("學號") & "/" & CursorDBPoint.GetString("姓名"))
    Next
End Sub

Sub DisplaySurveyTitle(strSQL As String,indexSurvey As String)  '呼叫顯示行動問卷每一個題目之副程式
    intSurvey =indexSurvey.SubString2(4,5)
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = intSurvey-1            '設定開始游標指定第一筆記錄
	lblDisplaySurveyTitle.Text=CursorDBPoint.GetString("題號").SubString2(4,5) & ". " & CursorDBPoint.GetString("題目")
	SurveyNo=CursorDBPoint.GetString("題號")
End Sub

Sub btnNextQuestion_Click
Dim strSQL As String ="SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表"
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	Msgbox(CursorDBPoint.GetString("總問卷題數"),"總問卷題數")
End Sub

Sub ClearSurveyItem()  '呼叫清除前一題問卷的選項之副程式
    rdoOption1.Checked = False
    rdoOption2.Checked = False
    rdoOption3.Checked = False
    rdoOption4.Checked = False
    rdoOption5.Checked = False
End Sub

Sub btnMoveFirst_Click  '第一題
    DisplaySurveyTitle("Select * FROM 問卷題庫表","D0001")
End Sub	

Sub btnMovePrevious_Click  '上一題
	If intSurvey<=1 Then
	  Msgbox("已經是第一筆了!","訊息公佈")
	Else
      intSurvey=intSurvey-1
      SurveyNo="D000" & intSurvey
      DisplaySurveyTitle("Select * FROM 問卷題庫表",SurveyNo)
	End If
End Sub
Sub btnMoveNext_Click  '下一題
	Dim strSQL As String ="SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表"
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	If intSurvey>=CursorDBPoint.GetString("總問卷題數") Then
	    Msgbox("已經是最後一筆了!","訊息公佈")
	Else
	  intSurvey=intSurvey+1
	  If CursorDBPoint.GetString("總問卷題數")>=10 Then
	     SurveyNo="D00" & intSurvey
	  Else
	     SurveyNo="D000" & intSurvey
	  End If
      DisplaySurveyTitle("Select * FROM 問卷題庫表",SurveyNo)
	End If
	 ClearSurveyItem  '呼叫清除前一題問卷的選項之副程式
End Sub
Sub btnMoveLast_Click  '最後一題
    Dim strSQL As String ="SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表"
    CursorDBPoint = SQLCmd.ExecQuery(strSQL)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	If CursorDBPoint.GetString("總問卷題數")>=10 Then
	  SurveyNo="D00" & CursorDBPoint.GetString("總問卷題數")
	Else
	  SurveyNo="D000" & CursorDBPoint.GetString("總問卷題數")
	End If
    DisplaySurveyTitle("Select * FROM 問卷題庫表",SurveyNo)	
End Sub

Sub btnRun_Click
 If rdoOption1.Checked = False AND rdoOption2.Checked = False AND rdoOption3.Checked = False AND rdoOption4.Checked = False AND rdoOption5.Checked = False Then
   Msgbox("您尚未點選問卷記錄表!","錯誤訊息")
 Else
  If rdoOption1.Checked = True Then         '非常滿意
    CheckOptionValue=5
  Else If rdoOption2.Checked = True Then    '滿意
    CheckOptionValue=4
  Else If rdoOption3.Checked = True Then    '普通
    CheckOptionValue=3
  Else If rdoOption4.Checked = True Then    '不滿意
    CheckOptionValue=2
  Else If rdoOption5.Checked = True Then    '非常不滿意
    CheckOptionValue=1
  End If
   Save_OptionValue '呼叫儲存選項值
 End If
 End Sub
Sub Save_OptionValue()
    Dim strSQL As String
	StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號
	strSQL = "INSERT INTO 問卷記錄表(學號,題號,滿意值) VALUES('" & StudNo & "','" & SurveyNo & "','" & CheckOptionValue & "')"
	Check_StuID_SurveyNo_Exists(StudNo,SurveyNo)       '呼叫檢查「重複填寫問卷」之副程式
    If Check_Survey_Exist=True Then    '檢查是否有重複新增
	   Msgbox("您已經「重複填寫此題問卷」了！","填寫問卷錯誤!!!")
    Else                               '沒有重複新增
	   SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄
       ToastMessageShow("您完成第" & SurveyNo.SubString2(4,5) & "題問卷", True)   '顯示新增成功狀態
    End If
	Check_Survey_Exist=False    
	QueryDBList("Select * FROM 問卷記錄表 Where 學號='" & StudNo & "' Order by 題號")    '顯示您已填的問卷清單
	Check_Survery_Complete  '呼叫檢查問卷是否全部填寫完成之副程式
End Sub

Sub Check_Survery_Complete()  '檢查問卷是否全部填寫完成之副程式
	Dim SQL_Query As String
	Dim Msg_Value As String
	StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號
  	SQL_Query="Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists" & _
	          "(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='" & StudNo & "'))"		
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	If CursorDBPoint.RowCount>0 Then             '檢查目前此份問卷，該位同學已經填過了
       Msg_Value = Msgbox2("恭喜您填寫完成了!" & CRLF & "您確定要離開嗎?", "行動問卷管理系統", "確認", "", "取消", LoadBitmap(File.DirAssets,"Leech_icon.jpg"))
       If Msg_Value = DialogResponse.POSITIVE Then
          Activity.Finish()  '活動完成
          StartActivity(Main)
       End If	
	End If
End Sub
Sub Check_StuID_SurveyNo_Exists(strStudNo As String,strSurveyNo As String)    '檢查「重複填寫問卷」之副程式
	Dim SQL_Query As String
  	SQL_Query="Select * FROM 問卷記錄表 Where 學號='" & strStudNo & "' And 題號='" & strSurveyNo & "' "
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值
	If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「此題問卷」是否已經存在
	   Check_Survey_Exist=True                   '代表重複「填寫問卷」了!
	End If
End Sub

Sub btnExitSurvey_Click
    Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)
  
End Sub
Sub Check_NoWriteItem_Survey  '呼叫檢查尚未填寫的題目之副程式(利用差集運算)
	Dim SQL_Query As String
	Dim out As String 
	StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號
  	SQL_Query="Select 題號 From 問卷題庫表 Except Select 題號 From 問卷記錄表 Where 學號='" & StudNo & "'"
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	If CursorDBPoint.RowCount>0 Then          
	   For i = 0 To CursorDBPoint.RowCount - 1     
	       CursorDBPoint.Position = i              
		  out=out& CursorDBPoint.GetString("題號") & "  " 
       Next 
	   Msgbox("您尚未完成此份問卷的填寫哦!" & CRLF & out,"未完成問卷通知!!!")
      DisplaySurveyTitle("Select * FROM 問卷題庫表",out.SubString2(0,5))
	End If

End Sub
Sub spnStudent_ItemClick (Position As Int, Value As Object)  '利用除法運算
	Dim SQL_Query As String
	StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號
  	SQL_Query="Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists" & _
	          "(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='" & StudNo & "'))"		
    CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)
    CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄
	If CursorDBPoint.RowCount>0 Then             '檢查目前此份問卷，該位同學已經填過了
	   Msgbox(CursorDBPoint.GetString("姓名"),"全部填完通知!!!")
	Else
	   Check_NoWriteItem_Survey  '呼叫檢查尚未填寫的題目之副程式
	End If
	 QueryDBList("Select * FROM 問卷記錄表 Where 學號='" & StudNo & "' Order by 題號")    '顯示您已填的問卷清單
End Sub

Sub QueryDBList(strSQLite As String)
    livSurvey.Clear '清單清空
	strSqlViewTitle1 ="您本份問卷記錄如下：" & CRLF
	strSqlViewTitle2 =""
	strSqlViewContent =""
	strLine =""
    CursorDBPoint = SQLCmd.ExecQuery(strSQLite)
	livSurvey.SingleLineLayout.Label.TextSize=12
	livSurvey.SingleLineLayout.ItemHeight = 15dip
	For i = 1 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」
	    strSqlViewTitle2=strSqlViewTitle2 & CursorDBPoint.GetColumnName(i) & "     " 
		strLine=strLine & "======="              '設定「分隔水平線之每一小段的長度」
	Next	
    livSurvey.AddSingleLine (strSqlViewTitle1)  '顯示「您本份問卷記錄如下：」
    livSurvey.AddSingleLine (strSqlViewTitle2)  '顯示「欄位名稱」
    livSurvey.AddSingleLine (strLine)           '顯示「分隔水平線」
    For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數
        CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)
      For j=1 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數
	     strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & "    "
	  Next	
	  	 livSurvey.AddSingleLine2((i+1) & ". " & strSqlViewContent,CursorDBPoint.GetString(CursorDBPoint.GetColumnName(1)))
		 strSqlViewContent =""
	Next	
End Sub

Sub livSurvey_ItemClick (Position As Int, Value As Object)
  Dim Msg_Value As String
  Dim strSQL As String
  Msg_Value = Msgbox2("您確定要「刪除」此題問卷記錄嗎?", "確認刪除對話方塊", "確認", "", "取消", Null)
  If Msg_Value = DialogResponse.POSITIVE Then
  	strSQL="DELETE FROM 問卷記錄表 Where 學號='" & spnStudent.SelectedItem.SubString2(0,5) & "' And 題號='" & livSurvey.GetItem(Position) & "' "
	Msgbox(strSQL,"strSQL")
     SQLCmd.ExecNonQuery(strSQL)
     livSurvey.RemoveAt(Position)
     ToastMessageShow("刪除「問卷資料」記錄...", False)
	 QueryDBList("Select * FROM 問卷記錄表 Where 學號='" & spnStudent.SelectedItem.SubString2(0,5) & "' Order by 題號")    '顯示您已填的問卷清單
  End If
End Sub