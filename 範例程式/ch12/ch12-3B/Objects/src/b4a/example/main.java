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
public static anywheresoftware.b4a.samples.httputils2.httpjob _myhttpjob = null;
public static anywheresoftware.b4a.objects.collections.JSONParser _json = null;
public static String _opendataurl = "";
public anywheresoftware.b4a.objects.SpinnerWrapper _spinner_country = null;
public anywheresoftware.b4a.objects.collections.List _arrayrows = null;
public anywheresoftware.b4a.objects.collections.Map _key_value = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_country = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_publishagency = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_publishtime = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_sitename = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_title = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_twd97lat = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_twd97lon = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_uvi = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_resulttitle = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 38;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="Activity.Title =\"紫外線即時監測資料JSON\"";
mostCurrent._activity.setTitle((Object)("紫外線即時監測資料JSON"));
 //BA.debugLineNum = 40;BA.debugLine="MyHttpJob.Initialize(\"JSON\", Me)";
_myhttpjob._initialize(processBA,"JSON",main.getObject());
 //BA.debugLineNum = 41;BA.debugLine="MyHttpJob.Download(OpenDataUrl)";
_myhttpjob._download(_opendataurl);
 //BA.debugLineNum = 42;BA.debugLine="Spinner_Country.Add(\"請選擇縣市\")";
mostCurrent._spinner_country.Add("請選擇縣市");
 //BA.debugLineNum = 43;BA.debugLine="Label_ResultTitle.Text=\"\"";
mostCurrent._label_resulttitle.setText((Object)(""));
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _clear_uvidata() throws Exception{
 //BA.debugLineNum = 54;BA.debugLine="Sub Clear_UVIData  '定義「清除各縣市的紫外線指數」之副程式";
 //BA.debugLineNum = 55;BA.debugLine="Label_UVI.Text= \"\"";
mostCurrent._label_uvi.setText((Object)(""));
 //BA.debugLineNum = 56;BA.debugLine="Label_Country.Text= \"\"";
mostCurrent._label_country.setText((Object)(""));
 //BA.debugLineNum = 57;BA.debugLine="Label_PublishTime.Text= \"\"";
mostCurrent._label_publishtime.setText((Object)(""));
 //BA.debugLineNum = 58;BA.debugLine="Label_PublishAgency.Text=  \"\"";
mostCurrent._label_publishagency.setText((Object)(""));
 //BA.debugLineNum = 59;BA.debugLine="Label_SiteName.Text=  \"\"";
mostCurrent._label_sitename.setText((Object)(""));
 //BA.debugLineNum = 60;BA.debugLine="Label_TWD97Lat.Text=  \"\"";
mostCurrent._label_twd97lat.setText((Object)(""));
 //BA.debugLineNum = 61;BA.debugLine="Label_TWD97Lon.Text=  \"\"";
mostCurrent._label_twd97lon.setText((Object)(""));
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _display_uvi() throws Exception{
int _i = 0;
 //BA.debugLineNum = 46;BA.debugLine="Sub Display_UVI  '定義「顯示各縣市的紫外線指數」之副程式";
 //BA.debugLineNum = 47;BA.debugLine="ArrayRows = JSON.NextArray()      '將取得的JSON資料剖析成List串列(亦即資料列)";
mostCurrent._arrayrows = _json.NextArray();
 //BA.debugLineNum = 48;BA.debugLine="For i = 0 To ArrayRows.Size - 1   '從資料列再分割成許多欄位值";
{
final int step30 = 1;
final int limit30 = (int) (mostCurrent._arrayrows.getSize()-1);
for (_i = (int) (0); (step30 > 0 && _i <= limit30) || (step30 < 0 && _i >= limit30); _i = ((int)(0 + _i + step30))) {
 //BA.debugLineNum = 49;BA.debugLine="Key_Value = ArrayRows.Get(i)";
mostCurrent._key_value.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(mostCurrent._arrayrows.Get(_i)));
 //BA.debugLineNum = 50;BA.debugLine="Spinner_Country.Add(Key_Value.Get(\"County\"))";
mostCurrent._spinner_country.Add(BA.ObjectToString(mostCurrent._key_value.Get((Object)("County"))));
 }
};
 //BA.debugLineNum = 52;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim Spinner_Country As Spinner";
mostCurrent._spinner_country = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim ArrayRows As List            'ArrayRows視為陣列";
mostCurrent._arrayrows = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 25;BA.debugLine="Dim Key_Value As Map             'Key_Value視為成對的(key和Value)";
mostCurrent._key_value = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 26;BA.debugLine="Dim Label_Country As Label";
mostCurrent._label_country = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Label_PublishAgency As Label";
mostCurrent._label_publishagency = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim Label_PublishTime As Label";
mostCurrent._label_publishtime = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim Label_SiteName As Label";
mostCurrent._label_sitename = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim Label_Title As Label";
mostCurrent._label_title = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim Label_TWD97Lat As Label";
mostCurrent._label_twd97lat = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim Label_TWD97Lon As Label";
mostCurrent._label_twd97lon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim Label_UVI As Label";
mostCurrent._label_uvi = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim Label_ResultTitle As Label";
mostCurrent._label_resulttitle = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 63;BA.debugLine="Sub JobDone(job As HttpJob)";
 //BA.debugLineNum = 64;BA.debugLine="If job.Success Then";
if (_job._success) { 
 //BA.debugLineNum = 65;BA.debugLine="ToastMessageShow(\"資料載入完成!!\", True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("資料載入完成!!",anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 66;BA.debugLine="JSON.Initialize(job.GetString)";
_json.Initialize(_job._getstring());
 //BA.debugLineNum = 67;BA.debugLine="Display_UVI  '呼叫「顯示各縣市的紫外線指數」之副程式";
_display_uvi();
 }else {
 //BA.debugLineNum = 69;BA.debugLine="Msgbox(job.ErrorMessage, \"Error\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_job._errormessage,"Error",mostCurrent.activityBA);
 };
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 16;BA.debugLine="Dim MyHttpJob As HttpJob  '使用HTTP函式庫可以在網路上交換資料。";
_myhttpjob = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 17;BA.debugLine="Dim JSON As JSONParser    'JSONParser物件是用來剖析JSON資料(類似XML)";
_json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 18;BA.debugLine="Dim OpenDataUrl As String";
_opendataurl = "";
 //BA.debugLineNum = 19;BA.debugLine="OpenDataUrl = \"http://opendata.epa.gov.tw/ws/Data/UV/?format=json\"";
_opendataurl = "http://opendata.epa.gov.tw/ws/Data/UV/?format=json";
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public static String  _spinner_country_itemclick(int _position,Object _value) throws Exception{
int _i = 0;
 //BA.debugLineNum = 74;BA.debugLine="Sub Spinner_Country_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 75;BA.debugLine="For i = 0 To ArrayRows.Size - 1";
{
final int step54 = 1;
final int limit54 = (int) (mostCurrent._arrayrows.getSize()-1);
for (_i = (int) (0); (step54 > 0 && _i <= limit54) || (step54 < 0 && _i >= limit54); _i = ((int)(0 + _i + step54))) {
 //BA.debugLineNum = 76;BA.debugLine="Key_Value = ArrayRows.Get(i)";
mostCurrent._key_value.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(mostCurrent._arrayrows.Get(_i)));
 //BA.debugLineNum = 77;BA.debugLine="If Spinner_Country.SelectedItem = Key_Value.Get(\"County\") Then";
if ((mostCurrent._spinner_country.getSelectedItem()).equals(BA.ObjectToString(mostCurrent._key_value.Get((Object)("County"))))) { 
 //BA.debugLineNum = 78;BA.debugLine="Label_ResultTitle.Text=\"[\" & Key_Value.Get(\"County\") & \"] 查詢結果\"";
mostCurrent._label_resulttitle.setText((Object)("["+BA.ObjectToString(mostCurrent._key_value.Get((Object)("County")))+"] 查詢結果"));
 //BA.debugLineNum = 79;BA.debugLine="Label_UVI.Text= \"UVI:\" & Key_Value.Get(\"UVI\")";
mostCurrent._label_uvi.setText((Object)("UVI:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("UVI")))));
 //BA.debugLineNum = 80;BA.debugLine="Label_Country.Text= \"縣市:\" & Key_Value.Get(\"County\")";
mostCurrent._label_country.setText((Object)("縣市:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("County")))));
 //BA.debugLineNum = 81;BA.debugLine="Label_PublishTime.Text= \"發布時間:\" & Key_Value.Get(\"PublishTime\")";
mostCurrent._label_publishtime.setText((Object)("發布時間:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("PublishTime")))));
 //BA.debugLineNum = 82;BA.debugLine="Label_PublishAgency.Text= \"發布機關:\" & Key_Value.Get(\"PublishAgency\")";
mostCurrent._label_publishagency.setText((Object)("發布機關:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("PublishAgency")))));
 //BA.debugLineNum = 83;BA.debugLine="Label_SiteName.Text= \"測站名稱:\" & Key_Value.Get(\"SiteName\")";
mostCurrent._label_sitename.setText((Object)("測站名稱:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("SiteName")))));
 //BA.debugLineNum = 84;BA.debugLine="Label_TWD97Lat.Text= \"緯度:\" & Key_Value.Get(\"TWD97Lat\")";
mostCurrent._label_twd97lat.setText((Object)("緯度:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("TWD97Lat")))));
 //BA.debugLineNum = 85;BA.debugLine="Label_TWD97Lon.Text= \"經度:\" & Key_Value.Get(\"TWD97Lon\")";
mostCurrent._label_twd97lon.setText((Object)("經度:"+BA.ObjectToString(mostCurrent._key_value.Get((Object)("TWD97Lon")))));
 //BA.debugLineNum = 86;BA.debugLine="i = ArrayRows.Size - 1";
_i = (int) (mostCurrent._arrayrows.getSize()-1);
 }else {
 //BA.debugLineNum = 88;BA.debugLine="Clear_UVIData  '呼叫「清除各縣市的紫外線指數」之副程式";
_clear_uvidata();
 };
 }
};
 //BA.debugLineNum = 91;BA.debugLine="End Sub";
return "";
}
}
