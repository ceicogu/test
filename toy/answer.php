<?php
header("Content-type: text/html; charset=utf-8");

require_once("../db/DbMysql.inc");
require_once("../com/common.inc");

if(isset($_GET["q"]))  $Var["q"] = $_GET["q"]; 
$DB = new DbMysql('localhost', 'mysql', '123456', 'qihao');

$sql = "SELECT type, answer FROM answer WHERE question = \"{$Var['q']}\";";
$query = $DB->query($sql);
$queryResult = $DB->get_result_array($query);

if(!$queryResult) $queryResult['error'] = 0;
else $queryResult[0]['error'] = 1;

echo json_encode($queryResult);


?>
