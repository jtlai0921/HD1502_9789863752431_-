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

public class bookstore extends Activity implements B4AActivity{
	public static bookstore mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.bookstore");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (bookstore).");
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
		activityBA = new BA(this, layout, processBA, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.bookstore");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leechphd.myebook.B4AtoSQLServer2.bookstore", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (bookstore) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (bookstore) Resume **");
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
		return bookstore.class;
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
        BA.LogInfo("** Activity (bookstore) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (bookstore) Resume **");
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
public static boolean _check_bookno_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookname = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livbookstore = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitbookstore = null;
public static String _strsqlviewtitle = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public Leechphd.myebook.B4AtoSQLServer2.main _main = null;
public Leechphd.myebook.B4AtoSQLServer2.httputils _httputils = null;
public Leechphd.myebook.B4AtoSQLServer2.httputilsservice _httputilsservice = null;
public Leechphd.myebook.B4AtoSQLServer2.myebook _myebook = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 32;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 33;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 34;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeBookDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeBookDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"BookStore\")";
mostCurrent._activity.LoadLayout("BookStore",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="Activity.Title =\"【書籍管理系統】\"";
mostCurrent._activity.setTitle((Object)("【書籍管理系統】"));
 //BA.debugLineNum = 38;BA.debugLine="edtBookNo.Text=\"B0007\"";
mostCurrent._edtbookno.setText((Object)("B0007"));
 //BA.debugLineNum = 39;BA.debugLine="edtBookName.Text=\"手機與機器人程式設計\"";
mostCurrent._edtbookname.setText((Object)("手機與機器人程式設計"));
 //BA.debugLineNum = 40;BA.debugLine="QueryDBList(\"Select * FROM 書籍資料表\")      '呼叫顯示目前存在的書籍資料表之副程式";
_querydblist("Select * FROM 書籍資料表");
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _strbookno = "";
String _strsql = "";
 //BA.debugLineNum = 96;BA.debugLine="Sub btnDelete_Click   '刪除「書籍」資料";
 //BA.debugLineNum = 97;BA.debugLine="If edtBookNo.Text=\"\" Then";
if ((mostCurrent._edtbookno.getText()).equals("")) { 
 //BA.debugLineNum = 98;BA.debugLine="Msgbox(\"您尚未輸入「書籍資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 100;BA.debugLine="Dim strBookNo As String =edtBookNo.Text";
_strbookno = mostCurrent._edtbookno.getText();
 //BA.debugLineNum = 101;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 102;BA.debugLine="strSQL = \"DELETE FROM 書籍資料表\"";
_strsql = "DELETE FROM 書籍資料表";
 //BA.debugLineNum = 103;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 104;BA.debugLine="ToastMessageShow(\"刪除全部「書籍資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除全部「書籍資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 105;BA.debugLine="QueryDBList(\"Select * FROM 書籍資料表\")      '呼叫顯示目前存在的書籍資料表之副程式";
_querydblist("Select * FROM 書籍資料表");
 };
 //BA.debugLineNum = 107;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitbookstore_click() throws Exception{
 //BA.debugLineNum = 146;BA.debugLine="Sub btnExitBookStore_Click";
 //BA.debugLineNum = 147;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 148;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 149;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _strsql = "";
String _strbookno = "";
String _strbookname = "";
 //BA.debugLineNum = 51;BA.debugLine="Sub btnInsert_Click  '新增「書籍」資料";
 //BA.debugLineNum = 52;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 53;BA.debugLine="Dim strBookNo As String =edtBookNo.Text             '將「書號」欄位內容指定給變數";
_strbookno = mostCurrent._edtbookno.getText();
 //BA.debugLineNum = 54;BA.debugLine="Dim strBookName As String = edtBookName.Text        '將「書名」欄位內容指定給變數";
_strbookname = mostCurrent._edtbookname.getText();
 //BA.debugLineNum = 56;BA.debugLine="If strBookNo=\"\" OR strBookName=\"\" Then";
if ((_strbookno).equals("") || (_strbookname).equals("")) { 
 //BA.debugLineNum = 57;BA.debugLine="Msgbox(\"您尚未完整輸入「書籍資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未完整輸入「書籍資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 59;BA.debugLine="strSQL = \"INSERT INTO 書籍資料表(書號,書名)\" & _              \"VALUES('\" & strBookNo & \"','\" & strBookName & \"')\"";
_strsql = "INSERT INTO 書籍資料表(書號,書名)"+"VALUES('"+_strbookno+"','"+_strbookname+"')";
 //BA.debugLineNum = 61;BA.debugLine="Check_BookNo_Exists(strBookNo)        '呼叫檢查「書號」是否有重複新增之副程式";
_check_bookno_exists(_strbookno);
 //BA.debugLineNum = 62;BA.debugLine="If Check_BookNo_Exist=True Then    '檢查是否有重複新增";
if (_check_bookno_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 63;BA.debugLine="Msgbox(\"您已經重複新增「書號」了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增「書號」了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 65;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 66;BA.debugLine="ToastMessageShow(\"您新增成功「書籍資料」記錄!\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您新增成功「書籍資料」記錄!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 68;BA.debugLine="Check_BookNo_Exist=False                                   '設定沒有重複新增「書號」";
_check_bookno_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 70;BA.debugLine="QueryDBList(\"Select * FROM 書籍資料表\")      '呼叫顯示目前存在的書籍資料表之副程式";
_querydblist("Select * FROM 書籍資料表");
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
String _strbookno = "";
String _strsql = "";
 //BA.debugLineNum = 108;BA.debugLine="Sub btnSelect_Click";
 //BA.debugLineNum = 109;BA.debugLine="If edtBookNo.Text=\"\" Then";
if ((mostCurrent._edtbookno.getText()).equals("")) { 
 //BA.debugLineNum = 110;BA.debugLine="Msgbox(\"您尚未輸入「書籍資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 112;BA.debugLine="Dim strBookNo As String =edtBookNo.Text";
_strbookno = mostCurrent._edtbookno.getText();
 //BA.debugLineNum = 113;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 114;BA.debugLine="strSQL = \"DELETE FROM 書籍資料表 WHERE 書號 = '\" & strBookNo & \"'\"";
_strsql = "DELETE FROM 書籍資料表 WHERE 書號 = '"+_strbookno+"'";
 //BA.debugLineNum = 115;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 116;BA.debugLine="ToastMessageShow(\"刪除一筆「書籍資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆「書籍資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="QueryDBList(\"Select * FROM 書籍資料表\")      '呼叫顯示目前存在的書籍資料表之副程式";
_querydblist("Select * FROM 書籍資料表");
 };
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _strbookno = "";
String _strsql = "";
 //BA.debugLineNum = 83;BA.debugLine="Sub btnUpdate_Click   '修改「書籍」資料";
 //BA.debugLineNum = 84;BA.debugLine="If edtBookNo.Text=\"\" Then";
if ((mostCurrent._edtbookno.getText()).equals("")) { 
 //BA.debugLineNum = 85;BA.debugLine="Msgbox(\"您尚未輸入「書籍資料」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「書籍資料」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 87;BA.debugLine="Dim strBookNo As String =edtBookNo.Text";
_strbookno = mostCurrent._edtbookno.getText();
 //BA.debugLineNum = 88;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 89;BA.debugLine="strSQL = \"UPDATE 書籍資料表 SET 書名 ='\" & edtBookName.Text & \"' WHERE 書號 = '\" & strBookNo & \"'\"";
_strsql = "UPDATE 書籍資料表 SET 書名 ='"+mostCurrent._edtbookname.getText()+"' WHERE 書號 = '"+_strbookno+"'";
 //BA.debugLineNum = 90;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 91;BA.debugLine="ToastMessageShow(\"更新一筆「書籍資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆「書籍資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 92;BA.debugLine="QueryDBList(\"Select * FROM 書籍資料表\")      '呼叫顯示目前存在的書籍資料表之副程式";
_querydblist("Select * FROM 書籍資料表");
 };
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _check_bookno_exists(String _strbookno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 73;BA.debugLine="Sub Check_BookNo_Exists(strBookNo As String)       '檢查「書號」是否有重複新增之副程式";
 //BA.debugLineNum = 74;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 75;BA.debugLine="SQL_Query=\"Select * FROM 書籍資料表 Where 書號='\" & strBookNo & \"' \"";
_sql_query = "Select * FROM 書籍資料表 Where 書號='"+_strbookno+"' ";
 //BA.debugLineNum = 76;BA.debugLine="Msgbox(SQL_Query,\"SQL\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_sql_query,"SQL",mostCurrent.activityBA);
 //BA.debugLineNum = 77;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 78;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「書籍資料表」是否已經存在一筆了";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 79;BA.debugLine="Check_BookNo_Exist=True                    '代表重複新增「書號」了!";
_check_bookno_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim edtBookNo As EditText           '書號";
mostCurrent._edtbookno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim edtBookName As EditText         '書名";
mostCurrent._edtbookname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnSelect As Button             '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim livBookStore As ListView          '顯示「書籍資料表」清單元件";
mostCurrent._livbookstore = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim btnExitBookStore As Button        '離開功能";
mostCurrent._btnexitbookstore = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_BookNo_Exist As Boolean=False    '檢查新增「書號」時是否重複";
_check_bookno_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 120;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 121;BA.debugLine="livBookStore.Clear '清單清空";
mostCurrent._livbookstore.Clear();
 //BA.debugLineNum = 122;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 123;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 124;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 125;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 126;BA.debugLine="livBookStore.SingleLineLayout.Label.TextSize=16";
mostCurrent._livbookstore.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 127;BA.debugLine="livBookStore.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livbookstore.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 128;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step105 = 1;
final int limit105 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step105 > 0 && _i <= limit105) || (step105 < 0 && _i >= limit105); _i = ((int)(0 + _i + step105))) {
 //BA.debugLineNum = 129;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"                \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"                ";
 //BA.debugLineNum = 130;BA.debugLine="strLine=strLine & \"================\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"================";
 }
};
 //BA.debugLineNum = 132;BA.debugLine="livBookStore.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」";
mostCurrent._livbookstore.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 133;BA.debugLine="livBookStore.AddSingleLine (strLine)          '顯示「分隔水平線」";
mostCurrent._livbookstore.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 134;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step111 = 1;
final int limit111 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step111 > 0 && _i <= limit111) || (step111 < 0 && _i >= limit111); _i = ((int)(0 + _i + step111))) {
 //BA.debugLineNum = 135;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 136;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step113 = 1;
final int limit113 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step113 > 0 && _j <= limit113) || (step113 < 0 && _j >= limit113); _j = ((int)(0 + _j + step113))) {
 //BA.debugLineNum = 137;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 139;BA.debugLine="livBookStore.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livbookstore.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent);
 //BA.debugLineNum = 140;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 142;BA.debugLine="ToastMessageShow(\"目前的書籍記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前的書籍記錄共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 143;BA.debugLine="lblResult.Text =\"===書籍清單共有( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===書籍清單共有( "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
}
