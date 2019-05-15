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

import codes.ait.applock.AppLockConstants;
import codes.ait.applock.Data.AppInfo;
import codes.ait.applock.R;
import codes.ait.applock.Utils.AppLockLogEvents;
import codes.ait.applock.Utils.SharedPreference;

/**
 * Created by amitshekhar on 28/04/15.
 */
public class AvailableApplicationListAdapter extends RecyclerView.Adapter<AvailableApplicationListAdapter.ViewHolder> {
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

        public ViewHolder(View v) {
            super(v);
            applicationName = (TextView) v.findViewById(R.id.applicationName);
            cardView = (CardView) v.findViewById(R.id.card_view);
            icon = (ImageView) v.findViewById(R.id.icon);
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
    public AvailableApplicationListAdapter(List<AppInfo> appInfoList, Context context, boolean showAll) {
        installedApps = appInfoList;
        this.context = context;
        sharedPreference = new SharedPreference();
        List<AppInfo> lockedFilteredAppList = new ArrayList<AppInfo>();
        List<AppInfo> unlockedFilteredAppList = new ArrayList<AppInfo>();
        if(showAll == false) {
            boolean flag = true;
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
            installedApps.clear();
            installedApps.addAll(unlockedFilteredAppList);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AvailableApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final AppInfo appInfo = installedApps.get(position);
        holder.applicationName.setText(appInfo.getName());
        holder.icon.setBackgroundDrawable(appInfo.getIcon());

        holder.cardView.setOnClickListener(null);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());
                context.startActivity(launchIntent);
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