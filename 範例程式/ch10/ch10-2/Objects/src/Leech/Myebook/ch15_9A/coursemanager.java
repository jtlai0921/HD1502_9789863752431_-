package Leech.Myebook.ch15_9A;

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

public class coursemanager extends Activity implements B4AActivity{
	public static coursemanager mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.Myebook.ch15_9A", "Leech.Myebook.ch15_9A.coursemanager");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (coursemanager).");
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
		activityBA = new BA(this, layout, processBA, "Leech.Myebook.ch15_9A", "Leech.Myebook.ch15_9A.coursemanager");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.Myebook.ch15_9A.coursemanager", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (coursemanager) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (coursemanager) Resume **");
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
		return coursemanager.class;
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
        BA.LogInfo("** Activity (coursemanager) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (coursemanager) Resume **");
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
public anywheresoftware.b4a.objects.EditTextWrapper _edtcourno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtcourname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtcourcredits = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerycourno = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livcourse = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlistdata = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitcourse = null;
public static String _strsqlviewtitle = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public Leech.Myebook.ch15_9A.main _main = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 38;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, _ 		\"MyeSchoolDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 41;BA.debugLine="Activity.LoadLayout(\"Course\")";
mostCurrent._activity.LoadLayout("Course",mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="Activity.Title =\"【課程資料管理系統】\"";
mostCurrent._activity.setTitle((Object)("【課程資料管理系統】"));
 //BA.debugLineNum = 43;BA.debugLine="SpinQueryCourNo.Visible=False  '查詢「課程代號」的下拉式元件隱藏";
mostCurrent._spinquerycourno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 44;BA.debugLine="edtCourNo.Text=\"C006\"";
mostCurrent._edtcourno.setText((Object)("C006"));
 //BA.debugLineNum = 45;BA.debugLine="edtCourName.Text=\"手機程式設計\"";
mostCurrent._edtcourname.setText((Object)("手機程式設計"));
 //BA.debugLineNum = 46;BA.debugLine="edtCourCredits.Text=\"3\"";
mostCurrent._edtcourcredits.setText((Object)("3"));
 //BA.debugLineNum = 47;BA.debugLine="QueryDBList(\"Select * FROM 課程資料表\")      '呼叫顯示「課程資料」全部記錄(多筆清單)";
_querydblist("Select * FROM 課程資料表");
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _strsql = "";
String _courno = "";
 //BA.debugLineNum = 81;BA.debugLine="Sub btnDelete_Click   '刪除「課程資料」";
 //BA.debugLineNum = 82;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 83;BA.debugLine="Dim CourNo As String =edtCourNo.Text";
_courno = mostCurrent._edtcourno.getText();
 //BA.debugLineNum = 84;BA.debugLine="strSQL = \"DELETE FROM 課程資料表 WHERE 課號 = '\" & CourNo & \"'\"";
_strsql = "DELETE FROM 課程資料表 WHERE 課號 = '"+_courno+"'";
 //BA.debugLineNum = 85;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 86;BA.debugLine="QueryDBList(\"Select * FROM 課程資料表\")           '呼叫顯示「課程資料」全部記錄(多筆清單)";
_querydblist("Select * FROM 課程資料表");
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitcourse_click() throws Exception{
 //BA.debugLineNum = 148;BA.debugLine="Sub btnExitCourse_Click";
 //BA.debugLineNum = 149;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 150;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _strsql = "";
String _courno = "";
String _courname = "";
String _courcredits = "";
 //BA.debugLineNum = 58;BA.debugLine="Sub btnInsert_Click  '新增「課程資料」";
 //BA.debugLineNum = 59;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 60;BA.debugLine="Dim CourNo As String =edtCourNo.Text               '將「課程代號」欄位內容指定給變數";
_courno = mostCurrent._edtcourno.getText();
 //BA.debugLineNum = 61;BA.debugLine="Dim CourName As String = edtCourName.Text          '將「課程名稱」欄位內容指定給變數";
_courname = mostCurrent._edtcourname.getText();
 //BA.debugLineNum = 62;BA.debugLine="Dim CourCredits As String = edtCourCredits.Text    '將「學分數」欄位內容指定給變數";
_courcredits = mostCurrent._edtcourcredits.getText();
 //BA.debugLineNum = 63;BA.debugLine="strSQL = \"INSERT INTO 課程資料表(課號,課名,學分數)\" & _              \"VALUES('\" & CourNo & \"','\" & CourName & \"'\" & _ 			 \",'\" & CourCredits & \"')\"";
_strsql = "INSERT INTO 課程資料表(課號,課名,學分數)"+"VALUES('"+_courno+"','"+_courname+"'"+",'"+_courcredits+"')";
 //BA.debugLineNum = 66;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                        '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 67;BA.debugLine="QueryDBList(\"Select * FROM 課程資料表\")            '呼叫顯示「課程資料」全部記錄(多筆清單)";
_querydblist("Select * FROM 課程資料表");
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _btnlistdata_click() throws Exception{
 //BA.debugLineNum = 145;BA.debugLine="Sub btnListData_Click";
 //BA.debugLineNum = 146;BA.debugLine="QueryDBList(\"Select * FROM 課程資料表\")";
_querydblist("Select * FROM 課程資料表");
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 89;BA.debugLine="Sub btnSelect_Click   '查詢「課程資料」";
 //BA.debugLineNum = 90;BA.debugLine="SpinQueryCourNo.Visible =True    '查詢「課程代號」的下拉式元件顯示";
mostCurrent._spinquerycourno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 91;BA.debugLine="edtCourNo.Visible =False         '「課程代號」欄位元件隱藏";
mostCurrent._edtcourno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 92;BA.debugLine="SpinQueryCourNo.Clear            '查詢「課程代號」的下拉式元件來清空";
mostCurrent._spinquerycourno.Clear();
 //BA.debugLineNum = 93;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 課號 FROM 課程資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 課號 FROM 課程資料表")));
 //BA.debugLineNum = 94;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step65 = 1;
final int limit65 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step65 > 0 && _i <= limit65) || (step65 < 0 && _i >= limit65); _i = ((int)(0 + _i + step65))) {
 //BA.debugLineNum = 95;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 96;BA.debugLine="SpinQueryCourNo.Add(CursorDBPoint.GetString(\"課號\"))";
mostCurrent._spinquerycourno.Add(_cursordbpoint.GetString("課號"));
 }
};
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _strsql = "";
String _courno = "";
 //BA.debugLineNum = 70;BA.debugLine="Sub btnUpdate_Click   '修改「課程資料」";
 //BA.debugLineNum = 71;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 72;BA.debugLine="Dim CourNo As String =edtCourNo.Text";
_courno = mostCurrent._edtcourno.getText();
 //BA.debugLineNum = 73;BA.debugLine="strSQL = \"UPDATE 課程資料表\" & _             \" SET 課名 ='\" & edtCourName.Text & \"'\" & _             \",學分數 ='\" & edtCourCredits.Text & \"'\" & _ 			\" WHERE 課號 = '\" & CourNo & \"'\"";
_strsql = "UPDATE 課程資料表"+" SET 課名 ='"+mostCurrent._edtcourname.getText()+"'"+",學分數 ='"+mostCurrent._edtcourcredits.getText()+"'"+" WHERE 課號 = '"+_courno+"'";
 //BA.debugLineNum = 77;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 78;BA.debugLine="QueryDBList(\"Select * FROM 課程資料表\")            '呼叫顯示「課程資料」全部記錄(多筆清單)";
_querydblist("Select * FROM 課程資料表");
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_coursedata(String _strsql) throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub CallShow_CourseData(strSQL As String)  '顯示「課程資料表」之副程式";
 //BA.debugLineNum = 111;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 112;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 113;BA.debugLine="edtCourNo.Text = CursorDBPoint.GetString(\"課號\")";
mostCurrent._edtcourno.setText((Object)(_cursordbpoint.GetString("課號")));
 //BA.debugLineNum = 114;BA.debugLine="edtCourName.Text=CursorDBPoint.GetString(\"課名\")";
mostCurrent._edtcourname.setText((Object)(_cursordbpoint.GetString("課名")));
 //BA.debugLineNum = 115;BA.debugLine="edtCourCredits.Text=CursorDBPoint.GetString(\"學分數\")";
mostCurrent._edtcourcredits.setText((Object)(_cursordbpoint.GetString("學分數")));
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim edtCourNo As EditText           '課程代號";
mostCurrent._edtcourno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim edtCourName As EditText         '課程名稱";
mostCurrent._edtcourname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim edtCourCredits As EditText      '學分數";
mostCurrent._edtcourcredits = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnSelect As Button             '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim SpinQueryCourNo As Spinner      '查詢「課程代號」";
mostCurrent._spinquerycourno = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim livCourse As ListView           '顯示「課程資料表」清單元件";
mostCurrent._livcourse = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnListData As Button           '清單功能";
mostCurrent._btnlistdata = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnExitCourse As Button         '離開功能";
mostCurrent._btnexitcourse = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 31;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 118;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 119;BA.debugLine="livCourse.Clear '清單清空";
mostCurrent._livcourse.Clear();
 //BA.debugLineNum = 120;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 121;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 122;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 123;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 124;BA.debugLine="livCourse.SingleLineLayout.Label.TextSize=14";
mostCurrent._livcourse.getSingleLineLayout().Label.setTextSize((float) (14));
 //BA.debugLineNum = 125;BA.debugLine="livCourse.SingleLineLayout.ItemHeight = 16dip";
mostCurrent._livcourse.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (16)));
 //BA.debugLineNum = 126;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step93 = 1;
final int limit93 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step93 > 0 && _i <= limit93) || (step93 < 0 && _i >= limit93); _i = ((int)(0 + _i + step93))) {
 //BA.debugLineNum = 127;BA.debugLine="strSqlViewTitle=strSqlViewTitle & _ 		CursorDBPoint.GetColumnName(i) & \"      \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"      ";
 //BA.debugLineNum = 129;BA.debugLine="strLine=strLine & \"======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"======";
 }
};
 //BA.debugLineNum = 131;BA.debugLine="livCourse.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」";
mostCurrent._livcourse.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 132;BA.debugLine="livCourse.AddSingleLine (strLine)           '顯示「分隔水平線」";
mostCurrent._livcourse.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 133;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step99 = 1;
final int limit99 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step99 > 0 && _i <= limit99) || (step99 < 0 && _i >= limit99); _i = ((int)(0 + _i + step99))) {
 //BA.debugLineNum = 134;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 135;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step101 = 1;
final int limit101 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step101 > 0 && _j <= limit101) || (step101 < 0 && _j >= limit101); _j = ((int)(0 + _j + step101))) {
 //BA.debugLineNum = 136;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"  \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"  ";
 }
};
 //BA.debugLineNum = 138;BA.debugLine="livCourse.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livcourse.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent);
 //BA.debugLineNum = 139;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 141;BA.debugLine="ToastMessageShow(\"目前的課程記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前的課程記錄共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 142;BA.debugLine="lblResult.Text =\"===本學期開設課程清單( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===本學期開設課程清單( "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerycourno_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querycourno = "";
 //BA.debugLineNum = 100;BA.debugLine="Sub SpinQueryCourNo_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 101;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 102;BA.debugLine="Dim QueryCourNo As String =SpinQueryCourNo.SelectedItem";
_querycourno = mostCurrent._spinquerycourno.getSelectedItem();
 //BA.debugLineNum = 103;BA.debugLine="SQL_Query=\"Select * FROM 課程資料表\" & _ 	          \" Where 課號 = '\" & QueryCourNo & \"'\"";
_sql_query = "Select * FROM 課程資料表"+" Where 課號 = '"+_querycourno+"'";
 //BA.debugLineNum = 105;BA.debugLine="CallShow_CourseData(SQL_Query)    '呼叫顯示「課程資料表」之副程式";
_callshow_coursedata(_sql_query);
 //BA.debugLineNum = 106;BA.debugLine="SpinQueryCourNo.Visible =False    '查詢「課程代號」的下拉式元件隱藏";
mostCurrent._spinquerycourno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 107;BA.debugLine="edtCourNo.Visible =True           '「課程代號」欄位元件顯示";
mostCurrent._edtcourno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
}
