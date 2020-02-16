package Leech.myebook.ch12.example;

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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch12.example", "Leech.myebook.ch12.example.main");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch12.example", "Leech.myebook.ch12.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch12.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
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
public static String _dns = "";
public static String _bookstore = "";
public anywheresoftware.b4a.objects.ListViewWrapper _livbookstores = null;
public Leech.myebook.ch12.example.bookstores _bookstores = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"BookStore",_bookstore,"BookStores",Debug.moduleToString(Leech.myebook.ch12.example.bookstores.class),"DNS",_dns,"livBookStores",mostCurrent._livbookstores};
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (bookstores.mostCurrent != null);
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

 {
            Activity __a = null;
            if (bookstores.previousOne != null) {
				__a = bookstores.previousOne.get();
			}
            else {
                BA ba = bookstores.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
public static String  _aboutapp_click() throws Exception{
		Debug.PushSubsStack("AboutApp_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _about = "";
 BA.debugLineNum = 46;BA.debugLine="Sub AboutApp_Click";
Debug.ShouldStop(8192);
 BA.debugLineNum = 47;BA.debugLine="Dim About As String";
Debug.ShouldStop(16384);
_about = "";Debug.locals.put("About", _about);
 BA.debugLineNum = 48;BA.debugLine="About=\"網路書店APP\" & CRLF & \"設計者：李春雄老師\"";
Debug.ShouldStop(32768);
_about = "網路書店APP"+anywheresoftware.b4a.keywords.Common.CRLF+"設計者：李春雄老師";Debug.locals.put("About", _about);
 BA.debugLineNum = 49;BA.debugLine="Msgbox(About, \"關於本系統\")";
Debug.ShouldStop(65536);
anywheresoftware.b4a.keywords.Common.Msgbox(_about,"關於本系統",mostCurrent.activityBA);
 BA.debugLineNum = 50;BA.debugLine="End Sub";
Debug.ShouldStop(131072);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 24;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(8388608);
 BA.debugLineNum = 25;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(16777216);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 26;BA.debugLine="Activity.Title =\"網路書店APP\"";
Debug.ShouldStop(33554432);
mostCurrent._activity.setTitle((Object)("網路書店APP"));
 BA.debugLineNum = 27;BA.debugLine="Activity.AddMenuItem(\"設計者：李春雄老師\", \"AboutApp\")";
Debug.ShouldStop(67108864);
mostCurrent._activity.AddMenuItem("設計者：李春雄老師","AboutApp");
 BA.debugLineNum = 28;BA.debugLine="Activity.AddMenuItem(\"結束\", \"ExitApp\")";
Debug.ShouldStop(134217728);
mostCurrent._activity.AddMenuItem("結束","ExitApp");
 BA.debugLineNum = 29;BA.debugLine="livBookStores.AddSingleLine2(\"上奇資訊\",\"http://www.grandtech.info/\")";
Debug.ShouldStop(268435456);
mostCurrent._livbookstores.AddSingleLine2("上奇資訊",(Object)("http://www.grandtech.info/"));
 BA.debugLineNum = 30;BA.debugLine="livBookStores.AddSingleLine2(\"全華圖書\",\"http://www.chwa.com.tw/NEWun/about.asp\")";
Debug.ShouldStop(536870912);
mostCurrent._livbookstores.AddSingleLine2("全華圖書",(Object)("http://www.chwa.com.tw/NEWun/about.asp"));
 BA.debugLineNum = 31;BA.debugLine="livBookStores.AddSingleLine2(\"滄海圖書\",\"http://www.tsanghai.com.tw/\")";
Debug.ShouldStop(1073741824);
mostCurrent._livbookstores.AddSingleLine2("滄海圖書",(Object)("http://www.tsanghai.com.tw/"));
 BA.debugLineNum = 32;BA.debugLine="livBookStores.AddSingleLine2(\"碁峰資訊\",\"http://www.gotop.com.tw/\")";
Debug.ShouldStop(-2147483648);
mostCurrent._livbookstores.AddSingleLine2("碁峰資訊",(Object)("http://www.gotop.com.tw/"));
 BA.debugLineNum = 33;BA.debugLine="livBookStores.AddSingleLine2(\"文京出版社\",\"http://www.wun-ching.com.tw/\")";
Debug.ShouldStop(1);
mostCurrent._livbookstores.AddSingleLine2("文京出版社",(Object)("http://www.wun-ching.com.tw/"));
 BA.debugLineNum = 34;BA.debugLine="livBookStores.AddSingleLine2(\"台科大圖書\",\"http://www.tiked.com.tw/\")";
Debug.ShouldStop(2);
mostCurrent._livbookstores.AddSingleLine2("台科大圖書",(Object)("http://www.tiked.com.tw/"));
 BA.debugLineNum = 35;BA.debugLine="livBookStores.AddSingleLine2(\"交通查詢\",\"http://twtraffic.tra.gov.tw/twrail/mobile/home.aspx\")";
Debug.ShouldStop(4);
mostCurrent._livbookstores.AddSingleLine2("交通查詢",(Object)("http://twtraffic.tra.gov.tw/twrail/mobile/home.aspx"));
 BA.debugLineNum = 36;BA.debugLine="End Sub";
Debug.ShouldStop(8);
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
 BA.debugLineNum = 42;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(512);
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
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 38;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(32);
 BA.debugLineNum = 40;BA.debugLine="End Sub";
Debug.ShouldStop(128);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _exitapp_click() throws Exception{
		Debug.PushSubsStack("ExitApp_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _msg_value = "";
 BA.debugLineNum = 52;BA.debugLine="Sub ExitApp_Click";
Debug.ShouldStop(524288);
 BA.debugLineNum = 53;BA.debugLine="Dim Msg_Value As String";
Debug.ShouldStop(1048576);
_msg_value = "";Debug.locals.put("Msg_Value", _msg_value);
 BA.debugLineNum = 54;BA.debugLine="Msg_Value = Msgbox2(\"您確定要結束本系統嗎?\", \"網路書店APP\", \"確認\", \"\", \"取消\", LoadBitmap(File.DirAssets,\"Leech_icon.jpg\"))";
Debug.ShouldStop(2097152);
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要結束本系統嗎?","網路書店APP","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Leech_icon.jpg").getObject()),mostCurrent.activityBA));Debug.locals.put("Msg_Value", _msg_value);
 BA.debugLineNum = 55;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
Debug.ShouldStop(4194304);
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 BA.debugLineNum = 56;BA.debugLine="Activity.Finish()  '活動完成";
Debug.ShouldStop(8388608);
mostCurrent._activity.Finish();
 BA.debugLineNum = 57;BA.debugLine="ExitApplication    '離開";
Debug.ShouldStop(16777216);
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 BA.debugLineNum = 59;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
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
Debug.StartDebugging(mostCurrent.activityBA, 21155, new int[] {3, 2}, "22b2dbc9-bd17-4e1f-9c47-05b133379ef0");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
bookstores._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim livBookStores As ListView  '網路書店清單";
mostCurrent._livbookstores = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _livbookstores_itemclick(int _position,Object _value) throws Exception{
		Debug.PushSubsStack("livBookStores_ItemClick (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("Position", _position);
Debug.locals.put("Value", _value);
 BA.debugLineNum = 61;BA.debugLine="Sub livBookStores_ItemClick (Position As Int, Value As Object)";
Debug.ShouldStop(268435456);
 BA.debugLineNum = 62;BA.debugLine="DNS=Value            '取得領域名稱";
Debug.ShouldStop(536870912);
_dns = BA.ObjectToString(_value);
 BA.debugLineNum = 63;BA.debugLine="Select Position      '取得出版社名稱";
Debug.ShouldStop(1073741824);
switch (_position) {
case 0:
 BA.debugLineNum = 64;BA.debugLine="Case 0: BookStore = \"上奇資訊\"";
Debug.ShouldStop(-2147483648);
_bookstore = "上奇資訊";
 break;
case 1:
 BA.debugLineNum = 65;BA.debugLine="Case 1: BookStore = \"全華圖書\"";
Debug.ShouldStop(1);
_bookstore = "全華圖書";
 break;
case 2:
 BA.debugLineNum = 66;BA.debugLine="Case 2: BookStore = \"滄海圖書\"";
Debug.ShouldStop(2);
_bookstore = "滄海圖書";
 break;
case 3:
 BA.debugLineNum = 67;BA.debugLine="Case 3: BookStore = \"碁峰資訊\"";
Debug.ShouldStop(4);
_bookstore = "碁峰資訊";
 break;
case 4:
 BA.debugLineNum = 68;BA.debugLine="Case 4: BookStore = \"文京出版社\"";
Debug.ShouldStop(8);
_bookstore = "文京出版社";
 break;
case 5:
 BA.debugLineNum = 69;BA.debugLine="Case 5: BookStore = \"台科大圖書\"";
Debug.ShouldStop(16);
_bookstore = "台科大圖書";
 break;
case 6:
 BA.debugLineNum = 70;BA.debugLine="Case 6: BookStore = \"交通查詢\"";
Debug.ShouldStop(32);
_bookstore = "交通查詢";
 break;
}
;
 BA.debugLineNum = 72;BA.debugLine="StartActivity(\"BookStores\")";
Debug.ShouldStop(128);
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("BookStores"));
 BA.debugLineNum = 73;BA.debugLine="End Sub";
Debug.ShouldStop(256);
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
 //BA.debugLineNum = 16;BA.debugLine="Dim DNS As String        '領域名稱";
_dns = "";
 //BA.debugLineNum = 17;BA.debugLine="Dim BookStore As String  '出版社名稱";
_bookstore = "";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
