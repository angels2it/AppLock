package codes.ait.applock.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import codes.ait.applock.R;
import codes.ait.applock.AppLockConstants;
import codes.ait.applock.Data.AppInfo;
import codes.ait.applock.Utils.AppLockLogEvents;
import codes.ait.applock.Utils.SharedPreference;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {
    List<AppInfo> installedApps = new ArrayList();
    private Context context;
    SharedPreference sharedPreference;
    String requiredAppsType;

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView applicationName;
        public CardView cardView;
        public ImageView icon;
        public Switch switchView;

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            icon = (ImageView) v.findViewById(R.id.icon);
            if(requiredAppsType != AppLockConstants.AVAILABLE) {
                switchView = (Switch) v.findViewById(R.id.switchView);
            }
        }
    }

    public void add(int position, String item) {
//        mDataset.add(position, item);
//        notifyItemInserted(position);
    }

    public void remove(AppInfo item) {
//        int position = installedApps.indexOf(item);
//        installedApps.remove(position);
//        notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ApplicationListAdapter(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
        installedApps = appInfoList;
        this.context = context;
        this.requiredAppsType = requiredAppsType;
        sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        boolean flag = true;
        if (requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.AVAILABLE) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
            for (int i = 0; i < installedApps.size(); i++) {
                flag = true;
                if (sharedPreference.getLocked(context) != null) {
                    for (int j = 0; j < sharedPreference.getLocked(context).size(); j++) {
                        if (installedApps.get(i).getPackageName().matches(sharedPreference.getLocked(context).get(j))) {
                            lockedFilteredAppList.add(installedApps.get(i));
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    unlockedFilteredAppList.add(installedApps.get(i));
                }
            }
            if (requiredAppsType.matches(AppLockConstants.LOCKED)) {
                installedApps.clear();
                installedApps.addAll(lockedFilteredAppList);
            } else if (requiredAppsType.matches(AppLockConstants.UNLOCKED) || requiredAppsType.matches(AppLockConstants.AVAILABLE)) {
                installedApps.clear();
                installedApps.addAll(unlockedFilteredAppList);
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        if(this.requiredAppsType == AppLockConstants.AVAILABLE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            vh.switchView = null;
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            vh.switchView = (Switch) v.findViewById(R.id.switchView);
            return vh;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = installedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

        if(holder.switchView != null) {
            holder.switchView.setOnCheckedChangeListener(null);
        }
        holder.cardView.setOnClickListener(null);
        if(holder.switchView != null) {
            if (checkLockedItem(appInfo.getPackageName())) {
                holder.switchView.setChecked(true);
            } else {
                holder.switchView.setChecked(false);
            }

            holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", appInfo.getPackageName());
                        sharedPreference.addLocked(context, appInfo.getPackageName());
                    } else {
                        AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", appInfo.getPackageName());
                        sharedPreference.removeLocked(context, appInfo.getPackageName());
                    }
                }
            });
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requiredAppsType != AppLockConstants.AVAILABLE) {
                    holder.switchView.performClick();
                } else {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
                    context.startActivity(launchIntent);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return installedApps.size();
    }

    /*Checks whether a particular app exists in SharedPreferences*/
    public boolean checkLockedItem(String checkApp) {
        boolean check = false;
        List<String> locked = sharedPreference.getLocked(context);
        if (locked != null) {
            for (String lock : locked) {
                if (lock.equals(checkApp)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

}