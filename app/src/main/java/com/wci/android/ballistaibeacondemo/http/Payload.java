package com.wci.android.ballistaibeacondemo.http;

/**
 * Created by philippe on 14-07-25.
 *
 * @author philippe
 */
public class Payload {
	public String url = "";

	public Payload() {
	}

	public Payload(String beaconPayload) {
		url = beaconPayload;
	}

	@Override public String toString() {
		return url;
	}
}
