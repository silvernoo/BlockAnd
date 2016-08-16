package com.github.blockand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by saikou on 2016/8/12 0012.
 * Email uedeck@gmail.com .
 */
public class RingtoneStopReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, RingtoneService.class));
    }
}