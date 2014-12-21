<?php

//save GCM regId, in DB
//DB credentials saved in separate file for safety
include "db.php";

$db = mysql_connect($dbhost, $dbuser, $dbpass);
mysql_select_db($dbname, $db);

//http://php.net/manual/en/function.mysql-real-escape-string.php
//Apparently, mysql_real_escape_string is deprecated... Maybe I should look into that.. Someday..
$sql = "INSERT INTO `hwcon_gcm` (`regId`, `deviceName`) VALUES ('" . mysql_real_escape_string($_POST['regId']) . "','" . mysql_real_escape_string($_POST['deviceName']) . "')";

//echo $sql;

mysql_query($sql, $db);

//echo mysql_error($db);
$error = mysql_error($db);
if (!startsWith($error, "Duplicate entry")){
    //ignore Duplicate entry errors, other errors, print those.
    echo $error;
}

mysql_close($db);

echo "ok";

//http://stackoverflow.com/questions/834303/startswith-and-endswith-functions-in-php
function startsWith($haystack, $needle) {
    // search backwards starting from haystack length characters from the end
    return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
}

function endsWith($haystack, $needle) {
    // search forward starting from end minus needle length characters
    return $needle === "" || strpos($haystack, $needle, strlen($haystack) - strlen($needle)) !== FALSE;
}

?>