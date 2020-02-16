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

public class keywordquery extends Activity implements B4AActivity{
	public static keywordquery mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "tw.idv.myebook.project01", "tw.idv.myebook.project01.keywordquery");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (keywordquery).");
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
		activityBA = new BA(this, layout, processBA, "tw.idv.myebook.project01", "tw.idv.myebook.project01.keywordquery");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "tw.idv.myebook.project01.keywordquery", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (keywordquery) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (keywordquery) Resume **");
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
		return keywordquery.class;
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
        BA.LogInfo("** Activity (keywordquery) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (keywordquery) Resume **");
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
public static String _bookid_2 = "";
public static String _bookname_2 = "";
public static String _query_index = "";
public anywheresoftware.b4a.objects.TabHostWrapper _tabhost1 = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livbooklist = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnkeyword = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtkeyword = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _picbook = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spnqueryclass = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtqueryclass = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnclassquery = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtqueryclass_bookid = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtqueryclass_author = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtqueryclass_pubdate = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edtqueryclass_bookstore = null;
public anywheresoftware.b4a.objects.ListViewWrapper _livquery_class = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnreturn = null;
public tw.idv.myebook.project01.main _main = null;
public tw.idv.myebook.project01.booktable _booktable = null;
public tw.idv.myebook.project01.myebook _myebook = null;
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
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="If SQLCmd.IsInitialized() = False Then";
if (_sqlcmd.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 38;BA.debugLine="SQLCmd.Initialize(File.DirDefaultExternal, \"MyebookDBS.sqlite\", False)";
_sqlcmd.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirDefaultExternal(),"MyebookDBS.sqlite",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 40;BA.debugLine="Activity.LoadLayout(\"KeywordQuery\")";
mostCurrent._activity.LoadLayout("KeywordQuery",mostCurrent.activityBA);
 //BA.debugLineNum = 41;BA.debugLine="Activity.Title =\"歡迎使用「關鍵字查詢系統」\"";
mostCurrent._activity.setTitle((Object)("歡迎使用「關鍵字查詢系統」"));
 //BA.debugLineNum = 42;BA.debugLine="TabHost1.AddTab(\"查詢關鍵字\",\"Query_keyword\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"查詢關鍵字","Query_keyword");
 //BA.debugLineNum = 43;BA.debugLine="TabHost1.AddTab(\"查詢各類別\",\"Query_Class\")";
mostCurrent._tabhost1.AddTab(mostCurrent.activityBA,"查詢各類別","Query_Class");
 //BA.debugLineNum = 44;BA.debugLine="edtQueryClass_BookID.Visible=True";
mostCurrent._edtqueryclass_bookid.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 45;BA.debugLine="edtQueryClass_Author.Visible=False";
mostCurrent._edtqueryclass_author.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 46;BA.debugLine="edtQueryClass_PubDate.Visible=False";
mostCurrent._edtqueryclass_pubdate.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 47;BA.debugLine="edtQueryClass_BookStore.Visible=False";
mostCurrent._edtqueryclass_bookstore.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 49;BA.debugLine="spnQueryClass.Add(\"書號\")";
mostCurrent._spnqueryclass.Add("書號");
 //BA.debugLineNum = 50;BA.debugLine="spnQueryClass.Add(\"作者\")";
mostCurrent._spnqueryclass.Add("作者");
 //BA.debugLineNum = 51;BA.debugLine="spnQueryClass.Add(\"出版日期\")";
mostCurrent._spnqueryclass.Add("出版日期");
 //BA.debugLineNum = 52;BA.debugLine="spnQueryClass.Add(\"出版社名稱\")";
mostCurrent._spnqueryclass.Add("出版社名稱");
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _btnclassquery_click() throws Exception{
String _keyword = "";
int _i = 0;
String _temp = "";
 //BA.debugLineNum = 133;BA.debugLine="Sub btnClassQuery_Click";
 //BA.debugLineNum = 134;BA.debugLine="livQuery_Class.Clear()";
mostCurrent._livquery_class.Clear();
 //BA.debugLineNum = 135;BA.debugLine="If edtQueryClass_BookID.text=\"\" AND edtQueryClass_Author.text=\"\" AND edtQueryClass_PubDate.text=\"\" AND edtQueryClass_BookStore.text=\"\" Then";
if ((mostCurrent._edtqueryclass_bookid.getText()).equals("") && (mostCurrent._edtqueryclass_author.getText()).equals("") && (mostCurrent._edtqueryclass_pubdate.getText()).equals("") && (mostCurrent._edtqueryclass_bookstore.getText()).equals("")) { 
 //BA.debugLineNum = 136;BA.debugLine="Msgbox(\"您尚未輸入關鍵字哦!\",\"錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入關鍵字哦!","錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 138;BA.debugLine="If Query_Index=1 Then";
if ((_query_index).equals(BA.NumberToString(1))) { 
 //BA.debugLineNum = 139;BA.debugLine="Dim Keyword As String =edtQueryClass_BookID.text";
_keyword = mostCurrent._edtqueryclass_bookid.getText();
 //BA.debugLineNum = 140;BA.debugLine="SQL_Query=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書號='\" & Keyword & \"' \"";
_sql_query = "Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書號='"+_keyword+"' ";
 }else if((_query_index).equals(BA.NumberToString(2))) { 
 //BA.debugLineNum = 142;BA.debugLine="Dim Keyword As String =edtQueryClass_Author.text";
_keyword = mostCurrent._edtqueryclass_author.getText();
 //BA.debugLineNum = 143;BA.debugLine="SQL_Query=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 作者 Like '%\" & Keyword & \"%' \"";
_sql_query = "Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 作者 Like '%"+_keyword+"%' ";
 }else if((_query_index).equals(BA.NumberToString(3))) { 
 //BA.debugLineNum = 145;BA.debugLine="Dim Keyword As String =edtQueryClass_PubDate.text";
_keyword = mostCurrent._edtqueryclass_pubdate.getText();
 //BA.debugLineNum = 146;BA.debugLine="SQL_Query=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版日期 Like '%\" & Keyword & \"%' \"";
_sql_query = "Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版日期 Like '%"+_keyword+"%' ";
 }else if((_query_index).equals(BA.NumberToString(4))) { 
 //BA.debugLineNum = 148;BA.debugLine="Dim Keyword As String =edtQueryClass_BookStore.text";
_keyword = mostCurrent._edtqueryclass_bookstore.getText();
 //BA.debugLineNum = 149;BA.debugLine="SQL_Query=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版社名稱 Like '%\" & Keyword & \"%' \"";
_sql_query = "Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 出版社名稱 Like '%"+_keyword+"%' ";
 };
 //BA.debugLineNum = 152;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 153;BA.debugLine="livQuery_Class.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livquery_class.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 154;BA.debugLine="livQuery_Class.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livquery_class.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 155;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step136 = 1;
final int limit136 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step136 > 0 && _i <= limit136) || (step136 < 0 && _i >= limit136); _i = ((int)(0 + _i + step136))) {
 //BA.debugLineNum = 156;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 157;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 158;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
mostCurrent._picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 160;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
mostCurrent._picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 162;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=10 Then";
if (_cursordbpoint.GetString("書名").length()>=10) { 
 //BA.debugLineNum = 163;BA.debugLine="livQuery_Class.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,10) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_class.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (10))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(mostCurrent._picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 165;BA.debugLine="livQuery_Class.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livquery_class.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(mostCurrent._picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 168;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 169;BA.debugLine="Temp=\"找不到關鍵字「\" & Keyword & \"」!\" & CRLF & \"請重新查詢!!!\"";
_temp = "找不到關鍵字「"+_keyword+"」!"+anywheresoftware.b4a.keywords.Common.CRLF+"請重新查詢!!!";
 //BA.debugLineNum = 171;BA.debugLine="edtQueryClass_BookID.text=\"\"";
mostCurrent._edtqueryclass_bookid.setText((Object)(""));
 //BA.debugLineNum = 172;BA.debugLine="edtQueryClass_Author.text=\"\"";
mostCurrent._edtqueryclass_author.setText((Object)(""));
 //BA.debugLineNum = 173;BA.debugLine="edtQueryClass_PubDate.text=\"\"";
mostCurrent._edtqueryclass_pubdate.setText((Object)(""));
 //BA.debugLineNum = 174;BA.debugLine="edtQueryClass_BookStore.text=\"\"";
mostCurrent._edtqueryclass_bookstore.setText((Object)(""));
 };
 };
 //BA.debugLineNum = 177;BA.debugLine="End Sub";
return "";
}
public static String  _btnkeyword_click() throws Exception{
String _temp = "";
String _keyword = "";
int _i = 0;
 //BA.debugLineNum = 54;BA.debugLine="Sub btnKeyword_Click";
 //BA.debugLineNum = 55;BA.debugLine="livBookList.Clear()";
mostCurrent._livbooklist.Clear();
 //BA.debugLineNum = 56;BA.debugLine="Dim Temp As String";
_temp = "";
 //BA.debugLineNum = 57;BA.debugLine="Dim Keyword As String =edtKeyword.text";
_keyword = mostCurrent._edtkeyword.getText();
 //BA.debugLineNum = 58;BA.debugLine="If Keyword=\"\" Then";
if ((_keyword).equals("")) { 
 //BA.debugLineNum = 59;BA.debugLine="Msgbox(\"您尚未輸入關鍵字哦!\",\"錯誤訊息!!!\")";
anywheresoftware.b4a.keywords.Common.Msgbox("您尚未輸入關鍵字哦!","錯誤訊息!!!",mostCurrent.activityBA);
 }else {
 //BA.debugLineNum = 61;BA.debugLine="SQL_Query=\"Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書名 Like '%\" & Keyword & \"%' \"";
_sql_query = "Select 書號,書名,出版社名稱 FROM 書籍資料表 as A,出版社資料表 as B Where A.出版社編號=B.出版社編號 And 書名 Like '%"+_keyword+"%' ";
 //BA.debugLineNum = 62;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 63;BA.debugLine="livBookList.TwoLinesLayout.Label.TextSize = 10";
mostCurrent._livbooklist.getTwoLinesLayout().Label.setTextSize((float) (10));
 //BA.debugLineNum = 64;BA.debugLine="livBookList.TwoLinesLayout.ItemHeight=30dip";
mostCurrent._livbooklist.getTwoLinesLayout().setItemHeight(anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (30)));
 //BA.debugLineNum = 65;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step53 = 1;
final int limit53 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step53 > 0 && _i <= limit53) || (step53 < 0 && _i >= limit53); _i = ((int)(0 + _i + step53))) {
 //BA.debugLineNum = 66;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 67;BA.debugLine="If File.Exists(File.DirAssets ,CursorDBPoint.GetString(\"書號\") & \".jpg\") Then";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg")) { 
 //BA.debugLineNum = 68;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,CursorDBPoint.GetString(\"書號\") & \".jpg\")";
mostCurrent._picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),_cursordbpoint.GetString("書號")+".jpg");
 }else {
 //BA.debugLineNum = 70;BA.debugLine="PicBook=LoadBitmap(File.DirAssets,\"0000000.jpg\")";
mostCurrent._picbook = anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"0000000.jpg");
 };
 //BA.debugLineNum = 72;BA.debugLine="If CursorDBPoint.GetString(\"書名\").Length >=10 Then";
if (_cursordbpoint.GetString("書名").length()>=10) { 
 //BA.debugLineNum = 73;BA.debugLine="livBookList.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\").SubString2(0,10) & \"...\",CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livbooklist.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名").substring((int) (0),(int) (10))+"...",_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(mostCurrent._picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 }else {
 //BA.debugLineNum = 75;BA.debugLine="livBookList.AddTwoLinesAndBitmap2((i+1) & \".\" & CursorDBPoint.GetString(\"書名\"),CursorDBPoint.GetString(\"出版社名稱\"),PicBook,CursorDBPoint.GetString(\"書號\"))";
mostCurrent._livbooklist.AddTwoLinesAndBitmap2(BA.NumberToString((_i+1))+"."+_cursordbpoint.GetString("書名"),_cursordbpoint.GetString("出版社名稱"),(android.graphics.Bitmap)(mostCurrent._picbook.getObject()),(Object)(_cursordbpoint.GetString("書號")));
 };
 }
};
 //BA.debugLineNum = 78;BA.debugLine="If i=0 Then";
if (_i==0) { 
 //BA.debugLineNum = 79;BA.debugLine="Temp=\"找不到關鍵字「\" & Keyword & \"」!\" & CRLF & \"請重新查詢!!!\"";
_temp = "找不到關鍵字「"+_keyword+"」!"+anywheresoftware.b4a.keywords.Common.CRLF+"請重新查詢!!!";
 //BA.debugLineNum = 80;BA.debugLine="Msgbox(Temp,\"查詢結果\")";
anywheresoftware.b4a.keywords.Common.Msgbox(_temp,"查詢結果",mostCurrent.activityBA);
 //BA.debugLineNum = 81;BA.debugLine="edtKeyword.text=\"\"";
mostCurrent._edtkeyword.setText((Object)(""));
 };
 };
 //BA.debugLineNum = 84;BA.debugLine="End Sub";
return "";
}
public static String  _btnreturn_click() throws Exception{
 //BA.debugLineNum = 189;BA.debugLine="Sub btnReturn_Click";
 //BA.debugLineNum = 190;BA.debugLine="Activity.Finish()         '活動完成";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 191;BA.debugLine="StartActivity(UserAPP)  ' 返回UserAPP主活動";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._userapp.getObject()));
 //BA.debugLineNum = 192;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 19;BA.debugLine="Dim TabHost1 As TabHost";
mostCurrent._tabhost1 = new anywheresoftware.b4a.objects.TabHostWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Dim livBookList As ListView";
mostCurrent._livbooklist = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Dim btnKeyword As Button";
mostCurrent._btnkeyword = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim edtKeyword As EditText";
mostCurrent._edtkeyword = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Dim PicBook As Bitmap";
mostCurrent._picbook = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim spnQueryClass As Spinner";
mostCurrent._spnqueryclass = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim edtQueryClass As EditText";
mostCurrent._edtqueryclass = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim btnClassQuery As Button";
mostCurrent._btnclassquery = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim edtQueryClass_BookID As EditText";
mostCurrent._edtqueryclass_bookid = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim edtQueryClass_Author As EditText";
mostCurrent._edtqueryclass_author = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Dim edtQueryClass_PubDate As EditText";
mostCurrent._edtqueryclass_pubdate = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim edtQueryClass_BookStore As EditText";
mostCurrent._edtqueryclass_bookstore = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim livQuery_Class As ListView";
mostCurrent._livquery_class = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Query_Index=1";
_query_index = BA.NumberToString(1);
 //BA.debugLineNum = 33;BA.debugLine="Dim btnReturn As Button";
mostCurrent._btnreturn = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}
public static String  _livbooklist_itemclick(int _position,Object _value) throws Exception{
int _i = 0;
 //BA.debugLineNum = 86;BA.debugLine="Sub livBookList_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 87;BA.debugLine="BookID_2 = Value    '書籍編號";
_bookid_2 = BA.ObjectToString(_value);
 //BA.debugLineNum = 88;BA.debugLine="SQL_Query=\"Select 書名 FROM 書籍資料表 Where 書號='\" & BookID_2 & \"' \"";
_sql_query = "Select 書名 FROM 書籍資料表 Where 書號='"+_bookid_2+"' ";
 //BA.debugLineNum = 90;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 91;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step77 = 1;
final int limit77 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step77 > 0 && _i <= limit77) || (step77 < 0 && _i >= limit77); _i = ((int)(0 + _i + step77))) {
 //BA.debugLineNum = 92;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 93;BA.debugLine="BookName_2=CursorDBPoint.GetString(\"書名\")";
_bookname_2 = _cursordbpoint.GetString("書名");
 }
};
 //BA.debugLineNum = 95;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
public static String  _livquery_class_itemclick(int _position,Object _value) throws Exception{
int _i = 0;
 //BA.debugLineNum = 178;BA.debugLine="Sub livQuery_Class_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 179;BA.debugLine="BookID_2 = Value    '書籍編號";
_bookid_2 = BA.ObjectToString(_value);
 //BA.debugLineNum = 180;BA.debugLine="SQL_Query=\"Select 書名 FROM 書籍資料表 Where 書號='\" & BookID_2 & \"' \"";
_sql_query = "Select 書名 FROM 書籍資料表 Where 書號='"+_bookid_2+"' ";
 //BA.debugLineNum = 182;BA.debugLine="CursorDBPoint = SQLCmd.ExecQuery(SQL_Query)";
_cursordbpoint.setObject((android.database.Cursor)(_sqlcmd.ExecQuery(_sql_query)));
 //BA.debugLineNum = 183;BA.debugLine="For i = 0 To CursorDBPoint.RowCount - 1";
{
final int step162 = 1;
final int limit162 = (int) (_cursordbpoint.getRowCount()-1);
for (_i = (int) (0); (step162 > 0 && _i <= limit162) || (step162 < 0 && _i >= limit162); _i = ((int)(0 + _i + step162))) {
 //BA.debugLineNum = 184;BA.debugLine="CursorDBPoint.Position = i";
_cursordbpoint.setPosition(_i);
 //BA.debugLineNum = 185;BA.debugLine="BookName_2=CursorDBPoint.GetString(\"書名\")";
_bookname_2 = _cursordbpoint.GetString("書名");
 }
};
 //BA.debugLineNum = 187;BA.debugLine="StartActivity(\"Myebook\")";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Myebook"));
 //BA.debugLineNum = 188;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 10;BA.debugLine="Dim BookID_2 As String     '書籍編號";
_bookid_2 = "";
 //BA.debugLineNum = 11;BA.debugLine="Dim BookName_2 As String   '書名";
_bookname_2 = "";
 //BA.debugLineNum = 12;BA.debugLine="Dim Query_Index As String  '依照查詢類別 1代表書號  2代表作者  3代表出版日期  4代表出版社";
_query_index = "";
 //BA.debugLineNum = 13;BA.debugLine="End Sub";
return "";
}
public static String  _spnqueryclass_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 105;BA.debugLine="Sub spnQueryClass_ItemClick (Position As Int, Value As Object)";
 //BA.debugLineNum = 106;BA.debugLine="Select Value      '取得出版社名稱";
switch (BA.switchObjectToInt(_value,(Object)("書號"),(Object)("作者"),(Object)("出版日期"),(Object)("出版社名稱"))) {
case 0:
 //BA.debugLineNum = 108;BA.debugLine="edtQueryClass_BookID.Visible=True";
mostCurrent._edtqueryclass_bookid.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="edtQueryClass_Author.Visible=False";
mostCurrent._edtqueryclass_author.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 110;BA.debugLine="edtQueryClass_PubDate.Visible=False";
mostCurrent._edtqueryclass_pubdate.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 111;BA.debugLine="edtQueryClass_BookStore.Visible=False";
mostCurrent._edtqueryclass_bookstore.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 112;BA.debugLine="Query_Index=1";
_query_index = BA.NumberToString(1);
 break;
case 1:
 //BA.debugLineNum = 114;BA.debugLine="edtQueryClass_BookID.Visible=False";
mostCurrent._edtqueryclass_bookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 115;BA.debugLine="edtQueryClass_Author.Visible=True";
mostCurrent._edtqueryclass_author.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 116;BA.debugLine="edtQueryClass_PubDate.Visible=False";
mostCurrent._edtqueryclass_pubdate.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 117;BA.debugLine="edtQueryClass_BookStore.Visible=False";
mostCurrent._edtqueryclass_bookstore.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 118;BA.debugLine="Query_Index=2";
_query_index = BA.NumberToString(2);
 break;
case 2:
 //BA.debugLineNum = 120;BA.debugLine="edtQueryClass_BookID.Visible=False";
mostCurrent._edtqueryclass_bookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 121;BA.debugLine="edtQueryClass_Author.Visible=False";
mostCurrent._edtqueryclass_author.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 122;BA.debugLine="edtQueryClass_PubDate.Visible=True";
mostCurrent._edtqueryclass_pubdate.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 123;BA.debugLine="edtQueryClass_BookStore.Visible=False";
mostCurrent._edtqueryclass_bookstore.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 124;BA.debugLine="Query_Index=3";
_query_index = BA.NumberToString(3);
 break;
case 3:
 //BA.debugLineNum = 126;BA.debugLine="edtQueryClass_BookID.Visible=False";
mostCurrent._edtqueryclass_bookid.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 127;BA.debugLine="edtQueryClass_Author.Visible=False";
mostCurrent._edtqueryclass_author.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 128;BA.debugLine="edtQueryClass_PubDate.Visible=False";
mostCurrent._edtqueryclass_pubdate.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 129;BA.debugLine="edtQueryClass_BookStore.Visible=True";
mostCurrent._edtqueryclass_bookstore.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 130;BA.debugLine="Query_Index=4";
_query_index = BA.NumberToString(4);
 break;
}
;
 //BA.debugLineNum = 132;BA.debugLine="End Sub";
return "";
}
}
