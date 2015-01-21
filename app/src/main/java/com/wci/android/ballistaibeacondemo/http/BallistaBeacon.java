package com.wci.android.ballistaibeacondemo.http;


import com.wci.android.ballistaibeacondemo.BeaconApp;

import org.altbeacon.beacon.Beacon;

/**
 * Created by philippe on 14-07-25.
 *
 * @author philippe
 */
public class BallistaBeacon {
	public double distance;
	public int    major;
	public int    minor;
	public String uuid;
	public Payload payload = new Payload();
	public String btAddress;
	public int    tx;
	public int    rssi;

	public BallistaBeacon() {
	}

	public BallistaBeacon(int major, int minor, String uuid, Payload payload) {
		this.major = major;
		this.minor = minor;
		this.uuid = uuid;
		this.payload = payload;
	}

	public BallistaBeacon(Beacon beacon) {
		major = beacon.getId2().toInt();
		minor = beacon.getId3().toInt();
		uuid = beacon.getId1().toString();
		btAddress = beacon.getBluetoothAddress();
		tx = beacon.getTxPower();
		rssi = beacon.getRssi();
		distance = beacon.getDistance();
		payload = new Payload(
				BeaconApp
						.getInstance()
						.getBeaconPayload(this.toString())
		);
	}

	@Override
	public String toString() {
		return major + "-" + minor + "-" + uuid;
	}

	@Override
	public boolean equals(Object o) {
		final BallistaBeacon _o = (BallistaBeacon) o;
		return uuid.equalsIgnoreCase(_o.uuid)
				&& major == _o.major
				&& minor == _o.minor;
	}

	public Beacon toBeacon() {
		return new Beacon.Builder()
				.setId1(uuid)
				.setId2(String.valueOf(major))
				.setId3(String.valueOf(major))
				.build();
	}
}
