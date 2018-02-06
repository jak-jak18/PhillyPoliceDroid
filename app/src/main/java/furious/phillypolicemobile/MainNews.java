	package furious.phillypolicemobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainNews extends Fragment {
	ArrayList<NewsObject> listofMainNews;
	//NewsShortMainAdapter mainNewsAdapter;
	NewsAdapter newsAdapter;
	DistrictListAdapter adapter;
	ArrayList<String> arrayofNews;
	TextView footText;
	ArrayAdapter<String> newsArrayAdapter;
	ProgressBar progress;
	ProgressBar progress1;
	TextView Ftitle;
	TextView waitText;
	ScrollView scroll;
	HttpClient client;
	View moreNewsBtt;
	TextView errorText;
	RelativeLayout footer;
	
    TextView headerTxt;
    int Srt = 0;
    int End = 3;
	int TOTAL_COUNT = 0;
	ListView listView;
	private boolean isNoMore;

	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent service = new Intent(getActivity(),PoliceUpdateService.class);
        service.putExtra("PoliceServiceCode", 200);
        getActivity().startService(service);
        
     //   isWiFiOn();
        

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       
    	View layout = inflater.inflate(R.layout.activity_main, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) layout.findViewById(R.id.MainActivityListView);
        progress = (ProgressBar) layout.findViewById(R.id.HeaderLoadMore);
        waitText = (TextView) layout.findViewById(R.id.pleaseWaitText);
        headerTxt = (TextView) layout.findViewById(R.id.MainNewsListTextView);
        errorText = (TextView) layout.findViewById(R.id.MainConnErr1);
        
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
		
				
				
					if(view.findViewById(R.id.MainNewsListTextView) != null){
						//Toast.makeText(getActivity(), "the Bottom", Toast.LENGTH_LONG).show();

						if(isNoMore){
							Toast.makeText(getActivity(), "No more news", Toast.LENGTH_SHORT).show();
						}else{

							progress1 = (ProgressBar) view.findViewById(R.id.progressBar1);
							footText = (TextView) view.findViewById(R.id.MainNewsListTextView);
							progress1.setVisibility(View.VISIBLE);
							footText.setVisibility(View.INVISIBLE);

							new getMoreNews().execute();
						}

						
					}else{
						
						NewsObject lObj = (NewsObject) parent.getItemAtPosition(position);	
						
						String vidURL = lObj.getVideoURL();
						String desc = lObj.getStoryExcert();
						String storyTil = lObj.getStoryTitle();
						String imgURL = lObj.getCaptionURL();
						String imgID = lObj.getID();
						
						Intent policeNews = new Intent(getActivity(), PoliceNews.class);
						policeNews.putExtra("URL", vidURL);
						policeNews.putExtra("Description", desc);
						policeNews.putExtra("StoryTitle", storyTil);
						policeNews.putExtra("ImageURL", imgURL);
						policeNews.putExtra("StoryID", imgID);
						policeNews.putExtra("isUCVid", false);
						policeNews.putExtra("isAlrBk", false);
		            	startActivity(policeNews);
		            	
					}

				
			}
        	
        });
        
        
        return layout;
 }
    
    @Override
 	public void onActivityCreated(Bundle savedState) {
 	    super.onActivityCreated(savedState);
 	   new getZeroNews().execute();
 	   
    }
    
    class getMoreNews extends AsyncTask<String, Void, ArrayList<NewsObject>>{

		@Override
		protected ArrayList<NewsObject> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			try {
				
//				Srt = Srt+3;
//				End = End+3;
				
				String data = null;
				if((TOTAL_COUNT - listofMainNews.size()) <=3){
					data = getListData(HttpClientInfo.URL,listofMainNews.size(),TOTAL_COUNT);
				}else if((TOTAL_COUNT - listofMainNews.size()) > 3){
					data = getListData(HttpClientInfo.URL,listofMainNews.size(),3);
				}
				
				
				//Log.i("DATa returned", data);
					if(data.equals("No Data Connection") || data.equals(null)){
						Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
					}else{
						
						JSONObject jObj = new JSONObject(data);
						JSONArray jArray = jObj.getJSONArray("News");
						TOTAL_COUNT = jObj.getInt("TotalCount");
						int count = jArray.length();
						//listofMainNews = new ArrayList<NewsObject>();
							
						for(int i=0;i<count;i++){
							
							JSONObject daObj = jArray.getJSONObject(i);
							NewsObject nObj = new NewsObject();
							nObj.setAlertType(daObj.getString("AlertType"));
							nObj.setStoryDate(daObj.getString("StoryDate"));
							nObj.setAuthor(daObj.getString("StoryAuthor"));
							nObj.setStoryTitle(daObj.getString("StoryTitle"));
							nObj.setStoryExcert(daObj.getString("StoryExcert"));
							nObj.setCaptionURL(daObj.getString("ImageURL"));
							nObj.setVideoURL(daObj.getString("TubeURL"));
							nObj.setID(daObj.getString("StoryID"));
							listofMainNews.add(nObj);
						}
						
					}
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("LOG_TAG", "Connection Error", e);
				e.printStackTrace();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			  finally{
			  
				  client.getConnectionManager().shutdown();
			}
			
			
			
			return listofMainNews;
		}
		
		protected void onPostExecute(final ArrayList<NewsObject> news_short_Objs) {

			newsAdapter.updateList(news_short_Objs);

			if(TOTAL_COUNT == news_short_Objs.size()){

				Ftitle.setText("No More News");
				progress1.setVisibility(View.INVISIBLE);
				footText.setVisibility(View.VISIBLE);
				isNoMore = true;

			}else{

				Ftitle.setText("More News "+"( "+listofMainNews.size()+" of "+TOTAL_COUNT+" )");
				progress1.setVisibility(View.INVISIBLE);
				footText.setVisibility(View.VISIBLE);
				isNoMore = false;
			}

			

					
		}
    	
		
		
    }
    

    
    class getZeroNews extends AsyncTask<String, Void, ArrayList<NewsObject>>{

		@SuppressLint("NewApi")
		@Override
		protected ArrayList<NewsObject> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			try {
				String data = getListData(HttpClientInfo.URL,Srt,End);
				//Log.i("DATa returned", data);
					if(data.equals("No Data Connection") || data.isEmpty()){
						Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
					}else{
						
						JSONObject jObj = new JSONObject(data);
						JSONArray jArray = jObj.getJSONArray("News");
						TOTAL_COUNT = jObj.getInt("TotalCount");
						int count = jArray.length();
						listofMainNews = new ArrayList<NewsObject>();
							
						for(int i=0;i<count;i++){
								
							JSONObject daObj = jArray.getJSONObject(i);
							NewsObject nObj = new NewsObject();
							nObj.setAlertType(daObj.getString("AlertType"));
							nObj.setStoryDate(daObj.getString("StoryDate"));
							nObj.setAuthor(daObj.getString("StoryAuthor"));
							nObj.setStoryTitle(daObj.getString("StoryTitle"));
							nObj.setStoryExcert(daObj.getString("StoryExcert"));
							nObj.setCaptionURL(daObj.getString("ImageURL"));
							nObj.setVideoURL(daObj.getString("TubeURL"));
							nObj.setID(daObj.getString("StoryID"));
							listofMainNews.add(nObj);
						}
					
					}
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("LOG_TAG", "Connection Error", e);
				e.printStackTrace();
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			  finally{
			  
				  client.getConnectionManager().shutdown();
			}
			
			
			
			return listofMainNews;
		}
		
		protected void onPostExecute(final ArrayList<NewsObject> news_short_Objs) {
			
			
				newsAdapter = new NewsAdapter(getActivity(), news_short_Objs);
//				headerTxt.setText("More News "+"( "+End+" of "+TOTAL_COUNT+" )");
				
					if(TOTAL_COUNT == news_short_Objs.size()){
						listView.addFooterView(addTheFooter("No More News"));
						listView.setAdapter(newsAdapter);
//						footer.setVisibility(View.VISIBLE);
						progress.setVisibility(View.GONE);
						waitText.setVisibility(View.INVISIBLE);
						isNoMore = true;
					}else{
						listView.addFooterView(addTheFooter("More News "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )"));
						listView.setAdapter(newsAdapter);
//						footer.setVisibility(View.VISIBLE);
						progress.setVisibility(View.GONE);
						waitText.setVisibility(View.INVISIBLE);
						isNoMore = false;
					}
					
					
		}
    	
		
		
    }
    


    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // do whatever
//            	Intent intent = new Intent(MainActivity.this, PreferenceFragment.class);
//            	startActivity(intent);
//                return true;
//               
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
    
    private String getListData(String uRL, int srt, int end) throws JSONException, UnsupportedEncodingException{
    	
    	String finalStr = null;
    	HttpParams httpParams = new BasicHttpParams();
    	int some_reasonable_timeout = (int) (30 * DateUtils.SECOND_IN_MILLIS);
    	HttpConnectionParams.setConnectionTimeout(httpParams, some_reasonable_timeout);
    	HttpConnectionParams.setSoTimeout(httpParams, some_reasonable_timeout);
    	
		client = new DefaultHttpClient(httpParams);
		HttpPost clientRequest = new HttpPost(uRL);
		
		
		String macAddress = HttpClientInfo.getMacAddress(getActivity());
		String deviceID = HttpClientInfo.getMD5(macAddress);
		JSONObject postObj = new JSONObject();
 		postObj.put("LatestNews", "true");
 		postObj.put("DeviceID", deviceID);
 		postObj.put("Start", srt);
 		postObj.put("End", end);
 		
 		clientRequest.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
 		clientRequest.setHeader("Content-Type","application/json");
 		clientRequest.setHeader("Accept-Encoding","application/json");
 		clientRequest.setHeader("Accept-Language","en-US");
 		
 		HttpResponse response;
		try {
			response = client.execute(clientRequest);
			HttpEntity httpEnt = response.getEntity();
				if(httpEnt == null){
					Log.i("LOG SAID", "NO Conection");
					return "NO CONNECTION";
					
				}
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
 				ByteArrayOutputStream os = new ByteArrayOutputStream(); 
 				response.getEntity().writeTo(os); 
 				String responseString = os.toString();
 				finalStr = responseString;
 			}else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_GATEWAY){
 				finalStr = "Server Timeout";
 			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
	//		Toast.makeText(getActivity(), "No Data Connection", Toast.LENGTH_LONG).show();
			//e.printStackTrace();
		}
		
 			return finalStr;	

	}
    
//    private View Header(String string) {
//    	LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    	View k = inflater.inflate(R.layout.news_more_header, null);
//    //	View k = getActivity().getLayoutInflater().inflate(R.layout.news_more_header, null);
//    	TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
//    	title.setText(string);
//    	return k;
//	}
    
    private View addTheFooter(String string){
    	LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View k = inflater.inflate(R.layout.main_news_more, null);
    //	View k = getActivity().getLayoutInflater().inflate(R.layout.news_more_header, null);
    	Ftitle = (TextView) k.findViewById(R.id.MainNewsListTextView);
    	Ftitle.setText(string);
    	return k;	
    }
    
    
    
    
    @Override
    public void onResume() {
       Log.i("PPD_MAIN_NEWS_FRAGMENT", "onResume()");
       super.onResume();
     //  new getZeroNews().execute();
    }

    @Override
    public void onPause() {
    	Log.i("PPD_MAIN_NEWS_FRAGMENT", "onPause()");
      super.onPause();
    }
    
    
}
