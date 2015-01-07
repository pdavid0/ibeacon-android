package com.wci.android.ballistaibeacondemo.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import com.wci.android.ballistaibeacondemo.adapters.IBeaconAdapter;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeaconConsumer;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeaconManager;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.MonitorNotifier;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.RangeNotifier;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.Region;
import com.wci.android.ballistaibeacondemo.http.Beacon;
import com.wci.android.ballistaibeacondemo.http.ListBeaconRequest;
import com.wci.android.ballistaibeacondemo.http.ListBeaconResult;
import com.wherecloud.android.http.RequestManager;
import com.wherecloud.android.http.requests.AbstractRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends ListActivity implements IBeaconConsumer, RequestListener<ListBeaconResult> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long TIME_TO_REACT = 5000L;

    private SpiceManager spiceManager;

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);
    private AbstractRequest listBeaconRequest;

    private ListView listView;
    private IBeaconAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

//        checkBluetoothAvailable();
        IBeaconManager.setDebug(false);
        iBeaconManager.setForegroundBetweenScanPeriod(1000L);
        iBeaconManager.bind(this);

        setContentView(R.layout.activity_main);

        //init adapter with empty list
        listView = getListView();
        mAdapter = new IBeaconAdapter(this, new ArrayList<IBeacon>());
        listView.setAdapter(mAdapter);

        spiceManager = RequestManager.getInstance().getSpiceManager();
        listBeaconRequest = new ListBeaconRequest(getString(R.string.ballista_api_list_beacons), this);

        handler.postDelayed(runnable, 1000L);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iBeaconManager.isBound(this)) {
            iBeaconManager.setBackgroundMode(this, true);
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

        IBeacon item = mAdapter.getItem(position);

        Beacon beacon = new Beacon(item);
        beacon.payload.url = BeaconApp.getInstance().getBeaconPayload(item);

        final Intent intent = new Intent(this, BeaconDetailActivity.class);
        intent.putExtra(BeaconDetailActivity.BEACON_KEY, new Gson().toJson(beacon));
        startActivity(intent);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, R.string.toast_list_beacons_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(ListBeaconResult result) {
        setProgressBarIndeterminateVisibility(false);
        if (result != null) {
            if (result.beacons != null) {
                BeaconApp.getInstance().setBeaconList(result.beacons);

                //
                for (Beacon b : result.beacons) {
                    try {
                        Region region = new Region(b.toString(), b.uuid, b.major, b.minor);
                        iBeaconManager.startMonitoringBeaconsInRegion(region);
                        iBeaconManager.startRangingBeaconsInRegion(region);
                    } catch (RemoteException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(final Region region) {
                Log.e(TAG, "I just saw an iBeacon named " + region.getUniqueId() + " for the first time!");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        IBeacon object = new IBeacon(region.getProximityUuid(), region.getMajor(), region.getMinor());
                        mSeenBeacons.put(new Beacon(object).toString()
                                , SystemClock.elapsedRealtimeNanos());
                        mAdapter.add(object);
                    }
                });
            }

            @Override
            public void didExitRegion(final Region region) {
                Log.e(TAG, "I no longer see an iBeacon named " + region.getUniqueId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(new IBeacon(region.getProximityUuid(), region.getMajor(), region.getMinor()));
                    }
                });
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.e(TAG, "I have just switched from seeing/not seeing iBeacons: " + state);
            }

        });

        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<IBeacon> iBeacons, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(iBeacons);
                        for (IBeacon iBeacon : iBeacons) {
                            mSeenBeacons.put(new Beacon(iBeacon).toString(), SystemClock.elapsedRealtimeNanos());
                        }
                    }
                });
            }
        });
    }

    public void showNotification(Beacon beacon) {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(this)
                .setContentTitle("Hello from : " + beacon.uuid)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText("You were near an iBeacon for at least " + TIME_TO_REACT + "seconds");

        Intent resultIntent = new Intent(this, BeaconDetailActivity.class);
        resultIntent.putExtra(BeaconDetailActivity.BEACON_KEY, new Gson().toJson(beacon));
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    ConcurrentHashMap<String, Long> mSeenBeacons = new ConcurrentHashMap<String, Long>();
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Iterator keyIterator = mSeenBeacons.keySet().iterator();
            ArrayList<String> toRemove = new ArrayList<String>();
            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                Long mLastSeenTime = mSeenBeacons.get(key);

                if (SystemClock.elapsedRealtimeNanos() - mLastSeenTime >= TIME_TO_REACT) {

                    toRemove.add(key);
                    Beacon beaconFromUUID = BeaconApp.getInstance().getBeaconFromUUID(key);
                    if (beaconFromUUID != null) {
                        showNotification(beaconFromUUID);
                    }
                }
            }
            for (String s : toRemove) {
                mSeenBeacons.remove(s);
            }
            handler.postDelayed(this, 1000L);
        }
    };
}