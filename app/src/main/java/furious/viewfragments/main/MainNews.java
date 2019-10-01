package furious.viewfragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import furious.dataobjs.NewStoryObject;
import furious.objadapters.NewsAdapter;
import furious.phillypolicemobile.PoliceUpdateService;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.bookmark.PoliceNews;

	public class MainNews extends Fragment {
	ArrayList<NewStoryObject> listofMainNews;
	NewsAdapter newsAdapter;
	TextView footText;
	HttpURLConnection httpcon;
	ProgressBar progress;
	ProgressBar progress1;
	TextView Ftitle;
	TextView Htitle;
	TextView waitText;
	TextView errorText;
	
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

					if(view.findViewById(R.id.MainNewsListTextView) != null){

						if(isNoMore){
							//Toast.makeText(getActivity(), "No more news", Toast.LENGTH_SHORT).show();
						}else{

							progress1 = (ProgressBar) view.findViewById(R.id.progressBar1);
							footText = (TextView) view.findViewById(R.id.MainNewsListTextView);
							progress1.setVisibility(View.VISIBLE);
							footText.setVisibility(View.INVISIBLE);

							new getMoreNews().execute();
						}

						
					}else{
						
						NewStoryObject lObj = (NewStoryObject) parent.getItemAtPosition(position);

						View v = view.findViewById(R.id.DistrictNewsImageView);
						v.setDrawingCacheEnabled(true);
						Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
						v.setDrawingCacheEnabled(false);
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
						byte[] byteArray = stream.toByteArray();

						String vidURL = lObj.getTubeURL();
						String desc = lObj.getDescription();
						String storyTil = lObj.getTitle();
						String imgURL = lObj.getImageURL();
						String imgID = lObj.getNewsStoryID();

						
						Intent policeNews = new Intent(getActivity(), PoliceNews.class);
						policeNews.putExtra("URL", vidURL);
						policeNews.putExtra("Description", desc);
						policeNews.putExtra("StoryTitle", storyTil);
						policeNews.putExtra("ImageURL", imgURL);
						policeNews.putExtra("StoryID", imgID);
						policeNews.putExtra("ParentActivity", "MainNews");
						policeNews.putExtra("isUCVid", false);
						policeNews.putExtra("isAlrBk", false);
						policeNews.putExtra("VictimImage", byteArray);
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
    
     class getMoreNews extends AsyncTask<String, Void, ArrayList<NewStoryObject>>{

		@Override
		protected ArrayList<NewStoryObject> doInBackground(String... arg0) {
			
			try {
				
				String data = null;
				if((TOTAL_COUNT - listofMainNews.size()) <=3){
					data = getListData(HttpClientInfo.URL,listofMainNews.size(),TOTAL_COUNT);
				}else if((TOTAL_COUNT - listofMainNews.size()) > 3){
					data = getListData(HttpClientInfo.URL,listofMainNews.size(),3);
				}
				

					if(data.equals("No Data Connection") || data.equals(null)){
						Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
					}else{
						
						JSONObject jObj = new JSONObject(data);
						JSONArray jArray = jObj.getJSONArray("News");
						TOTAL_COUNT = jObj.getInt("TotalCount");
						int count = jArray.length();
							
						for(int i=0;i<count;i++){
							
							JSONObject daObj = jArray.getJSONObject(i);
							NewStoryObject nObj = new NewStoryObject();
							nObj.setCategory(daObj.getString("Category"));
							nObj.setPubDate(daObj.getString("PubDate"));
							nObj.setStoryAuthor(daObj.getString("StoryAuthor"));
							nObj.setTitle(daObj.getString("Title"));
							nObj.setDescription(daObj.getString("Description"));
							nObj.setImageURL(daObj.getString("ImageURL"));
							nObj.setTubeURL(daObj.getString("TubeURL"));
							nObj.setNewsStoryID(daObj.getString("NewsStoryID"));
							listofMainNews.add(nObj);
						}
						
					}
	
			} catch (IOException e) {

				Log.e("LOG_TAG", "Connection Error", e);
				e.printStackTrace();
				
			} catch (JSONException e) {

				e.printStackTrace();
			}

			
			
			
			return listofMainNews;
		}
		
		protected void onPostExecute(final ArrayList<NewStoryObject> news_short_Objs) {

			newsAdapter.updateList(news_short_Objs);

			if(TOTAL_COUNT == news_short_Objs.size()){

				Ftitle.setText("No More News");
				progress1.setVisibility(View.INVISIBLE);
				footText.setVisibility(View.VISIBLE);
				isNoMore = true;

			}else if(news_short_Objs == null){
				Toast.makeText(getActivity(),"Hello",Toast.LENGTH_LONG).show();
			}

			else{

				Ftitle.setText("More News "+"( "+listofMainNews.size()+" of "+TOTAL_COUNT+" )");
				progress1.setVisibility(View.INVISIBLE);
				footText.setVisibility(View.VISIBLE);
				isNoMore = false;
			}

			

					
		}
    	

		
    }
    

    
    class getZeroNews extends AsyncTask<String, Void, ArrayList<NewStoryObject>>{

		@Override
		protected ArrayList<NewStoryObject> doInBackground(String... arg0) {
			
			try {
				String data = getListData(HttpClientInfo.URL,Srt,End);

					if(data.equals("No Data Connection") || data.isEmpty() || data.length() == 0){
						Toast.makeText(getActivity(), "No Data", Toast.LENGTH_LONG).show();
					}else{
						
						JSONObject jObj = new JSONObject(data);
						JSONArray jArray = jObj.getJSONArray("News");
						TOTAL_COUNT = jObj.getInt("TotalCount");
						int count = jArray.length();
						listofMainNews = new ArrayList<NewStoryObject>();
							
						for(int i=0;i<count;i++){
								
							JSONObject daObj = jArray.getJSONObject(i);
							NewStoryObject nObj = new NewStoryObject();
							nObj.setCategory(daObj.getString("Category"));
							nObj.setPubDate(daObj.getString("PubDate"));
							nObj.setStoryAuthor(daObj.getString("StoryAuthor"));
							nObj.setTitle(daObj.getString("Title"));
							nObj.setDescription(daObj.getString("Description"));
							nObj.setImageURL(daObj.getString("ImageURL"));
							nObj.setTubeURL(daObj.getString("TubeURL"));
							nObj.setNewsStoryID(daObj.getString("NewsStoryID"));
							listofMainNews.add(nObj);
						}
					
					}
	
			} catch (IOException e) {

				Log.e("LOG_TAG", "Connection Error", e);
				e.printStackTrace();
				
			} catch (JSONException e) {

				Log.e("LOG_TAG", "Connection Error 111", e);
				e.printStackTrace();
			}
			
			
			
			return listofMainNews;
		}
		
		protected void onPostExecute(final ArrayList<NewStoryObject> news_short_Objs) {
			
			
				newsAdapter = new NewsAdapter(getActivity(), news_short_Objs);

					if(TOTAL_COUNT == news_short_Objs.size()){
						listView.addHeaderView(addTheHeader("Latest Police News"));
						listView.addFooterView(addTheFooter("No More News"));
						listView.setAdapter(newsAdapter);
						progress.setVisibility(View.GONE);
						waitText.setVisibility(View.INVISIBLE);
						isNoMore = true;
					}else{
						listView.addHeaderView(addTheHeader("Latest Police News"));
						listView.addFooterView(addTheFooter("More News "+"( "+news_short_Objs.size()+" of "+TOTAL_COUNT+" )"));
						listView.setAdapter(newsAdapter);
						progress.setVisibility(View.GONE);
						waitText.setVisibility(View.INVISIBLE);
						isNoMore = false;
					}
					
					
		}
    	
		
		
    }
    



    private String getListData(String uRL, int srt, int end) throws JSONException, UnsupportedEncodingException{

		String result = null;

		String macAddress = HttpClientInfo.getMacAddress(getActivity());
		String deviceID = HttpClientInfo.getMD5(macAddress);

		try {

		JSONObject postObj = new JSONObject();
		postObj.put("LatestNews", "true");
		postObj.put("DeviceID", deviceID);
		postObj.put("Start", srt);
		postObj.put("End", end);
		String data = postObj.toString();

		//Connect
		httpcon = (HttpURLConnection) ((new URL(uRL).openConnection()));
		httpcon.setDoOutput(true);
		httpcon.setRequestProperty("Content-Type", "application/json");
		httpcon.setRequestProperty("Accept", "application/json");
		httpcon.setRequestProperty("Accept-Language", "en-US");
		httpcon.setRequestMethod("POST");
		httpcon.connect();


		//Write
		OutputStream os = httpcon.getOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
		writer.write(data);
		writer.close();
		os.close();

		//Read
		BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream(), "UTF-8"));

		String line = null;
		StringBuilder sb = new StringBuilder();

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		br.close();
		result = sb.toString();
	}

	catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}

		finally{

			httpcon.disconnect();
		}

		if(result == null){
			result = "000";
		}


		return result;
		


	}

    
    private View addTheFooter(String string){
    	LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View k = inflater.inflate(R.layout.main_news_more, null);
		Ftitle = (TextView) k.findViewById(R.id.MainNewsListTextView);
    	Ftitle.setText(string);
    	return k;	
    }

		private View addTheHeader(String string){
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View k = inflater.inflate(R.layout.main_news_more_1, null);
			Htitle = (TextView) k.findViewById(R.id.MainNewsListTextView2);
			Htitle.setText(string);
			return k;
		}
    
    
    
    
    @Override
    public void onResume() {
       Log.i("PPD_MAIN_NEWS_FRAGMENT", "onResume()");
       super.onResume();
    }

    @Override
    public void onPause() {
    	Log.i("PPD_MAIN_NEWS_FRAGMENT", "onPause()");
      super.onPause();
    }
    
    
}
