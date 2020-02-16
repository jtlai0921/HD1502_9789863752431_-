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
			processBA = new BA(this.getApplicationContext(), null, null, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.main");
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
		activityBA = new BA(this, layout, processBA, "Leech.myebook.ch16.Project", "Leech.myebook.ch16.Project.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "Leech.myebook.ch16.Project.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
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
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncallquery = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncallcourse_selection = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncallcourse = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncalldepartment = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btncallstudent = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnexitapp = null;
public Leech.myebook.ch16.Project.department _department = null;
public Leech.myebook.ch16.Project.course _course = null;
public Leech.myebook.ch16.Project.student _student = null;
public Leech.myebook.ch16.Project.course_selection _course_selection = null;
public Leech.myebook.ch16.Project.query _query = null;
public Leech.myebook.ch16.Project.writesql _writesql = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (department.mostCurrent != null);
vis = vis | (course.mostCurrent != null);
vis = vis | (student.mostCurrent != null);
vis = vis | (course_selection.mostCurrent != null);
vis = vis | (query.mostCurrent != null);
vis = vis | (writesql.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 35;BA.debugLine="If File.Exists(File.DirDefaultExternal, \"MyeSchoolDB.sqlite\") = False Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 37;BA.debugLine="File.Copy(File.DirAssets, \"MyeSchoolDB.sqlite\",File.DirDefaultExternal, \"MyeSchoolDB.sqlite\")";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"MyeSchoolDB.sqlite",anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyeSchoolDB.sqlite");
 };
 //BA.debugLineNum = 39;BA.debugLine="Activity.LoadLayout(\"Main\")          '使用「Main(版面配置檔)」來輸出";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="Activity.Title =\"行動選課系統App\"    '本頁的標題名稱";
mostCurrent._activity.setTitle((Object)("行動選課系統App"));
 //BA.debugLineNum = 41;BA.debugLine="TabHost1.AddTab(\"科系管理\",\"Department_home\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"科系管理","Department_home");
 //BA.debugLineNum = 42;BA.debugLine="TabHost1.AddTab(\"課程管理\",\"Course_home\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"課程管理","Course_home");
 //BA.debugLineNum = 43;BA.debugLine="TabHost1.AddTab(\"學生管理\",\"Student_home\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"學生管理","Student_home");
 //BA.debugLineNum = 44;BA.debugLine="TabHost1.AddTab(\"選課作業\",\"Course_Selection_home\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"選課作業","Course_Selection_home");
 //BA.debugLineNum = 45;BA.debugLine="TabHost1.AddTab(\"查詢作業\",\"Query_home\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"查詢作業","Query_home");
 //BA.debugLineNum = 46;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 77;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 75;BA.debugLine="End Sub";
return "";
}
public static String  _btncallcourse_click() throws Exception{
 //BA.debugLineNum = 51;BA.debugLine="Sub btnCallCourse_Click";
 //BA.debugLineNum = 52;BA.debugLine="StartActivity(Course)             '課程管理";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._course.getObject()));
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _btncallcourse_selection_click() throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Sub btnCallCourse_Selection_Click";
 //BA.debugLineNum = 58;BA.debugLine="StartActivity(Course_Selection)   '選課作業";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._course_selection.getObject()));
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _btncalldepartment_click() throws Exception{
 //BA.debugLineNum = 48;BA.debugLine="Sub btnCallDepartment_Click";
 //BA.debugLineNum = 49;BA.debugLine="StartActivity(Department)         '科系管理";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._department.getObject()));
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _btncallquery_click() throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub btnCallQuery_Click";
 //BA.debugLineNum = 61;BA.debugLine="StartActivity(Query)              '查詢作業";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._query.getObject()));
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _btncallstudent_click() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub btnCallStudent_Click";
 //BA.debugLineNum = 55;BA.debugLine="StartActivity(Student)            '學生管理";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._student.getObject()));
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _btnexitapp_click() throws Exception{
String _msg_value = "";
 //BA.debugLineNum = 64;BA.debugLine="Sub btnExitApp_Click";
 //BA.debugLineNum = 65;BA.debugLine="Dim Msg_Value As String";
_msg_value = "";
 //BA.debugLineNum = 66;BA.debugLine="Msg_Value = Msgbox2(\"您確定要結束本系統嗎?\", \"行動選課系統APP\", \"確認\", \"\", \"取消\", LoadBitmap(File.DirAssets,\"Leech_icon.jpg\"))";
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要結束本系統嗎?","行動選課系統APP","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Leech_icon.jpg").getObject()),mostCurrent.activityBA));
 //BA.debugLineNum = 67;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then'";
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 //BA.debugLineNum = 68;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 69;BA.debugLine="ExitApplication    '離開";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
department._process_globals();
course._process_globals();
student._process_globals();
course_selection._process_globals();
query._process_globals();
writesql._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnCallQuery As Button";
mostCurrent._btncallquery = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim btnCallCourse_Selection As Button";
mostCurrent._btncallcourse_selection = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim btnCallCourse As Button";
mostCurrent._btncallcourse = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim btnCallDepartment As Button";
mostCurrent._btncalldepartment = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnCallStudent As Button";
mostCurrent._btncallstudent = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnExitApp As Button";
mostCurrent._btnexitapp = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
}
