package furious.phillypolicemobile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.widget.TextView;

import android.widget.AdapterView.OnItemClickListener;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;



public class UCVideoBookmark extends ListFragment implements AdapterView.OnItemLongClickListener {

	//ListView list;
//	ProgressDialog pDialog;
	HttpClient client;
	
	//ArrayList<NewsStoryBookmarkObject> newsObjs;
	ArrayList<NewsStoryBookmarkObject> vidObjs;
	NewsStoryBookmarkAdapter adapter;
	TextView noBookmark;
	int TOTAL_COUNT;
	String JSON_DATA;
	String CLICKED;
	int CLICKED_VID;
	ProgressBar progress;
	
	
	static UCVideoBookmark newInstance(String jsonData){
		
		UCVideoBookmark frag = new UCVideoBookmark();
        Bundle args = new Bundle();
        args.putString("JSON_DATA", jsonData);
        frag.setArguments(args);

        return frag;
    }
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //JSON_DATA = this.getArguments().getString("JSON_DATA");
        
//        noBookmark = (TextView) findViewById(R.id.BookmarkNoView1);
//        list = (ListView) findViewById(R.id.bookmarkListView);
        new fetchBokmarks().execute();
        
    }
	
	
	
	@Override
 	public void onActivityCreated(Bundle savedState){
 	    super.onActivityCreated(savedState);
 	    registerForContextMenu(getListView());
 	    
	 	this.getListView().setOnItemLongClickListener(this);
	 	this.getListView().setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
//			if(arg1.findViewById(R.id.MoreListTextView) != null){
//			if(TOTAL_COUNT != newsObjs.size()){
//				footerTxt = (TextView) getActivity().findViewById(R.id.MoreListTextView);
//				progressM = (ProgressBar) getActivity().findViewById(R.id.HeaderLoadMore);
//				progressM.setVisibility(View.VISIBLE);
//				footerTxt.setVisibility(View.INVISIBLE);
//				
//				new fetchMoreNews().execute(HttpClientInfo.URL);
//			}else{
//				Toast.makeText(getActivity(), "No more news at this time", Toast.LENGTH_LONG).show();
//			}
//		}
			
			NewsStoryBookmarkObject lObj = (NewsStoryBookmarkObject) arg0.getItemAtPosition(arg2);
			boolean isVid = false;
				if(!lObj.getVideoURL().equals(0) || !lObj.getVideoURL().equals(null)){
					isVid = true;
				}
				
				Intent policeNews = new Intent(getActivity(),PoliceNews.class);
				Bundle bundle = new Bundle();
				bundle.putString("Description", lObj.getDescription());
				bundle.putString("StoryTitle", lObj.getTitle());
				bundle.putString("URL", lObj.getVideoURL());
				bundle.putString("ImageURL", lObj.getImageURL());
				bundle.putBoolean("isVideo", isVid);
				bundle.putBoolean("isUCVid", false);
				bundle.putBoolean("isAlrBk", true);

				policeNews.putExtras(bundle);
				
				startActivity(policeNews);
			

			
		}});

 	}
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){	
	 super.onCreateView(inflater, container, savedInstanceState);
	 
	 View layout = inflater.inflate(R.layout.bookmark_ucvids, container, false);	
	 noBookmark = (TextView) layout.findViewById(R.id.BookmarkNoView1);
	 progress = (ProgressBar) layout.findViewById(R.id.progressBar1UCVideos);
//	 noNewsTxt = (TextView) layout.findViewById(R.id.NoNewsTxtView);
//	 headerTxt = (TextView) layout.findViewById(R.id.NewsListHeaderTextView);
//	 headerTxt.setText(CVDistrict(DISTRICT)+" District News");
//	 headerTxt.setVisibility(View.VISIBLE);

     return layout;	        
	
    }
	
	
	public class fetchBokmarks extends AsyncTask<String, Void, ArrayList<NewsStoryBookmarkObject>>{
		 
		 @Override
		    protected void onPreExecute() {
			 super.onPreExecute();	
//			 pDialog  = ProgressDialog.show(getApplicationContext(), "Loading Bookmarks...", "Please wait...", false);
//			 pDialog.setCancelable(true);
		 }
		 
			@Override
			protected ArrayList<NewsStoryBookmarkObject> doInBackground(String... params) {
				//newsObjs = new ArrayList<NewsStoryBookmarkObject>();
				vidObjs = new ArrayList<NewsStoryBookmarkObject>();
				
					try{
						
						String Data = getBookmarkListData();
						Log.i("UCVID_PASS",Data);
						JSONObject object = new JSONObject(Data);
						JSONObject bookMarks = object.getJSONObject("Bookmarks");
						JSONArray uc_vid_Array = bookMarks.getJSONArray("UCVideos");
						//JSONArray news_story_Array = bookMarks.getJSONArray("NewsStory");
						
						int uc_vid_count = uc_vid_Array.length();
						//int news_story_count = news_story_Array.length();
								
							for(int i=0;i<uc_vid_count;i++){
									
								NewsStoryBookmarkObject item = new NewsStoryBookmarkObject();
								JSONObject vid_object = uc_vid_Array.getJSONObject(i);
								item.setID(vid_object.getString("VideoID"));
								item.setTitle(vid_object.getString("Title"));
								item.setDescription(vid_object.getString("Description"));
								item.setStoryDate(vid_object.getString("PubDate"));
								item.setImageURL(vid_object.getString("ImageURL"));
								item.setCategory(vid_object.getString("Category"));
								item.setVideoURL(vid_object.getString("TubeURL"));
								item.setDivision(vid_object.getString("Division"));
								vidObjs.add(item);
							}

							

							
								TOTAL_COUNT = object.getInt("TotalCount");
					}	
									catch (ClientProtocolException e) {e.printStackTrace();}
									catch (IOException e) {e.printStackTrace();} 
									catch (JSONException e) {e.printStackTrace();}
					finally {
	 					client.getConnectionManager().shutdown();
	 				}
				
				return vidObjs;
			}
			
				protected void onPostExecute(ArrayList<NewsStoryBookmarkObject> lockers) {
					
					adapter = new NewsStoryBookmarkAdapter(getActivity(),lockers);
					//Madapter = new MergeAdapter();
					//Madapter.addAdapter(adapter);
					//items = new ArrayList<String>();
					//items.add("More News");
					//Madapter.addAdapter(new OptionAdapter(getActivity(), R.layout.morenewsheading, items));
						if(lockers.size() <= 0){
							//pDialog.dismiss();
							//Toast.makeText(getActivity(), "No news at this time", Toast.LENGTH_LONG).show();
							noBookmark.setVisibility(View.VISIBLE);
						}else{
							String ct = Integer.toString(TOTAL_COUNT);
							//String dc = Integer.toString(DISPLAY_COUNT);
							if(TOTAL_COUNT == lockers.size()){
								View title = Header("No More News "+"( "+lockers.size()+" of "+TOTAL_COUNT+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);
								//pDialog.dismiss();
							}else{
								View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);
								//pDialog.dismiss();
							}
							
						}
						
						progress.setVisibility(View.INVISIBLE);
					
							
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		
		NewsStoryBookmarkObject hol = (NewsStoryBookmarkObject) parent.getItemAtPosition(position);
		CLICKED = hol.getID();
		CLICKED_VID = position;
		
		
		
		
		return false;
	}
	
	
	
	public class deleteBookmark extends AsyncTask<String, Void, String>{
		 
		 @Override
		    protected void onPreExecute() {
			 super.onPreExecute();	
//			 pDialog  = ProgressDialog.show(getApplicationContext(), "Loading Bookmarks...", "Please wait...", false);
//			 pDialog.setCancelable(true);
		 }
		 
			@Override
			protected String doInBackground(String... params) {
				
				String data = null;
				try {
					
					data = deleteListData();
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
				
				
				
				return data;
			}
			
				protected void onPostExecute(String jsonData) {
					
					JSONObject jsonObj;
					try {
						
						jsonObj = new JSONObject(jsonData);
						String isErr = jsonObj.getString("error");
						
						if(isErr.equals("false")){
							//Toast.makeText(getActivity(), jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
							vidObjs.remove(CLICKED_VID);
							adapter.notifyDataSetChanged();
						}else if(isErr.equals("true")){
							Log.e("NETWORK_ERROR", jsonObj.getString("msg"));
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
							
		}
		
	}
	
	
	
	
	
	
	
//	 @Override
//		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//		      super.onCreateContextMenu(menu, v, menuInfo);
//		      
//		          MenuInflater inflater = getActivity().getMenuInflater();
//		          inflater.inflate(R.menu.bookmark, menu);
//		      
//		}
//		
//		@Override
//		public boolean onContextItemSelected(MenuItem item) {
//
//		    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//		    switch (item.getItemId()) {
//
//		        case R.id.add: // <-- your custom menu item id here
//		            // do something here
//		            return true;
//		            
//		        case R.id.delete: // <-- your custom menu item id here
//		            // do something here
//		        	
//		            //Toast.makeText(getActivity(), CLICKED, Toast.LENGTH_LONG).show();
//		            Log.e("THIS_ITEM HERE","HAS BEEN CLICKEED");
//		            return true;
//		            
//		        case R.id.edit: // <-- your custom menu item id here
//		            // do something here
//		            return true;
//
//		        default:
//		            return super.onContextItemSelected(item);
//		    }
//		}
	
	
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  //  menu.add(5, 1, 0, "Add");
	    menu.add(5, 2, 0, "Delete Video Bookmark");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
	    //only this fragment's context menus have group ID of -1
		
	    if (item.getGroupId() == 5) {
	        switch(item.getItemId()) {
	        	case 1: 
	        	//doSomething(); 
	        		
	       
	        	break;
	        
	        	case 2: 
	        	//doSomethingElse(); 
	        		//Log.i("CLICKED_HERE","YOUCLICKED 2");
	        		//Toast.makeText(getActivity(), CLICKED, Toast.LENGTH_LONG).show();
	        		new deleteBookmark().execute();
	        	
	        	break;
	        	
	        	
	        }
	    }
		
	    return false;
	}
	
	
		  @SuppressLint("InflateParams")
		private View Header(String string) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View k = inflater.inflate(R.layout.news_more_header, null);
		//	View k = getActivity().getLayoutInflater().inflate(R.layout.news_more_header, null);
			TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
			title.setText(string);
			return k;
		}
		  
		  private String deleteListData() throws ClientProtocolException, IOException, JSONException{
				 
			 	//client = AndroidHttpClient.newInstance("ComboNation");
				String macAddss = HttpClientInfo.getMacAddress(getActivity());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);
			 	client = new DefaultHttpClient();
		 		HttpPost post = new HttpPost(HttpClientInfo.URL);
		 		
			 	JSONObject postObj = new JSONObject();
		 		postObj.put("Bookmark", "true");
		 		postObj.put("DeviceID", deviceID);
		 		postObj.put("BookmarkRemove", "true");
		 		postObj.put("News", "false");
		 		postObj.put("Video", "true");
		 		postObj.put("VideoID", CLICKED);
		 				
		 		
		 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
		 		post.setHeader("Content-Type","application/json");
		 		post.setHeader("Accept-Encoding","application/json");
		 		post.setHeader("Accept-Language","en-US");
		 		
		 		HttpResponse res = client.execute(post);
		 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
				res.getEntity().writeTo(os); 
				
//				HttpClient android = new DefaultHttpClient();
//				HttpGet clientRequest = new HttpGet(uRL);
//				HttpResponse response = android.execute(clientRequest);
		//
//				ByteArrayOutputStream os = new ByteArrayOutputStream(); 
//				response.getEntity().writeTo(os); 
//				String responseString = os.toString();
//				
//				return responseString;
				
				return os.toString();
				
				
			}
		  

		private String getBookmarkListData() throws ClientProtocolException, IOException, JSONException{
				 
			 	//client = AndroidHttpClient.newInstance("ComboNation");
				String macAddss = HttpClientInfo.getMacAddress(getActivity());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);
			 	client = new DefaultHttpClient();
		 		HttpPost post = new HttpPost(HttpClientInfo.URL);
		 		
			 	JSONObject postObj = new JSONObject();
		 		postObj.put("Bookmark", "true");
		 		postObj.put("DeviceID", deviceID);
		 		postObj.put("Bookmark_UCVideos", "true");
		 		postObj.put("Bookmark_NewsStory", "false");
		 				
		 		
		 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
		 		post.setHeader("Content-Type","application/json");
		 		post.setHeader("Accept-Encoding","application/json");
		 		post.setHeader("Accept-Language","en-US");
		 		
		 		HttpResponse res = client.execute(post);
		 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
				res.getEntity().writeTo(os); 
				
//				HttpClient android = new DefaultHttpClient();
//				HttpGet clientRequest = new HttpGet(uRL);
//				HttpResponse response = android.execute(clientRequest);
		//
//				ByteArrayOutputStream os = new ByteArrayOutputStream(); 
//				response.getEntity().writeTo(os); 
//				String responseString = os.toString();
//				
//				return responseString;
				
				return os.toString();
				
				
			}






//	@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
    
//    @Override
//    protected void onDestroy() {
//
//          super.onDestroy();
//    }
    
    
    
    
}
