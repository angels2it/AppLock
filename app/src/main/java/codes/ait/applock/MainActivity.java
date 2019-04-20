package codes.ait.applock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

import codes.ait.applock.Custom.PasswordMatchedListener;
import codes.ait.applock.Fragments.PasswordFragment;
import codes.ait.applock.R;
import codes.ait.applock.Custom.FlatButton;
import codes.ait.applock.Data.AppInfo;
import codes.ait.applock.Fragments.AllAppFragment;
import codes.ait.applock.Fragments.AvailableAppFragment;
import codes.ait.applock.Fragments.ImeiFragment;
import codes.ait.applock.Utils.AppLockLogEvents;
import codes.ait.applock.Utils.MyUtils;
import codes.ait.applock.services.AppCheckServices;


public class MainActivity extends AppCompatActivity {

    //save our header or result
    private Drawer.Result result = null;
    FragmentManager fragmentManager;
    Context context;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    long numOfTimesAppOpened = 0;
    FloatingActionButton fabLock;
    public static boolean isLockMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fabLock = findViewById(R.id.fabLock);
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        numOfTimesAppOpened = sharedPreferences.getLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, 0) + 1;
        editor.putLong(AppLockConstants.NUM_OF_TIMES_APP_OPENED, numOfTimesAppOpened);
        editor.commit();

        //Google Analytics
        Tracker t = ((AppLockApplication) getApplication()).getTracker(AppLockApplication.TrackerName.APP_TRACKER);
        t.setScreenName(AppLockConstants.MAIN_SCREEN);
        t.send(new HitBuilders.AppViewBuilder().build());

        if (Build.VERSION.SDK_INT > 20){
            Toast.makeText(getApplicationContext(), "If you have not allowed , allow App Lock so that it can work properly from sliding menu options", Toast.LENGTH_LONG).show();
        }

        fragmentManager = getSupportFragmentManager();

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create the drawer
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("All Applications").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Locked Applications").withIcon(FontAwesome.Icon.faw_lock),
                        new PrimaryDrawerItem().withName("Unlocked Applications").withIcon(FontAwesome.Icon.faw_unlock),
                        new PrimaryDrawerItem().withName("Allow Access").withIcon(FontAwesome.Icon.faw_share),
                        new PrimaryDrawerItem().withName("Add IMEI").withIcon(FontAwesome.Icon.faw_share)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {

                            if (position == 0) {
                                getSupportActionBar().setTitle("Available Apps");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.AVAILABLE);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Show Unlocked Applications Clicked", "show_unlocked_applications_clicked", "");
                            }

                            if (position == 1) {
                                Fragment fp = PasswordFragment.newInstance(fragmentManager, AppLockConstants.FRAGMENT_LOCKED);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fp).commit();
                                getSupportActionBar().setTitle("Locked Applications");
                            }

                            if (position == 2) {
                                Fragment fp = PasswordFragment.newInstance(fragmentManager, AppLockConstants.FRAGMENT_UNLOCK);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, fp).commit();
                                getSupportActionBar().setTitle("Unlocked Applications");
                            }

                            if (position == 3) {
                                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "If you have not allowed , allow App Lock so that it can work properly", Toast.LENGTH_LONG).show();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Allow Access", "allow_access", "");
                                result.setSelection(0);
                            }

                            if (position == 4) {
                                getSupportActionBar().setTitle("Set IMEI");
                                Fragment f = ImeiFragment.newInstance();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f, "IMEI").addToBackStack("IMEI").commit();
                                AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Set IMEI Clicked", "set_imei_clicked", "");
                            }
                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {


                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();


        //react on the keyboard
//        result.keyboardSupportEnabled(this, true);
        fabLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLockMode) {
                    Fragment fp = PasswordFragment.newInstance(new PasswordMatchedListener() {
                        @Override
                        public void onMatched() {
                            isLockMode = false;
                            fabLock.setImageResource(R.drawable.unlock);
                            getSupportActionBar().setTitle("ALL APPS");
                            Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                        }
                    });
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fp).commit();
                } else {
                    isLockMode = true;
                    fabLock.setImageResource(R.drawable.locked);
                    getSupportActionBar().setTitle("Available Apps");
                    Fragment f = AllAppFragment.newInstance(AppLockConstants.AVAILABLE);
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (getCurrentFragment() instanceof AvailableAppFragment) {
                // do nothing
            } else {
                fragmentManager.popBackStack();
                getSupportActionBar().setTitle("AllAppFragment");
                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                result.setSelection(0);
            }
        }
    }

    /**
     * Returns currentfragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(i);
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);

        String myPackedName = context.getPackageName();

        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                ResolveInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                try {
                    appInfo = packageManager.getApplicationInfo(p.activityInfo.packageName, 0);

                    AppInfo app = new AppInfo();
                    app.setName(p.loadLabel(packageManager).toString());
                    app.setPackageName(p.activityInfo.packageName);
                    app.setIcon(p.loadIcon(packageManager));

                    if(appInfo.packageName.matches(myPackedName)) {
                        continue;
                    }
                    installedApps.add(app);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return installedApps;
        }
        return null;
    }

    @Override
    protected void onStart() {
        GoogleAnalytics.getInstance(context).reportActivityStart(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(context).reportActivityStop(this);
        super.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
