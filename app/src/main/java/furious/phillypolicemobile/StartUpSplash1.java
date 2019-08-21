package furious.phillypolicemobile;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StartUpSplash1 extends Activity {

	ProgressBar progress;
	String URL = HttpClientInfo.URL;
	HttpURLConnection httpcon;

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.startupsplash1);
       
       
       SharedPreferences isAG = getApplicationContext().getSharedPreferences("Agreement", MODE_PRIVATE);
       
       	if(isAG.contains("isAgreement")){
       		Intent intent = new Intent(StartUpSplash1.this, MainStart.class);
         	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
         	startActivity(intent);
         	finish();

       	}else{
       		
       		//final Button goSett = (Button) findViewById(R.id.gotosettings);
            final Button done = (Button) findViewById(R.id.donebutton);
            final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
            progress = (ProgressBar) findViewById(R.id.progressBar1);
            
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

     		@Override
     		public void onCheckedChanged(CompoundButton buttonView,
     				boolean isChecked) {
     			// TODO Auto-generated method stub
     			
     				if(isChecked){
     					done.setEnabled(true);
     				}else if(!isChecked){
     					done.setEnabled(false);
     				}
     			
     		}
            
            });
            
            
            done.setOnClickListener(new OnClickListener(){
            	
     		@Override
     		public void onClick(View v) {
     			// TODO Auto-generated method stub
     			
     			progress.setVisibility(View.VISIBLE);
     			done.setEnabled(false);
//     			goSett.setEnabled(false);
     			checkBox.setEnabled(false);
     			new pushDevID().execute();

     		}
         	   
            });
       		
       		
       	}
       
       
       

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
		
		
		
		
		
		public class pushDevID extends AsyncTask<String, Void, String>{

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				
					try {
						
						if(isConnected()){
							
							String jsonData = getJSONData();
							Log.i("PHILLY_POLICE", jsonData);
							JSONObject jsonObj = new JSONObject(jsonData);
							String isGood = jsonObj.getString("error");
							String isSuccess = jsonObj.getString("msg");
							
								if(isGood.equals("false") && isSuccess.equals("success")){
									
									SharedPreferences AGR = getApplicationContext().getSharedPreferences("Agreement", MODE_PRIVATE);
				     				
				     				if (AGR.contains("isAgreement")) {
				     					Editor edit = AGR.edit();
				     					edit.clear();
				     					edit.putString("isAgreement", "true");
				     					edit.commit();
				     				}else{
				     					Editor edit = AGR.edit();
				     					edit.clear();
				     					edit.putString("isAgreement", "true");
				     					edit.commit();
				     				}
								
									Intent intent = new Intent(StartUpSplash1.this, MainStart.class);
					             	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					             	startActivity(intent);
					             	finish();
								
								}else if(isGood.equals("false")){
									
									Toast.makeText(getApplicationContext(), jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
								}
							
						}
					
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				return null;
			}
			
		}

		
		
		
		private boolean isConnected() throws IOException{
			boolean isGood = false;
			 ConnectivityManager connMgr = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
			 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			 	if (networkInfo != null && networkInfo.isConnected()) {
			 		//Log.i("POLICE_N", "URL Connection is Good");
		        	java.net.URL Srvurl = new java.net.URL(URL);
		    		HttpURLConnection conn = (HttpURLConnection) Srvurl.openConnection();
		    		conn.setConnectTimeout(20000);
		    		conn.connect();
		    		int ok = conn.getResponseCode();
		    			if(ok == HttpURLConnection.HTTP_OK){
		    				isGood = true;
		    				Log.i("POLICE_N", "Connection to server is OK");
		    			}else if(ok == -1 || ok != HttpURLConnection.HTTP_OK){
		    				isGood = false;
		    				Log.i("POLICE_N","SERVER Said "+ok+" NOT UP");
		    			}
			        		isGood = true;
			        }else{
			        	Log.i("POLICE_N", "NETWORK NOT UP");
			            isGood = false;
			        }
			
			return isGood;
		};
		
		
		 public String getJSONData() throws JSONException, IOException{
			 	
			 	String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);

			 String result = null;

			 try {

			 	JSONObject postObj = new JSONObject();
		 		postObj.put("isAgreement", "true");
		 		postObj.put("DeviceID", deviceID);
				String data = postObj.toString();
		 		Log.e("PUSHING_THIS",postObj.toString());


				 //Connect
				 httpcon = (HttpURLConnection) ((new URL(HttpClientInfo.URL).openConnection()));
				 httpcon.setDoOutput(true);
				 httpcon.setRequestProperty("Content-Type", "application/json");
				 httpcon.setRequestProperty("Accept", "application/json");
				 httpcon.setRequestProperty("Accept-Language","en-US");
				 httpcon.setRequestMethod("POST");
				 httpcon.connect();

				 //Write
				 OutputStream os = httpcon.getOutputStream();
				 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				 writer.write(data);
				 writer.close();
				 os.close();

				 //Read
				 BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(),"UTF-8"));

				 String line = null;
				 StringBuilder sb = new StringBuilder();

				 while ((line = br.readLine()) != null) {
					 sb.append(line);
				 }

				 br.close();
				 result = sb.toString();

			 } catch (IOException e) {
				 e.printStackTrace();
			 }

			 return result;


		 }
	
}


   
    

