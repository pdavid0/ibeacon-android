package com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.client;

import android.os.Handler;

import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeacon;
import com.wci.android.ballistaibeacondemo.bluetooth.ibeacon.IBeaconDataNotifier;


public class NullIBeaconDataFactory implements IBeaconDataFactory {

    @Override
    public void requestIBeaconData(IBeacon iBeacon, final IBeaconDataNotifier notifier) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifier.iBeaconDataUpdate(null, null, new DataProviderException("Please upgrade to the Pro version of the Android iBeacon Library."));
            }
        });
    }
}

