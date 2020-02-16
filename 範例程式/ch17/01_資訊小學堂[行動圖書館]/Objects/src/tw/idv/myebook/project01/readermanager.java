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

public class readermanager extends Activity implements B4AActivity{
	public static readermanager mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.readermanager");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (readermanager).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.readermanager");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.readermanager", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (readermanager) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (readermanager) Resume **");
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
		return readermanager.class;
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
        BA.LogInfo("** Activity (readermanager) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (readermanager) Resume **");
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
public static boolean _check_readeraccount_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtreader_account = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtreader_password = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtreader_name = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectreaderaccount = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectreadername = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinqueryreader_account = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinqueryreader_name = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreaderlist = null;
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
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 34;BA.debugLine="If SQLCmd.IsInitialized() = False Then    '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 35;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 37;BA.debugLine="Activity.LoadLayout(\"ReaderManager\")";
mostCurrent._activity.LoadLayout("ReaderManager",mostCurrent.activityBA);
 //BA.debugLineNum = 38;BA.debugLine="Activity.Title = \"【讀者資料管理系統】\"";
mostCurrent._activity.setTitle((Object)("【讀者資料管理系統】"));
 //BA.debugLineNum = 39;BA.debugLine="SpinQueryReader_Account.Visible =False";
mostCurrent._spinqueryreader_account.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 40;BA.debugLine="SpinQueryReader_Name.Visible =False";
mostCurrent._spinqueryreader_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 41;BA.debugLine="edtReader_Account.Text = \"44444\"";
mostCurrent._edtreader_account.setText((Object)("44444"));
 //BA.debugLineNum = 42;BA.debugLine="edtReader_Password.Text = \"44444\"";
mostCurrent._edtreader_password.setText((Object)("44444"));
 //BA.debugLineNum = 43;BA.debugLine="edtReader_Name.Text = \"四維\"";
mostCurrent._edtreader_name.setText((Object)("四維"));
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _reader_account = "";
String _strsql = "";
 //BA.debugLineNum = 93;BA.debugLine="Sub btnDelete_Click    '刪除讀者資料";
 //BA.debugLineNum = 94;BA.debugLine="Dim Reader_Account As String =edtReader_Account.Text";
_reader_account = mostCurrent._edtreader_account.getText();
 //BA.debugLineNum = 95;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 96;BA.debugLine="strSQL = \"DELETE FROM 讀者資料表 WHERE 帳號 = '\" & Reader_Account & \"'\"";
_strsql = "DELETE FROM 讀者資料表 WHERE 帳號 = '"+_reader_account+"'";
 //BA.debugLineNum = 97;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 98;BA.debugLine="ToastMessageShow(\"刪除一筆讀者記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆讀者記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _reader_account = "";
String _reader_password = "";
String _reader_name = "";
String _strsql = "";
int _dbchange = 0;
 //BA.debugLineNum = 53;BA.debugLine="Sub btnInsert_Click   '新增讀者資料";
 //BA.debugLineNum = 54;BA.debugLine="Dim Reader_Account As String =edtReader_Account.Text";
_reader_account = mostCurrent._edtreader_account.getText();
 //BA.debugLineNum = 55;BA.debugLine="Dim Reader_Password As String = edtReader_Password.Text";
_reader_password = mostCurrent._edtreader_password.getText();
 //BA.debugLineNum = 56;BA.debugLine="Dim Reader_Name As String= edtReader_Name.Text";
_reader_name = mostCurrent._edtreader_name.getText();
 //BA.debugLineNum = 57;BA.debugLine="If Reader_Account=\"\" OR Reader_Password=\"\" Then";
if ((_reader_account).equals("") || (_reader_password).equals("")) { 
 //BA.debugLineNum = 58;BA.debugLine="Msgbox(\"您尚未輸入「讀者」相關哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「讀者」相關哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 60;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 61;BA.debugLine="strSQL = \"INSERT INTO 讀者資料表(帳號,密碼,姓名)\" & _              \"VALUES('\" & Reader_Account & \"','\" & Reader_Password & \"','\" & Reader_Name & \"')\"";
_strsql = "INSERT INTO 讀者資料表(帳號,密碼,姓名)"+"VALUES('"+_reader_account+"','"+_reader_password+"','"+_reader_name+"')";
 //BA.debugLineNum = 63;BA.debugLine="Check_ReaderAccount_Exists(Reader_Account)";
_check_readeraccount_exists(_reader_account);
 //BA.debugLineNum = 64;BA.debugLine="If Check_ReaderAccount_Exist=True Then";
if (_check_readeraccount_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 65;BA.debugLine="Msgbox(\"您已經重複新增這位讀者了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增這位讀者了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 67;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 68;BA.debugLine="Dim dbChange As Int";
_dbchange = 0;
 //BA.debugLineNum = 69;BA.debugLine="dbChange = SQLCmd.ExecQuerySingleResult(\"SELECT changes() FROM 讀者資料表\")";
_dbchange = (int)(Double.parseDouble(_sqlcmd.ExecQuerySingleResult("SELECT changes() FROM 讀者資料表")));
 //BA.debugLineNum = 70;BA.debugLine="ToastMessageShow(\"新增讀者記錄: \" & dbChange & \" 筆\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("新增讀者記錄: "+BA.NumberToString(_dbchange)+" 筆",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 72;BA.debugLine="Check_ReaderAccount_Exist=False";
_check_readeraccount_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _btnreaderlist_click() throws Exception{
 //BA.debugLineNum = 163;BA.debugLine="Sub btnReaderList_Click";
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(\"ReaderTable\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("ReaderTable"));
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
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
public static String  _btnselectreaderaccount_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 102;BA.debugLine="Sub btnSelectReaderAccount_Click";
 //BA.debugLineNum = 103;BA.debugLine="If SpinQueryReader_Name.Visible =True Then";
if (mostCurrent._spinqueryreader_name.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 104;BA.debugLine="SpinQueryReader_Name.Visible =False";
mostCurrent._spinqueryreader_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 105;BA.debugLine="SpinQueryReader_Account.Visible =True";
mostCurrent._spinqueryreader_account.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 107;BA.debugLine="SpinQueryReader_Account.Visible =True";
mostCurrent._spinqueryreader_account.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 109;BA.debugLine="edtReader_Account.Visible =False";
mostCurrent._edtreader_account.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 110;BA.debugLine="edtReader_Name.Visible =True";
mostCurrent._edtreader_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 111;BA.debugLine="SpinQueryReader_Account.Clear";
mostCurrent._spinqueryreader_account.Clear();
 //BA.debugLineNum = 112;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 帳號 FROM 讀者資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 帳號 FROM 讀者資料表")));
 //BA.debugLineNum = 113;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step89 = 1;
final int limit89 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step89 > 0 && _i <= limit89) || (step89 < 0 && _i >= limit89); _i = ((int)(0 + _i + step89))) {
 //BA.debugLineNum = 114;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 115;BA.debugLine="SpinQueryReader_Account.Add(CursorDBPoint.GetString(\"帳號\"))";
mostCurrent._spinqueryreader_account.Add(_cursordbpoint.GetString("帳號"));
 }
};
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectreadername_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 138;BA.debugLine="Sub btnSelectReaderName_Click";
 //BA.debugLineNum = 139;BA.debugLine="If SpinQueryReader_Account.Visible =True Then";
if (mostCurrent._spinqueryreader_account.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 140;BA.debugLine="SpinQueryReader_Account.Visible =False";
mostCurrent._spinqueryreader_account.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 141;BA.debugLine="SpinQueryReader_Name.Visible =True";
mostCurrent._spinqueryreader_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 143;BA.debugLine="SpinQueryReader_Name.Visible =True";
mostCurrent._spinqueryreader_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 145;BA.debugLine="edtReader_Name.Visible =False";
mostCurrent._edtreader_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 146;BA.debugLine="edtReader_Account.Visible =True";
mostCurrent._edtreader_account.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 147;BA.debugLine="SpinQueryReader_Name.Clear";
mostCurrent._spinqueryreader_name.Clear();
 //BA.debugLineNum = 148;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 姓名 FROM 讀者資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 姓名 FROM 讀者資料表")));
 //BA.debugLineNum = 149;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step120 = 1;
final int limit120 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step120 > 0 && _i <= limit120) || (step120 < 0 && _i >= limit120); _i = ((int)(0 + _i + step120))) {
 //BA.debugLineNum = 150;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 151;BA.debugLine="SpinQueryReader_Name.Add(CursorDBPoint.GetString(\"姓名\"))";
mostCurrent._spinqueryreader_name.Add(_cursordbpoint.GetString("姓名"));
 }
};
 //BA.debugLineNum = 153;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _reader_account = "";
String _strsql = "";
 //BA.debugLineNum = 84;BA.debugLine="Sub btnUpdate_Click    '修改讀者資料";
 //BA.debugLineNum = 85;BA.debugLine="Dim Reader_Account As String =edtReader_Account.Text";
_reader_account = mostCurrent._edtreader_account.getText();
 //BA.debugLineNum = 86;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 87;BA.debugLine="strSQL = \"UPDATE 讀者資料表 SET 密碼 ='\" & edtReader_Password.Text & \"',姓名 ='\" & edtReader_Name.Text & \"' WHERE 帳號 = '\" & Reader_Account & \"'\"";
_strsql = "UPDATE 讀者資料表 SET 密碼 ='"+mostCurrent._edtreader_password.getText()+"',姓名 ='"+mostCurrent._edtreader_name.getText()+"' WHERE 帳號 = '"+_reader_account+"'";
 //BA.debugLineNum = 89;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 90;BA.debugLine="ToastMessageShow(\"更新一筆讀者記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆讀者記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_readerdata(String _strsql) throws Exception{
 //BA.debugLineNum = 129;BA.debugLine="Sub CallShow_ReaderData(strSQL As String)  '顯示「密碼」之副程式";
 //BA.debugLineNum = 130;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 131;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 132;BA.debugLine="edtReader_Account.Text = CursorDBPoint.GetString(\"帳號\")";
mostCurrent._edtreader_account.setText((Object)(_cursordbpoint.GetString("帳號")));
 //BA.debugLineNum = 133;BA.debugLine="edtReader_Password.Text=CursorDBPoint.GetString(\"密碼\")";
mostCurrent._edtreader_password.setText((Object)(_cursordbpoint.GetString("密碼")));
 //BA.debugLineNum = 134;BA.debugLine="edtReader_Name.Text=CursorDBPoint.GetString(\"姓名\")";
mostCurrent._edtreader_name.setText((Object)(_cursordbpoint.GetString("姓名")));
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _check_readeraccount_exists(String _strreader_account) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 75;BA.debugLine="Sub Check_ReaderAccount_Exists(strReader_Account As String)";
 //BA.debugLineNum = 76;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 77;BA.debugLine="SQL_Query=\"Select * FROM 讀者資料表 Where 帳號='\" & strReader_Account & \"' \"";
_sql_query = "Select * FROM 讀者資料表 Where 帳號='"+_strreader_account+"' ";
 //BA.debugLineNum = 78;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 79;BA.debugLine="If CursorDBPoint.RowCount=1 Then";
if (_cursordbpoint.getRowCount()==1) { 
 //BA.debugLineNum = 80;BA.debugLine="Check_ReaderAccount_Exist=True  '代表讀者已經新增過了(存在)";
_check_readeraccount_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim edtReader_Account As EditText         '帳號";
mostCurrent._edtreader_account = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim edtReader_Password As EditText    '密碼";
mostCurrent._edtreader_password = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim edtReader_Name As EditText            '讀者的姓名";
mostCurrent._edtreader_name = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnInsert As Button                   '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnUpdate As Button                   '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnDelete As Button                   '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSelectReaderAccount As Button      '依照「帳號」查詢";
mostCurrent._btnselectreaderaccount = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnSelectReaderName As Button         '依照「姓名」查詢";
mostCurrent._btnselectreadername = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim SpinQueryReader_Account As Spinner    '查詢帳號";
mostCurrent._spinqueryreader_account = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim SpinQueryReader_Name As Spinner       '查詢姓名";
mostCurrent._spinqueryreader_name = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnReaderList As Button";
mostCurrent._btnreaderlist = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                                   '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor                         '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_ReaderAccount_Exist As Boolean=False      '檢查新增「讀者」時的帳號是否重複";
_check_readeraccount_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _spinqueryreader_account_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _queryreader_account = "";
 //BA.debugLineNum = 120;BA.debugLine="Sub SpinQueryReader_Account_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 121;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 122;BA.debugLine="Dim QueryReader_Account As String =SpinQueryReader_Account.SelectedItem";
_queryreader_account = mostCurrent._spinqueryreader_account.getSelectedItem();
 //BA.debugLineNum = 123;BA.debugLine="SQL_Query=\"Select 帳號,密碼,姓名 FROM 讀者資料表 Where 帳號='\" & QueryReader_Account & \"' \"";
_sql_query = "Select 帳號,密碼,姓名 FROM 讀者資料表 Where 帳號='"+_queryreader_account+"' ";
 //BA.debugLineNum = 124;BA.debugLine="CallShow_ReaderData(SQL_Query)";
_callshow_readerdata(_sql_query);
 //BA.debugLineNum = 125;BA.debugLine="SpinQueryReader_Account.Visible =False";
mostCurrent._spinqueryreader_account.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 126;BA.debugLine="edtReader_Account.Visible =True";
mostCurrent._edtreader_account.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _spinqueryreader_name_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _queryreader_name = "";
 //BA.debugLineNum = 154;BA.debugLine="Sub SpinQueryReader_Name_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 155;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 156;BA.debugLine="Dim QueryReader_Name As String =SpinQueryReader_Name.SelectedItem";
_queryreader_name = mostCurrent._spinqueryreader_name.getSelectedItem();
 //BA.debugLineNum = 157;BA.debugLine="SQL_Query=\"Select 帳號,密碼,姓名 FROM 讀者資料表 Where 姓名='\" & QueryReader_Name & \"' \"";
_sql_query = "Select 帳號,密碼,姓名 FROM 讀者資料表 Where 姓名='"+_queryreader_name+"' ";
 //BA.debugLineNum = 158;BA.debugLine="CallShow_ReaderData(SQL_Query)";
_callshow_readerdata(_sql_query);
 //BA.debugLineNum = 159;BA.debugLine="SpinQueryReader_Name.Visible =False";
mostCurrent._spinqueryreader_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 160;BA.debugLine="edtReader_Account.Visible =True";
mostCurrent._edtreader_account.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 161;BA.debugLine="edtReader_Name.Visible =True";
mostCurrent._edtreader_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 162;BA.debugLine="End Sub";
return "";
}
}
