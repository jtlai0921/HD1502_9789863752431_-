package Leech.myebook.ch15.hw1;

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

public class mathtest extends Activity implements B4AActivity{
	public static mathtest mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.mathtest");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (mathtest).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.mathtest");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch15.hw1.mathtest", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (mathtest) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (mathtest) Resume **");
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
		return mathtest.class;
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
        BA.LogInfo("** Activity (mathtest) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (mathtest) Resume **");
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
public static boolean _check_score = false;
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursordbpoint = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtresult = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnum1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblnum2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public static int _score = 0;
public static int _times = 0;
public static int _i = 0;
public anywheresoftware.b4a.objects.LabelWrapper _lbltesttimes = null;
public static int _num = 0;
public static String[] _arraylist = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnqueryscorelist = null;
public static String _output = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitapp = null;
public static String _strsql = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblop = null;
public static int _scorelevel = 0;
public Leech.myebook.ch15.hw1.main _main = null;
public Leech.myebook.ch15.hw1.userlogin _userlogin = null;
public Leech.myebook.ch15.hw1.scorelist _scorelist = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 33;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyMathScoreDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyMathScoreDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 35;BA.debugLine="QueryScoreLevel    '呼叫查詢考生目前的級數之副程式";
_queryscorelevel();
 //BA.debugLineNum = 36;BA.debugLine="Activity.LoadLayout(\"MathTest\")";
mostCurrent._activity.LoadLayout("MathTest",mostCurrent.activityBA);
 //BA.debugLineNum = 37;BA.debugLine="Activity.Title = \"心算測驗(5關遊戲)\"";
mostCurrent._activity.setTitle((Object)("心算測驗(5關遊戲)"));
 //BA.debugLineNum = 38;BA.debugLine="times=1 '第1次開始";
_times = (int) (1);
 //BA.debugLineNum = 39;BA.debugLine="Check_Score=False  '尚未查詢成績";
_check_score = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 40;BA.debugLine="NewQuestion";
_newquestion();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitapp_click() throws Exception{
 //BA.debugLineNum = 196;BA.debugLine="Sub btnExitApp_Click";
 //BA.debugLineNum = 197;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 198;BA.debugLine="StartActivity(UserLogin)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._userlogin.getObject()));
 //BA.debugLineNum = 199;BA.debugLine="End Sub";
return "";
}
public static String  _btnqueryscore_click() throws Exception{
String _mathscore = "";
String _msg_value = "";
 //BA.debugLineNum = 179;BA.debugLine="Sub btnQueryScore_Click";
 //BA.debugLineNum = 180;BA.debugLine="Dim MathScore  As String   '字串變數";
_mathscore = "";
 //BA.debugLineNum = 181;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 182;BA.debugLine="Msg_Value = Msgbox2(MathScore, \"公佈成績!!!\", \"繼續\", \"\", \"結束\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2(_mathscore,"公佈成績!!!","繼續","","結束",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 183;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 184;BA.debugLine="times=1 '第1次開始";
_times = (int) (1);
 //BA.debugLineNum = 185;BA.debugLine="Score=0";
_score = (int) (0);
 //BA.debugLineNum = 186;BA.debugLine="NewQuestion    '作下一題";
_newquestion();
 }else {
 //BA.debugLineNum = 188;BA.debugLine="Activity.Finish() '結束";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 189;BA.debugLine="ExitApplication   '離開";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 //BA.debugLineNum = 191;BA.debugLine="End Sub";
return "";
}
public static String  _btnqueryscorelist_click() throws Exception{
 //BA.debugLineNum = 193;BA.debugLine="Sub btnQueryScoreList_Click";
 //BA.debugLineNum = 194;BA.debugLine="StartActivity(ScoreList)  '查詢成績單";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._scorelist.getObject()));
 //BA.debugLineNum = 195;BA.debugLine="End Sub";
return "";
}
public static String  _btnrun_click() throws Exception{
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _levelpicture = null;
int _result = 0;
String _msg_value = "";
 //BA.debugLineNum = 90;BA.debugLine="Sub btnRun_Click";
 //BA.debugLineNum = 91;BA.debugLine="Dim LevelPicture As Bitmap";
_levelpicture = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Dim Result As Int";
_result = 0;
 //BA.debugLineNum = 93;BA.debugLine="If edtResult.Text = \"\" Then";
if ((mostCurrent._edtresult.getText()).equals("")) { 
 //BA.debugLineNum = 94;BA.debugLine="Msgbox(\"您尚未作答...\", \"錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未作答...","錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 96;BA.debugLine="Select ScoreLevel+1";
switch (BA.switchObjectToInt(_scorelevel+1,1,2,3,4,5)) {
case 0:
 //BA.debugLineNum = 98;BA.debugLine="Result=lblNum1.Text + lblNum2.Text";
_result = (int) ((double)(Double.parseDouble(mostCurrent._lblnum1.getText()))+(double)(Double.parseDouble(mostCurrent._lblnum2.getText())));
 break;
case 1:
 //BA.debugLineNum = 100;BA.debugLine="Result=lblNum1.Text + lblNum2.Text";
_result = (int) ((double)(Double.parseDouble(mostCurrent._lblnum1.getText()))+(double)(Double.parseDouble(mostCurrent._lblnum2.getText())));
 break;
case 2:
 //BA.debugLineNum = 102;BA.debugLine="Result=lblNum1.Text + lblNum2.Text";
_result = (int) ((double)(Double.parseDouble(mostCurrent._lblnum1.getText()))+(double)(Double.parseDouble(mostCurrent._lblnum2.getText())));
 break;
case 3:
 //BA.debugLineNum = 104;BA.debugLine="Result=lblNum1.Text * lblNum2.Text";
_result = (int) ((double)(Double.parseDouble(mostCurrent._lblnum1.getText()))*(double)(Double.parseDouble(mostCurrent._lblnum2.getText())));
 break;
case 4:
 //BA.debugLineNum = 106;BA.debugLine="Result=lblNum1.Text * lblNum2.Text";
_result = (int) ((double)(Double.parseDouble(mostCurrent._lblnum1.getText()))*(double)(Double.parseDouble(mostCurrent._lblnum2.getText())));
 break;
}
;
 //BA.debugLineNum = 108;BA.debugLine="If edtResult.Text = Result Then  '如果答對";
if ((mostCurrent._edtresult.getText()).equals(BA.NumberToString(_result))) { 
 //BA.debugLineNum = 109;BA.debugLine="lblResult.Text = \"恭喜您！答對了...\"";
mostCurrent._lblresult.setText((Object)("恭喜您！答對了..."));
 //BA.debugLineNum = 110;BA.debugLine="Score=Score+20 '每題20分";
_score = (int) (_score+20);
 //BA.debugLineNum = 111;BA.debugLine="ArrayList(times) =\"O\"";
mostCurrent._arraylist[_times] = "O";
 }else {
 //BA.debugLineNum = 113;BA.debugLine="lblResult.Text = \"很抱歉！答錯了...\"";
mostCurrent._lblresult.setText((Object)("很抱歉！答錯了..."));
 //BA.debugLineNum = 114;BA.debugLine="ToastMessageShow (\"您第：\" & times & \" 題答錯了...\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您第："+BA.NumberToString(_times)+" 題答錯了...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 115;BA.debugLine="ArrayList(times) =\"X\"";
mostCurrent._arraylist[_times] = "X";
 };
 //BA.debugLineNum = 117;BA.debugLine="times=times+1";
_times = (int) (_times+1);
 //BA.debugLineNum = 118;BA.debugLine="NewQuestion    '作下一題";
_newquestion();
 };
 //BA.debugLineNum = 120;BA.debugLine="If times>5 Then       '判斷測驗的題數";
if (_times>5) { 
 //BA.debugLineNum = 121;BA.debugLine="If Score>=60 Then   '判斷是否及格";
if (_score>=60) { 
 //BA.debugLineNum = 122;BA.debugLine="If Score=100 Then";
if (_score==100) { 
 //BA.debugLineNum = 123;BA.debugLine="ScoreLevel=ScoreLevel+1";
_scorelevel = (int) (_scorelevel+1);
 //BA.debugLineNum = 124;BA.debugLine="LevelPicture=LoadBitmap(File.DirAssets,\"Pass\" & ScoreLevel & \".jpg\")";
_levelpicture = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Pass"+BA.NumberToString(_scorelevel)+".jpg");
 //BA.debugLineNum = 125;BA.debugLine="Msgbox2(\"通過第\" & ScoreLevel & \"關\", \"公佈結果\", \"OK\", \"\", \"\", LevelPicture)";
anywheresoftware.b4a.keywords.Common.Msgbox2("通過第"+BA.NumberToString(_scorelevel)+"關","公佈結果","OK","","",(android.graphics.Bitmap)(_levelpicture.getObject()),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 127;BA.debugLine="Output=\"您作了5題，總共得\" & Score & \"分==>及格^-^\"";
mostCurrent._output = "您作了5題，總共得"+BA.NumberToString(_score)+"分==>及格^-^";
 }else {
 //BA.debugLineNum = 129;BA.debugLine="Output=\"您作了5題，總共得\" & Score & \"分==>不及格\"";
mostCurrent._output = "您作了5題，總共得"+BA.NumberToString(_score)+"分==>不及格";
 };
 //BA.debugLineNum = 131;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 132;BA.debugLine="Msg_Value = Msgbox2(Output, \"公佈成績!!!\", \"繼續練習\", \"\", \"查詢成績單\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2(mostCurrent._output,"公佈成績!!!","繼續練習","","查詢成績單",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 133;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 134;BA.debugLine="QueryScore     '呼叫查詢成績副程式";
_queryscore();
 //BA.debugLineNum = 135;BA.debugLine="times=1        '第1次開始";
_times = (int) (1);
 //BA.debugLineNum = 136;BA.debugLine="Score=0";
_score = (int) (0);
 //BA.debugLineNum = 137;BA.debugLine="NewQuestion    '作下一題";
_newquestion();
 }else {
 //BA.debugLineNum = 139;BA.debugLine="Check_Score=True";
_check_score = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 140;BA.debugLine="QueryScore     '呼叫查詢成績副程式";
_queryscore();
 };
 };
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim btnRun As Button";
mostCurrent._btnrun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim edtResult As EditText";
mostCurrent._edtresult = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim lblNum1 As Label";
mostCurrent._lblnum1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim lblNum2 As Label";
mostCurrent._lblnum2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim Score As Int";
_score = 0;
 //BA.debugLineNum = 19;BA.debugLine="Dim times,i As Int";
_times = 0;
_i = 0;
 //BA.debugLineNum = 20;BA.debugLine="Dim lblTestTimes As Label";
mostCurrent._lbltesttimes = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim Num As Int=6";
_num = (int) (6);
 //BA.debugLineNum = 22;BA.debugLine="Dim ArrayList(Num) As String";
mostCurrent._arraylist = new String[_num];
java.util.Arrays.fill(mostCurrent._arraylist,"");
 //BA.debugLineNum = 23;BA.debugLine="Dim btnQueryScoreList As Button";
mostCurrent._btnqueryscorelist = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Output As String";
mostCurrent._output = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim btnExitApp As Button";
mostCurrent._btnexitapp = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim strSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim lblOP As Label";
mostCurrent._lblop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim ScoreLevel As Int";
_scorelevel = 0;
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _newquestion() throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub NewQuestion()";
 //BA.debugLineNum = 54;BA.debugLine="QueryScoreLevel    '呼叫查詢考生目前的級數之副程式";
_queryscorelevel();
 //BA.debugLineNum = 55;BA.debugLine="lblTestTimes.Text=\"===您目前正在測驗【第 \"& (ScoreLevel+1) & \" 關】第 \" & times & \" 題===\"";
mostCurrent._lbltesttimes.setText((Object)("===您目前正在測驗【第 "+BA.NumberToString((_scorelevel+1))+" 關】第 "+BA.NumberToString(_times)+" 題==="));
 //BA.debugLineNum = 56;BA.debugLine="Select ScoreLevel+1";
switch (BA.switchObjectToInt(_scorelevel+1,1,2,3,4,5)) {
case 0:
 //BA.debugLineNum = 58;BA.debugLine="lblNum1.Text = Rnd(1, 10)";
mostCurrent._lblnum1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10))));
 //BA.debugLineNum = 59;BA.debugLine="lblNum2.Text = Rnd(1, 10)";
mostCurrent._lblnum2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10))));
 //BA.debugLineNum = 60;BA.debugLine="lblOP.Text=\"+\"";
mostCurrent._lblop.setText((Object)("+"));
 break;
case 1:
 //BA.debugLineNum = 62;BA.debugLine="lblNum1.Text = Rnd(1, 10)";
mostCurrent._lblnum1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10))));
 //BA.debugLineNum = 63;BA.debugLine="lblNum2.Text = Rnd(10, 100)";
mostCurrent._lblnum2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (10),(int) (100))));
 //BA.debugLineNum = 64;BA.debugLine="lblOP.Text=\"+\"";
mostCurrent._lblop.setText((Object)("+"));
 break;
case 2:
 //BA.debugLineNum = 66;BA.debugLine="lblNum1.Text = Rnd(10, 50)";
mostCurrent._lblnum1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (10),(int) (50))));
 //BA.debugLineNum = 67;BA.debugLine="lblNum2.Text = Rnd(10, 50)";
mostCurrent._lblnum2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (10),(int) (50))));
 //BA.debugLineNum = 68;BA.debugLine="lblOP.Text=\"+\"";
mostCurrent._lblop.setText((Object)("+"));
 break;
case 3:
 //BA.debugLineNum = 70;BA.debugLine="lblNum1.Text = Rnd(1, 10)";
mostCurrent._lblnum1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10))));
 //BA.debugLineNum = 71;BA.debugLine="lblNum2.Text = Rnd(1, 10)";
mostCurrent._lblnum2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (1),(int) (10))));
 //BA.debugLineNum = 72;BA.debugLine="lblOP.Text=\"*\"";
mostCurrent._lblop.setText((Object)("*"));
 break;
case 4:
 //BA.debugLineNum = 74;BA.debugLine="lblNum1.Text = Rnd(10, 20)";
mostCurrent._lblnum1.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (10),(int) (20))));
 //BA.debugLineNum = 75;BA.debugLine="lblNum2.Text = Rnd(10, 20)";
mostCurrent._lblnum2.setText((Object)(anywheresoftware.b4a.keywords.Common.Rnd((int) (10),(int) (20))));
 //BA.debugLineNum = 76;BA.debugLine="lblOP.Text=\"*\"";
mostCurrent._lblop.setText((Object)("*"));
 break;
}
;
 //BA.debugLineNum = 78;BA.debugLine="lblResult.Text = \"請作答...\"";
mostCurrent._lblresult.setText((Object)("請作答..."));
 //BA.debugLineNum = 79;BA.debugLine="edtResult.Text = \"\"";
mostCurrent._edtresult.setText((Object)(""));
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim Check_Score As Boolean";
_check_score = false;
 //BA.debugLineNum = 9;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _queryscore() throws Exception{
String _outscore = "";
String _outputdb = "";
 //BA.debugLineNum = 145;BA.debugLine="Sub QueryScore()    '查詢成績副程式";
 //BA.debugLineNum = 146;BA.debugLine="Dim OutScore As String";
_outscore = "";
 //BA.debugLineNum = 147;BA.debugLine="Dim OutputDB As String";
_outputdb = "";
 //BA.debugLineNum = 148;BA.debugLine="For i=0 To 4";
{
final int step131 = 1;
final int limit131 = (int) (4);
for (_i = (int) (0); (step131 > 0 && _i <= limit131) || (step131 < 0 && _i >= limit131); _i = ((int)(0 + _i + step131))) {
 //BA.debugLineNum = 149;BA.debugLine="OutScore=OutScore & \"第\" & (i+1) & \"答 \" & ArrayList(i+1) & CRLF";
_outscore = _outscore+"第"+BA.NumberToString((_i+1))+"答 "+mostCurrent._arraylist[(int) (_i+1)]+anywheresoftware.b4a.keywords.Common.CRLF;
 }
};
 //BA.debugLineNum = 151;BA.debugLine="OutScore=OutScore & Output";
_outscore = _outscore+mostCurrent._output;
 //BA.debugLineNum = 152;BA.debugLine="Msgbox(OutScore ,\"您的成績單\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_outscore,"您的成績單",mostCurrent.activityBA);
 //BA.debugLineNum = 153;BA.debugLine="Select Score";
switch (_score) {
case 20:
 //BA.debugLineNum = 155;BA.debugLine="OutputDB=\"答對1/5(不及格)\"";
_outputdb = "答對1/5(不及格)";
 break;
case 40:
 //BA.debugLineNum = 157;BA.debugLine="OutputDB=\"答對2/5(不及格)\"";
_outputdb = "答對2/5(不及格)";
 break;
case 60:
 //BA.debugLineNum = 159;BA.debugLine="OutputDB=\"答對3/5(及格)\"";
_outputdb = "答對3/5(及格)";
 break;
case 80:
 //BA.debugLineNum = 161;BA.debugLine="OutputDB=\"答對4/5(及格)\"";
_outputdb = "答對4/5(及格)";
 break;
case 100:
 //BA.debugLineNum = 163;BA.debugLine="OutputDB=\"答對5/5(及格)\"";
_outputdb = "答對5/5(及格)";
 break;
}
;
 //BA.debugLineNum = 165;BA.debugLine="SaveScoreDB(OutputDB,Score)    '儲存測驗歷程成績到「MyMathScoreDB.sqlite」資料庫中。";
_savescoredb(_outputdb,_score);
 //BA.debugLineNum = 166;BA.debugLine="If Check_Score=True Then";
if (_check_score==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 167;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 168;BA.debugLine="StartActivity(ScoreList)  '查詢成績單";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._scorelist.getObject()));
 };
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _queryscorelevel() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub QueryScoreLevel()";
 //BA.debugLineNum = 44;BA.debugLine="strSQL=\"Select * FROM 測驗成績表 Where 帳號='\" & UserLogin.UserAccount & \"'\"";
mostCurrent._strsql = "Select * FROM 測驗成績表 Where 帳號='"+mostCurrent._userlogin._useraccount+"'";
 //BA.debugLineNum = 45;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)       '執行SQL指令";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(mostCurrent._strsql)));
 //BA.debugLineNum = 46;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數";
{
final int step37 = 1;
final int limit37 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step37 > 0 && _i <= limit37) || (step37 < 0 && _i >= limit37); _i = ((int)(0 + _i + step37))) {
 //BA.debugLineNum = 47;BA.debugLine="CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 48;BA.debugLine="ScoreLevel=CursorDBPoint.GetString(\"級數\")  '取得考生目前的級數";
_scorelevel = (int)(Double.parseDouble(_cursordbpoint.GetString("級數")));
 }
};
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _savescoredb(String _outscore,int _scoredb) throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub SaveScoreDB(OutScore As String,ScoreDB As Int)  '儲存成績副程式";
 //BA.debugLineNum = 173;BA.debugLine="Dim strSQL  As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 174;BA.debugLine="strSQL = \"INSERT INTO 測驗成績表(帳號,作答歷程,成績,級數) VALUES('\" & UserLogin.UserAccount & \"','\" & OutScore & \"','\" & ScoreDB & \"' ,'\" & ScoreLevel & \"')\"";
mostCurrent._strsql = "INSERT INTO 測驗成績表(帳號,作答歷程,成績,級數) VALUES('"+mostCurrent._userlogin._useraccount+"','"+_outscore+"','"+BA.NumberToString(_scoredb)+"' ,'"+BA.NumberToString(_scorelevel)+"')";
 //BA.debugLineNum = 175;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(mostCurrent._strsql);
 //BA.debugLineNum = 176;BA.debugLine="ToastMessageShow(\"新增心算成績資料(到資料庫中)...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("新增心算成績資料(到資料庫中)...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return "";
}
}
