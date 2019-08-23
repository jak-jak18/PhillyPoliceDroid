package furious.viewfragments.district;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;

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

import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;


public class DistrictFragmentActivity extends FragmentActivity{
	private String district;
	HttpURLConnection httpcon;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        district = CVTdistrict(extras.getString("DistrictNumber"));
        
        
        setContentView(R.layout.fragment_pager);
        Log.i("PHILLYPOLICE","PASSData "+district);
        
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
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
                    findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                    break;

                case 1:
                    findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                    findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
                    findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
                    break;
                    
                case 2:
                    findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
                    findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
                    findViewById(R.id.third_tab).setVisibility(View.VISIBLE);
                    break;
                }
			}
        	
        });
    
    }
	
	public String CVTdistrict(String string) {

		if(string.equals("1st")){
			return "1";
		}else if(string.equals("3rd")){
			return "3";
		}else if(string.equals("17th")){
			return "17";
		}else if(string.equals("2nd")){
			return "2";
		}else if(string.equals("7th")){
			return "7";
		}else if(string.equals("8th")){
			return "8";
		}else if(string.equals("15th")){
			return "15";
		}else if(string.equals("19th")){
			return "19";
		}else if(string.equals("18th")){
			return "18";
		}else if(string.equals("16th")){
			return "16";
		}else if(string.equals("12th")){
			return "12";
		}else if(string.equals("5th")){
			return "5";
		}else if(string.equals("39th")){
			return "39";
		}else if(string.equals("35th")){
			return "35";
		}else if(string.equals("14th")){
			return "14";
		}else if(string.equals("6th")){
			return "6";
		}else if(string.equals("9th")){
			return "9";
		}else if(string.equals("22nd")){
			return "22";
		}else if(string.equals("25th")){
			return "25";
		}else if(string.equals("24th")){
			return "24";
		}else if(string.equals("26th")){
			return "26";
		}else
			
			return null;
	}

	public class NewsPagerAdapter extends FragmentPagerAdapter{
    	private String[] Positions = {"District News","Unsolved Crimes","District Info"};

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
        		return DistrictNewsList.newInstance(district);
        	}else if(position == 1){
        		return DistrictInfoFragment.newInstance(district);
        	}else if(position == 2){
        		return CrimeFragment.newInstance(district);
        	}else{
        		return DistrictNewsList.newInstance(district);
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

		try {

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

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
		
		
	}

	
}