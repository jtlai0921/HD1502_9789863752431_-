package Leech.myebook.ch15.hw1;

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

public class userlogin extends Activity implements B4AActivity{
	public static userlogin mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.userlogin");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (userlogin).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.userlogin");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch15.hw1.userlogin", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (userlogin) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (userlogin) Resume **");
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
		return userlogin.class;
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
        BA.LogInfo("** Activity (userlogin) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (userlogin) Resume **");
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
public static String _sql_query = "";
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursordbpoint = null;
public static String _useraccount = "";
public static String _username = "";
public static int _scorelevel = 0;
public anywheresoftware.b4a.objects.EditTextWrapper _edtuseraccount = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlogin = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public static String _strsql = "";
public Leech.myebook.ch15.hw1.main _main = null;
public Leech.myebook.ch15.hw1.mathtest _mathtest = null;
public Leech.myebook.ch15.hw1.scorelist _scorelist = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 23;BA.debugLine="Activity.LoadLayout(\"UserLogin\")";
mostCurrent._activity.LoadLayout("UserLogin",mostCurrent.activityBA);
 //BA.debugLineNum = 24;BA.debugLine="Activity.Title=\"考生登入介面\"";
mostCurrent._activity.setTitle((Object)("考生登入介面"));
 //BA.debugLineNum = 25;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 26;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyMathScoreDB.sqlite\", True)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyMathScoreDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 28;BA.debugLine="edtUserAccount.Text=\"11111\"";
mostCurrent._edtuseraccount.setText((Object)("11111"));
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _btnlogin_click() throws Exception{
String _welcome = "";
int _i = 0;
 //BA.debugLineNum = 39;BA.debugLine="Sub btnLogin_Click";
 //BA.debugLineNum = 40;BA.debugLine="Dim Welcome As String";
_welcome = "";
 //BA.debugLineNum = 41;BA.debugLine="If edtUserAccount.Text=\"\" Then";
if ((mostCurrent._edtuseraccount.getText()).equals("")) { 
 //BA.debugLineNum = 42;BA.debugLine="Msgbox(\"您尚未輸入的帳號，請輸入！\",\"系統回覆\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入的帳號，請輸入！","系統回覆",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 44;BA.debugLine="UserAccount = edtUserAccount.Text";
_useraccount = mostCurrent._edtuseraccount.getText();
 //BA.debugLineNum = 45;BA.debugLine="SQL_Query=\"Select * FROM 學生資料表 Where 帳號='\" & UserAccount & \"'\"";
_sql_query = "Select * FROM 學生資料表 Where 帳號='"+_useraccount+"'";
 //BA.debugLineNum = 46;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 47;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step34 = 1;
final int limit34 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step34 > 0 && _i <= limit34) || (step34 < 0 && _i >= limit34); _i = ((int)(0 + _i + step34))) {
 //BA.debugLineNum = 48;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 }
};
 //BA.debugLineNum = 50;BA.debugLine="If CursorDBPoint.Position=0 Then";
if (_cursordbpoint.getPosition()==0) { 
 //BA.debugLineNum = 51;BA.debugLine="UserName=CursorDBPoint.GetString(\"姓名\")";
_username = _cursordbpoint.GetString("姓名");
 //BA.debugLineNum = 52;BA.debugLine="QueryScoreLevel '呼叫查詢考生目前的級數之副程式";
_queryscorelevel();
 //BA.debugLineNum = 53;BA.debugLine="Welcome=UserName & \"考生您好：\" & CRLF & \"歡迎使用【心算測驗系統】!\"";
_welcome = _username+"考生您好："+anywheresoftware.b4a.keywords.Common.CRLF+"歡迎使用【心算測驗系統】!";
 //BA.debugLineNum = 54;BA.debugLine="Msgbox(Welcome,\"系統回覆\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_welcome,"系統回覆",mostCurrent.activityBA);
 //BA.debugLineNum = 55;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 56;BA.debugLine="StartActivity(MathTest)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._mathtest.getObject()));
 }else {
 //BA.debugLineNum = 58;BA.debugLine="Msgbox(\"您非考生，無法登入本系統\",\"系統回覆\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您非考生，無法登入本系統","系統回覆",mostCurrent.activityBA);
 };
 };
 //BA.debugLineNum = 61;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 77;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 78;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim edtUserAccount As EditText";
mostCurrent._edtuseraccount = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim btnLogin As Button";
mostCurrent._btnlogin = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim strSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 9;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Dim UserAccount As String";
_useraccount = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim UserName As String";
_username = "";
 //BA.debugLineNum = 12;BA.debugLine="Dim ScoreLevel As Int=0";
_scorelevel = (int) (0);
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _queryscorelevel() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _levelpicture = null;
int _i = 0;
 //BA.debugLineNum = 63;BA.debugLine="Sub QueryScoreLevel()";
 //BA.debugLineNum = 64;BA.debugLine="Dim LevelPicture As Bitmap";
_levelpicture = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 65;BA.debugLine="strSQL=\"Select * FROM 測驗成績表 Where 帳號='\" & UserAccount & \"'\"";
mostCurrent._strsql = "Select * FROM 測驗成績表 Where 帳號='"+_useraccount+"'";
 //BA.debugLineNum = 66;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)       '執行SQL指令";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(mostCurrent._strsql)));
 //BA.debugLineNum = 67;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數";
{
final int step53 = 1;
final int limit53 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step53 > 0 && _i <= limit53) || (step53 < 0 && _i >= limit53); _i = ((int)(0 + _i + step53))) {
 //BA.debugLineNum = 68;BA.debugLine="CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 69;BA.debugLine="ScoreLevel=CursorDBPoint.GetString(\"級數\")  '取得考生目前的級數";
_scorelevel = (int)(Double.parseDouble(_cursordbpoint.GetString("級數")));
 }
};
 //BA.debugLineNum = 71;BA.debugLine="If ScoreLevel>0 Then";
if (_scorelevel>0) { 
 //BA.debugLineNum = 72;BA.debugLine="LevelPicture=LoadBitmap(File.DirAssets,\"Pass\" & ScoreLevel & \".jpg\")";
_levelpicture = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Pass"+BA.NumberToString(_scorelevel)+".jpg");
 //BA.debugLineNum = 73;BA.debugLine="Msgbox2(UserName & \"您目前已通過第\" & ScoreLevel & \"關\", \"公佈結果\", \"OK\", \"\", \"\", LevelPicture)";
anywheresoftware.b4a.keywords.Common.Msgbox2(_username+"您目前已通過第"+BA.NumberToString(_scorelevel)+"關","公佈結果","OK","","",(android.graphics.Bitmap)(_levelpicture.getObject()),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
}
