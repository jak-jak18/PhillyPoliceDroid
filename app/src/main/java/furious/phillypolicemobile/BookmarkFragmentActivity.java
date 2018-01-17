package furious.phillypolicemobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class BookmarkFragmentActivity extends FragmentActivity{
	
	HttpClient client;
	String JSON_DATA;
	

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_mainstart);
        
//        try {
//        	
//			JSON_DATA = getBookmarkListData();
//		
//        } catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
        
        pager.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				switch(position){
                case 0:
                	findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
                    findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                    break;

                case 1:
                	
                    findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                    findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
                    break;
                }
			}
        	
        });
        
	
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch (item.getItemId()) {
//		    case R.id.action_settings:
//		    	 Intent intent = new Intent();
//		         intent.setClass(BookmarkFragmentActivity.this, MainPreferenceActivity.class);
//		         startActivity(intent); 
//		   
//		        return true;
//		        
//		    case R.id.action_bookmark:
//		    	 Intent bookIntent = new Intent();
//		    	 bookIntent.setClass(BookmarkFragmentActivity.this, NewsStoryBookmark.class);
//		         startActivity(bookIntent); 
//		   
//		        return true;
//	        
//		    default:
//		        break;
//	    }
//
//	    return false;
//	}
	
	public class NewsPagerAdapter extends FragmentPagerAdapter{
    	private String[] Positions = {"News Stories","UC Videos"};

        public NewsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return Positions.length;
        }

        @Override
        public Fragment getItem(int position){
        	if(position == 0){
        		Fragment nwsStryBkm = new NewsStoryBookmark();
        		Bundle args1 = new Bundle();
//        		args1.putInt("param", R.drawable.image1);
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}else if(position == 1){
        		Fragment nwsStryBkm = new UCVideoBookmark();
        		Bundle args1 = new Bundle();
//        		args1.putInt("param", R.drawable.image1);
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}else{
        		Fragment nwsStryBkm = new NewsStoryBookmark();
        		Bundle args1 = new Bundle();
//        		args1.putInt("param", R.drawable.image1);
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}
        }

		public String getTitle(int position) {
			return Positions[position];
		}

		public String[] getPositions() {
			return Positions;
		}

		public void setPositions(String[] positionsTitles) {
			Positions = positionsTitles;
		}
  }
	
	
	
		
	
	private String getBookmarkListData() throws ClientProtocolException, IOException, JSONException{
		 
	 	//client = AndroidHttpClient.newInstance("ComboNation");
		String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
	 	String deviceID = HttpClientInfo.getMD5(macAddss);
	 	client = new DefaultHttpClient();
 		HttpPost post = new HttpPost(HttpClientInfo.URL);
 		
	 	JSONObject postObj = new JSONObject();
 		postObj.put("Bookmark", "true");
 		postObj.put("DeviceID", deviceID);
 				
 		
 		post.setEntity(new StringEntity(postObj.toString(), "UTF-8"));
 		post.setHeader("Content-Type","application/json");
 		post.setHeader("Accept-Encoding","application/json");
 		post.setHeader("Accept-Language","en-US");
 		
 		HttpResponse res = client.execute(post);
 		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		res.getEntity().writeTo(os); 
		
//		HttpClient android = new DefaultHttpClient();
//		HttpGet clientRequest = new HttpGet(uRL);
//		HttpResponse response = android.execute(clientRequest);
//
//		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
//		response.getEntity().writeTo(os); 
//		String responseString = os.toString();
//		
//		return responseString;
		
		return os.toString();
		
		
	}
		
	
	
	
	
	
}