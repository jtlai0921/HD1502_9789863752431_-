<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<?php

$databasehost = "localhost";
$databasename = "b4adb";
$databaseusername ="leech";
$databasepassword = "S125026177csu";

$con = @mysql_connect($databasehost,$databaseusername,$databasepassword) or die(mysql_error());
mysql_select_db($databasename) or die(mysql_error());
mysql_query("SET CHARACTER SET utf8");

$query = $_GET["sqlre"];
$sth = mysql_query($query);

if (mysql_errno()) {
    header("HTTP/1.1 500 Internal Server Error");
    echo $query.'\n';
    echo mysql_error();
}
else
{
    $rows = array();
    while($r = mysql_fetch_assoc($sth)) {
        $rows[] = $r;

    }
    print urldecode(json_encode($rows,JSON_UNESCAPED_UNICODE));  
}
?>


