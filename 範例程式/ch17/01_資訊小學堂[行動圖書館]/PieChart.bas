Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	
End Sub

Sub Globals
	Dim Panel1 As Panel
	Dim btnReturn As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	Activity.LoadLayout("PieChart")
    Activity.Title = DataAnalysis.DataAnalysis_Title 
    Dim PieData1 As PieData
    PieData1.Initialize()
    PieData1.Target = Panel1  ' 指定顯示到Panel1容器元件中
    ' 新增資料項目及統計數量	
    For i = 0 To DataAnalysis.Total - 1
         Charts.AddPieItem(PieData1, DataAnalysis.Class_Name(i) & DataAnalysis.Class_No(i) , DataAnalysis.Class_No(i), 0)
	Next
    ' 圖例的背景色彩
    PieData1.LegendBackColor = Colors.ARGB(100, 100, 100, 100)
    ' 繪出派圖, 傳回圖例的Bitmap物件
    Dim PieChart_BookClass As Bitmap
    PieChart_BookClass = Charts.DrawPie(PieData1, Colors.Gray, True)
    If PieChart_BookClass.IsInitialized() Then  ' 有圖例
        ' 建立ImageView物件
        Dim ImageView1 As ImageView
        ImageView1.Initialize("")
        ImageView1.SetBackgroundImage(PieChart_BookClass)	
        ' 在派圖新增圖例
        Panel1.AddView(ImageView1, 0dip,0dip, PieChart_BookClass.Width, PieChart_BookClass.Height)
    End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub btnReturn_Click
	Activity.Finish()                 '活動完成
	StartActivity(DataAnalysis)       '回統計分析報表
End Sub