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

public class booksmanager extends Activity implements B4AActivity{
	public static booksmanager mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.booksmanager");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (booksmanager).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.booksmanager");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.booksmanager", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (booksmanager) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (booksmanager) Resume **");
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
		return booksmanager.class;
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
        BA.LogInfo("** Activity (booksmanager) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (booksmanager) Resume **");
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
public static boolean _check_bookid_exist = false;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookid = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtbookname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtauthor = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtpubdate = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtprice = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinbookstore = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinbookclass = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtdns = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btninsert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnupdate = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndelete = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselect = null;
public static long _now = 0L;
public static String _bookstoreid = "";
public static String _bookclassid = "";
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookid = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnselectbookname = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinquerybookname = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
public tw.idv.myebook.project01.myebook _myebook = null;
public tw.idv.myebook.project01.keywordquery _keywordquery = null;
public tw.idv.myebook.project01.bookclassquery _bookclassquery = null;
public tw.idv.myebook.project01.userapp _userapp = null;
public tw.idv.myebook.project01.managerapp _managerapp = null;
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
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 40;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 41;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 43;BA.debugLine="Activity.LoadLayout(\"BooksManager\")";
mostCurrent._activity.LoadLayout("BooksManager",mostCurrent.activityBA);
 //BA.debugLineNum = 44;BA.debugLine="Activity.Title = \"【書籍資料管理系統】\"";
mostCurrent._activity.setTitle((Object)("【書籍資料管理系統】"));
 //BA.debugLineNum = 45;BA.debugLine="SpinQueryBookID.Visible =False";
mostCurrent._spinquerybookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 46;BA.debugLine="SpinQueryBookName.Visible =False";
mostCurrent._spinquerybookname.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 47;BA.debugLine="edtBookID.Text = \"Book001\"";
mostCurrent._edtbookid.setText((Object)("Book001"));
 //BA.debugLineNum = 48;BA.debugLine="edtBookName.Text = \"開發Android APP使用VB輕鬆學\"";
mostCurrent._edtbookname.setText((Object)("開發Android APP使用VB輕鬆學"));
 //BA.debugLineNum = 49;BA.debugLine="edtAuthor.Text = \"李春雄\"";
mostCurrent._edtauthor.setText((Object)("李春雄"));
 //BA.debugLineNum = 50;BA.debugLine="edtPubDate.Text = DateTime.Date(now)";
mostCurrent._edtpubdate.setText((Object)(anywheresoftware.b4a.keywords.Common.DateTime.Date(_now)));
 //BA.debugLineNum = 51;BA.debugLine="edtPrice.Text = \"620\"";
mostCurrent._edtprice.setText((Object)("620"));
 //BA.debugLineNum = 52;BA.debugLine="CallShow_BookStoreDB(\"SELECT 出版社編號,出版社名稱 FROM 出版社資料表\")  '呼叫顯示「出版社名稱」之副程式";
_callshow_bookstoredb("SELECT 出版社編號,出版社名稱 FROM 出版社資料表");
 //BA.debugLineNum = 53;BA.debugLine="CallShow_BookClass(\"SELECT 分類代號,分類名稱 FROM 書籍分類表\")          '呼叫顯示「書籍分類」之副程式";
_callshow_bookclass("SELECT 分類代號,分類名稱 FROM 書籍分類表");
 //BA.debugLineNum = 54;BA.debugLine="edtDNS.Text = \"http://myebook.idv.tw\"";
mostCurrent._edtdns.setText((Object)("http://myebook.idv.tw"));
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 86;BA.debugLine="Sub Activity_Pause(UserClosed As Boolean)";
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 82;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _btndelete_click() throws Exception{
String _bookid = "";
String _strsql = "";
 //BA.debugLineNum = 138;BA.debugLine="Sub btnDelete_Click    '刪除書籍資料";
 //BA.debugLineNum = 139;BA.debugLine="Dim BookID As String =edtBookID.Text";
_bookid = mostCurrent._edtbookid.getText();
 //BA.debugLineNum = 140;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 141;BA.debugLine="strSQL = \"DELETE FROM 書籍資料表 WHERE 書號 = '\" & BookID & \"'\"";
_strsql = "DELETE FROM 書籍資料表 WHERE 書號 = '"+_bookid+"'";
 //BA.debugLineNum = 142;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 143;BA.debugLine="ToastMessageShow(\"刪除一筆書籍記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("刪除一筆書籍記錄...",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 144;BA.debugLine="End Sub";
return "";
}
public static String  _btninsert_click() throws Exception{
String _bookid = "";
String _bookname = "";
String _author = "";
String _pubdate = "";
String _price = "";
String _dns = "";
String _strsql = "";
int _dbchange = 0;
 //BA.debugLineNum = 90;BA.debugLine="Sub btnInsert_Click   '新增書籍資料";
 //BA.debugLineNum = 91;BA.debugLine="Dim BookID As String =edtBookID.Text";
_bookid = mostCurrent._edtbookid.getText();
 //BA.debugLineNum = 92;BA.debugLine="Dim BookName As String = edtBookName.Text";
_bookname = mostCurrent._edtbookname.getText();
 //BA.debugLineNum = 93;BA.debugLine="Dim Author As String= edtAuthor.Text";
_author = mostCurrent._edtauthor.getText();
 //BA.debugLineNum = 94;BA.debugLine="Dim PubDate As String= edtPubDate.Text";
_pubdate = mostCurrent._edtpubdate.getText();
 //BA.debugLineNum = 95;BA.debugLine="Dim Price As String= edtPrice.Text";
_price = mostCurrent._edtprice.getText();
 //BA.debugLineNum = 96;BA.debugLine="Dim DNS As String= edtDNS.Text";
_dns = mostCurrent._edtdns.getText();
 //BA.debugLineNum = 97;BA.debugLine="If BookStoreID=\"\" OR BookClassID=\"\" Then";
if ((mostCurrent._bookstoreid).equals("") || (mostCurrent._bookclassid).equals("")) { 
 //BA.debugLineNum = 98;BA.debugLine="Msgbox(\"您尚未選擇「出版社」或「書籍類別」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未選擇「出版社」或「書籍類別」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 100;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 101;BA.debugLine="strSQL = \"INSERT INTO 書籍資料表(書號,書名,作者,出版日期,定價,出版社編號,分類代號,官方網站)\" & _              \"VALUES('\" & BookID & \"','\" & BookName & \"','\" & Author & \"','\" & PubDate & \"','\" & Price & \"','\" & BookStoreID & \"','\" & BookClassID & \"','\" & DNS & \"')\"";
_strsql = "INSERT INTO 書籍資料表(書號,書名,作者,出版日期,定價,出版社編號,分類代號,官方網站)"+"VALUES('"+_bookid+"','"+_bookname+"','"+_author+"','"+_pubdate+"','"+_price+"','"+mostCurrent._bookstoreid+"','"+mostCurrent._bookclassid+"','"+_dns+"')";
 //BA.debugLineNum = 103;BA.debugLine="Check_BookID_Exists(BookID)";
_check_bookid_exists(_bookid);
 //BA.debugLineNum = 104;BA.debugLine="If Check_BookID_Exist=True Then";
if (_check_bookid_exist==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 105;BA.debugLine="Msgbox(\"您已經重複新增這本書了！\",\"新增錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您已經重複新增這本書了！","新增錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 107;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 108;BA.debugLine="Dim dbChange As Int";
_dbchange = 0;
 //BA.debugLineNum = 109;BA.debugLine="dbChange = SQLCmd.ExecQuerySingleResult(\"SELECT changes() FROM 書籍資料表\")";
_dbchange = (int)(Double.parseDouble(_sqlcmd.ExecQuerySingleResult("SELECT changes() FROM 書籍資料表")));
 //BA.debugLineNum = 110;BA.debugLine="ToastMessageShow(\"新增書籍記錄: \" & dbChange & \" 筆\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("新增書籍記錄: "+BA.NumberToString(_dbchange)+" 筆",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 112;BA.debugLine="Check_BookID_Exist=False";
_check_bookid_exist = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 202;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 203;BA.debugLine="Activity.Finish()  '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 204;BA.debugLine="StartActivity(\"ManagerAPP\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("ManagerAPP"));
 //BA.debugLineNum = 205;BA.debugLine="End Sub";
return "";
}
public static String  _btnselect_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 145;BA.debugLine="Sub btnSelect_Click";
 //BA.debugLineNum = 146;BA.debugLine="If SpinQueryBookName.Visible =True Then";
if (mostCurrent._spinquerybookname.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 147;BA.debugLine="SpinQueryBookName.Visible =False";
mostCurrent._spinquerybookname.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 148;BA.debugLine="SpinQueryBookID.Visible =True";
mostCurrent._spinquerybookid.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 150;BA.debugLine="SpinQueryBookID.Visible =True";
mostCurrent._spinquerybookid.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 152;BA.debugLine="SpinQueryBookID.Clear";
mostCurrent._spinquerybookid.Clear();
 //BA.debugLineNum = 153;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 書號 FROM 書籍資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 書號 FROM 書籍資料表")));
 //BA.debugLineNum = 154;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step129 = 1;
final int limit129 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step129 > 0 && _i <= limit129) || (step129 < 0 && _i >= limit129); _i = ((int)(0 + _i + step129))) {
 //BA.debugLineNum = 155;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 156;BA.debugLine="SpinQueryBookID.Add(CursorDBPoint.GetString(\"書號\"))";
mostCurrent._spinquerybookid.Add(_cursordbpoint.GetString("書號"));
 }
};
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
public static String  _btnselectbookname_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 179;BA.debugLine="Sub btnSelectBookName_Click";
 //BA.debugLineNum = 180;BA.debugLine="If SpinQueryBookID.Visible =True Then";
if (mostCurrent._spinquerybookid.getVisible()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 181;BA.debugLine="SpinQueryBookID.Visible =False";
mostCurrent._spinquerybookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 182;BA.debugLine="SpinQueryBookName.Visible =True";
mostCurrent._spinquerybookname.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 184;BA.debugLine="SpinQueryBookName.Visible =True";
mostCurrent._spinquerybookname.setVisible(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 186;BA.debugLine="SpinQueryBookName.Clear";
mostCurrent._spinquerybookname.Clear();
 //BA.debugLineNum = 187;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(\"SELECT 書名 FROM 書籍資料表\")";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery("SELECT 書名 FROM 書籍資料表")));
 //BA.debugLineNum = 188;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step160 = 1;
final int limit160 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step160 > 0 && _i <= limit160) || (step160 < 0 && _i >= limit160); _i = ((int)(0 + _i + step160))) {
 //BA.debugLineNum = 189;BA.debugLine="CursorDBPoint.Position = i     '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 190;BA.debugLine="SpinQueryBookName.Add(CursorDBPoint.GetString(\"書名\"))";
mostCurrent._spinquerybookname.Add(_cursordbpoint.GetString("書名"));
 }
};
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _btnupdate_click() throws Exception{
String _bookid = "";
String _strsql = "";
 //BA.debugLineNum = 126;BA.debugLine="Sub btnUpdate_Click    '修改書籍資料";
 //BA.debugLineNum = 127;BA.debugLine="Dim BookID As String =edtBookID.Text";
_bookid = mostCurrent._edtbookid.getText();
 //BA.debugLineNum = 128;BA.debugLine="Dim strSQL As String";
_strsql = "";
 //BA.debugLineNum = 129;BA.debugLine="If BookStoreID=\"\" OR BookClassID=\"\" Then";
if ((mostCurrent._bookstoreid).equals("") || (mostCurrent._bookclassid).equals("")) { 
 //BA.debugLineNum = 130;BA.debugLine="Msgbox(\"您尚未選擇「出版社」或「書籍類別」哦！\",\"錯誤訊息回報!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未選擇「出版社」或「書籍類別」哦！","錯誤訊息回報!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 132;BA.debugLine="strSQL = \"UPDATE 書籍資料表 SET 書名 ='\" & edtBookName.Text & \"',作者 ='\" & edtAuthor.Text & \"',出版日期 ='\" & edtPubDate.Text & \"',定價 ='\" & edtPrice.Text & \"',出版社編號 ='\" & BookStoreID & \"',分類代號 ='\" & BookClassID & \"',官方網站 ='\" & edtDNS.Text & \"' WHERE 書號 = '\" & BookID & \"'\"";
_strsql = "UPDATE 書籍資料表 SET 書名 ='"+mostCurrent._edtbookname.getText()+"',作者 ='"+mostCurrent._edtauthor.getText()+"',出版日期 ='"+mostCurrent._edtpubdate.getText()+"',定價 ='"+mostCurrent._edtprice.getText()+"',出版社編號 ='"+mostCurrent._bookstoreid+"',分類代號 ='"+mostCurrent._bookclassid+"',官方網站 ='"+mostCurrent._edtdns.getText()+"' WHERE 書號 = '"+_bookid+"'";
 //BA.debugLineNum = 134;BA.debugLine="SQLCmd.ExecNonQuery(strSQL)";
_sqlcmd.ExecNonQuery(_strsql);
 //BA.debugLineNum = 135;BA.debugLine="ToastMessageShow(\"更新一筆書籍記錄...\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("更新一筆書籍記錄...",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 137;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookclass(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 70;BA.debugLine="Sub CallShow_BookClass(strSQL As String)    '顯示「書籍分類」之副程式";
 //BA.debugLineNum = 71;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 72;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step56 = 1;
final int limit56 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step56 > 0 && _i <= limit56) || (step56 < 0 && _i >= limit56); _i = ((int)(0 + _i + step56))) {
 //BA.debugLineNum = 73;BA.debugLine="CursorDBPoint.Position = i           '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 74;BA.debugLine="SpinBookClass.Add(CursorDBPoint.GetString(\"分類代號\") & \"/\" & CursorDBPoint.GetString(\"分類名稱\"))";
mostCurrent._spinbookclass.Add(_cursordbpoint.GetString("分類代號")+"/"+_cursordbpoint.GetString("分類名稱"));
 }
};
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookdata(String _strsql) throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub CallShow_BookData(strSQL As String)  '顯示「出版社名稱」之副程式";
 //BA.debugLineNum = 169;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 170;BA.debugLine="CursorDBPoint.Position = 0";
_cursordbpoint.setPosition((int) (0));
 //BA.debugLineNum = 171;BA.debugLine="edtBookID.Text = CursorDBPoint.GetString(\"書號\")";
mostCurrent._edtbookid.setText((Object)(_cursordbpoint.GetString("書號")));
 //BA.debugLineNum = 172;BA.debugLine="edtBookName.Text=CursorDBPoint.GetString(\"書名\")";
mostCurrent._edtbookname.setText((Object)(_cursordbpoint.GetString("書名")));
 //BA.debugLineNum = 173;BA.debugLine="edtAuthor.Text = CursorDBPoint.GetString(\"作者\")";
mostCurrent._edtauthor.setText((Object)(_cursordbpoint.GetString("作者")));
 //BA.debugLineNum = 174;BA.debugLine="edtPubDate.Text = CursorDBPoint.GetString(\"出版日期\")";
mostCurrent._edtpubdate.setText((Object)(_cursordbpoint.GetString("出版日期")));
 //BA.debugLineNum = 175;BA.debugLine="edtPrice.Text = CursorDBPoint.GetString(\"定價\")";
mostCurrent._edtprice.setText((Object)(_cursordbpoint.GetString("定價")));
 //BA.debugLineNum = 176;BA.debugLine="edtDNS.Text = CursorDBPoint.GetString(\"官方網站\")";
mostCurrent._edtdns.setText((Object)(_cursordbpoint.GetString("官方網站")));
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return "";
}
public static String  _callshow_bookstoredb(String _strsql) throws Exception{
int _i = 0;
 //BA.debugLineNum = 57;BA.debugLine="Sub CallShow_BookStoreDB(strSQL As String)  '顯示「出版社名稱」之副程式";
 //BA.debugLineNum = 58;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(strSQL)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_strsql)));
 //BA.debugLineNum = 59;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step46 = 1;
final int limit46 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step46 > 0 && _i <= limit46) || (step46 < 0 && _i >= limit46); _i = ((int)(0 + _i + step46))) {
 //BA.debugLineNum = 60;BA.debugLine="CursorDBPoint.Position = i         '設定開始游標指定第一筆記錄";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 61;BA.debugLine="SpinBookStore.Add(CursorDBPoint.GetString(\"出版社編號\") & \"/\" & CursorDBPoint.GetString(\"出版社名稱\"))";
mostCurrent._spinbookstore.Add(_cursordbpoint.GetString("出版社編號")+"/"+_cursordbpoint.GetString("出版社名稱"));
 }
};
 //BA.debugLineNum = 63;BA.debugLine="End Sub";
return "";
}
public static String  _check_bookid_exists(String _strbookid) throws Exception{
String _sql_query = "";
int _i = 0;
 //BA.debugLineNum = 115;BA.debugLine="Sub Check_BookID_Exists(strBookID As String)";
 //BA.debugLineNum = 116;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 117;BA.debugLine="SQL_Query=\"Select * FROM 書籍資料表 Where 書號='\" & strBookID & \"' \"";
_sql_query = "Select * FROM 書籍資料表 Where 書號='"+_strbookid+"' ";
 //BA.debugLineNum = 118;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 119;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step96 = 1;
final int limit96 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step96 > 0 && _i <= limit96) || (step96 < 0 && _i >= limit96); _i = ((int)(0 + _i + step96))) {
 //BA.debugLineNum = 120;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 121;BA.debugLine="Check_BookID_Exist=True";
_check_bookid_exist = anywheresoftware.b4a.keywords.Common.True;
 };
 }
};
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim edtBookID As EditText     '書號";
mostCurrent._edtbookid = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 16;BA.debugLine="Dim edtBookName As EditText   '書名";
mostCurrent._edtbookname = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim edtAuthor As EditText     '作者";
mostCurrent._edtauthor = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Dim edtPubDate As EditText    '出版日期";
mostCurrent._edtpubdate = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Dim edtPrice As EditText      '定價";
mostCurrent._edtprice = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim SpinBookStore As Spinner  '出版社名稱";
mostCurrent._spinbookstore = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim SpinBookClass As Spinner  '書籍分類名稱";
mostCurrent._spinbookclass = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim edtDNS As EditText        '書籍的官方網站";
mostCurrent._edtdns = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim btnInsert As Button       '新增功能";
mostCurrent._btninsert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim btnUpdate As Button       '修改功能";
mostCurrent._btnupdate = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnDelete As Button       '刪除功能";
mostCurrent._btndelete = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim btnSelect As Button       '查詢功能";
mostCurrent._btnselect = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 29;BA.debugLine="now=DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 31;BA.debugLine="Dim BookStoreID As String '出版社編號";
mostCurrent._bookstoreid = "";
 //BA.debugLineNum = 32;BA.debugLine="Dim BookClassID As String '書籍分類代號";
mostCurrent._bookclassid = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim SpinQueryBookID As Spinner  '查詢書籍代號";
mostCurrent._spinquerybookid = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim btnSelectBookName As Button";
mostCurrent._btnselectbookname = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Dim SpinQueryBookName As Spinner";
mostCurrent._spinquerybookname = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Dim SQLCmd As SQL";
_sqlcmd = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 8;BA.debugLine="Dim CursorDBPoint As Cursor";
_cursordbpoint = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 9;BA.debugLine="Dim Check_BookID_Exist As Boolean=False   '檢查新增書籍時的書號是否重複";
_check_bookid_exist = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _spinbookclass_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub SpinBookClass_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 79;BA.debugLine="BookClassID=SpinBookClass.SelectedItem.SubString2(0,5)";
mostCurrent._bookclassid = mostCurrent._spinbookclass.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 80;BA.debugLine="End Sub";
return "";
}
public static String  _spinbookstore_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 65;BA.debugLine="Sub SpinBookStore_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 66;BA.debugLine="BookStoreID=SpinBookStore.SelectedItem.SubString2(0,5)";
mostCurrent._bookstoreid = mostCurrent._spinbookstore.getSelectedItem().substring((int) (0),(int) (5));
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookid_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookid = "";
 //BA.debugLineNum = 160;BA.debugLine="Sub SpinQueryBookID_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 161;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 162;BA.debugLine="Dim QueryBookID As String =SpinQueryBookID.SelectedItem";
_querybookid = mostCurrent._spinquerybookid.getSelectedItem();
 //BA.debugLineNum = 163;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書號='\" & QueryBookID & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書號='"+_querybookid+"' ";
 //BA.debugLineNum = 164;BA.debugLine="CallShow_BookData(SQL_Query)";
_callshow_bookdata(_sql_query);
 //BA.debugLineNum = 165;BA.debugLine="SpinQueryBookID.Visible =False";
mostCurrent._spinquerybookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 166;BA.debugLine="End Sub";
return "";
}
public static String  _spinquerybookname_itemclick(int _position,Object _value) throws Exception{
String _sql_query = "";
String _querybookname = "";
 //BA.debugLineNum = 193;BA.debugLine="Sub SpinQueryBookName_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 194;BA.debugLine="Dim SQL_Query As String";
_sql_query = "";
 //BA.debugLineNum = 195;BA.debugLine="Dim QueryBookName As String =SpinQueryBookName.SelectedItem";
_querybookname = mostCurrent._spinquerybookname.getSelectedItem();
 //BA.debugLineNum = 196;BA.debugLine="SQL_Query=\"Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書名='\" & QueryBookName & \"' \"";
_sql_query = "Select 書號,書名,作者,出版日期,定價,B.出版社編號,出版社名稱,B.分類代號,分類名稱,官方網站 FROM 出版社資料表 as A,書籍資料表 as B,書籍分類表 as C Where A.出版社編號=B.出版社編號 And B.分類代號=C.分類代號 And 書名='"+_querybookname+"' ";
 //BA.debugLineNum = 198;BA.debugLine="CallShow_BookData(SQL_Query)";
_callshow_bookdata(_sql_query);
 //BA.debugLineNum = 199;BA.debugLine="SpinQueryBookName.Visible =False";
mostCurrent._spinquerybookname.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 200;BA.debugLine="End Sub";
return "";
}
}
