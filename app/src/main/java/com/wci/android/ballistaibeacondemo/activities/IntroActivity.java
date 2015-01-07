package com.wci.android.ballistaibeacondemo.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.wci.android.ballistaibeacondemo.R;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeaconManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IntroActivity extends Activity {

    private static final String TAG = "IntroActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    @InjectView(R.id.intro_img_connection) ImageView mImgConnection;
    @InjectView(R.id.intro_img_bt_services) ImageView mImgBtServices;
    @InjectView(R.id.intro_img_ok) ImageView mOk;
    @InjectView(R.id.intro_btn_next) ImageView mNext;

    private boolean mBluetoothAvailable;
    private boolean mNetworkIsAvailable;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.inject(this);

        //TODO: ask user to open bluetooth
        mBluetoothAvailable = bluetoothAvailable();
        if (mBluetoothAvailable) {
            boolean mBluetoothEnabled = isBluetoothEnabled();
            if (!mBluetoothEnabled) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            animateViewAlpha(mImgBtServices, 0);
        } else {
            Toast.makeText(this, "Bluetooth Low Energy is not available on your device. This application is rather useless without it.", Toast.LENGTH_LONG).show();
        }

        mNetworkIsAvailable = networkIsAvailable();
        if (mNetworkIsAvailable) {
            animateViewAlpha(mImgConnection, 1);
        } else {
            Toast.makeText(this, "You do not have connectivity.", Toast.LENGTH_LONG).show();
            animateViewAlpha(mImgConnection, 1);
        }

        if (mBluetoothAvailable && mNetworkIsAvailable) {
            animateViewAlpha(mOk, 2);
            animateViewAlpha(mNext, 3);
        } else {
            Toast.makeText(this, "Something is missing !", Toast.LENGTH_LONG).show();
            mNext.setImageResource(R.drawable.help);
            mNext.setTag(-1);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
                mNext.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary_light)));
            }
            animateViewAlpha(mNext, 1);
        }
    }

    @OnClick(R.id.intro_btn_next)
    public void onClickNext(View v) {

        final boolean initialisationWorked = v.getTag() != null;
        if (initialisationWorked) {
        } else {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Show a view with an Alpha Animation, from 0 to 1.
     *
     * @param view the view to animate
     * @param i    the index of the view, that way we can add a delay to the animation
     */
    private void animateViewAlpha(View view, int i) {
        view.animate().setDuration(1000).setStartDelay(i * 1000).alpha(1).start();
    }

    /**
     * Start {@link com.wci.android.ballistaibeacondemo.activities.MainActivity}
     */
    public void startMainActivity() {
        //TODO : add animations
        startActivity(new Intent(this, MainActivity.class),
                ActivityOptions.makeCustomAnimation(this,
                        R.anim.wci_default_enter_slideleft,
                        R.anim.wci_default_exit_slideleft).toBundle()
        );
    }

    /**
     * Check BT LE availability
     *
     * @return true if bluetooth LE is available on the device
     */
    private boolean bluetoothAvailable() {
        try {
            if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
                return true;
            }
        } catch (RuntimeException e) {
            return false;
        }
        return false;
    }

    /**
     * Check if bluetooth is enabled
     *
     * @return true if enabled
     */
    public boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Check network connectivity
     *
     * @return true if has access to network
     */
    private boolean networkIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Send a HTTP request to Google, act as a ping.
     * TODO: since we send a command it must be put in an AsyncCall
     *
     * @return true if has access to internet
     */
    private boolean hasActiveInternetConnection() {
        if (networkIsAvailable()) {

            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
        //        new AsyncTask<Void, Void, Void>() {
        //            @Override protected Void doInBackground(Void... params) {
        //                if (hasActiveInternetConnection()) {
        //                    runOnUiThread(new Runnable() {
        //                        @Override public void run() {
        //                        }
        //                    });
        //                }
        //                return null;
        //            }
        //        }.execute();
    }

}
