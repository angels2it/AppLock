package applock.mindorks.com.applock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import applock.mindorks.com.applock.Api.ApiInstance;
import applock.mindorks.com.applock.Api.ApiResult;
import applock.mindorks.com.applock.Api.ConfigResult;
import applock.mindorks.com.applock.Utils.AppLockLogEvents;
import applock.mindorks.com.applock.Utils.SharedPreference;
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
        loadConfig();
    }
    private void loadConfig () {
        ApiInstance.homeService.config()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ConfigResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(ConfigResult result) {
                        if(result.success) {
                            Toast.makeText(context, "Success : config loaded", Toast.LENGTH_SHORT).show();
                            editor.putString(AppLockConstants.PASSWORD, result.password);
                            editor.commit();
                            if(result.apps != null) {
                                for (int i = 0; i < result.apps.length; i++) {
                                    sharedPreference.addLocked(context, result.apps[i]);
                                }
                            }
                            Intent i = new Intent(context, MainActivity.class);
                            context.startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "Error : Failed while loading config", Toast.LENGTH_SHORT).show();

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Error - load config")
                                .setMessage("Click ok to reload app config")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        loadConfig();
                                    }
                                })
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert).create();
                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        dialog.show();
                    }
                });
    }
}
