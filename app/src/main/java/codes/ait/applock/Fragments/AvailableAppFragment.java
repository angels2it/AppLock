package codes.ait.applock.Fragments;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import codes.ait.applock.Adapter.AvailableApplicationListAdapter;
import codes.ait.applock.R;
import codes.ait.applock.Adapter.ApplicationListAdapter;
import codes.ait.applock.MainActivity;

/**
 * Created by amitshekhar on 30/04/15.
 */
public class AvailableAppFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean showAll = false;

    public static AvailableAppFragment newInstance(boolean showAll) {
        AvailableAppFragment f = new AvailableAppFragment();
        f.showAll = showAll;
        return (f);
    }


    public AvailableAppFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_available_apps, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new AvailableApplicationListAdapter(((MainActivity) getActivity()).getListOfInstalledApp(getActivity()), getActivity(), showAll);
        mRecyclerView.setAdapter(mAdapter);

        return v;

    }
}
