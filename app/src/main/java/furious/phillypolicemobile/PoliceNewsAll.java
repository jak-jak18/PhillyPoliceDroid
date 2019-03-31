package furious.phillypolicemobile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PoliceNewsAll extends Activity{
	ListView listview;
	TextView dateView;
	TextView loading;
	String timeStamp;
	String HASH_TAG;
	ArrayList<NewsObject> newsObjs;
	ArrayList<NewsObject> videoObjs;
	int TOTAL_COUNT;
	NewsAdapter newsAdapter;
 	DefaultHttpClient client = new DefaultHttpClient();
 	ProgressBar progress;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police_news_all);
        
        Bundle extras = getIntent().getExtras();
        HASH_TAG = extras.getString("HashTag");
        
        listview = (ListView) findViewById(R.id.PoliceNewsAllListView);
        progress = (ProgressBar) findViewById(R.id.PoliceNewsAllProgress);
        loading = (TextView) findViewById(R.id.textViewLoading);
        new fetchMoreNews().execute(HttpClientInfo.URL);
        dateView = (TextView) findViewById(R.id.textViewDate);
//        Toast.makeText(getApplicationContext(), HASH_TAG, Toast.LENGTH_LONG).show();
        
        
        listview.setOnItemClickListener(new OnItemClickListener(){

			@SuppressLint("NewApi") @Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "Hello you Clicked", Toast.LENGTH_LONG).show();
				if(arg1.findViewById(R.id.MoreListTextViewAll) != null){
//						headerTxt = (TextView) getActivity().findViewById(R.id.MoreListTextView);
//						progressM = (ProgressBar) getActivity().findViewById(R.id.HeaderLoadMore);
//						progressM.setVisibility(View.VISIBLE);
//						headerTxt.setVisibility(View.INVISIBLE);
//						
					Toast.makeText(getApplicationContext(), "Making something", Toast.LENGTH_LONG).show();
					
						//new fetchMoreNews().execute(HttpClientInfo.URL);
					
				}else{
					
					NewsObject lObj = (NewsObject) arg0.getItemAtPosition(arg2);
					boolean isVid = false;
						if(!lObj.getVideoURL().isEmpty()){
							isVid = true;
						}
					Intent policeNews = new Intent(PoliceNewsAll.this,PoliceNews.class);
					Bundle bundle = new Bundle();
					bundle.putString("URL", lObj.getVideoURL());
					bundle.putString("Description", lObj.getStoryExcert());
					bundle.putString("StoryTitle", lObj.getStoryTitle());
					bundle.putString("ImageURL", lObj.getCaptionURL());
					bundle.putString("StoryID", lObj.getID());
					bundle.putBoolean("isVideo", isVid);
					bundle.putBoolean("isUCVid", lObj.isUC_Vid());
					policeNews.putExtras(bundle);
					
					startActivity(policeNews);

				}
				
			}
        	
        });

    }
	
	@Override
    public void onStart(){
    	super.onStart();
    	
    }
	
	public class fetchMoreNews extends AsyncTask<String, Void, ArrayList<NewsObject>>{

		ArrayList<NewsObject> finalArray;
		
		@Override
		protected ArrayList<NewsObject> doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				
				String rdata = getListData(HASH_TAG);
				JSONObject jObj = new JSONObject(rdata);
				String NewsCt = jObj.getString("NewsTotalCount");
				String VideoCt = jObj.getString("VideoTotalCount");
				timeStamp = jObj.getString("TimeStamp");
				
				newsObjs = new ArrayList<NewsObject>();
				videoObjs = new ArrayList<NewsObject>();
				finalArray = new ArrayList<NewsObject>();
				
				
				
				
					if(Integer.valueOf(NewsCt) >=1){
						
						JSONArray objectArray = jObj.getJSONArray("NewsStories");
						int count = objectArray.length();
						
						for(int i=0;i<count;i++){
							
							NewsObject item = new NewsObject();
							JSONObject newobject = objectArray.getJSONObject(i);
							item.setID(newobject.getString("NewsID"));
							item.setDistrictNumber(newobject.getString("District"));
							item.setStoryExcert(newobject.getString("Description"));
							item.setStoryDate(newobject.getString("PubDate"));
							item.setCaptionURL(newobject.getString("ImageURL"));
							item.setStoryTitle(newobject.getString("Title"));
							item.setVideoURL(newobject.getString("TubeURL"));
							item.setAlertType(newobject.getString("Category"));
							item.setAuthor(newobject.getString("Author"));
							newsObjs.add(item);
						}
					}
					
					if(Integer.valueOf(VideoCt) >=1){
						
						JSONArray objectArray = jObj.getJSONArray("VideoObjects");
						int count = objectArray.length();
							
							for(int i=0;i<count;i++){
								
								NewsObject vObj = new NewsObject();
								JSONObject vidObj = objectArray.getJSONObject(i);
								vObj.setID(vidObj.getString("ID"));
								vObj.setStoryExcert(vidObj.getString("Description"));
								vObj.setStoryDate(vidObj.getString("VideoDate"));
								vObj.setCaptionURL(vidObj.getString("VideoImageURL"));
								vObj.setStoryTitle(vidObj.getString("VideoTitle"));
								vObj.setVideoURL(convertTube(vidObj.getString("VideoID")));
								vObj.setAlertType(vidObj.getString("CrimeType"));
								vObj.setAuthor("Phila PD");
								vObj.setUC_Vid(true);
								videoObjs.add(vObj);
							}
						
						
					}
					
						if(!videoObjs.isEmpty()){
							//finalArray.addAll(videoObjs);
							for(int i=0;i<videoObjs.size();i++){
								finalArray.add(videoObjs.get(i));
							}

						}
					
						if(!newsObjs.isEmpty()){
							//finalArray.addAll(newsObjs);
							for(int i=0;i<newsObjs.size();i++){
								finalArray.add(newsObjs.get(i));
							}
						}
						
				
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			return finalArray;
		}
		
		protected void onPostExecute(ArrayList<NewStoryObject> newsSty){
			
			dateView.setText(timeStamp);
			newsAdapter = new NewsAdapter(getApplicationContext(), newsSty);
			listview.setAdapter(newsAdapter);
			progress.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			
		}
		 
	 }
	
	private String convertTube(String src){
		
		String newURL = null;
		String root = "https://www.youtube.com/watch?v=";
		newURL = root+src;
		
		return newURL;
	}
	
	@SuppressLint("NewApi") 
	private JSONArray getDistricts(){

	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    Set<String> a = pref.getStringSet("district_preference", null);
    JSONArray jArray = new JSONArray();
    		
    		for (String str: a) {
	        	jArray.put(str);
	        }
	
    	return jArray;	
    }
	 
	
	private String getListData(String Dist_Num) throws ClientProtocolException, IOException, JSONException{
		 
	 	
		String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
	 	String deviceID = HttpClientInfo.getMD5(macAddss);
	 	//client = AndroidHttpClient.newInstance("ComboNation");
	 	client = new DefaultHttpClient();
 		HttpPost post = new HttpPost(HttpClientInfo.URL);
 		
 		
	 	JSONObject postObj = new JSONObject();
	 	postObj.put("District_Update","true");
 		postObj.put("HashTag", HASH_TAG);
 		postObj.put("DeviceID", deviceID);
 		postObj.put("Districts", getDistricts());
 		Log.i("SENDING TO SERVER", postObj.toString());
 		
 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
 		post.setHeader("Content-Type","application/json");
 		post.setHeader("Accept-Encoding","application/json");
 		post.setHeader("Accept-Language","en-US");
 		
 		HttpResponse res = client.execute(post);
 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		res.getEntity().writeTo(os);
		return os.toString();
			
	}
	 
//	private View Header(String string) {
//    	
//		View k = this.getLayoutInflater().inflate(R.layout.news_more_header_all, null);
//    	TextView title = (TextView) k.findViewById(R.id.MoreListTextViewAll);
//    	title.setText(string);
//    	return k;
//	}
	
}