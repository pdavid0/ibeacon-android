package com.wci.android.ballistaibeacondemo.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.http.Beacon;

/**
 * Activity (themed as a dialog) to show a big picture of the payload of a iBeacon.
 *
 * @author philippe
 */
public class BeaconDetailActivity extends Activity {
    public static String BEACON_KEY = "beacon";
    private ImageView mPayloadIv;
    private TextView mUuidTv;
    private Beacon mBeacon;
    private TextView mAccuracyTv;
    private TextView mBtAddressTv;
    private TextView mProximityTv;
    private TextView mRSSITv;
    private TextView mTxTv;
    private TextView mMajorTv;
    private TextView mMinorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_beacon_detail);

        this.mPayloadIv = (ImageView) findViewById(R.id.activity_beacon_detail_image);
        this.mUuidTv = (TextView) findViewById(R.id.activity_beacon_detail_uuid);
        this.mAccuracyTv = (TextView) findViewById(R.id.activity_beacon_detail_accuracy);
        this.mBtAddressTv = (TextView) findViewById(R.id.activity_beacon_detail_bt_address);
        this.mProximityTv = (TextView) findViewById(R.id.activity_beacon_detail_proximity);
        this.mRSSITv = (TextView) findViewById(R.id.activity_beacon_detail_rssi);
        this.mTxTv = (TextView) findViewById(R.id.activity_beacon_detail_tx);
        this.mMajorTv = (TextView) findViewById(R.id.activity_beacon_detail_major);
        this.mMinorTv = (TextView) findViewById(R.id.activity_beacon_detail_minor);

        final Intent intent = getIntent();
        if (intent.hasExtra(BEACON_KEY)) {

            this.mBeacon = new Gson().fromJson(intent.getExtras().getString(BEACON_KEY, "{}"), Beacon.class);

            if (mBeacon != null) {

                Picasso.with(this).load(mBeacon.payload.url).into(mPayloadIv);
                mUuidTv.setText("UUID : " + mBeacon.uuid);
                mAccuracyTv.setText("Accuracy : " + mBeacon.accuracy);
                mBtAddressTv.setText("BT Address : " + mBeacon.btAddress);
                mMajorTv.setText("Major : " + mBeacon.major);
                mMinorTv.setText("Minor : " + mBeacon.minor);
                mRSSITv.setText("RDDI : " + mBeacon.rssi);
                mTxTv.setText("TX : " + mBeacon.tx);
                mProximityTv.setText("Proximity : " + mBeacon.proximity);
            } else {
                finish();
            }
        } else {
            finish();
        }

        //Cancel notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
