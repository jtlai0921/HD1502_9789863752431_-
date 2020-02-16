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

public class bookclassmanager extends Activity implements B4AActivity{
	public static bookclassmanager mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookclassmanager");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (bookclassmanager).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.bookclassmanager");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.bookclassmanager", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (bookclassmanager) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (bookclassmanager) Resume **");
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
		return bookclassmanager.class;
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
        BA.LogInfo("** Activity (bookclassmanager) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (bookclassmanager) Resume **");
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
public static boolean _check_bookclassid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookclass_id = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookclass_name = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectbookclassid = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectbookclassname = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookclass_id = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookclass_name = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbookclasslist = null;
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
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"BookClassManager\")";
mostCurrent._activity.LoadLayout("BookClassManager",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="Activity.Title = \"【書籍類別管理系統】\"";
mostCurrent._activity.setTitle((Object)("【書籍類別管理系統】"));
 //BA.debugLineNum = 38;BA.debugLine="SpinQueryBookClass_ID.Visible =False";
mostCurrent._spinquerybookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 39;BA.debugLine="SpinQueryBookClass_Name.Visible =False";
mostCurrent._spinquerybookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 40;BA.debugLine="edtBookClass_ID.Text = \"B0007\"";
mostCurrent._edtbookclass_id.setText((Object)("B0007"));
 //BA.debugLineNum = 41;BA.debugLine="edtBookClass_Name.Text = \"網頁設計\"";
mostCurrent._edtbookclass_name.setText((Object)("網頁設計"));
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _btnbookclasslist_click() throws Exception{
 //BA.debugLineNum = 159;BA.debugLine="Sub btnBookClassList_Click";
 //BA.debugLineNum = 160;BA.debugLine="StartActivity(\"BookClassTable\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("BookClassTable"));
 //BA.debugLineNum = 161;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _bookclass_id = "";
String _strsql = "";
 //BA.debugLineNum = 92;BA.debugLine="Sub btnDelete_Click    '刪除出版社資料";
 //BA.debugLineNum = 93;BA.debugLine="Dim BookClass_ID As String =edtBookClass_ID.Text";
_bookclass_id = mostCurrent._edtbookclass_id.getText();
 //BA.debugLineNum = 94;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 95;BA.debugLine="strSQL = \"DELETE FROM 書籍分類表 WHERE 分類代號 = '\" & BookClass_ID & \"'\"";
_strsql = "DELETE FROM 書籍分類表 WHERE 分類代號 = '"+_bookclass_id+"'";
 //BA.debugLineNum = 96;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 97;BA.debugLine="ToastMessageShow(\"刪除一筆書籍分類記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆書籍分類記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _bookclass_id = "";
String _bookclass_name = "";
String _strsql = "";
int _dbchange = 0;
 //BA.debugLineNum = 51;BA.debugLine="Sub btnInsert_Click   '新增書籍分類資料";
 //BA.debugLineNum = 52;BA.debugLine="Dim BookClass_ID As String =edtBookClass_ID.Text";
_bookclass_id = mostCurrent._edtbookclass_id.getText();
 //BA.debugLineNum = 53;BA.debugLine="Dim BookClass_Name As String = edtBookClass_Name.Text";
_bookclass_name = mostCurrent._edtbookclass_name.getText();
 //BA.debugLineNum = 54;BA.debugLine="If BookClass_ID=\"\" OR BookClass_Name=\"\" Then";
if ((_bookclass_id).equals("") || (_bookclass_name).equals("")) { 
 //BA.debugLineNum = 55;BA.debugLine="Msgbox(\"您尚未輸入「書籍分類」相關哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「書籍分類」相關哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 57;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 58;BA.debugLine="strSQL = \"INSERT INTO 書籍分類表(分類代號,分類名稱)\" & _              \"VALUES('\" & BookClass_ID & \"','\" & BookClass_Name & \"')\"";
_strsql = "INSERT INTO 書籍分類表(分類代號,分類名稱)"+"VALUES('"+_bookclass_id+"','"+_bookclass_name+"')";
 //BA.debugLineNum = 60;BA.debugLine="Check_BookClassID_Exists(BookClass_ID)";
_check_bookclassid_exists(_bookclass_id);
 //BA.debugLineNum = 61;BA.debugLine="If Check_BookClassID_Exist=True Then";
if (_check_bookclassid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 62;BA.debugLine="Msgbox(\"您已經重複新增書籍分類了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增書籍分類了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 64;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 65;BA.debugLine="Dim dbChange As Int";
_dbchange = 0;
 //BA.debugLineNum = 66;BA.debugLine="dbChange = SQLCmd.ExecQuerySingleResult(\"SELECT changes() FROM 書籍分類表\")";
_dbchange = (int)(Double.parseDouble(_sqlcmd.ExecQuerySingleResult("SELECT changes() FROM 書籍分類表")));
 //BA.debugLineNum = 67;BA.debugLine="ToastMessageShow(\"新增書籍分類記錄: \" & dbChange & \" 筆\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("新增書籍分類記錄: "+BA.NumberToString(_dbchange)+" 筆",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 69;BA.debugLine="Check_BookClassID_Exist=False";
_check_bookclassid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 162;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 163;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(\"ManagerAPP\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("ManagerAPP"));
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectbookclassid_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 100;BA.debugLine="Sub btnSelectBookClassID_Click";
 //BA.debugLineNum = 101;BA.debugLine="If SpinQueryBookClass_Name.Visible =True Then";
if (mostCurrent._spinquerybookclass_name.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 102;BA.debugLine="SpinQueryBookClass_Name.Visible =False";
mostCurrent._spinquerybookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 103;BA.debugLine="SpinQueryBookClass_ID.Visible =True";
mostCurrent._spinquerybookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 105;BA.debugLine="SpinQueryBookClass_ID.Visible =True";
mostCurrent._spinquerybookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 107;BA.debugLine="edtBookClass_ID.Visible =False";
mostCurrent._edtbookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 108;BA.debugLine="edtBookClass_Name.Visible =True";
mostCurrent._edtbookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="SpinQueryBookClass_ID.Clear";
mostCurrent._spinquerybookclass_id.Clear();
 //BA.debugLineNum = 110;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 分類代號 FROM 書籍分類表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 分類代號 FROM 書籍分類表")));
 //BA.debugLineNum = 111;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step88 = 1;
final int limit88 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step88 > 0 && _i <= limit88) || (step88 < 0 && _i >= limit88); _i = ((int)(0 + _i + step88))) {
 //BA.debugLineNum = 112;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 113;BA.debugLine="SpinQueryBookClass_ID.Add(CursorDBPoint.GetString(\"分類代號\"))";
mostCurrent._spinquerybookclass_id.Add(_cursordbpoint.GetString("分類代號"));
 }
};
 //BA.debugLineNum = 116;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectbookclassname_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 134;BA.debugLine="Sub btnSelectBookClassName_Click";
 //BA.debugLineNum = 135;BA.debugLine="If SpinQueryBookClass_ID.Visible =True Then";
if (mostCurrent._spinquerybookclass_id.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 136;BA.debugLine="SpinQueryBookClass_ID.Visible =False";
mostCurrent._spinquerybookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 137;BA.debugLine="SpinQueryBookClass_Name.Visible =True";
mostCurrent._spinquerybookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 139;BA.debugLine="SpinQueryBookClass_Name.Visible =True";
mostCurrent._spinquerybookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 141;BA.debugLine="edtBookClass_Name.Visible =False";
mostCurrent._edtbookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 142;BA.debugLine="edtBookClass_ID.Visible =True";
mostCurrent._edtbookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 143;BA.debugLine="SpinQueryBookClass_Name.Clear";
mostCurrent._spinquerybookclass_name.Clear();
 //BA.debugLineNum = 144;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 分類名稱 FROM 書籍分類表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 分類名稱 FROM 書籍分類表")));
 //BA.debugLineNum = 145;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step118 = 1;
final int limit118 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step118 > 0 && _i <= limit118) || (step118 < 0 && _i >= limit118); _i = ((int)(0 + _i + step118))) {
 //BA.debugLineNum = 146;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 147;BA.debugLine="SpinQueryBookClass_Name.Add(CursorDBPoint.GetString(\"分類名稱\"))";
mostCurrent._spinquerybookclass_name.Add(_cursordbpoint.GetString("分類名稱"));
 }
};
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _bookclass_id = "";
String _strsql = "";
 //BA.debugLineNum = 83;BA.debugLine="Sub btnUpdate_Click    '修改出版社資料";
 //BA.debugLineNum = 84;BA.debugLine="Dim BookClass_ID As String =edtBookClass_ID.Text";
_bookclass_id = mostCurrent._edtbookclass_id.getText();
 //BA.debugLineNum = 85;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 86;BA.debugLine="strSQL = \"UPDATE 書籍分類表 SET 分類名稱 ='\" & edtBookClass_Name.Text & \"' WHERE 分類代號 = '\" & BookClass_ID & \"'\"";
_strsql = "UPDATE 書籍分類表 SET 分類名稱 ='"+mostCurrent._edtbookclass_name.getText()+"' WHERE 分類代號 = '"+_bookclass_id+"'";
 //BA.debugLineNum = 88;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 89;BA.debugLine="ToastMessageShow(\"更新一筆書籍分類記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆書籍分類記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclassdata(String _strsql) throws Exception{
 //BA.debugLineNum = 127;BA.debugLine="Sub CallShow_BookClassData(strSQL As String)  '顯示「分類名稱」之副程式";
 //BA.debugLineNum = 128;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 129;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 130;BA.debugLine="edtBookClass_ID.Text = CursorDBPoint.GetString(\"分類代號\")";
mostCurrent._edtbookclass_id.setText((Object)(_cursordbpoint.GetString("分類代號")));
 //BA.debugLineNum = 131;BA.debugLine="edtBookClass_Name.Text=CursorDBPoint.GetString(\"分類名稱\")";
mostCurrent._edtbookclass_name.setText((Object)(_cursordbpoint.GetString("分類名稱")));
 //BA.debugLineNum = 132;BA.debugLine="End Sub";
return "";
}
public static String  _check_bookclassid_exists(String _strbookclass_id) throws Exception{
String _sql_query = "";
int _i = 0;
 //BA.debugLineNum = 72;BA.debugLine="Sub Check_BookClassID_Exists(strBookClass_ID As String)";
 //BA.debugLineNum = 73;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 74;BA.debugLine="SQL_Query=\"Select * FROM 書籍分類表 Where 分類代號='\" & strBookClass_ID & \"' \"";
_sql_query = "Select * FROM 書籍分類表 Where 分類代號='"+_strbookclass_id+"' ";
 //BA.debugLineNum = 75;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 76;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step57 = 1;
final int limit57 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step57 > 0 && _i <= limit57) || (step57 < 0 && _i >= limit57); _i = ((int)(0 + _i + step57))) {
 //BA.debugLineNum = 77;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 78;BA.debugLine="Check_BookClassID_Exist=True";
_check_bookclassid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim edtBookClass_ID As EditText     '書籍類別編號";
mostCurrent._edtbookclass_id = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim edtBookClass_Name As EditText   '書籍類別名稱";
mostCurrent._edtbookclass_name = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSelectBookClassID As Button    '依照書籍類別編號查詢";
mostCurrent._btnselectbookclassid = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnSelectBookClassName As Button  '依照書籍類別名稱查詢";
mostCurrent._btnselectbookclassname = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim SpinQueryBookClass_ID As Spinner    '查詢書籍類別編號";
mostCurrent._spinquerybookclass_id = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim SpinQueryBookClass_Name As Spinner  '查詢書籍類別名稱";
mostCurrent._spinquerybookclass_name = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnBookClassList As Button";
mostCurrent._btnbookclasslist = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                              '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor                    '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_BookClassID_Exist As Boolean=False   '檢查新增「書籍類別」時的代號是否重複";
_check_bookclassid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookclass_id_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookclass_id = "";
 //BA.debugLineNum = 118;BA.debugLine="Sub SpinQueryBookClass_ID_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 119;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 120;BA.debugLine="Dim QueryBookClass_ID As String =SpinQueryBookClass_ID.SelectedItem";
_querybookclass_id = mostCurrent._spinquerybookclass_id.getSelectedItem();
 //BA.debugLineNum = 121;BA.debugLine="SQL_Query=\"Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類代號='\" & QueryBookClass_ID & \"' \"";
_sql_query = "Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類代號='"+_querybookclass_id+"' ";
 //BA.debugLineNum = 122;BA.debugLine="CallShow_BookClassData(SQL_Query)";
_callshow_bookclassdata(_sql_query);
 //BA.debugLineNum = 123;BA.debugLine="SpinQueryBookClass_ID.Visible =False";
mostCurrent._spinquerybookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 124;BA.debugLine="edtBookClass_ID.Visible =True";
mostCurrent._edtbookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookclass_name_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookclass_name = "";
 //BA.debugLineNum = 150;BA.debugLine="Sub SpinQueryBookClass_Name_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 151;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 152;BA.debugLine="Dim QueryBookClass_Name As String =SpinQueryBookClass_Name.SelectedItem";
_querybookclass_name = mostCurrent._spinquerybookclass_name.getSelectedItem();
 //BA.debugLineNum = 153;BA.debugLine="SQL_Query=\"Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類名稱='\" & QueryBookClass_Name & \"' \"";
_sql_query = "Select 分類代號,分類名稱 FROM 書籍分類表 Where 分類名稱='"+_querybookclass_name+"' ";
 //BA.debugLineNum = 154;BA.debugLine="CallShow_BookClassData(SQL_Query)";
_callshow_bookclassdata(_sql_query);
 //BA.debugLineNum = 155;BA.debugLine="SpinQueryBookClass_Name.Visible =False";
mostCurrent._spinquerybookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 156;BA.debugLine="edtBookClass_ID.Visible =True";
mostCurrent._edtbookclass_id.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 157;BA.debugLine="edtBookClass_Name.Visible =True";
mostCurrent._edtbookclass_name.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
}
