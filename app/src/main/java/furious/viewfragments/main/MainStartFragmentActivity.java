package furious.viewfragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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

		Toolbar mractionbar = findViewById(R.id.mr_toolbar);
		setSupportActionBar(mractionbar);

		final ViewPager pager = findViewById(R.id.viewPager);
		pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
		TabLayout tabLayout = findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(pager);
//        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//			@Override
//			public void onTabSelected(TabLayout.Tab tab) {
//
//				pager.setCurrentItem(tab.getPosition());
//			}
//
//			@Override
//			public void onTabUnselected(TabLayout.Tab tab) {
//
//			}
//
//			@Override
//			public void onTabReselected(TabLayout.Tab tab) {
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
<<<<<<< HEAD
	
	public class NewsPagerAdapter extends FragmentStatePagerAdapter {
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
=======

	public class NewsPagerAdapter extends FragmentPagerAdapter{
		private String[] Positions = {"District News","Main News","Shootings"};
		private Fragment[] frags = {
				new MainNews(),
				new MainDistrictNews(),
				new ShootingFragment()
		};

		NewsPagerAdapter(FragmentManager fm) {
			super(fm);
>>>>>>> debug
		}

		@Override
		public int getCount() {
			return Positions.length;
		}

		@Override
		public Fragment getItem(int position){ return frags[position]; }

		@Override
		public CharSequence getPageTitle(int position) {
			return Positions[position];
		}
	}
}