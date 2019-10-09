package furious.viewfragments.bookmark;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import furious.dataobjs.NewsStoryBookmarkObject;
import furious.objadapters.NewsStoryBookmarkAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;

import static android.app.Activity.RESULT_OK;


public class UCVideoBookmark extends ListFragment{



	ArrayList<NewsStoryBookmarkObject> vidObjs;
	NewsStoryBookmarkAdapter adapter;
	TextView noBookmark;
	int TOTAL_COUNT;
	ProgressBar progress;
	HttpURLConnection httpcon;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        new fetchBokmarks().execute();
        
    }
	
	
	
	@Override
 	public void onActivityCreated(Bundle savedState){
 	    super.onActivityCreated(savedState);

	 	this.getListView().setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			if (arg1.findViewById(R.id.MoreListTextView) != null) {
				TextView ismore = (TextView) arg1.findViewById(R.id.MoreListTextView);
				if (!ismore.getText().equals("No More Bookmarks")) {
					new fetchMoreBokmarks().execute();

				} else {
					Toast.makeText(getActivity(), "Need top write more code", Toast.LENGTH_SHORT).show();
				}

			}else{

				NewsStoryBookmarkObject lObj = (NewsStoryBookmarkObject) arg0.getItemAtPosition(arg2);
				boolean isVid = false;
				if(!lObj.getVideoURL().equals(0) || !lObj.getVideoURL().equals(null)){
					isVid = true;
				}

				View v = arg1.findViewById(R.id.BookmarkImageView);
				v.setDrawingCacheEnabled(true);
				Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
				v.setDrawingCacheEnabled(false);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();

				Intent policeNews = new Intent(getActivity(),PoliceNews.class);
				Bundle bundle = new Bundle();
				bundle.putString("Description", lObj.getDescription());
				bundle.putString("StoryTitle", lObj.getTitle());
				bundle.putString("URL", lObj.getVideoURL());
				bundle.putString("ImageURL", lObj.getImageURL());
				bundle.putString("CrimeType", lObj.getCategory());
				bundle.putString("DistrictNumber", lObj.getDistrict());
				bundle.putString("ParentActivity", "UCVideoBookmark");
				bundle.putBoolean("isVideo", isVid);
				bundle.putBoolean("isUCVid", true);
				bundle.putString("UCVideoID", lObj.getID());

				policeNews.putExtra("VictimImage", byteArray);
				policeNews.putExtras(bundle);

				startActivityForResult(policeNews, 2222);

			}



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

	public class fetchMoreBokmarks extends AsyncTask<String, Void, ArrayList<NewsStoryBookmarkObject>>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//progress.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<NewsStoryBookmarkObject> doInBackground(String... params) {

			try{

				String Data = null;
				if((TOTAL_COUNT - vidObjs.size()) <= 5){
					Data = getBookmarkListData(vidObjs.size(),TOTAL_COUNT);
				}else if((TOTAL_COUNT - vidObjs.size()) > 5){
					Data = getBookmarkListData(vidObjs.size(),5);

				}


				Log.i("UCVID_GOT",Data);
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
					item.setDistrict(vid_object.getString("District"));
					vidObjs.add(item);
				}


				//TOTAL_COUNT = object.getInt("TotalCount");
			}
			catch (IOException e) {e.printStackTrace();}
			catch (JSONException e) {e.printStackTrace();}


			return vidObjs;
		}

		protected void onPostExecute(ArrayList<NewsStoryBookmarkObject> lockers) {

			adapter.updateList(lockers);

				String ct = Integer.toString(TOTAL_COUNT);

				if(TOTAL_COUNT == lockers.size()){
					TextView tit = (TextView) getListView().findViewById(R.id.MoreListTextView);
					tit.setText("No More Bookmarks");

				}else{

					TextView tit = (TextView) getListView().findViewById(R.id.MoreListTextView);
					tit.setText("More Bookmarks "+"( "+lockers.size()+" of "+ct+" )");

				}



			progress.setVisibility(View.INVISIBLE);


		}

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
						
						String Data = getBookmarkListData(0,5);
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
								item.setDistrict(vid_object.getString("District"));
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

                        if (lockers.size() == 1) {
                            getListView().setAdapter(adapter);

                        }else if(TOTAL_COUNT == lockers.size()){
								View title = Header("No More Bookmarks");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);

                        }else{
								View title = Header("More Bookmarks "+"( "+lockers.size()+" of "+ct+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);

                        }
							
					}
						
						progress.setVisibility(View.INVISIBLE);
					
							
		}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


		if (requestCode == 2222 && resultCode == RESULT_OK) {

			vidObjs.remove(data.getIntExtra("ItemPosition", 0));
			adapter.notifyDataSetChanged();
			TextView txt = (TextView) getListView().findViewById(R.id.MoreListTextView);
			int NT = --TOTAL_COUNT;
			txt.setText("More News " + "( " + vidObjs.size() + " of " + NT + " )");


			Toast.makeText(getActivity(), "Record Deleted", Toast.LENGTH_LONG).show();

			// TODO: Do something with your extra data
		}


	}

	

		private View Header(String string) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View k = inflater.inflate(R.layout.news_more_header, null);
			TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
			title.setText(string);
			return k;
		}
		  

		  

		private String getBookmarkListData(int Start, int End) throws IOException, JSONException{

				String macAddss = HttpClientInfo.getMacAddress(getActivity());
			 	String deviceID = HttpClientInfo.getMD5(macAddss);

				String result = null;

			 	try{
		 		
			 	JSONObject postObj = new JSONObject();
		 		postObj.put("Bookmark", "true");
		 		postObj.put("DeviceID", deviceID);
		 		postObj.put("Bookmark_UCVideos", "true");
		 		postObj.put("Bookmark_NewsStory", "false");
		 		postObj.put("Start", Start);
		 		postObj.put("End",End);

		 		String data = postObj.toString();
		 		Log.i("UC_PASS",data);


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