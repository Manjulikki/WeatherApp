package manjunn.weather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

public class HomeSliding extends AppCompatActivity {

    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"Cities", "Current", "Next"};
    int noOfTabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_sliding);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), titles, noOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setSelected(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return R.color.white;
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        CharSequence Titles[];
        int NumbOfTabs;

        public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                CityList cityList = new CityList();
                return cityList;
            } else if (position == 1) {
                return new Home();
            } else {
                WeatherForecastFull weatherForecastFull = new WeatherForecastFull();
                return weatherForecastFull;
            }
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //when back button pressed
            showMainAlertDialog(getResources().getString(R.string.quit_app));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showMainAlertDialog(String text) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.leave_app))
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);  //to quit application
                    }
                })
                .setIcon(R.drawable.exit)
                .show();
    }
}

