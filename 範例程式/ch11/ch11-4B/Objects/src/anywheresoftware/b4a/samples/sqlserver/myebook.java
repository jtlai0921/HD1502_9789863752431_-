package anywheresoftware.b4a.samples.sqlserver;

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
			processBA = new BA(this.getApplicationContext(), null, null, "anywheresoftware.b4a.samples.sqlserver", "anywheresoftware.b4a.samples.sqlserver.myebook");
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
		activityBA = new BA(this, layout, processBA, "anywheresoftware.b4a.samples.sqlserver", "anywheresoftware.b4a.samples.sqlserver.myebook");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "anywheresoftware.b4a.samples.sqlserver.myebook", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
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
public static anywheresoftware.b4a.objects.collections.JSONParser _json = null;
public static String _response = "";
public static String _strsql = "";
public static String _serverurl = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblshowebook = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livebook = null;
public anywheresoftware.b4a.samples.sqlserver.main _main = null;
public anywheresoftware.b4a.samples.sqlserver.httputils _httputils = null;
public anywheresoftware.b4a.samples.sqlserver.httputilsservice _httputilsservice = null;
public anywheresoftware.b4a.samples.sqlserver.member _member = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 24;BA.debugLine="Activity.LoadLayout(\"Myebook\")                         '使用「Main(版面配置檔)」來輸出";
mostCurrent._activity.LoadLayout("Myebook",mostCurrent.activityBA);
 //BA.debugLineNum = 25;BA.debugLine="Activity.Title =\"歡迎閱讀電子書\"    '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("歡迎閱讀電子書"));
 //BA.debugLineNum = 26;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 27;BA.debugLine="HttpUtils.CallbackActivity = \"Myebook\"      '設定回覆的活動頁面名稱";
mostCurrent._httputils._callbackactivity = "Myebook";
 //BA.debugLineNum = 28;BA.debugLine="HttpUtils.CallbackJobDoneSub = \"JobDone\"    '設定當HTTP完成下載工作之後，所要觸發的事件名稱";
mostCurrent._httputils._callbackjobdonesub = "JobDone";
 };
 //BA.debugLineNum = 30;BA.debugLine="StrSQL=\"select * from Myebook\"";
_strsql = "select * from Myebook";
 //BA.debugLineNum = 32;BA.debugLine="lblResult.Text=\"電子書載入中...\"";
mostCurrent._lblresult.setText((Object)("電子書載入中..."));
 //BA.debugLineNum = 33;BA.debugLine="lblShowebook.Visible=False";
mostCurrent._lblshowebook.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 34;BA.debugLine="HttpUtils.PostString(\"MyJob2\", ServerUrl, StrSQL)";
mostCurrent._httputils._poststring(mostCurrent.activityBA,"MyJob2",_serverurl,_strsql);
 //BA.debugLineNum = 35;BA.debugLine="Listebook  '顯示書單";
_listebook();
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 81;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 74;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 70;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 71;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Dim btnReturn As Button    '返回鈕";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim lblResult As Label     '顯示「會員身份審查結果」標題字";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim lblShowebook As Label  '顯示「請點選喜歡的電子書」標題字";
mostCurrent._lblshowebook = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim livebook As ListView   '用來顯示電子書的清單內容";
mostCurrent._livebook = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(String _job) throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub JobDone (Job As String)";
 //BA.debugLineNum = 39;BA.debugLine="If HttpUtils.IsSuccess(Main.ServerUrl) Then        '當HTTP完成下載成功";
if (mostCurrent._httputils._issuccess(mostCurrent.activityBA,mostCurrent._main._serverurl)) { 
 //BA.debugLineNum = 40;BA.debugLine="ToastMessageShow(\"連線成功!!\", True)  '顯示資料載入完成!!";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("連線成功!!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 41;BA.debugLine="response = HttpUtils.GetString(ServerUrl) '取得Web Service的回覆";
_response = mostCurrent._httputils._getstring(mostCurrent.activityBA,_serverurl);
 //BA.debugLineNum = 42;BA.debugLine="JSON.Initialize(response)                 '用來剖析「Web Service的回覆」資料格式內容";
_json.Initialize(_response);
 }else {
 //BA.debugLineNum = 44;BA.debugLine="ToastMessageShow(\"連線失敗!!\", True)      '顯示載入失敗!!";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("連線失敗!!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _listebook() throws Exception{
anywheresoftware.b4a.objects.collections.List _arrayrows = null;
anywheresoftware.b4a.objects.collections.Map _key_value = null;
int _i = 0;
 //BA.debugLineNum = 48;BA.debugLine="Sub Listebook()";
 //BA.debugLineNum = 49;BA.debugLine="livebook.SingleLineLayout.Label.TextSize=20";
mostCurrent._livebook.getSingleLineLayout().Label.setTextSize((float) (20));
 //BA.debugLineNum = 50;BA.debugLine="livebook.SingleLineLayout.ItemHeight = 25dip";
mostCurrent._livebook.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (25)));
 //BA.debugLineNum = 51;BA.debugLine="Dim ArrayRows As List            'ArrayRows視為陣列";
_arrayrows = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 52;BA.debugLine="Dim Key_Value As Map             'Key_Value視為成對的(key和Value)";
_key_value = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 53;BA.debugLine="ArrayRows = JSON.NextArray()     '將取得的JSON資料剖析成List串列(亦即資料列)";
_arrayrows = _json.NextArray();
 //BA.debugLineNum = 54;BA.debugLine="For i = 0 To ArrayRows.Size - 1  '顯示全部的記錄（資料列）";
{
final int step41 = 1;
final int limit41 = (int) (_arrayrows.getSize()-1);
for (_i = (int) (0); (step41 > 0 && _i <= limit41) || (step41 < 0 && _i >= limit41); _i = ((int)(0 + _i + step41))) {
 //BA.debugLineNum = 55;BA.debugLine="Key_Value = ArrayRows.Get(i) '取得每一筆記錄";
_key_value.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_arrayrows.Get(_i)));
 //BA.debugLineNum = 56;BA.debugLine="livebook.AddSingleLine((i+1) & \". 書名：\" & Key_Value.Get(\"Book_Name\")) '顯示記錄的中的「Book_Name」欄位值";
mostCurrent._livebook.AddSingleLine(BA.NumberToString((_i+1))+". 書名："+BA.ObjectToString(_key_value.Get((Object)("Book_Name"))));
 }
};
 //BA.debugLineNum = 58;BA.debugLine="lblShowebook.Visible=True";
mostCurrent._lblshowebook.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 59;BA.debugLine="lblResult.Text=\"電子書載入完成！！\"";
mostCurrent._lblresult.setText((Object)("電子書載入完成！！"));
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _livebook_itemclick(int _position,Object _value) throws Exception{
String _temp = "";
 //BA.debugLineNum = 62;BA.debugLine="Sub livebook_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 63;BA.debugLine="Dim Temp As String";
_temp = "";
 //BA.debugLineNum = 64;BA.debugLine="Temp = \"你剛才點選的電子書為：\" & CRLF";
_temp = "你剛才點選的電子書為："+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 65;BA.debugLine="Temp = Temp & livebook.GetItem(Position)";
_temp = _temp+BA.ObjectToString(mostCurrent._livebook.GetItem(_position));
 //BA.debugLineNum = 66;BA.debugLine="lblShowebook.Text =Temp";
mostCurrent._lblshowebook.setText((Object)(_temp));
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 8;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Dim JSON As JSONParser     'JSONParser物件是用來剖析JSON資料(類似XML)";
_json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 10;BA.debugLine="Dim response As String     '取得Web Service的回覆";
_response = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim StrSQL As String       '宣告SQL的查詢指令字串";
_strsql = "";
 //BA.debugLineNum = 12;BA.debugLine="Dim ServerUrl As String    '宣告Web Service的網址";
_serverurl = "";
 //BA.debugLineNum = 13;BA.debugLine="ServerUrl = \"http://120.118.165.192:81/B4ASQL/b4asql.php\"";
_serverurl = "http://120.118.165.192:81/B4ASQL/b4asql.php";
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
}
