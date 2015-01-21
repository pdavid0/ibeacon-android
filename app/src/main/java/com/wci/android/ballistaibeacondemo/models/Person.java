package com.wci.android.ballistaibeacondemo.models;

import com.firebase.client.DataSnapshot;

import java.util.Map;

/**
 * Created by phil on 2015-01-19.
 */
public class Person {
	public String  firstName = "";
	public boolean isPresent = false;
	public String  id        = "";
	public String  device    = "";

	public Person() {
	}

	public Person(DataSnapshot aPerson) {

		final String id = aPerson.getKey();
		Map<String, Object> value = (Map<String, Object>) aPerson.getValue();
		firstName = (String) value.get("first_name");
		isPresent = (boolean) value.get("is_present");
		device = (String) value.get("device");
	}

	@Override
	public String toString() {
		return firstName;
	}

	public boolean hasDevice() {
		return device == null || !device.equalsIgnoreCase("");
	}
}