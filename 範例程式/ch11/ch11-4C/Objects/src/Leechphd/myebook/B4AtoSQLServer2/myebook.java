package Leechphd.myebook.B4AtoSQLServer2;

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

public class myebook extends Activity implements B4AActivity{
	public static myebook mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.myebook");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (myebook).");
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
		activityBA = new BA(this, layout, processBA, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.myebook");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leechphd.myebook.B4AtoSQLServer2.myebook", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (myebook) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (myebook) Resume **");
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
		return myebook.class;
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
        BA.LogInfo("** Activity (myebook) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (myebook) Resume **");
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
public static String _sql_query = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public static String _strsql = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livebook = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblshowebook = null;
public anywheresoftware.b4a.objects.collections.JSONParser _json = null;
public static String _response = "";
public Leechphd.myebook.B4AtoSQLServer2.main _main = null;
public Leechphd.myebook.B4AtoSQLServer2.httputils _httputils = null;
public Leechphd.myebook.B4AtoSQLServer2.httputilsservice _httputilsservice = null;
public Leechphd.myebook.B4AtoSQLServer2.bookstore _bookstore = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 25;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 26;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeBookDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeBookDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 28;BA.debugLine="Activity.LoadLayout(\"Myebook\")          '使用「Main(版面配置檔)」來輸出";
mostCurrent._activity.LoadLayout("Myebook",mostCurrent.activityBA);
 //BA.debugLineNum = 29;BA.debugLine="Activity.Title =\"B4A連結MySQL(檢查是否為會員)\"    '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("B4A連結MySQL(檢查是否為會員)"));
 //BA.debugLineNum = 30;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 31;BA.debugLine="HttpUtils.CallbackActivity = \"Myebook\"";
mostCurrent._httputils._callbackactivity = "Myebook";
 //BA.debugLineNum = 32;BA.debugLine="HttpUtils.CallbackJobDoneSub = \"JobDone\"";
mostCurrent._httputils._callbackjobdonesub = "JobDone";
 };
 //BA.debugLineNum = 34;BA.debugLine="StrSQL=\"select * from Member Where Password='\" & Main.Password & \"'\"";
mostCurrent._strsql = "select * from Member Where Password='"+mostCurrent._main._password+"'";
 //BA.debugLineNum = 38;BA.debugLine="HttpUtils.PostString(\"LeechJob\", Main.ServerUrl, StrSQL)";
mostCurrent._httputils._poststring(mostCurrent.activityBA,"LeechJob",mostCurrent._main._serverurl,mostCurrent._strsql);
 //BA.debugLineNum = 40;BA.debugLine="lblShowebook.Visible=False";
mostCurrent._lblshowebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 41;BA.debugLine="SQL_Query=\"Select * FROM 書籍資料表\"";
_sql_query = "Select * FROM 書籍資料表";
 //BA.debugLineNum = 42;BA.debugLine="lblResult.Text=\"會員身份審查中...\"";
mostCurrent._lblresult.setText((Object)("會員身份審查中..."));
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 46;BA.debugLine="If HttpUtils.Complete = True Then";
if (mostCurrent._httputils._complete==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 47;BA.debugLine="JobDone(HttpUtils.Job)";
_jobdone(mostCurrent._httputils._job);
 };
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 106;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 107;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _check_member() throws Exception{
String _username = "";
String _output = "";
anywheresoftware.b4a.objects.collections.List _rows = null;
anywheresoftware.b4a.objects.collections.Map _user = null;
 //BA.debugLineNum = 64;BA.debugLine="Sub Check_Member()";
 //BA.debugLineNum = 65;BA.debugLine="Dim Username As String";
_username = "";
 //BA.debugLineNum = 66;BA.debugLine="Dim Output As String";
_output = "";
 //BA.debugLineNum = 67;BA.debugLine="Dim rows As List";
_rows = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 68;BA.debugLine="rows = JSON.NextArray";
_rows = mostCurrent._json.NextArray();
 //BA.debugLineNum = 69;BA.debugLine="If rows.Size= 1 Then";
if (_rows.getSize()==1) { 
 //BA.debugLineNum = 70;BA.debugLine="Dim user As Map";
_user = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 71;BA.debugLine="user = rows.Get(0)";
_user.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_rows.Get((int) (0))));
 //BA.debugLineNum = 72;BA.debugLine="Username=user.Get(\"UserName\")  '從遠端SQL Server資料庫取得使用者名稱";
_username = BA.ObjectToString(_user.Get((Object)("UserName")));
 //BA.debugLineNum = 73;BA.debugLine="lblResult.Text=\"==會員身份審查通過==\"";
mostCurrent._lblresult.setText((Object)("==會員身份審查通過=="));
 //BA.debugLineNum = 74;BA.debugLine="Output=Username.Trim & \" 會員您好：\" & CRLF & \"您是正式會員\"";
_output = _username.trim()+" 會員您好："+anywheresoftware.b4a.keywords.Common.CRLF+"您是正式會員";
 //BA.debugLineNum = 75;BA.debugLine="Msgbox(Output,\"檢查結果\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_output,"檢查結果",mostCurrent.activityBA);
 //BA.debugLineNum = 76;BA.debugLine="lblResult.Text=\"==「\" & Username.Trim & \"」會員本期電子書如下==\"";
mostCurrent._lblresult.setText((Object)("==「"+_username.trim()+"」會員本期電子書如下=="));
 //BA.debugLineNum = 77;BA.debugLine="QueryBookTable(SQL_Query)   '顯示書籍";
_querybooktable(_sql_query);
 }else {
 //BA.debugLineNum = 79;BA.debugLine="lblResult.Text=\"==會員身份審查尚未通過==\"";
mostCurrent._lblresult.setText((Object)("==會員身份審查尚未通過=="));
 //BA.debugLineNum = 80;BA.debugLine="Msgbox(\"抱歉!沒有你的會員資料\",\"檢查結果\")";
anywheresoftware.b4a.keywords.Common.Msgbox("抱歉!沒有你的會員資料","檢查結果",mostCurrent.activityBA);
 //BA.debugLineNum = 81;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 82;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 };
 //BA.debugLineNum = 84;BA.debugLine="HttpUtils.Complete = False";
mostCurrent._httputils._complete = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim StrSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 17;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim livebook As ListView";
mostCurrent._livebook = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim lblShowebook As Label";
mostCurrent._lblshowebook = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim JSON As JSONParser";
mostCurrent._json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 21;BA.debugLine="Dim response As String";
mostCurrent._response = "";
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(String _job) throws Exception{
 //BA.debugLineNum = 55;BA.debugLine="Sub JobDone (Job As String)";
 //BA.debugLineNum = 56;BA.debugLine="If HttpUtils.IsSuccess(Main.ServerUrl) Then";
if (mostCurrent._httputils._issuccess(mostCurrent.activityBA,mostCurrent._main._serverurl)) { 
 //BA.debugLineNum = 57;BA.debugLine="response = HttpUtils.GetString(Main.ServerUrl)  '取得Web Service的回覆";
mostCurrent._response = mostCurrent._httputils._getstring(mostCurrent.activityBA,mostCurrent._main._serverurl);
 //BA.debugLineNum = 58;BA.debugLine="JSON.Initialize(response)                     '用來剖析「Web Service的回覆」資料格式內容";
mostCurrent._json.Initialize(mostCurrent._response);
 };
 //BA.debugLineNum = 60;BA.debugLine="HttpUtils.Complete = False";
mostCurrent._httputils._complete = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 61;BA.debugLine="Check_Member  '呼叫檢查使用者身份";
_check_member();
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _livebook_itemclick(int _position,Object _value) throws Exception{
String _temp = "";
 //BA.debugLineNum = 97;BA.debugLine="Sub livebook_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 98;BA.debugLine="Dim Temp As String";
_temp = "";
 //BA.debugLineNum = 99;BA.debugLine="Temp = \"你剛才點選的電子書為：\" & CRLF";
_temp = "你剛才點選的電子書為："+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 100;BA.debugLine="Temp = Temp & livebook.GetItem(Position)";
_temp = _temp+BA.ObjectToString(mostCurrent._livebook.GetItem(_position));
 //BA.debugLineNum = 101;BA.debugLine="lblShowebook.Visible=True";
mostCurrent._lblshowebook.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 102;BA.debugLine="lblShowebook.Text =Temp";
mostCurrent._lblshowebook.setText((Object)(_temp));
 //BA.debugLineNum = 103;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 10;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _querybooktable(String _strsqlite) throws Exception{
int _i = 0;
 //BA.debugLineNum = 87;BA.debugLine="Sub QueryBookTable(StrSQLite As String)";
 //BA.debugLineNum = 88;BA.debugLine="livebook.Clear()";
mostCurrent._livebook.Clear();
 //BA.debugLineNum = 89;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(StrSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 90;BA.debugLine="livebook.SingleLineLayout.Label.TextSize = 16";
mostCurrent._livebook.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 91;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step71 = 1;
final int limit71 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step71 > 0 && _i <= limit71) || (step71 < 0 && _i >= limit71); _i = ((int)(0 + _i + step71))) {
 //BA.debugLineNum = 92;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 93;BA.debugLine="livebook.AddSingleLine((i+1) & \".\" & CursorDBPoint.GetString(\"書號\") & \"(\" & CursorDBPoint.GetString(\"書名\") & \")\" )";
mostCurrent._livebook.AddSingleLine(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書號")+"("+_cursordbpoint.GetString("書名")+")");
 }
};
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
}
