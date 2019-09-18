package furious.viewfragments.district;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import furious.phillypolicemobile.R;
import furious.utils.Utils;
import furious.viewfragments.bookmark.BookmarkFragmentActivity;
import furious.viewfragments.preferences.MainPreferenceActivity;

import static furious.utils.Utils.CVTdistrict;


public class DistrictFragmentActivity extends AppCompatActivity {

	private String district;
	Context mcontext;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        district = CVTdistrict(extras.getString("DistrictNumber"));
        
        
        setContentView(R.layout.fragment_pager);
        Log.i("PHILLYPOLICE","PASSData "+district);

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout2);
		tabLayout.addTab(tabLayout.newTab().setText("News Stories"));
		tabLayout.addTab(tabLayout.newTab().setText("Information"));
		tabLayout.addTab(tabLayout.newTab().setText("Crimes"));
		tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

		final ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
		pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
		pager.setOffscreenPageLimit(2);
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




//        pager.addOnPageChangeListener(new OnPageChangeListener() {
//			@Override
//			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//			}
//
//			@Override
//			public void onPageSelected(int position) {
//
//				switch(position){
//					case 0:
//						findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
//						findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
//						findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
//						break;
//
//					case 1:
//						findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
//						findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
//						findViewById(R.id.third_tab).setVisibility(View.INVISIBLE);
//						break;
//
//					case 2:
//						findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
//						findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
//						findViewById(R.id.third_tab).setVisibility(View.VISIBLE);
//						break;
//				}
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int state) {
//
//			}
//		});
    
    }

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			super.onCreateOptionsMenu(menu);
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case R.id.action_settings:
					Intent intent = new Intent();
					intent.setClass(DistrictFragmentActivity.this, MainPreferenceActivity.class);
					startActivity(intent);

					return true;

				case R.id.action_bookmark:
					Intent bookIntent = new Intent();
					bookIntent.setClass(DistrictFragmentActivity.this, BookmarkFragmentActivity.class);
					startActivity(bookIntent);

					return true;

				default:
					break;
			}

			return false;
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
	


	
}