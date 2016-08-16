package com.github.blockand;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.github.blockand.dao.BlockListDao;
import com.github.blockand.event.UpdateDBEvent;
import com.github.blockand.ext.MyListItem;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;

/**
 * Created by saikou on 2016/8/12 0012.
 * Email uedeck@gmail.com .
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    public static String TAG = "PhoneStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String incomingNumber =
                        intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                BlockListDao blockListDao = new BlockListDao(context);
                Cursor cursor = blockListDao.writableDatabase.rawQuery(String.format("SELECT * " +
                        " FROM BLOCK_LIST" +
                        " WHERE BLOCK_LIST.number = '%s';", incomingNumber), null);
                if (cursor.getCount() > 0) {
                    cursor.moveToLast();
                    MyListItem myListItem = MyListItem.fromCursor(cursor);
                    myListItem.count++;
                    blockListDao.updateById(myListItem._id, myListItem);
                    UpdateDBEvent updateDBEvent = new UpdateDBEvent();
                    updateDBEvent.ListItem = myListItem;
                    EventBus.getDefault().post(updateDBEvent);
                    if (killCall(context)) { // Using the method defined earlier
                        context.startService(new Intent(context, RingtoneService.class));
                    } else {
                        Log.d(TAG, "PhoneStateReceiver **Unable to kill incoming call");
                    }
                }
                blockListDao.writableDatabase.close();
            }
        }
    }

    public boolean killCall(Context context) {
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
            methodGetITelephony.setAccessible(true);
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
