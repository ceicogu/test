<?php

class DbMysql{
    private $Host;
    private $User;
    private $Pw;
    private $DbName;
    private $Conn = false;
    private $Persistent = false;
    
    function __construct($Host, $User, $Pw, $DbName, $Persistent = false) {
        $this->Host = $Host;
        $this->User = $User;
        $this->Pw = $Pw;
        $this->DbName = $DbName;
        $this->Persistent = $Persistent;

        if($Persistent) $this->pconnect();
        else $this->connect();
        //mysql_client_encoding($this->Conn);
        mysql_query("SET NAMES UTF8");
    }
    
    private function connect()
    {
        $this->Conn = mysql_connect($this->Host, $this->User, $this->Pw); 
        if (!$this->Conn){
            $this->errlog("connect");
            exit(0);
        }

        if(!mysql_select_db($this->DbName, $this->Conn)){
            $this->errlog("connect");
            exit(0);
        }

        return true;
    }

    private function pconnect()
    {
        $this->Conn = mysql_pconnect($this->Host, $this->User, $this->Pw); 
        if (!$this->Conn){
            $this->errlog("pconnect");
            exit(0);
        }

        if(!mysql_select_db($this->DbName, $this->Conn)){
            $this->errlog("pconnect");
            exit(0);
        }
        
        $this->Persistent = true;
        return true;
    }

    public function query($SQL)
    {
        $Query = mysql_query($SQL, $this->Conn);

        if (!$Query){ 
            $this->errlog("query", $SQL);
            return false;
        }
        
        return $Query;
    }

    public function get_result_array($Query)
    {
        $Result = array();

        while( $Row = mysql_fetch_assoc($Query) ){
            $Result[] = $Row;
        }
 
        if(count($Result)) return $Result;

        return false;
    }

    public function free_result($Query)
    {
        return mysql_free_result($Query);
    }

    public function close()
    {
        mysql_close();
    }

    public function insert_id()
    {
        $id = mysql_insert_id();
        return $id;
    }

    public function errlog($Func, $SQL = '')
    {
        $Date = date('d.m.Y h:i:s');   
        $ErrNum = mysql_errno();
        $ErrStr   = mysql_error();
        $Log = '[' . $Func . '][' . $Date . '][' . $ErrNum . '][' . $ErrStr . '][' . $SQL . ']' . "\n";
        error_log($Log);
    }
}
?>
