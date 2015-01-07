package com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.simulator;


import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;

import java.util.List;

/**
 * Created by dyoung on 4/18/14.
 */
public interface BeaconSimulator {
    public List<IBeacon> getBeacons();
}
