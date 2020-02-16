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
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblscorelist = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public static String _temp1 = "";
public static String _temp2 = "";
public static int _total = 0;
public static int _aver = 0;
public static int[][] _score = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"Aver",_aver,"btnRun1",mostCurrent._btnrun1,"btnRun2",mostCurrent._btnrun2,"btnRun3",mostCurrent._btnrun3,"lblResult",mostCurrent._lblresult,"lblScoreList",mostCurrent._lblscorelist,"Score",_score,"temp1",mostCurrent._temp1,"temp2",mostCurrent._temp2,"Total",_total};
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
 BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(16);
 BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(32);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 39;BA.debugLine="Activity.Title=\"二維陣列的應用\"";
Debug.ShouldStop(64);
mostCurrent._activity.setTitle((Object)("二維陣列的應用"));
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
 BA.debugLineNum = 51;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(262144);
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
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _print_score = "";
 BA.debugLineNum = 42;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(512);
 BA.debugLineNum = 43;BA.debugLine="Dim Print_Score As String =\"姓名    國文    英文    數學    計概    程式\" & CRLF";
Debug.ShouldStop(1024);
_print_score = "姓名    國文    英文    數學    計概    程式"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("Print_Score", _print_score);Debug.locals.put("Print_Score", _print_score);
 BA.debugLineNum = 44;BA.debugLine="Print_Score=Print_Score &  \"張三    65         85        78        75        69\" & CRLF";
Debug.ShouldStop(2048);
_print_score = _print_score+"張三    65         85        78        75        69"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("Print_Score", _print_score);
 BA.debugLineNum = 45;BA.debugLine="Print_Score=Print_Score &  \"李四    66         55        52        92        47\" & CRLF";
Debug.ShouldStop(4096);
_print_score = _print_score+"李四    66         55        52        92        47"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("Print_Score", _print_score);
 BA.debugLineNum = 46;BA.debugLine="Print_Score=Print_Score &  \"王五    75         99        63        73        86\" & CRLF";
Debug.ShouldStop(8192);
_print_score = _print_score+"王五    75         99        63        73        86"+anywheresoftware.b4a.keywords.Common.CRLF;Debug.locals.put("Print_Score", _print_score);
 BA.debugLineNum = 47;BA.debugLine="Print_Score=Print_Score &  \"雄雄    77         88        99        91        100\"";
Debug.ShouldStop(16384);
_print_score = _print_score+"雄雄    77         88        99        91        100";Debug.locals.put("Print_Score", _print_score);
 BA.debugLineNum = 48;BA.debugLine="lblScoreList.Text=Print_Score";
Debug.ShouldStop(32768);
mostCurrent._lblscorelist.setText((Object)(_print_score));
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
public static String  _btnrun1_click() throws Exception{
		Debug.PushSubsStack("btnRun1_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 55;BA.debugLine="Sub btnRun1_Click   '計算出「雄雄」總分與平均成績";
Debug.ShouldStop(4194304);
 BA.debugLineNum = 57;BA.debugLine="Total = Score(3,0) + Score(3,1) + Score(3,2) + Score(3,3) + Score(3,4)";
Debug.ShouldStop(16777216);
_total = (int) (_score[(int) (3)][(int) (0)]+_score[(int) (3)][(int) (1)]+_score[(int) (3)][(int) (2)]+_score[(int) (3)][(int) (3)]+_score[(int) (3)][(int) (4)]);
 BA.debugLineNum = 58;BA.debugLine="Aver = Total / 5        '算出平均";
Debug.ShouldStop(33554432);
_aver = (int) (_total/(double)5);
 BA.debugLineNum = 60;BA.debugLine="lblResult.Text =\"雄雄同學  總和=\" & Total & \"  平均=\" & Aver";
Debug.ShouldStop(134217728);
mostCurrent._lblresult.setText((Object)("雄雄同學  總和="+BA.NumberToString(_total)+"  平均="+BA.NumberToString(_aver)));
 BA.debugLineNum = 61;BA.debugLine="End Sub";
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
public static String  _btnrun2_click() throws Exception{
		Debug.PushSubsStack("btnRun2_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _i = 0;
int _j = 0;
int[] _sum = null;
int[] _average = null;
 BA.debugLineNum = 62;BA.debugLine="Sub btnRun2_Click   '計算出「每一科目」的平均分數";
Debug.ShouldStop(536870912);
 BA.debugLineNum = 63;BA.debugLine="Dim i,j As Int";
Debug.ShouldStop(1073741824);
_i = 0;Debug.locals.put("i", _i);
_j = 0;Debug.locals.put("j", _j);
 BA.debugLineNum = 64;BA.debugLine="Dim Sum(5),Average(5) As Int";
Debug.ShouldStop(-2147483648);
_sum = new int[(int) (5)];
;Debug.locals.put("Sum", _sum);
_average = new int[(int) (5)];
;Debug.locals.put("Average", _average);
 BA.debugLineNum = 65;BA.debugLine="temp1=\"\"";
Debug.ShouldStop(1);
mostCurrent._temp1 = "";
 BA.debugLineNum = 66;BA.debugLine="For j = 0 To 4       '控制行數";
Debug.ShouldStop(2);
{
final int step55 = 1;
final int limit55 = (int) (4);
for (_j = (int) (0); (step55 > 0 && _j <= limit55) || (step55 < 0 && _j >= limit55); _j = ((int)(0 + _j + step55))) {
Debug.locals.put("j", _j);
 BA.debugLineNum = 67;BA.debugLine="For i = 0 To 3    '控制列數";
Debug.ShouldStop(4);
{
final int step56 = 1;
final int limit56 = (int) (3);
for (_i = (int) (0); (step56 > 0 && _i <= limit56) || (step56 < 0 && _i >= limit56); _i = ((int)(0 + _i + step56))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 68;BA.debugLine="Sum(j) = Sum(j) + Score(i,j)   '計算出每一科目的總分數";
Debug.ShouldStop(8);
_sum[_j] = (int) (_sum[_j]+_score[_i][_j]);Debug.locals.put("Sum", _sum);
 }
}Debug.locals.put("i", _i);
;
 }
}Debug.locals.put("j", _j);
;
 BA.debugLineNum = 71;BA.debugLine="For j = 0 To 4";
Debug.ShouldStop(64);
{
final int step60 = 1;
final int limit60 = (int) (4);
for (_j = (int) (0); (step60 > 0 && _j <= limit60) || (step60 < 0 && _j >= limit60); _j = ((int)(0 + _j + step60))) {
Debug.locals.put("j", _j);
 BA.debugLineNum = 72;BA.debugLine="Average(j)=Sum(j) / 4            '計算出每一科目的平均分數";
Debug.ShouldStop(128);
_average[_j] = (int) (_sum[_j]/(double)4);Debug.locals.put("Average", _average);
 BA.debugLineNum = 73;BA.debugLine="temp1=temp1 & Average(j) & \"  \"";
Debug.ShouldStop(256);
mostCurrent._temp1 = mostCurrent._temp1+BA.NumberToString(_average[_j])+"  ";
 }
}Debug.locals.put("j", _j);
;
 BA.debugLineNum = 76;BA.debugLine="lblResult.Text =\"每一科目的平均分數：\" & temp1";
Debug.ShouldStop(2048);
mostCurrent._lblresult.setText((Object)("每一科目的平均分數："+mostCurrent._temp1));
 BA.debugLineNum = 77;BA.debugLine="End Sub";
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
public static String  _btnrun3_click() throws Exception{
		Debug.PushSubsStack("btnRun3_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _i = 0;
int _j = 0;
int[] _sum = null;
int[] _average = null;
 BA.debugLineNum = 78;BA.debugLine="Sub btnRun3_Click   '計算出「每一位學生」平均成績";
Debug.ShouldStop(8192);
 BA.debugLineNum = 79;BA.debugLine="Dim i,j As Int";
Debug.ShouldStop(16384);
_i = 0;Debug.locals.put("i", _i);
_j = 0;Debug.locals.put("j", _j);
 BA.debugLineNum = 80;BA.debugLine="Dim Sum(5),Average(5) As Int";
Debug.ShouldStop(32768);
_sum = new int[(int) (5)];
;Debug.locals.put("Sum", _sum);
_average = new int[(int) (5)];
;Debug.locals.put("Average", _average);
 BA.debugLineNum = 81;BA.debugLine="temp2=\"\"";
Debug.ShouldStop(65536);
mostCurrent._temp2 = "";
 BA.debugLineNum = 82;BA.debugLine="For i = 0 To 3         '控制列數";
Debug.ShouldStop(131072);
{
final int step70 = 1;
final int limit70 = (int) (3);
for (_i = (int) (0); (step70 > 0 && _i <= limit70) || (step70 < 0 && _i >= limit70); _i = ((int)(0 + _i + step70))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 83;BA.debugLine="For j = 0 To 4    '控制行數";
Debug.ShouldStop(262144);
{
final int step71 = 1;
final int limit71 = (int) (4);
for (_j = (int) (0); (step71 > 0 && _j <= limit71) || (step71 < 0 && _j >= limit71); _j = ((int)(0 + _j + step71))) {
Debug.locals.put("j", _j);
 BA.debugLineNum = 84;BA.debugLine="Sum(i) = Sum(i) + Score(i,j)   '計算出每一位同學的總成績";
Debug.ShouldStop(524288);
_sum[_i] = (int) (_sum[_i]+_score[_i][_j]);Debug.locals.put("Sum", _sum);
 }
}Debug.locals.put("j", _j);
;
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 87;BA.debugLine="For i = 0 To 3";
Debug.ShouldStop(4194304);
{
final int step75 = 1;
final int limit75 = (int) (3);
for (_i = (int) (0); (step75 > 0 && _i <= limit75) || (step75 < 0 && _i >= limit75); _i = ((int)(0 + _i + step75))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 88;BA.debugLine="Average(i)=Sum(i) / 5              '計算出每一位同學的平均成績";
Debug.ShouldStop(8388608);
_average[_i] = (int) (_sum[_i]/(double)5);Debug.locals.put("Average", _average);
 BA.debugLineNum = 89;BA.debugLine="temp2=temp2 & Average(i) & \"  \"";
Debug.ShouldStop(16777216);
mostCurrent._temp2 = mostCurrent._temp2+BA.NumberToString(_average[_i])+"  ";
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 92;BA.debugLine="lblResult.Text =\"每一位同學的平均成績：\" & temp2";
Debug.ShouldStop(134217728);
mostCurrent._lblresult.setText((Object)("每一位同學的平均成績："+mostCurrent._temp2));
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

public static void initializeProcessGlobals() {
    if (mostCurrent != null && mostCurrent.activityBA != null) {
Debug.StartDebugging(mostCurrent.activityBA, 15594, new int[] {3}, "cc9a023b-4415-400e-aab6-80f59d1f63d3");}

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
 //BA.debugLineNum = 22;BA.debugLine="Dim btnRun1 As Button";
mostCurrent._btnrun1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnRun2 As Button";
mostCurrent._btnrun2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim btnRun3 As Button";
mostCurrent._btnrun3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblScoreList As Label";
mostCurrent._lblscorelist = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim temp1,temp2 As String";
mostCurrent._temp1 = "";
mostCurrent._temp2 = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim Total, Aver As Int";
_total = 0;
_aver = 0;
 //BA.debugLineNum = 30;BA.debugLine="Dim Score(4,5) As Int";
_score = new int[(int) (4)][];
{
int d0 = _score.length;
int d1 = (int) (5);
for (int i0 = 0;i0 < d0;i0++) {
_score[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 31;BA.debugLine="Score(0,0)=65:Score(0,1)=85:Score(0,2)=78:Score(0,3)=75:Score(0,4)=69";
_score[(int) (0)][(int) (0)] = (int) (65);
 //BA.debugLineNum = 31;BA.debugLine="Score(0,0)=65:Score(0,1)=85:Score(0,2)=78:Score(0,3)=75:Score(0,4)=69";
_score[(int) (0)][(int) (1)] = (int) (85);
 //BA.debugLineNum = 31;BA.debugLine="Score(0,0)=65:Score(0,1)=85:Score(0,2)=78:Score(0,3)=75:Score(0,4)=69";
_score[(int) (0)][(int) (2)] = (int) (78);
 //BA.debugLineNum = 31;BA.debugLine="Score(0,0)=65:Score(0,1)=85:Score(0,2)=78:Score(0,3)=75:Score(0,4)=69";
_score[(int) (0)][(int) (3)] = (int) (75);
 //BA.debugLineNum = 31;BA.debugLine="Score(0,0)=65:Score(0,1)=85:Score(0,2)=78:Score(0,3)=75:Score(0,4)=69";
_score[(int) (0)][(int) (4)] = (int) (69);
 //BA.debugLineNum = 32;BA.debugLine="Score(1,0)=66:Score(1,1)=55:Score(1,2)=52:Score(1,3)=92:Score(1,4)=47";
_score[(int) (1)][(int) (0)] = (int) (66);
 //BA.debugLineNum = 32;BA.debugLine="Score(1,0)=66:Score(1,1)=55:Score(1,2)=52:Score(1,3)=92:Score(1,4)=47";
_score[(int) (1)][(int) (1)] = (int) (55);
 //BA.debugLineNum = 32;BA.debugLine="Score(1,0)=66:Score(1,1)=55:Score(1,2)=52:Score(1,3)=92:Score(1,4)=47";
_score[(int) (1)][(int) (2)] = (int) (52);
 //BA.debugLineNum = 32;BA.debugLine="Score(1,0)=66:Score(1,1)=55:Score(1,2)=52:Score(1,3)=92:Score(1,4)=47";
_score[(int) (1)][(int) (3)] = (int) (92);
 //BA.debugLineNum = 32;BA.debugLine="Score(1,0)=66:Score(1,1)=55:Score(1,2)=52:Score(1,3)=92:Score(1,4)=47";
_score[(int) (1)][(int) (4)] = (int) (47);
 //BA.debugLineNum = 33;BA.debugLine="Score(2,0)=75:Score(2,1)=99:Score(2,2)=63:Score(2,3)=73:Score(2,4)=86";
_score[(int) (2)][(int) (0)] = (int) (75);
 //BA.debugLineNum = 33;BA.debugLine="Score(2,0)=75:Score(2,1)=99:Score(2,2)=63:Score(2,3)=73:Score(2,4)=86";
_score[(int) (2)][(int) (1)] = (int) (99);
 //BA.debugLineNum = 33;BA.debugLine="Score(2,0)=75:Score(2,1)=99:Score(2,2)=63:Score(2,3)=73:Score(2,4)=86";
_score[(int) (2)][(int) (2)] = (int) (63);
 //BA.debugLineNum = 33;BA.debugLine="Score(2,0)=75:Score(2,1)=99:Score(2,2)=63:Score(2,3)=73:Score(2,4)=86";
_score[(int) (2)][(int) (3)] = (int) (73);
 //BA.debugLineNum = 33;BA.debugLine="Score(2,0)=75:Score(2,1)=99:Score(2,2)=63:Score(2,3)=73:Score(2,4)=86";
_score[(int) (2)][(int) (4)] = (int) (86);
 //BA.debugLineNum = 34;BA.debugLine="Score(3,0)=77:Score(3,1)=88:Score(3,2)=99:Score(3,3)=91:Score(3,4)=100";
_score[(int) (3)][(int) (0)] = (int) (77);
 //BA.debugLineNum = 34;BA.debugLine="Score(3,0)=77:Score(3,1)=88:Score(3,2)=99:Score(3,3)=91:Score(3,4)=100";
_score[(int) (3)][(int) (1)] = (int) (88);
 //BA.debugLineNum = 34;BA.debugLine="Score(3,0)=77:Score(3,1)=88:Score(3,2)=99:Score(3,3)=91:Score(3,4)=100";
_score[(int) (3)][(int) (2)] = (int) (99);
 //BA.debugLineNum = 34;BA.debugLine="Score(3,0)=77:Score(3,1)=88:Score(3,2)=99:Score(3,3)=91:Score(3,4)=100";
_score[(int) (3)][(int) (3)] = (int) (91);
 //BA.debugLineNum = 34;BA.debugLine="Score(3,0)=77:Score(3,1)=88:Score(3,2)=99:Score(3,3)=91:Score(3,4)=100";
_score[(int) (3)][(int) (4)] = (int) (100);
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
}
