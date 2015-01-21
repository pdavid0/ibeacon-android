package com.wci.android.ballistaibeacondemo.models;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phil on 2015-01-20.
 */
public class FirebaseManager {
	private static FirebaseManager instance;

	private Firebase mRootRef;
	private Firebase mPeopleRef;

	private List<Person> persons;

	public static FirebaseManager getInstance() {
		if (instance == null) {
			instance = new FirebaseManager();
		}
		return instance;
	}

	private FirebaseManager() {
		mRootRef = new Firebase("https://wherecloud.firebaseio.com/");
		mPeopleRef = new Firebase("https://wherecloud.firebaseio.com/people");

		persons = new ArrayList<>();
		mPeopleRef.addValueEventListener(
				new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {
						for (DataSnapshot aPerson : dataSnapshot.getChildren()) {
							persons.add(new Person(aPerson));
						}
					}

					@Override
					public void onCancelled(FirebaseError firebaseError) {
						return;
					}
				}
		);

	}

	public void addPeopleListener(ValueEventListener listener) {
		mRootRef.addValueEventListener(listener);
	}

	public void updatePeople(Person p, Firebase.CompletionListener listener) {
		final Map<String, Object> _hash = new HashMap<>();
		_hash.put("firstName", p.firstName);
		_hash.put("is_present", p.isPresent);
		_hash.put("device", p.device);

		mPeopleRef.child(p.id).updateChildren(_hash, listener);
	}

	public List<Person> getPersons() {
		return persons;
	}
}
