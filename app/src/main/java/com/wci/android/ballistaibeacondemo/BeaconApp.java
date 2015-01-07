package com.wci.android.ballistaibeacondemo;

import android.app.Application;

import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;
import com.wci.android.ballistaibeacondemo.http.Beacon;

import java.util.Iterator;
import java.util.List;

/**
 * Created by philippe on 14-07-25.
 *
 * @author philippe
 */
public class BeaconApp extends Application {

    private static final String TAG = BeaconApp.class.getSimpleName();

    private static BeaconApp instance;
    private List<Beacon> beaconList;

    public static BeaconApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public void setBeaconList(List<Beacon> beaconList) {
        this.beaconList = beaconList;
    }

    public List<Beacon> getBeaconList() {
        return beaconList;
    }

    public String getBeaconPayload(IBeacon beacon) {
        for (Beacon b : beaconList) {
            if (b.uuid.equals(beacon.getProximityUuid())
                    && b.major == beacon.getMajor()
                    && b.minor == beacon.getMinor()) {
                return b.payload.url;
            }
        }
        return null;
    }

    public Beacon getBeaconFromUUID(String id) {
        for (Iterator<Beacon> iterator = beaconList.iterator(); iterator.hasNext(); ) {
            final Beacon next = iterator.next();
            if (next.toString().equals(id)) {
                return next;
            }
        }
        return null;
    }
}
