package Leech.myebook.PalyGame4;

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

public class play2 extends Activity implements B4AActivity{
	public static play2 mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.PalyGame4", "Leech.myebook.PalyGame4.play2");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (play2).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.PalyGame4", "Leech.myebook.PalyGame4.play2");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.PalyGame4.play2", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (play2) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (play2) Resume **");
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
		return play2.class;
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
        BA.LogInfo("** Activity (play2) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (play2) Resume **");
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
public anywheresoftware.b4a.objects.ButtonWrapper _btn1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn6 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn8 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btn9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper[] _arraybtn = null;
public static int[] _savexo = null;
public static String[] _xo = null;
public static int _counter = 0;
public static int[] _win = null;
public static int _sp = 0;
public static int _i = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbegin = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresutl = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _btncolor_gray = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _btncolor_blue = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexit = null;
public static boolean _winflag = false;
public static int _o_wins = 0;
public static int _x_wins = 0;
public static int _tie = 0;
public Leech.myebook.PalyGame4.main _main = null;
public Leech.myebook.PalyGame4.play1 _play1 = null;
public Leech.myebook.PalyGame4.scorelist _scorelist = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 40;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyGameScore.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyGameScore.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 42;BA.debugLine="Activity.LoadLayout(\"Play2\")    '使用「Play2(版面配置檔)」來輸出";
mostCurrent._activity.LoadLayout("Play2",mostCurrent.activityBA);
 //BA.debugLineNum = 43;BA.debugLine="Activity.Title = \"電腦對戰\"     '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("電腦對戰"));
 //BA.debugLineNum = 44;BA.debugLine="Arraybtn =Array As Button(btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9)   '按鈕陣列";
mostCurrent._arraybtn = new anywheresoftware.b4a.objects.ButtonWrapper[]{mostCurrent._btn1,mostCurrent._btn2,mostCurrent._btn3,mostCurrent._btn4,mostCurrent._btn5,mostCurrent._btn6,mostCurrent._btn7,mostCurrent._btn8,mostCurrent._btn9};
 //BA.debugLineNum = 45;BA.debugLine="btnColor_Gray.Initialize(Colors.Gray,10dip)    '灰色";
mostCurrent._btncolor_gray.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Gray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 46;BA.debugLine="btnColor_Blue.Initialize(Colors.Yellow,10dip)  '黃色";
mostCurrent._btncolor_blue.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Yellow,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 47;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 56;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 49;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 51;BA.debugLine="For i=0 To 8";
{
final int step42 = 1;
final int limit42 = (int) (8);
for (_i = (int) (0); (step42 > 0 && _i <= limit42) || (step42 < 0 && _i >= limit42); _i = ((int)(0 + _i + step42))) {
 //BA.debugLineNum = 52;BA.debugLine="Arraybtn(i).Enabled = False";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _btnbegin_click() throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub btnBegin_Click'重新開始";
 //BA.debugLineNum = 61;BA.debugLine="For i=0 To 8";
{
final int step49 = 1;
final int limit49 = (int) (8);
for (_i = (int) (0); (step49 > 0 && _i <= limit49) || (step49 < 0 && _i >= limit49); _i = ((int)(0 + _i + step49))) {
 //BA.debugLineNum = 62;BA.debugLine="Arraybtn(i).Enabled = True           '設定井字遊戲中的九個按鈕有作用";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 63;BA.debugLine="Arraybtn(i).Background=btnColor_Gray '灰色";
mostCurrent._arraybtn[_i].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_gray.getObject()));
 //BA.debugLineNum = 64;BA.debugLine="Arraybtn(i).Text =\"\"                 '清除O與X的文字內容";
mostCurrent._arraybtn[_i].setText((Object)(""));
 //BA.debugLineNum = 65;BA.debugLine="SaveXO(i)=0                          '存放資料格預設值為0";
_savexo[_i] = (int) (0);
 }
};
 //BA.debugLineNum = 67;BA.debugLine="btnBegin.Enabled =False                '「重新開始」按鈕沒作用";
mostCurrent._btnbegin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 68;BA.debugLine="WinFlag=False                          '預設目前對戰情況為尚未有一方贏";
_winflag = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 69;BA.debugLine="lblResutl.Text=\"=井字遊戲=\"	           '顯示「井字遊戲」";
mostCurrent._lblresutl.setText((Object)("=井字遊戲="));
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _btnexit_click() throws Exception{
 //BA.debugLineNum = 502;BA.debugLine="Sub btnExit_Click";
 //BA.debugLineNum = 503;BA.debugLine="Activity.Finish()      '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 504;BA.debugLine="StartActivity(Main)    '回首";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 505;BA.debugLine="End Sub";
return "";
}
public static String  _button_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _send = null;
 //BA.debugLineNum = 73;BA.debugLine="Sub Button_Click";
 //BA.debugLineNum = 74;BA.debugLine="Dim Send As Button";
_send = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Send=Sender   '取得事件來源";
_send.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 77;BA.debugLine="If SaveXO(Send.Tag) = 0 Then        '如果是空格";
if (_savexo[(int)(BA.ObjectToNumber(_send.getTag()))]==0) { 
 //BA.debugLineNum = 78;BA.debugLine="Arraybtn(Send.Tag).Text = \"O\"    '玩家設為O，並顯示O在按鈕上";
mostCurrent._arraybtn[(int)(BA.ObjectToNumber(_send.getTag()))].setText((Object)("O"));
 //BA.debugLineNum = 79;BA.debugLine="SaveXO(Send.Tag) = 1             '在按下時，填入1到指定的資料格中";
_savexo[(int)(BA.ObjectToNumber(_send.getTag()))] = (int) (1);
 //BA.debugLineNum = 80;BA.debugLine="count_counter                    '呼叫統計目前空格數之副程式";
_count_counter();
 //BA.debugLineNum = 81;BA.debugLine="CheckWin                         '檢查O或X贏";
_checkwin();
 //BA.debugLineNum = 82;BA.debugLine="If WinFlag=False Then             '如果目前對戰情況為尚未有一方贏";
if (_winflag==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 83;BA.debugLine="If counter>6 Then               '如果目前空格數大於6";
if (_counter>6) { 
 //BA.debugLineNum = 84;BA.debugLine="PcPlay1                      '呼叫前三次，電腦亂數出牌(亦即OXO)";
_pcplay1();
 }else {
 //BA.debugLineNum = 86;BA.debugLine="PcPlay2                      '呼叫後六次(阻擋及進改)";
_pcplay2();
 };
 };
 }else {
 //BA.debugLineNum = 90;BA.debugLine="Msgbox (\"此地已下過,不可再下!!\",\"錯誤\")";
anywheresoftware.b4a.keywords.Common.Msgbox("此地已下過,不可再下!!","錯誤",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 92;BA.debugLine="CheckError(Send.Tag)  '呼叫「檢查例外處理」之副程式";
_checkerror((int)(BA.ObjectToNumber(_send.getTag())));
 //BA.debugLineNum = 93;BA.debugLine="End Sub";
return "";
}
public static String  _checkerror(int _errtag) throws Exception{
int _t = 0;
 //BA.debugLineNum = 95;BA.debugLine="Sub CheckError(errTag As Int)";
 //BA.debugLineNum = 96;BA.debugLine="Dim t As Int = 0";
_t = (int) (0);
 //BA.debugLineNum = 97;BA.debugLine="For i = 0 To 8";
{
final int step81 = 1;
final int limit81 = (int) (8);
for (_i = (int) (0); (step81 > 0 && _i <= limit81) || (step81 < 0 && _i >= limit81); _i = ((int)(0 + _i + step81))) {
 //BA.debugLineNum = 98;BA.debugLine="If SaveXO(i) = 0 Then";
if (_savexo[_i]==0) { 
 //BA.debugLineNum = 99;BA.debugLine="t = t + 1";
_t = (int) (_t+1);
 };
 }
};
 //BA.debugLineNum = 102;BA.debugLine="If t=8 AND Arraybtn(errTag).Text = \"X\" Then";
if (_t==8 && (mostCurrent._arraybtn[_errtag].getText()).equals("X")) { 
 //BA.debugLineNum = 103;BA.debugLine="Arraybtn(errTag).Text = \"O\"";
mostCurrent._arraybtn[_errtag].setText((Object)("O"));
 //BA.debugLineNum = 104;BA.debugLine="SaveXO(errTag) = 1";
_savexo[_errtag] = (int) (1);
 //BA.debugLineNum = 105;BA.debugLine="If counter>6 Then";
if (_counter>6) { 
 //BA.debugLineNum = 106;BA.debugLine="PcPlay1  '呼叫前三次，電腦亂數出牌(亦即OXO)";
_pcplay1();
 }else {
 //BA.debugLineNum = 108;BA.debugLine="PcPlay2  '呼叫後六次(阻擋及進改)";
_pcplay2();
 };
 };
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}
public static String  _checkwin() throws Exception{
 //BA.debugLineNum = 345;BA.debugLine="Sub CheckWin";
 //BA.debugLineNum = 347;BA.debugLine="If SaveXO(0) + SaveXO(1) + SaveXO(2) = 3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (1)]+_savexo[(int) (2)]==3) { 
 //BA.debugLineNum = 348;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 349;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 350;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 351;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 354;BA.debugLine="If SaveXO(3) + SaveXO(4) + SaveXO(5) = 3 Then";
if (_savexo[(int) (3)]+_savexo[(int) (4)]+_savexo[(int) (5)]==3) { 
 //BA.debugLineNum = 355;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 356;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 357;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 358;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 361;BA.debugLine="If SaveXO(6) + SaveXO(7) + SaveXO(8) = 3 Then";
if (_savexo[(int) (6)]+_savexo[(int) (7)]+_savexo[(int) (8)]==3) { 
 //BA.debugLineNum = 362;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 363;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 364;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 365;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 369;BA.debugLine="If SaveXO(0) + SaveXO(3) + SaveXO(6) = 3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (3)]+_savexo[(int) (6)]==3) { 
 //BA.debugLineNum = 370;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 371;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 372;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 373;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 376;BA.debugLine="If SaveXO(1) + SaveXO(4) + SaveXO(7) = 3 Then";
if (_savexo[(int) (1)]+_savexo[(int) (4)]+_savexo[(int) (7)]==3) { 
 //BA.debugLineNum = 377;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 378;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 379;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 380;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 383;BA.debugLine="If SaveXO(2) + SaveXO(5) + SaveXO(8) = 3 Then";
if (_savexo[(int) (2)]+_savexo[(int) (5)]+_savexo[(int) (8)]==3) { 
 //BA.debugLineNum = 384;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 385;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 386;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 387;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 390;BA.debugLine="If SaveXO(0) + SaveXO(4) + SaveXO(8) = 3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (4)]+_savexo[(int) (8)]==3) { 
 //BA.debugLineNum = 391;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 392;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 393;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 394;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 397;BA.debugLine="If SaveXO(2) + SaveXO(4) + SaveXO(6) = 3 Then";
if (_savexo[(int) (2)]+_savexo[(int) (4)]+_savexo[(int) (6)]==3) { 
 //BA.debugLineNum = 398;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 399;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 400;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 401;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 404;BA.debugLine="If SaveXO(0) + SaveXO(1) + SaveXO(2) = -3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (1)]+_savexo[(int) (2)]==-3) { 
 //BA.debugLineNum = 405;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 406;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 407;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 408;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 411;BA.debugLine="If SaveXO(3) + SaveXO(4) + SaveXO(5) = -3 Then";
if (_savexo[(int) (3)]+_savexo[(int) (4)]+_savexo[(int) (5)]==-3) { 
 //BA.debugLineNum = 412;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 413;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 414;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 415;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 418;BA.debugLine="If SaveXO(6) + SaveXO(7) + SaveXO(8) = -3 Then";
if (_savexo[(int) (6)]+_savexo[(int) (7)]+_savexo[(int) (8)]==-3) { 
 //BA.debugLineNum = 419;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 420;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 421;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 422;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 425;BA.debugLine="If SaveXO(0) + SaveXO(3) + SaveXO(6) = -3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (3)]+_savexo[(int) (6)]==-3) { 
 //BA.debugLineNum = 426;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 427;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 428;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 429;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 432;BA.debugLine="If SaveXO(1) + SaveXO(4) + SaveXO(7) = -3 Then";
if (_savexo[(int) (1)]+_savexo[(int) (4)]+_savexo[(int) (7)]==-3) { 
 //BA.debugLineNum = 433;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 434;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 435;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 436;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 439;BA.debugLine="If SaveXO(2) + SaveXO(5) + SaveXO(8) = -3 Then";
if (_savexo[(int) (2)]+_savexo[(int) (5)]+_savexo[(int) (8)]==-3) { 
 //BA.debugLineNum = 440;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 441;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 442;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 443;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 446;BA.debugLine="If SaveXO(0) + SaveXO(4) + SaveXO(8) = -3 Then";
if (_savexo[(int) (0)]+_savexo[(int) (4)]+_savexo[(int) (8)]==-3) { 
 //BA.debugLineNum = 447;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 448;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 449;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 450;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 453;BA.debugLine="If SaveXO(2) + SaveXO(4) + SaveXO(6) = -3 Then";
if (_savexo[(int) (2)]+_savexo[(int) (4)]+_savexo[(int) (6)]==-3) { 
 //BA.debugLineNum = 454;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 455;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 456;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 457;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 460;BA.debugLine="If lblResutl.Text=\"O贏!\" Then";
if ((mostCurrent._lblresutl.getText()).equals("O贏!")) { 
 //BA.debugLineNum = 461;BA.debugLine="QueryScore  '查詢目前戰狀成績";
_queryscore();
 //BA.debugLineNum = 462;BA.debugLine="O_Wins=O_Wins+1";
_o_wins = (int) (_o_wins+1);
 //BA.debugLineNum = 463;BA.debugLine="SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。";
_savescoredb();
 }else if((mostCurrent._lblresutl.getText()).equals("X贏!")) { 
 //BA.debugLineNum = 465;BA.debugLine="QueryScore  '查詢目前戰狀成績";
_queryscore();
 //BA.debugLineNum = 466;BA.debugLine="X_Wins=X_Wins+1";
_x_wins = (int) (_x_wins+1);
 //BA.debugLineNum = 467;BA.debugLine="SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。";
_savescoredb();
 }else if((mostCurrent._arraybtn[(int) (0)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (1)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (2)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (2)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (3)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (4)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (5)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (6)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (7)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (8)].getText()).equals("") == false) { 
 //BA.debugLineNum = 471;BA.debugLine="lblResutl.Text = \"平手!\"";
mostCurrent._lblresutl.setText((Object)("平手!"));
 //BA.debugLineNum = 472;BA.debugLine="QueryScore  '查詢目前戰狀成績";
_queryscore();
 //BA.debugLineNum = 473;BA.debugLine="Tie=Tie+1";
_tie = (int) (_tie+1);
 //BA.debugLineNum = 474;BA.debugLine="SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。";
_savescoredb();
 };
 //BA.debugLineNum = 476;BA.debugLine="End Sub";
return "";
}
public static String  _count_counter() throws Exception{
 //BA.debugLineNum = 113;BA.debugLine="Sub count_counter()   '統計目前空格數之副程式";
 //BA.debugLineNum = 115;BA.debugLine="counter = 0";
_counter = (int) (0);
 //BA.debugLineNum = 116;BA.debugLine="For i = 0 To 8";
{
final int step98 = 1;
final int limit98 = (int) (8);
for (_i = (int) (0); (step98 > 0 && _i <= limit98) || (step98 < 0 && _i >= limit98); _i = ((int)(0 + _i + step98))) {
 //BA.debugLineNum = 117;BA.debugLine="If SaveXO(i) = 0 Then counter = counter + 1";
if (_savexo[_i]==0) { 
_counter = (int) (_counter+1);};
 }
};
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _count_win() throws Exception{
 //BA.debugLineNum = 334;BA.debugLine="Sub count_win()";
 //BA.debugLineNum = 335;BA.debugLine="Win(1) = SaveXO(0) + SaveXO(1) + SaveXO(2)  '3(您) or -3(電腦)";
_win[(int) (1)] = (int) (_savexo[(int) (0)]+_savexo[(int) (1)]+_savexo[(int) (2)]);
 //BA.debugLineNum = 336;BA.debugLine="Win(2) = SaveXO(3) + SaveXO(4) + SaveXO(5)  '3(您) or -3(電腦)";
_win[(int) (2)] = (int) (_savexo[(int) (3)]+_savexo[(int) (4)]+_savexo[(int) (5)]);
 //BA.debugLineNum = 337;BA.debugLine="Win(3) = SaveXO(6) + SaveXO(7) + SaveXO(8)  '3(您) or -3(電腦)";
_win[(int) (3)] = (int) (_savexo[(int) (6)]+_savexo[(int) (7)]+_savexo[(int) (8)]);
 //BA.debugLineNum = 338;BA.debugLine="Win(4) = SaveXO(0) + SaveXO(3) + SaveXO(6)  '3(您) or -3(電腦)";
_win[(int) (4)] = (int) (_savexo[(int) (0)]+_savexo[(int) (3)]+_savexo[(int) (6)]);
 //BA.debugLineNum = 339;BA.debugLine="Win(5) = SaveXO(1) + SaveXO(4) + SaveXO(7)  '3(您) or -3(電腦)";
_win[(int) (5)] = (int) (_savexo[(int) (1)]+_savexo[(int) (4)]+_savexo[(int) (7)]);
 //BA.debugLineNum = 340;BA.debugLine="Win(6) = SaveXO(2) + SaveXO(5) + SaveXO(8)  '3(您) or -3(電腦)";
_win[(int) (6)] = (int) (_savexo[(int) (2)]+_savexo[(int) (5)]+_savexo[(int) (8)]);
 //BA.debugLineNum = 341;BA.debugLine="Win(7) = SaveXO(0) + SaveXO(4) + SaveXO(8)  '3(您) or -3(電腦)";
_win[(int) (7)] = (int) (_savexo[(int) (0)]+_savexo[(int) (4)]+_savexo[(int) (8)]);
 //BA.debugLineNum = 342;BA.debugLine="Win(8) = SaveXO(2) + SaveXO(4) + SaveXO(6)  '3(您) or -3(電腦)";
_win[(int) (8)] = (int) (_savexo[(int) (2)]+_savexo[(int) (4)]+_savexo[(int) (6)]);
 //BA.debugLineNum = 343;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim btn1 As Button : Dim btn2 As Button";
mostCurrent._btn1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Dim btn1 As Button : Dim btn2 As Button";
mostCurrent._btn2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim btn3 As Button : Dim btn4 As Button";
mostCurrent._btn3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim btn3 As Button : Dim btn4 As Button";
mostCurrent._btn4 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim btn5 As Button : Dim btn6 As Button";
mostCurrent._btn5 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim btn5 As Button : Dim btn6 As Button";
mostCurrent._btn6 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim btn7 As Button : Dim btn8 As Button";
mostCurrent._btn7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim btn7 As Button : Dim btn8 As Button";
mostCurrent._btn8 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim btn9 As Button";
mostCurrent._btn9 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim Arraybtn() As Button            '宣告按鈕陣列";
mostCurrent._arraybtn = new anywheresoftware.b4a.objects.ButtonWrapper[(int) (0)];
{
int d0 = mostCurrent._arraybtn.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._arraybtn[i0] = new anywheresoftware.b4a.objects.ButtonWrapper();
}
}
;
 //BA.debugLineNum = 20;BA.debugLine="Dim SaveXO(9) As Int                '記錄目前的空格狀態";
_savexo = new int[(int) (9)];
;
 //BA.debugLineNum = 21;BA.debugLine="Dim xo(9) As String                 '存放資料格";
mostCurrent._xo = new String[(int) (9)];
java.util.Arrays.fill(mostCurrent._xo,"");
 //BA.debugLineNum = 22;BA.debugLine="Dim counter As Int                  '目前可下的空格數目";
_counter = 0;
 //BA.debugLineNum = 23;BA.debugLine="Dim Win(9) As Int                   '記錄八種勝利的狀態";
_win = new int[(int) (9)];
;
 //BA.debugLineNum = 24;BA.debugLine="Dim sp As Int";
_sp = 0;
 //BA.debugLineNum = 25;BA.debugLine="Dim i As Int                        '計數變數";
_i = 0;
 //BA.debugLineNum = 26;BA.debugLine="Dim btnBegin As Button              '重新開始";
mostCurrent._btnbegin = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim lblResutl As Label              '顯示標題及O贏或X贏";
mostCurrent._lblresutl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnColor_Gray As ColorDrawable  '灰色";
mostCurrent._btncolor_gray = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 29;BA.debugLine="Dim btnColor_Blue As ColorDrawable  '藍色";
mostCurrent._btncolor_blue = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 30;BA.debugLine="Dim btnExit As Button               '結束離開";
mostCurrent._btnexit = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim WinFlag As Boolean              '記錄目前是否O贏或X贏或平手";
_winflag = false;
 //BA.debugLineNum = 32;BA.debugLine="Dim O_Wins As Int                   'O贏場數";
_o_wins = 0;
 //BA.debugLineNum = 33;BA.debugLine="Dim X_Wins As Int                   'X贏場數";
_x_wins = 0;
 //BA.debugLineNum = 34;BA.debugLine="Dim Tie As Int                      '平手場數";
_tie = 0;
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _pcplay1() throws Exception{
int _r = 0;
 //BA.debugLineNum = 121;BA.debugLine="Sub PcPlay1()";
 //BA.debugLineNum = 122;BA.debugLine="Dim R As Int";
_r = 0;
 //BA.debugLineNum = 123;BA.debugLine="R = Rnd(0,9)";
_r = anywheresoftware.b4a.keywords.Common.Rnd((int) (0),(int) (9));
 //BA.debugLineNum = 124;BA.debugLine="For i=0 To 8";
{
final int step105 = 1;
final int limit105 = (int) (8);
for (_i = (int) (0); (step105 > 0 && _i <= limit105) || (step105 < 0 && _i >= limit105); _i = ((int)(0 + _i + step105))) {
 //BA.debugLineNum = 125;BA.debugLine="If SaveXO(i)=0 Then";
if (_savexo[_i]==0) { 
 //BA.debugLineNum = 126;BA.debugLine="Arraybtn(R).Text = \"X\"";
mostCurrent._arraybtn[_r].setText((Object)("X"));
 //BA.debugLineNum = 127;BA.debugLine="SaveXO(R) =-1";
_savexo[_r] = (int) (-1);
 };
 }
};
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static String  _pcplay2() throws Exception{
int _runtimes1 = 0;
int _runtimes2 = 0;
int _runtimes3 = 0;
String _sel = "";
String _num1 = "";
String _num2 = "";
String _num3 = "";
 //BA.debugLineNum = 132;BA.debugLine="Sub PcPlay2()";
 //BA.debugLineNum = 133;BA.debugLine="Dim sp As Int=0";
_sp = (int) (0);
 //BA.debugLineNum = 134;BA.debugLine="Dim Runtimes1 As Int=1";
_runtimes1 = (int) (1);
 //BA.debugLineNum = 135;BA.debugLine="Dim Runtimes2 As Int=1";
_runtimes2 = (int) (1);
 //BA.debugLineNum = 136;BA.debugLine="Dim Runtimes3 As Int=1";
_runtimes3 = (int) (1);
 //BA.debugLineNum = 137;BA.debugLine="Dim sel As String";
_sel = "";
 //BA.debugLineNum = 138;BA.debugLine="Dim num1,num2,num3 As String";
_num1 = "";
_num2 = "";
_num3 = "";
 //BA.debugLineNum = 140;BA.debugLine="xo(1) = \"012\"";
mostCurrent._xo[(int) (1)] = "012";
 //BA.debugLineNum = 141;BA.debugLine="xo(2) = \"345\"";
mostCurrent._xo[(int) (2)] = "345";
 //BA.debugLineNum = 142;BA.debugLine="xo(3) = \"678\"";
mostCurrent._xo[(int) (3)] = "678";
 //BA.debugLineNum = 143;BA.debugLine="xo(4) = \"036\"";
mostCurrent._xo[(int) (4)] = "036";
 //BA.debugLineNum = 144;BA.debugLine="xo(5) = \"147\"";
mostCurrent._xo[(int) (5)] = "147";
 //BA.debugLineNum = 145;BA.debugLine="xo(6) = \"258\"";
mostCurrent._xo[(int) (6)] = "258";
 //BA.debugLineNum = 146;BA.debugLine="xo(7) = \"048\"";
mostCurrent._xo[(int) (7)] = "048";
 //BA.debugLineNum = 147;BA.debugLine="xo(8) = \"246\"";
mostCurrent._xo[(int) (8)] = "246";
 //BA.debugLineNum = 148;BA.debugLine="count_counter  '檢查目前的空格數目";
_count_counter();
 //BA.debugLineNum = 150;BA.debugLine="If counter = 6 Then";
if (_counter==6) { 
 //BA.debugLineNum = 152;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(0) = 1 AND SaveXO(8) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (0)]==1 && _savexo[(int) (8)]==1) { 
 //BA.debugLineNum = 153;BA.debugLine="Arraybtn(3).Text = \"X\"";
mostCurrent._arraybtn[(int) (3)].setText((Object)("X"));
 //BA.debugLineNum = 154;BA.debugLine="SaveXO(3) = -1 :sp=1";
_savexo[(int) (3)] = (int) (-1);
 //BA.debugLineNum = 154;BA.debugLine="SaveXO(3) = -1 :sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 156;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(2) = 1 AND SaveXO(6) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (2)]==1 && _savexo[(int) (6)]==1) { 
 //BA.debugLineNum = 157;BA.debugLine="Arraybtn(3).Text = \"X\"";
mostCurrent._arraybtn[(int) (3)].setText((Object)("X"));
 //BA.debugLineNum = 158;BA.debugLine="SaveXO(3) = -1:sp=1";
_savexo[(int) (3)] = (int) (-1);
 //BA.debugLineNum = 158;BA.debugLine="SaveXO(3) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 161;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(1) = 1 AND SaveXO(3) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (1)]==1 && _savexo[(int) (3)]==1) { 
 //BA.debugLineNum = 162;BA.debugLine="Arraybtn(0).Text = \"X\"";
mostCurrent._arraybtn[(int) (0)].setText((Object)("X"));
 //BA.debugLineNum = 163;BA.debugLine="SaveXO(0) = -1:sp=1";
_savexo[(int) (0)] = (int) (-1);
 //BA.debugLineNum = 163;BA.debugLine="SaveXO(0) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 165;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(1) = 1 AND SaveXO(5) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (1)]==1 && _savexo[(int) (5)]==1) { 
 //BA.debugLineNum = 166;BA.debugLine="Arraybtn(2).Text = \"X\"";
mostCurrent._arraybtn[(int) (2)].setText((Object)("X"));
 //BA.debugLineNum = 167;BA.debugLine="SaveXO(2) = -1:sp=1";
_savexo[(int) (2)] = (int) (-1);
 //BA.debugLineNum = 167;BA.debugLine="SaveXO(2) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 170;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(3) = 1 AND SaveXO(7) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (3)]==1 && _savexo[(int) (7)]==1) { 
 //BA.debugLineNum = 171;BA.debugLine="Arraybtn(6).Text = \"X\"";
mostCurrent._arraybtn[(int) (6)].setText((Object)("X"));
 //BA.debugLineNum = 172;BA.debugLine="SaveXO(6) = -1:sp=1";
_savexo[(int) (6)] = (int) (-1);
 //BA.debugLineNum = 172;BA.debugLine="SaveXO(6) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 174;BA.debugLine="If SaveXO(4) = -1 AND SaveXO(5) = 1 AND SaveXO(7) = 1 Then";
if (_savexo[(int) (4)]==-1 && _savexo[(int) (5)]==1 && _savexo[(int) (7)]==1) { 
 //BA.debugLineNum = 175;BA.debugLine="Arraybtn(8).Text = \"X\"";
mostCurrent._arraybtn[(int) (8)].setText((Object)("X"));
 //BA.debugLineNum = 176;BA.debugLine="SaveXO(8) = -1:sp=1";
_savexo[(int) (8)] = (int) (-1);
 //BA.debugLineNum = 176;BA.debugLine="SaveXO(8) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 180;BA.debugLine="If SaveXO(4) = 1 AND SaveXO(0) = -1 AND SaveXO(8) = 1 Then";
if (_savexo[(int) (4)]==1 && _savexo[(int) (0)]==-1 && _savexo[(int) (8)]==1) { 
 //BA.debugLineNum = 181;BA.debugLine="Arraybtn(3).Text = \"X\"";
mostCurrent._arraybtn[(int) (3)].setText((Object)("X"));
 //BA.debugLineNum = 182;BA.debugLine="SaveXO(3) = -1:sp=1";
_savexo[(int) (3)] = (int) (-1);
 //BA.debugLineNum = 182;BA.debugLine="SaveXO(3) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 184;BA.debugLine="If SaveXO(4) = 1 AND SaveXO(6) = 1 AND SaveXO(2) = -1 Then";
if (_savexo[(int) (4)]==1 && _savexo[(int) (6)]==1 && _savexo[(int) (2)]==-1) { 
 //BA.debugLineNum = 185;BA.debugLine="Arraybtn(5).Text = \"X\"";
mostCurrent._arraybtn[(int) (5)].setText((Object)("X"));
 //BA.debugLineNum = 186;BA.debugLine="SaveXO(5) = -1:sp=1";
_savexo[(int) (5)] = (int) (-1);
 //BA.debugLineNum = 186;BA.debugLine="SaveXO(5) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 188;BA.debugLine="If SaveXO(3) = 1 AND SaveXO(6) = 1 AND SaveXO(6) = -1 Then";
if (_savexo[(int) (3)]==1 && _savexo[(int) (6)]==1 && _savexo[(int) (6)]==-1) { 
 //BA.debugLineNum = 189;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 190;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 190;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 192;BA.debugLine="If SaveXO(0) = 1 AND SaveXO(6) = 1 AND SaveXO(3) = -1 Then";
if (_savexo[(int) (0)]==1 && _savexo[(int) (6)]==1 && _savexo[(int) (3)]==-1) { 
 //BA.debugLineNum = 193;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 194;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 194;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 196;BA.debugLine="If SaveXO(0) = 1 AND SaveXO(3) = 1 AND SaveXO(6) = -1 Then";
if (_savexo[(int) (0)]==1 && _savexo[(int) (3)]==1 && _savexo[(int) (6)]==-1) { 
 //BA.debugLineNum = 197;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 198;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 198;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 200;BA.debugLine="If SaveXO(4) = 1 AND SaveXO(7) = 1 AND SaveXO(1) = -1 Then";
if (_savexo[(int) (4)]==1 && _savexo[(int) (7)]==1 && _savexo[(int) (1)]==-1) { 
 //BA.debugLineNum = 201;BA.debugLine="Arraybtn(0).Text = \"X\"";
mostCurrent._arraybtn[(int) (0)].setText((Object)("X"));
 //BA.debugLineNum = 202;BA.debugLine="SaveXO(0) = -1:sp=1";
_savexo[(int) (0)] = (int) (-1);
 //BA.debugLineNum = 202;BA.debugLine="SaveXO(0) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 204;BA.debugLine="If SaveXO(1) = 1 AND SaveXO(7) = 1 AND SaveXO(4) = -1 Then";
if (_savexo[(int) (1)]==1 && _savexo[(int) (7)]==1 && _savexo[(int) (4)]==-1) { 
 //BA.debugLineNum = 205;BA.debugLine="Arraybtn(3).Text = \"X\"";
mostCurrent._arraybtn[(int) (3)].setText((Object)("X"));
 //BA.debugLineNum = 206;BA.debugLine="SaveXO(3) = -1:sp=1";
_savexo[(int) (3)] = (int) (-1);
 //BA.debugLineNum = 206;BA.debugLine="SaveXO(3) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 208;BA.debugLine="If SaveXO(1) = 1 AND SaveXO(4) = 1 AND SaveXO(7) = -1 Then";
if (_savexo[(int) (1)]==1 && _savexo[(int) (4)]==1 && _savexo[(int) (7)]==-1) { 
 //BA.debugLineNum = 209;BA.debugLine="Arraybtn(6).Text = \"X\"";
mostCurrent._arraybtn[(int) (6)].setText((Object)("X"));
 //BA.debugLineNum = 210;BA.debugLine="SaveXO(6) = -1:sp=1";
_savexo[(int) (6)] = (int) (-1);
 //BA.debugLineNum = 210;BA.debugLine="SaveXO(6) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 212;BA.debugLine="If SaveXO(5) = 1 AND SaveXO(8) = 1 AND SaveXO(2) = -1 Then";
if (_savexo[(int) (5)]==1 && _savexo[(int) (8)]==1 && _savexo[(int) (2)]==-1) { 
 //BA.debugLineNum = 213;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 214;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 214;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 216;BA.debugLine="If SaveXO(2) = 1 AND SaveXO(8) = 1 AND SaveXO(5) = -1 Then";
if (_savexo[(int) (2)]==1 && _savexo[(int) (8)]==1 && _savexo[(int) (5)]==-1) { 
 //BA.debugLineNum = 217;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 218;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 218;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 220;BA.debugLine="If SaveXO(2) = 1 AND SaveXO(5) = 1 AND SaveXO(8) = -1 Then";
if (_savexo[(int) (2)]==1 && _savexo[(int) (5)]==1 && _savexo[(int) (8)]==-1) { 
 //BA.debugLineNum = 221;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 222;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 222;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 227;BA.debugLine="If SaveXO(1) = 1 AND SaveXO(2) = 1 AND SaveXO(0) = -1 Then";
if (_savexo[(int) (1)]==1 && _savexo[(int) (2)]==1 && _savexo[(int) (0)]==-1) { 
 //BA.debugLineNum = 228;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 229;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 229;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 231;BA.debugLine="If SaveXO(0) = 1 AND SaveXO(2) = 1 AND SaveXO(1) = -1 Then";
if (_savexo[(int) (0)]==1 && _savexo[(int) (2)]==1 && _savexo[(int) (1)]==-1) { 
 //BA.debugLineNum = 232;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 233;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 233;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 235;BA.debugLine="If SaveXO(0) = 1 AND SaveXO(1) = 1 AND SaveXO(2) = -1 Then";
if (_savexo[(int) (0)]==1 && _savexo[(int) (1)]==1 && _savexo[(int) (2)]==-1) { 
 //BA.debugLineNum = 236;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 237;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 237;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 240;BA.debugLine="If SaveXO(4) = 1 AND SaveXO(5) = 1 AND SaveXO(3) = -1 Then";
if (_savexo[(int) (4)]==1 && _savexo[(int) (5)]==1 && _savexo[(int) (3)]==-1) { 
 //BA.debugLineNum = 241;BA.debugLine="Arraybtn(6).Text = \"X\"";
mostCurrent._arraybtn[(int) (6)].setText((Object)("X"));
 //BA.debugLineNum = 242;BA.debugLine="SaveXO(6) = -1:sp=1";
_savexo[(int) (6)] = (int) (-1);
 //BA.debugLineNum = 242;BA.debugLine="SaveXO(6) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 244;BA.debugLine="If SaveXO(3) = 1 AND SaveXO(5) = 1 AND SaveXO(4) = -1 Then";
if (_savexo[(int) (3)]==1 && _savexo[(int) (5)]==1 && _savexo[(int) (4)]==-1) { 
 //BA.debugLineNum = 245;BA.debugLine="Arraybtn(8).Text = \"X\"";
mostCurrent._arraybtn[(int) (8)].setText((Object)("X"));
 //BA.debugLineNum = 246;BA.debugLine="SaveXO(8) = -1:sp=1";
_savexo[(int) (8)] = (int) (-1);
 //BA.debugLineNum = 246;BA.debugLine="SaveXO(8) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 248;BA.debugLine="If SaveXO(3) = 1 AND SaveXO(4) = 1 AND SaveXO(2) = -1 Then";
if (_savexo[(int) (3)]==1 && _savexo[(int) (4)]==1 && _savexo[(int) (2)]==-1) { 
 //BA.debugLineNum = 249;BA.debugLine="Arraybtn(8).Text = \"X\"";
mostCurrent._arraybtn[(int) (8)].setText((Object)("X"));
 //BA.debugLineNum = 250;BA.debugLine="SaveXO(8) = -1:sp=1";
_savexo[(int) (8)] = (int) (-1);
 //BA.debugLineNum = 250;BA.debugLine="SaveXO(8) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 253;BA.debugLine="If SaveXO(7) = 1 AND SaveXO(8) = 1 AND SaveXO(6) = -1 Then";
if (_savexo[(int) (7)]==1 && _savexo[(int) (8)]==1 && _savexo[(int) (6)]==-1) { 
 //BA.debugLineNum = 254;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 255;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 255;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 257;BA.debugLine="If SaveXO(6) = 1 AND SaveXO(8) = 1 AND SaveXO(7) = -1 Then";
if (_savexo[(int) (6)]==1 && _savexo[(int) (8)]==1 && _savexo[(int) (7)]==-1) { 
 //BA.debugLineNum = 258;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 259;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 259;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 261;BA.debugLine="If SaveXO(6) = 1 AND SaveXO(7) = 1 AND SaveXO(8) = -1 Then";
if (_savexo[(int) (6)]==1 && _savexo[(int) (7)]==1 && _savexo[(int) (8)]==-1) { 
 //BA.debugLineNum = 262;BA.debugLine="Arraybtn(4).Text = \"X\"";
mostCurrent._arraybtn[(int) (4)].setText((Object)("X"));
 //BA.debugLineNum = 263;BA.debugLine="SaveXO(4) = -1:sp=1";
_savexo[(int) (4)] = (int) (-1);
 //BA.debugLineNum = 263;BA.debugLine="SaveXO(4) = -1:sp=1";
_sp = (int) (1);
 };
 //BA.debugLineNum = 265;BA.debugLine="Runtimes1=Runtimes1+1";
_runtimes1 = (int) (_runtimes1+1);
 };
 //BA.debugLineNum = 268;BA.debugLine="count_win";
_count_win();
 //BA.debugLineNum = 269;BA.debugLine="CheckWin";
_checkwin();
 //BA.debugLineNum = 271;BA.debugLine="For i = 1 To 8";
{
final int step263 = 1;
final int limit263 = (int) (8);
for (_i = (int) (1); (step263 > 0 && _i <= limit263) || (step263 < 0 && _i >= limit263); _i = ((int)(0 + _i + step263))) {
 //BA.debugLineNum = 272;BA.debugLine="If Win(i) = -2 AND (Runtimes1=1) AND (Runtimes2=1) AND (Runtimes3=1) Then";
if (_win[_i]==-2 && (_runtimes1==1) && (_runtimes2==1) && (_runtimes3==1)) { 
 //BA.debugLineNum = 273;BA.debugLine="sp=1";
_sp = (int) (1);
 //BA.debugLineNum = 274;BA.debugLine="sel = xo(i)";
_sel = mostCurrent._xo[_i];
 //BA.debugLineNum = 275;BA.debugLine="num1 = sel.CharAt(0)";
_num1 = BA.ObjectToString(_sel.charAt((int) (0)));
 //BA.debugLineNum = 276;BA.debugLine="num2 = sel.CharAt(1)";
_num2 = BA.ObjectToString(_sel.charAt((int) (1)));
 //BA.debugLineNum = 277;BA.debugLine="num3 = sel.CharAt(2)";
_num3 = BA.ObjectToString(_sel.charAt((int) (2)));
 //BA.debugLineNum = 278;BA.debugLine="If SaveXO(num1) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num1))]==0) { 
 //BA.debugLineNum = 279;BA.debugLine="Arraybtn(num1).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num1))].setText((Object)("X"));
 //BA.debugLineNum = 280;BA.debugLine="SaveXO(num1) = -1";
_savexo[(int)(Double.parseDouble(_num1))] = (int) (-1);
 };
 //BA.debugLineNum = 282;BA.debugLine="If SaveXO(num2) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num2))]==0) { 
 //BA.debugLineNum = 283;BA.debugLine="Arraybtn(num2).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num2))].setText((Object)("X"));
 //BA.debugLineNum = 284;BA.debugLine="SaveXO(num2) = -1";
_savexo[(int)(Double.parseDouble(_num2))] = (int) (-1);
 };
 //BA.debugLineNum = 286;BA.debugLine="If SaveXO(num3) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num3))]==0) { 
 //BA.debugLineNum = 287;BA.debugLine="Arraybtn(num3).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num3))].setText((Object)("X"));
 //BA.debugLineNum = 288;BA.debugLine="SaveXO(num3) = -1";
_savexo[(int)(Double.parseDouble(_num3))] = (int) (-1);
 };
 //BA.debugLineNum = 290;BA.debugLine="Runtimes2=Runtimes2+1";
_runtimes2 = (int) (_runtimes2+1);
 //BA.debugLineNum = 291;BA.debugLine="CheckWin";
_checkwin();
 };
 }
};
 //BA.debugLineNum = 294;BA.debugLine="count_win";
_count_win();
 //BA.debugLineNum = 296;BA.debugLine="For i = 1 To 8";
{
final int step287 = 1;
final int limit287 = (int) (8);
for (_i = (int) (1); (step287 > 0 && _i <= limit287) || (step287 < 0 && _i >= limit287); _i = ((int)(0 + _i + step287))) {
 //BA.debugLineNum = 297;BA.debugLine="If Win(i) = 2 AND (Runtimes2=1) AND (Runtimes3=1) Then";
if (_win[_i]==2 && (_runtimes2==1) && (_runtimes3==1)) { 
 //BA.debugLineNum = 298;BA.debugLine="sp=1";
_sp = (int) (1);
 //BA.debugLineNum = 299;BA.debugLine="sel = xo(i)";
_sel = mostCurrent._xo[_i];
 //BA.debugLineNum = 300;BA.debugLine="num1 = sel.CharAt(0)";
_num1 = BA.ObjectToString(_sel.charAt((int) (0)));
 //BA.debugLineNum = 301;BA.debugLine="num2 = sel.CharAt(1)";
_num2 = BA.ObjectToString(_sel.charAt((int) (1)));
 //BA.debugLineNum = 302;BA.debugLine="num3 = sel.CharAt(2)";
_num3 = BA.ObjectToString(_sel.charAt((int) (2)));
 //BA.debugLineNum = 303;BA.debugLine="If SaveXO(num1) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num1))]==0) { 
 //BA.debugLineNum = 304;BA.debugLine="Arraybtn(num1).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num1))].setText((Object)("X"));
 //BA.debugLineNum = 305;BA.debugLine="SaveXO(num1) = -1";
_savexo[(int)(Double.parseDouble(_num1))] = (int) (-1);
 };
 //BA.debugLineNum = 307;BA.debugLine="If SaveXO(num2) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num2))]==0) { 
 //BA.debugLineNum = 308;BA.debugLine="Arraybtn(num2).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num2))].setText((Object)("X"));
 //BA.debugLineNum = 309;BA.debugLine="SaveXO(num2) = -1";
_savexo[(int)(Double.parseDouble(_num2))] = (int) (-1);
 };
 //BA.debugLineNum = 311;BA.debugLine="If SaveXO(num3) = 0 Then";
if (_savexo[(int)(Double.parseDouble(_num3))]==0) { 
 //BA.debugLineNum = 312;BA.debugLine="Arraybtn(num3).Text = \"X\"";
mostCurrent._arraybtn[(int)(Double.parseDouble(_num3))].setText((Object)("X"));
 //BA.debugLineNum = 313;BA.debugLine="SaveXO(num3) = -1";
_savexo[(int)(Double.parseDouble(_num3))] = (int) (-1);
 };
 //BA.debugLineNum = 315;BA.debugLine="Runtimes3=Runtimes3+1";
_runtimes3 = (int) (_runtimes3+1);
 };
 }
};
 //BA.debugLineNum = 318;BA.debugLine="Runtimes2=1";
_runtimes2 = (int) (1);
 //BA.debugLineNum = 319;BA.debugLine="Runtimes3=1";
_runtimes3 = (int) (1);
 //BA.debugLineNum = 320;BA.debugLine="If sp=0 Then";
if (_sp==0) { 
 //BA.debugLineNum = 321;BA.debugLine="PcPlay3";
_pcplay3();
 };
 //BA.debugLineNum = 323;BA.debugLine="End Sub";
return "";
}
public static String  _pcplay3() throws Exception{
 //BA.debugLineNum = 325;BA.debugLine="Sub PcPlay3()";
 //BA.debugLineNum = 326;BA.debugLine="For i=0 To 8";
{
final int step316 = 1;
final int limit316 = (int) (8);
for (_i = (int) (0); (step316 > 0 && _i <= limit316) || (step316 < 0 && _i >= limit316); _i = ((int)(0 + _i + step316))) {
 //BA.debugLineNum = 327;BA.debugLine="If SaveXO(i)=0 Then";
if (_savexo[_i]==0) { 
 //BA.debugLineNum = 328;BA.debugLine="Arraybtn(i).Text = \"X\"";
mostCurrent._arraybtn[_i].setText((Object)("X"));
 //BA.debugLineNum = 329;BA.debugLine="SaveXO(i) =-1";
_savexo[_i] = (int) (-1);
 //BA.debugLineNum = 330;BA.debugLine="Exit";
if (true) break;
 };
 }
};
 //BA.debugLineNum = 333;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                   '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor         '宣告資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
public static String  _queryscore() throws Exception{
String _strsqlite = "";
 //BA.debugLineNum = 478;BA.debugLine="Sub QueryScore() '查詢目前戰狀成績";
 //BA.debugLineNum = 479;BA.debugLine="Msgbox(lblResutl.Text,\"遊戲結果!\")";
anywheresoftware.b4a.keywords.Common.Msgbox(mostCurrent._lblresutl.getText(),"遊戲結果!",mostCurrent.activityBA);
 //BA.debugLineNum = 480;BA.debugLine="For i=0 To 8";
{
final int step448 = 1;
final int limit448 = (int) (8);
for (_i = (int) (0); (step448 > 0 && _i <= limit448) || (step448 < 0 && _i >= limit448); _i = ((int)(0 + _i + step448))) {
 //BA.debugLineNum = 481;BA.debugLine="Arraybtn(i).Enabled = False";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 //BA.debugLineNum = 483;BA.debugLine="btnBegin.Enabled =True";
mostCurrent._btnbegin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 484;BA.debugLine="WinFlag=True";
_winflag = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 485;BA.debugLine="Dim strSQLite As String=\"Select * FROM 遊戲歷程表 where 對戰方式='電腦對戰'\"";
_strsqlite = "Select * FROM 遊戲歷程表 where 對戰方式='電腦對戰'";
 //BA.debugLineNum = 486;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)       '執行SQL指令";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 487;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數";
{
final int step455 = 1;
final int limit455 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step455 > 0 && _i <= limit455) || (step455 < 0 && _i >= limit455); _i = ((int)(0 + _i + step455))) {
 //BA.debugLineNum = 488;BA.debugLine="CursorDBPoint.Position = i                  '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 489;BA.debugLine="O_Wins=CursorDBPoint.GetString(\"O贏\")   '取得「O贏」的次數";
_o_wins = (int)(Double.parseDouble(_cursordbpoint.GetString("O贏")));
 //BA.debugLineNum = 490;BA.debugLine="X_Wins=CursorDBPoint.GetString(\"X贏\")   '取得「X贏」的次數";
_x_wins = (int)(Double.parseDouble(_cursordbpoint.GetString("X贏")));
 //BA.debugLineNum = 491;BA.debugLine="Tie=CursorDBPoint.GetString(\"平手\")     '取得「平手」的次數";
_tie = (int)(Double.parseDouble(_cursordbpoint.GetString("平手")));
 }
};
 //BA.debugLineNum = 493;BA.debugLine="End Sub";
return "";
}
public static String  _savescoredb() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 495;BA.debugLine="Sub SaveScoreDB()  '儲存成績副程式";
 //BA.debugLineNum = 496;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 497;BA.debugLine="strSQL = \"UPDATE 遊戲歷程表 SET O贏='\" & O_Wins & \"',X贏='\" & X_Wins & \"',平手='\" & Tie & \"' where 對戰方式='電腦對戰'\"";
_strsql = "UPDATE 遊戲歷程表 SET O贏='"+BA.NumberToString(_o_wins)+"',X贏='"+BA.NumberToString(_x_wins)+"',平手='"+BA.NumberToString(_tie)+"' where 對戰方式='電腦對戰'";
 //BA.debugLineNum = 498;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 499;BA.debugLine="ToastMessageShow(\"更新最新成績資料(到資料庫中)...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新最新成績資料(到資料庫中)...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 500;BA.debugLine="End Sub";
return "";
}
}
