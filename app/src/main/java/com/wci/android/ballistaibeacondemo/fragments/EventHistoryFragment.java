package com.wci.android.ballistaibeacondemo.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.squareup.otto.Subscribe;
import com.wci.android.ballistaibeacondemo.BeaconApp;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.adapters.EventHistoryAdapter;
import com.wci.android.ballistaibeacondemo.models.Event;
import com.wci.android.ballistaibeacondemo.models.HistoryEvent;

import java.util.ArrayList;

/**
 * Created by phil on 2015-01-19.
 */
public class EventHistoryFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private EventHistoryAdapter mAdapter;
    private AbsListView mList;

    // TODO: Rename and change types of parameters
    public static EventHistoryFragment newInstance(String param1, String param2) {
        EventHistoryFragment fragment = new EventHistoryFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventHistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new EventHistoryAdapter(getActivity(), new ArrayList<Event>());
        setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getListView().addHeaderView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BeaconApp.getBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BeaconApp.getBus().unregister(this);
    }

    @Subscribe
    public void onNewEvent(HistoryEvent obj) {
        mAdapter.addAll(obj.getEvents());
    }

}
