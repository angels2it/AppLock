package applock.mindorks.com.applock.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import applock.mindorks.com.applock.Utils.MyUtils;

/**
 * Created by amitshekhar on 02/05/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(MyUtils.isDrawOverrideGranted(context)) {
            context.startService(new Intent(context, AppCheckServices.class));
        }
    }
}
