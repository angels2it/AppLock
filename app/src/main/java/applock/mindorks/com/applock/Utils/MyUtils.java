package applock.mindorks.com.applock.Utils;

import android.Manifest;
import android.app.Activity;
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
    static List<String> permissions = Arrays.asList("draw", "phone");
    public static int DRAW_REQUEST_CODE = 5463 & 0xffffff00;
    public static int PHONE_REQUEST_CODE = 5464 & 0xffffff00;
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
                    checkAppPermissions(context);
                } else {
                    checkDrawOverlayPermission(context);
                }
            } else if (permission == "phone") {
                if(isPhoneGranted(context)) {
                    currentPermission ++;
                    checkAppPermissions(context);
                } else {
                    checkPhonePermission(context);
                }
            }
            currentPermission ++;
            return true;
        } else {
            return false;
        }
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
}
