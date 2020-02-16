package tw.idv.myebook.project10;

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

public class department extends Activity implements B4AActivity{
	public static department mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project10", "tw.idv.myebook.project10.department");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (department).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project10", "tw.idv.myebook.project10.department");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project10.department", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (department) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (department) Resume **");
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
		return department.class;
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
        BA.LogInfo("** Activity (department) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (department) Resume **");
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
public static anywheresoftware.b4a.sql.SQL _v5 = null;
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _v6 = null;
public static boolean _check_deptid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtdeptno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtdeptname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtdeptmanager = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerydeptno = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livdepartment = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnlistdata = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitdepartment = null;
public static String _vv7 = "";
public static String _vv0 = "";
public static String _vvv1 = "";
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public tw.idv.myebook.project10.main _vv6 = null;
public tw.idv.myebook.project10.course _v0 = null;
public tw.idv.myebook.project10.student _vv3 = null;
public tw.idv.myebook.project10.course_selection _course_selection = null;
public tw.idv.myebook.project10.query _vv2 = null;
public tw.idv.myebook.project10.writesql _vv4 = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="If SQLCmd.IsInitialized() = False Then  '判斷SQL物件是否已經初始化，如果沒有，則";
if (_v5.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 39;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyeSchoolDB.sqlite\", True) '將SQL物件初始化資料庫";
_v5.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 41;BA.debugLine="Activity.LoadLayout(\"Department\")";
mostCurrent._activity.LoadLayout("Department",mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="Activity.Title =\"【科系代碼管理系統】\"";
mostCurrent._activity.setTitle((Object)("【科系代碼管理系統】"));
 //BA.debugLineNum = 43;BA.debugLine="SpinQueryDeptNo.Visible=False  '查詢「科系代碼」的下拉式元件隱藏";
mostCurrent._spinquerydeptno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 44;BA.debugLine="edtDeptNo.Text=\"D003\"";
mostCurrent._edtdeptno.setText((Object)("D003"));
 //BA.debugLineNum = 45;BA.debugLine="edtDeptName.Text=\"數位系\"";
mostCurrent._edtdeptname.setText((Object)("數位系"));
 //BA.debugLineNum = 46;BA.debugLine="edtDeptManager.Text=\"劉主任\"";
mostCurrent._edtdeptmanager.setText((Object)("劉主任"));
 //BA.debugLineNum = 47;BA.debugLine="QueryDBList(\"Select * FROM 科系代碼表\")  '呼叫顯示目前存在的科系代碼表之副程式";
_vv5("Select * FROM 科系代碼表");
 //BA.debugLineNum = 48;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 50;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _deptno = "";
String _strsql = "";
 //BA.debugLineNum = 104;BA.debugLine="Sub btnDelete_Click   '刪除「科系代碼」資料";
 //BA.debugLineNum = 105;BA.debugLine="If edtDeptNo.Text=\"\" Then";
if ((mostCurrent._edtdeptno.getText()).equals("")) { 
 //BA.debugLineNum = 106;BA.debugLine="Msgbox(\"您尚未輸入「科系代碼」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「科系代碼」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 108;BA.debugLine="Dim DeptNo As String =edtDeptNo.Text";
_deptno = mostCurrent._edtdeptno.getText();
 //BA.debugLineNum = 109;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 110;BA.debugLine="strSQL = \"DELETE FROM 科系代碼表 WHERE 系碼 = '\" & DeptNo & \"'\"";
_strsql = "DELETE FROM 科系代碼表 WHERE 系碼 = '"+_deptno+"'";
 //BA.debugLineNum = 111;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 112;BA.debugLine="ToastMessageShow(\"刪除一筆「科系代碼」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆「科系代碼」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 113;BA.debugLine="QueryDBList(\"Select * FROM 科系代碼表\")  '呼叫顯示目前存在的科系代碼表之副程式";
_vv5("Select * FROM 科系代碼表");
 };
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitdepartment_click() throws Exception{
 //BA.debugLineNum = 175;BA.debugLine="Sub btnExitDepartment_Click";
 //BA.debugLineNum = 176;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 177;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._vv6.getObject()));
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _strsql = "";
String _deptno = "";
String _deptname = "";
String _deptmanager = "";
 //BA.debugLineNum = 58;BA.debugLine="Sub btnInsert_Click  '新增「科系代碼」資料";
 //BA.debugLineNum = 59;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 60;BA.debugLine="Dim DeptNo As String =edtDeptNo.Text               '將「科系代碼」欄位內容指定給變數";
_deptno = mostCurrent._edtdeptno.getText();
 //BA.debugLineNum = 61;BA.debugLine="Dim DeptName As String = edtDeptName.Text          '將「科系名稱」欄位內容指定給變數";
_deptname = mostCurrent._edtdeptname.getText();
 //BA.debugLineNum = 62;BA.debugLine="Dim DeptManager As String = edtDeptManager.Text    '將「系主任」欄位內容指定給變數";
_deptmanager = mostCurrent._edtdeptmanager.getText();
 //BA.debugLineNum = 64;BA.debugLine="If DeptNo=\"\" OR DeptName=\"\" OR DeptManager=\"\" Then";
if ((_deptno).equals("") || (_deptname).equals("") || (_deptmanager).equals("")) { 
 //BA.debugLineNum = 65;BA.debugLine="Msgbox(\"您尚未完整輸入「科系代碼」相關哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未完整輸入「科系代碼」相關哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 67;BA.debugLine="strSQL = \"INSERT INTO 科系代碼表(系碼,系名,系主任)\" & _              \"VALUES('\" & DeptNo & \"','\" & DeptName & \"','\" & DeptManager & \"')\"";
_strsql = "INSERT INTO 科系代碼表(系碼,系名,系主任)"+"VALUES('"+_deptno+"','"+_deptname+"','"+_deptmanager+"')";
 //BA.debugLineNum = 69;BA.debugLine="Check_DeptID_Exists(DeptNo)        '呼叫檢查「科系代碼」是否有重複新增之副程式";
_check_deptid_exists(_deptno);
 //BA.debugLineNum = 70;BA.debugLine="If Check_DeptID_Exist=True Then    '檢查是否有重複新增";
if (_check_deptid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 71;BA.debugLine="Msgbox(\"您已經重複新增「科系代碼」了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增「科系代碼」了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 73;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)                             '執行SQL指令，以成功的新增記錄";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 74;BA.debugLine="ToastMessageShow(\"您新增成功「科系代碼」記錄!\", True)   '顯示新增成功狀態";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("您新增成功「科系代碼」記錄!",anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 76;BA.debugLine="Check_DeptID_Exist=False           '設定沒有重複新增「科系代碼」";
_check_deptid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 78;BA.debugLine="QueryDBList(\"Select * FROM 科系代碼表\")  '呼叫顯示目前存在的科系代碼表之副程式";
_vv5("Select * FROM 科系代碼表");
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _btnlistdata_click() throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub btnListData_Click";
 //BA.debugLineNum = 173;BA.debugLine="QueryDBList(\"Select * FROM 科系代碼表\")  '呼叫顯示目前存在的科系代碼表之副程式";
_vv5("Select * FROM 科系代碼表");
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 117;BA.debugLine="Sub btnSelect_Click   '查詢「科系代碼」資料";
 //BA.debugLineNum = 118;BA.debugLine="SpinQueryDeptNo.Visible =True    '查詢「科系代碼」的下拉式元件顯示";
mostCurrent._spinquerydeptno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 119;BA.debugLine="edtDeptNo.Visible =False         '「科系代碼」欄位元件隱藏";
mostCurrent._edtdeptno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 121;BA.debugLine="SpinQueryDeptNo.Clear";
mostCurrent._spinquerydeptno.Clear();
 //BA.debugLineNum = 122;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 系碼 FROM 科系代碼表\")";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery("SELECT 系碼 FROM 科系代碼表")));
 //BA.debugLineNum = 123;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step95 = 1;
final int limit95 = (int) (_v6.getRowCount()-1);
for (_i = (int) (0); (step95 > 0 && _i <= limit95) || (step95 < 0 && _i >= limit95); _i = ((int)(0 + _i + step95))) {
 //BA.debugLineNum = 124;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_v6.setPosition(_i);
 //BA.debugLineNum = 125;BA.debugLine="SpinQueryDeptNo.Add(CursorDBPoint.GetString(\"系碼\"))";
mostCurrent._spinquerydeptno.Add(_v6.GetString("系碼"));
 }
};
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _deptno = "";
String _strsql = "";
 //BA.debugLineNum = 90;BA.debugLine="Sub btnUpdate_Click   '修改「科系代碼」資料";
 //BA.debugLineNum = 91;BA.debugLine="If edtDeptNo.Text=\"\" Then";
if ((mostCurrent._edtdeptno.getText()).equals("")) { 
 //BA.debugLineNum = 92;BA.debugLine="Msgbox(\"您尚未輸入「科系代碼」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入「科系代碼」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 94;BA.debugLine="Dim DeptNo As String =edtDeptNo.Text";
_deptno = mostCurrent._edtdeptno.getText();
 //BA.debugLineNum = 95;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 96;BA.debugLine="strSQL = \"UPDATE 科系代碼表 SET 系名 ='\" & edtDeptName.Text & \"'\" & _             \",系主任 ='\" & edtDeptManager.Text & \"'  WHERE 系碼 = '\" & DeptNo & \"'\"";
_strsql = "UPDATE 科系代碼表 SET 系名 ='"+mostCurrent._edtdeptname.getText()+"'"+",系主任 ='"+mostCurrent._edtdeptmanager.getText()+"'  WHERE 系碼 = '"+_deptno+"'";
 //BA.debugLineNum = 98;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_v5.ExecNonQuery(_strsql);
 //BA.debugLineNum = 99;BA.debugLine="ToastMessageShow(\"更新一筆「科系代碼」記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆「科系代碼」記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 100;BA.debugLine="QueryDBList(\"Select * FROM 科系代碼表\")  '呼叫顯示目前存在的科系代碼表之副程式";
_vv5("Select * FROM 科系代碼表");
 };
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_deptdata(String _strsql) throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Sub CallShow_DeptData(strSQL As String)  '顯示「科系代碼表」之副程式";
 //BA.debugLineNum = 139;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_strsql)));
 //BA.debugLineNum = 140;BA.debugLine="CursorDBPoint.Position = 0";
_v6.setPosition((int) (0));
 //BA.debugLineNum = 141;BA.debugLine="edtDeptNo.Text = CursorDBPoint.GetString(\"系碼\")";
mostCurrent._edtdeptno.setText((Object)(_v6.GetString("系碼")));
 //BA.debugLineNum = 142;BA.debugLine="edtDeptName.Text=CursorDBPoint.GetString(\"系名\")";
mostCurrent._edtdeptname.setText((Object)(_v6.GetString("系名")));
 //BA.debugLineNum = 143;BA.debugLine="edtDeptManager.Text=CursorDBPoint.GetString(\"系主任\")";
mostCurrent._edtdeptmanager.setText((Object)(_v6.GetString("系主任")));
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static String  _check_deptid_exists(String _strdeptno) throws Exception{
String _sql_query = "";
 //BA.debugLineNum = 81;BA.debugLine="Sub Check_DeptID_Exists(strDeptNo As String)  '檢查「科系代碼」是否有重複新增之副程式";
 //BA.debugLineNum = 82;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 83;BA.debugLine="SQL_Query=\"Select * FROM 科系代碼表 Where 系碼='\" & strDeptNo & \"' \"";
_sql_query = "Select * FROM 科系代碼表 Where 系碼='"+_strdeptno+"' ";
 //BA.debugLineNum = 84;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)  '執行SQL指令，並回傳值";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_sql_query)));
 //BA.debugLineNum = 85;BA.debugLine="If CursorDBPoint.RowCount>0 Then  '檢查目前資料庫中的「科系代碼表」是否已經存在一筆了";
if (_v6.getRowCount()>0) { 
 //BA.debugLineNum = 86;BA.debugLine="Check_DeptID_Exist=True        '代表重複新增「科系代碼」了!";
_check_deptid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 14;BA.debugLine="Dim edtDeptNo As EditText           '科系代碼";
mostCurrent._edtdeptno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 15;BA.debugLine="Dim edtDeptName As EditText         '科系名稱";
mostCurrent._edtdeptname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim edtDeptManager As EditText      '系主任";
mostCurrent._edtdeptmanager = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim btnInsert As Button             '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim btnUpdate As Button             '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnDelete As Button             '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnSelect As Button             '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim SpinQueryDeptNo As Spinner      '查詢「科系代碼」";
mostCurrent._spinquerydeptno = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim livDepartment As ListView       '顯示「科系代碼表」清單元件";
mostCurrent._livdepartment = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnListData As Button           '清單功能";
mostCurrent._btnlistdata = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim btnExitDepartment As Button     '離開功能";
mostCurrent._btnexitdepartment = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim strSqlViewTitle As String =\"\"";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim strLine As String =\"\"";
mostCurrent._vv0 = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim strSqlViewContent As String =\"\"";
mostCurrent._vvv1 = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL                         '宣告SQL物件";
_v5 = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor               '資料庫記錄指標";
_v6 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_DeptID_Exist As Boolean=False   '檢查新增「科系代碼」時的代號是否重複";
_check_deptid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _vv5(String _strsqlite) throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 146;BA.debugLine="Sub QueryDBList(strSQLite As String) '顯示目前存在的科系代碼表之副程式";
 //BA.debugLineNum = 147;BA.debugLine="livDepartment.Clear '清單清空";
mostCurrent._livdepartment.Clear();
 //BA.debugLineNum = 148;BA.debugLine="strSqlViewTitle =\"\"";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 149;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._vvv1 = "";
 //BA.debugLineNum = 150;BA.debugLine="strLine =\"\"";
mostCurrent._vv0 = "";
 //BA.debugLineNum = 151;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQLite)";
_v6.setObject((android.database.Cursor)(_v5.ExecQuery(_strsqlite)));
 //BA.debugLineNum = 152;BA.debugLine="livDepartment.SingleLineLayout.Label.TextSize=16";
mostCurrent._livdepartment.getSingleLineLayout().Label.setTextSize((float) (16));
 //BA.debugLineNum = 153;BA.debugLine="livDepartment.SingleLineLayout.ItemHeight = 20dip";
mostCurrent._livdepartment.getSingleLineLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (20)));
 //BA.debugLineNum = 154;BA.debugLine="For i = 0 To CursorDBPoint.ColumnCount-1    '取得「欄位名稱」";
{
final int step123 = 1;
final int limit123 = (int) (_v6.getColumnCount()-1);
for (_i = (int) (0); (step123 > 0 && _i <= limit123) || (step123 < 0 && _i >= limit123); _i = ((int)(0 + _i + step123))) {
 //BA.debugLineNum = 155;BA.debugLine="strSqlViewTitle=strSqlViewTitle & CursorDBPoint.GetColumnName(i) & \"          \"";
mostCurrent._vv7 = mostCurrent._vv7+_v6.GetColumnName(_i)+"          ";
 //BA.debugLineNum = 156;BA.debugLine="strLine=strLine & \"=======\"              '設定「分隔水平線之每一小段的長度」";
mostCurrent._vv0 = mostCurrent._vv0+"=======";
 }
};
 //BA.debugLineNum = 158;BA.debugLine="livDepartment.AddSingleLine (strSqlViewTitle)  '顯示「欄位名稱」";
mostCurrent._livdepartment.AddSingleLine(mostCurrent._vv7);
 //BA.debugLineNum = 159;BA.debugLine="livDepartment.AddSingleLine (strLine)          '顯示「分隔水平線」";
mostCurrent._livdepartment.AddSingleLine(mostCurrent._vv0);
 //BA.debugLineNum = 160;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1     '控制「記錄」的筆數";
{
final int step129 = 1;
final int limit129 = (int) (_v6.getRowCount()-1);
for (_i = (int) (0); (step129 > 0 && _i <= limit129) || (step129 < 0 && _i >= limit129); _i = ((int)(0 + _i + step129))) {
 //BA.debugLineNum = 161;BA.debugLine="CursorDBPoint.Position = i              '記錄指標從第1筆開始(索引為0)";
_v6.setPosition(_i);
 //BA.debugLineNum = 162;BA.debugLine="For j=0 To CursorDBPoint.ColumnCount-1    '控制「欄位」的個數";
{
final int step131 = 1;
final int limit131 = (int) (_v6.getColumnCount()-1);
for (_j = (int) (0); (step131 > 0 && _j <= limit131) || (step131 < 0 && _j >= limit131); _j = ((int)(0 + _j + step131))) {
 //BA.debugLineNum = 163;BA.debugLine="strSqlViewContent=strSqlViewContent & CursorDBPoint.GetString(CursorDBPoint.GetColumnName(j)) & \"    \"";
mostCurrent._vvv1 = mostCurrent._vvv1+_v6.GetString(_v6.GetColumnName(_j))+"    ";
 }
};
 //BA.debugLineNum = 165;BA.debugLine="livDepartment.AddSingleLine ((i+1) & \". \" & strSqlViewContent )   '顯示「每一筆記錄的內容」";
mostCurrent._livdepartment.AddSingleLine(BA.NumberToString((_i+1))+". "+mostCurrent._vvv1);
 //BA.debugLineNum = 166;BA.debugLine="strSqlViewContent =\"\"";
mostCurrent._vvv1 = "";
 }
};
 //BA.debugLineNum = 168;BA.debugLine="ToastMessageShow(\"目前的科系記錄共有: \" & CursorDBPoint.RowCount & \" 筆!\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("目前的科系記錄共有: "+BA.NumberToString(_v6.getRowCount())+" 筆!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 169;BA.debugLine="lblResult.Text =\"===本校的科系清單共有( \" & CursorDBPoint.RowCount & \" 筆)===\"";
mostCurrent._lblresult.setText((Object)("===本校的科系清單共有( "+BA.NumberToString(_v6.getRowCount())+" 筆)==="));
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerydeptno_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querydeptno = "";
 //BA.debugLineNum = 129;BA.debugLine="Sub SpinQueryDeptNo_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 130;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 131;BA.debugLine="Dim QueryDeptNo As String =SpinQueryDeptNo.SelectedItem";
_querydeptno = mostCurrent._spinquerydeptno.getSelectedItem();
 //BA.debugLineNum = 132;BA.debugLine="SQL_Query=\"Select * FROM 科系代碼表 Where 系碼 = '\" & QueryDeptNo & \"'\"";
_sql_query = "Select * FROM 科系代碼表 Where 系碼 = '"+_querydeptno+"'";
 //BA.debugLineNum = 133;BA.debugLine="CallShow_DeptData(SQL_Query)";
_callshow_deptdata(_sql_query);
 //BA.debugLineNum = 134;BA.debugLine="SpinQueryDeptNo.Visible =False    '查詢「科系代碼」的下拉式元件隱藏";
mostCurrent._spinquerydeptno.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 135;BA.debugLine="edtDeptNo.Visible =True           '「科系代碼」欄位元件顯示";
mostCurrent._edtdeptno.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
}
