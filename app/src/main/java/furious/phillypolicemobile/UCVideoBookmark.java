package furious.phillypolicemobile;
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



	ArrayList<NewsStoryBookmarkObject> vidObjs;
	NewsStoryBookmarkAdapter adapter;
	TextView noBookmark;
	int TOTAL_COUNT;
	String JSON_DATA;
	String CLICKED;
	int CLICKED_VID;
	ProgressBar progress;
	HttpURLConnection httpcon;
	
	
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


     return layout;	        
	
    }
	
	
	public class fetchBokmarks extends AsyncTask<String, Void, ArrayList<NewsStoryBookmarkObject>>{
		 
		 @Override
		    protected void onPreExecute() {
			 super.onPreExecute();	


		 }
		 
			@Override
			protected ArrayList<NewsStoryBookmarkObject> doInBackground(String... params) {

		 	vidObjs = new ArrayList<NewsStoryBookmarkObject>();
				
					try{
						
						String Data = getBookmarkListData();
						Log.i("UCVID_PASS",Data);
						JSONObject object = new JSONObject(Data);
						JSONObject bookMarks = object.getJSONObject("Bookmarks");
						JSONArray uc_vid_Array = bookMarks.getJSONArray("UCVideos");

						int uc_vid_count = uc_vid_Array.length();

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
									catch (IOException e) {e.printStackTrace();}
									catch (JSONException e) {e.printStackTrace();}

				
				return vidObjs;
			}
			
				protected void onPostExecute(ArrayList<NewsStoryBookmarkObject> lockers) {
					
					adapter = new NewsStoryBookmarkAdapter(getActivity(),lockers);

					if(lockers.size() <= 0){
							noBookmark.setVisibility(View.VISIBLE);
						}else{
							String ct = Integer.toString(TOTAL_COUNT);

							if(TOTAL_COUNT == lockers.size()){
								View title = Header("No More News "+"( "+lockers.size()+" of "+TOTAL_COUNT+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);

							}else{
								View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);

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

		}
		 
			@Override
			protected String doInBackground(String... params) {
				
				String data = null;
				try {
					
					data = deleteListData();
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
	
	
	
	
	

	
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  //  menu.add(5, 1, 0, "Add");
	    menu.add(5, 2, 0, "Delete Video Bookmark");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
	    //only this fragment's context menus have group ID of -1
		
	    if (item.getGroupId() == 5) {
	        switch(item.getItemId()) {
	        	case 1:
	        		
	       
	        	break;
	        
	        	case 2: 

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
			TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
			title.setText(string);
			return k;
		}
		  
		  private String deleteListData() throws IOException, JSONException{
				 
			 	String macAddss = HttpClientInfo.getMacAddress(getActivity());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);

			  String result = null;

			  try {
		 		
			 	JSONObject postObj = new JSONObject();
		 		postObj.put("Bookmark", "true");
		 		postObj.put("DeviceID", deviceID);
		 		postObj.put("BookmarkRemove", "true");
		 		postObj.put("News", "false");
		 		postObj.put("Video", "true");
		 		postObj.put("VideoID", CLICKED);
		 		String data = postObj.toString();

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
		  

		private String getBookmarkListData() throws IOException, JSONException{

				String macAddss = HttpClientInfo.getMacAddress(getActivity());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);

				String result = null;

			 	try{
		 		
			 	JSONObject postObj = new JSONObject();
		 		postObj.put("Bookmark", "true");
		 		postObj.put("DeviceID", deviceID);
		 		postObj.put("Bookmark_UCVideos", "true");
		 		postObj.put("Bookmark_NewsStory", "false");
		 		String data = postObj.toString();


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




    
    
}
