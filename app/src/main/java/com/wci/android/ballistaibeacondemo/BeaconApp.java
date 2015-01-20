package com.wci.android.ballistaibeacondemo;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.wci.android.ballistaibeacondemo.activities.MainActivity;
import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.HashMap;
import java.util.List;

/**
 * Created by philippe on 14-07-25.
 *
 * @author philippe
 */
public class BeaconApp extends Application implements BootstrapNotifier {

    private static final String TAG = BeaconApp.class.getSimpleName();

    private static BeaconApp instance;
    //    private List<BallistaBeacon> beaconList;
    HashMap<String, BallistaBeacon> beaconHash = new HashMap<>();
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private BeaconConsumer monitoringActivity = null;
    private BeaconManager beaconManager;

    public static BeaconApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // wake up the app when any beacon is seen (you can specify specific id filers in the parameters below)
        Region region = new Region("myRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    public HashMap<String, BallistaBeacon> getBeaconHash() {
        return beaconHash;
    }

    public void setBeaconList(List<BallistaBeacon> beaconList) {

        for (BallistaBeacon _ballistaBeacon : beaconList) {
            String key = _ballistaBeacon.toString();//_ballistaBeacon.uuid + "-" + _ballistaBeacon.major + "-" + _ballistaBeacon.minor;
            beaconHash.put(key, _ballistaBeacon);
        }
    }

    public String getBeaconPayload(String key) {
        return beaconHash.get(key).payload.url;
    }

    @Override
    public void didEnterRegion(Region region) {

        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.i(TAG, "$ did enter region.");
//        Log.i(TAG, "Region : " + region.getUniqueId());
//        if (!haveDetectedBeaconsSinceBoot) {
//            Log.i(TAG, "auto launching MainActivity");
//
//            // The very first time since boot that we detect an beacon, we launch the
//            // MainActivity
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            // Important:  make sure to add android:launchMode="singleInstance" in the manifest
//            // to keep multiple copies of this activity from getting created if the user has
//            // already manually launched the app.
//            this.startActivity(intent);
//            haveDetectedBeaconsSinceBoot = true;
//        } else {
//            if (monitoringActivity != null) {
//                // If the Monitoring Activity is visible, we log info about the beacons we have
//                // seen on its display
//                toast("I see a beacon again");
//            } else {
//                // If we have already seen beacons before, but the monitoring activity is not in
//                // the foreground, we send a notification to the user on subsequent detections.
//                Log.i(TAG, "Sending notification.");
//                sendNotification();
//            }
//        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i(TAG, "$ did exit region.");
//        if (monitoringActivity != null) {
//            toast("I no longer see a beacon.");
//        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
//        if (monitoringActivity != null) {
//            toast("I have just switched from seeing/not seeing beacons: " + state);
//        }
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("Beacon Reference Application")
                        .setContentText("An beacon is nearby.")
                        .setSmallIcon(R.drawable.success_512_w);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(BeaconConsumer activity) {
        this.monitoringActivity = activity;
    }
}
