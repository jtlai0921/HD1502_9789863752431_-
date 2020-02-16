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
public anywheresoftware.b4a.objects.LabelWrapper _lblnum1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnum2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnequal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnnotequal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsmaller = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnsmallerequal = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btngreater = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btngreaterequal = null;
public static String _result = "";
public static int _a = 0;
public static int _b = 0;
  public Object[] GetGlobals() {
		return new Object[] {"A",_a,"Activity",mostCurrent._activity,"B",_b,"btnEqual",mostCurrent._btnequal,"btnGreater",mostCurrent._btngreater,"btnGreaterEqual",mostCurrent._btngreaterequal,"btnNotEqual",mostCurrent._btnnotequal,"btnSmaller",mostCurrent._btnsmaller,"btnSmallerEqual",mostCurrent._btnsmallerequal,"lblNum1",mostCurrent._lblnum1,"lblNum2",mostCurrent._lblnum2,"lblResult",mostCurrent._lblresult,"Result",mostCurrent._result};
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
 BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(8);
 BA.debugLineNum = 37;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(16);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 38;BA.debugLine="Activity.Title=\"撰寫「關係運算子」的程式\"";
Debug.ShouldStop(32);
mostCurrent._activity.setTitle((Object)("撰寫「關係運算子」的程式"));
 BA.debugLineNum = 39;BA.debugLine="lblNum1.Text =A";
Debug.ShouldStop(64);
mostCurrent._lblnum1.setText((Object)(_a));
 BA.debugLineNum = 40;BA.debugLine="lblNum2.Text =B";
Debug.ShouldStop(128);
mostCurrent._lblnum2.setText((Object)(_b));
 BA.debugLineNum = 41;BA.debugLine="End Sub";
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
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 47;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(16384);
 BA.debugLineNum = 49;BA.debugLine="End Sub";
Debug.ShouldStop(65536);
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
 BA.debugLineNum = 43;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(1024);
 BA.debugLineNum = 45;BA.debugLine="End Sub";
Debug.ShouldStop(4096);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btnequal_click() throws Exception{
		Debug.PushSubsStack("btnEqual_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 50;BA.debugLine="Sub btnEqual_Click";
Debug.ShouldStop(131072);
 BA.debugLineNum = 51;BA.debugLine="If A=B Then  '判斷A與B是否相等";
Debug.ShouldStop(262144);
if (_a==_b) { 
 BA.debugLineNum = 52;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(524288);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 54;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(2097152);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 56;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(8388608);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 57;BA.debugLine="End Sub";
Debug.ShouldStop(16777216);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btngreater_click() throws Exception{
		Debug.PushSubsStack("btnGreater_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 86;BA.debugLine="Sub btnGreater_Click";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 87;BA.debugLine="If A>B Then '判斷A是否大於B";
Debug.ShouldStop(4194304);
if (_a>_b) { 
 BA.debugLineNum = 88;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(8388608);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 90;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(33554432);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 92;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(134217728);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 93;BA.debugLine="End Sub";
Debug.ShouldStop(268435456);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btngreaterequal_click() throws Exception{
		Debug.PushSubsStack("btnGreaterEqual_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 95;BA.debugLine="Sub btnGreaterEqual_Click";
Debug.ShouldStop(1073741824);
 BA.debugLineNum = 96;BA.debugLine="If A>=B Then '判斷A是否大於等於B";
Debug.ShouldStop(-2147483648);
if (_a>=_b) { 
 BA.debugLineNum = 97;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(1);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 99;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(4);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 101;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(16);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 102;BA.debugLine="End Sub";
Debug.ShouldStop(32);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btnnotequal_click() throws Exception{
		Debug.PushSubsStack("btnNotEqual_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 59;BA.debugLine="Sub btnNotEqual_Click";
Debug.ShouldStop(67108864);
 BA.debugLineNum = 60;BA.debugLine="If A<>B Then '判斷A是否不等於B";
Debug.ShouldStop(134217728);
if (_a!=_b) { 
 BA.debugLineNum = 61;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(268435456);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 63;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(1073741824);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 65;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(1);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 66;BA.debugLine="End Sub";
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
public static String  _btnsmaller_click() throws Exception{
		Debug.PushSubsStack("btnSmaller_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 68;BA.debugLine="Sub btnSmaller_Click";
Debug.ShouldStop(8);
 BA.debugLineNum = 69;BA.debugLine="If A<B Then '判斷A是否小於B";
Debug.ShouldStop(16);
if (_a<_b) { 
 BA.debugLineNum = 70;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(32);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 72;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(128);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 74;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(512);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 75;BA.debugLine="End Sub";
Debug.ShouldStop(1024);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _btnsmallerequal_click() throws Exception{
		Debug.PushSubsStack("btnSmallerEqual_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 77;BA.debugLine="Sub btnSmallerEqual_Click";
Debug.ShouldStop(4096);
 BA.debugLineNum = 78;BA.debugLine="If A<=B Then '判斷A是否小於等於B";
Debug.ShouldStop(8192);
if (_a<=_b) { 
 BA.debugLineNum = 79;BA.debugLine="Result=\"True\"";
Debug.ShouldStop(16384);
mostCurrent._result = "True";
 }else {
 BA.debugLineNum = 81;BA.debugLine="Result=\"False\"";
Debug.ShouldStop(65536);
mostCurrent._result = "False";
 };
 BA.debugLineNum = 83;BA.debugLine="lblResult.Text =Result";
Debug.ShouldStop(262144);
mostCurrent._lblresult.setText((Object)(mostCurrent._result));
 BA.debugLineNum = 84;BA.debugLine="End Sub";
Debug.ShouldStop(524288);
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
Debug.StartDebugging(mostCurrent.activityBA, 8391, new int[] {4}, "b3f870d3-2f95-4379-92d2-faa6c1ce1774");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim lblNum1 As Label";
mostCurrent._lblnum1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblNum2 As Label";
mostCurrent._lblnum2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnEqual As Button";
mostCurrent._btnequal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnNotEqual As Button";
mostCurrent._btnnotequal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnSmaller As Button";
mostCurrent._btnsmaller = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnSmallerEqual As Button";
mostCurrent._btnsmallerequal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnGreater As Button";
mostCurrent._btngreater = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim btnGreaterEqual As Button";
mostCurrent._btngreaterequal = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim Result As String";
mostCurrent._result = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim A,B As Int";
_a = 0;
_b = 0;
 //BA.debugLineNum = 33;BA.debugLine="A=5:B=15";
_a = (int) (5);
 //BA.debugLineNum = 33;BA.debugLine="A=5:B=15";
_b = (int) (15);
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
