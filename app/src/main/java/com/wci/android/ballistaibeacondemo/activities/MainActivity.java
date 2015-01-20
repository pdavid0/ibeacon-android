package com.wci.android.ballistaibeacondemo.activities;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.wci.android.ballistaibeacondemo.BeaconApp;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.fragments.BallistaBeaconFragment;
import com.wci.android.ballistaibeacondemo.fragments.EventHistoryFragment;
import com.wci.android.ballistaibeacondemo.fragments.ProfileFragment;
import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;
import com.wci.android.ballistaibeacondemo.http.ListBeaconRequest;
import com.wci.android.ballistaibeacondemo.http.ListBeaconResult;
import com.wci.android.ballistaibeacondemo.models.People;
import com.wherecloud.android.http.RequestManager;
import com.wherecloud.android.http.requests.AbstractRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Locale;

public class MainActivity extends ListActivity implements BeaconConsumer,
        BallistaBeaconFragment.OnFragmentInteractionListener,
        RequestListener<ListBeaconResult>,
        ActionBar.TabListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private AbstractRequest listBeaconRequest;
    private SpiceManager spiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        setContentView(R.layout.activity_main);

        spiceManager = RequestManager.getInstance().getSpiceManager();
        listBeaconRequest = new ListBeaconRequest(getString(R.string.ballista_api_list_beacons), this);

        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("MonitorNotifier", "Did enter Region ID : " + region.getUniqueId());
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("MonitorNotifier", "Did exit Region ID : " + region.getUniqueId());
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i("MonitorNotifier", "Did determine Region ID : " + region.getUniqueId() + "\ti: " + ((i == 0) ? " INSIDE " : " OUTSIDE "));
            }
        });
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i("RangeNotifier", "Did Range Beacons In Region : " + region.getUniqueId() + "\tcount: " + beacons.size());

                for (Beacon _beacon : beacons) {

                    String _UUID = _beacon.getId1().toString();
                    Log.i("RangeNotifier", "Name: " + _beacon.getBluetoothName() + "\t UUID: " + _UUID);

                    String _beaconPayload = BeaconApp.getInstance().getBeaconPayload(_UUID + "-" + _beacon.getId2().toString() + "-" + _beacon.getId3().toString());

                    Log.i("PAYLOAD", _beaconPayload);
                }
            }
        });
        mBeaconManager.bind(this);

        // Set up the action bar.
        //TODO : change to https://developer.android.com/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabStrip.html
        //since it's deprecated
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    //<editor-fold desc="LIFECYCLE">
    @Override
    protected void onStart() {
        super.onStart();
        if (!spiceManager.isStarted()) {
            spiceManager.start(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setProgressBarIndeterminateVisibility(true);

        RequestManager.getInstance().performRequest(listBeaconRequest, MainActivity.this, DurationInMillis.ALWAYS_EXPIRED, spiceManager);

        ((BeaconApp) getApplication()).setMonitoringActivity(this);

        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ((BeaconApp) getApplication()).setMonitoringActivity(null);

        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="Ballista Beacon">
    @Override
    public void onRequestFailure(SpiceException spiceException) {
        spiceException.printStackTrace();
        Toast.makeText(this, R.string.toast_list_beacons_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(ListBeaconResult result) {
        setProgressBarIndeterminateVisibility(false);
        if (result != null) {
            if (result.beacons != null) {
                BeaconApp.getInstance().setBeaconList(result.beacons);
                manageBeacons(result);
            }
        }
    }

    private void manageBeacons(ListBeaconResult result) {
        //unique uuids
        for (BallistaBeacon _beacon : result.beacons) {
            Log.i(TAG, "Received Beacon" + _beacon.toString());
            try {
                Region region = new Region(
                        _beacon.uuid,
                        Identifier.parse(_beacon.uuid),
                        Identifier.fromInt(_beacon.major),
                        Identifier.fromInt(_beacon.minor));

                mBeaconManager.startMonitoringBeaconsInRegion(region);
                mBeaconManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }
    //</editor-fold>

    @Override
    public void onBeaconServiceConnect() {
        Toast.makeText(this, "onBeaconServiceConnect", Toast.LENGTH_SHORT).show();
//        manageBeacons(BeaconApp.getInstance());
    }

    //
    ValueEventListener mPeopleListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                //create people if they dont exists
                //update them otherwise
                BeaconApp.getInstance().peopleHash.put(snapshot.getKey(), new People(snapshot));
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            firebaseError.toException().printStackTrace();
        }
    };

    @Override
    public void onBallistaBeaconItemClick(BallistaBeacon mBeacon) {

        final Intent intent = new Intent(this, BeaconDetailActivity.class);
        intent.putExtra(BeaconDetailActivity.BEACON_KEY, new Gson().toJson(mBeacon));
        startActivity(intent);
    }

    //<editor-fold desc="TABS">
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    //</editor-fold>

    /**
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //TODO: pass init data
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = BallistaBeaconFragment.newInstance("", "");
                    break;
                case 2:
                    fragment = EventHistoryFragment.newInstance("", "");
                    break;
                default:
                    fragment = ProfileFragment.newInstance("", "");
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


}