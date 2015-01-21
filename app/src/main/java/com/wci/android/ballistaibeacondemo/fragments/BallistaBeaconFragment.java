package com.wci.android.ballistaibeacondemo.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.wci.android.ballistaibeacondemo.BeaconApp;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.adapters.BeaconAdapter;
import com.wci.android.ballistaibeacondemo.events.RangeBeaconEvent;
import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class BallistaBeaconFragment extends ListFragment {

	private OnFragmentInteractionListener mListener;

	/**
	 * The Adapter which will be used to populate the ListView/GridView with
	 * Views.
	 */
	private BeaconAdapter mAdapter;

	public static BallistaBeaconFragment newInstance(String param1, String param2) {
		BallistaBeaconFragment fragment = new BallistaBeaconFragment();
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BallistaBeaconFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = new BeaconAdapter(getActivity(), new ArrayList<BallistaBeacon>());

		setListAdapter(mAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_ballista_beacon, container, false);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
			BeaconApp.getBus().register(this);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString()
							+ " must implement Listener"
			);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
		BeaconApp.getBus().unregister(this);
	}

	@Override public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (null != mListener) {
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			final BallistaBeacon itemIdAtPosition = (BallistaBeacon)
					getListView().getAdapter().getItem(position);

			mListener.onBallistaBeaconItemClick(itemIdAtPosition);
		}
	}

	@Subscribe
	public void beaconInRange(RangeBeaconEvent event) {
		mAdapter.addAll(event.getBeacons());
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		public void onBallistaBeaconItemClick(BallistaBeacon mBeacon);
	}

}
