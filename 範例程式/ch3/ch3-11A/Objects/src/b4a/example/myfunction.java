package b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class myfunction {
private static myfunction mostCurrent = new myfunction();
public static Object getObject() {
    throw new RuntimeException("Code module does not support this method.");
}
 public anywheresoftware.b4a.keywords.Common __c = null;
public static String _title = "";
public b4a.example.main _main = null;
  public Object[] GetGlobals() {
		return new Object[] {"Main",Debug.moduleToString(b4a.example.main.class),"Title",_title};
}
public static String  _delay(anywheresoftware.b4a.BA _ba,long _ms) throws Exception{
		Debug.PushSubsStack("Delay (myfunction) ","myfunction",1,_ba,mostCurrent);
try {
long _now = 0L;
;
Debug.locals.put("ms", _ms);
 BA.debugLineNum = 7;BA.debugLine="Sub Delay(ms As Long)";
Debug.ShouldStop(64);
 BA.debugLineNum = 8;BA.debugLine="Dim now As Long";
Debug.ShouldStop(128);
_now = 0L;Debug.locals.put("now", _now);
 BA.debugLineNum = 9;BA.debugLine="If ms > 1000 Then ms = 1000";
Debug.ShouldStop(256);
if (_ms>1000) { 
_ms = (long) (1000);Debug.locals.put("ms", _ms);};
 BA.debugLineNum = 10;BA.debugLine="now = DateTime.now";
Debug.ShouldStop(512);
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();Debug.locals.put("now", _now);
 BA.debugLineNum = 11;BA.debugLine="Do Until (DateTime.now > now + ms)";
Debug.ShouldStop(1024);
while (!((anywheresoftware.b4a.keywords.Common.DateTime.getNow()>_now+_ms))) {
 BA.debugLineNum = 12;BA.debugLine="DoEvents";
Debug.ShouldStop(2048);
anywheresoftware.b4a.keywords.Common.DoEvents();
 }
;
 BA.debugLineNum = 14;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 3;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 4;BA.debugLine="Dim Title As String : Title = \"我的延遲函數\"";
_title = "";
 //BA.debugLineNum = 4;BA.debugLine="Dim Title As String : Title = \"我的延遲函數\"";
_title = "我的延遲函數";
 //BA.debugLineNum = 5;BA.debugLine="End Sub";
return "";
}
}
