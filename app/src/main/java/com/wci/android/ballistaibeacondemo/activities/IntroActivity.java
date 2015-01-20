package com.wci.android.ballistaibeacondemo.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.wci.android.ballistaibeacondemo.R;
import com.wherecloud.android.view.animation.CustomAnims;

import org.altbeacon.beacon.BeaconManager;
import org.apache.http.conn.ClientConnectionManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class IntroActivity extends Activity {

    private static final String TAG = "IntroActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_WIFI = 2;
    private static final int REQUEST_ENABLE_DATA = 3;

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

        int mTintColor = getResources().getColor(R.color.primary_light);
        mNetworkIsAvailable = networkIsAvailable();
        if (mNetworkIsAvailable) {
            animateViewAlpha(mImgConnection, 0);
        } else {
            Toast.makeText(this, "You do not have connectivity.", Toast.LENGTH_SHORT).show();
            animateViewAlpha(mImgConnection, 0, mTintColor);
            CustomAnims.bounce(mImgConnection);
        }

        //TODO: ask user to open bluetooth
        mBluetoothAvailable = bluetoothAvailable();
        if (mBluetoothAvailable) {
            boolean mBluetoothEnabled = isBluetoothEnabled();
            if (!mBluetoothEnabled) {

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            animateViewAlpha(mImgBtServices, 1);
        } else {
            Toast.makeText(this, "Bluetooth Low Energy is not enabled on your device. This application is rather useless without it. Open bluetooth and restart App.", Toast.LENGTH_SHORT).show();
            animateViewAlpha(mImgBtServices, 1, mTintColor);
            CustomAnims.bounce(mImgBtServices);
        }

        if (mBluetoothAvailable && mNetworkIsAvailable) {
            animateViewAlpha(mOk, 2);
            animateViewAlpha(mNext, 3);
            mNext.setTag(1);//initialisation worked
        } else {
            Toast.makeText(this, "Something is missing !", Toast.LENGTH_SHORT).show();
            mNext.setImageResource(R.drawable.help);
            mNext.setTag(-1);//initialisation didn't work
            mOk.setImageResource(R.drawable.close_96);
            animateViewAlpha(mOk, 2, mTintColor);
            animateViewAlpha(mNext, 3, mTintColor);

            CustomAnims.bounce(mNext);
            mNext.animate().y(200).translationY(-100).setStartDelay(2800).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
    }

    @OnClick(R.id.intro_btn_next)
    public void onClickNext(View v) {

        final boolean initialisationWorked = v.getTag() != -1;
        if (initialisationWorked) {
            finish();
            startMainActivity();

        } else {
            Toast _toast = Toast.makeText(this, "You need to fix problems before continuing !", Toast.LENGTH_SHORT);
            _toast.setGravity(Gravity.TOP, 0, 0);
            _toast.show();
        }
    }

    @OnClick(R.id.intro_img_bt_services)
    public void onBtErrorClick(View v) {

        CustomAnims.bounce(mImgBtServices);
        if (!mBluetoothAvailable) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else {
            CustomAnims.bounce(mNext);
        }
    }

    @OnClick(R.id.intro_img_connection)
    public void onConnectionErrorClick(View v) {
        if (!mNetworkIsAvailable) {
            CustomAnims.bounce(mImgConnection);
            Intent gpsOptionsIntent = new Intent(  android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivityForResult(gpsOptionsIntent,REQUEST_ENABLE_WIFI);
        }else {
            CustomAnims.bounce(mNext);
        }
    }

    @OnClick(R.id.intro_img_ok)
    public void onErrorClick(View v) {
        CustomAnims.bounce(mOk);
        CustomAnims.bounce(mNext);
        CustomAnims.bounce(mImgConnection);
        CustomAnims.bounce(mImgBtServices);
        Toast.makeText(this, "Try Clicking on the missing feature to enable them !;)", Toast.LENGTH_SHORT).show();
    }
    /**
     * Show a view with an Alpha Animation, from 0 to 1.
     *
     * @param view the view to animate
     * @param i    the index of the view, that way we can add a delay to the animation
     */
    private void animateViewAlpha(View view, int i) {
        view.animate().setDuration(1000).setStartDelay(i * 800).alpha(1).start();
    }

    /**
     * Show a view with an Alpha Animation, from 0 to 1.
     *
     * @param view the view to animate
     * @param i    the index of the view, that way we can add a delay to the animation
     * @param tint
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateViewAlpha(ImageView view, int i, int tint) {
        view.setColorFilter(tint);
        view.animate().setDuration(1000).setStartDelay(i * 800).alpha(1).start();
    }

    /**
     * Start {@link com.wci.android.ballistaibeacondemo.activities.MainActivity}
     */
    public void startMainActivity() {
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
            final BeaconManager _instanceForApplication = BeaconManager.getInstanceForApplication(this);
            return _instanceForApplication.checkAvailability();
        } catch (RuntimeException e) {
            Toast.makeText(this, "Bluetooth Low Energy is not available on your device. This application is rather useless without it.", Toast.LENGTH_SHORT).show();
            return false;
        }
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

//    /**
//     * Send a HTTP request to Google, act as a ping.
//     * TODO: since we send a command it must be put in an AsyncCall
//     *
//     * @return true if has access to internet
//     */
//    private boolean hasActiveInternetConnection() {
//        if (networkIsAvailable()) {
//
//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                Log.e(TAG, "Error checking internet connection", e);
//            }
//        } else {
//            Log.d(TAG, "No network available!");
//        }
//        return false;
//        //        new AsyncTask<Void, Void, Void>() {
//        //            @Override protected Void doInBackground(Void... params) {
//        //                if (hasActiveInternetConnection()) {
//        //                    runOnUiThread(new Runnable() {
//        //                        @Override public void run() {
//        //                        }
//        //                    });
//        //                }
//        //                return null;
//        //            }
//        //        }.execute();
//    }

}
