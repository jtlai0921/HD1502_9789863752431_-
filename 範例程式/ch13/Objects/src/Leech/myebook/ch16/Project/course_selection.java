package Leech.myebook.ch16.Project;

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

public class course_selection extends Activity implements B4AActivity{
	public static course_selection mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.course_selection");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (course_selection).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.course_selection");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch16.Project.course_selection", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (course_selection) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (course_selection) Resume **");
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
		return course_selection.class;
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
        BA.LogInfo("** Activity (course_selection) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (course_selection) Resume **");
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
public static anywheresoftware.b4a.sql.SQL _sqlcmd = null;
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursordbpoint = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelcourse = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitcourse_selection = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnaddcourse = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livviewaddcourse = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnclearcourse = null;
public static boolean _check_stuno_courseno_exist = false;
public static String _strviewaddcourse = "";
public static String _strsqlviewtitle = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public anywheresoftware.b4a.objects.SpinnerWrapper _spnstudent = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public Leech.myebook.ch16.Project.main _main = null;
public Leech.myebook.ch16.Project.department _department = null;
public Leech.myebook.ch16.Project.course _course = null;
public Leech.myebook.ch16.Project.student _student = null;
public Leech.myebook.ch16.Project.query _query = null;
public Leech.myebook.ch16.Project.writesql _writesql = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 30;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeSchoolDB.sqlite\", True)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Course_Selection\")";
mostCurrent._activity.LoadLayout("Course_Selection",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title =\"選課作業\"";
mostCurrent._activity.setTitle((Object)("選課作業"));
 //BA.debugLineNum = 34;BA.debugLine="CallShow_StudentDB(\"SELECT * FROM 學生資料表\")  '呼叫顯示「學生資料」之副程式";
_callshow_studentdb("SELECT * FROM 學生資料表");
 //BA.debugLineNum = 35;BA.debugLine="CallShow_CourseDB(\"SELECT * FROM 課程資料表\")   '呼叫顯示「課程資料」之副程式";
_callshow_coursedb("SELECT * FROM 課程資料表");
 //BA.debugLineNum = 36;BA.debugLine="lblResult.Text =\"===您尚未選擇欲加選學生===\"";
mostCurrent._lblresult.setText((Object)("===您尚未選擇欲加選學生==="));
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 159;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _btnclearcourse_click() throws Exception{
String _msg_value = "";
String _strsql = "";
 //BA.debugLineNum = 95;BA.debugLine="Sub btnClearCourse_Click  '全退";
 //BA.debugLineNum = 96;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 97;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 98;BA.debugLine="Msg_Value = Msgbox2(\"您確定要「全退」全部課程資料嗎?\", \"確認全退對話方塊\", \"確認\", \"\", \"取消\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要「全退」全部課程資料嗎?","確認全退對話方塊","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 99;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 100;BA.debugLine="strSQL=\"DELETE FROM 選課資料表 Where 學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"'\"";
_strsql = "DELETE FROM 選課資料表 Where 學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"'";
 //BA.debugLineNum = 101;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 102;BA.debugLine="livViewAddCourse.Clear";
mostCurrent._livviewaddcourse.Clear();
 //BA.debugLineNum = 103;BA.debugLine="ToastMessageShow(\"您已退掉全部課程記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您已退掉全部課程記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 104;BA.debugLine="QueryDBList(strViewAddCourse)       '顯示您已加選的課程清單";
_querydblist(mostCurrent._strviewaddcourse);
 };
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _btndelcourse_click() throws Exception{
 //BA.debugLineNum = 91;BA.debugLine="Sub btnDelCourse_Click    '退選";
 //BA.debugLineNum = 92;BA.debugLine="Msgbox(\"請點選清單中的課程!!!\",\"退選指引\")";
anywheresoftware.b4a.keywords.Common.Msgbox("請點選清單中的課程!!!","退選指引",mostCurrent.activityBA);
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitcourse_selection_click() throws Exception{
 //BA.debugLineNum = 148;BA.debugLine="Sub btnExitCourse_Selection_Click";
 //BA.debugLineNum = 149;BA.debugLine="Activity.Finish()";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 150;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_coursedb(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 53;BA.debugLine="Sub CallShow_CourseDB(strSQL As String)  '顯示「課程資料」之副程式";
 //BA.debugLineNum = 54;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 55;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step41 = 1;
final int limit41 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step41 > 0 && _i <= limit41) || (step41 < 0 && _i >= limit41); _i = ((int)(0 + _i + step41))) {
 //BA.debugLineNum = 56;BA.debugLine="CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 57;BA.debugLine="spnAddCourse.Add(CursorDBPoint.GetString(\"課號\") & \"/\" & CursorDBPoint.GetString(\"課名\")  & \"(\" & CursorDBPoint.GetString(\"學分數\") & \")\" )";
mostCurrent._spnaddcourse.Add(_cursordbpoint.GetString("課號")+"/"+_cursordbpoint.GetString("課名")+"("+_cursordbpoint.GetString("學分數")+")");
 }
};
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_studentdb(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 39;BA.debugLine="Sub CallShow_StudentDB(strSQL As String)  '顯示「學生資料」之副程式";
 //BA.debugLineNum = 40;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 41;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step30 = 1;
final int limit30 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step30 > 0 && _i <= limit30) || (step30 < 0 && _i >= limit30); _i = ((int)(0 + _i + step30))) {
 //BA.debugLineNum = 42;BA.debugLine="CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 43;BA.debugLine="spnStudent.Add(CursorDBPoint.GetString(\"學號\") & \"/\" & CursorDBPoint.GetString(\"姓名\"))";
mostCurrent._spnstudent.Add(_cursordbpoint.GetString("學號")+"/"+_cursordbpoint.GetString("姓名"));
 }
};
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _check_stuid_exists(String _strstudno,String _strcourno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 82;BA.debugLine="Sub Check_StuID_Exists(strStudNo As String,strCourNo As String)    '檢查「重複選課」之副程式";
 //BA.debugLineNum = 83;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 84;BA.debugLine="SQL_Query=\"Select * FROM 選課資料表 Where 學號='\" & strStudNo & \"' And 課號='\" & strCourNo & \"' \"";
_sql_query = "Select * FROM 選課資料表 Where 學號='"+_strstudno+"' And 課號='"+_strcourno+"' ";
 //BA.debugLineNum = 85;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 86;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「學生資料表」是否已經存在一筆了";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 87;BA.debugLine="Check_StuNo_CourseNo_Exist=True           '代表重複新增「學號」了!";
_check_stuno_courseno_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim btnDelCourse As Button";
mostCurrent._btndelcourse = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim btnExitCourse_Selection As Button";
mostCurrent._btnexitcourse_selection = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim spnAddCourse As Spinner";
mostCurrent._spnaddcourse = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim livViewAddCourse As ListView";
mostCurrent._livviewaddcourse = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim btnClearCourse As Button";
mostCurrent._btnclearcourse = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim Check_StuNo_CourseNo_Exist As Boolean=False  '檢查是否有「重複選課」(預設為否)";
_check_stuno_courseno_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 19;BA.debugLine="Dim strViewAddCourse As String";
mostCurrent._strviewaddcourse = "";
 //BA.debugLineNum = 21;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 22;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 23;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 24;BA.debugLine="Dim spnStudent As Spinner";
mostCurrent._spnstudent = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _livviewaddcourse_itemclick(int _position,Object _value) throws Exception{
String _msg_value = "";
String _strsql = "";
 //BA.debugLineNum = 108;BA.debugLine="Sub livViewAddCourse_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 109;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 110;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 111;BA.debugLine="Msg_Value = Msgbox2(\"您確定要「退選」此課程資料嗎?\", \"確認退選對話方塊\", \"確認\", \"\", \"取消\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要「退選」此課程資料嗎?","確認退選對話方塊","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 112;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 113;BA.debugLine="strSQL=\"DELETE FROM 選課資料表 Where 學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"' And 課號='\" & livViewAddCourse.GetItem(Position) & \"' \"";
_strsql = "DELETE FROM 選課資料表 Where 學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"' And 課號='"+BA.ObjectToString(mostCurrent._livviewaddcourse.GetItem(_position))+"' ";
 //BA.debugLineNum = 114;BA.debugLine="Msgbox(strSQL,\"strSQL\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_strsql,"strSQL",mostCurrent.activityBA);
 //BA.debugLineNum = 115;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 116;BA.debugLine="livViewAddCourse.RemoveAt(Position)";
mostCurrent._livviewaddcourse.RemoveAt(_position);
 //BA.debugLineNum = 117;BA.debugLine="ToastMessageShow(\"刪除一筆「學生資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆「學生資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 118;BA.debugLine="QueryDBList(strViewAddCourse)      '顯示您已加選的課程清單";
_querydblist(mostCurrent._strviewaddcourse);
 };
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 9;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 122;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 123;BA.debugLine="livViewAddCourse.Clear '清單清空";
mostCurrent._livviewaddcourse.Clear();
 //BA.debugLineNum = 124;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 125;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 126;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 127;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 128;BA.debugLine="livViewAddCourse.SingleLineLayout.Label.TextSize=16";
mostCurrent._livviewaddcourse.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 129;BA.debugLine="livViewAddCourse.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livviewaddcourse.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 130;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step109 = 1;
final int limit109 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step109 > 0 && _i <= limit109) || (step109 < 0 && _i >= limit109); _i = ((int)(0 + _i + step109))) {
 //BA.debugLineNum = 131;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"         \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"         ";
 //BA.debugLineNum = 132;BA.debugLine="strLine=strLine & \"=======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"=======";
 }
};
 //BA.debugLineNum = 134;BA.debugLine="livViewAddCourse.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」";
mostCurrent._livviewaddcourse.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 135;BA.debugLine="livViewAddCourse.AddSingleLine (strLine)          '顯示「分隔水平線」";
mostCurrent._livviewaddcourse.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 136;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step115 = 1;
final int limit115 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step115 > 0 && _i <= limit115) || (step115 < 0 && _i >= limit115); _i = ((int)(0 + _i + step115))) {
 //BA.debugLineNum = 137;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 138;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step117 = 1;
final int limit117 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step117 > 0 && _j <= limit117) || (step117 < 0 && _j >= limit117); _j = ((int)(0 + _j + step117))) {
 //BA.debugLineNum = 139;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 141;BA.debugLine="livViewAddCourse.AddSingleLine2((i+1) & \". \" & strSqlViewContent,CursorDBPoint.GetString(CursorDBPoint.GetColumnName(2)))";
mostCurrent._livviewaddcourse.AddSingleLine2(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent,(Object)(_cursordbpoint.GetString(_cursordbpoint.GetColumnName((int) (2)))));
 //BA.debugLineNum = 142;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 144;BA.debugLine="ToastMessageShow(\"目前您的加選科目，共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前您的加選科目，共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 145;BA.debugLine="lblResult.Text =\"===您已加選課程清單( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===您已加選課程清單( "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
public static String  _spnaddcourse_itemclick(int _position,Object _value) throws Exception{
String _strsql = "";
String _studno = "";
String _courno = "";
int _intrndscore = 0;
 //BA.debugLineNum = 60;BA.debugLine="Sub spnAddCourse_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 61;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 62;BA.debugLine="Dim StudNo As String               '學號";
_studno = "";
 //BA.debugLineNum = 63;BA.debugLine="Dim CourNo As String            '課號";
_courno = "";
 //BA.debugLineNum = 64;BA.debugLine="Dim intRndScore As Int             '成績";
_intrndscore = 0;
 //BA.debugLineNum = 65;BA.debugLine="StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號";
_studno = mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 66;BA.debugLine="CourNo=spnAddCourse.SelectedItem.SubString2(0,4)  '課號";
_courno = mostCurrent._spnaddcourse.getSelectedItem().substring((int) (0),(int) (4));
 //BA.debugLineNum = 67;BA.debugLine="intRndScore=Rnd(60,100)                              '成績";
_intrndscore = anywheresoftware.b4a.keywords.Common.Rnd((int) (60),(int) (100));
 //BA.debugLineNum = 68;BA.debugLine="strSQL = \"INSERT INTO 選課資料表(學號,課號,成績)\" & _              \"VALUES('\" & StudNo & \"','\" & CourNo & \"','\" & intRndScore & \"')\"";
_strsql = "INSERT INTO 選課資料表(學號,課號,成績)"+"VALUES('"+_studno+"','"+_courno+"','"+BA.NumberToString(_intrndscore)+"')";
 //BA.debugLineNum = 71;BA.debugLine="Check_StuID_Exists(StudNo,CourNo)       '呼叫檢查「重複選課」之副程式";
_check_stuid_exists(_studno,_courno);
 //BA.debugLineNum = 72;BA.debugLine="If Check_StuNo_CourseNo_Exist=True Then    '檢查是否有重複新增";
if (_check_stuno_courseno_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 73;BA.debugLine="Msgbox(\"您已經「重複選課」了！\",\"加選錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經「重複選課」了！","加選錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 75;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 76;BA.debugLine="ToastMessageShow(\"您加選成功!\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您加選成功!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 78;BA.debugLine="Check_StuNo_CourseNo_Exist=False                                   '設定沒有重複新增「學號」";
_check_stuno_courseno_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 79;BA.debugLine="QueryDBList(strViewAddCourse)  '顯示您已加選的課程清單";
_querydblist(mostCurrent._strviewaddcourse);
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _spnstudent_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub spnStudent_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 48;BA.debugLine="strViewAddCourse=\"Select A.學號,姓名,B.課號,課名,學分數 FROM 學生資料表 As A,課程資料表 As B,選課資料表 As C\" & _ 	       \" Where A.學號=C.學號 And B.課號=C.課號 And C.學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"'\"";
mostCurrent._strviewaddcourse = "Select A.學號,姓名,B.課號,課名,學分數 FROM 學生資料表 As A,課程資料表 As B,選課資料表 As C"+" Where A.學號=C.學號 And B.課號=C.課號 And C.學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"'";
 //BA.debugLineNum = 50;BA.debugLine="QueryDBList(strViewAddCourse)";
_querydblist(mostCurrent._strviewaddcourse);
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
}
