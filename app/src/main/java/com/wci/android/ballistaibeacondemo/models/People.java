package com.wci.android.ballistaibeacondemo.models;

import com.firebase.client.DataSnapshot;

/**
 * Created by phil on 2015-01-19.
 */
public class People {
    public String firstName = "";
    public boolean isPresent = false;

    public People() {
    }

    public People(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

            final String key = dataSnapshot.getKey();
            if (key.equalsIgnoreCase("first_name")) {
                firstName = dataSnapshot.getValue().toString();
            } else if (key.equalsIgnoreCase("is_present")) {
                isPresent = ((Boolean) dataSnapshot.getValue());
            }
        }
    }
}