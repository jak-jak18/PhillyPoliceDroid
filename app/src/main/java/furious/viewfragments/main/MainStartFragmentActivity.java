package furious.viewfragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import furious.phillypolicemobile.R;
import furious.viewfragments.bookmark.BookmarkFragmentActivity;
import furious.viewfragments.preferences.MainPreferenceActivity;


public class MainStartFragmentActivity extends AppCompatActivity{
	

	@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainstart);

		Toolbar mractionbar = (Toolbar) findViewById(R.id.mr_toolbar);
		setSupportActionBar(mractionbar);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
        
        pager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

				switch(position){
					case 0:
						findViewById(R.id.first_tab).setVisibility(View.VISIBLE);
						findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
						findViewById(R.id.thirdd_tab).setVisibility(View.INVISIBLE);
						break;

					case 1:

						findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
						findViewById(R.id.second_tab).setVisibility(View.VISIBLE);
						findViewById(R.id.thirdd_tab).setVisibility(View.INVISIBLE);
						break;


					case 2:

						findViewById(R.id.first_tab).setVisibility(View.INVISIBLE);
						findViewById(R.id.second_tab).setVisibility(View.INVISIBLE);
						findViewById(R.id.thirdd_tab).setVisibility(View.VISIBLE);
						break;
				}

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
        
	
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
		         intent.setClass(MainStartFragmentActivity.this, MainPreferenceActivity.class);
		         startActivity(intent); 
		   
		        return true;
		        
		    case R.id.action_bookmark:
		    	 Intent bookIntent = new Intent();
		    	 bookIntent.setClass(MainStartFragmentActivity.this, BookmarkFragmentActivity.class);
		         startActivity(bookIntent); 
		   
		        return true;
	        
		    default:
		        break;
	    }

	    return false;
	}
	
	public class NewsPagerAdapter extends FragmentPagerAdapter{
    	private String[] Positions = {"District News","Main News","Shootings"};

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
        		return new MainNews();
        	}else if(position == 1){
        		return new MainDistrictNews();
        	}else if(position == 2){
				return new ShootingFragment();
			}else{
        		return new MainDistrictNews();
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