Type=Activity
Version=3
@EndOfDesignText@

#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

'Activity module
Sub Process_Globals
    Dim SQLCmd As SQL                         '宣告SQL物件
    Dim CursorDBPoint As Cursor               '資料庫記錄指標
	Dim SQL_Query As String
End Sub

Sub Globals
	Dim lblResult As Label
	Dim StrSQL As String 
	Dim btnReturn As Button
	Dim livebook As ListView
	Dim lblShowebook As Label
	Dim JSON As JSONParser
	Dim response As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則
       SQLCmd.Initialize(File.DirDefaultExternal, "MyeBookDB.sqlite", True) '將SQL物件初始化資料庫
    End If
	Activity.LoadLayout("Myebook")          '使用「Main(版面配置檔)」來輸出
    Activity.Title ="B4A連結SQL_Server(檢查是否為會員)"    '本頁的標題名稱
	If FirstTime Then
		HttpUtils.CallbackActivity = "Myebook"
		HttpUtils.CallbackJobDoneSub = "JobDone"
	End If
    StrSQL="select * from Member Where Password='" & Main.Password & "'"
	'直接新增到遠端的SQL Server資料庫
	'StrSQL="insert into member(username,password) values('testname','testpassword')"
	
    HttpUtils.PostString("LeechJob", Main.ServerUrl, StrSQL)
	
	lblShowebook.Visible=False
	SQL_Query="Select * FROM 書籍資料表"
	lblResult.Text="會員身份審查中..."
End Sub

Sub Activity_Resume
	If HttpUtils.Complete = True Then 
	   JobDone(HttpUtils.Job)
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
End Sub

Sub JobDone (Job As String)
	If HttpUtils.IsSuccess(Main.ServerUrl) Then
		response = HttpUtils.GetString(Main.ServerUrl)  '取得Web Service的回覆
		JSON.Initialize(response)                     '用來剖析「Web Service的回覆」資料格式內容
	End If
	HttpUtils.Complete = False
	Check_Member  '呼叫檢查使用者身份
End Sub

Sub Check_Member()
  Dim Username As String 
  Dim Output As String 
  Dim rows As List
  rows = JSON.NextArray
  If rows.Size= 1 Then
  	 Dim user As Map
	 user = rows.Get(0)
	 Username=user.Get("UserName")  '從遠端SQL Server資料庫取得使用者名稱
	 lblResult.Text="==會員身份審查通過=="
	 Output=Username.Trim & " 會員您好：" & CRLF & "您是正式會員"
 	 Msgbox(Output,"檢查結果")
	 lblResult.Text="==「" & Username.Trim & "」會員本期電子書如下=="
	 QueryBookTable(SQL_Query)   '顯示書籍
  Else
 	 lblResult.Text="==會員身份審查尚未通過=="
 	 Msgbox("抱歉!沒有你的會員資料","檢查結果")
	 Activity.Finish()  '活動完成
	 StartActivity(Main)
  End If
  HttpUtils.Complete = False	
End Sub

Sub QueryBookTable(StrSQLite As String)
    livebook.Clear()
    CursorDBPoint = SQLCmd.ExecQuery(StrSQLite)
    livebook.SingleLineLayout.Label.TextSize = 16
    For i = 0 To CursorDBPoint.RowCount - 1
	    CursorDBPoint.Position = i
		livebook.AddSingleLine((i+1) & "." & CursorDBPoint.GetString("書號") & "(" & CursorDBPoint.GetString("書名") & ")" )
    Next
End Sub

Sub livebook_ItemClick (Position As Int, Value As Object)
  Dim Temp As String
  Temp = "你剛才點選的電子書為：" & CRLF
  Temp = Temp & livebook.GetItem(Position)
  lblShowebook.Visible=True
  lblShowebook.Text =Temp
End Sub

Sub btnReturn_Click
	Activity.Finish()  '活動完成
	StartActivity(Main)
End Sub
