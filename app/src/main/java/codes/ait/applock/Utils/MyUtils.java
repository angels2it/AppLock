package codes.ait.applock.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

/**
 * Created by amitshekhar on 22/05/15.
 */
public class MyUtils {
    static List<String> permissions = Arrays.asList("draw", "phone", "usage");
    public static int DRAW_REQUEST_CODE = 5463 & 0xffffff00;
    public static int PHONE_REQUEST_CODE = 5464 & 0xffffff00;
    public static int APP_USAGE_REQUEST_CODE = 5465 & 0xffffff00;
    public static int currentPermission = 0;
    /**
     * Checks if user has internet connectivity
     *
     * @param context
     * @return
     */
    public static boolean isInternetConnected(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
            return true;
        else
            return false;
    }

    public static boolean checkAppPermissions (Activity context) {
        if(currentPermission < permissions.size()) {
            String permission = permissions.get(currentPermission);
            if(permission == "draw") {
                if(isDrawOverrideGranted(context)) {
                    currentPermission ++;
                    return checkAppPermissions(context);
                } else {
                    checkDrawOverlayPermission(context);
                    return true;
                }
            } else if (permission == "phone") {
                if(isPhoneGranted(context)) {
                    currentPermission ++;
                    return checkAppPermissions(context);
                } else {
                    checkPhonePermission(context);
                    return true;
                }
            } else if (permission == "usage") {
                if(isUsageGranted(context)) {
                    currentPermission ++;
                    return checkAppPermissions(context);
                } else {
                    checkUsagePermission(context);
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isPhoneGranted (Activity context) {
        return  ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isDrawOverrideGranted (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static boolean isUsageGranted (Activity context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return false;
            }

            final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

            if (appOpsManager == null) {
                return false;
            }

            final int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
            if (mode != AppOpsManager.MODE_ALLOWED) {
                return false;
            }

            // Verify that access is possible. Some devices "lie" and return MODE_ALLOWED even when it's not.
            final long now = System.currentTimeMillis();
            final UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000 * 10, now);
            return (stats != null && !stats.isEmpty());
    }

    public static void checkPhonePermission (Activity context) {
        ActivityCompat.requestPermissions(context,
                new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_REQUEST_CODE);
    }

    public static void checkDrawOverlayPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, DRAW_REQUEST_CODE);
            }
        }
    }

    public static void checkUsagePermission(Activity context) {
        final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        context.startActivityForResult(intent, APP_USAGE_REQUEST_CODE);
    }
}
