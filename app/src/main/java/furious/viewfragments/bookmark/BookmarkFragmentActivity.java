package furious.viewfragments.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import java.util.List;

import furious.phillypolicemobile.R;
import furious.utils.HttpClientInfo;
import furious.utils.Utils;

public class BookmarkFragmentActivity extends AppCompatActivity {

	String JSON_DATA;
	HttpURLConnection httpcon;

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_mainstart);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_bmrk);
		tabLayout.addTab(tabLayout.newTab().setText("News Stories"));
		tabLayout.addTab(tabLayout.newTab().setText("Unsolved Murders"));
		tabLayout.addTab(tabLayout.newTab().setText("US Crime Videos"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		Toolbar mractionbar = (Toolbar) findViewById(R.id.mr_toolbar_bmrk);
		mractionbar.setTitle("Bookmarks");
		setSupportActionBar(mractionbar);

        final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
		pager.setPageTransformer(true, new Utils.DepthPageTransformer());

		pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				pager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

        
	
	}

	
	public class NewsPagerAdapter extends FragmentPagerAdapter{
    	private String[] Positions = {"News Stories","Unsolved Murers","UC Crime Videos"};

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
        		Fragment nwsStryBkm = new USMurderBookmark();
        		Bundle args1 = new Bundle();
                args1.putString("JSON_DATA", JSON_DATA);
                nwsStryBkm.setArguments(args1);

        		return nwsStryBkm;
        	}else{
        		Fragment nwsStryBkm = new UCVideoBookmark();
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




	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		//Log.e("ERROR","MAIN PARENT HIT ON ACTIVITY RESULTS "+Integer.toString(data.getIntExtra("ItemPosition",99)));
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				fragment.onActivityResult(requestCode, resultCode, data);

			}
		}


	}





}