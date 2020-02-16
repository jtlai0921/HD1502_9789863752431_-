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
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblresult = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnsubject = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livsubject = null;
  public Object[] GetGlobals() {
		return new Object[] {"Activity",mostCurrent._activity,"lblResult",mostCurrent._lblresult,"livSubject",mostCurrent._livsubject,"spnSubject",mostCurrent._spnsubject,"TabHost1",mostCurrent._tabhost1};
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
public static String  _aboutapp_click() throws Exception{
		Debug.PushSubsStack("AboutApp_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _about = "";
 BA.debugLineNum = 80;BA.debugLine="Sub AboutApp_Click";
Debug.ShouldStop(32768);
 BA.debugLineNum = 81;BA.debugLine="Dim About As String";
Debug.ShouldStop(65536);
_about = "";Debug.locals.put("About", _about);
 BA.debugLineNum = 82;BA.debugLine="About=\"題目：資訊系統(整合多個表單)\" & CRLF & \"設計者：李春雄老師\"";
Debug.ShouldStop(131072);
_about = "題目：資訊系統(整合多個表單)"+anywheresoftware.b4a.keywords.Common.CRLF+"設計者：李春雄老師";Debug.locals.put("About", _about);
 BA.debugLineNum = 83;BA.debugLine="Msgbox(About, \"關於本系統\")";
Debug.ShouldStop(262144);
anywheresoftware.b4a.keywords.Common.Msgbox(_about,"關於本系統",mostCurrent.activityBA);
 BA.debugLineNum = 84;BA.debugLine="End Sub";
Debug.ShouldStop(524288);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook1 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook2 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook3 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook4 = null;
anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook5 = null;
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 26;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(33554432);
 BA.debugLineNum = 27;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(67108864);
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 BA.debugLineNum = 28;BA.debugLine="Activity.Title =\"資訊系統(整合多個表單)\"";
Debug.ShouldStop(134217728);
mostCurrent._activity.setTitle((Object)("資訊系統(整合多個表單)"));
 BA.debugLineNum = 29;BA.debugLine="Activity.AddMenuItem(\"設計者：李春雄老師\", \"AboutApp\")";
Debug.ShouldStop(268435456);
mostCurrent._activity.AddMenuItem("設計者：李春雄老師","AboutApp");
 BA.debugLineNum = 30;BA.debugLine="Activity.AddMenuItem(\"結束\", \"ExitApp\")";
Debug.ShouldStop(536870912);
mostCurrent._activity.AddMenuItem("結束","ExitApp");
 BA.debugLineNum = 31;BA.debugLine="TabHost1.AddTab(\"下拉式(Spinner)\",\"Page1\")";
Debug.ShouldStop(1073741824);
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"下拉式(Spinner)","Page1");
 BA.debugLineNum = 32;BA.debugLine="TabHost1.AddTab(\"清單選項(ListView)\",\"Page2\")";
Debug.ShouldStop(-2147483648);
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"清單選項(ListView)","Page2");
 BA.debugLineNum = 33;BA.debugLine="spnSubject.Add(\"程式設計\")";
Debug.ShouldStop(1);
mostCurrent._spnsubject.Add("程式設計");
 BA.debugLineNum = 34;BA.debugLine="spnSubject.Add(\"資料庫系統\")";
Debug.ShouldStop(2);
mostCurrent._spnsubject.Add("資料庫系統");
 BA.debugLineNum = 35;BA.debugLine="spnSubject.Add(\"資料結構\")";
Debug.ShouldStop(4);
mostCurrent._spnsubject.Add("資料結構");
 BA.debugLineNum = 36;BA.debugLine="spnSubject.Add(\"計算機概論\")";
Debug.ShouldStop(8);
mostCurrent._spnsubject.Add("計算機概論");
 BA.debugLineNum = 37;BA.debugLine="spnSubject.Add(\"數位學習\")";
Debug.ShouldStop(16);
mostCurrent._spnsubject.Add("數位學習");
 BA.debugLineNum = 38;BA.debugLine="Dim PicBook1,PicBook2,PicBook3,PicBook4,PicBook5 As Bitmap";
Debug.ShouldStop(32);
_picbook1 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("PicBook1", _picbook1);
_picbook2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("PicBook2", _picbook2);
_picbook3 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("PicBook3", _picbook3);
_picbook4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("PicBook4", _picbook4);
_picbook5 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();Debug.locals.put("PicBook5", _picbook5);
 BA.debugLineNum = 39;BA.debugLine="PicBook1=LoadBitmap(File.DirAssets,\"book1.jpg\")";
Debug.ShouldStop(64);
_picbook1 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book1.jpg");Debug.locals.put("PicBook1", _picbook1);
 BA.debugLineNum = 40;BA.debugLine="PicBook2=LoadBitmap(File.DirAssets,\"book2.jpg\")";
Debug.ShouldStop(128);
_picbook2 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book2.jpg");Debug.locals.put("PicBook2", _picbook2);
 BA.debugLineNum = 41;BA.debugLine="PicBook3=LoadBitmap(File.DirAssets,\"book3.jpg\")";
Debug.ShouldStop(256);
_picbook3 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book3.jpg");Debug.locals.put("PicBook3", _picbook3);
 BA.debugLineNum = 42;BA.debugLine="PicBook4=LoadBitmap(File.DirAssets,\"book4.jpg\")";
Debug.ShouldStop(512);
_picbook4 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book4.jpg");Debug.locals.put("PicBook4", _picbook4);
 BA.debugLineNum = 43;BA.debugLine="PicBook5=LoadBitmap(File.DirAssets,\"book5.jpg\")";
Debug.ShouldStop(1024);
_picbook5 = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book5.jpg");Debug.locals.put("PicBook5", _picbook5);
 BA.debugLineNum = 44;BA.debugLine="livSubject.AddTwoLinesAndBitmap(\"程式設計\",\"6\",PicBook1)";
Debug.ShouldStop(2048);
mostCurrent._livsubject.AddTwoLinesAndBitmap("程式設計","6",(android.graphics.Bitmap)(_picbook1.getObject()));
 BA.debugLineNum = 45;BA.debugLine="livSubject.AddTwoLinesAndBitmap(\"資料庫系統\",\"5\",PicBook2)";
Debug.ShouldStop(4096);
mostCurrent._livsubject.AddTwoLinesAndBitmap("資料庫系統","5",(android.graphics.Bitmap)(_picbook2.getObject()));
 BA.debugLineNum = 46;BA.debugLine="livSubject.AddTwoLinesAndBitmap(\"資料結構\",\"3\",PicBook3)";
Debug.ShouldStop(8192);
mostCurrent._livsubject.AddTwoLinesAndBitmap("資料結構","3",(android.graphics.Bitmap)(_picbook3.getObject()));
 BA.debugLineNum = 47;BA.debugLine="livSubject.AddTwoLinesAndBitmap(\"計算機概論\",\"4\",PicBook4)";
Debug.ShouldStop(16384);
mostCurrent._livsubject.AddTwoLinesAndBitmap("計算機概論","4",(android.graphics.Bitmap)(_picbook4.getObject()));
 BA.debugLineNum = 48;BA.debugLine="livSubject.AddTwoLinesAndBitmap(\"數位學習\",\"3\",PicBook5)";
Debug.ShouldStop(32768);
mostCurrent._livsubject.AddTwoLinesAndBitmap("數位學習","3",(android.graphics.Bitmap)(_picbook5.getObject()));
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
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 55;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(4194304);
 BA.debugLineNum = 57;BA.debugLine="End Sub";
Debug.ShouldStop(16777216);
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
 BA.debugLineNum = 51;BA.debugLine="Sub Activity_Resume";
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
public static String  _exitapp_click() throws Exception{
		Debug.PushSubsStack("ExitApp_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _msg_value = "";
 BA.debugLineNum = 86;BA.debugLine="Sub ExitApp_Click";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 87;BA.debugLine="Dim Msg_Value As String";
Debug.ShouldStop(4194304);
_msg_value = "";Debug.locals.put("Msg_Value", _msg_value);
 BA.debugLineNum = 88;BA.debugLine="Msg_Value = Msgbox2(\"您確定要結束本系統嗎?\", \"資訊系統APP\", \"確認\", \"\", \"取消\", LoadBitmap(File.DirAssets,\"Leech_icon.jpg\"))";
Debug.ShouldStop(8388608);
_msg_value = BA.NumberToString(anywheresoftware.b4a.keywords.Common.Msgbox2("您確定要結束本系統嗎?","資訊系統APP","確認","","取消",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Leech_icon.jpg").getObject()),mostCurrent.activityBA));Debug.locals.put("Msg_Value", _msg_value);
 BA.debugLineNum = 89;BA.debugLine="If Msg_Value = DialogResponse.POSITIVE Then";
Debug.ShouldStop(16777216);
if ((_msg_value).equals(BA.NumberToString(anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE))) { 
 BA.debugLineNum = 90;BA.debugLine="Activity.Finish()  '活動完成";
Debug.ShouldStop(33554432);
mostCurrent._activity.Finish();
 BA.debugLineNum = 91;BA.debugLine="ExitApplication    '離開";
Debug.ShouldStop(67108864);
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
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
Debug.StartDebugging(mostCurrent.activityBA, 28562, new int[] {3}, "c9aa4c35-e420-45b2-8b1c-a2d426069da4");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim lblResult As Label";
mostCurrent._lblresult = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim spnSubject As Spinner";
mostCurrent._spnsubject = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim livSubject As ListView";
mostCurrent._livsubject = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="End Sub";
return "";
}
public static String  _livsubject_itemclick(int _position,Object _value) throws Exception{
		Debug.PushSubsStack("livSubject_ItemClick (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _temp = "";
String _bookname = "";
String _creditnum = "";
Debug.locals.put("Position", _position);
Debug.locals.put("Value", _value);
 BA.debugLineNum = 64;BA.debugLine="Sub livSubject_ItemClick (Position As Int, Value As Object)";
Debug.ShouldStop(-2147483648);
 BA.debugLineNum = 65;BA.debugLine="Dim Temp As String";
Debug.ShouldStop(1);
_temp = "";Debug.locals.put("Temp", _temp);
 BA.debugLineNum = 66;BA.debugLine="Dim BookName As String";
Debug.ShouldStop(2);
_bookname = "";Debug.locals.put("BookName", _bookname);
 BA.debugLineNum = 67;BA.debugLine="Dim CreditNum As String";
Debug.ShouldStop(4);
_creditnum = "";Debug.locals.put("CreditNum", _creditnum);
 BA.debugLineNum = 68;BA.debugLine="BookName = Value  ' 書籍名稱";
Debug.ShouldStop(8);
_bookname = BA.ObjectToString(_value);Debug.locals.put("BookName", _bookname);
 BA.debugLineNum = 69;BA.debugLine="Select Position      ' 取得學分數";
Debug.ShouldStop(16);
switch (_position) {
case 0:
 BA.debugLineNum = 70;BA.debugLine="Case 0: CreditNum = 6";
Debug.ShouldStop(32);
_creditnum = BA.NumberToString(6);Debug.locals.put("CreditNum", _creditnum);
 break;
case 1:
 BA.debugLineNum = 71;BA.debugLine="Case 1: CreditNum = 5";
Debug.ShouldStop(64);
_creditnum = BA.NumberToString(5);Debug.locals.put("CreditNum", _creditnum);
 break;
case 2:
 BA.debugLineNum = 72;BA.debugLine="Case 2: CreditNum = 3";
Debug.ShouldStop(128);
_creditnum = BA.NumberToString(3);Debug.locals.put("CreditNum", _creditnum);
 break;
case 3:
 BA.debugLineNum = 73;BA.debugLine="Case 3: CreditNum = 4";
Debug.ShouldStop(256);
_creditnum = BA.NumberToString(4);Debug.locals.put("CreditNum", _creditnum);
 break;
case 4:
 BA.debugLineNum = 74;BA.debugLine="Case 4: CreditNum = 3";
Debug.ShouldStop(512);
_creditnum = BA.NumberToString(3);Debug.locals.put("CreditNum", _creditnum);
 break;
}
;
 BA.debugLineNum = 76;BA.debugLine="Temp= \"你點選科目：\"  & CRLF &  \"「\"  & BookName & \"」學分數為：\" & CreditNum";
Debug.ShouldStop(2048);
_temp = "你點選科目："+anywheresoftware.b4a.keywords.Common.CRLF+"「"+_bookname+"」學分數為："+_creditnum;Debug.locals.put("Temp", _temp);
 BA.debugLineNum = 77;BA.debugLine="lblResult.Text =Temp";
Debug.ShouldStop(4096);
mostCurrent._lblresult.setText((Object)(_temp));
 BA.debugLineNum = 78;BA.debugLine="End Sub";
Debug.ShouldStop(8192);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _spnsubject_itemclick(int _position,Object _value) throws Exception{
		Debug.PushSubsStack("spnSubject_ItemClick (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _temp = "";
Debug.locals.put("Position", _position);
Debug.locals.put("Value", _value);
 BA.debugLineNum = 58;BA.debugLine="Sub spnSubject_ItemClick (Position As Int, Value As Object)";
Debug.ShouldStop(33554432);
 BA.debugLineNum = 59;BA.debugLine="Dim Temp As String";
Debug.ShouldStop(67108864);
_temp = "";Debug.locals.put("Temp", _temp);
 BA.debugLineNum = 60;BA.debugLine="Temp = \"你喜歡的科目為：\" & spnSubject.SelectedItem";
Debug.ShouldStop(134217728);
_temp = "你喜歡的科目為："+mostCurrent._spnsubject.getSelectedItem();Debug.locals.put("Temp", _temp);
 BA.debugLineNum = 61;BA.debugLine="Msgbox(Temp,\"訊息回覆\")";
Debug.ShouldStop(268435456);
anywheresoftware.b4a.keywords.Common.Msgbox(_temp,"訊息回覆",mostCurrent.activityBA);
 BA.debugLineNum = 62;BA.debugLine="End Sub";
Debug.ShouldStop(536870912);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
}
