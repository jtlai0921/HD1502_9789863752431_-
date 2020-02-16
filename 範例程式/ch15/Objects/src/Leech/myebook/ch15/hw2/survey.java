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

public class survey extends Activity implements B4AActivity{
	public static survey mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch15.hw2", "Leech.myebook.ch15.hw2.survey");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (survey).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch15.hw2", "Leech.myebook.ch15.hw2.survey");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch15.hw2.survey", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (survey) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (survey) Resume **");
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
		return survey.class;
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
        BA.LogInfo("** Activity (survey) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (survey) Resume **");
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
public static boolean _check_courid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtsurveyno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtsurveyname = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerysurveyno = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livsurvey = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlistdata = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitsurvey = null;
public static String _strsqlviewtitle = "";
public static String _strline = "";
public static String _strsqlviewcontent = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public Leech.myebook.ch15.hw2.main _main = null;
public Leech.myebook.ch15.hw2.mobile_survey _mobile_survey = null;
public Leech.myebook.ch15.hw2.student _student = null;
public Leech.myebook.ch15.hw2.query _query = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 38;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal,\"MyeSurveyDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSurveyDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 40;BA.debugLine="Activity.LoadLayout(\"Survey\")";
mostCurrent._activity.LoadLayout("Survey",mostCurrent.activityBA);
 //BA.debugLineNum = 41;BA.debugLine="Activity.Title =\"【行動問卷管理系統】\"";
mostCurrent._activity.setTitle((Object)("【行動問卷管理系統】"));
 //BA.debugLineNum = 42;BA.debugLine="SpinQuerySurveyNo.Visible=False            '查詢「問卷題號」的下拉式元件隱藏";
mostCurrent._spinquerysurveyno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 43;BA.debugLine="edtSurveyNo.Text=\"D0006\"";
mostCurrent._edtsurveyno.setText((Object)("D0006"));
 //BA.debugLineNum = 44;BA.debugLine="edtSurveyName.Text=\"請選擇您未來可能會從事那一個工作?\"";
mostCurrent._edtsurveyname.setText((Object)("請選擇您未來可能會從事那一個工作?"));
 //BA.debugLineNum = 45;BA.debugLine="QueryDBList(\"Select * FROM 問卷題庫表\")	  '呼叫顯示目前存在的問卷題庫表之副程式";
_querydblist("Select * FROM 問卷題庫表");
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _strsql = "";
String _surveyno = "";
 //BA.debugLineNum = 101;BA.debugLine="Sub btnDelete_Click   '刪除「問卷資料」";
 //BA.debugLineNum = 102;BA.debugLine="If edtSurveyNo.Text=\"\" Then";
if ((mostCurrent._edtsurveyno.getText()).equals("")) { 
 //BA.debugLineNum = 103;BA.debugLine="Msgbox(\"您尚未輸入「問卷題號」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「問卷題號」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 105;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 106;BA.debugLine="Dim SurveyNo As String =edtSurveyNo.Text";
_surveyno = mostCurrent._edtsurveyno.getText();
 //BA.debugLineNum = 107;BA.debugLine="strSQL = \"DELETE FROM 問卷題庫表 WHERE 題號 = '\" & SurveyNo & \"'\"";
_strsql = "DELETE FROM 問卷題庫表 WHERE 題號 = '"+_surveyno+"'";
 //BA.debugLineNum = 108;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 109;BA.debugLine="ToastMessageShow(\"刪除一筆「問卷資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆「問卷資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 110;BA.debugLine="QueryDBList(\"Select * FROM 問卷題庫表\")	'呼叫顯示目前存在的問卷題庫表之副程式";
_querydblist("Select * FROM 問卷題庫表");
 };
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitsurvey_click() throws Exception{
 //BA.debugLineNum = 171;BA.debugLine="Sub btnExitSurvey_Click";
 //BA.debugLineNum = 172;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 173;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _strsql = "";
String _surveyno = "";
String _surveyname = "";
 //BA.debugLineNum = 56;BA.debugLine="Sub btnInsert_Click  '新增「問卷資料」";
 //BA.debugLineNum = 57;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 58;BA.debugLine="Dim SurveyNo As String =edtSurveyNo.Text               '將「問卷題號」欄位內容指定給變數";
_surveyno = mostCurrent._edtsurveyno.getText();
 //BA.debugLineNum = 59;BA.debugLine="Dim SurveyName As String = edtSurveyName.Text          '將「問卷題目」欄位內容指定給變數";
_surveyname = mostCurrent._edtsurveyname.getText();
 //BA.debugLineNum = 61;BA.debugLine="If SurveyNo=\"\" OR SurveyName=\"\" Then";
if ((_surveyno).equals("") || (_surveyname).equals("")) { 
 //BA.debugLineNum = 62;BA.debugLine="Msgbox(\"您尚未完整輸入「問卷資料」相關哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未完整輸入「問卷資料」相關哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 64;BA.debugLine="strSQL = \"INSERT INTO 問卷題庫表(題號,題目)VALUES('\" & SurveyNo & \"','\" & SurveyName & \"')\"";
_strsql = "INSERT INTO 問卷題庫表(題號,題目)VALUES('"+_surveyno+"','"+_surveyname+"')";
 //BA.debugLineNum = 65;BA.debugLine="Check_CourID_Exists(SurveyNo)        '呼叫檢查「問卷題號」是否有重複新增之副程式";
_check_courid_exists(_surveyno);
 //BA.debugLineNum = 66;BA.debugLine="If Check_CourID_Exist=True Then      '檢查是否有重複新增";
if (_check_courid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 67;BA.debugLine="Msgbox(\"您已經重複新增「問卷資料」了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增「問卷資料」了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 69;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                               '執行SQL指令，以成功的新增記錄";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 70;BA.debugLine="ToastMessageShow(\"您新增成功「問卷資料」記錄!\", True)     '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您新增成功「問卷資料」記錄!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 72;BA.debugLine="Check_CourID_Exist=False                   '設定沒有重複新增「問卷題號」";
_check_courid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 74;BA.debugLine="QueryDBList(\"Select * FROM 問卷題庫表\")	  '呼叫顯示目前存在的問卷題庫表之副程式";
_querydblist("Select * FROM 問卷題庫表");
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
public static String  _btnlistdata_click() throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub btnListData_Click";
 //BA.debugLineNum = 169;BA.debugLine="QueryDBList(\"Select * FROM 問卷題庫表\")	'呼叫顯示目前存在的問卷題庫表之副程式";
_querydblist("Select * FROM 問卷題庫表");
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 114;BA.debugLine="Sub btnSelect_Click   '查詢「問卷資料」";
 //BA.debugLineNum = 115;BA.debugLine="SpinQuerySurveyNo.Visible =True    '查詢「問卷題號」的下拉式元件顯示";
mostCurrent._spinquerysurveyno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="edtSurveyNo.Visible =False         '「問卷題號」欄位元件隱藏";
mostCurrent._edtsurveyno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="SpinQuerySurveyNo.Clear            '查詢「問卷題號」的下拉式元件來清空";
mostCurrent._spinquerysurveyno.Clear();
 //BA.debugLineNum = 118;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 題號 FROM 問卷題庫表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 題號 FROM 問卷題庫表")));
 //BA.debugLineNum = 119;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step92 = 1;
final int limit92 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step92 > 0 && _i <= limit92) || (step92 < 0 && _i >= limit92); _i = ((int)(0 + _i + step92))) {
 //BA.debugLineNum = 120;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 121;BA.debugLine="SpinQuerySurveyNo.Add(CursorDBPoint.GetString(\"題號\"))";
mostCurrent._spinquerysurveyno.Add(_cursordbpoint.GetString("題號"));
 }
};
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _strsql = "";
String _surveyno = "";
 //BA.debugLineNum = 86;BA.debugLine="Sub btnUpdate_Click   '修改「問卷資料」";
 //BA.debugLineNum = 87;BA.debugLine="If edtSurveyNo.Text=\"\" Then";
if ((mostCurrent._edtsurveyno.getText()).equals("")) { 
 //BA.debugLineNum = 88;BA.debugLine="Msgbox(\"您尚未輸入「問卷題號」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「問卷題號」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 90;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 91;BA.debugLine="Dim SurveyNo As String =edtSurveyNo.Text";
_surveyno = mostCurrent._edtsurveyno.getText();
 //BA.debugLineNum = 92;BA.debugLine="strSQL = \"UPDATE 問卷題庫表\" & _             \" SET 題目 ='\" & edtSurveyName.Text & \"'\" & _ 			\" WHERE 題號 = '\" & SurveyNo & \"'\"";
_strsql = "UPDATE 問卷題庫表"+" SET 題目 ='"+mostCurrent._edtsurveyname.getText()+"'"+" WHERE 題號 = '"+_surveyno+"'";
 //BA.debugLineNum = 95;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 96;BA.debugLine="ToastMessageShow(\"更新一筆「問卷資料」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆「問卷資料」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 97;BA.debugLine="QueryDBList(\"Select * FROM 問卷題庫表\")	'呼叫顯示目前存在的問卷題庫表之副程式";
_querydblist("Select * FROM 問卷題庫表");
 };
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_surveydata(String _strsql) throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Sub CallShow_SurveyData(strSQL As String)  '顯示「問卷題庫表」之副程式";
 //BA.debugLineNum = 135;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 136;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 137;BA.debugLine="edtSurveyNo.Text = CursorDBPoint.GetString(\"題號\")";
mostCurrent._edtsurveyno.setText((Object)(_cursordbpoint.GetString("題號")));
 //BA.debugLineNum = 138;BA.debugLine="edtSurveyName.Text=CursorDBPoint.GetString(\"題目\")";
mostCurrent._edtsurveyname.setText((Object)(_cursordbpoint.GetString("題目")));
 //BA.debugLineNum = 139;BA.debugLine="End Sub";
return "";
}
public static String  _check_courid_exists(String _strsurveyno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 77;BA.debugLine="Sub Check_CourID_Exists(strSurveyNo As String)  '檢查「問卷題號」是否有重複新增之副程式";
 //BA.debugLineNum = 78;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 79;BA.debugLine="SQL_Query=\"Select * FROM 問卷題庫表 Where 題號='\" & strSurveyNo & \"' \"";
_sql_query = "Select * FROM 問卷題庫表 Where 題號='"+_strsurveyno+"' ";
 //BA.debugLineNum = 80;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 81;BA.debugLine="If CursorDBPoint.RowCount>0 Then  '檢查目前資料庫中的「問卷題庫表」是否已經存在一筆了";
if (_cursordbpoint.getRowCount()>0) { 
 //BA.debugLineNum = 82;BA.debugLine="Check_CourID_Exist=True        '代表重複新增「問卷題號」了!";
_check_courid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim edtSurveyNo As EditText         '問卷題號";
mostCurrent._edtsurveyno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim edtSurveyName As EditText         '問卷題目";
mostCurrent._edtsurveyname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnSelect As Button             '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim SpinQuerySurveyNo As Spinner      '查詢「問卷題號」";
mostCurrent._spinquerysurveyno = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim livSurvey As ListView           '顯示「問卷題庫表」清單元件";
mostCurrent._livsurvey = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnListData As Button           '清單功能";
mostCurrent._btnlistdata = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnExitSurvey As Button         '離開功能";
mostCurrent._btnexitsurvey = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 31;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_CourID_Exist As Boolean=False   '檢查新增「問卷題號」時的代號是否重複";
_check_courid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 141;BA.debugLine="Sub QueryDBList(strSQLite As String)  '顯示目前存在的問卷題庫表之副程式";
 //BA.debugLineNum = 142;BA.debugLine="livSurvey.Clear '清單清空";
mostCurrent._livsurvey.Clear();
 //BA.debugLineNum = 143;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 144;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 145;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 146;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 147;BA.debugLine="livSurvey.SingleLineLayout.Label.TextSize=16";
mostCurrent._livsurvey.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 148;BA.debugLine="livSurvey.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livsurvey.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 149;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step119 = 1;
final int limit119 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step119 > 0 && _i <= limit119) || (step119 < 0 && _i >= limit119); _i = ((int)(0 + _i + step119))) {
 //BA.debugLineNum = 150;BA.debugLine="strSqlViewTitle=strSqlViewTitle & _ 		CursorDBPoint.GetColumnName(i) & \"          \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"          ";
 //BA.debugLineNum = 152;BA.debugLine="strLine=strLine & \"=======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"=======";
 }
};
 //BA.debugLineNum = 154;BA.debugLine="livSurvey.AddSingleLine (strSqlViewTitle)   '顯示「欄位名稱」";
mostCurrent._livsurvey.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 155;BA.debugLine="livSurvey.AddSingleLine (strLine)           '顯示「分隔水平線」";
mostCurrent._livsurvey.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 156;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step125 = 1;
final int limit125 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step125 > 0 && _i <= limit125) || (step125 < 0 && _i >= limit125); _i = ((int)(0 + _i + step125))) {
 //BA.debugLineNum = 157;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 158;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step127 = 1;
final int limit127 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step127 > 0 && _j <= limit127) || (step127 < 0 && _j >= limit127); _j = ((int)(0 + _j + step127))) {
 //BA.debugLineNum = 159;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 161;BA.debugLine="livSurvey.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livsurvey.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent);
 //BA.debugLineNum = 162;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 164;BA.debugLine="ToastMessageShow(\"目前的問卷題庫共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前的問卷題庫共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 165;BA.debugLine="lblResult.Text =\"===問卷題庫清單( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===問卷題庫清單( "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerysurveyno_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querysurveyno = "";
 //BA.debugLineNum = 125;BA.debugLine="Sub SpinQuerySurveyNo_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 126;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 127;BA.debugLine="Dim QuerySurveyNo As String =SpinQuerySurveyNo.SelectedItem";
_querysurveyno = mostCurrent._spinquerysurveyno.getSelectedItem();
 //BA.debugLineNum = 128;BA.debugLine="SQL_Query=\"Select * FROM 問卷題庫表 Where 題號 = '\" & QuerySurveyNo & \"'\"";
_sql_query = "Select * FROM 問卷題庫表 Where 題號 = '"+_querysurveyno+"'";
 //BA.debugLineNum = 129;BA.debugLine="CallShow_SurveyData(SQL_Query)      '呼叫顯示「問卷題庫表」之副程式";
_callshow_surveydata(_sql_query);
 //BA.debugLineNum = 130;BA.debugLine="SpinQuerySurveyNo.Visible =False    '查詢「問卷題號」的下拉式元件隱藏";
mostCurrent._spinquerysurveyno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 131;BA.debugLine="edtSurveyNo.Visible =True           '「問卷題號」欄位元件顯示";
mostCurrent._edtsurveyno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 132;BA.debugLine="End Sub";
return "";
}
}
