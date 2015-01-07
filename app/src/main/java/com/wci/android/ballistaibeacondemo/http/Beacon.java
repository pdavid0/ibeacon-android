package com.wci.android.ballistaibeacondemo.http;

import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;

/**
 * Created by philippe on 14-07-25.
 *
 * @author philippe
 */
public class Beacon {
    public int major;
    public int minor;
    public String uuid;
    public Payload payload = new Payload();
    public double accuracy;
    public String btAddress;
    public int tx;
    public int proximity;
    public int rssi;

    public Beacon() {
    }

    public Beacon(int major, int minor, String uuid, Payload payload) {
        this.major = major;
        this.minor = minor;
        this.uuid = uuid;
        this.payload = payload;
    }

    public Beacon(IBeacon beacon) {
        major = beacon.getMajor();
        minor = beacon.getMinor();
        uuid = beacon.getProximityUuid();
        accuracy = beacon.getAccuracy();
        btAddress = beacon.getBluetoothAddress();
        tx = beacon.getTxPower();
        rssi = beacon.getRssi();
        proximity = beacon.getProximity();
    }

    @Override
    public String toString() {
        return major + "-" + minor + "-" + uuid;
    }
}
