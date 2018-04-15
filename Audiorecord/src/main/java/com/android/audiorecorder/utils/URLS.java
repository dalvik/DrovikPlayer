package com.android.audiorecorder.utils;


public class URLS {
    
    private static final String HOST = "10.0.2.2";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private final static String URL_HOST = "www.drovik.com";
    private final static String URL_WWW_HOST = "www."+URL_HOST;
    private final static String URL_MY_HOST = "my."+URL_HOST;
    private final static String URL_SPLITTER = "/";
    private final static String URL_API_HOST = HTTP + URL_HOST + URL_SPLITTER;
    public final static String THUMB_LIST = URL_API_HOST+"action/api/thumb_list.php";
    public final static String FILE_LIST = URL_API_HOST+"action/api/cloud_file_list.php";
    public final static String DOMAIN = URL_API_HOST;
    public static final String URL = DOMAIN + "action/api/br/view/user.php";
    public static final String HEADER_URL = DOMAIN + "action/api/br/view/header.php";
    private static final String NAME_SPACE = "http://webservice.dhsoft.com";
    
    private URLS(){
        
    }
    
}
