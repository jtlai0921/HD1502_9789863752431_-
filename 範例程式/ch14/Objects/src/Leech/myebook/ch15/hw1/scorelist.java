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

public class scorelist extends Activity implements B4AActivity{
	public static scorelist mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.scorelist");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (scorelist).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch15.hw1", "Leech.myebook.ch15.hw1.scorelist");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch15.hw1.scorelist", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (scorelist) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (scorelist) Resume **");
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
		return scorelist.class;
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
        BA.LogInfo("** Activity (scorelist) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (scorelist) Resume **");
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
public static String _strsqlviewtitle = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public anywheresoftware.b4a.objects.ListViewWrapper _livscorelist = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public Leech.myebook.ch15.hw1.main _main = null;
public Leech.myebook.ch15.hw1.mathtest _mathtest = null;
public Leech.myebook.ch15.hw1.userlogin _userlogin = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 21;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 22;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyMathScoreDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyMathScoreDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 24;BA.debugLine="Activity.LoadLayout(\"ScoreList\")";
mostCurrent._activity.LoadLayout("ScoreList",mostCurrent.activityBA);
 //BA.debugLineNum = 25;BA.debugLine="Activity.Title = UserLogin.UserName & \" 您好：心算測驗成績清單如下：\"";
mostCurrent._activity.setTitle((Object)(mostCurrent._userlogin._username+" 您好：心算測驗成績清單如下："));
 //BA.debugLineNum = 26;BA.debugLine="QueryDBList(\"Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='\" & UserLogin.UserAccount & \"'\")";
_querydblist("Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='"+mostCurrent._userlogin._useraccount+"'");
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 54;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 55;BA.debugLine="StartActivity(MathTest)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._mathtest.getObject()));
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 14;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 15;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 16;BA.debugLine="Dim livScoreList As ListView";
mostCurrent._livscorelist = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static String  _livscorelist_itemclick(int _position,Object _value) throws Exception{
String _msg_value = "";
String _strsql = "";
 //BA.debugLineNum = 67;BA.debugLine="Sub livScoreList_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 68;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 69;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 70;BA.debugLine="Msg_Value = Msgbox2(\"您確定要「刪除」此全部成績記錄嗎?\", \"確認「刪除」對話方塊\", \"確認\", \"\", \"取消\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要「刪除」此全部成績記錄嗎?","確認「刪除」對話方塊","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 71;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 72;BA.debugLine="strSQL=\"DELETE FROM 測驗成績表 Where 帳號='\" & UserLogin.UserAccount & \"' \"";
_strsql = "DELETE FROM 測驗成績表 Where 帳號='"+mostCurrent._userlogin._useraccount+"' ";
 //BA.debugLineNum = 73;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 74;BA.debugLine="livScoreList.RemoveAt(Position)";
mostCurrent._livscorelist.RemoveAt(_position);
 //BA.debugLineNum = 75;BA.debugLine="ToastMessageShow(\"刪除「心算成績」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除「心算成績」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="QueryDBList(\"Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='\" & UserLogin.UserAccount & \"'\")";
_querydblist("Select 帳號,作答歷程,成績,日期,級數 FROM 測驗成績表 Where 帳號='"+mostCurrent._userlogin._useraccount+"'");
 };
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 28;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 29;BA.debugLine="livScoreList.Clear '清單清空";
mostCurrent._livscorelist.Clear();
 //BA.debugLineNum = 30;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 31;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 32;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 33;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 34;BA.debugLine="livScoreList.SingleLineLayout.Label.TextSize=14";
mostCurrent._livscorelist.getSingleLineLayout().Label.setTextSize((float) (14));
 //BA.debugLineNum = 35;BA.debugLine="livScoreList.SingleLineLayout.ItemHeight = 17dip";
mostCurrent._livscorelist.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (17)));
 //BA.debugLineNum = 36;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1     '取得「欄位名稱」";
{
final int step27 = 1;
final int limit27 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step27 > 0 && _i <= limit27) || (step27 < 0 && _i >= limit27); _i = ((int)(0 + _i + step27))) {
 //BA.debugLineNum = 37;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"        \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"        ";
 //BA.debugLineNum = 38;BA.debugLine="strLine=strLine & \"=======\"               '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"=======";
 }
};
 //BA.debugLineNum = 40;BA.debugLine="livScoreList.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」";
mostCurrent._livscorelist.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 41;BA.debugLine="livScoreList.AddSingleLine (strLine)           '顯示「分隔水平線」";
mostCurrent._livscorelist.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 42;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1        '控制「記錄」的筆數";
{
final int step33 = 1;
final int limit33 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step33 > 0 && _i <= limit33) || (step33 < 0 && _i >= limit33); _i = ((int)(0 + _i + step33))) {
 //BA.debugLineNum = 43;BA.debugLine="CursorDBPoint.Position = i                 '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 44;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1       '控制「欄位」的個數";
{
final int step35 = 1;
final int limit35 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step35 > 0 && _j <= limit35) || (step35 < 0 && _j >= limit35); _j = ((int)(0 + _j + step35))) {
 //BA.debugLineNum = 45;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"  \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"  ";
 }
};
 //BA.debugLineNum = 47;BA.debugLine="livScoreList.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livscorelist.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent);
 //BA.debugLineNum = 48;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 50;BA.debugLine="ToastMessageShow(\"您目前心算的測驗歷程記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您目前心算的測驗歷程記錄共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
}
