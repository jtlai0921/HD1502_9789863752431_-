Type=Activity
Version=3
@EndOfDesignText@

#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

'Activity module
Sub Process_Globals
	Dim JSON As JSONParser     'JSONParser物件是用來剖析JSON資料(類似XML)
	Dim response As String     '取得Web Service的回覆
	Dim StrSQL As String       '宣告SQL的查詢指令字串
	Dim ServerUrl As String    '宣告Web Service的網址
    ServerUrl = "http://120.118.165.192:81/B4ASQL/b4asql.php"
End Sub

Sub Globals
	Dim btnReturn As Button    '返回鈕
	Dim lblResult As Label     '顯示「會員身份審查結果」標題字
	Dim lblShowebook As Label  '顯示「請點選喜歡的電子書」標題字
	Dim livebook As ListView   '用來顯示電子書的清單內容
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("Myebook")                         '使用「Main(版面配置檔)」來輸出
    Activity.Title ="歡迎閱讀電子書"    '本頁的標題名稱
    If FirstTime Then                           
		HttpUtils.CallbackActivity = "Myebook"      '設定回覆的活動頁面名稱
		HttpUtils.CallbackJobDoneSub = "JobDone"    '設定當HTTP完成下載工作之後，所要觸發的事件名稱
	End If
	StrSQL="select * from Myebook"
	'ServerUrl代表網站中Web Service的網址，StrSQL是指對此Web Service查詢的指令
    lblResult.Text="電子書載入中..."
	lblShowebook.Visible=False
	HttpUtils.PostString("MyJob2", ServerUrl, StrSQL)
	Listebook  '顯示書單
End Sub

Sub JobDone (Job As String)
	If HttpUtils.IsSuccess(Main.ServerUrl) Then        '當HTTP完成下載成功
		ToastMessageShow("連線成功!!", True)  '顯示資料載入完成!!
		response = HttpUtils.GetString(ServerUrl) '取得Web Service的回覆
		JSON.Initialize(response)                 '用來剖析「Web Service的回覆」資料格式內容
	Else                                          '下載失敗
		ToastMessageShow("連線失敗!!", True)      '顯示載入失敗!!
	End If	
End Sub

Sub Listebook()
   livebook.SingleLineLayout.Label.TextSize=20
   livebook.SingleLineLayout.ItemHeight = 25dip
	Dim ArrayRows As List            'ArrayRows視為陣列
	Dim Key_Value As Map             'Key_Value視為成對的(key和Value)
    ArrayRows = JSON.NextArray()     '將取得的JSON資料剖析成List串列(亦即資料列)
	For i = 0 To ArrayRows.Size - 1  '顯示全部的記錄（資料列）
		Key_Value = ArrayRows.Get(i) '取得每一筆記錄
		livebook.AddSingleLine((i+1) & ". 書名：" & Key_Value.Get("Book_Name")) '顯示記錄的中的「Book_Name」欄位值
	Next
   lblShowebook.Visible=True
   lblResult.Text="電子書載入完成！！"
End Sub

Sub livebook_ItemClick (Position As Int, Value As Object)
  Dim Temp As String
  Temp = "你剛才點選的電子書為：" & CRLF
  Temp = Temp & livebook.GetItem(Position)
  lblShowebook.Text =Temp
End Sub

Sub btnReturn_Click
	Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub

Sub Activity_Resume


End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
End Sub
