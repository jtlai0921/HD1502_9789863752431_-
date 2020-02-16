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
	Dim StrSQL As String       '宣告SQL的查詢指令字串(查詢密碼)
End Sub

Sub Globals
	Dim btnReturn As Button    '返回鈕
	Dim lblResult As Label     '顯示「會員身份審查結果」標題字
	Dim Button_MyeBook As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("Member")                         '使用「Main(版面配置檔)」來輸出
    Activity.Title ="B4A連結SQL_Server(檢查是否為會員)"    '本頁的標題名稱
    If FirstTime Then                           
		HttpUtils.CallbackActivity = "Member"      '設定回覆的活動頁面名稱
		HttpUtils.CallbackJobDoneSub = "JobDone"    '設定當HTTP完成下載工作之後，所要觸發的事件名稱
	End If
	StrSQL="select * from Member Where Password='" & Main.Password & "'"
	'ServerUrl代表網站中Web Service的網址，StrSQL是指對此Web Service查詢的指令
    lblResult.Text="會員身份審查中..."
	HttpUtils.PostString("MyJob1", Main.ServerUrl, StrSQL)
End Sub

Sub JobDone (Job As String)
	If HttpUtils.IsSuccess(Main.ServerUrl) Then        '當HTTP完成下載成功
		ToastMessageShow("連線成功!!", True)  '顯示資料載入完成!!
		response = HttpUtils.GetString(Main.ServerUrl) '取得Web Service的回覆
		JSON.Initialize(response)                 '用來剖析「Web Service的回覆」資料格式內容
	Else                                          '下載失敗
		ToastMessageShow("連線失敗!!", True)      '顯示載入失敗!!
	End If	
	Check_Member  '呼叫檢查使用者身份
End Sub

Sub Check_Member()
    Dim Username As String 
    Dim Output As String 
	Dim ArrayRows As List                   'ArrayRows視為陣列
	Dim Key_Value As Map                    'Key_Value視為成對的(key和Value)
    ArrayRows = JSON.NextArray()            '將取得的JSON資料剖析成List串列(亦即資料列)
	If ArrayRows.Size= 1 Then               '如果有找到一位符合條件的會員資料
		Key_Value = ArrayRows.Get(0)        '取得這一筆會員記錄
		Username=Key_Value.Get("UserName")  '取得這一筆會員記錄之姓名資料
		lblResult.Text="==會員身份審查通過=="
	    Output=Username.Trim & "會員 您好：" & CRLF & "您是正式會員"
        Msgbox(Output,"檢查結果")
		lblResult.Text="==「" & Username.Trim & "」會員您好=="
    Else
		lblResult.Text="==會員身份審查尚未通過=="
        Msgbox("抱歉!沒有你的會員資料","檢查結果")
		Activity.Finish()                   '活動完成
        StartActivity(Main)                 '回主頁面再重新輸入密碼
    End If
End Sub

Sub btnReturn_Click     '返回
	Activity.Finish()   '活動完成
	StartActivity(Main)
End Sub

Sub Button_MyeBook_Click  '歡迎閱讀電子書
	Activity.Finish()     '活動完成
	StartActivity(Myebook)	
End Sub

Sub Activity_Resume


End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
End Sub

