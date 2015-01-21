package com.wci.android.ballistaibeacondemo.events;

import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by phil on 2015-01-20.
 */
public class RangeBeaconEvent {
    private Collection<BallistaBeacon> beacons;

    public RangeBeaconEvent(Collection<Beacon> beacons) {
        this.beacons = new ArrayList<>();
        for (Beacon beacon : beacons) {
            this.beacons.add(new BallistaBeacon(beacon));
        }
    }

    public Collection<BallistaBeacon> getBeacons() {
        return beacons;
    }
}
