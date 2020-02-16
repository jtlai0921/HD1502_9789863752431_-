package b4a.example;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.pc.*;

public class main_subs_0 {


public static RemoteObject  _activity_create(RemoteObject _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 29;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(268435456);
 BA.debugLineNum = 30;BA.debugLine="Activity.LoadLayout(\"Main\")";
Debug.ShouldStop(536870912);
main.mostCurrent._activity.runMethodAndSync(false,"LoadLayout",(Object)(BA.ObjectToString("Main")),main.mostCurrent.activityBA);
 BA.debugLineNum = 31;BA.debugLine="Activity.Title=\"1到N的三種常見求法\"";
Debug.ShouldStop(1073741824);
main.mostCurrent._activity.runMethod(false,"setTitle",RemoteObject.createImmutable(("1到N的三種常見求法")));
 BA.debugLineNum = 32;BA.debugLine="End Sub";
Debug.ShouldStop(-2147483648);
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
 BA.debugLineNum = 38;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(32);
 BA.debugLineNum = 40;BA.debugLine="End Sub";
Debug.ShouldStop(128);
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
 BA.debugLineNum = 34;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(2);
 BA.debugLineNum = 36;BA.debugLine="End Sub";
Debug.ShouldStop(8);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _btnrun1_click() throws Exception{
		Debug.PushSubsStack("btnRun1_Click (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
RemoteObject _i = RemoteObject.createImmutable(0);
RemoteObject _n = RemoteObject.createImmutable(0);
RemoteObject _sum = RemoteObject.createImmutable(0);
 BA.debugLineNum = 42;BA.debugLine="Sub btnRun1_Click        '第一種累加";
Debug.ShouldStop(512);
 BA.debugLineNum = 43;BA.debugLine="If edtNumber.Text=\"\" Then";
Debug.ShouldStop(1024);
if (RemoteObject.solveBoolean("=",main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"),BA.ObjectToString(""))) { 
 BA.debugLineNum = 44;BA.debugLine="Msgbox(\"您尚未輸入終值哦!\",\"產生錯誤...\")";
Debug.ShouldStop(2048);
main.mostCurrent.__c.runVoidMethodAndSync ("Msgbox",(Object)(BA.ObjectToString("您尚未輸入終值哦!")),(Object)(BA.ObjectToString("產生錯誤...")),main.mostCurrent.activityBA);
 }else {
 BA.debugLineNum = 46;BA.debugLine="Dim i,N,Sum As Int";
Debug.ShouldStop(8192);
_i = RemoteObject.createImmutable(0);Debug.locals.put("i", _i);
_n = RemoteObject.createImmutable(0);Debug.locals.put("N", _n);
_sum = RemoteObject.createImmutable(0);Debug.locals.put("Sum", _sum);
 BA.debugLineNum = 47;BA.debugLine="N = edtNumber.Text";
Debug.ShouldStop(16384);
_n = BA.numberCast(int.class, main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"));Debug.locals.put("N", _n);
 BA.debugLineNum = 48;BA.debugLine="For i = 1 To N           '「計數變數」設定初值為1";
Debug.ShouldStop(32768);
{
final int step23 = 1;
final int limit23 = _n.<Integer>get().intValue();
for (_i = BA.numberCast(int.class, 1); (step23 > 0 && _i.<Integer>get().intValue() <= limit23) || (step23 < 0 && _i.<Integer>get().intValue() >= limit23); _i = RemoteObject.createImmutable((int)(0 + _i.<Integer>get().intValue() + step23))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 49;BA.debugLine="Sum = Sum + i         '執行「程式區塊」亦即總和累加";
Debug.ShouldStop(65536);
_sum = RemoteObject.solve(new RemoteObject[] {_sum,_i}, "+",1, 1);Debug.locals.put("Sum", _sum);
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 51;BA.debugLine="lblResult.Text =Sum";
Debug.ShouldStop(262144);
main.mostCurrent._lblresult.runMethod(true,"setText",(_sum));
 };
 BA.debugLineNum = 53;BA.debugLine="End Sub";
Debug.ShouldStop(1048576);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _btnrun2_click() throws Exception{
		Debug.PushSubsStack("btnRun2_Click (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
RemoteObject _i = RemoteObject.createImmutable(0);
RemoteObject _n = RemoteObject.createImmutable(0);
RemoteObject _sum = RemoteObject.createImmutable(0);
 BA.debugLineNum = 54;BA.debugLine="Sub btnRun2_Click        '第二種奇數和";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 55;BA.debugLine="If edtNumber.Text=\"\" Then";
Debug.ShouldStop(4194304);
if (RemoteObject.solveBoolean("=",main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"),BA.ObjectToString(""))) { 
 BA.debugLineNum = 56;BA.debugLine="Msgbox(\"您尚未輸入終值哦!\",\"產生錯誤...\")";
Debug.ShouldStop(8388608);
main.mostCurrent.__c.runVoidMethodAndSync ("Msgbox",(Object)(BA.ObjectToString("您尚未輸入終值哦!")),(Object)(BA.ObjectToString("產生錯誤...")),main.mostCurrent.activityBA);
 }else {
 BA.debugLineNum = 58;BA.debugLine="Dim i,N,Sum As Int";
Debug.ShouldStop(33554432);
_i = RemoteObject.createImmutable(0);Debug.locals.put("i", _i);
_n = RemoteObject.createImmutable(0);Debug.locals.put("N", _n);
_sum = RemoteObject.createImmutable(0);Debug.locals.put("Sum", _sum);
 BA.debugLineNum = 59;BA.debugLine="N = edtNumber.Text";
Debug.ShouldStop(67108864);
_n = BA.numberCast(int.class, main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"));Debug.locals.put("N", _n);
 BA.debugLineNum = 60;BA.debugLine="For i = 1 To N Step 2     '「計數變數」設定初值為1";
Debug.ShouldStop(134217728);
{
final int step35 = 2;
final int limit35 = _n.<Integer>get().intValue();
for (_i = BA.numberCast(int.class, 1); (step35 > 0 && _i.<Integer>get().intValue() <= limit35) || (step35 < 0 && _i.<Integer>get().intValue() >= limit35); _i = RemoteObject.createImmutable((int)(0 + _i.<Integer>get().intValue() + step35))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 61;BA.debugLine="Sum = Sum + i          '執行「程式區塊」亦即總和累加";
Debug.ShouldStop(268435456);
_sum = RemoteObject.solve(new RemoteObject[] {_sum,_i}, "+",1, 1);Debug.locals.put("Sum", _sum);
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 63;BA.debugLine="lblResult.Text =Sum";
Debug.ShouldStop(1073741824);
main.mostCurrent._lblresult.runMethod(true,"setText",(_sum));
 };
 BA.debugLineNum = 65;BA.debugLine="End Sub";
Debug.ShouldStop(1);
return RemoteObject.createImmutable("");
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static RemoteObject  _btnrun3_click() throws Exception{
		Debug.PushSubsStack("btnRun3_Click (main) ","main",0,main.mostCurrent.activityBA,main.mostCurrent);
try {
RemoteObject _i = RemoteObject.createImmutable(0);
RemoteObject _n = RemoteObject.createImmutable(0);
RemoteObject _sum = RemoteObject.createImmutable(0);
 BA.debugLineNum = 66;BA.debugLine="Sub btnRun3_Click        '第三種偶數和";
Debug.ShouldStop(2);
 BA.debugLineNum = 67;BA.debugLine="If edtNumber.Text=\"\" Then";
Debug.ShouldStop(4);
if (RemoteObject.solveBoolean("=",main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"),BA.ObjectToString(""))) { 
 BA.debugLineNum = 68;BA.debugLine="Msgbox(\"您尚未輸入終值哦!\",\"產生錯誤...\")";
Debug.ShouldStop(8);
main.mostCurrent.__c.runVoidMethodAndSync ("Msgbox",(Object)(BA.ObjectToString("您尚未輸入終值哦!")),(Object)(BA.ObjectToString("產生錯誤...")),main.mostCurrent.activityBA);
 }else {
 BA.debugLineNum = 70;BA.debugLine="Dim i,N,Sum As Int";
Debug.ShouldStop(32);
_i = RemoteObject.createImmutable(0);Debug.locals.put("i", _i);
_n = RemoteObject.createImmutable(0);Debug.locals.put("N", _n);
_sum = RemoteObject.createImmutable(0);Debug.locals.put("Sum", _sum);
 BA.debugLineNum = 71;BA.debugLine="N = edtNumber.Text";
Debug.ShouldStop(64);
_n = BA.numberCast(int.class, main.mostCurrent._edtnumber.runMethodAndSync(true,"getText"));Debug.locals.put("N", _n);
 BA.debugLineNum = 72;BA.debugLine="For i = 2 To N Step 2     '「計數變數」設定初值為1";
Debug.ShouldStop(128);
{
final int step47 = 2;
final int limit47 = _n.<Integer>get().intValue();
for (_i = BA.numberCast(int.class, 2); (step47 > 0 && _i.<Integer>get().intValue() <= limit47) || (step47 < 0 && _i.<Integer>get().intValue() >= limit47); _i = RemoteObject.createImmutable((int)(0 + _i.<Integer>get().intValue() + step47))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 73;BA.debugLine="Sum = Sum + i          '執行「程式區塊」亦即總和累加";
Debug.ShouldStop(256);
_sum = RemoteObject.solve(new RemoteObject[] {_sum,_i}, "+",1, 1);Debug.locals.put("Sum", _sum);
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 75;BA.debugLine="lblResult.Text =Sum";
Debug.ShouldStop(1024);
main.mostCurrent._lblresult.runMethod(true,"setText",(_sum));
 };
 BA.debugLineNum = 77;BA.debugLine="End Sub";
Debug.ShouldStop(4096);
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
 //BA.debugLineNum = 22;BA.debugLine="Dim edtNumber As EditText";
main.mostCurrent._edtnumber = RemoteObject.createNew ("anywheresoftware.b4a.objects.EditTextWrapper");
 //BA.debugLineNum = 23;BA.debugLine="Dim lblResult As Label";
main.mostCurrent._lblresult = RemoteObject.createNew ("anywheresoftware.b4a.objects.LabelWrapper");
 //BA.debugLineNum = 24;BA.debugLine="Dim btnRun1 As Button";
main.mostCurrent._btnrun1 = RemoteObject.createNew ("anywheresoftware.b4a.objects.ButtonWrapper");
 //BA.debugLineNum = 25;BA.debugLine="Dim btnRun2 As Button";
main.mostCurrent._btnrun2 = RemoteObject.createNew ("anywheresoftware.b4a.objects.ButtonWrapper");
 //BA.debugLineNum = 26;BA.debugLine="Dim btnRun3 As Button";
main.mostCurrent._btnrun3 = RemoteObject.createNew ("anywheresoftware.b4a.objects.ButtonWrapper");
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return RemoteObject.createImmutable("");
}
public static RemoteObject  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return RemoteObject.createImmutable("");
}
}