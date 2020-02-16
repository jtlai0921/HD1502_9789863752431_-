package Leech.myebook.ch15.hw2;

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

public class mobile_survey extends Activity implements B4AActivity{
	public static mobile_survey mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch15.hw2", "Leech.myebook.ch15.hw2.mobile_survey");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (mobile_survey).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch15.hw2", "Leech.myebook.ch15.hw2.mobile_survey");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch15.hw2.mobile_survey", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (mobile_survey) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (mobile_survey) Resume **");
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
		return mobile_survey.class;
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
        BA.LogInfo("** Activity (mobile_survey) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (mobile_survey) Resume **");
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
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrun = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _rdooption1 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _rdooption2 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _rdooption3 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _rdooption4 = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _rdooption5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnquery = null;
public static int _checkoptionvalue = 0;
public anywheresoftware.b4a.objects.LabelWrapper _lbldisplaysurveytitle = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnstudent = null;
public static String _studno = "";
public static String _surveyno = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnmovefirst = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmoveprevious = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmovenext = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmovelast = null;
public static int _intsurvey = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitsurvey = null;
public static boolean _check_survey_exist = false;
public anywheresoftware.b4a.objects.ListViewWrapper _livsurvey = null;
public static String _strsqlviewtitle1 = "";
public static String _strsqlviewtitle2 = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public Leech.myebook.ch15.hw2.main _main = null;
public Leech.myebook.ch15.hw2.student _student = null;
public Leech.myebook.ch15.hw2.query _query = null;
public Leech.myebook.ch15.hw2.survey _survey = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 41;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 42;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 43;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal,\"MyeSurveyDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSurveyDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 45;BA.debugLine="Activity.LoadLayout(\"Mobile_Survey\")";
mostCurrent._activity.LoadLayout("Mobile_Survey",mostCurrent.activityBA);
 //BA.debugLineNum = 46;BA.debugLine="Activity.Title =\"行動問卷\"";
mostCurrent._activity.setTitle((Object)("行動問卷"));
 //BA.debugLineNum = 47;BA.debugLine="SurveyNo=\"D0001\"";
mostCurrent._surveyno = "D0001";
 //BA.debugLineNum = 48;BA.debugLine="CallShow_StudentDB(\"SELECT * FROM 學生資料表\")    '呼叫顯示「學生資料」之副程式";
_callshow_studentdb("SELECT * FROM 學生資料表");
 //BA.debugLineNum = 49;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",SurveyNo)    '呼叫顯示行動問卷每一個題目之副程式";
_displaysurveytitle("Select * FROM 問卷題庫表",mostCurrent._surveyno);
 //BA.debugLineNum = 50;BA.debugLine="QueryDBList(\"Select * FROM 問卷記錄表 Where 學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"' Order by 題號\")    '顯示您已填的問卷清單";
_querydblist("Select * FROM 問卷記錄表 Where 學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"' Order by 題號");
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 194;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 196;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 190;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitsurvey_click() throws Exception{
 //BA.debugLineNum = 185;BA.debugLine="Sub btnExitSurvey_Click";
 //BA.debugLineNum = 186;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 187;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 188;BA.debugLine="End Sub";
return "";
}
public static String  _btnmovefirst_click() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub btnMoveFirst_Click  '第一題";
 //BA.debugLineNum = 85;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",\"D0001\")";
_displaysurveytitle("Select * FROM 問卷題庫表","D0001");
 //BA.debugLineNum = 86;BA.debugLine="End Sub";
return "";
}
public static String  _btnmovelast_click() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 114;BA.debugLine="Sub btnMoveLast_Click  '最後一題";
 //BA.debugLineNum = 115;BA.debugLine="Dim strSQL As String =\"SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表\"";
_strsql = "SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表";
 //BA.debugLineNum = 116;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 117;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 118;BA.debugLine="If CursorDBPoint.GetString(\"總問卷題數\")>=10 Then";
if ((double)(Double.parseDouble(_cursordbpoint.GetString("總問卷題數")))>=10) { 
 //BA.debugLineNum = 119;BA.debugLine="SurveyNo=\"D00\" & CursorDBPoint.GetString(\"總問卷題數\")";
mostCurrent._surveyno = "D00"+_cursordbpoint.GetString("總問卷題數");
 }else {
 //BA.debugLineNum = 121;BA.debugLine="SurveyNo=\"D000\" & CursorDBPoint.GetString(\"總問卷題數\")";
mostCurrent._surveyno = "D000"+_cursordbpoint.GetString("總問卷題數");
 };
 //BA.debugLineNum = 123;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",SurveyNo)";
_displaysurveytitle("Select * FROM 問卷題庫表",mostCurrent._surveyno);
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _btnmovenext_click() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 97;BA.debugLine="Sub btnMoveNext_Click  '下一題";
 //BA.debugLineNum = 98;BA.debugLine="Dim strSQL As String =\"SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表\"";
_strsql = "SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表";
 //BA.debugLineNum = 99;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 100;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 101;BA.debugLine="If intSurvey>=CursorDBPoint.GetString(\"總問卷題數\") Then";
if (_intsurvey>=(double)(Double.parseDouble(_cursordbpoint.GetString("總問卷題數")))) { 
 //BA.debugLineNum = 102;BA.debugLine="Msgbox(\"已經是最後一筆了!\",\"訊息公佈\")";
anywheresoftware.b4a.keywords.Common.Msgbox("已經是最後一筆了!","訊息公佈",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 104;BA.debugLine="intSurvey=intSurvey+1";
_intsurvey = (int) (_intsurvey+1);
 //BA.debugLineNum = 105;BA.debugLine="If CursorDBPoint.GetString(\"總問卷題數\")>=10 Then";
if ((double)(Double.parseDouble(_cursordbpoint.GetString("總問卷題數")))>=10) { 
 //BA.debugLineNum = 106;BA.debugLine="SurveyNo=\"D00\" & intSurvey";
mostCurrent._surveyno = "D00"+BA.NumberToString(_intsurvey);
 }else {
 //BA.debugLineNum = 108;BA.debugLine="SurveyNo=\"D000\" & intSurvey";
mostCurrent._surveyno = "D000"+BA.NumberToString(_intsurvey);
 };
 //BA.debugLineNum = 110;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",SurveyNo)";
_displaysurveytitle("Select * FROM 問卷題庫表",mostCurrent._surveyno);
 };
 //BA.debugLineNum = 112;BA.debugLine="ClearSurveyItem  '呼叫清除前一題問卷的選項之副程式";
_clearsurveyitem();
 //BA.debugLineNum = 113;BA.debugLine="End Sub";
return "";
}
public static String  _btnmoveprevious_click() throws Exception{
 //BA.debugLineNum = 88;BA.debugLine="Sub btnMovePrevious_Click  '上一題";
 //BA.debugLineNum = 89;BA.debugLine="If intSurvey<=1 Then";
if (_intsurvey<=1) { 
 //BA.debugLineNum = 90;BA.debugLine="Msgbox(\"已經是第一筆了!\",\"訊息公佈\")";
anywheresoftware.b4a.keywords.Common.Msgbox("已經是第一筆了!","訊息公佈",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 92;BA.debugLine="intSurvey=intSurvey-1";
_intsurvey = (int) (_intsurvey-1);
 //BA.debugLineNum = 93;BA.debugLine="SurveyNo=\"D000\" & intSurvey";
mostCurrent._surveyno = "D000"+BA.NumberToString(_intsurvey);
 //BA.debugLineNum = 94;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",SurveyNo)";
_displaysurveytitle("Select * FROM 問卷題庫表",mostCurrent._surveyno);
 };
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _btnnextquestion_click() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 69;BA.debugLine="Sub btnNextQuestion_Click";
 //BA.debugLineNum = 70;BA.debugLine="Dim strSQL As String =\"SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表\"";
_strsql = "SELECT Count(*) AS 總問卷題數 FROM 問卷題庫表";
 //BA.debugLineNum = 71;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 72;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 73;BA.debugLine="Msgbox(CursorDBPoint.GetString(\"總問卷題數\"),\"總問卷題數\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_cursordbpoint.GetString("總問卷題數"),"總問卷題數",mostCurrent.activityBA);
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _btnrun_click() throws Exception{
 //BA.debugLineNum = 126;BA.debugLine="Sub btnRun_Click";
 //BA.debugLineNum = 127;BA.debugLine="If rdoOption1.Checked = False AND rdoOption2.Checked = False AND rdoOption3.Checked = False AND rdoOption4.Checked = False AND rdoOption5.Checked = False Then";
if (mostCurrent._rdooption1.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._rdooption2.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._rdooption3.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._rdooption4.getChecked()==anywheresoftware.b4a.keywords.Common.False && mostCurrent._rdooption5.getChecked()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 128;BA.debugLine="Msgbox(\"您尚未點選問卷記錄表!\",\"錯誤訊息\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未點選問卷記錄表!","錯誤訊息",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 130;BA.debugLine="If rdoOption1.Checked = True Then         '非常滿意";
if (mostCurrent._rdooption1.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 131;BA.debugLine="CheckOptionValue=5";
_checkoptionvalue = (int) (5);
 }else if(mostCurrent._rdooption2.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 133;BA.debugLine="CheckOptionValue=4";
_checkoptionvalue = (int) (4);
 }else if(mostCurrent._rdooption3.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 135;BA.debugLine="CheckOptionValue=3";
_checkoptionvalue = (int) (3);
 }else if(mostCurrent._rdooption4.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 137;BA.debugLine="CheckOptionValue=2";
_checkoptionvalue = (int) (2);
 }else if(mostCurrent._rdooption5.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 139;BA.debugLine="CheckOptionValue=1";
_checkoptionvalue = (int) (1);
 };
 //BA.debugLineNum = 141;BA.debugLine="Save_OptionValue '呼叫儲存選項值";
_save_optionvalue();
 };
 //BA.debugLineNum = 143;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_studentdb(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 53;BA.debugLine="Sub CallShow_StudentDB(strSQL As String)  '顯示「學生資料」之副程式";
 //BA.debugLineNum = 54;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 55;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step44 = 1;
final int limit44 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step44 > 0 && _i <= limit44) || (step44 < 0 && _i >= limit44); _i = ((int)(0 + _i + step44))) {
 //BA.debugLineNum = 56;BA.debugLine="CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 57;BA.debugLine="spnStudent.Add(CursorDBPoint.GetString(\"學號\") & \"/\" & CursorDBPoint.GetString(\"姓名\"))";
mostCurrent._spnstudent.Add(_cursordbpoint.GetString("學號")+"/"+_cursordbpoint.GetString("姓名"));
 }
};
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _check_nowriteitem_survey() throws Exception{
String _sql_query = "";
String _out = "";
int _i = 0;
 //BA.debugLineNum = 197;BA.debugLine="Sub Check_NoWriteItem_Survey  '呼叫檢查尚未填寫的題目之副程式(利用差集運算)";
 //BA.debugLineNum = 198;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 199;BA.debugLine="Dim out As String";
_out = "";
 //BA.debugLineNum = 200;BA.debugLine="StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號";
mostCurrent._studno = mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 201;BA.debugLine="SQL_Query=\"Select 題號 From 問卷題庫表 Except Select 題號 From 問卷記錄表 Where 學號='\" & StudNo & \"'\"";
_sql_query = "Select 題號 From 問卷題庫表 Except Select 題號 From 問卷記錄表 Where 學號='"+mostCurrent._studno+"'";
 //BA.debugLineNum = 202;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 203;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 204;BA.debugLine="If CursorDBPoint.RowCount>0 Then";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 205;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step181 = 1;
final int limit181 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step181 > 0 && _i <= limit181) || (step181 < 0 && _i >= limit181); _i = ((int)(0 + _i + step181))) {
 //BA.debugLineNum = 206;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 207;BA.debugLine="out=out& CursorDBPoint.GetString(\"題號\") & \"  \"";
_out = _out+_cursordbpoint.GetString("題號")+"  ";
 }
};
 //BA.debugLineNum = 209;BA.debugLine="Msgbox(\"您尚未完成此份問卷的填寫哦!\" & CRLF & out,\"未完成問卷通知!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未完成此份問卷的填寫哦!"+anywheresoftware.b4a.keywords.Common.CRLF+_out,"未完成問卷通知!!!",mostCurrent.activityBA);
 //BA.debugLineNum = 210;BA.debugLine="DisplaySurveyTitle(\"Select * FROM 問卷題庫表\",out.SubString2(0,5))";
_displaysurveytitle("Select * FROM 問卷題庫表",_out.substring((int) (0),(int) (5)));
 };
 //BA.debugLineNum = 213;BA.debugLine="End Sub";
return "";
}
public static String  _check_stuid_surveyno_exists(String _strstudno,String _strsurveyno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 176;BA.debugLine="Sub Check_StuID_SurveyNo_Exists(strStudNo As String,strSurveyNo As String)    '檢查「重複填寫問卷」之副程式";
 //BA.debugLineNum = 177;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 178;BA.debugLine="SQL_Query=\"Select * FROM 問卷記錄表 Where 學號='\" & strStudNo & \"' And 題號='\" & strSurveyNo & \"' \"";
_sql_query = "Select * FROM 問卷記錄表 Where 學號='"+_strstudno+"' And 題號='"+_strsurveyno+"' ";
 //BA.debugLineNum = 179;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 180;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前資料庫中的「此題問卷」是否已經存在";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 181;BA.debugLine="Check_Survey_Exist=True                   '代表重複「填寫問卷」了!";
_check_survey_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 183;BA.debugLine="End Sub";
return "";
}
public static String  _check_survery_complete() throws Exception{
String _sql_query = "";
String _msg_value = "";
 //BA.debugLineNum = 160;BA.debugLine="Sub Check_Survery_Complete()  '檢查問卷是否全部填寫完成之副程式";
 //BA.debugLineNum = 161;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 162;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 163;BA.debugLine="StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號";
mostCurrent._studno = mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 164;BA.debugLine="SQL_Query=\"Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists\" & _ 	          \"(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='\" & StudNo & \"'))\"";
_sql_query = "Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists"+"(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='"+mostCurrent._studno+"'))";
 //BA.debugLineNum = 166;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 167;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 168;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前此份問卷，該位同學已經填過了";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 169;BA.debugLine="Msg_Value = Msgbox2(\"恭喜您填寫完成了!\" & CRLF & \"您確定要離開嗎?\", \"行動問卷管理系統\", \"確認\", \"\", \"取消\", LoadBitmap(File.DirAssets,\"Leech_icon.jpg\"))";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("恭喜您填寫完成了!"+anywheresoftware.b4a.keywords.Common.CRLF+"您確定要離開嗎?","行動問卷管理系統","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Leech_icon.jpg").getObject()),mostCurrent.activityBA));
 //BA.debugLineNum = 170;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 171;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 172;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 };
 };
 //BA.debugLineNum = 175;BA.debugLine="End Sub";
return "";
}
public static String  _clearsurveyitem() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub ClearSurveyItem()  '呼叫清除前一題問卷的選項之副程式";
 //BA.debugLineNum = 77;BA.debugLine="rdoOption1.Checked = False";
mostCurrent._rdooption1.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 78;BA.debugLine="rdoOption2.Checked = False";
mostCurrent._rdooption2.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 79;BA.debugLine="rdoOption3.Checked = False";
mostCurrent._rdooption3.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 80;BA.debugLine="rdoOption4.Checked = False";
mostCurrent._rdooption4.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 81;BA.debugLine="rdoOption5.Checked = False";
mostCurrent._rdooption5.setChecked(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _displaysurveytitle(String _strsql,String _indexsurvey) throws Exception{
 //BA.debugLineNum = 61;BA.debugLine="Sub DisplaySurveyTitle(strSQL As String,indexSurvey As String)  '呼叫顯示行動問卷每一個題目之副程式";
 //BA.debugLineNum = 62;BA.debugLine="intSurvey =indexSurvey.SubString2(4,5)";
_intsurvey = (int)(Double.parseDouble(_indexsurvey.substring((int) (4),(int) (5))));
 //BA.debugLineNum = 63;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 64;BA.debugLine="CursorDBPoint.Position = intSurvey-1            '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (_intsurvey-1));
 //BA.debugLineNum = 65;BA.debugLine="lblDisplaySurveyTitle.Text=CursorDBPoint.GetString(\"題號\").SubString2(4,5) & \". \" & CursorDBPoint.GetString(\"題目\")";
mostCurrent._lbldisplaysurveytitle.setText((Object)(_cursordbpoint.GetString("題號").substring((int) (4),(int) (5))+". "+_cursordbpoint.GetString("題目")));
 //BA.debugLineNum = 66;BA.debugLine="SurveyNo=CursorDBPoint.GetString(\"題號\")";
mostCurrent._surveyno = _cursordbpoint.GetString("題號");
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 11;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 12;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Dim btnRun As Button";
mostCurrent._btnrun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim rdoOption1 As RadioButton";
mostCurrent._rdooption1 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim rdoOption2 As RadioButton";
mostCurrent._rdooption2 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim rdoOption3 As RadioButton";
mostCurrent._rdooption3 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim rdoOption4 As RadioButton";
mostCurrent._rdooption4 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim rdoOption5 As RadioButton";
mostCurrent._rdooption5 = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnQuery As Button";
mostCurrent._btnquery = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim CheckOptionValue As Int  '記錄問卷的選項1~5(其中1代表非常不滿意，5代表非常滿意)";
_checkoptionvalue = 0;
 //BA.debugLineNum = 21;BA.debugLine="Dim lblDisplaySurveyTitle As Label";
mostCurrent._lbldisplaysurveytitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim spnStudent As Spinner";
mostCurrent._spnstudent = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim StudNo As String              '學號";
mostCurrent._studno = "";
 //BA.debugLineNum = 24;BA.debugLine="Dim SurveyNo As String            '題號";
mostCurrent._surveyno = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim btnMoveFirst As Button        '第一題";
mostCurrent._btnmovefirst = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnMovePrevious As Button     '上一題";
mostCurrent._btnmoveprevious = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnMoveNext As Button         '下一題";
mostCurrent._btnmovenext = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnMoveLast As Button         '最後一題";
mostCurrent._btnmovelast = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim intSurvey As Int";
_intsurvey = 0;
 //BA.debugLineNum = 30;BA.debugLine="Dim btnExitSurvey As Button";
mostCurrent._btnexitsurvey = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim Check_Survey_Exist As Boolean=False  '檢查是否有「重複填問卷」(預設為否)";
_check_survey_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 33;BA.debugLine="Dim livSurvey As ListView";
mostCurrent._livsurvey = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim strSqlViewTitle1 As String =\"\"";
mostCurrent._strsqlviewtitle1 = "";
 //BA.debugLineNum = 36;BA.debugLine="Dim strSqlViewTitle2 As String =\"\"";
mostCurrent._strsqlviewtitle2 = "";
 //BA.debugLineNum = 37;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 38;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _livsurvey_itemclick(int _position,Object _value) throws Exception{
String _msg_value = "";
String _strsql = "";
 //BA.debugLineNum = 255;BA.debugLine="Sub livSurvey_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 256;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 257;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 258;BA.debugLine="Msg_Value = Msgbox2(\"您確定要「刪除」此題問卷記錄嗎?\", \"確認刪除對話方塊\", \"確認\", \"\", \"取消\", Null)";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要「刪除」此題問卷記錄嗎?","確認刪除對話方塊","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA));
 //BA.debugLineNum = 259;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 260;BA.debugLine="strSQL=\"DELETE FROM 問卷記錄表 Where 學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"' And 題號='\" & livSurvey.GetItem(Position) & \"' \"";
_strsql = "DELETE FROM 問卷記錄表 Where 學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"' And 題號='"+BA.ObjectToString(mostCurrent._livsurvey.GetItem(_position))+"' ";
 //BA.debugLineNum = 261;BA.debugLine="Msgbox(strSQL,\"strSQL\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_strsql,"strSQL",mostCurrent.activityBA);
 //BA.debugLineNum = 262;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 263;BA.debugLine="livSurvey.RemoveAt(Position)";
mostCurrent._livsurvey.RemoveAt(_position);
 //BA.debugLineNum = 264;BA.debugLine="ToastMessageShow(\"刪除「問卷資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除「問卷資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 265;BA.debugLine="QueryDBList(\"Select * FROM 問卷記錄表 Where 學號='\" & spnStudent.SelectedItem.SubString2(0,5) & \"' Order by 題號\")    '顯示您已填的問卷清單";
_querydblist("Select * FROM 問卷記錄表 Where 學號='"+mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5))+"' Order by 題號");
 };
 //BA.debugLineNum = 267;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 229;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 230;BA.debugLine="livSurvey.Clear '清單清空";
mostCurrent._livsurvey.Clear();
 //BA.debugLineNum = 231;BA.debugLine="strSqlViewTitle1 =\"您本份問卷記錄如下：\" & CRLF";
mostCurrent._strsqlviewtitle1 = "您本份問卷記錄如下："+anywheresoftware.b4a.keywords.Common.CRLF;
 //BA.debugLineNum = 232;BA.debugLine="strSqlViewTitle2 =\"\"";
mostCurrent._strsqlviewtitle2 = "";
 //BA.debugLineNum = 233;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 234;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 235;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 236;BA.debugLine="livSurvey.SingleLineLayout.Label.TextSize=12";
mostCurrent._livsurvey.getSingleLineLayout().Label.setTextSize((float) (12));
 //BA.debugLineNum = 237;BA.debugLine="livSurvey.SingleLineLayout.ItemHeight = 15dip";
mostCurrent._livsurvey.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (15)));
 //BA.debugLineNum = 238;BA.debugLine="For i = 1 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step211 = 1;
final int limit211 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (1); (step211 > 0 && _i <= limit211) || (step211 < 0 && _i >= limit211); _i = ((int)(0 + _i + step211))) {
 //BA.debugLineNum = 239;BA.debugLine="strSqlViewTitle2=strSqlViewTitle2 & CursorDBPoint.GetColumnName(i) & \"     \"";
mostCurrent._strsqlviewtitle2 = mostCurrent._strsqlviewtitle2+_cursordbpoint.GetColumnName(_i)+"     ";
 //BA.debugLineNum = 240;BA.debugLine="strLine=strLine & \"=======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"=======";
 }
};
 //BA.debugLineNum = 242;BA.debugLine="livSurvey.AddSingleLine (strSqlViewTitle1)  '顯示「您本份問卷記錄如下：」";
mostCurrent._livsurvey.AddSingleLine(mostCurrent._strsqlviewtitle1);
 //BA.debugLineNum = 243;BA.debugLine="livSurvey.AddSingleLine (strSqlViewTitle2)  '顯示「欄位名稱」";
mostCurrent._livsurvey.AddSingleLine(mostCurrent._strsqlviewtitle2);
 //BA.debugLineNum = 244;BA.debugLine="livSurvey.AddSingleLine (strLine)           '顯示「分隔水平線」";
mostCurrent._livsurvey.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 245;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step218 = 1;
final int limit218 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step218 > 0 && _i <= limit218) || (step218 < 0 && _i >= limit218); _i = ((int)(0 + _i + step218))) {
 //BA.debugLineNum = 246;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 247;BA.debugLine="For j=1 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step220 = 1;
final int limit220 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (1); (step220 > 0 && _j <= limit220) || (step220 < 0 && _j >= limit220); _j = ((int)(0 + _j + step220))) {
 //BA.debugLineNum = 248;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 250;BA.debugLine="livSurvey.AddSingleLine2((i+1) & \". \" & strSqlViewContent,CursorDBPoint.GetString(CursorDBPoint.GetColumnName(1)))";
mostCurrent._livsurvey.AddSingleLine2(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent,(Object)(_cursordbpoint.GetString(_cursordbpoint.GetColumnName((int) (1)))));
 //BA.debugLineNum = 251;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 253;BA.debugLine="End Sub";
return "";
}
public static String  _save_optionvalue() throws Exception{
String _strsql = "";
 //BA.debugLineNum = 144;BA.debugLine="Sub Save_OptionValue()";
 //BA.debugLineNum = 145;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 146;BA.debugLine="StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號";
mostCurrent._studno = mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 147;BA.debugLine="strSQL = \"INSERT INTO 問卷記錄表(學號,題號,滿意值) VALUES('\" & StudNo & \"','\" & SurveyNo & \"','\" & CheckOptionValue & \"')\"";
_strsql = "INSERT INTO 問卷記錄表(學號,題號,滿意值) VALUES('"+mostCurrent._studno+"','"+mostCurrent._surveyno+"','"+BA.NumberToString(_checkoptionvalue)+"')";
 //BA.debugLineNum = 148;BA.debugLine="Check_StuID_SurveyNo_Exists(StudNo,SurveyNo)       '呼叫檢查「重複填寫問卷」之副程式";
_check_stuid_surveyno_exists(mostCurrent._studno,mostCurrent._surveyno);
 //BA.debugLineNum = 149;BA.debugLine="If Check_Survey_Exist=True Then    '檢查是否有重複新增";
if (_check_survey_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 150;BA.debugLine="Msgbox(\"您已經「重複填寫此題問卷」了！\",\"填寫問卷錯誤!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經「重複填寫此題問卷」了！","填寫問卷錯誤!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 152;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 153;BA.debugLine="ToastMessageShow(\"您完成第\" & SurveyNo.SubString2(4,5) & \"題問卷\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您完成第"+mostCurrent._surveyno.substring((int) (4),(int) (5))+"題問卷",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 155;BA.debugLine="Check_Survey_Exist=False";
_check_survey_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 156;BA.debugLine="QueryDBList(\"Select * FROM 問卷記錄表 Where 學號='\" & StudNo & \"' Order by 題號\")    '顯示您已填的問卷清單";
_querydblist("Select * FROM 問卷記錄表 Where 學號='"+mostCurrent._studno+"' Order by 題號");
 //BA.debugLineNum = 157;BA.debugLine="Check_Survery_Complete  '呼叫檢查問卷是否全部填寫完成之副程式";
_check_survery_complete();
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
public static String  _spnstudent_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 214;BA.debugLine="Sub spnStudent_ItemClick (Position As Int, Value As Object)  '利用除法運算";
 //BA.debugLineNum = 215;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 216;BA.debugLine="StudNo=spnStudent.SelectedItem.SubString2(0,5)    '學號";
mostCurrent._studno = mostCurrent._spnstudent.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 217;BA.debugLine="SQL_Query=\"Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists\" & _ 	          \"(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='\" & StudNo & \"'))\"";
_sql_query = "Select 姓名 FROM 學生資料表 As A Where not Exists(Select * From 問卷題庫表 As B Where not Exists"+"(Select * From 問卷記錄表 As C Where B.題號=C.題號 And A.學號=C.學號 And A.學號='"+mostCurrent._studno+"'))";
 //BA.debugLineNum = 219;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 220;BA.debugLine="CursorDBPoint.Position = 0         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 221;BA.debugLine="If CursorDBPoint.RowCount>0 Then             '檢查目前此份問卷，該位同學已經填過了";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 222;BA.debugLine="Msgbox(CursorDBPoint.GetString(\"姓名\"),\"全部填完通知!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_cursordbpoint.GetString("姓名"),"全部填完通知!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 224;BA.debugLine="Check_NoWriteItem_Survey  '呼叫檢查尚未填寫的題目之副程式";
_check_nowriteitem_survey();
 };
 //BA.debugLineNum = 226;BA.debugLine="QueryDBList(\"Select * FROM 問卷記錄表 Where 學號='\" & StudNo & \"' Order by 題號\")    '顯示您已填的問卷清單";
_querydblist("Select * FROM 問卷記錄表 Where 學號='"+mostCurrent._studno+"' Order by 題號");
 //BA.debugLineNum = 227;BA.debugLine="End Sub";
return "";
}
}
