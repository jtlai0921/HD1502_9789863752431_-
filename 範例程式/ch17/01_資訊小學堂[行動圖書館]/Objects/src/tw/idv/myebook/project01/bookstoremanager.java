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

public class bookstoremanager extends Activity implements B4AActivity{
	public static bookstoremanager mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookstoremanager");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (bookstoremanager).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookstoremanager");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.bookstoremanager", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (bookstoremanager) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (bookstoremanager) Resume **");
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
		return bookstoremanager.class;
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
        BA.LogInfo("** Activity (bookstoremanager) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (bookstoremanager) Resume **");
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
public static boolean _check_bookstoreid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookstore_id = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookstore_name = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookstore_dns = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectbookstoreid = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectbookstorename = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookstore_id = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookstore_name = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbookstorelist = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
public tw.idv.myebook.project01.myebook _myebook = null;
public tw.idv.myebook.project01.keywordquery _keywordquery = null;
public tw.idv.myebook.project01.bookclassquery _bookclassquery = null;
public tw.idv.myebook.project01.userapp _userapp = null;
public tw.idv.myebook.project01.managerapp _managerapp = null;
public tw.idv.myebook.project01.booksmanager _booksmanager = null;
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
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 34;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"BookStoreManager\")";
mostCurrent._activity.LoadLayout("BookStoreManager",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="Activity.Title = \"【出版社資料管理系統】\"";
mostCurrent._activity.setTitle((Object)("【出版社資料管理系統】"));
 //BA.debugLineNum = 38;BA.debugLine="SpinQueryBookStore_ID.Visible =False";
mostCurrent._spinquerybookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 39;BA.debugLine="SpinQueryBookStore_Name.Visible =False";
mostCurrent._spinquerybookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 40;BA.debugLine="edtBookStore_ID.Text = \"BS007\"";
mostCurrent._edtbookstore_id.setText((Object)("BS007"));
 //BA.debugLineNum = 41;BA.debugLine="edtBookStore_Name.Text = \"松崗圖書\"";
mostCurrent._edtbookstore_name.setText((Object)("松崗圖書"));
 //BA.debugLineNum = 42;BA.debugLine="edtBookStore_DNS.Text = \"http://myebook.idv.tw\"";
mostCurrent._edtbookstore_dns.setText((Object)("http://myebook.idv.tw"));
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return "";
}
public static String  _btnbookstorelist_click() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub btnBookStoreList_Click";
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(\"BookStoreTable\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("BookStoreTable"));
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _bookstore_id = "";
String _strsql = "";
 //BA.debugLineNum = 94;BA.debugLine="Sub btnDelete_Click    '刪除出版社資料";
 //BA.debugLineNum = 95;BA.debugLine="Dim BookStore_ID As String =edtBookStore_ID.Text";
_bookstore_id = mostCurrent._edtbookstore_id.getText();
 //BA.debugLineNum = 96;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 97;BA.debugLine="strSQL = \"DELETE FROM 出版社資料表 WHERE 出版社編號 = '\" & BookStore_ID & \"'\"";
_strsql = "DELETE FROM 出版社資料表 WHERE 出版社編號 = '"+_bookstore_id+"'";
 //BA.debugLineNum = 98;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 99;BA.debugLine="ToastMessageShow(\"刪除一筆出版社記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆出版社記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _bookstore_id = "";
String _bookstore_name = "";
String _bookstore_dns = "";
String _strsql = "";
int _dbchange = 0;
 //BA.debugLineNum = 52;BA.debugLine="Sub btnInsert_Click   '新增出版社資料";
 //BA.debugLineNum = 53;BA.debugLine="Dim BookStore_ID As String =edtBookStore_ID.Text";
_bookstore_id = mostCurrent._edtbookstore_id.getText();
 //BA.debugLineNum = 54;BA.debugLine="Dim BookStore_Name As String = edtBookStore_Name.Text";
_bookstore_name = mostCurrent._edtbookstore_name.getText();
 //BA.debugLineNum = 55;BA.debugLine="Dim BookStore_DNS As String= edtBookStore_DNS.Text";
_bookstore_dns = mostCurrent._edtbookstore_dns.getText();
 //BA.debugLineNum = 56;BA.debugLine="If BookStore_ID=\"\" OR BookStore_Name=\"\" Then";
if ((_bookstore_id).equals("") || (_bookstore_name).equals("")) { 
 //BA.debugLineNum = 57;BA.debugLine="Msgbox(\"您尚未輸入「出版社」相關哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「出版社」相關哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 59;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 60;BA.debugLine="strSQL = \"INSERT INTO 出版社資料表(出版社編號,出版社名稱,DNS)\" & _              \"VALUES('\" & BookStore_ID & \"','\" & BookStore_Name & \"','\" & BookStore_DNS & \"')\"";
_strsql = "INSERT INTO 出版社資料表(出版社編號,出版社名稱,DNS)"+"VALUES('"+_bookstore_id+"','"+_bookstore_name+"','"+_bookstore_dns+"')";
 //BA.debugLineNum = 62;BA.debugLine="Check_BookStoreID_Exists(BookStore_ID)";
_check_bookstoreid_exists(_bookstore_id);
 //BA.debugLineNum = 63;BA.debugLine="If Check_BookStoreID_Exist=True Then";
if (_check_bookstoreid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 64;BA.debugLine="Msgbox(\"您已經重複新增這家出版社了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增這家出版社了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 66;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 67;BA.debugLine="Dim dbChange As Int";
_dbchange = 0;
 //BA.debugLineNum = 68;BA.debugLine="dbChange = SQLCmd.ExecQuerySingleResult(\"SELECT changes() FROM 出版社資料表\")";
_dbchange = (int)(Double.parseDouble(_sqlcmd.ExecQuerySingleResult("SELECT changes() FROM 出版社資料表")));
 //BA.debugLineNum = 69;BA.debugLine="ToastMessageShow(\"新增出版社記錄: \" & dbChange & \" 筆\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("新增出版社記錄: "+BA.NumberToString(_dbchange)+" 筆",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 71;BA.debugLine="Check_BookStoreID_Exist=False";
_check_bookstoreid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 167;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 168;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 169;BA.debugLine="StartActivity(\"ManagerAPP\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("ManagerAPP"));
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectbookstoreid_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 103;BA.debugLine="Sub btnSelectBookStoreID_Click";
 //BA.debugLineNum = 104;BA.debugLine="If SpinQueryBookStore_Name.Visible =True Then";
if (mostCurrent._spinquerybookstore_name.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 105;BA.debugLine="SpinQueryBookStore_Name.Visible =False";
mostCurrent._spinquerybookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 106;BA.debugLine="SpinQueryBookStore_ID.Visible =True";
mostCurrent._spinquerybookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 108;BA.debugLine="SpinQueryBookStore_ID.Visible =True";
mostCurrent._spinquerybookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 110;BA.debugLine="edtBookStore_ID.Visible =False";
mostCurrent._edtbookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 111;BA.debugLine="edtBookStore_Name.Visible =True";
mostCurrent._edtbookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 112;BA.debugLine="SpinQueryBookStore_ID.Clear";
mostCurrent._spinquerybookstore_id.Clear();
 //BA.debugLineNum = 113;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 出版社編號 FROM 出版社資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 出版社編號 FROM 出版社資料表")));
 //BA.debugLineNum = 114;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step91 = 1;
final int limit91 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step91 > 0 && _i <= limit91) || (step91 < 0 && _i >= limit91); _i = ((int)(0 + _i + step91))) {
 //BA.debugLineNum = 115;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 116;BA.debugLine="SpinQueryBookStore_ID.Add(CursorDBPoint.GetString(\"出版社編號\"))";
mostCurrent._spinquerybookstore_id.Add(_cursordbpoint.GetString("出版社編號"));
 }
};
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectbookstorename_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 138;BA.debugLine="Sub btnSelectBookStoreName_Click";
 //BA.debugLineNum = 139;BA.debugLine="If SpinQueryBookStore_ID.Visible =True Then";
if (mostCurrent._spinquerybookstore_id.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 140;BA.debugLine="SpinQueryBookStore_ID.Visible =False";
mostCurrent._spinquerybookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 141;BA.debugLine="SpinQueryBookStore_Name.Visible =True";
mostCurrent._spinquerybookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 143;BA.debugLine="SpinQueryBookStore_Name.Visible =True";
mostCurrent._spinquerybookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 145;BA.debugLine="edtBookStore_Name.Visible =False";
mostCurrent._edtbookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 146;BA.debugLine="edtBookStore_ID.Visible =True";
mostCurrent._edtbookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 147;BA.debugLine="SpinQueryBookStore_Name.Clear";
mostCurrent._spinquerybookstore_name.Clear();
 //BA.debugLineNum = 148;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 出版社名稱 FROM 出版社資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 出版社名稱 FROM 出版社資料表")));
 //BA.debugLineNum = 149;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step122 = 1;
final int limit122 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step122 > 0 && _i <= limit122) || (step122 < 0 && _i >= limit122); _i = ((int)(0 + _i + step122))) {
 //BA.debugLineNum = 150;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 151;BA.debugLine="SpinQueryBookStore_Name.Add(CursorDBPoint.GetString(\"出版社名稱\"))";
mostCurrent._spinquerybookstore_name.Add(_cursordbpoint.GetString("出版社名稱"));
 }
};
 //BA.debugLineNum = 153;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _bookstore_id = "";
String _strsql = "";
 //BA.debugLineNum = 85;BA.debugLine="Sub btnUpdate_Click    '修改出版社資料";
 //BA.debugLineNum = 86;BA.debugLine="Dim BookStore_ID As String =edtBookStore_ID.Text";
_bookstore_id = mostCurrent._edtbookstore_id.getText();
 //BA.debugLineNum = 87;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 88;BA.debugLine="strSQL = \"UPDATE 出版社資料表 SET 出版社名稱 ='\" & edtBookStore_Name.Text & \"',DNS ='\" & edtBookStore_DNS.Text & \"' WHERE 出版社編號 = '\" & BookStore_ID & \"'\"";
_strsql = "UPDATE 出版社資料表 SET 出版社名稱 ='"+mostCurrent._edtbookstore_name.getText()+"',DNS ='"+mostCurrent._edtbookstore_dns.getText()+"' WHERE 出版社編號 = '"+_bookstore_id+"'";
 //BA.debugLineNum = 90;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 91;BA.debugLine="ToastMessageShow(\"更新一筆出版社記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆出版社記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookstoredata(String _strsql) throws Exception{
 //BA.debugLineNum = 130;BA.debugLine="Sub CallShow_BookStoreData(strSQL As String)  '顯示「出版社名稱」之副程式";
 //BA.debugLineNum = 131;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 132;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 133;BA.debugLine="edtBookStore_ID.Text = CursorDBPoint.GetString(\"出版社編號\")";
mostCurrent._edtbookstore_id.setText((Object)(_cursordbpoint.GetString("出版社編號")));
 //BA.debugLineNum = 134;BA.debugLine="edtBookStore_Name.Text=CursorDBPoint.GetString(\"出版社名稱\")";
mostCurrent._edtbookstore_name.setText((Object)(_cursordbpoint.GetString("出版社名稱")));
 //BA.debugLineNum = 135;BA.debugLine="edtBookStore_DNS.Text=CursorDBPoint.GetString(\"DNS\")";
mostCurrent._edtbookstore_dns.setText((Object)(_cursordbpoint.GetString("DNS")));
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static String  _check_bookstoreid_exists(String _strbookstore_id) throws Exception{
String _sql_query = "";
int _i = 0;
 //BA.debugLineNum = 74;BA.debugLine="Sub Check_BookStoreID_Exists(strBookStore_ID As String)";
 //BA.debugLineNum = 75;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 76;BA.debugLine="SQL_Query=\"Select * FROM 出版社資料表 Where 出版社編號='\" & strBookStore_ID & \"' \"";
_sql_query = "Select * FROM 出版社資料表 Where 出版社編號='"+_strbookstore_id+"' ";
 //BA.debugLineNum = 77;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 78;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step60 = 1;
final int limit60 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step60 > 0 && _i <= limit60) || (step60 < 0 && _i >= limit60); _i = ((int)(0 + _i + step60))) {
 //BA.debugLineNum = 79;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 80;BA.debugLine="Check_BookStoreID_Exist=True";
_check_bookstoreid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim edtBookStore_ID As EditText     '出版社編號";
mostCurrent._edtbookstore_id = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim edtBookStore_Name As EditText   '出版社名稱";
mostCurrent._edtbookstore_name = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim edtBookStore_DNS As EditText    '出版社的官方網站";
mostCurrent._edtbookstore_dns = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSelectBookStoreID As Button    '依照出版社編號查詢";
mostCurrent._btnselectbookstoreid = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnSelectBookStoreName As Button  '依照出版社名稱查詢";
mostCurrent._btnselectbookstorename = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim SpinQueryBookStore_ID As Spinner    '查詢出版社編號";
mostCurrent._spinquerybookstore_id = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim SpinQueryBookStore_Name As Spinner  '查詢出版社名稱";
mostCurrent._spinquerybookstore_name = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnBookStoreList As Button";
mostCurrent._btnbookstorelist = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                              '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor                    '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_BookStoreID_Exist As Boolean=False   '檢查新增「出版社」時的編號是否重複";
_check_bookstoreid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookstore_id_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookstore_id = "";
 //BA.debugLineNum = 121;BA.debugLine="Sub SpinQueryBookStore_ID_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 122;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 123;BA.debugLine="Dim QueryBookStore_ID As String =SpinQueryBookStore_ID.SelectedItem";
_querybookstore_id = mostCurrent._spinquerybookstore_id.getSelectedItem();
 //BA.debugLineNum = 124;BA.debugLine="SQL_Query=\"Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社編號='\" & QueryBookStore_ID & \"' \"";
_sql_query = "Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社編號='"+_querybookstore_id+"' ";
 //BA.debugLineNum = 125;BA.debugLine="CallShow_BookStoreData(SQL_Query)";
_callshow_bookstoredata(_sql_query);
 //BA.debugLineNum = 126;BA.debugLine="SpinQueryBookStore_ID.Visible =False";
mostCurrent._spinquerybookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="edtBookStore_ID.Visible =True";
mostCurrent._edtbookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 128;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookstore_name_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookstore_name = "";
 //BA.debugLineNum = 154;BA.debugLine="Sub SpinQueryBookStore_Name_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 155;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 156;BA.debugLine="Dim QueryBookStore_Name As String =SpinQueryBookStore_Name.SelectedItem";
_querybookstore_name = mostCurrent._spinquerybookstore_name.getSelectedItem();
 //BA.debugLineNum = 157;BA.debugLine="SQL_Query=\"Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社名稱='\" & QueryBookStore_Name & \"' \"";
_sql_query = "Select 出版社編號,出版社名稱,DNS FROM 出版社資料表 Where 出版社名稱='"+_querybookstore_name+"' ";
 //BA.debugLineNum = 158;BA.debugLine="CallShow_BookStoreData(SQL_Query)";
_callshow_bookstoredata(_sql_query);
 //BA.debugLineNum = 159;BA.debugLine="SpinQueryBookStore_Name.Visible =False";
mostCurrent._spinquerybookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 160;BA.debugLine="edtBookStore_ID.Visible =True";
mostCurrent._edtbookstore_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 161;BA.debugLine="edtBookStore_Name.Visible =True";
mostCurrent._edtbookstore_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="End Sub";
return "";
}
}
