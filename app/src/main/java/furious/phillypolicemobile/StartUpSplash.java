package furious.phillypolicemobile;


import java.net.URL;
import java.net.URLConnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class StartUpSplash extends Activity {


	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startupsplash);
        
        
        if(isWiFiOn()){
        	
        	Intent intent = new Intent(StartUpSplash.this, StartUpSplash1.class);
        	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	startActivity(intent);
        	finish();
        }

    }
	
	

	private boolean isWiFiOn(){
    	
		Log.i("POLICE_APP","Checking WiFi......");
		ConnectivityManager connManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
			if(mWifi.isConnected()){
				Log.i("POLICE_APP","WiFi is UP");
				Log.i("POLICE_APP","Checking Hosting Server Connection....");
				
					if(isServerUp()){
						Log.i("POLICE_APP","Hosting Server is UP");
						
					}else{
						Log.i("POLICENEWS","Server is Down");
						
						

					}
			}else if(!mWifi.isConnected()){
				Log.i("POLICE_APP", "Wifi is DOWN");
				
					if(mMobile.isConnected()){
						Log.i("POLICE_APP","Mobile Network is UP");
							if(isServerUp()){
								Log.i("POLICE_APP","Hosting Server is UP");
								
								
							}else{
								Log.i("POLICE_APP","Server is Down");

						}
					}else if(!mMobile.isConnected()){
						Log.i("POLICE_APP", "NO Network Acces on this device");
						
						new AlertDialog.Builder(this)
				        .setIcon(R.drawable.ic_launcher)
				        .setTitle("No Internet Connection")
				        .setMessage("Please Turn on Mobile Data")
				        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
				            @Override
				            public void onClick(DialogInterface dialog, int which) {
				                //code for exit
//				                Intent intent = new Intent(Intent.ACTION_MAIN);
//				                intent.addCategory(Intent.CATEGORY_HOME);            
//				                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////				                startActivity(intent);
				            	finish();
				            }

				        })
				        .show();
						
					}
				
					
			}
		return mWifi.isConnected();
	}
	
	
	
		public boolean isServerUp() {
			
	        try{
	           
	        	URL myUrl = new URL(HttpClientInfo.URL);
	            URLConnection connection = myUrl.openConnection();
	            connection.setConnectTimeout(10000);
	            connection.connect();
	            return true;
	        
	        } 
	        
	        	catch (Exception e) {
	            
	        		return false;
	        	}
	    }


   
    
}
