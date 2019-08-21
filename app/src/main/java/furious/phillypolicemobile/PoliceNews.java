package furious.phillypolicemobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URL;
import java.util.ArrayList;

public class PoliceNews extends Activity {


	MediaController media;
	ListView listview;
	ImageView imgHolder;
	ImageView ytHolder;
	String Desc;
	String storyTitle;
	String URL;
	String img_URL;
	String storyID;
	String BOOKMARK_NEWS = "false";
	String BOOKMARK_VIDEOS = "false";
	HttpURLConnection httpcon;
	boolean isAlrBk = false;
	boolean isUCVid = false;
	ArrayList<String> headers;
	ImageLoader imgLoader;
	Button bookMarkButton;
	ProgressBar diaLog;
	
	
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policenews);
        
        imgHolder = (ImageView) findViewById(R.id.PoliceImageHolder);
        ytHolder = (ImageView) findViewById(R.id.ImagetubeHolder);
        diaLog = (ProgressBar) findViewById(R.id.progressBar1);
        URL = (String) this.getIntent().getExtras().getString("URL");
		Desc = (String) this.getIntent().getExtras().getString("Description");
        storyTitle = (String) this.getIntent().getExtras().getString("StoryTitle");
        storyID = (String) this.getIntent().getExtras().getString("StoryID");
        img_URL = (String) this.getIntent().getExtras().getString("ImageURL");
        isUCVid = (boolean) this.getIntent().getExtras().getBoolean("isUCVid");
        isAlrBk = (boolean) this.getIntent().getExtras().getBoolean("isAlrBk");
        
        
        
        
        imgLoader = new ImageLoader(this); 		//Network Intense; need to pass IMG through intent
        imgLoader.DisplayImage(img_URL, imgHolder);
        
        bookMarkButton = (Button) findViewById(R.id.bookmarkButton1);
        
	        if(isAlrBk){
	        	bookMarkButton.setVisibility(View.INVISIBLE);
	        }

    		if(URL.equals("No Video") || URL.isEmpty()){
    			ytHolder.setVisibility(View.INVISIBLE);
    			ytHolder.setEnabled(false);
    			imgHolder.setEnabled(false);
    			
    			BOOKMARK_NEWS = "true";
    			BOOKMARK_VIDEOS = "false";
    			
    		}else{
    			
    			BOOKMARK_NEWS = "false";
    			BOOKMARK_VIDEOS = "true";
    		}
        		
    		if(isUCVid){
    			bookMarkButton.setText("Bookmark Video");
    			
    			BOOKMARK_NEWS = "false";
    			BOOKMARK_VIDEOS = "true";
    		}else{
    			
    			BOOKMARK_NEWS = "true";
    			BOOKMARK_VIDEOS = "false";
    		}
    		
			bookMarkButton.setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bookMarkButton.setEnabled(false);
					new fetchBokmarks().execute();
					
				}
		
			});
	        
        
        	
        
        
        
        
        TextView text = (TextView) findViewById(R.id.PoliceNewsDesc);
        TextView titleText = (TextView) findViewById(R.id.PoliceNewsInfoTitle);
        titleText.setText(storyTitle);
        text.setText(Desc);
        
        imgHolder.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(URL.equals("No Video")){
					Toast.makeText(getApplicationContext(), "No Video", Toast.LENGTH_LONG).show();
				}else{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL)));
				}
					
			}
        	
        });

    }
    
    


    
    public class fetchBokmarks extends AsyncTask<String, Void, String>{
    	
		 @Override
		    protected void onPreExecute() {
			 super.onPreExecute();
			 diaLog.setVisibility(View.VISIBLE);
		 }
		 
			@Override
			protected String doInBackground(String... params) {
				
				String finalData = null;
					
				try{
						
					String jsonData = saveBookmark();
						JSONObject jsonObj = new JSONObject(jsonData);
						
						String isError = jsonObj.getString("error");
						
							if(isError.equals("false")){
								finalData = jsonObj.getString("msg");
							}else if(isError.equals("true")){
								finalData = jsonObj.getString("msg");
							}

								
					}	
									catch (IOException e) {e.printStackTrace();}
									catch (JSONException e) {e.printStackTrace();}

				
				return finalData;
			}
			
				protected void onPostExecute(String lockers) {
					
					//diaLog.dismiss();
					Toast.makeText(getApplicationContext(), lockers, Toast.LENGTH_LONG).show();
					diaLog.setVisibility(View.INVISIBLE);
							
		}
		
	}



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    protected void onDestroy() {

          super.onDestroy();
    }
    
    public class PoliceNewsListAdapter extends ArrayAdapter<String>{
    	private LayoutInflater inflater;
    	private int resource;
    	private ArrayList<String> strings;
    	
		public PoliceNewsListAdapter(Context context, int textViewResourceId,
				ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			 resource = textViewResourceId;
			 inflater = getLayoutInflater();
			 strings = objects;
		}
		
		@Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view;

            if(convertView == null){
                view = inflater.inflate(resource, null);
            }else{
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(R.id.PoliceNewsListViewRowHeading);

            textView.setText(strings.get(position).toString());

            return view;
        }
		
    	
    }
    
    
    private String saveBookmark() throws IOException, JSONException{

		String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
	 	String deviceID = HttpClientInfo.getMD5(macAddss);


		String result = null;

		try {

			JSONObject postObj = new JSONObject();
			postObj.put("Bookmark", "true");
			postObj.put("DeviceID", deviceID);
			postObj.put("Bookmark_UCVideos", "false");
			postObj.put("Bookmark_NewsStory", "false");
			postObj.put("Bookmark_Submit", "true");
			postObj.put("BookmarkID", storyID);
			postObj.put("BookmarkNews", BOOKMARK_NEWS);
			postObj.put("BookmarkVideos", BOOKMARK_VIDEOS);
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
    
    
	    @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	    	super.onConfigurationChanged(newConfig);
	    		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	                 
	    		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    		}
	    	}
    
}
