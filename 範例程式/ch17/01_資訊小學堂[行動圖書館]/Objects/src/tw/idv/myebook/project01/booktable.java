package tw.idv.myebook.project01;

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

public class booktable extends Activity implements B4AActivity{
	public static booktable mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.booktable");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (booktable).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.booktable");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.booktable", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (booktable) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (booktable) Resume **");
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
		return booktable.class;
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
        BA.LogInfo("** Activity (booktable) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (booktable) Resume **");
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
public static String _bookid_1 = "";
public static String _bookname_1 = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livbooktable = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.myebook _myebook = null;
public tw.idv.myebook.project01.keywordquery _keywordquery = null;
public tw.idv.myebook.project01.bookclassquery _bookclassquery = null;
public tw.idv.myebook.project01.userapp _userapp = null;
public tw.idv.myebook.project01.managerapp _managerapp = null;
public tw.idv.myebook.project01.booksmanager _booksmanager = null;
public tw.idv.myebook.project01.bookstoremanager _bookstoremanager = null;
public tw.idv.myebook.project01.bookstoretable _bookstoretable = null;
public tw.idv.myebook.project01.bookclassmanager _bookclassmanager = null;
public tw.idv.myebook.project01.bookclasstable _bookclasstable = null;
public tw.idv.myebook.project01.userlogin _userlogin = null;
public tw.idv.myebook.project01.managerlogin _managerlogin = null;
public tw.idv.myebook.project01.mylovebooks _mylovebooks = null;
public tw.idv.myebook.project01.readermanager _readermanager = null;
public tw.idv.myebook.project01.readertable _readertable = null;
public tw.idv.myebook.project01.charts _charts = null;
public tw.idv.myebook.project01.dataanalysis _dataanalysis = null;
public tw.idv.myebook.project01.piechart _piechart = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 20;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 21;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 23;BA.debugLine="Activity.LoadLayout(\"BookTable\")";
mostCurrent._activity.LoadLayout("BookTable",mostCurrent.activityBA);
 //BA.debugLineNum = 24;BA.debugLine="Activity.Title =\"書籍清單查詢(全部)\"";
mostCurrent._activity.setTitle((Object)("書籍清單查詢(全部)"));
 //BA.debugLineNum = 25;BA.debugLine="SQL_Query=\"Select 出版社名稱,書號,書名 FROM 出版社資料表 As A ,書籍資料表 As B Where A.出版社編號=B.出版社編號\"";
_sql_query = "Select 出版社名稱,書號,書名 FROM 出版社資料表 As A ,書籍資料表 As B Where A.出版社編號=B.出版社編號";
 //BA.debugLineNum = 26;BA.debugLine="QueryBookTable(SQL_Query)";
_querybooktable(_sql_query);
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 58;BA.debugLine="Activity.Finish()         '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 59;BA.debugLine="StartActivity(UserAPP)  ' 返回UserAPP主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._userapp.getObject()));
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim livBookTable As ListView";
mostCurrent._livbooktable = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _livbooktable_itemclick(int _position,Object _value) throws Exception{
int _i = 0;
 //BA.debugLineNum = 46;BA.debugLine="Sub livBookTable_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 47;BA.debugLine="BookID_1 = Value    '書籍編號";
_bookid_1 = BA.ObjectToString(_value);
 //BA.debugLineNum = 48;BA.debugLine="SQL_Query=\"Select 書名 FROM 書籍資料表 Where 書號='\" & BookID_1 & \"' \"";
_sql_query = "Select 書名 FROM 書籍資料表 Where 書號='"+_bookid_1+"' ";
 //BA.debugLineNum = 49;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 50;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step37 = 1;
final int limit37 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step37 > 0 && _i <= limit37) || (step37 < 0 && _i >= limit37); _i = ((int)(0 + _i + step37))) {
 //BA.debugLineNum = 51;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 52;BA.debugLine="BookName_1=CursorDBPoint.GetString(\"書名\")";
_bookname_1 = _cursordbpoint.GetString("書名");
 }
};
 //BA.debugLineNum = 54;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 10;BA.debugLine="Dim BookID_1 As String     '書籍編號";
_bookid_1 = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim BookName_1 As String   '書名";
_bookname_1 = "";
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _querybooktable(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 36;BA.debugLine="Sub QueryBookTable(strSQL As String)";
 //BA.debugLineNum = 37;BA.debugLine="livBookTable.Clear()";
mostCurrent._livbooktable.Clear();
 //BA.debugLineNum = 38;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 39;BA.debugLine="livBookTable.SingleLineLayout.Label.TextSize = 16";
mostCurrent._livbooktable.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 40;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step28 = 1;
final int limit28 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step28 > 0 && _i <= limit28) || (step28 < 0 && _i >= limit28); _i = ((int)(0 + _i + step28))) {
 //BA.debugLineNum = 41;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 42;BA.debugLine="livBookTable.AddTwoLines2((i+1) & \".\" & CursorDBPoint.GetString(\"出版社名稱\") &  \"  \" & CursorDBPoint.GetString(\"書號\"),CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livbooktable.AddTwoLines2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("出版社名稱")+"  "+_cursordbpoint.GetString("書號"),_cursordbpoint.GetString("書名"),(Object)(_cursordbpoint.GetString("書號")));
 }
};
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
}
