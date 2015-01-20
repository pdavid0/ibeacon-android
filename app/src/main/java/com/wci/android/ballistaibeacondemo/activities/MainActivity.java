package com.wci.android.ballistaibeacondemo.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.wci.android.ballistaibeacondemo.BeaconApp;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.adapters.BeaconAdapter;
import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;
import com.wci.android.ballistaibeacondemo.http.ListBeaconRequest;
import com.wci.android.ballistaibeacondemo.http.ListBeaconResult;
import com.wherecloud.android.http.RequestManager;
import com.wherecloud.android.http.requests.AbstractRequest;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MainActivity extends ListActivity implements BeaconConsumer, RequestListener<ListBeaconResult> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SpiceManager spiceManager;

    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private AbstractRequest listBeaconRequest;

    private BeaconAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);

        setContentView(R.layout.activity_main);

        //init adapter with empty list
        ListView _listView = getListView();
        mAdapter = new BeaconAdapter(this, new ArrayList<BallistaBeacon>());
        _listView.setAdapter(mAdapter);

        spiceManager = RequestManager.getInstance().getSpiceManager();
        listBeaconRequest = new ListBeaconRequest(getString(R.string.ballista_api_list_beacons), this);


        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override public void didEnterRegion(Region region) {
                Log.i("MonitorNotifier", "Did enter Region ID : " + region.getUniqueId());
//                new BallistaBeacon(region);
//                HashMap<String, BallistaBeacon> beaconHash = BeaconApp.getInstance().getBeaconHash();
//                for (String identifier : beaconHash.keySet()) {
//                    identifier.equalsIgnoreCase()
//                }
            }

            @Override public void didExitRegion(Region region) {
                Log.i("MonitorNotifier", "Did exit Region ID : " + region.getUniqueId());
            }

            @Override public void didDetermineStateForRegion(int i, Region region) {
                Log.i("MonitorNotifier", "Did determine Region ID : " + region.getUniqueId() + "\ti: " + ((i == 0) ? " INSIDE " : " OUTSIDE "));
            }
        });
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i("RangeNotifier", "Did Range Beacons In Region : " + region.getUniqueId() + "\tcount: " + beacons.size());

                for (Beacon _beacon : beacons) {

                    String _UUID = _beacon.getId1().toString();
                    Log.i("RangeNotifier", "Name: " + _beacon.getBluetoothName() + "\t UUID: " + _UUID);

                    String _beaconPayload = BeaconApp.getInstance().getBeaconPayload(_UUID + "-" + _beacon.getId2().toString() + "-" + _beacon.getId3().toString());

                    Log.i("PAYLOAD", _beaconPayload);
                }
                mAdapter.updateAll(beacons);
            }
        });
        mBeaconManager.bind(this);
    }

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        BallistaBeacon mBeacon = mAdapter.getItem(position);
//        mBeacon.payload.url = BeaconApp.getInstance().getBeaconPayload(mBeacon);

        final Intent intent = new Intent(this, BeaconDetailActivity.class);
        intent.putExtra(BeaconDetailActivity.BEACON_KEY, new Gson().toJson(mBeacon));
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

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
                mAdapter.addAll(result.beacons);

                ArrayList<String> _uuids = new ArrayList<>();
                //unique uuids
                for (BallistaBeacon _beacon : result.beacons) {
                    final String _uuid = _beacon.uuid;
                    if (!_uuids.contains(_uuid)) {
                        _uuids.add(_uuid);
                    }
                }
                for (String s : _uuids) {
                    try {
                        Region region = new Region(s, Identifier.parse(s), null, null);

                        mBeaconManager.startMonitoringBeaconsInRegion(region);
                        mBeaconManager.startRangingBeaconsInRegion(region);
                    } catch (RemoteException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    @Override public void onBeaconServiceConnect() {
        Toast.makeText(this, "onBeaconServiceConnect", Toast.LENGTH_SHORT).show();
    }
}