package codes.ait.applock.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import codes.ait.applock.R;
import codes.ait.applock.Api.ApiInstance;
import codes.ait.applock.Api.ApiResult;
import codes.ait.applock.Api.Imei;
import codes.ait.applock.AppLockConstants;
import codes.ait.applock.Custom.FlatButton;
import codes.ait.applock.MainActivity;
import codes.ait.applock.Utils.AppLockLogEvents;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 30/04/15.
 */
public class ImeiFragment extends Fragment {
    EditText imeiInput;
    FlatButton confirmButton, retryButton;
    String imei;
    SharedPreferences sharedPreferences;

    public static ImeiFragment newInstance() {
        ImeiFragment f = new ImeiFragment();
        return (f);
    }

    public ImeiFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_set_imei, container, false);

        imeiInput = (EditText) v.findViewById(R.id.editText);
        confirmButton = (FlatButton) v.findViewById(R.id.confirmButton);
        retryButton = (FlatButton) v.findViewById(R.id.retryButton);
        confirmButton.setEnabled(false);
        sharedPreferences = getActivity().getSharedPreferences(AppLockConstants.MyPREFERENCES, Context.MODE_PRIVATE);

        getImei(true);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInstance.imeiService.addImei(new Imei(imei))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleObserver<ApiResult>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onSuccess(ApiResult result) {
                                if(result.success) {
                                    Toast.makeText(getContext(), "Success : IMEI added", Toast.LENGTH_SHORT).show();
                                    AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Set IMEI", "set_imei", "");
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getContext(), "Error : Failed while adding IMEI", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imeiInput.setText("");
                imei = "";
                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Retry Password", "retry_password", "");
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1212) {
            getImei(false);
        }
    }

    private void getImei (boolean request) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(request) {
                ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE}, 1212);
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            imeiInput.setText((imei));
            confirmButton.setEnabled(true);
        }
    }
}
