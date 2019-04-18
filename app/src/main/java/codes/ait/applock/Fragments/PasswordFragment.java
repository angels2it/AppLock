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

import com.takwolf.android.lock9.Lock9View;

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

    public static PasswordFragment newInstance(FragmentManager fragmentManager, String fragment) {
        PasswordFragment f = new PasswordFragment();
        f.fragmentManager = fragmentManager;
        f.fragment = fragment;
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

        final EditText inputPassword = (EditText) v.findViewById(R.id.input_password);
        Button confirmPassword = (Button) v.findViewById(R.id.confirm_password);
        confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputPassword.getText().toString().matches(sharedPreference.getPassword(getContext()))) {
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Correct Password", "correct_password", "");

                    if(AppLockConstants.FRAGMENT_LOCKED.matches(fragment)) {
                        Fragment f = AllAppFragment.newInstance(AppLockConstants.LOCKED);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Locked Applications Clicked", "show_locked_applications_clicked", "");
                    } else if(AppLockConstants.FRAGMENT_UNLOCK.matches(fragment)) {
                        Fragment f = AllAppFragment.newInstance(AppLockConstants.UNLOCKED);
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Unlocked Applications Clicked", "show_unLocked_applications_clicked", "");
                    }
                } else {
                    Toast.makeText(getContext(), "Wrong Pattern Try Again", Toast.LENGTH_SHORT).show();
                    AppLockLogEvents.logEvents(AppLockConstants.PASSWORD_CHECK_SCREEN, "Wrong Password", "wrong_password", "");
                }
            }
        });

        return v;
    }

}
