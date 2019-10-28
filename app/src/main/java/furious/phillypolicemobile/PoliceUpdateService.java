package furious.phillypolicemobile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import furious.utils.HttpClientInfo;
import furious.viewfragments.PoliceNewsAll;


public class PoliceUpdateService extends Service{

	NotificationManager notificationManager;
	HttpURLConnection httpcon;
	Uri police;
	String TITLE;
	String CType;
	ArrayList<String> prefs;
    Timer tim;

	boolean isNEW_STORY = false;
	boolean isNEW_VIDEOS = false;
	
	boolean IMDIFF_NEWS = false;
	boolean IMDIFF_VIDEO = false;

	int ok = 000;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		Log.i("PHILLY_POLICE","OnStartCommand()");
			
			if(intent != null){
				switch (intent.getExtras().getInt("PoliceServiceCode")){
					case 99:
						Log.i("Service Say:", "SYSTEM_BOOTED");
					 break;
					 
					case 200:
						Log.i("StartUP", "Hello User");
					break;

                    case 888:
                        Log.i("SERVICE CLASS", "YOU HIT MEEEEEE!");
                        checkForUpdateTask();
                        break;

                    case 9911:
                        Log.i("KILL CALLED ME", "TRYING TO STOP TIMER");
                        tim.cancel();
                        break;

                }
		}
			
	    return START_STICKY;
	}

	
	@Override
	public void onCreate(){
		super.onCreate();
		
		notificationManager = (NotificationManager) 
				  getSystemService(NOTIFICATION_SERVICE);
		

		Log.i("PHILLY_POLICE", "Service called onCreated()");
		//checkForUpdateTask();

}
	private void checkForUpdateTask() {
		tim = new Timer();
		tim.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				new chkforNewHashes().execute();
			}
//		},10000,7200000); //every 2 hours after 10 second delay
			},10000,10000);
	}
	
	
	
	private String getNewsHash(String key) {
		
		String cal_hash = null;
				
		SharedPreferences updateHash = this.getSharedPreferences("UPDATEHASH", MODE_PRIVATE);
		
		if (updateHash.contains(key)) {
			cal_hash = updateHash.getString(key, "cfcd208495d565ef66e7dff9f98764da");
		
		}else{
			Editor edit = updateHash.edit();
			//edit.clear();
			edit.putString(key, "cfcd208495d565ef66e7dff9f98764da");
			cal_hash = "cfcd208495d565ef66e7dff9f98764da";
			edit.commit();
		}
		
		
		return cal_hash;
	}
	
	private void setNewHash(String key, String value){
		SharedPreferences updateHash = getApplicationContext().getSharedPreferences("UPDATEHASH", MODE_PRIVATE);
		//SharedPreferences date = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		
		if (updateHash.contains(key)) {
			Editor edit = updateHash.edit();
			//edit.clear();
			edit.remove(key);
			edit.putString(key, value);
			edit.commit();
			Log.i("Police Service Log", key+" :: SAVING NEW HASH :: "+value);
		}else{
			Editor edit = updateHash.edit();
			//edit.clear();
			edit.putString(key, value);
			edit.commit();
		}
	}
	

	public class chkforNewHashes extends AsyncTask<String, Void, String>{
			String svrRes;
			
			@Override
			protected String doInBackground(String... params) {
				
					try {

						if(isConnected()){

							try {

								boolean isUpdate = false;
								boolean isfalseReturn = true;

								String jsonData = exchangeHashKey();

								Log.i("POLICE RETURN DATA", jsonData);
								JSONObject jObj = new JSONObject(jsonData);

								isfalseReturn = jObj.getBoolean("error");


									if(!isfalseReturn){
										// API RETURNED DATA WITHOUT ERROR
										JSONArray h_keys = jObj.getJSONArray("HashKeys");
										int count = h_keys.length();
											//LOOP THROUGH THE KEYs AND STORE THEm IN THE HASHKEY CLASS

											for(int i=0;i<count;i++){

												JSONObject itemObj = h_keys.getJSONObject(i);

												if(itemObj.getString("HashName").equals("Calendar")){
													 Log.i("SERVER RESPONSE :: ", "Calendar HASH: "+itemObj.getString("Hash"));
													//isNEW_STORY = isDiffHash("CalendarHash",itemObj.getString("Hash"));
												}

												if(itemObj.getString("HashName").equals("NewsStory")){

													Log.i("SERVER RESPONSE :: ", "NewsStory Hash: "+itemObj.getString("Hash"));
													isNEW_STORY = isDiffHash("NewsStoryHash",itemObj.getString("Hash"));

														if(isNEW_STORY){
															Log.e("PHILLY_POLICE","NEWS STORY HASH HAS CHANGED");
															IMDIFF_NEWS = true;

														}else if(!isNEW_STORY){
															Log.e("PHILLY_POLICE","NEWS STORY HASH HAS NOT CHANGED");
														}
												}

												if(itemObj.getString("HashName").equals("UCVideos")){

													Log.i("POLICE_N", "UCVideos Hash: "+itemObj.getString("Hash"));
													isNEW_VIDEOS = isDiffHash("UCVideosHash",itemObj.getString("Hash"));

														if(isNEW_VIDEOS){
															Log.e("PHILLY_POLICE","UC_VIDEOS HASH HAS CHANGED");
															IMDIFF_VIDEO = true;

														}else if(!isNEW_VIDEOS){
															Log.e("PHILLY_POLICE","UC_VIDEOS HASH HAS NOT CHANGED");
														}
												}


											}

												getMyUpdates();

									}



							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}else{

							// NOT CONNECTED TO THE INTERNET

						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
				
				return svrRes;
			}


		
	}
	
	 
	 public String exchangeHashKey() throws JSONException{

			String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
		 	String deviceID = HttpClientInfo.getMD5(macAddss);

		 String result = null;

		 try {

			 JSONObject postObj = new JSONObject();
			 postObj.put("Update", "true");
			 postObj.put("DeviceID", deviceID);
             String data = postObj.toString();

			 Log.i("DEVICE_ID ::: ",deviceID);
             Log.i("SENDING DATA", data);


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

		 } catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		 } catch (ProtocolException e) {
             e.printStackTrace();
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }


         return result;
	 }
	 
	 
	 public String getJSONData() throws JSONException{

		 	String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
		 	String deviceID = HttpClientInfo.getMD5(macAddss);

		 String result = null;

		 try {


			 JSONObject postObj = new JSONObject();
		 	JSONArray jArray = new JSONArray();
		 		
		 	if(!prefs.isEmpty()){
		 		
		 		for(int i=0;i<prefs.size();i++){
		 			jArray.put(prefs.get(i));
		 		}
		 		
		 	}else if(prefs.isEmpty()){
		 		jArray.put("00");
		 	}
		 	
		 	
	 		postObj.put("District_Update", "true");
	 		postObj.put("DeviceID", deviceID);
	 		postObj.put("Districts", jArray);
	 		postObj.put("UC_Videos", cst(IMDIFF_VIDEO));
	 		String data = postObj.toString();
	 		Log.e("POSTING_THIS", jArray.toString());
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

		 } catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }


			return result;
	 }

	 private void getMyUpdates() throws JSONException{
		 
			///PREFERENCE CHECK///
			SharedPreferences chkbx = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences chkbx2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean isDisSet = chkbx.getBoolean("checkbox_preference", false); // HAS THE USER CHECK THE NEWSTORY BOX
			boolean isUCSet =  chkbx2.getBoolean("UCV_checkbox_preference", false);
			
				
				if(isDisSet && IMDIFF_NEWS){
					
					Log.e("PHILLY_POLICE_SERVICE", "NEWS ALERTS SET && HASH HAS CHANGED");
					police = Uri.parse("android.resource://furious.phillypolicemobile/" + R.raw.police_siren);
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			        Set<String> a = pref.getStringSet("district_preference", null);
			        prefs = new ArrayList<String>();
			        	
			        	if(!a.isEmpty() || !a.equals(null) || !a.equals("")){
			        		
			        		for (String str: a) {
					        	//Log.i("CLICKED:: ", str);
					        	prefs.add(str);
					        }								
								
							 if(!isUCSet){
								
								Log.e("PHILLY_POLICE","UC_VIDEOS NOT SET && HASH HAS CHANGED");
								IMDIFF_VIDEO = false;
							
							 }else{
								
								 IMDIFF_VIDEO = true;
							
							 }
			        	
			        		String jsonData = getJSONData();
							Log.i("PHILLY_POLICE","RETURNED FROM THE SERVER ::::::  "+jsonData);
							JSONObject jObj = new JSONObject(jsonData);
							String Ncount = jObj.getString("NewsTotalCount");
							String Vcount = jObj.getString("VideoTotalCount");
							String desc  = "";
							String title = "";
					
								if(Integer.valueOf(Ncount) >=1 || Integer.valueOf(Vcount) >=1){
									
									Intent intent = new Intent(getApplicationContext(), PoliceNewsAll.class);
	//								Bundle bundle = new Bundle();
	//								bundle.putString("HashTag", getNewsHash());
									intent.putExtra("HashTag", getNewsHash("NewsStoryHash"));
									
									TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
									stackBuilder.addParentStack(PoliceNewsAll.class);
									stackBuilder.addNextIntent(intent);
									PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

									
									if(Integer.valueOf(Ncount) >=1 && Integer.valueOf(Vcount) == 0){ // News but no videos
										
										if(Integer.valueOf(Ncount) == 1){
											title = Ncount+" News Story";
										}else{
											title = Ncount+" News Stories";
										}
										
										desc = jObj.getJSONArray("NewsObjects").getJSONObject(0).getString("Title");
									
									}else if(Integer.valueOf(Ncount) >=1 && Integer.valueOf(Vcount) >= 1){
										
										String l1 = "";
										String l2 = "";
										
										if(Integer.valueOf(Ncount) == 1){
											l1 = Ncount+" News Story";
										}else{
											l1 = Ncount+" News Stories";
										}
										
										if(Integer.valueOf(Vcount) == 1){
											l2 = Vcount+" UC Video";
										}else{
											l2 = Vcount+" UC Videos";
										}
										
										title = l1+ " and "+l2;
										
										desc = jObj.getJSONArray("NewsObjects").getJSONObject(0).getString("Title");
									
									}else if(Integer.valueOf(Ncount) == 0 && Integer.valueOf(Vcount) >= 1){
										
											if(Integer.valueOf(Vcount) == 1){
												title = Vcount+" UC Video";
											}else{
												title = Vcount+" UC Videos";
											}
											
										desc = jObj.getJSONArray("VideoObjects").getJSONObject(0).getString("VideoTitle");
										
									}
									
									desc = desc.substring(0,Math.min(desc.length(), 200)); /// only 200 words in the Notification box
									
									RemoteViews views = new RemoteViews(getPackageName(),R.layout.notification_layout);
									views.setImageViewResource(R.id.image, R.drawable.ic_launcher);
									views.setTextViewText(R.id.title, title);
									views.setTextViewText(R.id.text, desc);
									
									NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						                     this)
											 .setSmallIcon(R.drawable.ic_launcher)
						                     .setContent(views)
						                     .setSound(police); 

															        		
									mBuilder.setContentIntent(pIntent);
									
									notificationManager.notify(0, mBuilder.build());
								
								
								
								
								}else{
									
								//	NO CHANGE IN THE JSON RETURN FORM THE SERVER; WHE ASKED ABOU THE ARRAY OD DISTRICTS 
									
								}
									
									

			        	
			        	}else{
			        		// NO SETTINGS RETUNRED 
			        	}
			        	
			
			        	IMDIFF_NEWS = false;
			        	
				}else if(isDisSet && !IMDIFF_NEWS){
					Log.e("PHILLY_POLICE_SERVICE","ALERTS CHECKED BUT NO UPDATES");
					
				}
				

				
			//END PREFERENCE CHECK///
			
		}
	 
	 private String cst(boolean isgood){
		String flwrd = null;
		 if(isgood){
			 flwrd = "true";
		 }else if(!isgood){
			 flwrd = "false";
		 }
		
		 return flwrd;
		 
	 }
	 
	 private boolean isConnected() throws IOException{
			boolean isGood = false;
			 ConnectivityManager connMgr = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
			 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			 	if (networkInfo != null && networkInfo.isConnected()) {
			 		//Log.i("POLICE_N", "URL Connection is Good");
		        	java.net.URL Srvurl = new java.net.URL(HttpClientInfo.URL);
		    		HttpURLConnection conn = (HttpURLConnection) Srvurl.openConnection();
		    		conn.setConnectTimeout(20000);

		    		try{
						conn.connect();
					}

					catch (IOException e) {
						// HttpUrlConnection will throw an IOException if any 4XX
						// response is sent. If we request the status again, this
						// time the internal status will be properly set, and we'll be
						// able to retrieve it.
						Log.i("ERROR","Connection failed");
					 ok = conn.getResponseCode();
					 e.printStackTrace();
					 Toast.makeText(this, " No Connection at this time",Toast.LENGTH_LONG).show();
					}
		    	//	int ok = conn.getResponseCode();
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
		
		private boolean isDiffHash(String HashKey, String curHash) {
			// TODO Auto-generated method stub
			
			String temp_hash = null;
			boolean hasChg = false;
			
			SharedPreferences updateHash = getApplicationContext().getSharedPreferences("UPDATEHASH", MODE_PRIVATE);
			
			
			if (updateHash.contains(HashKey)){
				temp_hash = updateHash.getString(HashKey, "cfcd208495d565ef66e7dff9f98764da");
					
					if(!temp_hash.equals(curHash)){
						hasChg = true;
						setNewHash(HashKey,curHash);
					}
				
			}else{
				
				Log.i("PHILLY_POLICE_SERVICE","HASH DOES NOT EXIST");
				setNewHash(HashKey,curHash);
				Log.i("PHILLY_POLICE_SERVICE","SETTING NEW HASH "+curHash);
				
			}
			
			return hasChg;
		}
	
}