package furious.viewfragments.district;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import furious.dataobjs.NewStoryObject;
import furious.objadapters.NewsAdapter;
import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.viewfragments.bookmark.PoliceNews;


public class DistrictNewsList extends ListFragment implements AdapterView.OnItemLongClickListener{


	String DISTRICT;
	ListView listview;
	ArrayList<NewStoryObject> newsObjs;
	ArrayList<String> items;
	ProgressBar progressM;
	ProgressDialog pDialog;
	HttpURLConnection httpcon;
	TextView footerTxt;
	TextView headerTxt;
	TextView noNewsTxt;
	NewsAdapter adapter;
	private int TOTAL_COUNT;
	private int DISPLAY_COUNT;

	
	static DistrictNewsList newInstance(String district){
		DistrictNewsList frag = new DistrictNewsList();
        Bundle args = new Bundle();
        args.putString("DistrictNumber", district);
        frag.setArguments(args);

        return frag;
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
    	
    	DISTRICT = Uri.encode(this.getArguments().getString("DistrictNumber"));
    	new getDistrictNews().execute(DISTRICT);
        
    }
	
 	@Override
 	public void onActivityCreated(Bundle savedState){
 	    super.onActivityCreated(savedState);
 	  
 	   this.getListView().setOnItemLongClickListener(this);
 	   this.getListView().setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			if(arg1.findViewById(R.id.MoreListTextView) != null){
				if(TOTAL_COUNT != newsObjs.size()){
					footerTxt = (TextView) getActivity().findViewById(R.id.MoreListTextView);
					progressM = (ProgressBar) getActivity().findViewById(R.id.HeaderLoadMore);
					progressM.setVisibility(View.VISIBLE);
					footerTxt.setVisibility(View.INVISIBLE);
					
					new fetchMoreNews().execute(HttpClientInfo.URL);
				}else{
					Toast.makeText(getActivity(), "No more news at this time", Toast.LENGTH_LONG).show();
				}
			}else{

				NewStoryObject lObj = (NewStoryObject) arg0.getItemAtPosition(arg2);

				View v = arg1.findViewById(R.id.DistrictNewsImageView);
				v.setDrawingCacheEnabled(true);
				Bitmap capturedBitmap = Bitmap.createBitmap(v.getDrawingCache());
				v.setDrawingCacheEnabled(false);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				capturedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();

				boolean isVid = false;
					if(!lObj.getTubeURL().equals(0) || !lObj.getTubeURL().equals(null)){
						isVid = true;
					}
				Intent policeNews = new Intent(getActivity(),PoliceNews.class);
				Bundle bundle = new Bundle();
				bundle.putString("Description", lObj.getDescription());
				bundle.putString("StoryTitle", lObj.getTitle());
				bundle.putString("URL", lObj.getTubeURL());
				bundle.putString("StoryID", lObj.getNewsStoryID());
				bundle.putString("ImageURL", lObj.getImageURL());
				bundle.putString("CrimeType",lObj.getCategory());
				bundle.putString("ParentActivity", "DistrictNewsList");
				bundle.putBoolean("isVideo", isVid);
				bundle.putBoolean("isUCVid", false);
				bundle.putBoolean("isAlrBk", false);
				policeNews.putExtra("VictimImage", byteArray);
				
				policeNews.putExtras(bundle);
				
				startActivity(policeNews);

			}
			
		}});

 	}

	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                             Bundle savedInstanceState){	
		 super.onCreateView(inflater, container, savedInstanceState);
		 
		 View layout = inflater.inflate(R.layout.newslistlayout, container, false);
		 noNewsTxt = (TextView) layout.findViewById(R.id.NoNewsTxtView);
		 headerTxt = (TextView) layout.findViewById(R.id.NewsListHeaderTextView);
		 headerTxt.setText(CVDistrict(DISTRICT)+" District News");
		 headerTxt.setVisibility(View.VISIBLE);

	     return layout;	        
		
	    }
	 
	 public static String CVDistrict(String dnum){
		String nwdrt = null;
		
			if(dnum.equals("2") || dnum.equals("22")){
				nwdrt = dnum.concat("nd");
			}else if(dnum.equals("1") || dnum.equals("21")){
				nwdrt = dnum.concat("st");
			}else if(dnum.equals("3") || dnum.equals("23")){
				nwdrt = dnum.concat("rd");
			}else{
				nwdrt = dnum.concat("th");
			}
		
		 return nwdrt;
		 
	 }
	 
	 public class getDistrictNews extends AsyncTask<String, Void, ArrayList<NewStoryObject>>{
		 
		 @Override
		    protected void onPreExecute() {
			 super.onPreExecute();	
			 pDialog  = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", false);
			 pDialog.setCancelable(true);
		 }
		 
			@Override
			protected ArrayList<NewStoryObject> doInBackground(String... params) {
				newsObjs = new ArrayList<NewStoryObject>();
				
					try{
						String Data = getListData(params[0],0,5);
						JSONObject object = new JSONObject(Data);
						JSONArray objectArray = object.getJSONArray("Articles");
							int count = objectArray.length();
								
							for(int i=0;i<count;i++){
									NewStoryObject item = new NewStoryObject();
									JSONObject newobject = objectArray.getJSONObject(i);
									item.setNewsStoryID(newobject.getString("NewsStoryID"));
									item.setDistrictNumber(newobject.getString("DistrictNumber"));
									item.setDescription(newobject.getString("Description"));
									item.setPubDate(newobject.getString("PubDate"));
									item.setImageURL(newobject.getString("ImageURL"));
									item.setURL(newobject.getString("URL"));
									item.setTitle(newobject.getString("Title"));
									item.setTubeURL(newobject.getString("TubeURL"));
									item.setCategory(newobject.getString("Category"));
									item.setStoryAuthor(newobject.getString("StoryAuthor"));
									newsObjs.add(item);
								}
								TOTAL_COUNT = object.getInt("TotalCount");
					}	
									catch (IOException e) {e.printStackTrace();}
									catch (JSONException e) {e.printStackTrace();}

				
				return newsObjs;
			}
			
				protected void onPostExecute(ArrayList<NewStoryObject> lockers) {
					
					adapter = new NewsAdapter(getActivity(),lockers);

					if(lockers.size() <= 0){
							pDialog.dismiss();
							noNewsTxt.setVisibility(View.VISIBLE);
						}else{
							String ct = Integer.toString(TOTAL_COUNT);

							if(TOTAL_COUNT == lockers.size()){
								View title = Header("No More News");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);
								pDialog.dismiss();
							}else{
								View title = Header("More News "+"( "+lockers.size()+" of "+ct+" )");
								getListView().addFooterView(title);
								getListView().setAdapter(adapter);
								pDialog.dismiss();
							}
							
						}
					
							
		}
		
	}
	 
	 
	 public class fetchMoreNews extends AsyncTask<String, Void, ArrayList<NewStoryObject>>{

		@Override
		protected ArrayList<NewStoryObject> doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				String rdata = null;
					if((TOTAL_COUNT - newsObjs.size()) <=5){
						rdata = getListData(DISTRICT,newsObjs.size(),TOTAL_COUNT);
					}else if((TOTAL_COUNT - newsObjs.size())>5){
						rdata = getListData(DISTRICT,newsObjs.size(),5);
					}

				JSONObject jObj = new JSONObject(rdata);
				Log.i("Counts", DISTRICT);
				JSONArray objectArray = jObj.getJSONArray("Articles");
				int count = objectArray.length();
					
				for(int i=0;i<count;i++){
						NewStoryObject item = new NewStoryObject();
						JSONObject newobject = objectArray.getJSONObject(i);
						item.setNewsStoryID(newobject.getString("NewsStoryID"));
						item.setDistrictNumber(newobject.getString("DistrictNumber"));
						item.setDescription(newobject.getString("Description"));
						item.setPubDate(newobject.getString("PubDate"));
						item.setImageURL(newobject.getString("ImageURL"));
						item.setURL(newobject.getString("URL"));
						item.setTitle(newobject.getString("Title"));
						item.setTubeURL(newobject.getString("TubeURL"));
						item.setCategory(newobject.getString("Category"));
						item.setStoryAuthor(newobject.getString("StoryAuthor"));
						newsObjs.add(item);
					}
					TOTAL_COUNT = jObj.getInt("TotalCount");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return newsObjs;
		}
		
		protected void onPostExecute(ArrayList<NewStoryObject> newsSty){
			
			adapter.updateList(newsSty);
			adapter.notifyDataSetChanged();
			progressM.setVisibility(View.INVISIBLE);
				if(newsSty.size() == TOTAL_COUNT){
					footerTxt.setText("No More News "+"( "+newsSty.size()+" of "+TOTAL_COUNT+" )");
					footerTxt.setVisibility(View.VISIBLE);
				}else{
					footerTxt.setText("More News "+"( "+newsSty.size()+" of "+TOTAL_COUNT+" )");
					footerTxt.setVisibility(View.VISIBLE);
				}
			
}
		 
	 }

	 
	 private String getListData(String Dist_Num,int srt, int end) throws  IOException, JSONException{

		 String result = null;

		 try {

			 JSONObject postObj = new JSONObject();
			 postObj.put("DistrictNumber", Dist_Num);
			 postObj.put("DistrictNews", "true");
			 postObj.put("News", "ALL");
			 postObj.put("Start", srt);
			 postObj.put("End", end);
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
		 }

		 catch (UnsupportedEncodingException e) {
			 e.printStackTrace();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }

			 return result;


	 		
			
		}
	 

     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
         super.onCreateContextMenu(menu, v, menuInfo);
         getActivity().getMenuInflater().inflate(R.menu.newsobject_menu, menu);

     }
     
     
  	@Override
  	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

  		registerForContextMenu(this.getListView());
  	    getListView().showContextMenu();
  	    return true;
  	}
  	
  	 private View Header(String string) {
	    	View k = getActivity().getLayoutInflater().inflate(R.layout.news_more_header, null);
	    	TextView title = (TextView) k.findViewById(R.id.MoreListTextView);
	    	title.setText(string);
	    	return k;
		}
  	 

}