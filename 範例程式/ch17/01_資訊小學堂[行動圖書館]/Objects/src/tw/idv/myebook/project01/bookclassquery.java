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

public class bookclassquery extends Activity implements B4AActivity{
	public static bookclassquery mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookclassquery");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (bookclassquery).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookclassquery");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.bookclassquery", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (bookclassquery) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (bookclassquery) Resume **");
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
		return bookclassquery.class;
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
        BA.LogInfo("** Activity (bookclassquery) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (bookclassquery) Resume **");
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
public static String _sql_query_pg = "";
public static String _sql_query_db = "";
public static String _sql_query_ds = "";
public static String _sql_query_pc = "";
public static String _sql_query_el = "";
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursordbpoint = null;
public static anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook = null;
public static String _bookid_3 = "";
public static String _sql_query = "";
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_pg = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_db = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_ds = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_pc = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_el = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
public tw.idv.myebook.project01.myebook _myebook = null;
public tw.idv.myebook.project01.keywordquery _keywordquery = null;
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
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 31;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 32;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 34;BA.debugLine="Activity.LoadLayout(\"KeywordQuery\")";
mostCurrent._activity.LoadLayout("KeywordQuery",mostCurrent.activityBA);
 //BA.debugLineNum = 35;BA.debugLine="Activity.Title =\"歡迎使用「書籍分類系統」\"";
mostCurrent._activity.setTitle((Object)("歡迎使用「書籍分類系統」"));
 //BA.debugLineNum = 36;BA.debugLine="TabHost1.AddTab(\"程式\",\"Query_PG\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"程式","Query_PG");
 //BA.debugLineNum = 37;BA.debugLine="TabHost1.AddTab(\"資料庫\",\"Query_DB\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"資料庫","Query_DB");
 //BA.debugLineNum = 38;BA.debugLine="TabHost1.AddTab(\"資結\",\"Query_DS\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"資結","Query_DS");
 //BA.debugLineNum = 39;BA.debugLine="TabHost1.AddTab(\"計概\",\"Query_PC\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"計概","Query_PC");
 //BA.debugLineNum = 40;BA.debugLine="TabHost1.AddTab(\"數位\",\"Query_EL\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"數位","Query_EL");
 //BA.debugLineNum = 41;BA.debugLine="SQL_Query_PG=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='程式設計'\"";
_sql_query_pg = "Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='程式設計'";
 //BA.debugLineNum = 42;BA.debugLine="CallShow_BookClassQuery_PG(SQL_Query_PG)  '呼叫顯示「程式設計」類別書籍之副程式";
_callshow_bookclassquery_pg(_sql_query_pg);
 //BA.debugLineNum = 43;BA.debugLine="SQL_Query_DB=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料庫系統'\"";
_sql_query_db = "Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料庫系統'";
 //BA.debugLineNum = 44;BA.debugLine="CallShow_BookClassQuery_DB(SQL_Query_DB)  '呼叫顯示「資料庫系統」類別書籍之副程式";
_callshow_bookclassquery_db(_sql_query_db);
 //BA.debugLineNum = 45;BA.debugLine="SQL_Query_DS=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料結構'\"";
_sql_query_ds = "Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='資料結構'";
 //BA.debugLineNum = 46;BA.debugLine="CallShow_BookClassQuery_DS(SQL_Query_DS)  '呼叫顯示「資料結構」類別書籍之副程式";
_callshow_bookclassquery_ds(_sql_query_ds);
 //BA.debugLineNum = 47;BA.debugLine="SQL_Query_PC=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='計算機概論'\"";
_sql_query_pc = "Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='計算機概論'";
 //BA.debugLineNum = 48;BA.debugLine="CallShow_BookClassQuery_PC(SQL_Query_PC)  '呼叫顯示「計算機概論」類別書籍之副程式";
_callshow_bookclassquery_pc(_sql_query_pc);
 //BA.debugLineNum = 49;BA.debugLine="SQL_Query_EL=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='數位學習'\"";
_sql_query_el = "Select 書號,書名,出版社名稱 FROM 書籍資料表 As A,出版社資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND A.分類代號=C.分類代號 AND 分類名稱='數位學習'";
 //BA.debugLineNum = 50;BA.debugLine="CallShow_BookClassQuery_EL(SQL_Query_EL)  '呼叫顯示「數位學習」類別書籍之副程式";
_callshow_bookclassquery_el(_sql_query_el);
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassquery_db(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 72;BA.debugLine="Sub CallShow_BookClassQuery_DB(strSQL As String)  '顯示「資料庫系統」類別書籍之副程式";
 //BA.debugLineNum = 73;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 74;BA.debugLine="livQuery_DB.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_db.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 75;BA.debugLine="livQuery_DB.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_db.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 76;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step64 = 1;
final int limit64 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step64 > 0 && _i <= limit64) || (step64 < 0 && _i >= limit64); _i = ((int)(0 + _i + step64))) {
 //BA.debugLineNum = 77;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 78;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 79;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 81;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 83;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=15 Then";
if (_cursordbpoint.GetString("書名").length()>=15) { 
 //BA.debugLineNum = 84;BA.debugLine="livQuery_DB.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,15) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_db.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (15))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 86;BA.debugLine="livQuery_DB.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_db.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassquery_ds(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 91;BA.debugLine="Sub CallShow_BookClassQuery_DS(strSQL As String)  '顯示「資料結構」類別書籍之副程式";
 //BA.debugLineNum = 92;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 93;BA.debugLine="livQuery_DS.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_ds.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 94;BA.debugLine="livQuery_DS.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_ds.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 95;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step82 = 1;
final int limit82 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step82 > 0 && _i <= limit82) || (step82 < 0 && _i >= limit82); _i = ((int)(0 + _i + step82))) {
 //BA.debugLineNum = 96;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 97;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 //BA.debugLineNum = 98;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=15 Then";
if (_cursordbpoint.GetString("書名").length()>=15) { 
 //BA.debugLineNum = 99;BA.debugLine="livQuery_DS.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,15) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_ds.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (15))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 101;BA.debugLine="livQuery_DS.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_ds.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassquery_el(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 125;BA.debugLine="Sub CallShow_BookClassQuery_EL(strSQL As String)  '顯示「數位學習」類別書籍之副程式";
 //BA.debugLineNum = 126;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 127;BA.debugLine="livQuery_EL.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_el.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 128;BA.debugLine="livQuery_EL.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_el.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 129;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step114 = 1;
final int limit114 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step114 > 0 && _i <= limit114) || (step114 < 0 && _i >= limit114); _i = ((int)(0 + _i + step114))) {
 //BA.debugLineNum = 130;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 131;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 132;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 134;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 136;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=15 Then";
if (_cursordbpoint.GetString("書名").length()>=15) { 
 //BA.debugLineNum = 137;BA.debugLine="livQuery_EL.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,15) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_el.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (15))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 139;BA.debugLine="livQuery_EL.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_el.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassquery_pc(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 105;BA.debugLine="Sub CallShow_BookClassQuery_PC(strSQL As String)  '顯示「計算機概論」類別書籍之副程式";
 //BA.debugLineNum = 106;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 107;BA.debugLine="livQuery_PC.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_pc.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 108;BA.debugLine="livQuery_PC.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_pc.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 109;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step96 = 1;
final int limit96 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step96 > 0 && _i <= limit96) || (step96 < 0 && _i >= limit96); _i = ((int)(0 + _i + step96))) {
 //BA.debugLineNum = 110;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 112;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 113;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 115;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 117;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=15 Then";
if (_cursordbpoint.GetString("書名").length()>=15) { 
 //BA.debugLineNum = 118;BA.debugLine="livQuery_PC.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,15) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_pc.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (15))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 120;BA.debugLine="livQuery_PC.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_pc.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassquery_pg(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 53;BA.debugLine="Sub CallShow_BookClassQuery_PG(strSQL As String)  '顯示「程式設計」類別書籍之副程式";
 //BA.debugLineNum = 54;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 55;BA.debugLine="livQuery_PG.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_pg.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 56;BA.debugLine="livQuery_PG.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_pg.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 57;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step46 = 1;
final int limit46 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step46 > 0 && _i <= limit46) || (step46 < 0 && _i >= limit46); _i = ((int)(0 + _i + step46))) {
 //BA.debugLineNum = 58;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 59;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 60;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 62;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
_picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 64;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=20 Then";
if (_cursordbpoint.GetString("書名").length()>=20) { 
 //BA.debugLineNum = 65;BA.debugLine="livQuery_PG.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,20) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_pg.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (20))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 67;BA.debugLine="livQuery_PG.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_pg.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(_picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim livQuery_PG As ListView";
mostCurrent._livquery_pg = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim livQuery_DB As ListView";
mostCurrent._livquery_db = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim livQuery_DS As ListView";
mostCurrent._livquery_ds = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim livQuery_PC As ListView";
mostCurrent._livquery_pc = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim livQuery_EL As ListView";
mostCurrent._livquery_el = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_db_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 149;BA.debugLine="Sub livQuery_DB_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 150;BA.debugLine="BookID_3 = Value    '書籍編號";
_bookid_3 = BA.ObjectToString(_value);
 //BA.debugLineNum = 151;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_ds_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub livQuery_DS_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 154;BA.debugLine="BookID_3 = Value    '書籍編號";
_bookid_3 = BA.ObjectToString(_value);
 //BA.debugLineNum = 155;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_el_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub livQuery_EL_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 163;BA.debugLine="BookID_3 = Value    '書籍編號";
_bookid_3 = BA.ObjectToString(_value);
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_pc_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub livQuery_PC_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 159;BA.debugLine="BookID_3 = Value    '書籍編號";
_bookid_3 = BA.ObjectToString(_value);
 //BA.debugLineNum = 160;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_pg_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 144;BA.debugLine="Sub livQuery_PG_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 145;BA.debugLine="BookID_3 = Value    '書籍編號";
_bookid_3 = BA.ObjectToString(_value);
 //BA.debugLineNum = 146;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim SQL_Query_PG As String";
_sql_query_pg = "";
 //BA.debugLineNum = 9;BA.debugLine="Dim SQL_Query_DB As String";
_sql_query_db = "";
 //BA.debugLineNum = 10;BA.debugLine="Dim SQL_Query_DS As String";
_sql_query_ds = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim SQL_Query_PC As String";
_sql_query_pc = "";
 //BA.debugLineNum = 12;BA.debugLine="Dim SQL_Query_EL As String";
_sql_query_el = "";
 //BA.debugLineNum = 13;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim PicBook As Bitmap";
_picbook = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim BookID_3 As String     '書籍編號";
_bookid_3 = "";
 //BA.debugLineNum = 16;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
