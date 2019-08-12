package furious.phillypolicemobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class DistrictFragmentActivity extends FragmentActivity{
	private String district;
//	private String division;
	String URL = "";
	HttpClient client;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        district = CVTdistrict(extras.getString("DistrictNumber"));
//        division = extras.getString("Division");
        
        
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
//		String[] South = {"1st","3rd","17th"};
//		String[] Northeast = {"2nd","7th","8th","15th"};
//		String[] Southwest = {"19th","18th","16th","12th"};
//		String[] Northwest = {"5th","39th","35th","14th"};
//		String[] ROCSC = {"6th","9th","22nd"};
//		String[] ROCNE = {"25th","24th","26th"};
		
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
	
	
	
//	  @Override
//	   protected void  onResume(){
//	      super.onResume();
//	      int page = getIntent().getIntExtra("page", 0);
//	      ViewPager  pager = (ViewPager) findViewById(R.id.viewPager);
//	      pager.setCurrentItem(page, false);
//	   }
	
}