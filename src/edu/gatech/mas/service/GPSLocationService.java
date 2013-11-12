package edu.gatech.mas.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.R;

/**
 * Service that gets the user location and stores it in the database.
 * 
 * @author Pawel
 * 
 */
public class GPSLocationService extends Service {

	private LocationManager locationManager;
	private LocationListener locationListener;

	private static long refreshTimeMilis = 2000;
	private static long minDistanceMeters = 20;

	private int lastStatus = 0;

	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		GPSLocationService getService() {
			return GPSLocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		startGPSService();

		// Display a notification about starting a service. An icon will be
		// displayed in the
		// status bar.
		showNotification();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		// continue running service until it is explicitly stopped, so return
		// sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		shutdownLoggerService();

		// Cancel the persistent notification.
		mNotificationManager.cancel(R.string.local_service_stopped);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT)
				.show();

	}

	private void startGPSService() {

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationListener = new MyLocationListener();

		// exceptions will be thrown if provider is not permitted.
		boolean gps_enabled = false, network_enabled = false;
		gps_enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		network_enabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// if no provider is enabled, ask user to enable it in the settings
		if (!gps_enabled && !network_enabled) {
			Intent gpsOptionsIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(gpsOptionsIntent);
		}

		// start updating location with a chosen provider
		locationManager.requestLocationUpdates(
				gps_enabled ? LocationManager.GPS_PROVIDER
						: LocationManager.NETWORK_PROVIDER, refreshTimeMilis,
				minDistanceMeters, locationListener);
	}

	private void shutdownLoggerService() {
		locationManager.removeUpdates(locationListener);
	}

	/**
	 * Listener that returns user location.
	 * 
	 * @author Pawel
	 * 
	 */
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location loc) {
			if (loc != null) {

				Toast.makeText(
						getBaseContext(),
						"Location stored: \nLat: "
								+ loc.getLatitude()
								+ " \nLon: "
								+ loc.getLongitude()
								+ " \nAlt: "
								+ (loc.hasAltitude() ? loc.getAltitude() + "m"
										: "?")
								+ " \nAcc: "
								+ (loc.hasAccuracy() ? loc.getAccuracy() + "m"
										: "?"), Toast.LENGTH_SHORT).show();

				// TODO: insert into database here, instead of showing it to the
				// user. Should be something like this: (also see code below in PostToDb)

				// new PostToDb().execute(generateUsername(),
				// loc.getLongitude(), loc.getLatitude(), loc.getAltitude(),
				// loc.getAccuracy();)
			}
		}

		String generateUsername() {
			final int length = 8;
			StringBuffer sb = new StringBuffer();
			for (int x = 0; x < length; x++) {
				sb.append((char) ((int) (Math.random() % 26) + 97));
			}
			return sb.toString();
		}

		class PostToDb extends AsyncTask<String, Void, Boolean> {

			@Override
			protected Boolean doInBackground(String... strings) {

				String url = "http://dev.m.gatech.edu/d/pkwiecien3/w/colab/c/api/username/"
						+ strings[0];

				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);

				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("longitude",
						strings[1]));
				urlParameters
						.add(new BasicNameValuePair("latitude", strings[2]));
				urlParameters
						.add(new BasicNameValuePair("altitude", strings[3]));
				urlParameters
						.add(new BasicNameValuePair("accuracy", strings[4]));

				try {
					post.setEntity(new UrlEncodedFormEntity(urlParameters));

					// Execute HTTP Post Request
					HttpResponse response = client.execute(post);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
				return true;
			}
		}

		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
					Toast.LENGTH_SHORT).show();

		}

		public void onProviderEnabled(String provider) {
			Toast.makeText(getBaseContext(), "onProviderEnabled: " + provider,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			String showStatus = null;
			if (status == LocationProvider.AVAILABLE)
				showStatus = "Available";
			if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
				showStatus = "Temporarily Unavailable";
			if (status == LocationProvider.OUT_OF_SERVICE)
				showStatus = "Out of Service";
			if (status != lastStatus) {
				Toast.makeText(getBaseContext(), "new status: " + showStatus,
						Toast.LENGTH_SHORT).show();
			}
			lastStatus = status;
		}
	}

	private NotificationManager mNotificationManager;

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		CharSequence text = getText(R.string.local_service_started);

		// Set the icon, scrolling text and timestamp Notification
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ClassListActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, getText(R.string.service_name),
				text, contentIntent);

		// Send the notification.
		// We use a string id because it is a unique number. We use it later to
		// cancel.
		mNotificationManager.notify(R.string.local_service_started,
				notification);
	}

}
