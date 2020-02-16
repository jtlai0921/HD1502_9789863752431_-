Type=StaticCode
Version=3
@EndOfDesignText@
'Code module
'Subs in this code module will be accessible from all modules.
Sub Process_Globals
  Dim Title As String : Title = "我的延遲函數"
End Sub

Sub Delay(ms As Long)
    Dim now As Long
    If ms > 1000 Then ms = 1000 
    now = DateTime.now
    Do Until (DateTime.now > now + ms)
        DoEvents
    Loop
End Sub