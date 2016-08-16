package com.github.blockand;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

/**
 * Created by saikou on 2016/8/12 0012.
 * Email uedeck@gmail.com .
 */
public class RingtoneService extends Service {

    private static int PUSH_COUNTER_ID = 123;
    private MediaPlayer player;
    private RingtoneStopReceiver ringtoneStopReceiver = new RingtoneStopReceiver();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        unregisterReceiver(ringtoneStopReceiver);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(getBaseContext(), Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent notificationIntent = new Intent("blockand.provider.Telephony.REMOVE_NOTIFICATION");
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setDeleteIntent(PendingIntent.getBroadcast(this, PUSH_COUNTER_ID, notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT))
                .setLargeIcon(BitmapFactory.decodeResource(res, android.R.drawable.sym_def_app_icon))
                .setTicker(res.getString(R.string.notification_title))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.notification_title))
                .setContentText(res.getString(R.string.notification_title));
        Notification n = builder.getNotification();
        n.flags = Notification.FLAG_AUTO_CANCEL;
        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(PUSH_COUNTER_ID, n);
        registerReceiver(ringtoneStopReceiver, new IntentFilter("blockand.provider.Telephony.REMOVE_NOTIFICATION"));
    }
}
