package b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.pc.*;

public class main_subs_0 {


public static RemoteObject  _activity_create(RemoteObject _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(67108864);
 BA.debugLineNum = 28;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(134217728);
main.mostCurrent._activity.runMethodAndSync(false,"LoadLayout",(Object)(BA.ObjectToString("Main")),main.mostCurrent.activityBA);
 BA.debugLineNum = 29;BA.debugLine="Activity.Title=\"九九乘法表\"";
Debug.ShouldStop(268435456);
main.mostCurrent._activity.runMethod(false,"setTitle",RemoteObject.createImmutable(("九九乘法表")));
 BA.debugLineNum = 30;BA.debugLine="End Sub";
Debug.ShouldStop(536870912);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _activity_pause(RemoteObject _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 36;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(8);
 BA.debugLineNum = 38;BA.debugLine="End Sub";
Debug.ShouldStop(32);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
 BA.debugLineNum = 32;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(-2147483648);
 BA.debugLineNum = 34;BA.debugLine="End Sub";
Debug.ShouldStop(2);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _btnrun_click() throws Exception{
		Debug.PushSubsStack("btnRun_Click (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
RemoteObject _number = RemoteObject.createImmutable(0);
RemoteObject _temp = RemoteObject.createImmutable("");
int _i = 0;
int _j = 0;
 BA.debugLineNum = 40;BA.debugLine="Sub btnRun_Click";
Debug.ShouldStop(128);
 BA.debugLineNum = 41;BA.debugLine="If edtNumber.Text=\"\"  Then";
Debug.ShouldStop(256);
if (RemoteObject.solveBoolean("=",main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"),BA.ObjectToString(""))) { 
 BA.debugLineNum = 42;BA.debugLine="Msgbox(\"您尚未輸入數值資料哦!\",\"產生錯誤...\")";
Debug.ShouldStop(512);
main.mostCurrent.__c.runVoidMethodAndSync ("Msgbox",(Object)(BA.ObjectToString("您尚未輸入數值資料哦!")),(Object)(BA.ObjectToString("產生錯誤...")),main.mostCurrent.activityBA);
 }else {
 BA.debugLineNum = 44;BA.debugLine="Dim Number As Int";
Debug.ShouldStop(2048);
_number = RemoteObject.createImmutable(0);Debug.locals.put("Number", _number);
 BA.debugLineNum = 45;BA.debugLine="Dim Temp As String";
Debug.ShouldStop(4096);
_temp = RemoteObject.createImmutable("");Debug.locals.put("Temp", _temp);
 BA.debugLineNum = 46;BA.debugLine="Number =edtNumber.Text";
Debug.ShouldStop(8192);
_number = BA.numberCast(int.class, main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"));Debug.locals.put("Number", _number);
 BA.debugLineNum = 47;BA.debugLine="For i = 1 To Number           '外迴圈";
Debug.ShouldStop(16384);
{
final int step22 = 1;
final int limit22 = _number.<Integer>get().intValue();
for (_i = 1; (step22 > 0 && _i <= limit22) || (step22 < 0 && _i >= limit22); _i = ((int)(0 + _i + step22))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 48;BA.debugLine="For j = 1 To Number        '內迴圈";
Debug.ShouldStop(32768);
{
final int step23 = 1;
final int limit23 = _number.<Integer>get().intValue();
for (_j = 1; (step23 > 0 && _j <= limit23) || (step23 < 0 && _j >= limit23); _j = ((int)(0 + _j + step23))) {
Debug.locals.put("j", _j);
 BA.debugLineNum = 49;BA.debugLine="If (i * j > 9) Then";
Debug.ShouldStop(65536);
if ((RemoteObject.solveBoolean(">",RemoteObject.solve(new RemoteObject[] {RemoteObject.createImmutable(_i),RemoteObject.createImmutable(_j)}, "*",0, 1),BA.numberCast(double.class, 9)))) { 
 BA.debugLineNum = 50;BA.debugLine="Temp = Temp & i & \"*\" & j & \"=\" & (i*j) & \"　\"";
Debug.ShouldStop(131072);
_temp = RemoteObject.concat(_temp,RemoteObject.createImmutable(_i),RemoteObject.createImmutable("*"),RemoteObject.createImmutable(_j),RemoteObject.createImmutable("="),(RemoteObject.solve(new RemoteObject[] {RemoteObject.createImmutable(_i),RemoteObject.createImmutable(_j)}, "*",0, 1)),RemoteObject.createImmutable("　"));Debug.locals.put("Temp", _temp);
 }else {
 BA.debugLineNum = 52;BA.debugLine="Temp = Temp & i & \"*\" & j & \"=\" & \"  \" & (i*j) & \"　\"";
Debug.ShouldStop(524288);
_temp = RemoteObject.concat(_temp,RemoteObject.createImmutable(_i),RemoteObject.createImmutable("*"),RemoteObject.createImmutable(_j),RemoteObject.createImmutable("="),RemoteObject.createImmutable("  "),(RemoteObject.solve(new RemoteObject[] {RemoteObject.createImmutable(_i),RemoteObject.createImmutable(_j)}, "*",0, 1)),RemoteObject.createImmutable("　"));Debug.locals.put("Temp", _temp);
 };
 }
}Debug.locals.put("j", _j);
;
 BA.debugLineNum = 55;BA.debugLine="Temp = Temp & CRLF      '換行";
Debug.ShouldStop(4194304);
_temp = RemoteObject.concat(_temp,main.mostCurrent.__c.getField(true,"CRLF"));Debug.locals.put("Temp", _temp);
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 57;BA.debugLine="lblResult.Text=Temp";
Debug.ShouldStop(16777216);
main.mostCurrent._lblresult.runMethod(true,"setText",(_temp));
 };
 BA.debugLineNum = 59;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main_subs_0._process_globals();
main.myClass = BA.getDeviceClass ("b4a.example.main");
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static RemoteObject  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim lblResult As Label";
main.mostCurrent._lblresult = RemoteObject.createNew ("anywheresoftware.b4a.objects.LabelWrapper");
 //BA.debugLineNum = 23;BA.debugLine="Dim btnRun As Button";
main.mostCurrent._btnrun = RemoteObject.createNew ("anywheresoftware.b4a.objects.ButtonWrapper");
 //BA.debugLineNum = 24;BA.debugLine="Dim edtNumber As EditText";
main.mostCurrent._edtnumber = RemoteObject.createNew ("anywheresoftware.b4a.objects.EditTextWrapper");
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return RemoteObject.createImmutable("");
}
public static RemoteObject  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return RemoteObject.createImmutable("");
}
}