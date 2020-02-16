package b4a.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
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
public static anywheresoftware.b4a.samples.httputils2.httpjob _myhttpjob = null;
public static anywheresoftware.b4a.objects.SaxParser _xmlparser = null;
public static String _opendataurl = "";
public anywheresoftware.b4a.objects.ListViewWrapper _listview1 = null;
public anywheresoftware.b4a.objects.collections.Map _key_value = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"HttpUtils2Service",mostCurrent._httputils2service,"Key_Value",mostCurrent._key_value,"ListView1",mostCurrent._listview1,"MyHttpJob",_myhttpjob,"OpenDataUrl",_opendataurl,"XMLParser",_xmlparser};
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}

public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = main.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
public static String  _activity_create(boolean _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(67108864);
 BA.debugLineNum = 28;BA.debugLine="Activity.LoadLayout(\"Main\")	             '指定Main主程式使用Main.bal版面配置檔。";
Debug.ShouldStop(134217728);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 29;BA.debugLine="Activity.Title =\"紫外線即時監測資料XML\"  '設定「標題名稱」為“紫外線即時監測資料JSON”。";
Debug.ShouldStop(268435456);
mostCurrent._activity.setTitle((Object)("紫外線即時監測資料XML"));
 BA.debugLineNum = 30;BA.debugLine="XMLParser.Initialize    'XMLParser物件初始化";
Debug.ShouldStop(536870912);
_xmlparser.Initialize(processBA);
 BA.debugLineNum = 31;BA.debugLine="Key_Value.Initialize    'Key_Value物件初始化";
Debug.ShouldStop(1073741824);
mostCurrent._key_value.Initialize();
 BA.debugLineNum = 32;BA.debugLine="MyHttpJob.Initialize(\"XML\", Me) '初始化";
Debug.ShouldStop(-2147483648);
_myhttpjob._initialize(processBA,"XML",main.getObject());
 BA.debugLineNum = 33;BA.debugLine="MyHttpJob.Download(OpenDataUrl) '下載xml網站資料";
Debug.ShouldStop(1);
_myhttpjob._download(_opendataurl);
 BA.debugLineNum = 34;BA.debugLine="End Sub";
Debug.ShouldStop(2);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 60;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(134217728);
 BA.debugLineNum = 62;BA.debugLine="End Sub";
Debug.ShouldStop(536870912);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 56;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(8388608);
 BA.debugLineNum = 58;BA.debugLine="End Sub";
Debug.ShouldStop(33554432);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}

public static void initializeProcessGlobals() {
    if (mostCurrent != null && mostCurrent.activityBA != null) {
Debug.StartDebugging(mostCurrent.activityBA, 21356, new int[] {2}, "279706a3-e042-4ac6-a63c-d04dc1fccf53");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim ListView1 As ListView   '宣告ListView1變數，用來顯示「各縣市紫外線指數」內容";
mostCurrent._listview1 = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Key_Value As Map        '宣告變數Key_Value視為成對的(key和Value)";
mostCurrent._key_value = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
		Debug.PushSubsStack("JobDone (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("job", _job);
 BA.debugLineNum = 46;BA.debugLine="Sub JobDone(job As HttpJob)";
Debug.ShouldStop(8192);
 BA.debugLineNum = 47;BA.debugLine="If job.Success Then '假如下載成功";
Debug.ShouldStop(16384);
if (_job._success) { 
 BA.debugLineNum = 48;BA.debugLine="ToastMessageShow(\"資料載入完成!!\", True)       '顯示資料載入完成!!";
Debug.ShouldStop(32768);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("資料載入完成!!",anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 49;BA.debugLine="XMLParser.Parse(job.GetInputStream, \"Parser\")  '讀取並解析下載的XML資源";
Debug.ShouldStop(65536);
_xmlparser.Parse((java.io.InputStream)(_job._getinputstream().getObject()),"Parser");
 }else {
 BA.debugLineNum = 51;BA.debugLine="Msgbox(job.ErrorMessage, \"Error\") '顯示Error";
Debug.ShouldStop(262144);
anywheresoftware.b4a.keywords.Common.Msgbox(_job._errormessage,"Error",mostCurrent.activityBA);
 };
 BA.debugLineNum = 53;BA.debugLine="End Sub";
Debug.ShouldStop(1048576);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _parser_endelement(String _uri,String _name,anywheresoftware.b4a.keywords.StringBuilderWrapper _text) throws Exception{
		Debug.PushSubsStack("parser_EndElement (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("Uri", _uri);
Debug.locals.put("Name", _name);
Debug.locals.put("Text", _text);
 BA.debugLineNum = 36;BA.debugLine="Sub parser_EndElement (Uri As String, Name As String, Text As StringBuilder)";
Debug.ShouldStop(8);
 BA.debugLineNum = 37;BA.debugLine="If Name=\"SiteName\" Then";
Debug.ShouldStop(16);
if ((_name).equals("SiteName")) { 
 BA.debugLineNum = 38;BA.debugLine="Key_Value.Put(\"SiteName\", Text.ToString) '獲得資訊並轉成字串";
Debug.ShouldStop(32);
mostCurrent._key_value.Put((Object)("SiteName"),(Object)(_text.ToString()));
 }else 
{ BA.debugLineNum = 39;BA.debugLine="Else If Name=\"UVI\" Then";
Debug.ShouldStop(64);
if ((_name).equals("UVI")) { 
 BA.debugLineNum = 40;BA.debugLine="Key_Value.Put(\"UVI\", Text.ToString)      '獲得資訊並轉成字串";
Debug.ShouldStop(128);
mostCurrent._key_value.Put((Object)("UVI"),(Object)(_text.ToString()));
 BA.debugLineNum = 41;BA.debugLine="ListView1.AddTwoLines(Key_Value.Get(\"SiteName\"),\"紫外線指數：\" & Key_Value.Get(\"UVI\"))'擷取Key_Value的特定資料並寫入到ListView1";
Debug.ShouldStop(256);
mostCurrent._listview1.AddTwoLines(BA.ObjectToString(mostCurrent._key_value.Get((Object)("SiteName"))),"紫外線指數："+BA.ObjectToString(mostCurrent._key_value.Get((Object)("UVI"))));
 BA.debugLineNum = 42;BA.debugLine="Key_Value.Clear  '擷取Key_Value的特定資料清空";
Debug.ShouldStop(512);
mostCurrent._key_value.Clear();
 }};
 BA.debugLineNum = 44;BA.debugLine="End Sub";
Debug.ShouldStop(2048);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim MyHttpJob As HttpJob    '使用HTTP函式庫可以在網路上交換資料";
_myhttpjob = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 17;BA.debugLine="Dim XMLParser As SaxParser	'SaxParser物件是用來剖析XML資料。";
_xmlparser = new anywheresoftware.b4a.objects.SaxParser();
 //BA.debugLineNum = 18;BA.debugLine="Dim OpenDataUrl As String   '宣告下載網址的字串變數";
_opendataurl = "";
 //BA.debugLineNum = 19;BA.debugLine="OpenDataUrl = \"http://opendata.epa.gov.tw/ws/Data/UV/?format=xml\"";
_opendataurl = "http://opendata.epa.gov.tw/ws/Data/UV/?format=xml";
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
}
