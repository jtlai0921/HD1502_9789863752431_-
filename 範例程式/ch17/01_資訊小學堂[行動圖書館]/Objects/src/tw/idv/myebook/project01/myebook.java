package tw.idv.myebook.project01;

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

public class myebook extends Activity implements B4AActivity{
	public static myebook mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.myebook");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (myebook).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.myebook");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.myebook", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (myebook) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (myebook) Resume **");
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
		return myebook.class;
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
        BA.LogInfo("** Activity (myebook) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (myebook) Resume **");
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
public static String _sql_query = "";
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursordbpoint = null;
public static String _dns = "";
public static boolean _check_mylovebooks_exist = false;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbookid = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbookname = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblauthor = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblpubdate = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblprice = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbookclass = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblbookstore = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnmylove = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
public tw.idv.myebook.project01.keywordquery _keywordquery = null;
public tw.idv.myebook.project01.bookclassquery _bookclassquery = null;
public tw.idv.myebook.project01.userapp _userapp = null;
public tw.idv.myebook.project01.managerapp _managerapp = null;
public tw.idv.myebook.project01.booksmanager _booksmanager = null;
public tw.idv.myebook.project01.bookstoremanager _bookstoremanager = null;
public tw.idv.myebook.project01.bookstoretable _bookstoretable = null;
public tw.idv.myebook.project01.bookclassmanager _bookclassmanager = null;
public tw.idv.myebook.project01.bookclasstable _bookclasstable = null;
public tw.idv.myebook.project01.userlogin _userlogin = null;
public tw.idv.myebook.project01.managerlogin _managerlogin = null;
public tw.idv.myebook.project01.mylovebooks _mylovebooks = null;
public tw.idv.myebook.project01.readermanager _readermanager = null;
public tw.idv.myebook.project01.readertable _readertable = null;
public tw.idv.myebook.project01.charts _charts = null;
public tw.idv.myebook.project01.dataanalysis _dataanalysis = null;
public tw.idv.myebook.project01.piechart _piechart = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 30;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 31;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 32;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 34;BA.debugLine="Activity.LoadLayout(\"Myebook\")";
mostCurrent._activity.LoadLayout("Myebook",mostCurrent.activityBA);
 //BA.debugLineNum = 35;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 36;BA.debugLine="Activity.Title =\"您正查詢「\" & BookTable.BookName_1  & \"」電子書\"";
mostCurrent._activity.setTitle((Object)("您正查詢「"+mostCurrent._booktable._bookname_1+"」電子書"));
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 38;BA.debugLine="Activity.Title =\"您正查詢「\" & KeywordQuery.BookName_2  & \"」電子書\"";
mostCurrent._activity.setTitle((Object)("您正查詢「"+mostCurrent._keywordquery._bookname_2+"」電子書"));
 };
 //BA.debugLineNum = 40;BA.debugLine="Show_BookPicture '呼叫顯示書籍的封面照片之副程式";
_show_bookpicture();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 153;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 155;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 44;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 45;BA.debugLine="CheckMyloveBooks_Exists(BookTable.BookID_1)";
_checkmylovebooks_exists(mostCurrent._booktable._bookid_1);
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 47;BA.debugLine="CheckMyloveBooks_Exists(KeywordQuery.BookID_2)";
_checkmylovebooks_exists(mostCurrent._keywordquery._bookid_2);
 }else if(mostCurrent._userapp._changequery==3) { 
 //BA.debugLineNum = 49;BA.debugLine="CheckMyloveBooks_Exists(BookClassQuery.BookID_3)";
_checkmylovebooks_exists(mostCurrent._bookclassquery._bookid_3);
 }else if(mostCurrent._userapp._changequery==4) { 
 //BA.debugLineNum = 51;BA.debugLine="CheckMyloveBooks_Exists( MyloveBooks.BookID_4)";
_checkmylovebooks_exists(mostCurrent._mylovebooks._bookid_4);
 };
 //BA.debugLineNum = 54;BA.debugLine="If Check_MyloveBooks_Exist=True Then";
if (_check_mylovebooks_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 55;BA.debugLine="btnMyLove.Text=\"移除書籤\"";
mostCurrent._btnmylove.setText((Object)("移除書籤"));
 }else if(_check_mylovebooks_exist==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 57;BA.debugLine="btnMyLove.Text=\"加入書籤\"";
mostCurrent._btnmylove.setText((Object)("加入書籤"));
 };
 //BA.debugLineNum = 59;BA.debugLine="End Sub";
return "";
}
public static String  _btnmylove_click() throws Exception{
long _now = 0L;
String _strsql = "";
String _account = "";
 //BA.debugLineNum = 117;BA.debugLine="Sub btnMyLove_Click  '加入書籤或移除書籤";
 //BA.debugLineNum = 118;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 119;BA.debugLine="now=DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 120;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 121;BA.debugLine="Dim Account As String =UserLogin.UserAccount";
_account = mostCurrent._userlogin._useraccount;
 //BA.debugLineNum = 123;BA.debugLine="If btnMyLove.Text=\"加入書籤\" AND Check_MyloveBooks_Exist=False Then   '加入書籤";
if ((mostCurrent._btnmylove.getText()).equals("加入書籤") && _check_mylovebooks_exist==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 124;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 125;BA.debugLine="strSQL = \"INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('\" & BookTable.BookID_1 & \"','\" & UserLogin.UserAccount & \"','1','\" & DateTime.Date(now) & \"')\"";
_strsql = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('"+mostCurrent._booktable._bookid_1+"','"+mostCurrent._userlogin._useraccount+"','1','"+anywheresoftware.b4a.keywords.Common.DateTime.Date(_now)+"')";
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 127;BA.debugLine="strSQL = \"INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('\" & KeywordQuery.BookID_2 & \"','\" & UserLogin.UserAccount & \"','1','\" & DateTime.Date(now) & \"')\"";
_strsql = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('"+mostCurrent._keywordquery._bookid_2+"','"+mostCurrent._userlogin._useraccount+"','1','"+anywheresoftware.b4a.keywords.Common.DateTime.Date(_now)+"')";
 }else if(mostCurrent._userapp._changequery==3) { 
 //BA.debugLineNum = 129;BA.debugLine="strSQL = \"INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('\" & BookClassQuery.BookID_3 & \"','\" & UserLogin.UserAccount & \"','1','\" & DateTime.Date(now) & \"')\"";
_strsql = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('"+mostCurrent._bookclassquery._bookid_3+"','"+mostCurrent._userlogin._useraccount+"','1','"+anywheresoftware.b4a.keywords.Common.DateTime.Date(_now)+"')";
 }else if(mostCurrent._userapp._changequery==4) { 
 //BA.debugLineNum = 131;BA.debugLine="strSQL = \"INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('\" & MyloveBooks.BookID_4 & \"','\" & UserLogin.UserAccount & \"','1','\" & DateTime.Date(now) & \"')\"";
_strsql = "INSERT INTO 借閱記錄表(書號,帳號,書籤,日期) VALUES('"+mostCurrent._mylovebooks._bookid_4+"','"+mostCurrent._userlogin._useraccount+"','1','"+anywheresoftware.b4a.keywords.Common.DateTime.Date(_now)+"')";
 };
 //BA.debugLineNum = 133;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 134;BA.debugLine="ToastMessageShow(\"加入書籤...!\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("加入書籤...!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 135;BA.debugLine="btnMyLove.Text=\"移除書籤\"";
mostCurrent._btnmylove.setText((Object)("移除書籤"));
 }else if((mostCurrent._btnmylove.getText()).equals("移除書籤") && _check_mylovebooks_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 138;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 139;BA.debugLine="strSQL = \"DELETE FROM 借閱記錄表 WHERE 書號='\" & BookTable.BookID_1 &\"' And 帳號='\" & Account  & \"'\"";
_strsql = "DELETE FROM 借閱記錄表 WHERE 書號='"+mostCurrent._booktable._bookid_1+"' And 帳號='"+_account+"'";
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 141;BA.debugLine="strSQL = \"DELETE FROM 借閱記錄表 WHERE 書號='\" & KeywordQuery.BookID_2 &\"' And 帳號='\" & Account  & \"'\"";
_strsql = "DELETE FROM 借閱記錄表 WHERE 書號='"+mostCurrent._keywordquery._bookid_2+"' And 帳號='"+_account+"'";
 }else if(mostCurrent._userapp._changequery==3) { 
 //BA.debugLineNum = 143;BA.debugLine="strSQL = \"DELETE FROM 借閱記錄表 WHERE 書號='\" & BookClassQuery.BookID_3 &\"' And 帳號='\" & Account  & \"'\"";
_strsql = "DELETE FROM 借閱記錄表 WHERE 書號='"+mostCurrent._bookclassquery._bookid_3+"' And 帳號='"+_account+"'";
 }else if(mostCurrent._userapp._changequery==4) { 
 //BA.debugLineNum = 145;BA.debugLine="strSQL = \"DELETE FROM 借閱記錄表 WHERE 書號='\" & MyloveBooks.BookID_4 &\"' And 帳號='\" & Account  & \"'\"";
_strsql = "DELETE FROM 借閱記錄表 WHERE 書號='"+mostCurrent._mylovebooks._bookid_4+"' And 帳號='"+_account+"'";
 };
 //BA.debugLineNum = 147;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 148;BA.debugLine="ToastMessageShow(\"移除書籤...!\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("移除書籤...!",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 149;BA.debugLine="btnMyLove.Text=\"加入書籤\"";
mostCurrent._btnmylove.setText((Object)("加入書籤"));
 };
 //BA.debugLineNum = 151;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 157;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 158;BA.debugLine="Activity.Finish()    '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 159;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 160;BA.debugLine="StartActivity(BookTable)              ' 返回BookTable主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._booktable.getObject()));
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 162;BA.debugLine="StartActivity(KeywordQuery)           ' 返回KeywordQuery主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._keywordquery.getObject()));
 }else if(mostCurrent._userapp._changequery==3) { 
 //BA.debugLineNum = 164;BA.debugLine="StartActivity(BookClassQuery)         ' 返回BookClassQuery主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._bookclassquery.getObject()));
 }else if(mostCurrent._userapp._changequery==4) { 
 //BA.debugLineNum = 166;BA.debugLine="StartActivity(MyloveBooks)            ' 返回MyloveBooks主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._mylovebooks.getObject()));
 };
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 169;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 170;BA.debugLine="Dim i As Intent  '宣告「意圖物件」來啟動Android內建的APP";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 171;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://play.google.com/store/apps/details?id=tw.idv.myebook.infoclass\")";
_i.Initialize(_i.ACTION_VIEW,"https://play.google.com/store/apps/details?id=tw.idv.myebook.infoclass");
 //BA.debugLineNum = 172;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 173;BA.debugLine="End Sub";
return "";
}
public static String  _checkmylovebooks_exists(String _strbookid) throws Exception{
String _account = "";
 //BA.debugLineNum = 61;BA.debugLine="Sub CheckMyloveBooks_Exists(strBookID As String)";
 //BA.debugLineNum = 62;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 63;BA.debugLine="Dim Account As String =UserLogin.UserAccount";
_account = mostCurrent._userlogin._useraccount;
 //BA.debugLineNum = 64;BA.debugLine="SQL_Query=\"Select * FROM 借閱記錄表 Where 書號='\" & strBookID &\"' And 帳號='\" & Account  & \"'\"";
_sql_query = "Select * FROM 借閱記錄表 Where 書號='"+_strbookid+"' And 帳號='"+_account+"'";
 //BA.debugLineNum = 65;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 66;BA.debugLine="If CursorDBPoint.RowCount=1 Then";
if (_cursordbpoint.getRowCount()==1) { 
 //BA.debugLineNum = 67;BA.debugLine="Check_MyloveBooks_Exist=True";
_check_mylovebooks_exist = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 68;BA.debugLine="btnMyLove.Text=\"移除書籤\"";
mostCurrent._btnmylove.setText((Object)("移除書籤"));
 }else {
 //BA.debugLineNum = 70;BA.debugLine="Check_MyloveBooks_Exist=False";
_check_mylovebooks_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 71;BA.debugLine="btnMyLove.Text=\"加入書籤\"";
mostCurrent._btnmylove.setText((Object)("加入書籤"));
 };
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim ImageView2 As ImageView";
mostCurrent._imageview2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim lblBookID As Label";
mostCurrent._lblbookid = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim lblBookName As Label";
mostCurrent._lblbookname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim lblAuthor As Label";
mostCurrent._lblauthor = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim lblPubDate As Label";
mostCurrent._lblpubdate = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lblPrice As Label";
mostCurrent._lblprice = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim lblBookClass As Label";
mostCurrent._lblbookclass = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim lblBookStore As Label";
mostCurrent._lblbookstore = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnMyLove As Button";
mostCurrent._btnmylove = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _imageview2_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 174;BA.debugLine="Sub ImageView2_Click";
 //BA.debugLineNum = 175;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 176;BA.debugLine="i.Initialize(i.ACTION_VIEW, DNS)";
_i.Initialize(_i.ACTION_VIEW,_dns);
 //BA.debugLineNum = 177;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 178;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 9;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 10;BA.debugLine="Dim DNS As String";
_dns = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim Check_MyloveBooks_Exist As Boolean=False   '檢查我的最愛書籍是否重複";
_check_mylovebooks_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _show_bookpicture() throws Exception{
String _bookid = "";
String _bookname = "";
String _author = "";
String _pubdate = "";
String _price = "";
String _bookclass = "";
String _bookstore = "";
int _i = 0;
 //BA.debugLineNum = 74;BA.debugLine="Sub Show_BookPicture  '顯示書籍的封面照片之副程式";
 //BA.debugLineNum = 75;BA.debugLine="Dim BookID As String";
_bookid = "";
 //BA.debugLineNum = 76;BA.debugLine="Dim BookName As String";
_bookname = "";
 //BA.debugLineNum = 77;BA.debugLine="Dim Author As String";
_author = "";
 //BA.debugLineNum = 78;BA.debugLine="Dim PubDate As String";
_pubdate = "";
 //BA.debugLineNum = 79;BA.debugLine="Dim Price As String";
_price = "";
 //BA.debugLineNum = 80;BA.debugLine="Dim BookClass As String";
_bookclass = "";
 //BA.debugLineNum = 81;BA.debugLine="Dim BookStore As String";
_bookstore = "";
 //BA.debugLineNum = 82;BA.debugLine="If UserAPP.ChangeQuery =1 Then";
if (mostCurrent._userapp._changequery==1) { 
 //BA.debugLineNum = 83;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='\" & BookTable.BookID_1 & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='"+mostCurrent._booktable._bookid_1+"' ";
 }else if(mostCurrent._userapp._changequery==2) { 
 //BA.debugLineNum = 85;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='\" & KeywordQuery.BookID_2 & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='"+mostCurrent._keywordquery._bookid_2+"' ";
 }else if(mostCurrent._userapp._changequery==3) { 
 //BA.debugLineNum = 87;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='\" & BookClassQuery.BookID_3 & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='"+mostCurrent._bookclassquery._bookid_3+"' ";
 }else if(mostCurrent._userapp._changequery==4) { 
 //BA.debugLineNum = 89;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='\" & MyloveBooks.BookID_4 & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,出版社名稱,分類名稱,官方網站 FROM 出版社資料表 As A ,書籍資料表 As B,書籍分類表 As C Where A.出版社編號=B.出版社編號 AND B.分類代號=C.分類代號 AND 書號='"+mostCurrent._mylovebooks._bookid_4+"' ";
 };
 //BA.debugLineNum = 91;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 92;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step80 = 1;
final int limit80 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step80 > 0 && _i <= limit80) || (step80 < 0 && _i >= limit80); _i = ((int)(0 + _i + step80))) {
 //BA.debugLineNum = 93;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 94;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 95;BA.debugLine="ImageView1.Bitmap = LoadBitmapSample(File.DirAssets, CursorDBPoint.GetString(\"書號\") & \".jpg\", 100, 100)";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg",(int) (100),(int) (100)).getObject()));
 }else {
 //BA.debugLineNum = 97;BA.debugLine="ImageView1.Bitmap = LoadBitmapSample(File.DirAssets, \"0000000.jpg\", 100, 100)";
mostCurrent._imageview1.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmapSample(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg",(int) (100),(int) (100)).getObject()));
 };
 //BA.debugLineNum = 99;BA.debugLine="BookID=CursorDBPoint.GetString(\"書號\")";
_bookid = _cursordbpoint.GetString("書號");
 //BA.debugLineNum = 100;BA.debugLine="BookName=CursorDBPoint.GetString(\"書名\")";
_bookname = _cursordbpoint.GetString("書名");
 //BA.debugLineNum = 101;BA.debugLine="Author=CursorDBPoint.GetString(\"作者\")";
_author = _cursordbpoint.GetString("作者");
 //BA.debugLineNum = 102;BA.debugLine="PubDate=CursorDBPoint.GetString(\"出版日期\")";
_pubdate = _cursordbpoint.GetString("出版日期");
 //BA.debugLineNum = 103;BA.debugLine="Price=CursorDBPoint.GetString(\"定價\")";
_price = _cursordbpoint.GetString("定價");
 //BA.debugLineNum = 104;BA.debugLine="BookStore=CursorDBPoint.GetString(\"出版社名稱\")";
_bookstore = _cursordbpoint.GetString("出版社名稱");
 //BA.debugLineNum = 105;BA.debugLine="BookClass=CursorDBPoint.GetString(\"分類名稱\")";
_bookclass = _cursordbpoint.GetString("分類名稱");
 //BA.debugLineNum = 106;BA.debugLine="DNS=CursorDBPoint.GetString(\"官方網站\")";
_dns = _cursordbpoint.GetString("官方網站");
 }
};
 //BA.debugLineNum = 108;BA.debugLine="lblBookID.Text=BookID";
mostCurrent._lblbookid.setText((Object)(_bookid));
 //BA.debugLineNum = 109;BA.debugLine="lblBookName.Text=BookName";
mostCurrent._lblbookname.setText((Object)(_bookname));
 //BA.debugLineNum = 110;BA.debugLine="lblAuthor.Text=Author";
mostCurrent._lblauthor.setText((Object)(_author));
 //BA.debugLineNum = 111;BA.debugLine="lblPubDate.Text=PubDate";
mostCurrent._lblpubdate.setText((Object)(_pubdate));
 //BA.debugLineNum = 112;BA.debugLine="lblPrice.Text=Price";
mostCurrent._lblprice.setText((Object)(_price));
 //BA.debugLineNum = 113;BA.debugLine="lblBookClass.Text=BookClass";
mostCurrent._lblbookclass.setText((Object)(_bookclass));
 //BA.debugLineNum = 114;BA.debugLine="lblBookStore.Text=BookStore";
mostCurrent._lblbookstore.setText((Object)(_bookstore));
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
}
