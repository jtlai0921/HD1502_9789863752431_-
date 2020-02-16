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

public class dataanalysis extends Activity implements B4AActivity{
	public static dataanalysis mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.dataanalysis");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (dataanalysis).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.dataanalysis");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.dataanalysis", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (dataanalysis) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (dataanalysis) Resume **");
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
		return dataanalysis.class;
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
        BA.LogInfo("** Activity (dataanalysis) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (dataanalysis) Resume **");
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
public static int _number = 0;
public static String[] _class_name = null;
public static int[] _class_no = null;
public static int _total = 0;
public static String _dataanalysis_title = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnbookclassanalysis = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbookstoreanalysis = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreaderclassanalysis = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
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
public tw.idv.myebook.project01.piechart _piechart = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 25;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 26;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 27;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"DataAnalysis\")";
mostCurrent._activity.LoadLayout("DataAnalysis",mostCurrent.activityBA);
 //BA.debugLineNum = 30;BA.debugLine="Activity.Title = \"統計分析報表\"";
mostCurrent._activity.setTitle((Object)("統計分析報表"));
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _btnbookclassanalysis_click() throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub btnBookClassAnalysis_Click";
 //BA.debugLineNum = 42;BA.debugLine="SQL_Query=\"Select A.分類代號,分類名稱,Count(*) As 各類別數量 FROM 書籍資料表 As A,書籍分類表 As B Where A.分類代號=B.分類代號 Group By  A.分類代號,分類名稱\"";
_sql_query = "Select A.分類代號,分類名稱,Count(*) As 各類別數量 FROM 書籍資料表 As A,書籍分類表 As B Where A.分類代號=B.分類代號 Group By  A.分類代號,分類名稱";
 //BA.debugLineNum = 43;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 44;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 45;BA.debugLine="For Number = 0 To CursorDBPoint.RowCount - 1";
{
final int step32 = 1;
final int limit32 = (int) (_cursordbpoint.getRowCount()-1);
for (_number = (int) (0); (step32 > 0 && _number <= limit32) || (step32 < 0 && _number >= limit32); _number = ((int)(0 + _number + step32))) {
 //BA.debugLineNum = 46;BA.debugLine="CursorDBPoint.Position = Number";
_cursordbpoint.setPosition(_number);
 //BA.debugLineNum = 47;BA.debugLine="ListView1.AddSingleLine2((Number+1) & \".\" & CursorDBPoint.GetString(\"分類名稱\") &  \"  數量：\" & CursorDBPoint.GetString(\"各類別數量\") ,CursorDBPoint.GetString(\"各類別數量\"))";
mostCurrent._listview1.AddSingleLine2(BA.NumberToString((_number+1))+"."+_cursordbpoint.GetString("分類名稱")+"  數量："+_cursordbpoint.GetString("各類別數量"),(Object)(_cursordbpoint.GetString("各類別數量")));
 //BA.debugLineNum = 48;BA.debugLine="Class_Name(Number)=CursorDBPoint.GetString(\"分類名稱\")";
_class_name[_number] = _cursordbpoint.GetString("分類名稱");
 //BA.debugLineNum = 49;BA.debugLine="Class_No(Number)=CursorDBPoint.GetString(\"各類別數量\")";
_class_no[_number] = (int)(Double.parseDouble(_cursordbpoint.GetString("各類別數量")));
 }
};
 //BA.debugLineNum = 51;BA.debugLine="Total=CursorDBPoint.RowCount  '記錄筆數";
_total = _cursordbpoint.getRowCount();
 //BA.debugLineNum = 52;BA.debugLine="DataAnalysis_Title=\"各「類別」書籍統計\"";
_dataanalysis_title = "各「類別」書籍統計";
 //BA.debugLineNum = 53;BA.debugLine="StartActivity(PieChart)  ' 啟動PieChart活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._piechart.getObject()));
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _btnbookstoreanalysis_click() throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub btnBookStoreAnalysis_Click";
 //BA.debugLineNum = 71;BA.debugLine="SQL_Query=\"Select A.出版社編號,B.出版社名稱,Count(*) As 各出版社數量 FROM 書籍資料表 As A,出版社資料表 As B Where A.出版社編號=B.出版社編號 Group By  A.出版社編號,B.出版社名稱\"";
_sql_query = "Select A.出版社編號,B.出版社名稱,Count(*) As 各出版社數量 FROM 書籍資料表 As A,出版社資料表 As B Where A.出版社編號=B.出版社編號 Group By  A.出版社編號,B.出版社名稱";
 //BA.debugLineNum = 72;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 73;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 74;BA.debugLine="For Number = 0 To CursorDBPoint.RowCount - 1";
{
final int step60 = 1;
final int limit60 = (int) (_cursordbpoint.getRowCount()-1);
for (_number = (int) (0); (step60 > 0 && _number <= limit60) || (step60 < 0 && _number >= limit60); _number = ((int)(0 + _number + step60))) {
 //BA.debugLineNum = 75;BA.debugLine="CursorDBPoint.Position = Number";
_cursordbpoint.setPosition(_number);
 //BA.debugLineNum = 76;BA.debugLine="ListView1.AddSingleLine2((Number+1) & \".\" & CursorDBPoint.GetString(\"出版社名稱\") &  \"  數量：\" & CursorDBPoint.GetString(\"各出版社數量\") ,CursorDBPoint.GetString(\"各出版社數量\"))";
mostCurrent._listview1.AddSingleLine2(BA.NumberToString((_number+1))+"."+_cursordbpoint.GetString("出版社名稱")+"  數量："+_cursordbpoint.GetString("各出版社數量"),(Object)(_cursordbpoint.GetString("各出版社數量")));
 //BA.debugLineNum = 77;BA.debugLine="Class_Name(Number)=CursorDBPoint.GetString(\"出版社名稱\")";
_class_name[_number] = _cursordbpoint.GetString("出版社名稱");
 //BA.debugLineNum = 78;BA.debugLine="Class_No(Number)=CursorDBPoint.GetString(\"各出版社數量\")";
_class_no[_number] = (int)(Double.parseDouble(_cursordbpoint.GetString("各出版社數量")));
 }
};
 //BA.debugLineNum = 80;BA.debugLine="Total=CursorDBPoint.RowCount  '記錄筆數";
_total = _cursordbpoint.getRowCount();
 //BA.debugLineNum = 81;BA.debugLine="DataAnalysis_Title=\"各「出版社」書籍統計\"";
_dataanalysis_title = "各「出版社」書籍統計";
 //BA.debugLineNum = 82;BA.debugLine="StartActivity(PieChart)  ' 啟動PieChart活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._piechart.getObject()));
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
public static String  _btnreaderclassanalysis_click() throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub btnReaderClassAnalysis_Click";
 //BA.debugLineNum = 57;BA.debugLine="SQL_Query=\"Select A.分類代號,分類名稱,Count(*) As 各類別讀者借閱數量 FROM 書籍資料表 As A,書籍分類表 As B,借閱記錄表 As C Where A.分類代號=B.分類代號 AND A.書號=C.書號 Group By  A.分類代號,分類名稱\"";
_sql_query = "Select A.分類代號,分類名稱,Count(*) As 各類別讀者借閱數量 FROM 書籍資料表 As A,書籍分類表 As B,借閱記錄表 As C Where A.分類代號=B.分類代號 AND A.書號=C.書號 Group By  A.分類代號,分類名稱";
 //BA.debugLineNum = 58;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 59;BA.debugLine="ListView1.Clear";
mostCurrent._listview1.Clear();
 //BA.debugLineNum = 60;BA.debugLine="For Number = 0 To CursorDBPoint.RowCount - 1";
{
final int step46 = 1;
final int limit46 = (int) (_cursordbpoint.getRowCount()-1);
for (_number = (int) (0); (step46 > 0 && _number <= limit46) || (step46 < 0 && _number >= limit46); _number = ((int)(0 + _number + step46))) {
 //BA.debugLineNum = 61;BA.debugLine="CursorDBPoint.Position = Number";
_cursordbpoint.setPosition(_number);
 //BA.debugLineNum = 62;BA.debugLine="ListView1.AddSingleLine2((Number+1) & \".\" & CursorDBPoint.GetString(\"分類名稱\") &  \"  數量：\" & CursorDBPoint.GetString(\"各類別讀者借閱數量\") ,CursorDBPoint.GetString(\"各類別讀者借閱數量\"))";
mostCurrent._listview1.AddSingleLine2(BA.NumberToString((_number+1))+"."+_cursordbpoint.GetString("分類名稱")+"  數量："+_cursordbpoint.GetString("各類別讀者借閱數量"),(Object)(_cursordbpoint.GetString("各類別讀者借閱數量")));
 //BA.debugLineNum = 63;BA.debugLine="Class_Name(Number)=CursorDBPoint.GetString(\"分類名稱\")";
_class_name[_number] = _cursordbpoint.GetString("分類名稱");
 //BA.debugLineNum = 64;BA.debugLine="Class_No(Number)=CursorDBPoint.GetString(\"各類別讀者借閱數量\")";
_class_no[_number] = (int)(Double.parseDouble(_cursordbpoint.GetString("各類別讀者借閱數量")));
 }
};
 //BA.debugLineNum = 66;BA.debugLine="Total=CursorDBPoint.RowCount  '記錄筆數";
_total = _cursordbpoint.getRowCount();
 //BA.debugLineNum = 67;BA.debugLine="DataAnalysis_Title=\"讀者閱讀「類別」書籍統計\"";
_dataanalysis_title = "讀者閱讀「類別」書籍統計";
 //BA.debugLineNum = 68;BA.debugLine="StartActivity(PieChart)  ' 啟動PieChart活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._piechart.getObject()));
 //BA.debugLineNum = 69;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 86;BA.debugLine="Activity.Finish()               '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 87;BA.debugLine="StartActivity(ManagerAPP)       '回上一頁";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._managerapp.getObject()));
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 17;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim btnBookClassAnalysis As Button";
mostCurrent._btnbookclassanalysis = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnBookStoreAnalysis As Button";
mostCurrent._btnbookstoreanalysis = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnReaderClassAnalysis As Button";
mostCurrent._btnreaderclassanalysis = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim ListView1 As ListView";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 10;BA.debugLine="Dim Number As Int";
_number = 0;
 //BA.debugLineNum = 11;BA.debugLine="Dim Class_Name(10) As String";
_class_name = new String[(int) (10)];
java.util.Arrays.fill(_class_name,"");
 //BA.debugLineNum = 12;BA.debugLine="Dim Class_No(10) As Int";
_class_no = new int[(int) (10)];
;
 //BA.debugLineNum = 13;BA.debugLine="Dim Total As Int";
_total = 0;
 //BA.debugLineNum = 14;BA.debugLine="Dim DataAnalysis_Title As String";
_dataanalysis_title = "";
 //BA.debugLineNum = 15;BA.debugLine="End Sub";
return "";
}
}
