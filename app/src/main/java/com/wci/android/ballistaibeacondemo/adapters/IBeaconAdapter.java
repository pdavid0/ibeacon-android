package com.wci.android.ballistaibeacondemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wci.android.ballistaibeacondemo.BeaconApp;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;

import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by philippe on 14-07-28.
 *
 * @author philippe
 */
public class IBeaconAdapter extends ArrayAdapter<IBeacon> {

    private final LayoutInflater inflater;

    private List<IBeacon> list;

    private final TimerTask timer = new TimerTask() {
        @Override
        public void run() {
            for (IBeacon b : list) {

            }
        }
    };

    public IBeaconAdapter(Context context, List<IBeacon> o) {
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
        IBeacon beacon = getItem(position);
        h.uuid.setText(beacon.getProximityUuid());
        h.major.setText("" + beacon.getMajor());
        h.minor.setText("" + beacon.getMinor());

        String beaconPayload = BeaconApp.getInstance().getBeaconPayload(beacon);

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
    public void add(IBeacon object) {
//        super.add(object);
        if (!list.contains(object)) {
            list.add(object);
        } else {
            list.set(list.indexOf(object), object);
        }
        notifyDataSetChanged();
    }

    @Override
    public void addAll(Collection<? extends IBeacon> collection) {
//        super.addAll(collection);
        for (IBeacon b : collection) {
            this.add(b);
        }
    }

    @Override
    public void remove(IBeacon object) {
//        super.remove(object);
        if (list.contains(object)) {
            list.remove(object);
        }
        notifyDataSetChanged();
    }

    @Override
    public void addAll(IBeacon... items) {
//        super.addAll(items);
        for (IBeacon b : items) {
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
