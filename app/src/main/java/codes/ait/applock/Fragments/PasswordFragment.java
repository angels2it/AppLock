package codes.ait.applock.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.takwolf.android.lock9.Lock9View;

import codes.ait.applock.Custom.PasswordMatchedListener;
import codes.ait.applock.PasswordRecoveryActivity;
import codes.ait.applock.R;
import codes.ait.applock.AppLockConstants;
import codes.ait.applock.Custom.FlatButton;
import codes.ait.applock.MainActivity;
import codes.ait.applock.Utils.AppLockLogEvents;
import codes.ait.applock.Utils.SharedPreference;

/**
 * Created by amitshekhar on 30/04/15.
 */
public class PasswordFragment extends Fragment {
    SharedPreference sharedPreference;
    FragmentManager fragmentManager;
    String fragment;
    PasswordMatchedListener listener;

    public static PasswordFragment newInstance(FragmentManager fragmentManager, String fragment) {
        PasswordFragment f = new PasswordFragment();
        f.fragmentManager = fragmentManager;
        f.fragment = fragment;
        return (f);
    }

    public static PasswordFragment newInstance(PasswordMatchedListener listener) {
        PasswordFragment f = new PasswordFragment();
        f.listener = listener;
        return (f);
    }

    public PasswordFragment() {
        super();
        sharedPreference = new SharedPreference();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_password, container, false);
        FirebaseAnalytics.getInstance(getContext()).setCurrentScreen(this.getActivity(), this.getClass().getSimpleName(), this.getClass().getSimpleName());
        final EditText inputPassword = (EditText) v.findViewById(R.id.input_password);
        Button confirmPassword = (Button) v.findViewById(R.id.confirm_password);
        confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPassword.getText().toString().matches(sharedPreference.getPassword(getContext()))) {
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");
                    if(listener != null) {
                        listener.onMatched();
                        return;
                    }
                    if(AppLockConstants.FRAGMENT_LOCKED.matches(fragment)) {
                        Fragment f = AllAppFragment.newInstance(AppLockConstants.LOCKED);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Locked Applications Clicked", "show_locked_applications_clicked", "");
                    } else if(AppLockConstants.FRAGMENT_UNLOCK.matches(fragment)) {
                        Fragment f = AllAppFragment.newInstance(AppLockConstants.UNLOCKED);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Unlocked Applications Clicked", "show_unLocked_applications_clicked", "");
                    } else if(AppLockConstants.FRAGMENT_ALL_APPS.matches(fragment)) {
                        Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show All Applications Clicked", "show_unLocked_applications_clicked", "");
                    }
                } else {
                    Toast.makeText(getContext(), "Password does not match, Try Again", Toast.LENGTH_SHORT).show();
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Wrong Password", "wrong_password", "");
                }
            }
        });

        TextView forgotPassword = v.findViewById(R.id.forgotText);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PasswordRecoveryActivity.class));
            }
        });

        return v;
    }

}
