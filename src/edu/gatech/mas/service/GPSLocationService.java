package edu.gatech.mas.service;

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
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.gatech.mas.ClassListActivity;
import edu.gatech.mas.R;

/**
 * GPSLocationServic is responsible for getting user location and storing it in
 * the local database.
 * 
 * @author Pawel
 * 
 */
public class GPSLocationService extends Service {

	private static String TAG = GPSLocationService.class.getSimpleName();

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;

	/** default times for getting a new location of the user */
	private static long refreshTimeMilis = 2000;
	private static long minDistanceMeters = 20;

	/** status of location provider */
	private int mLastStatus = 0;

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

		// Display a notification in status bar about starting a service.
		showNotification();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		// continue running service until it is explicitly stopped (thus sticky)
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

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();

		// exceptions will be thrown if provider is not permitted.
		boolean gpsEnabled = false, networkEnabled = false;
		gpsEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		networkEnabled = mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// if no provider is enabled, ask user to enable it in the settings
		if (!gpsEnabled && !networkEnabled) {
			Intent gpsOptionsIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(gpsOptionsIntent);
		}

		// start updating location with a chosen provider
		mLocationManager.requestLocationUpdates(
				gpsEnabled ? LocationManager.GPS_PROVIDER
						: LocationManager.NETWORK_PROVIDER, refreshTimeMilis,
				minDistanceMeters, mLocationListener);
	}

	private void shutdownLoggerService() {
		mLocationManager.removeUpdates(mLocationListener);
	}

	/**
	 * Location Listener that returns user location.
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

				// TODO Amy: insert into database here, instead of showing it to
				// the
				// user.
			}
		}

		/**
		 * Notify user that location provider is unavailable.
		 */
		public void onProviderDisabled(String provider) {
			Toast.makeText(getBaseContext(), "onProviderDisabled: " + provider,
					Toast.LENGTH_SHORT).show();
		}

		/**
		 * Notify user that location provider is available.
		 */
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
			if (status != mLastStatus) {
				Toast.makeText(getBaseContext(), "new status: " + showStatus,
						Toast.LENGTH_SHORT).show();
			}
			mLastStatus = status;
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
		mNotificationManager.notify(R.string.local_service_started,
				notification);
	}

}
