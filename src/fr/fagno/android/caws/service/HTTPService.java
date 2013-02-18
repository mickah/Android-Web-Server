package fr.fagno.android.caws.service;

import fr.fagno.android.caws.webserver.WebServer;
import fr.fagno.android.caws.ui.CAWSActivity;
import fr.fagno.android.caws.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HTTPService extends Service {
	private static final int NOTIFICATION_STARTED_ID = 1;

	private NotificationManager notifyManager = null;
	private WebServer server = null;

	@Override
	public void onCreate() {
		super.onCreate();
		notifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		server = new WebServer(this, notifyManager);
	}

	@Override
	public void onDestroy() {
		server.stopThread();
		notifyManager.cancel(NOTIFICATION_STARTED_ID);
		notifyManager = null;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		server.startThread();
		showNotification();
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void showNotification(){
		String text = getString(R.string.service_started);
		Notification notification = new Notification(R.drawable.caws_notification, text, System.currentTimeMillis());
		Intent startIntent = new Intent(this,CAWSActivity.class);
		startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(this, 0, startIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, 
				getString(R.string.notification_started_title), 
				getString(R.string.notification_started_text), 
				intent);
		notifyManager.notify(NOTIFICATION_STARTED_ID, notification);
	}
}
