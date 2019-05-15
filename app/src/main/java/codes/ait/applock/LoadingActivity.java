package codes.ait.applock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import codes.ait.applock.R;
import codes.ait.applock.Api.ApiInstance;
import codes.ait.applock.Api.ConfigResult;
import codes.ait.applock.Utils.MyUtils;
import codes.ait.applock.Utils.SharedPreference;
import codes.ait.applock.services.AppCheckServices;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 23/05/15.
 */
public class LoadingActivity extends AppCompatActivity {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreference sharedPreference;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        sharedPreference = new SharedPreference();
        editor = sharedPreferences.edit();
        checkPermissionAndLoadConfig();
    }

    private void checkPermissionAndLoadConfig () {
        boolean hasPermissionNeedToCheck = MyUtils.checkAppPermissions(this);
        if(!hasPermissionNeedToCheck) {
            String imei = MyUtils.getImei(this);
            if(imei != null && !imei.isEmpty()) {
                FirebaseAnalytics.getInstance(this).setUserId(imei);
            }
            loadConfig();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MyUtils.DRAW_REQUEST_CODE) {
            if(!MyUtils.isDrawOverrideGranted(this)) {
                MyUtils.checkDrawOverlayPermission(this);
            } else {
                startService(new Intent(LoadingActivity.this, AppCheckServices.class));
                checkPermissionAndLoadConfig();
            }
        } else if(requestCode == MyUtils.PHONE_REQUEST_CODE){
            if(!MyUtils.isPhoneGranted(this)) {
                MyUtils.checkPhonePermission(this);
            } else {
                checkPermissionAndLoadConfig();
            }
        } else if(requestCode == MyUtils.APP_USAGE_REQUEST_CODE){
            if(!MyUtils.isUsageGranted(this)) {
                MyUtils.checkUsagePermission(this);
            } else {
                checkPermissionAndLoadConfig();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MyUtils.PHONE_REQUEST_CODE){
            if(!MyUtils.isPhoneGranted(this)) {
                MyUtils.checkPhonePermission(this);
            } else {
                checkPermissionAndLoadConfig();
            }
        }
    }

    private void loadConfig () {
        boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        if(!isPasswordSet) {
            editor.putString(AppLockConstants.PASSWORD, MyUtils.Config.password);
            editor.commit();
            editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, true);
            editor.commit();
        }
        if(MyUtils.Config.apps != null) {
            List<String> lockApps = sharedPreference.getLocked(context);
            for (int i = 0; i < MyUtils.Config.apps.size(); i++) {
                if(lockApps == null || !lockApps.contains(MyUtils.Config.apps.get(i))) {
                    sharedPreference.addLocked(context, MyUtils.Config.apps.get(i));
                }
            }
        }
        Intent i = new Intent(context, MainActivity.class);
        startActivity(i);
    }
}
