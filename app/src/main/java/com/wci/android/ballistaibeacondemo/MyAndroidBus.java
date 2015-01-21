package com.wci.android.ballistaibeacondemo;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by phil on 2015-01-20.
 */
public class MyAndroidBus extends Bus {
	private final Handler mainThread = new Handler(Looper.getMainLooper());

	@Override
	public void post(final Object event) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			super.post(event);
		} else {
			mainThread.post(
					new Runnable() {
						@Override
						public void run() {
							MyAndroidBus.super.post(event);
						}
					}
			);
		}
	}
}