package furious.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


public class HttpClientInfo{

//	public static final String URL = "http://10.20.30.11/PhilaPolice-Info-API/phila_pd_api.php";
//public static final String URL = "http://sensationalsettings.info/api/api.php";
	public static final String URL = URLDataClass.getURL();

	public static String getMacAddress(Context context) {
	    WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    TelephonyManager   telephonyManager  = (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
	    
	    String macAddress = wimanager.getConnectionInfo().getMacAddress();
	    if (macAddress == null) {
	        macAddress = telephonyManager.getDeviceId();
	    }
	    
	    return macAddress;
	}
	
		
		public static String getMD5(String input){
		    	
		    	try {
		           
		    		MessageDigest md = MessageDigest.getInstance("MD5");
		            byte[] messageDigest = md.digest(input.getBytes());
		            BigInteger number = new BigInteger(1, messageDigest);
		            String hashtext = number.toString(16);
		            // Now we need to zero pad it if you actually want the full 32 chars.
		            
		            while (hashtext.length() < 32) {
		                hashtext = "0" + hashtext;
		            }
		            
		            return hashtext;
		        }
		        
		    	catch (NoSuchAlgorithmException e) {
		            throw new RuntimeException(e);
		        }
			}
	
	
	

}
