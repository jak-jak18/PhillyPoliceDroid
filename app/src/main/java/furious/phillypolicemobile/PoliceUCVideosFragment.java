package furious.phillypolicemobile;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PoliceUCVideosFragment extends ListFragment implements AdapterView.OnItemLongClickListener{

	private String DISTRICT_NUM;
//	private String DISTRICT_DIVISION;
	TextView districtNum;
	TextView districtAddress;
	TextView districtPhone;
	TextView districtEmail;
	TextView captainName;
	TextView headerTxt;
	TextView noVideoTextView;
	ProgressBar progressM;
	LinearLayout table;
	LinearLayout table1;
	Bitmap image;
	ImageView captainImage;
	ArrayList<PoliceUCVideoObject> UC_Obj;
	PoliceUCVideosAdapter adapter;
	private int TOTAL_COUNT;
	
	
	static PoliceUCVideosFragment newInstance(String district) {

		PoliceUCVideosFragment frag = new PoliceUCVideosFragment();
        Bundle args = new Bundle();
        args.putString("DistrictNumber", district);
        frag.setArguments(args);

        return frag;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        DISTRICT_NUM = Uri.encode(this.getArguments().getString("DistrictNumber"));

    }
	
	@Override
 	public void onActivityCreated(Bundle savedState) {
 	    super.onActivityCreated(savedState);

		new getUCVideoList().execute(HttpClientInfo.URL);

 	    this.getListView().setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if(arg1.findViewById(R.id.MoreListUCTextView) != null){
					
					if(TOTAL_COUNT != UC_Obj.size()){

						headerTxt = (TextView) getActivity().findViewById(R.id.MoreListUCTextView);
						progressM = (ProgressBar) getActivity().findViewById(R.id.HeaderLoadMoreUC);
						progressM.setVisibility(View.VISIBLE);
						headerTxt.setVisibility(View.INVISIBLE);

						new fetchMoreVids().execute(HttpClientInfo.URL);
					}else{
						Toast.makeText(getActivity(), "No more news at this time", Toast.LENGTH_LONG).show();
					}
					
				}else{
					
					boolean isVid = false;
					PoliceUCVideoObject lObj = (PoliceUCVideoObject) arg0.getItemAtPosition(arg2);
					
					if(!lObj.getTubeURL().equals(0) || !lObj.getTubeURL().equals(null)){
						isVid = true;
					}
					
					Intent policeNews = new Intent(getActivity(),PoliceNews.class);
					Bundle bundle = new Bundle();
					bundle.putString("Description", lObj.getDescription());
					bundle.putString("StoryTitle", lObj.getVideoTitle());
					bundle.putString("StoryID", lObj.getNewsStoryID());
					bundle.putString("ImageURL", lObj.getVideoImageURL());
					bundle.putString("URL", lObj.getTubeURL());
					bundle.putBoolean("isAlrBk", false);
					bundle.putBoolean("isUCVid", true);
					bundle.putBoolean("isVideo", isVid);
					policeNews.putExtras(bundle);
					
					startActivity(policeNews);
			}
				
		}
 	    	
 	   });
	
	}
	
	@Override
    public void onStart(){
    	super.onStart();
    	
    	
//    	DISTRICT_DIVISION = this.getArguments().getString("Division");
    	
//		//String newURL = URL.concat("?ismobile=true&main=true&district="+district);
//		Log.i("PHILLYPOLICE", "Going to: "+newURL);
//		//String newURL = URL.concat("?district="+12+"&ismobile=true");
    	
    }
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState) {
//		 	DISTRICT_DIVISION = this.getArguments().getString("Division");
	        View layout = inflater.inflate(R.layout.uc_videos, container, false);
	        TextView header = (TextView) layout.findViewById(R.id.UCVideoHeader);
	        header.setText("Division Unsolved Crime Videos");
	        

	        
	        return layout;
	        
		// return super.onCreateView(inflater, container, savedInstanceState);
	    }
	 
	 

	 
	 public class getUCVideoList extends AsyncTask<String, Void, ArrayList<PoliceUCVideoObject>>{
			
			@Override
			protected ArrayList<PoliceUCVideoObject> doInBackground(String... params) {
				UC_Obj = new ArrayList<PoliceUCVideoObject>();
				
					try{

						String Data = getListData(params[0],0,5);
						JSONObject object = new JSONObject(Data);
						TOTAL_COUNT = object.getInt("TotalCount");
						JSONArray objectArray = object.getJSONArray("Videos");
						int ct = objectArray.length();
							for(int i=0;i<ct;i++){
							//	Log.i("THIS_MANY_STING", ct+" "+objectArray.getString(ct));
								PoliceUCVideoObject info = new PoliceUCVideoObject();
								JSONObject Dobject = objectArray.getJSONObject(i);
								info.setVideoTitle(Dobject.getString("VideoTitle"));
								info.setUCVideoID(Dobject.getString("UCVideoID"));
								info.setDescription(Dobject.getString("Description"));
								info.setVideoImageURL(Dobject.getString("VideoImageURL"));
								info.setTubeURL(Dobject.getString("TubeURL"));
								info.setDistrictDivision(Dobject.getString("DistrictDivision"));
								info.setCrimeType(Dobject.getString("CrimeType"));
								info.setVideoDate(Dobject.getString("VideoDate"));
								UC_Obj.add(info);
							}
						Log.i("THE ARRAY COUNT", Integer.toString(UC_Obj.size()));
						
					//	image = getBitmapFromURL(Dobject.getString("CaptainURL"));
					}	
						catch (ClientProtocolException e) {e.printStackTrace();}
						catch (IOException e) {e.printStackTrace();} 
						catch (JSONException e) {e.printStackTrace();}
				
				return UC_Obj;
			}
			
				protected void onPostExecute(ArrayList<PoliceUCVideoObject> uc_vid_objs) {
					adapter = new PoliceUCVideosAdapter(getActivity(), uc_vid_objs);
					//setListAdapter(adapter);
					
					if(uc_vid_objs.size() <= 0){
						//pDialog.dismiss();
			//			Toast.makeText(getActivity(), "No videos at this time", Toast.LENGTH_LONG).show();
						noVideoTextView = (TextView) getActivity().findViewById(R.id.UCVideos_NoVid);
						noVideoTextView.setVisibility(View.VISIBLE);
					}else{
						String ct = Integer.toString(TOTAL_COUNT);
						//String dc = Integer.toString(DISPLAY_COUNT);
						if(TOTAL_COUNT == uc_vid_objs.size()){
							View title = Header("No More Videos "+"( "+uc_vid_objs.size()+" of "+TOTAL_COUNT+" )");
							getListView().addFooterView(title);
							getListView().setAdapter(adapter);
						}else{
							View title = Header("More Videos "+"( "+uc_vid_objs.size()+" of "+ct+" )");
							getListView().addFooterView(title);
							getListView().setAdapter(adapter);
						}
						
					}
							
							
		}
		
	}
	 
	 
	 public class fetchMoreVids extends AsyncTask<String, Void, ArrayList<PoliceUCVideoObject>>{

		@Override
		protected ArrayList<PoliceUCVideoObject> doInBackground(
				String... params) {
			// TODO Auto-generated method stub
			try {
				String rdata = null;
				if((TOTAL_COUNT - UC_Obj.size()) <=5){
					rdata = getListData(params[0],UC_Obj.size(),TOTAL_COUNT);
				}else if((TOTAL_COUNT - UC_Obj.size())>5){
					rdata = getListData(params[0],UC_Obj.size(),5);
				}
				//String rdata = getListData(params[0],UC_Obj.size(),TOTAL_COUNT);
				JSONObject jObj = new JSONObject(rdata);
				String isErr = jObj.getString("error");
					
					if(isErr.equals("true")){
						Log.e("No Videos To SHOW !!!", "FIXX ME; SHOW MESSAGE IN FRAGMENT");
					}else{
						JSONArray objectArray = jObj.getJSONArray("Videos");
						int ct = objectArray.length();
							for(int i=0;i<ct;i++){
								PoliceUCVideoObject info = new PoliceUCVideoObject();
								JSONObject Dobject = objectArray.getJSONObject(i);
								info.setVideoTitle(Dobject.getString("VideoTitle"));
								info.setNewsStoryID(Dobject.getString("NewsStoryID"));
								info.setDescription(Dobject.getString("Description"));
								info.setVideoImageURL(Dobject.getString("VideoImageURL"));
								info.setTubeURL(Dobject.getString("TubeURL"));
								info.setDistrictDivision(Dobject.getString("DistrictDivision"));
								info.setCrimeType(Dobject.getString("CrimeType"));
								info.setVideoDate(Dobject.getString("VideoDate"));
								UC_Obj.add(info);
							}
						
						TOTAL_COUNT = jObj.getInt("TotalCount");
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
			
			return UC_Obj;
		}
		
		protected void onPostExecute(ArrayList<PoliceUCVideoObject> uc_vid_objs){
			adapter.updateList(uc_vid_objs);
			//adapter.notifyDataSetChanged();
			progressM.setVisibility(View.INVISIBLE);
				if(uc_vid_objs.size() == TOTAL_COUNT){
					headerTxt.setText("No More News "+"( "+uc_vid_objs.size()+" of "+TOTAL_COUNT+" )");
					headerTxt.setVisibility(View.VISIBLE);
				}else{
					headerTxt.setText("More News "+"( "+uc_vid_objs.size()+" of "+TOTAL_COUNT+" )");
					headerTxt.setVisibility(View.VISIBLE);
				}
			
			
					
					
		}
		
		 
	 }
	 
	 private String getListData(String uRL, int srt, int end) throws ClientProtocolException, IOException, JSONException{

			HttpClient android = new DefaultHttpClient();
			HttpPost clientRequest = new HttpPost(uRL);
			
			
			JSONObject postObj = new JSONObject();
	 		postObj.put("UCVideos", "true");
	 		postObj.put("District", DISTRICT_NUM);
	// 		postObj.put("Division", DISTRICT_DIVISION);
	// 		postObj.put("DeviceID", HttpClientInfo.DEVICE_ID);
	 		postObj.put("Start", srt);
	 		postObj.put("End", end);
	 		
	 		clientRequest.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
	 		clientRequest.setHeader("Content-Type","application/json");
	 		clientRequest.setHeader("Accept-Encoding","application/json");
	 		clientRequest.setHeader("Accept-Language","en-US");
	 		
	 		HttpResponse response = android.execute(clientRequest);

			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			response.getEntity().writeTo(os); 
			String responseString = os.toString();
			
			return responseString;

		}
	 
	 public static Bitmap getBitmapFromURL(String src){

		     try {
		        java.net.URL url = new java.net.URL(src);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        Bitmap myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return null;
		    }
		}


	 
	 @Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			return false;
		}
	  	
	  	
	  	
	  	
	  	
	  	
	  	@SuppressLint("InflateParams")
		private View Header(String string) {
	    	View k = getActivity().getLayoutInflater().inflate(R.layout.uc_more_header, null);
	    	TextView title = (TextView) k.findViewById(R.id.MoreListUCTextView);
	    	title.setText(string);
	    	return k;
		}
	 
	 
	
}