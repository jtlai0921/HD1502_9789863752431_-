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
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtnumber = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic3 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic4 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic5 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _pic6 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper[] _arrimgview = null;
public b4a.example.myfunction _myfunction = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"arrImgView",mostCurrent._arrimgview,"btnRun",mostCurrent._btnrun,"edtNumber",mostCurrent._edtnumber,"lblResult",mostCurrent._lblresult,"MyFunction",Debug.moduleToString(b4a.example.myfunction.class),"pic1",mostCurrent._pic1,"pic2",mostCurrent._pic2,"pic3",mostCurrent._pic3,"pic4",mostCurrent._pic4,"pic5",mostCurrent._pic5,"pic6",mostCurrent._pic6};
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
 BA.debugLineNum = 34;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(2);
 BA.debugLineNum = 35;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(4);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 36;BA.debugLine="Activity.Title=\"動態投擲骰子\"";
Debug.ShouldStop(8);
mostCurrent._activity.setTitle((Object)("動態投擲骰子"));
 BA.debugLineNum = 38;BA.debugLine="arrImgView = Array As ImageView(pic1, pic2, pic3, pic4, pic5, pic6)";
Debug.ShouldStop(32);
mostCurrent._arrimgview = new anywheresoftware.b4a.objects.ImageViewWrapper[]{mostCurrent._pic1,mostCurrent._pic2,mostCurrent._pic3,mostCurrent._pic4,mostCurrent._pic5,mostCurrent._pic6};
 BA.debugLineNum = 39;BA.debugLine="LoadPicture  ' 呼叫載入圖片副程式";
Debug.ShouldStop(64);
_loadpicture();
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
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 46;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(8192);
 BA.debugLineNum = 48;BA.debugLine="End Sub";
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
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 42;BA.debugLine="Sub Activity_Resume";
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
public static String  _btnrun_click() throws Exception{
		Debug.PushSubsStack("btnRun_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int[] _a = null;
int _i = 0;
int _j = 0;
int _x = 0;
int _n = 0;
String _temp = "";
 BA.debugLineNum = 56;BA.debugLine="Sub btnRun_Click";
Debug.ShouldStop(8388608);
 BA.debugLineNum = 57;BA.debugLine="If edtNumber.Text=\"\" Then";
Debug.ShouldStop(16777216);
if ((mostCurrent._edtnumber.getText()).equals("")) { 
 BA.debugLineNum = 58;BA.debugLine="Msgbox(\"您尚未輸入投擲骰子次數哦!\",\"產生錯誤...\")";
Debug.ShouldStop(33554432);
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入投擲骰子次數哦!","產生錯誤...",mostCurrent.activityBA);
 }else {
 BA.debugLineNum = 60;BA.debugLine="Dim A(7) As Int";
Debug.ShouldStop(134217728);
_a = new int[(int) (7)];
;Debug.locals.put("A", _a);
 BA.debugLineNum = 61;BA.debugLine="Dim i, j, x,n As Int";
Debug.ShouldStop(268435456);
_i = 0;Debug.locals.put("i", _i);
_j = 0;Debug.locals.put("j", _j);
_x = 0;Debug.locals.put("x", _x);
_n = 0;Debug.locals.put("n", _n);
 BA.debugLineNum = 62;BA.debugLine="Dim temp As String";
Debug.ShouldStop(536870912);
_temp = "";Debug.locals.put("temp", _temp);
 BA.debugLineNum = 63;BA.debugLine="n=edtNumber.Text";
Debug.ShouldStop(1073741824);
_n = (int)(Double.parseDouble(mostCurrent._edtnumber.getText()));Debug.locals.put("n", _n);
 BA.debugLineNum = 64;BA.debugLine="For i = 1 To n";
Debug.ShouldStop(-2147483648);
{
final int step39 = 1;
final int limit39 = _n;
for (_i = (int) (1); (step39 > 0 && _i <= limit39) || (step39 < 0 && _i >= limit39); _i = ((int)(0 + _i + step39))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 65;BA.debugLine="x = Rnd(1,7)";
Debug.ShouldStop(1);
_x = anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (7));Debug.locals.put("x", _x);
 BA.debugLineNum = 66;BA.debugLine="arrImgView(x-1).Visible = True  '顯示圖片";
Debug.ShouldStop(2);
mostCurrent._arrimgview[(int) (_x-1)].setVisible(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 67;BA.debugLine="MyFunction.Delay(100)           '停止0.1秒";
Debug.ShouldStop(4);
mostCurrent._myfunction._delay(mostCurrent.activityBA,(long) (100));
 BA.debugLineNum = 68;BA.debugLine="arrImgView(x-1).Visible = False '隱藏圖片";
Debug.ShouldStop(8);
mostCurrent._arrimgview[(int) (_x-1)].setVisible(anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 69;BA.debugLine="A(x-1) = A(x-1) + 1";
Debug.ShouldStop(16);
_a[(int) (_x-1)] = (int) (_a[(int) (_x-1)]+1);Debug.locals.put("A", _a);
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 71;BA.debugLine="arrImgView(x-1).Visible = True   '顯示最後一張圖片";
Debug.ShouldStop(64);
mostCurrent._arrimgview[(int) (_x-1)].setVisible(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 72;BA.debugLine="temp=\"您投擲骰子 \" & n & \" 次\" & CRLF";
Debug.ShouldStop(128);
_temp = "您投擲骰子 "+BA.NumberToString(_n)+" 次"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("temp", _temp);
 BA.debugLineNum = 73;BA.debugLine="temp = temp & \"================\" & CRLF";
Debug.ShouldStop(256);
_temp = _temp+"================"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("temp", _temp);
 BA.debugLineNum = 74;BA.debugLine="For j = 0 To 5";
Debug.ShouldStop(512);
{
final int step49 = 1;
final int limit49 = (int) (5);
for (_j = (int) (0); (step49 > 0 && _j <= limit49) || (step49 < 0 && _j >= limit49); _j = ((int)(0 + _j + step49))) {
Debug.locals.put("j", _j);
 BA.debugLineNum = 75;BA.debugLine="temp = temp & (j+1) & \"點\" & \"    \" & A(j) & \"次\" & CRLF";
Debug.ShouldStop(1024);
_temp = _temp+BA.NumberToString((_j+1))+"點"+"    "+BA.NumberToString(_a[_j])+"次"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("temp", _temp);
 }
}Debug.locals.put("j", _j);
;
 BA.debugLineNum = 77;BA.debugLine="lblResult.Text=temp";
Debug.ShouldStop(4096);
mostCurrent._lblresult.setText((Object)(_temp));
 };
 BA.debugLineNum = 79;BA.debugLine="End Sub";
Debug.ShouldStop(16384);
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
Debug.StartDebugging(mostCurrent.activityBA, 41891, new int[] {3, 1}, "e1cebe84-0612-47d5-a196-97dcc70df773");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
myfunction._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim btnRun As Button";
mostCurrent._btnrun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim edtNumber As EditText";
mostCurrent._edtnumber = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim pic1 As ImageView";
mostCurrent._pic1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim pic2 As ImageView";
mostCurrent._pic2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim pic3 As ImageView";
mostCurrent._pic3 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim pic4 As ImageView";
mostCurrent._pic4 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim pic5 As ImageView";
mostCurrent._pic5 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim pic6 As ImageView";
mostCurrent._pic6 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim arrImgView() As ImageView";
mostCurrent._arrimgview = new anywheresoftware.b4a.objects.ImageViewWrapper[(int) (0)];
{
int d0 = mostCurrent._arrimgview.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._arrimgview[i0] = new anywheresoftware.b4a.objects.ImageViewWrapper();
}
}
;
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
public static String  _loadpicture() throws Exception{
		Debug.PushSubsStack("LoadPicture (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _i = 0;
anywheresoftware.b4a.objects.drawable.BitmapDrawable _picdb = null;
 BA.debugLineNum = 49;BA.debugLine="Sub LoadPicture()  '載入圖片副程式";
Debug.ShouldStop(65536);
 BA.debugLineNum = 50;BA.debugLine="For i = 1 To 6";
Debug.ShouldStop(131072);
{
final int step25 = 1;
final int limit25 = (int) (6);
for (_i = (int) (1); (step25 > 0 && _i <= limit25) || (step25 < 0 && _i >= limit25); _i = ((int)(0 + _i + step25))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 51;BA.debugLine="Dim PicDB As BitmapDrawable";
Debug.ShouldStop(262144);
_picdb = new anywheresoftware.b4a.objects.drawable.BitmapDrawable();Debug.locals.put("PicDB", _picdb);
 BA.debugLineNum = 52;BA.debugLine="PicDB.Initialize(LoadBitmap(File.DirAssets, \"G_\" & i & \".bmp\"))";
Debug.ShouldStop(524288);
_picdb.Initialize((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"G_"+BA.NumberToString(_i)+".bmp").getObject()));
 BA.debugLineNum = 53;BA.debugLine="arrImgView(i-1).Background = PicDB  ' 載入圖片";
Debug.ShouldStop(1048576);
mostCurrent._arrimgview[(int) (_i-1)].setBackground((android.graphics.drawable.Drawable)(_picdb.getObject()));
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 55;BA.debugLine="End Sub";
Debug.ShouldStop(4194304);
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
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
