package furious.test.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import furious.phillypolicemobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_mainactivity_layout);

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new NewsPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager);
    }

    public class NewsPagerAdapter extends FragmentPagerAdapter {
        private String[] Positions = {"District News","Main News","Shootings"};
        private Fragment[] frags = {
                new MainNewsFrag(),
                new DistrictNewsFrag(),
                new ShootingFrag()
        };

        NewsPagerAdapter(FragmentManager fm) { super(fm); }

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
