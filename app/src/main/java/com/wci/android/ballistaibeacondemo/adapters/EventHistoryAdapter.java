package com.wci.android.ballistaibeacondemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.models.Event;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by phil on 2015-01-20.
 */
public class EventHistoryAdapter extends ArrayAdapter<Event> {
    private LayoutInflater inflater;
    private ArrayList<Event> events;

    public EventHistoryAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.list_item_event_history, events);
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_event_history, parent, false);
            h = new ViewHolder(convertView);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        Event item = getItem(position);
        h.mText.setText(item.toString());
        if (item.action.equals("danger")) {
            h.mRoot.setBackgroundColor(getContext().getResources().getColor(R.color.accent));
            h.mText.setTextColor(getContext().getResources().getColor(R.color.divider));
        }
        return convertView;
    }

    @Override
    public void addAll(Collection<? extends Event> collection) {
        if (events.size() > 10) {
            events.clear();
        }
        events.addAll(collection);
        notifyDataSetChanged();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'list_item_event_history.xmly.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
     */
    static class ViewHolder {
        @InjectView(R.id.text)
        TextView mText;
        @InjectView(R.id.item_root)
        LinearLayout mRoot;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
