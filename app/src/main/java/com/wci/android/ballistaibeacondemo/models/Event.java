package com.wci.android.ballistaibeacondemo.models;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

/**
 * Created by phil on 2015-01-20.
 */
public class Event {
	public String title   = "";
	public String message = "";
	public String action  = "";

	public Event(String t, String m, String a) {
		title = t;
		message = m;
		action = a;
	}

	public Event(String s) {
		title = s;
	}

	@Override
	public String toString() {

		return title + ": " + message;
	}

	public static Event fromBeacons(Collection<Beacon> beacons) {
		return null;
	}
}
