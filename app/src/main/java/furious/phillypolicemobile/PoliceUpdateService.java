package furious.phillypolicemobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


public class PoliceUpdateService extends Service{
	
	String URL = HttpClientInfo.URL;
	HttpClient client;
	NotificationManager notificationManager;
	Uri police;
	String TITLE;
	String CType;
	ArrayList<String> prefs;

	boolean isNEW_STORY = false;
	boolean isNEW_VIDEOS = false;
	
	boolean IMDIFF_NEWS = false;
	boolean IMDIFF_VIDEO = false;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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

			}
		}
			
	    return START_STICKY;
	}
	
	
	@SuppressLint("NewApi")
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		notificationManager = (NotificationManager) 
				  getSystemService(NOTIFICATION_SERVICE);
		

		Log.i("PHILLY_POLICE", "Service called onCreated()");
		checkForUpdateTask();

}
	private void checkForUpdateTask() {
		Timer tim = new Timer();
		tim.scheduleAtFixedRate(new TimerTask(){
			@Override
			public void run(){
				new chkforNewHashes().execute();
			}
		},10000,7200000); //every 2 hours after 10 second delay
//			},10000,10000);
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
	
	 @SuppressLint("NewApi")
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
								
								Log.i("POLICE_SERVICE ::::: ", jsonData);
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
													//Log.i("SERVER RESPONSE :: ", "Calendar HASH: "+itemObj.getString("Hash"));
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

																	
								
							} catch (ClientProtocolException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally {
			 					client.getConnectionManager().shutdown();
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
			
//			protected void onPostExecute(String lockers) {
//			
//			NewsAdapter adapter = new NewsAdapter(getActivity(),lockers);
//			Madapter = new MergeAdapter();
//			Madapter.addAdapter(adapter);
//			items = new ArrayList<String>();
//			items.add("More News");
//			//Madapter.addAdapter(new OptionAdapter(getActivity(), R.layout.morenewsheading, items));
//			
//			
//			getListView().setAdapter(Madapter);
//
//}


	
			
			
		
	}
	
	 
	 public String exchangeHashKey() throws JSONException, ClientProtocolException, IOException{
		 	//client = AndroidHttpClient.newInstance("PoliceNewsApp");
		 	String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
		 	String deviceID = HttpClientInfo.getMD5(macAddss);

		 	client = new DefaultHttpClient();
	 		HttpPost post = new HttpPost(HttpClientInfo.URL);
		 	JSONObject postObj = new JSONObject();
//	 		postObj.put("HashTag",HASH_TAG);
	 		postObj.put("Update", "true");
	 		postObj.put("DeviceID", deviceID);
	 		Log.i("DEVICE_ID ::: ",deviceID);
//	 		postObj.put("NewsStory", getNewsHash("NewsStoryHash"));
//	 		postObj.put("UCVideos", getNewsHash("UCVideosHash"));
//	 		postObj.put("Calendar", getNewsHash("CalendarHash"));
//	 		Log.i("PPMobile", "Sending NEWS_STORY_HASH: "+getNewsHash("NewsStoryHash"));
//	 		Log.i("PPMobile", "Sending UC_VIDEOS_HASH: "+getNewsHash("UCVideosHash"));
//	 		Log.i("PPMobile", "Sending CALENDAR_HASH: "+getNewsHash("CalendarHash"));
	 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
	 		post.setHeader("Content-Type","application/json");
	 		post.setHeader("Accept-Encoding","application/json");
	 		post.setHeader("Accept-Language","en-US");
	 		Log.i("PPMobile", "Attempting to connecto to host "+URL);
	 		HttpResponse res = client.execute(post);
	 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			res.getEntity().writeTo(os);
			return os.toString();
	 }
	 
	 
	 public String getJSONData() throws JSONException, ClientProtocolException, IOException{
		 	String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
		 	String deviceID = HttpClientInfo.getMD5(macAddss);

		 	client = new DefaultHttpClient();
	 		HttpPost post = new HttpPost(HttpClientInfo.URL);
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
	 		//postObj.put("UC_Video_Hash", value);
	 		Log.e("POSTING_THIS", jArray.toString());
	 		Log.e("PUSHING_THIS",postObj.toString());
	 		

	 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
	 		post.setHeader("Content-Type","application/json");
	 		post.setHeader("Accept-Encoding","application/json");
	 		post.setHeader("Accept-Language","en-US");
	 		Log.i("PHILLY_POLICE_SERVICE", "Attempting to connecto to host "+URL);
	 		HttpResponse res = client.execute(post);
	 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			res.getEntity().writeTo(os);
			return os.toString();
	 }
	 
	 @SuppressLint("NewApi") 
	 private void getMyUpdates() throws ClientProtocolException, JSONException, IOException {
		 
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
									//PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
									
									
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