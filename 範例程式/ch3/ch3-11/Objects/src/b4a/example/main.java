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
public anywheresoftware.b4a.objects.EditTextWrapper _edtnum1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtnum2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _chkbook1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _chkbook2 = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"btnRun",mostCurrent._btnrun,"chkBook1",mostCurrent._chkbook1,"chkBook2",mostCurrent._chkbook2,"edtNum1",mostCurrent._edtnum1,"edtNum2",mostCurrent._edtnum2,"lblResult",mostCurrent._lblresult};
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
 BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(536870912);
 BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(-2147483648);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 33;BA.debugLine="Activity.Title =\"多版本訂書折扣\"";
Debug.ShouldStop(1);
mostCurrent._activity.setTitle((Object)("多版本訂書折扣"));
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
 BA.debugLineNum = 40;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(128);
 BA.debugLineNum = 42;BA.debugLine="End Sub";
Debug.ShouldStop(512);
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
 BA.debugLineNum = 36;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(8);
 BA.debugLineNum = 38;BA.debugLine="End Sub";
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
public static String  _btnrun_click() throws Exception{
		Debug.PushSubsStack("btnRun_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _number1 = 0;
int _number2 = 0;
int _sum = 0;
int _total = 0;
float _discount = 0f;
int _book1_value = 0;
int _book2_value = 0;
 BA.debugLineNum = 44;BA.debugLine="Sub btnRun_Click";
Debug.ShouldStop(2048);
 BA.debugLineNum = 45;BA.debugLine="Dim Number1,Number2,Sum,Total As Int";
Debug.ShouldStop(4096);
_number1 = 0;Debug.locals.put("Number1", _number1);
_number2 = 0;Debug.locals.put("Number2", _number2);
_sum = 0;Debug.locals.put("Sum", _sum);
_total = 0;Debug.locals.put("Total", _total);
 BA.debugLineNum = 46;BA.debugLine="Dim DisCount As Float          '折扣率";
Debug.ShouldStop(8192);
_discount = 0f;Debug.locals.put("DisCount", _discount);
 BA.debugLineNum = 47;BA.debugLine="Dim Book1_Value As Int = 500   '精裝版定價";
Debug.ShouldStop(16384);
_book1_value = (int) (500);Debug.locals.put("Book1_Value", _book1_value);Debug.locals.put("Book1_Value", _book1_value);
 BA.debugLineNum = 48;BA.debugLine="Dim Book2_Value As Int = 400   '一般版定價";
Debug.ShouldStop(32768);
_book2_value = (int) (400);Debug.locals.put("Book2_Value", _book2_value);Debug.locals.put("Book2_Value", _book2_value);
 BA.debugLineNum = 49;BA.debugLine="If chkBook1.Checked = True AND chkBook2.Checked = True Then    '精裝本+平裝本";
Debug.ShouldStop(65536);
if (mostCurrent._chkbook1.getChecked()==anywheresoftware.b4a.keywords.Common.True && mostCurrent._chkbook2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 50;BA.debugLine="Number1 = edtNum1.Text";
Debug.ShouldStop(131072);
_number1 = (int)(Double.parseDouble(mostCurrent._edtnum1.getText()));Debug.locals.put("Number1", _number1);
 BA.debugLineNum = 51;BA.debugLine="Number2 = edtNum2.Text";
Debug.ShouldStop(262144);
_number2 = (int)(Double.parseDouble(mostCurrent._edtnum2.getText()));Debug.locals.put("Number2", _number2);
 BA.debugLineNum = 52;BA.debugLine="Sum=Number1+Number2";
Debug.ShouldStop(524288);
_sum = (int) (_number1+_number2);Debug.locals.put("Sum", _sum);
 }else 
{ BA.debugLineNum = 53;BA.debugLine="Else If chkBook1.Checked = True  Then  '精裝本";
Debug.ShouldStop(1048576);
if (mostCurrent._chkbook1.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 54;BA.debugLine="Number1 = edtNum1.Text";
Debug.ShouldStop(2097152);
_number1 = (int)(Double.parseDouble(mostCurrent._edtnum1.getText()));Debug.locals.put("Number1", _number1);
 BA.debugLineNum = 55;BA.debugLine="Sum=Number1";
Debug.ShouldStop(4194304);
_sum = _number1;Debug.locals.put("Sum", _sum);
 }else 
{ BA.debugLineNum = 56;BA.debugLine="Else If chkBook2.Checked = True Then   '平裝本";
Debug.ShouldStop(8388608);
if (mostCurrent._chkbook2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 57;BA.debugLine="Number2 = edtNum2.Text";
Debug.ShouldStop(16777216);
_number2 = (int)(Double.parseDouble(mostCurrent._edtnum2.getText()));Debug.locals.put("Number2", _number2);
 BA.debugLineNum = 58;BA.debugLine="Sum=Number2";
Debug.ShouldStop(33554432);
_sum = _number2;Debug.locals.put("Sum", _sum);
 }}};
 BA.debugLineNum = 60;BA.debugLine="If Sum>=1 AND Sum<=5 Then";
Debug.ShouldStop(134217728);
if (_sum>=1 && _sum<=5) { 
 BA.debugLineNum = 61;BA.debugLine="DisCount = 1                    '1至5本書不打折扣";
Debug.ShouldStop(268435456);
_discount = (float) (1);Debug.locals.put("DisCount", _discount);
 }else 
{ BA.debugLineNum = 62;BA.debugLine="Else If Sum>=6 AND Sum<=10 Then";
Debug.ShouldStop(536870912);
if (_sum>=6 && _sum<=10) { 
 BA.debugLineNum = 63;BA.debugLine="DisCount = 0.9                  '6至10本書打9折";
Debug.ShouldStop(1073741824);
_discount = (float) (0.9);Debug.locals.put("DisCount", _discount);
 }else 
{ BA.debugLineNum = 64;BA.debugLine="Else If Sum>=11 AND Sum<=30 Then";
Debug.ShouldStop(-2147483648);
if (_sum>=11 && _sum<=30) { 
 BA.debugLineNum = 65;BA.debugLine="DisCount = 0.85                  '11至30本書打85折";
Debug.ShouldStop(1);
_discount = (float) (0.85);Debug.locals.put("DisCount", _discount);
 }else 
{ BA.debugLineNum = 66;BA.debugLine="Else If Sum>=31 AND Sum<=50 Then";
Debug.ShouldStop(2);
if (_sum>=31 && _sum<=50) { 
 BA.debugLineNum = 67;BA.debugLine="DisCount = 0.8                  '31至50本書打8折";
Debug.ShouldStop(4);
_discount = (float) (0.8);Debug.locals.put("DisCount", _discount);
 }else 
{ BA.debugLineNum = 68;BA.debugLine="Else If Sum>=50 Then";
Debug.ShouldStop(8);
if (_sum>=50) { 
 BA.debugLineNum = 69;BA.debugLine="DisCount = 0.7                  '50本書以上打7折";
Debug.ShouldStop(16);
_discount = (float) (0.7);Debug.locals.put("DisCount", _discount);
 }}}}};
 BA.debugLineNum = 72;BA.debugLine="If chkBook1.Checked = True AND chkBook2.Checked = True Then         '精裝本+平裝本";
Debug.ShouldStop(128);
if (mostCurrent._chkbook1.getChecked()==anywheresoftware.b4a.keywords.Common.True && mostCurrent._chkbook2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 73;BA.debugLine="Total = DisCount *(Book1_Value*Number1+Book2_Value*Number2)";
Debug.ShouldStop(256);
_total = (int) (_discount*(_book1_value*_number1+_book2_value*_number2));Debug.locals.put("Total", _total);
 }else 
{ BA.debugLineNum = 74;BA.debugLine="Else If chkBook1.Checked = True  Then                               '精裝本";
Debug.ShouldStop(512);
if (mostCurrent._chkbook1.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 75;BA.debugLine="Total = DisCount *(Book1_Value*Number1)";
Debug.ShouldStop(1024);
_total = (int) (_discount*(_book1_value*_number1));Debug.locals.put("Total", _total);
 }else 
{ BA.debugLineNum = 76;BA.debugLine="Else If  chkBook2.Checked = True Then                               '平裝本";
Debug.ShouldStop(2048);
if (mostCurrent._chkbook2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 77;BA.debugLine="Total = DisCount *(Book2_Value*Number2)";
Debug.ShouldStop(4096);
_total = (int) (_discount*(_book2_value*_number2));Debug.locals.put("Total", _total);
 }}};
 BA.debugLineNum = 79;BA.debugLine="lblResult.Text = Total";
Debug.ShouldStop(16384);
mostCurrent._lblresult.setText((Object)(_total));
 BA.debugLineNum = 80;BA.debugLine="End Sub";
Debug.ShouldStop(32768);
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
Debug.StartDebugging(mostCurrent.activityBA, 5386, new int[] {3}, "09317952-7906-4c48-8ecb-31677ec98768");}

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
 //BA.debugLineNum = 22;BA.debugLine="Dim edtNum1 As EditText";
mostCurrent._edtnum1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim edtNum2 As EditText";
mostCurrent._edtnum2 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnRun As Button";
mostCurrent._btnrun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim chkBook1 As CheckBox";
mostCurrent._chkbook1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim chkBook2 As CheckBox";
mostCurrent._chkbook2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
