<?php
 if(isset($_GET['cmd'])){


$myfile = fopen("cmds.xxx", "a") or die("Unable to open file!");
$txt =$_GET['cmd']."\n";
fwrite($myfile, $txt);
fclose($myfile);
}
else echo "Invalid Request";
?>