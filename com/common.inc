<?php

//日志
function doLog($msg, $func="")
{
    $date = date('d.m.Y h:i:s');
    $log =  '[' . $func . '][' . $date . '][' . $msg . ']';
    error_log($log);
}

//校验短信码
function verifyMsg($mobile, $msg)
{
    return true;
}

//注册openfire用户
function regOpenfireUser($id, $pwd, $name=NULL, $email=NULL)
{
    $host = '182.92.162.223';
    $port = '9090';
    $service = '/plugins/userService/userservice';
    $secret = 'aUg006Ad';
    $openfire = "http://{$host}:{$port}{$service}?type=add&secret={$secret}&username={$id}&password={$pwd}";

    $file = fopen($openfire);    
    $response = fread($file, 1024);   
    if (ereg('OK', $response)) {   
        return true;   
    } else {   
        return false;    
    }   
    fclose($file);   
}

?>
