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

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "Leechphd.myebook.B4AtoSQLServer2", "Leechphd.myebook.B4AtoSQLServer2.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leechphd.myebook.B4AtoSQLServer2.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (main) Resume **");
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
public static String _password = "";
public static String _dns1 = "";
public static String _dns2 = "";
public static String _serverurl = "";
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtpassword = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlogin = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmodelone = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmodeltwo = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbookmanager = null;
public static String _strsql = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitapp = null;
public anywheresoftware.b4a.objects.collections.JSONParser _json = null;
public static String _response = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdatesqlite = null;
public Leechphd.myebook.B4AtoSQLServer2.httputils _httputils = null;
public Leechphd.myebook.B4AtoSQLServer2.httputilsservice _httputilsservice = null;
public Leechphd.myebook.B4AtoSQLServer2.myebook _myebook = null;
public Leechphd.myebook.B4AtoSQLServer2.bookstore _bookstore = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (myebook.mostCurrent != null);
vis = vis | (bookstore.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 41;BA.debugLine="If File.Exists(File.DirDefaultExternal, \"MyeBookDB.sqlite\") = False Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeBookDB.sqlite")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 43;BA.debugLine="File.Copy(File.DirAssets, \"MyeBookDB.sqlite\",File.DirDefaultExternal, \"MyeBookDB.sqlite\")";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"MyeBookDB.sqlite",anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeBookDB.sqlite");
 };
 //BA.debugLineNum = 45;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 46;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeBookDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeBookDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 48;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 49;BA.debugLine="Activity.Title =\"B4A連結MySQL(手動更新SQLite)\"    '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("B4A連結MySQL(手動更新SQLite)"));
 //BA.debugLineNum = 50;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 51;BA.debugLine="HttpUtils.CallbackActivity = \"Myebook\"";
mostCurrent._httputils._callbackactivity = "Myebook";
 //BA.debugLineNum = 52;BA.debugLine="HttpUtils.CallbackJobDoneSub = \"JobDone\"";
mostCurrent._httputils._callbackjobdonesub = "JobDone";
 };
 //BA.debugLineNum = 54;BA.debugLine="StrSQL=\"select * from Myebook\"";
mostCurrent._strsql = "select * from Myebook";
 //BA.debugLineNum = 56;BA.debugLine="HttpUtils.PostString(\"LeechJob\", ServerUrl, StrSQL)";
mostCurrent._httputils._poststring(mostCurrent.activityBA,"LeechJob",_serverurl,mostCurrent._strsql);
 //BA.debugLineNum = 57;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _btnbookmanager_click() throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub btnBookManager_Click";
 //BA.debugLineNum = 121;BA.debugLine="StartActivity(BookStore)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._bookstore.getObject()));
 //BA.debugLineNum = 122;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitapp_click() throws Exception{
String _msg_value = "";
 //BA.debugLineNum = 124;BA.debugLine="Sub btnExitApp_Click";
 //BA.debugLineNum = 125;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 126;BA.debugLine="Msg_Value = Msgbox2(\"您確定要結束本系統嗎?\", \"行動電子書系統APP\", \"確認\", \"\", \"取消\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要結束本系統嗎?","行動電子書系統APP","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 127;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 128;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 129;BA.debugLine="ExitApplication    '離開";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 //BA.debugLineNum = 131;BA.debugLine="End Sub";
return "";
}
public static String  _btnlogin_click() throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub btnLogin_Click";
 //BA.debugLineNum = 102;BA.debugLine="If edtPassword.Text=\"\" Then";
if ((mostCurrent._edtpassword.getText()).equals("")) { 
 //BA.debugLineNum = 103;BA.debugLine="Msgbox(\"您尚未填入密碼\",\"錯誤訊息\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未填入密碼","錯誤訊息",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 105;BA.debugLine="Password = edtPassword.Text";
_password = mostCurrent._edtpassword.getText();
 //BA.debugLineNum = 106;BA.debugLine="StartActivity(Myebook)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._myebook.getObject()));
 };
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _btnmodelone_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 110;BA.debugLine="Sub btnModelone_Click '瀏覽遠端SQL Server資料庫(會員)";
 //BA.debugLineNum = 111;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 112;BA.debugLine="i.Initialize(i.ACTION_VIEW, DNS1)";
_i.Initialize(_i.ACTION_VIEW,_dns1);
 //BA.debugLineNum = 113;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _btnmodeltwo_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 115;BA.debugLine="Sub btnModeltwo_Click '瀏覽遠端SQL Server資料庫(書籍)";
 //BA.debugLineNum = 116;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 117;BA.debugLine="i.Initialize(i.ACTION_VIEW, DNS2)";
_i.Initialize(_i.ACTION_VIEW,_dns2);
 //BA.debugLineNum = 118;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdatesqlite_click() throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub btnUpdateSQLite_Click '更新SQLite";
 //BA.debugLineNum = 60;BA.debugLine="If HttpUtils.Complete = True Then";
if (mostCurrent._httputils._complete==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 61;BA.debugLine="JobDone(HttpUtils.Job) '呼叫JobDone副程式";
_jobdone(mostCurrent._httputils._job);
 };
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _delete_myebook() throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub Delete_Myebook() '刪除本機端的電子書";
 //BA.debugLineNum = 83;BA.debugLine="Dim StrSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 84;BA.debugLine="StrSQL = \"DELETE FROM 書籍資料表\"  '撰寫刪除「書籍資料表」的SQL指令";
mostCurrent._strsql = "DELETE FROM 書籍資料表";
 //BA.debugLineNum = 85;BA.debugLine="SQLCmd.ExecNonQuery(StrSQL)        '執行SQL指令";
_sqlcmd.ExecNonQuery(mostCurrent._strsql);
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
httputils._process_globals();
httputilsservice._process_globals();
myebook._process_globals();
bookstore._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 26;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Dim Label1 As Label             'B4A讀取SQL Server資料(檢查會員身份)";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim edtPassword As EditText     '輸入使用者密碼";
mostCurrent._edtpassword = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnLogin As Button          '檢查會員身份";
mostCurrent._btnlogin = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim btnModelone As Button       '遠端DB(會員)";
mostCurrent._btnmodelone = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim btnModeltwo As Button       '遠端DB(書籍)";
mostCurrent._btnmodeltwo = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim btnBookManager As Button    '書籍資料管理";
mostCurrent._btnbookmanager = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim StrSQL As String            '撰寫SQL指令";
mostCurrent._strsql = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim btnExitApp As Button        '離開本系統";
mostCurrent._btnexitapp = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim JSON As JSONParser          'JSONParser物件是用來剖析JSON資料(類似XML)";
mostCurrent._json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 36;BA.debugLine="Dim response As String          '取得Web Service的回覆";
mostCurrent._response = "";
 //BA.debugLineNum = 37;BA.debugLine="Dim btnUpdateSQLite As Button";
mostCurrent._btnupdatesqlite = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(String _job) throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Sub JobDone (Job As String)";
 //BA.debugLineNum = 74;BA.debugLine="If HttpUtils.IsSuccess(ServerUrl) Then";
if (mostCurrent._httputils._issuccess(mostCurrent.activityBA,_serverurl)) { 
 //BA.debugLineNum = 75;BA.debugLine="response = HttpUtils.GetString(ServerUrl) '取得Web Service的回覆";
mostCurrent._response = mostCurrent._httputils._getstring(mostCurrent.activityBA,_serverurl);
 //BA.debugLineNum = 76;BA.debugLine="JSON.Initialize(response)  '用來剖析「Web Service的回覆」資料格式內容";
mostCurrent._json.Initialize(mostCurrent._response);
 };
 //BA.debugLineNum = 78;BA.debugLine="Delete_Myebook  '呼叫刪除本機端的電子書";
_delete_myebook();
 //BA.debugLineNum = 79;BA.debugLine="Update_Myebook  '呼叫新增遠端匯入的最新電子書";
_update_myebook();
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim SQLCmd As SQL                '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 17;BA.debugLine="Dim Password As String           '宣告使用者密碼";
_password = "";
 //BA.debugLineNum = 18;BA.debugLine="Dim DNS1,DNS2 As String          '宣告兩個領域名稱";
_dns1 = "";
_dns2 = "";
 //BA.debugLineNum = 19;BA.debugLine="Dim ServerUrl As String          '宣告Web Service的網址";
_serverurl = "";
 //BA.debugLineNum = 21;BA.debugLine="DNS1 = \"http://120.118.165.192:81/B4ASQL/b4asql-1.php?sqlre=Select * from Member\"";
_dns1 = "http://120.118.165.192:81/B4ASQL/b4asql-1.php?sqlre=Select * from Member";
 //BA.debugLineNum = 22;BA.debugLine="DNS2 = \"http://120.118.165.192:81/B4ASQL/b4asql-1.php?sqlre=Select * from Myebook\"";
_dns2 = "http://120.118.165.192:81/B4ASQL/b4asql-1.php?sqlre=Select * from Myebook";
 //BA.debugLineNum = 23;BA.debugLine="ServerUrl = \"http://120.118.165.192:81/B4ASQL/b4asql.php\"";
_serverurl = "http://120.118.165.192:81/B4ASQL/b4asql.php";
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _update_myebook() throws Exception{
anywheresoftware.b4a.objects.collections.List _arrayrows = null;
anywheresoftware.b4a.objects.collections.Map _key_value = null;
int _i = 0;
 //BA.debugLineNum = 88;BA.debugLine="Sub Update_Myebook() '新增遠端匯入的最新電子書";
 //BA.debugLineNum = 89;BA.debugLine="Dim StrSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 90;BA.debugLine="Dim ArrayRows As List            'ArrayRows視為陣列";
_arrayrows = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 91;BA.debugLine="Dim Key_Value As Map             'Key_Value視為成對的(key和Value)";
_key_value = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 92;BA.debugLine="ArrayRows = JSON.NextArray()     '將取得的JSON資料剖析成List串列(亦即資料列)";
_arrayrows = mostCurrent._json.NextArray();
 //BA.debugLineNum = 93;BA.debugLine="For i = 0 To  ArrayRows.Size - 1  '從資料列再分割成許多欄位值";
{
final int step65 = 1;
final int limit65 = (int) (_arrayrows.getSize()-1);
for (_i = (int) (0); (step65 > 0 && _i <= limit65) || (step65 < 0 && _i >= limit65); _i = ((int)(0 + _i + step65))) {
 //BA.debugLineNum = 94;BA.debugLine="Key_Value =  ArrayRows.Get(i)";
_key_value.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_arrayrows.Get(_i)));
 //BA.debugLineNum = 95;BA.debugLine="StrSQL = \"INSERT INTO 書籍資料表(書號,書名) VALUES('\" & Key_Value.Get(\"Book_No\") & \"','\" & Key_Value.Get(\"Book_Name\") & \"')\"";
mostCurrent._strsql = "INSERT INTO 書籍資料表(書號,書名) VALUES('"+BA.ObjectToString(_key_value.Get((Object)("Book_No")))+"','"+BA.ObjectToString(_key_value.Get((Object)("Book_Name")))+"')";
 //BA.debugLineNum = 96;BA.debugLine="SQLCmd.ExecNonQuery(StrSQL)           '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(mostCurrent._strsql);
 }
};
 //BA.debugLineNum = 98;BA.debugLine="ToastMessageShow(\"您更新電子書!\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您更新電子書!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
}
