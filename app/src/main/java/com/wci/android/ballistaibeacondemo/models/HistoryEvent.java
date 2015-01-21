package com.wci.android.ballistaibeacondemo.models;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by phil on 2015-01-20.
 */
public class HistoryEvent {
	public HistoryEvent(Collection<Event> events) {
		this.events = events;
	}

	private Collection<Event> events;

	public HistoryEvent(Event... event) {
		events = new ArrayList<>();
		for (Event e : event) {
			events.add(e);
		}
	}

	public HistoryEvent(Event event, Collection<Event> collection) {

	}

	public Collection<Event> getEvents() {
		return events;
	}
}
