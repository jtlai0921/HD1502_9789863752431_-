package Leech.myebook.ch16.Project;

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

public class query extends Activity implements B4AActivity{
	public static query mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.query");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (query).");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.query");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch16.Project.query", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (query) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (query) Resume **");
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
		return query.class;
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
        BA.LogInfo("** Activity (query) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (query) Resume **");
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
public anywheresoftware.b4a.objects.EditTextWrapper _edtsql = null;
public static String _strsql = "";
public anywheresoftware.b4a.objects.ListViewWrapper _livsqlview = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnrunsql = null;
public static String _strsqlviewtitle = "";
public static String _strsqlviewcontent = "";
public static String _strline = "";
public static int _j = 0;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitquery = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltitle = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnsql = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnwritesql = null;
public Leech.myebook.ch16.Project.main _main = null;
public Leech.myebook.ch16.Project.department _department = null;
public Leech.myebook.ch16.Project.course _course = null;
public Leech.myebook.ch16.Project.student _student = null;
public Leech.myebook.ch16.Project.course_selection _course_selection = null;
public Leech.myebook.ch16.Project.writesql _writesql = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 30;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeSchoolDB.sqlite\", True) '將SQL物件初始化資料庫";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 32;BA.debugLine="Activity.LoadLayout(\"Query\")";
mostCurrent._activity.LoadLayout("Query",mostCurrent.activityBA);
 //BA.debugLineNum = 33;BA.debugLine="Activity.Title =\"查詢作業\"";
mostCurrent._activity.setTitle((Object)("查詢作業"));
 //BA.debugLineNum = 35;BA.debugLine="lblResult.Text =\"===您尚未執行SQL指令===\"";
mostCurrent._lblresult.setText((Object)("===您尚未執行SQL指令==="));
 //BA.debugLineNum = 36;BA.debugLine="spnSQL.Add(\"全部學生選課記錄\")";
mostCurrent._spnsql.Add("全部學生選課記錄");
 //BA.debugLineNum = 37;BA.debugLine="spnSQL.Add(\"科系代碼表記錄\")";
mostCurrent._spnsql.Add("科系代碼表記錄");
 //BA.debugLineNum = 38;BA.debugLine="spnSQL.Add(\"課程資料表記錄\")";
mostCurrent._spnsql.Add("課程資料表記錄");
 //BA.debugLineNum = 39;BA.debugLine="spnSQL.Add(\"學生資料表記錄\")";
mostCurrent._spnsql.Add("學生資料表記錄");
 //BA.debugLineNum = 40;BA.debugLine="spnSQL.Add(\"學號與成績排序\")";
mostCurrent._spnsql.Add("學號與成績排序");
 //BA.debugLineNum = 41;BA.debugLine="spnSQL.Add(\"每位學生選修科目\")";
mostCurrent._spnsql.Add("每位學生選修科目");
 //BA.debugLineNum = 42;BA.debugLine="spnSQL.Add(\"每位學生選修成績\")";
mostCurrent._spnsql.Add("每位學生選修成績");
 //BA.debugLineNum = 43;BA.debugLine="spnSQL.Add(\"每門科目選修人數\")";
mostCurrent._spnsql.Add("每門科目選修人數");
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 78;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 69;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 70;BA.debugLine="If WriteSQL.YourSQL.Length > 0 Then";
if (mostCurrent._writesql._yoursql.length()>0) { 
 //BA.debugLineNum = 71;BA.debugLine="edtSQL.Text =\"\"";
mostCurrent._edtsql.setText((Object)(""));
 //BA.debugLineNum = 72;BA.debugLine="edtSQL.Text =WriteSQL.YourSQL";
mostCurrent._edtsql.setText((Object)(mostCurrent._writesql._yoursql));
 };
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitquery_click() throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub btnExitQuery_Click";
 //BA.debugLineNum = 118;BA.debugLine="Activity.Finish()";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 119;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _btnrunsql_click() throws Exception{
 //BA.debugLineNum = 79;BA.debugLine="Sub btnRunSQL_Click";
 //BA.debugLineNum = 80;BA.debugLine="If edtSQL.Text=\"\" Then";
if ((mostCurrent._edtsql.getText()).equals("")) { 
 //BA.debugLineNum = 81;BA.debugLine="Msgbox(\"您尚未選擇查詢之下拉式選項哦！\",\"查詢錯誤\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未選擇查詢之下拉式選項哦！","查詢錯誤",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 83;BA.debugLine="QueryDBList(edtSQL.Text)";
_querydblist(mostCurrent._edtsql.getText());
 };
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static String  _btnwritesql_click() throws Exception{
 //BA.debugLineNum = 112;BA.debugLine="Sub btnWriteSQL_Click";
 //BA.debugLineNum = 114;BA.debugLine="StartActivity(WriteSQL)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._writesql.getObject()));
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 13;BA.debugLine="Dim edtSQL As EditText";
mostCurrent._edtsql = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 14;BA.debugLine="Dim strSQL As String";
mostCurrent._strsql = "";
 //BA.debugLineNum = 15;BA.debugLine="Dim livSQLView As ListView";
mostCurrent._livsqlview = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim btnRunSQL As Button";
mostCurrent._btnrunsql = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 18;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 19;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 20;BA.debugLine="Dim j As Int";
_j = 0;
 //BA.debugLineNum = 21;BA.debugLine="Dim btnExitQuery As Button";
mostCurrent._btnexitquery = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblTitle As Label";
mostCurrent._lbltitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim spnSQL As Spinner";
mostCurrent._spnsql = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnWriteSQL As Button";
mostCurrent._btnwritesql = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 9;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _querydblist(String _strsqlite) throws Exception{
int _i = 0;
 //BA.debugLineNum = 86;BA.debugLine="Sub QueryDBList(strSQLite As String)";
 //BA.debugLineNum = 87;BA.debugLine="livSQLView.Clear '清單清空";
mostCurrent._livsqlview.Clear();
 //BA.debugLineNum = 88;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._strsqlviewtitle = "";
 //BA.debugLineNum = 89;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 //BA.debugLineNum = 90;BA.debugLine="strLine =\"\"";
mostCurrent._strline = "";
 //BA.debugLineNum = 91;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 92;BA.debugLine="livSQLView.SingleLineLayout.Label.TextSize=16";
mostCurrent._livsqlview.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 93;BA.debugLine="livSQLView.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livsqlview.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 94;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step81 = 1;
final int limit81 = (int) (_cursordbpoint.getColumnCount()-1);
for (_i = (int) (0); (step81 > 0 && _i <= limit81) || (step81 < 0 && _i >= limit81); _i = ((int)(0 + _i + step81))) {
 //BA.debugLineNum = 95;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"       \"";
mostCurrent._strsqlviewtitle = mostCurrent._strsqlviewtitle+_cursordbpoint.GetColumnName(_i)+"       ";
 //BA.debugLineNum = 96;BA.debugLine="strLine=strLine & \"======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._strline = mostCurrent._strline+"======";
 }
};
 //BA.debugLineNum = 98;BA.debugLine="livSQLView.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」";
mostCurrent._livsqlview.AddSingleLine(mostCurrent._strsqlviewtitle);
 //BA.debugLineNum = 99;BA.debugLine="livSQLView.AddSingleLine (strLine)          '顯示「分隔水平線」";
mostCurrent._livsqlview.AddSingleLine(mostCurrent._strline);
 //BA.debugLineNum = 100;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step87 = 1;
final int limit87 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step87 > 0 && _i <= limit87) || (step87 < 0 && _i >= limit87); _i = ((int)(0 + _i + step87))) {
 //BA.debugLineNum = 101;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 102;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step89 = 1;
final int limit89 = (int) (_cursordbpoint.getColumnCount()-1);
for (_j = (int) (0); (step89 > 0 && _j <= limit89) || (step89 < 0 && _j >= limit89); _j = ((int)(0 + _j + step89))) {
 //BA.debugLineNum = 103;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._strsqlviewcontent = mostCurrent._strsqlviewcontent+_cursordbpoint.GetString(_cursordbpoint.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 105;BA.debugLine="livSQLView.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livsqlview.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._strsqlviewcontent);
 //BA.debugLineNum = 106;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._strsqlviewcontent = "";
 }
};
 //BA.debugLineNum = 108;BA.debugLine="ToastMessageShow(\"符合條件的記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("符合條件的記錄共有: "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="lblResult.Text =\"==符合條件的記錄共有( \" & CursorDBPoint.RowCount & \" 筆)==\"";
mostCurrent._lblresult.setText((Object)("==符合條件的記錄共有( "+BA.NumberToString(_cursordbpoint.getRowCount())+" 筆)=="));
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static String  _spnsql_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub spnSQL_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 46;BA.debugLine="Select Position";
switch (_position) {
case 0:
 //BA.debugLineNum = 48;BA.debugLine="strSQL=\"Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號\"";
mostCurrent._strsql = "Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號";
 break;
case 1:
 //BA.debugLineNum = 50;BA.debugLine="strSQL=\"Select * FROM 科系代碼表\"";
mostCurrent._strsql = "Select * FROM 科系代碼表";
 break;
case 2:
 //BA.debugLineNum = 52;BA.debugLine="strSQL=\"Select * FROM 課程資料表\"";
mostCurrent._strsql = "Select * FROM 課程資料表";
 break;
case 3:
 //BA.debugLineNum = 54;BA.debugLine="strSQL=\"Select * FROM 學生資料表\"";
mostCurrent._strsql = "Select * FROM 學生資料表";
 break;
case 4:
 //BA.debugLineNum = 56;BA.debugLine="strSQL=\"Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號 ORDER BY A.學號,成績\"";
mostCurrent._strsql = "Select A.學號, 姓名, 課號, 成績  FROM 學生資料表 As A, 選課資料表 As B  WHERE A.學號=B.學號 ORDER BY A.學號,成績";
 break;
case 5:
 //BA.debugLineNum = 58;BA.debugLine="strSQL=\"SELECT 學號, Count(*) AS 選科目數 FROM 選課資料表 GROUP BY 學號\"";
mostCurrent._strsql = "SELECT 學號, Count(*) AS 選科目數 FROM 選課資料表 GROUP BY 學號";
 break;
case 6:
 //BA.debugLineNum = 60;BA.debugLine="strSQL=\"Select 學號, AVG(成績) As 平均成績 FROM 選課資料表 GROUP BY 學號\"";
mostCurrent._strsql = "Select 學號, AVG(成績) As 平均成績 FROM 選課資料表 GROUP BY 學號";
 break;
case 7:
 //BA.debugLineNum = 62;BA.debugLine="strSQL=\"SELECT 課號, Count(*) AS 選課學生人數 FROM 選課資料表 GROUP BY 課號 ORDER BY 課號 DESC\"";
mostCurrent._strsql = "SELECT 課號, Count(*) AS 選課學生人數 FROM 選課資料表 GROUP BY 課號 ORDER BY 課號 DESC";
 break;
}
;
 //BA.debugLineNum = 64;BA.debugLine="edtSQL.Text =strSQL";
mostCurrent._edtsql.setText((Object)(mostCurrent._strsql));
 //BA.debugLineNum = 65;BA.debugLine="lblResult.Text =\"===您尚未執行SQL指令===\"";
mostCurrent._lblresult.setText((Object)("===您尚未執行SQL指令==="));
 //BA.debugLineNum = 66;BA.debugLine="livSQLView.Clear '清單清空";
mostCurrent._livsqlview.Clear();
 //BA.debugLineNum = 67;BA.debugLine="End Sub";
return "";
}
}
