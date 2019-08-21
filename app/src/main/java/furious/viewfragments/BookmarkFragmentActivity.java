package furious.viewfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;


public class BookmarkFragmentActivity extends FragmentActivity{

	String JSON_DATA;
	HttpURLConnection httpcon;

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_mainstart);

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
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}else if(position == 1){
        		Fragment nwsStryBkm = new UCVideoBookmark();
        		Bundle args1 = new Bundle();
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}else{
        		Fragment nwsStryBkm = new NewsStoryBookmark();
        		Bundle args1 = new Bundle();
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
	
	
	
		
	
	private String getBookmarkListData() throws IOException, JSONException{

		String macAddss = HttpClientInfo.getMacAddress(getApplicationContext());
	 	String deviceID = HttpClientInfo.getMD5(macAddss);
		String result = null;


	 	try{

			JSONObject postObj = new JSONObject();
			postObj.put("Bookmark", "true");
			postObj.put("DeviceID", deviceID);
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

		catch (IOException e) {
			e.printStackTrace();
		}

 				
 		


		
		return result;
		
		
	}
		
	
	
	
	
	
}