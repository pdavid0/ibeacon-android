package com.wci.android.ballistaibeacondemo.http;

import android.content.Context;

import com.wherecloud.android.http.requests.AbstractRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

/**
 * List Beacons
 *
 * @author philippe
 */
public class ListBeaconRequest extends AbstractRequest<ListBeaconResult> {
	public ListBeaconRequest(String url, Context c) {
		super(url, ListBeaconResult.class, c);
	}

	@Override
	public ListBeaconResult loadDataFromNetwork() throws Exception {

		final HttpEntity<?> entity = createEntity(null, "29BC407B7950477E83988F2B6698C00B");
		final HttpEntity<ListBeaconResult> response = getRestTemplate().exchange(
				mUrl,
				HttpMethod.GET,
				entity,
				getResultType()
		);
		return response.getBody();
	}
}
