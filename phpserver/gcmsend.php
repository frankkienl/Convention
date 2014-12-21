<?php

/*
 * Show list of possible receivers, make a message, send.
 */

include "db.php";

$db = mysql_connect($dbhost, $dbuser, $dbpass);
mysql_select_db($dbname, $db);
$sql = "SELECT * FROM `hwcon_gcm`";
$result = mysql_query($sql);
echo "<form method=\"post\">";
echo "<table border=1>\n";
echo "<tr><td>&nbsp;<th>_id<th>deviceName<th>Comment<th>regId</tr>\n";
while ($row = mysql_fetch_array($result)) {
    echo "<tr><td><input type=\"checkbox\" name=\"gcm_$row[regId]\" value=\"$row[regId]\"/><td>$row[_id]<td>$row[deviceName]<td>$row[comment]<td><span style=\"font-size: 5pt;\">$row[regId]</span></tr>\n";
}
echo "</table><hr />\n";
?>
<table>
    <tr>
        <td>type
        <td>
            <select name="type">
                <option value="sync">Sync</option>
                <option value="notification">Notification</option>
            </select>
        </td>
    </tr>
    <tr>
        <td>Notification Message<br /><span style="font-size: 12pt;">(only when using notification type)</span></td>
        <td><input type="text" name="message" /></td>
    </tr>
</table>
<br />
<input type="hidden" name="gezien" value="ja" />
<input type="submit" />
<?php

echo "</form>\n";

if (isset($_POST[gezien])) {
    //SEND !!
    $ids = array();
    foreach ($_POST as $key => $value) {
        if (startsWith($key, "gcm_")){
            if (isset($_POST[$key])){ //checkbox is checked
                //http://stackoverflow.com/questions/4554758/how-to-read-if-a-checkbox-is-checked-in-php
                $ids[] = $_POST[$key]; //add regId to list
            }
        }
    }
    if ($_POST[type] == "sync"){
        $data = array('action' => 'sync');
    } else if ($_POST[type] == "notification"){
        $data = array('action' => 'notification', 'message' => $_POST[message]);
    }

    sendGoogleCloudMessage($data, $ids, $apiKey);
}

function sendGoogleCloudMessage($data, $ids, $apiKey) {
//http://stackoverflow.com/questions/11242743/gcm-with-php-google-cloud-messaging

    $url = 'https://android.googleapis.com/gcm/send';
    $post = array(
        'registration_ids' => $ids,
        'data' => $data,
    );

    $headers = array(
        'Authorization: key=' . $apiKey,
        'Content-Type: application/json'
    );
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($post));
    $result = curl_exec($ch);
    if (curl_errno($ch)) {
        echo 'GCM error: ' . curl_error($ch);
    }
    curl_close($ch);
    echo $result;
}

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