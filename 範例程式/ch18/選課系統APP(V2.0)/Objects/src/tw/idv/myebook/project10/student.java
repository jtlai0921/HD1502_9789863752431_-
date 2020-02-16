package tw.idv.myebook.project10;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class student extends Activity implements B4AActivity{
	public static student mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project10", "tw.idv.myebook.project10.student");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (student).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project10", "tw.idv.myebook.project10.student");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project10.student", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (student) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (student) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return student.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (student) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (student) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.sql.SQL _v5 = null;
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _v6 = null;
public static boolean _check_stuid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtstudno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtstudname = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spndeptno = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerystudno = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livstudent = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlistdata = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitstudent = null;
public static String _vvv2 = "";
public static String _vv7 = "";
public static String _vv0 = "";
public static String _vvv1 = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public tw.idv.myebook.project10.main _vv6 = null;
public tw.idv.myebook.project10.department _vv1 = null;
public tw.idv.myebook.project10.course _v0 = null;
public tw.idv.myebook.project10.course_selection _course_selection = null;
public tw.idv.myebook.project10.query _vv2 = null;
public tw.idv.myebook.project10.writesql _vv4 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_v5.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 39;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeSchoolDB.sqlite\", True) '將SQL物件初始化資料庫";
_v5.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 41;BA.debugLine="Activity.LoadLayout(\"Student\")";
mostCurrent._activity.LoadLayout("Student",mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="Activity.Title =\"【學生資料管理系統】\"";
mostCurrent._activity.setTitle((Object)("【學生資料管理系統】"));
 //BA.debugLineNum = 43;BA.debugLine="SpinQueryStudNo.Visible=False  '查詢「科系代碼」的下拉式元件隱藏";
mostCurrent._spinquerystudno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 44;BA.debugLine="edtStudNo.Text=\"S0005\"";
mostCurrent._edtstudno.setText((Object)("S0005"));
 //BA.debugLineNum = 45;BA.debugLine="edtStudName.Text=\"五福\"";
mostCurrent._edtstudname.setText((Object)("五福"));
 //BA.debugLineNum = 46;BA.debugLine="CallShow_DeptDB(\"SELECT * FROM 科系代碼表\")  '呼叫顯示「科系代碼」之副程式";
_callshow_deptdb("SELECT * FROM 科系代碼表");
 //BA.debugLineNum = 47;BA.debugLine="QueryDBList(\"Select * FROM 學生資料表\")      '呼叫顯示目前存在的學生資料表之副程式";
_vv5("Select * FROM 學生資料表");
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _studno = "";
String _strsql = "";
 //BA.debugLineNum = 111;BA.debugLine="Sub btnDelete_Click   '刪除「學生」資料";
 //BA.debugLineNum = 112;BA.debugLine="If edtStudNo.Text=\"\" Then";
if ((mostCurrent._edtstudno.getText()).equals("")) { 
 //BA.debugLineNum = 113;BA.debugLine="Msgbox(\"您尚未輸入「學生資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「學生資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 115;BA.debugLine="Dim StudNo As String =edtStudNo.Text";
_studno = mostCurrent._edtstudno.getText();
 //BA.debugLineNum = 116;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 117;BA.debugLine="strSQL = \"DELETE FROM 學生資料表 WHERE 學號 = '\" & StudNo & \"'\"";
_strsql = "DELETE FROM 學生資料表 WHERE 學號 = '"+_studno+"'";
 //BA.debugLineNum = 118;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 119;BA.debugLine="ToastMessageShow(\"刪除一筆「學生資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆「學生資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 120;BA.debugLine="QueryDBList(\"Select * FROM 學生資料表\")      '呼叫顯示目前存在的學生資料表之副程式";
_vv5("Select * FROM 學生資料表");
 };
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitstudent_click() throws Exception{
 //BA.debugLineNum = 183;BA.debugLine="Sub btnExitStudent_Click";
 //BA.debugLineNum = 184;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 185;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vv6.getObject()));
 //BA.debugLineNum = 186;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _strsql = "";
String _studno = "";
String _studname = "";
 //BA.debugLineNum = 64;BA.debugLine="Sub btnInsert_Click  '新增「學生」資料";
 //BA.debugLineNum = 65;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 66;BA.debugLine="Dim StudNo As String =edtStudNo.Text             '將「學號」欄位內容指定給變數";
_studno = mostCurrent._edtstudno.getText();
 //BA.debugLineNum = 67;BA.debugLine="Dim StudName As String = edtStudName.Text        '將「姓名」欄位內容指定給變數";
_studname = mostCurrent._edtstudname.getText();
 //BA.debugLineNum = 68;BA.debugLine="DeptNo=spnDeptNo.SelectedItem.SubString2(0,4)";
mostCurrent._vvv2 = mostCurrent._spndeptno.getSelectedItem().substring((int) (0),(int) (4));
 //BA.debugLineNum = 70;BA.debugLine="If StudNo=\"\" OR StudName=\"\" OR DeptNo=\"\" Then";
if ((_studno).equals("") || (_studname).equals("") || (mostCurrent._vvv2).equals("")) { 
 //BA.debugLineNum = 71;BA.debugLine="Msgbox(\"您尚未完整輸入「學生資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未完整輸入「學生資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 73;BA.debugLine="strSQL = \"INSERT INTO 學生資料表(學號,姓名,系碼)\" & _              \"VALUES('\" & StudNo & \"','\" & StudName & \"','\" & DeptNo & \"')\"";
_strsql = "INSERT INTO 學生資料表(學號,姓名,系碼)"+"VALUES('"+_studno+"','"+_studname+"','"+mostCurrent._vvv2+"')";
 //BA.debugLineNum = 75;BA.debugLine="Msgbox(strSQL,\"SQL\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_strsql,"SQL",mostCurrent.activityBA);
 //BA.debugLineNum = 76;BA.debugLine="Check_StuID_Exists(DeptNo)        '呼叫檢查「學號」是否有重複新增之副程式";
_check_stuid_exists(mostCurrent._vvv2);
 //BA.debugLineNum = 77;BA.debugLine="If Check_StuID_Exist=True Then    '檢查是否有重複新增";
if (_check_stuid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 78;BA.debugLine="Msgbox(\"您已經重複新增「學號」了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增「學號」了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 80;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 81;BA.debugLine="ToastMessageShow(\"您新增成功「學生資料」記錄!\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您新增成功「學生資料」記錄!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 83;BA.debugLine="Check_StuID_Exist=False                                   '設定沒有重複新增「學號」";
_check_stuid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 85;BA.debugLine="QueryDBList(\"Select * FROM 學生資料表\")      '呼叫顯示目前存在的學生資料表之副程式";
_vv5("Select * FROM 學生資料表");
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _btnlistdata_click() throws Exception{
 //BA.debugLineNum = 180;BA.debugLine="Sub btnListData_Click";
 //BA.debugLineNum = 181;BA.debugLine="QueryDBList(\"Select * FROM 學生資料表\")      '呼叫顯示目前存在的學生資料表之副程式";
_vv5("Select * FROM 學生資料表");
 //BA.debugLineNum = 182;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 124;BA.debugLine="Sub btnSelect_Click   '查詢「學生」資料";
 //BA.debugLineNum = 125;BA.debugLine="SpinQueryStudNo.Visible =True    '查詢「學號」的下拉式元件顯示";
mostCurrent._spinquerystudno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 126;BA.debugLine="edtStudNo.Visible =False         '「學號」欄位元件隱藏";
mostCurrent._edtstudno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="SpinQueryStudNo.Clear";
mostCurrent._spinquerystudno.Clear();
 //BA.debugLineNum = 128;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 學號 FROM 學生資料表\")";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery("SELECT 學號 FROM 學生資料表")));
 //BA.debugLineNum = 129;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step104 = 1;
final int limit104 = (int) (_v6.getRowCount()-1);
for (_i = (int) (0); (step104 > 0 && _i <= limit104) || (step104 < 0 && _i >= limit104); _i = ((int)(0 + _i + step104))) {
 //BA.debugLineNum = 130;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_v6.setPosition(_i);
 //BA.debugLineNum = 131;BA.debugLine="SpinQueryStudNo.Add(CursorDBPoint.GetString(\"學號\"))";
mostCurrent._spinquerystudno.Add(_v6.GetString("學號"));
 }
};
 //BA.debugLineNum = 133;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _studno = "";
String _strsql = "";
 //BA.debugLineNum = 97;BA.debugLine="Sub btnUpdate_Click   '修改「學生」資料";
 //BA.debugLineNum = 98;BA.debugLine="If edtStudNo.Text=\"\" Then";
if ((mostCurrent._edtstudno.getText()).equals("")) { 
 //BA.debugLineNum = 99;BA.debugLine="Msgbox(\"您尚未輸入「學生資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「學生資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 101;BA.debugLine="Dim StudNo As String =edtStudNo.Text";
_studno = mostCurrent._edtstudno.getText();
 //BA.debugLineNum = 102;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 103;BA.debugLine="strSQL = \"UPDATE 學生資料表 SET 姓名 ='\" & edtStudName.Text & \"'\" & _             \",系碼 ='\" & spnDeptNo.SelectedItem.SubString2(0,4) & \"'  WHERE 學號 = '\" & StudNo & \"'\"";
_strsql = "UPDATE 學生資料表 SET 姓名 ='"+mostCurrent._edtstudname.getText()+"'"+",系碼 ='"+mostCurrent._spndeptno.getSelectedItem().substring((int) (0),(int) (4))+"'  WHERE 學號 = '"+_studno+"'";
 //BA.debugLineNum = 105;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 106;BA.debugLine="ToastMessageShow(\"更新一筆「學生資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆「學生資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 107;BA.debugLine="QueryDBList(\"Select * FROM 學生資料表\")      '呼叫顯示目前存在的學生資料表之副程式";
_vv5("Select * FROM 學生資料表");
 };
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_deptdb(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 49;BA.debugLine="Sub CallShow_DeptDB(strSQL As String)            '顯示「科系代碼」之副程式";
 //BA.debugLineNum = 50;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_strsql)));
 //BA.debugLineNum = 51;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step37 = 1;
final int limit37 = (int) (_v6.getRowCount()-1);
for (_i = (int) (0); (step37 > 0 && _i <= limit37) || (step37 < 0 && _i >= limit37); _i = ((int)(0 + _i + step37))) {
 //BA.debugLineNum = 52;BA.debugLine="CursorDBPoint.Position = i               '設定開始游標指定第一筆記錄";
_v6.setPosition(_i);
 //BA.debugLineNum = 53;BA.debugLine="spnDeptNo.Add(CursorDBPoint.GetString(\"系碼\") & \"/\" & CursorDBPoint.GetString(\"系名\"))";
mostCurrent._spndeptno.Add(_v6.GetString("系碼")+"/"+_v6.GetString("系名"));
 }
};
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_studdata(String _strsql) throws Exception{
 //BA.debugLineNum = 144;BA.debugLine="Sub CallShow_StudData(strSQL As String)  '顯示「學生資料表」之副程式";
 //BA.debugLineNum = 145;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_strsql)));
 //BA.debugLineNum = 146;BA.debugLine="CursorDBPoint.Position = 0";
_v6.setPosition((int) (0));
 //BA.debugLineNum = 147;BA.debugLine="edtStudNo.Text = CursorDBPoint.GetString(\"學號\")";
mostCurrent._edtstudno.setText((Object)(_v6.GetString("學號")));
 //BA.debugLineNum = 148;BA.debugLine="edtStudName.Text=CursorDBPoint.GetString(\"姓名\")";
mostCurrent._edtstudname.setText((Object)(_v6.GetString("姓名")));
 //BA.debugLineNum = 149;BA.debugLine="spnDeptNo.Clear";
mostCurrent._spndeptno.Clear();
 //BA.debugLineNum = 150;BA.debugLine="spnDeptNo.Add(CursorDBPoint.GetString(\"系碼\") & \"/\" & CursorDBPoint.GetString(\"系名\"))";
mostCurrent._spndeptno.Add(_v6.GetString("系碼")+"/"+_v6.GetString("系名"));
 //BA.debugLineNum = 151;BA.debugLine="CallShow_DeptDB(\"SELECT * FROM 科系代碼表\")  '呼叫顯示「科系代碼」之副程式";
_callshow_deptdb("SELECT * FROM 科系代碼表");
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _check_stuid_exists(String _strdeptno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 88;BA.debugLine="Sub Check_StuID_Exists(strDeptNo As String)       '檢查「學號」是否有重複新增之副程式";
 //BA.debugLineNum = 89;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 90;BA.debugLine="SQL_Query=\"Select * FROM 學生資料表 Where 學號='\" & strDeptNo & \"' \"";
_sql_query = "Select * FROM 學生資料表 Where 學號='"+_strdeptno+"' ";
 //BA.debugLineNum = 91;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_sql_query)));
 //BA.debugLineNum = 92;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「學生資料表」是否已經存在一筆了";
if (_v6.getRowCount()>0) { 
 //BA.debugLineNum = 93;BA.debugLine="Check_StuID_Exist=True                    '代表重複新增「學號」了!";
_check_stuid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim edtStudNo As EditText           '學號";
mostCurrent._edtstudno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim edtStudName As EditText         '姓名";
mostCurrent._edtstudname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim spnDeptNo As Spinner            '系碼";
mostCurrent._spndeptno = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSelect As Button             '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim SpinQueryStudNo As Spinner      '查詢「學號」";
mostCurrent._spinquerystudno = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim livStudent As ListView          '顯示「學生資料表」清單元件";
mostCurrent._livstudent = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnListData As Button           '清單功能";
mostCurrent._btnlistdata = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnExitStudent As Button        '離開功能";
mostCurrent._btnexitstudent = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim DeptNo As String                '系碼(從spnDeptNo元件取得)";
mostCurrent._vvv2 = "";
 //BA.debugLineNum = 31;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._vv0 = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._vvv1 = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_v5 = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_v6 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_StuID_Exist As Boolean=False    '檢查新增「學號」時是否重複";
_check_stuid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _vv5(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 154;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 155;BA.debugLine="livStudent.Clear '清單清空";
mostCurrent._livstudent.Clear();
 //BA.debugLineNum = 156;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 157;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._vvv1 = "";
 //BA.debugLineNum = 158;BA.debugLine="strLine =\"\"";
mostCurrent._vv0 = "";
 //BA.debugLineNum = 159;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 160;BA.debugLine="livStudent.SingleLineLayout.Label.TextSize=16";
mostCurrent._livstudent.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 161;BA.debugLine="livStudent.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livstudent.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 162;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step134 = 1;
final int limit134 = (int) (_v6.getColumnCount()-1);
for (_i = (int) (0); (step134 > 0 && _i <= limit134) || (step134 < 0 && _i >= limit134); _i = ((int)(0 + _i + step134))) {
 //BA.debugLineNum = 163;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"          \"";
mostCurrent._vv7 = mostCurrent._vv7+_v6.GetColumnName(_i)+"          ";
 //BA.debugLineNum = 164;BA.debugLine="strLine=strLine & \"=======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._vv0 = mostCurrent._vv0+"=======";
 }
};
 //BA.debugLineNum = 166;BA.debugLine="livStudent.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」";
mostCurrent._livstudent.AddSingleLine(mostCurrent._vv7);
 //BA.debugLineNum = 167;BA.debugLine="livStudent.AddSingleLine (strLine)          '顯示「分隔水平線」";
mostCurrent._livstudent.AddSingleLine(mostCurrent._vv0);
 //BA.debugLineNum = 168;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step140 = 1;
final int limit140 = (int) (_v6.getRowCount()-1);
for (_i = (int) (0); (step140 > 0 && _i <= limit140) || (step140 < 0 && _i >= limit140); _i = ((int)(0 + _i + step140))) {
 //BA.debugLineNum = 169;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_v6.setPosition(_i);
 //BA.debugLineNum = 170;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step142 = 1;
final int limit142 = (int) (_v6.getColumnCount()-1);
for (_j = (int) (0); (step142 > 0 && _j <= limit142) || (step142 < 0 && _j >= limit142); _j = ((int)(0 + _j + step142))) {
 //BA.debugLineNum = 171;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._vvv1 = mostCurrent._vvv1+_v6.GetString(_v6.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 173;BA.debugLine="livStudent.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livstudent.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._vvv1);
 //BA.debugLineNum = 174;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._vvv1 = "";
 }
};
 //BA.debugLineNum = 176;BA.debugLine="ToastMessageShow(\"目前的學生記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前的學生記錄共有: "+BA.NumberToString(_v6.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 177;BA.debugLine="lblResult.Text =\"===全校學校學生清單共有( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===全校學校學生清單共有( "+BA.NumberToString(_v6.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerystudno_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querydeptno = "";
 //BA.debugLineNum = 135;BA.debugLine="Sub SpinQueryStudNo_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 136;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 137;BA.debugLine="Dim QueryDeptNo As String =SpinQueryStudNo.SelectedItem";
_querydeptno = mostCurrent._spinquerystudno.getSelectedItem();
 //BA.debugLineNum = 138;BA.debugLine="SQL_Query=\"Select * FROM 學生資料表 As A,科系代碼表 As B Where A.系碼=B.系碼 And 學號 = '\" & QueryDeptNo & \"'\"";
_sql_query = "Select * FROM 學生資料表 As A,科系代碼表 As B Where A.系碼=B.系碼 And 學號 = '"+_querydeptno+"'";
 //BA.debugLineNum = 139;BA.debugLine="CallShow_StudData(SQL_Query)";
_callshow_studdata(_sql_query);
 //BA.debugLineNum = 140;BA.debugLine="SpinQueryStudNo.Visible =False    '查詢「學號」的下拉式元件隱藏";
mostCurrent._spinquerystudno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 141;BA.debugLine="edtStudNo.Visible =True           '「學號」欄位元件顯示";
mostCurrent._edtstudno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
}
