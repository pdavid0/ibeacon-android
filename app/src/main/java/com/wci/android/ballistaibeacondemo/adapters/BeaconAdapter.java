package com.wci.android.ballistaibeacondemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.http.BallistaBeacon;

import java.util.Collection;
import java.util.List;

/**
 * Created by philippe on 14-07-28.
 *
 * @author philippe
 */
public class BeaconAdapter extends ArrayAdapter<BallistaBeacon> {

    private final LayoutInflater inflater;

    private List<BallistaBeacon> list;

    public BeaconAdapter(Context context, List<BallistaBeacon> o) {
        super(context, R.layout.list_item_ibeacon, o);
        list = o;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_ibeacon, parent, false);

            h = new ViewHolder(convertView);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        BallistaBeacon beacon = getItem(position);
        //TODO
        h.uuid.setText(beacon.uuid);
        h.major.setText("" + beacon.major);
        h.minor.setText("" + beacon.minor);
        String beaconPayload = beacon.payload.url;

        Picasso.with(getContext())
                .load(beaconPayload)
                .into(h.payload);

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void add(BallistaBeacon object) {
//        super.add(object);
        if (!list.contains(object)) {
            list.add(object);
        } else {
            list.set(list.indexOf(object), object);
        }
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends BallistaBeacon> collection) {
        //        super.addAll(collection);
        for (BallistaBeacon b : collection) {
            this.add(b);
        }
    }

    @Override
    public void remove(BallistaBeacon object) {
        //        super.remove(object);
        if (list.contains(object)) {
            list.remove(object);
        }
        notifyDataSetChanged();
    }

    @Override
    public void addAll(BallistaBeacon... items) {
        //        super.addAll(items);
        for (BallistaBeacon b : items) {
            this.add(b);
        }
    }

    public class ViewHolder {

        TextView uuid;
        TextView major;
        TextView minor;
        ImageView payload;
//        TextView rssi;

        public ViewHolder(View convertView) {
            uuid = (TextView) convertView.findViewById(R.id.list_item_uuid);
            major = (TextView) convertView.findViewById(R.id.list_item_major_id);
            minor = (TextView) convertView.findViewById(R.id.list_item_minor_id);
            payload = (ImageView) convertView.findViewById(R.id.list_item_payload);
//            uuid = (TextView) convertView.findViewById(R.id.list_item_ssid);
        }
    }
}
