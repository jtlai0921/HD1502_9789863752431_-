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

public class play1 extends Activity implements B4AActivity{
	public static play1 mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.PalyGame4", "Leech.myebook.PalyGame4.play1");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (play1).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.PalyGame4", "Leech.myebook.PalyGame4.play1");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.PalyGame4.play1", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (play1) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (play1) Resume **");
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
		return play1.class;
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
        BA.LogInfo("** Activity (play1) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (play1) Resume **");
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
public static String[] _xo = null;
public static boolean _oxflag = false;
public anywheresoftware.b4a.objects.ButtonWrapper _btnbegin = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresutl = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _btncolor_gray = null;
public anywheresoftware.b4a.objects.drawable.ColorDrawable _btncolor_blue = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexit = null;
public static int _o_wins = 0;
public static int _x_wins = 0;
public static int _tie = 0;
public static int _i = 0;
public Leech.myebook.PalyGame4.main _main = null;
public Leech.myebook.PalyGame4.play2 _play2 = null;
public Leech.myebook.PalyGame4.scorelist _scorelist = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 36;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 37;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyGameScore.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyGameScore.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 39;BA.debugLine="Activity.LoadLayout(\"Play1\")                   '使用「Play1(版面配置檔)」來輸出";
mostCurrent._activity.LoadLayout("Play1",mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="Activity.Title = \"兩人對戰\"                    '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("兩人對戰"));
 //BA.debugLineNum = 41;BA.debugLine="Arraybtn =Array As Button(btn1,btn2,btn3,btn4,btn5,btn6,btn7,btn8,btn9) '按鈕陣列";
mostCurrent._arraybtn = new anywheresoftware.b4a.objects.ButtonWrapper[]{mostCurrent._btn1,mostCurrent._btn2,mostCurrent._btn3,mostCurrent._btn4,mostCurrent._btn5,mostCurrent._btn6,mostCurrent._btn7,mostCurrent._btn8,mostCurrent._btn9};
 //BA.debugLineNum = 42;BA.debugLine="btnColor_Gray.Initialize(Colors.Gray,10dip)    '灰色";
mostCurrent._btncolor_gray.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Gray,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 43;BA.debugLine="btnColor_Blue.Initialize(Colors.Yellow,10dip)  '黃色";
mostCurrent._btncolor_blue.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Yellow,anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)));
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 48;BA.debugLine="For i=0 To 8";
{
final int step38 = 1;
final int limit38 = (int) (8);
for (_i = (int) (0); (step38 > 0 && _i <= limit38) || (step38 < 0 && _i >= limit38); _i = ((int)(0 + _i + step38))) {
 //BA.debugLineNum = 49;BA.debugLine="Arraybtn(i).Enabled = False";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _btnbegin_click() throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub btnBegin_Click  '重新開始";
 //BA.debugLineNum = 58;BA.debugLine="For i=0 To 8";
{
final int step45 = 1;
final int limit45 = (int) (8);
for (_i = (int) (0); (step45 > 0 && _i <= limit45) || (step45 < 0 && _i >= limit45); _i = ((int)(0 + _i + step45))) {
 //BA.debugLineNum = 59;BA.debugLine="Arraybtn(i).Enabled = True            '設定井字遊戲中的九個按鈕有作用";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 60;BA.debugLine="Arraybtn(i).Background=btnColor_Gray  '灰色";
mostCurrent._arraybtn[_i].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_gray.getObject()));
 //BA.debugLineNum = 61;BA.debugLine="Arraybtn(i).Text =\"\"                  '清除O與X的文字內容";
mostCurrent._arraybtn[_i].setText((Object)(""));
 //BA.debugLineNum = 62;BA.debugLine="xo(i)=0                               '存放資料格預設值為0";
mostCurrent._xo[_i] = BA.NumberToString(0);
 }
};
 //BA.debugLineNum = 64;BA.debugLine="oxFlag = True                           '設定第一次在按下滑鼠左鍵時會得到O(第一個玩家設為O)";
_oxflag = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 65;BA.debugLine="btnBegin.Enabled =False                 '「重新開始」按鈕沒作用";
mostCurrent._btnbegin.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 66;BA.debugLine="lblResutl.Text=\"==井字遊戲==\"           '顯示「井字遊戲」";
mostCurrent._lblresutl.setText((Object)("==井字遊戲=="));
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _btnexit_click() throws Exception{
 //BA.debugLineNum = 247;BA.debugLine="Sub btnExit_Click";
 //BA.debugLineNum = 248;BA.debugLine="Activity.Finish()      '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 249;BA.debugLine="StartActivity(Main)    '回首頁";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 250;BA.debugLine="End Sub";
return "";
}
public static String  _button_click() throws Exception{
anywheresoftware.b4a.objects.ButtonWrapper _send = null;
 //BA.debugLineNum = 70;BA.debugLine="Sub Button_Click";
 //BA.debugLineNum = 71;BA.debugLine="Dim Send As Button";
_send = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 72;BA.debugLine="Send=Sender   '取得事件來源";
_send.setObject((android.widget.Button)(anywheresoftware.b4a.keywords.Common.Sender(mostCurrent.activityBA)));
 //BA.debugLineNum = 74;BA.debugLine="If xo(Send.Tag)=0 Then                '如果是空格";
if ((mostCurrent._xo[(int)(BA.ObjectToNumber(_send.getTag()))]).equals(BA.NumberToString(0))) { 
 //BA.debugLineNum = 75;BA.debugLine="If oxFlag = True Then";
if (_oxflag==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 76;BA.debugLine="Arraybtn(Send.Tag).Text = \"O\"    '第一個玩家設為O，並顯示O在按鈕上";
mostCurrent._arraybtn[(int)(BA.ObjectToNumber(_send.getTag()))].setText((Object)("O"));
 //BA.debugLineNum = 77;BA.debugLine="xo(Send.Tag) = 1                 '在按下時，填入1到指定的資料格中";
mostCurrent._xo[(int)(BA.ObjectToNumber(_send.getTag()))] = BA.NumberToString(1);
 }else {
 //BA.debugLineNum = 79;BA.debugLine="Arraybtn(Send.Tag).Text = \"X\"    '第二個玩家設為X，並顯示X在按鈕上";
mostCurrent._arraybtn[(int)(BA.ObjectToNumber(_send.getTag()))].setText((Object)("X"));
 //BA.debugLineNum = 80;BA.debugLine="xo(Send.Tag) = 2                 '在按下時，填入2到指定的資料格中";
mostCurrent._xo[(int)(BA.ObjectToNumber(_send.getTag()))] = BA.NumberToString(2);
 };
 //BA.debugLineNum = 82;BA.debugLine="oxFlag = Not(oxFlag)                '作O及X的切換動作";
_oxflag = anywheresoftware.b4a.keywords.Common.Not(_oxflag);
 //BA.debugLineNum = 83;BA.debugLine="CheckWin                            '檢查O或X贏";
_checkwin();
 }else {
 //BA.debugLineNum = 85;BA.debugLine="Msgbox (\"此地已下過,不可再下!!\",\"錯誤\")";
anywheresoftware.b4a.keywords.Common.Msgbox("此地已下過,不可再下!!","錯誤",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _checkwin() throws Exception{
 //BA.debugLineNum = 89;BA.debugLine="Sub CheckWin  '檢查O或X贏";
 //BA.debugLineNum = 91;BA.debugLine="If xo(0) * xo(1) * xo(2) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (1)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))==1) { 
 //BA.debugLineNum = 92;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 93;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 94;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 95;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 98;BA.debugLine="If xo(3) * xo(4) * xo(5) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (3)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (5)]))==1) { 
 //BA.debugLineNum = 99;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 100;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 101;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 102;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 105;BA.debugLine="If xo(6) * xo(7) * xo(8) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (7)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==1) { 
 //BA.debugLineNum = 106;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 107;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 108;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 109;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 113;BA.debugLine="If xo(0) * xo(3) * xo(6) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (3)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))==1) { 
 //BA.debugLineNum = 114;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 115;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 116;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 117;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 120;BA.debugLine="If xo(1) * xo(4) * xo(7) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (1)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (7)]))==1) { 
 //BA.debugLineNum = 121;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 122;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 123;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 124;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 127;BA.debugLine="If xo(2) * xo(5) * xo(8) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (5)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==1) { 
 //BA.debugLineNum = 128;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 129;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 130;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 131;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 134;BA.debugLine="If xo(0) * xo(4) * xo(8) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==1) { 
 //BA.debugLineNum = 135;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 136;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 137;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 138;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 141;BA.debugLine="If xo(2) * xo(4) * xo(6) = 1 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))==1) { 
 //BA.debugLineNum = 142;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 143;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 144;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 145;BA.debugLine="lblResutl.Text = \"O贏!\"";
mostCurrent._lblresutl.setText((Object)("O贏!"));
 };
 //BA.debugLineNum = 148;BA.debugLine="If xo(0) * xo(1) * xo(2) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (1)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))==8) { 
 //BA.debugLineNum = 149;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 150;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 151;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 152;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 155;BA.debugLine="If xo(3) * xo(4) * xo(5) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (3)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (5)]))==8) { 
 //BA.debugLineNum = 156;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 157;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 158;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 159;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 162;BA.debugLine="If xo(6) * xo(7) * xo(8) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (7)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==8) { 
 //BA.debugLineNum = 163;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 164;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 165;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 166;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 169;BA.debugLine="If xo(0) * xo(3) * xo(6) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (3)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))==8) { 
 //BA.debugLineNum = 170;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 171;BA.debugLine="Arraybtn(3).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (3)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 172;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 173;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 176;BA.debugLine="If xo(1) * xo(4) * xo(7) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (1)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (7)]))==8) { 
 //BA.debugLineNum = 177;BA.debugLine="Arraybtn(1).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (1)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 178;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 179;BA.debugLine="Arraybtn(7).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (7)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 180;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 183;BA.debugLine="If xo(2) * xo(5) * xo(8) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (5)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==8) { 
 //BA.debugLineNum = 184;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 185;BA.debugLine="Arraybtn(5).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (5)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 186;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 187;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 190;BA.debugLine="If xo(0) * xo(4) * xo(8) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (0)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (8)]))==8) { 
 //BA.debugLineNum = 191;BA.debugLine="Arraybtn(0).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (0)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 192;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 193;BA.debugLine="Arraybtn(8).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (8)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 194;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 197;BA.debugLine="If xo(2) * xo(4) * xo(6) = 8 Then";
if ((double)(Double.parseDouble(mostCurrent._xo[(int) (2)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (4)]))*(double)(Double.parseDouble(mostCurrent._xo[(int) (6)]))==8) { 
 //BA.debugLineNum = 198;BA.debugLine="Arraybtn(2).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (2)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 199;BA.debugLine="Arraybtn(4).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (4)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 200;BA.debugLine="Arraybtn(6).Background=btnColor_Blue";
mostCurrent._arraybtn[(int) (6)].setBackground((android.graphics.drawable.Drawable)(mostCurrent._btncolor_blue.getObject()));
 //BA.debugLineNum = 201;BA.debugLine="lblResutl.Text = \"X贏!\"";
mostCurrent._lblresutl.setText((Object)("X贏!"));
 };
 //BA.debugLineNum = 203;BA.debugLine="If (lblResutl.Text<>\"== 井字遊戲==\") AND( lblResutl.Text=\"O贏!\" OR lblResutl.Text = \"X贏!\" OR lblResutl.Text = \"平手!\" ) Then";
if (((mostCurrent._lblresutl.getText()).equals("== 井字遊戲==") == false) && ((mostCurrent._lblresutl.getText()).equals("O贏!") || (mostCurrent._lblresutl.getText()).equals("X贏!") || (mostCurrent._lblresutl.getText()).equals("平手!"))) { 
 //BA.debugLineNum = 204;BA.debugLine="Msgbox(lblResutl.Text,\"遊戲結果!\")";
anywheresoftware.b4a.keywords.Common.Msgbox(mostCurrent._lblresutl.getText(),"遊戲結果!",mostCurrent.activityBA);
 //BA.debugLineNum = 205;BA.debugLine="For i=0 To 8";
{
final int step171 = 1;
final int limit171 = (int) (8);
for (_i = (int) (0); (step171 > 0 && _i <= limit171) || (step171 < 0 && _i >= limit171); _i = ((int)(0 + _i + step171))) {
 //BA.debugLineNum = 206;BA.debugLine="Arraybtn(i).Enabled = False";
mostCurrent._arraybtn[_i].setEnabled(anywheresoftware.b4a.keywords.Common.False);
 }
};
 //BA.debugLineNum = 208;BA.debugLine="btnBegin.Enabled =True";
mostCurrent._btnbegin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 209;BA.debugLine="QueryScore  '查詢目前戰狀成績";
_queryscore();
 //BA.debugLineNum = 210;BA.debugLine="Select lblResutl.Text";
switch (BA.switchObjectToInt(mostCurrent._lblresutl.getText(),"O贏!","X贏!")) {
case 0:
 //BA.debugLineNum = 212;BA.debugLine="O_Wins=O_Wins+1";
_o_wins = (int) (_o_wins+1);
 break;
case 1:
 //BA.debugLineNum = 214;BA.debugLine="X_Wins=X_Wins+1";
_x_wins = (int) (_x_wins+1);
 break;
}
;
 //BA.debugLineNum = 216;BA.debugLine="SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。";
_savescoredb();
 }else if((mostCurrent._arraybtn[(int) (0)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (1)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (2)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (2)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (3)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (4)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (5)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (6)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (7)].getText()).equals("") == false && (mostCurrent._arraybtn[(int) (8)].getText()).equals("") == false) { 
 //BA.debugLineNum = 220;BA.debugLine="lblResutl.Text = \"平手!\"";
mostCurrent._lblresutl.setText((Object)("平手!"));
 //BA.debugLineNum = 221;BA.debugLine="Msgbox(\"平手\",\"遊戲結果!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("平手","遊戲結果!",mostCurrent.activityBA);
 //BA.debugLineNum = 222;BA.debugLine="btnBegin.Enabled =True";
mostCurrent._btnbegin.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 223;BA.debugLine="QueryScore     '查詢目前戰狀成績";
_queryscore();
 //BA.debugLineNum = 224;BA.debugLine="Tie=Tie+1";
_tie = (int) (_tie+1);
 //BA.debugLineNum = 225;BA.debugLine="SaveScoreDB    '儲存對戰成績到「MyGameScore.sqlite」資料庫中。";
_savescoredb();
 };
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 20;BA.debugLine="Dim xo(9) As String                 '存放資料格";
mostCurrent._xo = new String[(int) (9)];
java.util.Arrays.fill(mostCurrent._xo,"");
 //BA.debugLineNum = 22;BA.debugLine="Dim oxFlag As Boolean               '記錄O或X的目前的狀態";
_oxflag = false;
 //BA.debugLineNum = 23;BA.debugLine="Dim btnBegin As Button              '重新開始";
mostCurrent._btnbegin = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblResutl As Label              '顯示標題及O贏或X贏";
mostCurrent._lblresutl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnColor_Gray As ColorDrawable  '灰色";
mostCurrent._btncolor_gray = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnColor_Blue As ColorDrawable  '藍色";
mostCurrent._btncolor_blue = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnExit As Button               '結束離開";
mostCurrent._btnexit = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim O_Wins As Int                   'O贏場數";
_o_wins = 0;
 //BA.debugLineNum = 30;BA.debugLine="Dim X_Wins As Int                   'X贏場數";
_x_wins = 0;
 //BA.debugLineNum = 31;BA.debugLine="Dim Tie As Int                      '平手場數";
_tie = 0;
 //BA.debugLineNum = 32;BA.debugLine="Dim i As Int                        '計數變數";
_i = 0;
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 229;BA.debugLine="Sub QueryScore() '查詢目前戰狀成績";
 //BA.debugLineNum = 230;BA.debugLine="Dim strSQLite As String=\"Select * FROM 遊戲歷程表 where 對戰方式='兩人對戰'\"";
_strsqlite = "Select * FROM 遊戲歷程表 where 對戰方式='兩人對戰'";
 //BA.debugLineNum = 231;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)       '執行SQL指令";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 232;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1           '控制「記錄」的筆數";
{
final int step195 = 1;
final int limit195 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step195 > 0 && _i <= limit195) || (step195 < 0 && _i >= limit195); _i = ((int)(0 + _i + step195))) {
 //BA.debugLineNum = 233;BA.debugLine="CursorDBPoint.Position = i                     '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 234;BA.debugLine="O_Wins=CursorDBPoint.GetString(\"O贏\")          '取得「O贏」的次數";
_o_wins = (int)(Double.parseDouble(_cursordbpoint.GetString("O贏")));
 //BA.debugLineNum = 235;BA.debugLine="X_Wins=CursorDBPoint.GetString(\"X贏\")          '取得「X贏」的次數";
_x_wins = (int)(Double.parseDouble(_cursordbpoint.GetString("X贏")));
 //BA.debugLineNum = 236;BA.debugLine="Tie=CursorDBPoint.GetString(\"平手\")            '取得「平手」的次數";
_tie = (int)(Double.parseDouble(_cursordbpoint.GetString("平手")));
 }
};
 //BA.debugLineNum = 238;BA.debugLine="End Sub";
return "";
}
public static String  _savescoredb() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 240;BA.debugLine="Sub SaveScoreDB()  '儲存成績副程式";
 //BA.debugLineNum = 241;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 242;BA.debugLine="strSQL = \"UPDATE 遊戲歷程表 SET O贏='\" & O_Wins & \"',X贏='\" & X_Wins & \"',平手='\" & Tie & \"' where 對戰方式='兩人對戰'\"";
_strsql = "UPDATE 遊戲歷程表 SET O贏='"+BA.NumberToString(_o_wins)+"',X贏='"+BA.NumberToString(_x_wins)+"',平手='"+BA.NumberToString(_tie)+"' where 對戰方式='兩人對戰'";
 //BA.debugLineNum = 243;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 244;BA.debugLine="ToastMessageShow(\"更新最新成績資料(到資料庫中)...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新最新成績資料(到資料庫中)...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 245;BA.debugLine="End Sub";
return "";
}
}
