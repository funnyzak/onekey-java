package com.github.funnyzak.onekey.common.utils;

import java.net.HttpURLConnection;
import java.net.URL;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public class URLUtil {

	private static final Log logger = Logs.get();
    
	public static synchronized boolean isConnect(String urlStr) {  
        int counts = 0;  
        if (urlStr == null || urlStr.length() <= 0) {                         
            return false;                   
        }  
        while (counts < 3) {  
            try {  
            	URL url = new URL(urlStr);  
                HttpURLConnection   con = (HttpURLConnection) url.openConnection();  
                int state = con.getResponseCode();  
                if (state == 200) {  
                   return true;
                }  
                break;  
            }catch (Exception ex) {  
                counts++;   
                continue;  
            }  
        }  
        return false;  
    }  
	
	public static String getDomainUrl(String urlStr){
		String domainUrl=urlStr;
		try {
		     URL url = new URL(urlStr);
		     domainUrl=url.getProtocol()+"://"+url.getAuthority();
		} catch (Exception e) {
			logger.error("getDomainUrl is erro,url :"+urlStr, e);
		}
		return domainUrl;
	}
	
	
	public static String getHost(String urlStr){
		String host=urlStr;
		try {
		     URL url = new URL(urlStr);
		     host=url.getHost();
		} catch (Exception e) {
			logger.error("getHost is erro,url :"+urlStr, e);
		}
		return host;
	}

}
