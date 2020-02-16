Type=StaticCode
Version=3
@EndOfDesignText@
'Code module
'Subs in this code module will be accessible from all modules.
Sub Process_Globals
    Dim SQLCmd As SQL
End Sub
'將SQL物件初始化資料庫
Sub MyeBookDB
    If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則
        SQLCmd.Initialize(File.DirDefaultExternal, "MyebookDBS.sqlite", False) '將SQL物件初始化資料庫
    End If
End  Sub
