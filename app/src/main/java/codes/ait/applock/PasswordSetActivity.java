package codes.ait.applock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import codes.ait.applock.Custom.FlatButton;
import codes.ait.applock.Utils.AppLockLogEvents;

/**
 * Created by amitshekhar on 30/04/15.
 */
public class PasswordSetActivity extends AppCompatActivity {
    Button confirmButton, forgotButton;
    EditText password;
    EditText newPassword;
    String enteredPassword;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String currentPassword;
    Boolean isPasswordSet;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_password_set);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        forgotButton= (Button) findViewById(R.id.forgotButton);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.new_password);
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        currentPassword = sharedPreferences.getString(AppLockConstants.PASSWORD, "");
        isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        editor = sharedPreferences.edit();

        if(!isPasswordSet) {
            password.setVisibility(View.GONE);
            forgotButton.setVisibility(View.GONE);
        } else {
            password.setVisibility(View.VISIBLE);
            forgotButton.setVisibility(View.VISIBLE);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordSet) {
                    String newPass = newPassword.getText().toString();
                    if(newPass == null || newPass.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please input new password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password.getText().toString().matches(currentPassword)) {
                        editor.putString(AppLockConstants.PASSWORD, newPass);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Password does not match - Try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editor.putBoolean(AppLockConstants.IS_PASSWORD_SET, true);
                    editor.commit();
                    editor.putString(AppLockConstants.PASSWORD, newPassword.getText().toString());
                    editor.commit();

                    Intent i = new Intent(PasswordSetActivity.this, MainActivity.class);
                    i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PasswordRecoveryActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
