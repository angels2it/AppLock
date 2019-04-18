package codes.ait.applock.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import codes.ait.applock.Utils.MyUtils;

/**
 * Created by amitshekhar on 02/05/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(MyUtils.isDrawOverrideGranted(context)) {
            if(MyUtils.isMyServiceRunning(context, AppCheckServices.class)) {
                context.stopService(new Intent(context, AppCheckServices.class));
            }
            context.startService(new Intent(context, AppCheckServices.class));
        }
    }
}
