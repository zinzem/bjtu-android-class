package com.airsound.airsound;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.airsound.airsound.database.entities.Settings;
import com.airsound.airsound.entities.Record;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FragmentBrowse extends Fragment implements IDriveFragment {

    private Activity                            mActivity;

    private ListView                            mLocalList;
    private ListView                            mCloudList;

    private List<Record>                        mLocalRecords;

    private ImageView                           mStatus;

    public static FragmentBrowse newInstance() {
        FragmentBrowse fragment = new FragmentBrowse();

        return fragment;
    }

    public FragmentBrowse() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.fragment_browse, container, false);

        mStatus = (ImageView) inflateView.findViewById(R.id.status);
        mLocalList = (ListView) inflateView.findViewById(R.id.localList);
        mCloudList = (ListView) inflateView.findViewById(R.id.cloudList);

        getLocalRecords();
        LocalListAdapter customAdapter = new LocalListAdapter(mActivity, R.layout.local_list_item, mLocalRecords);
        mLocalList .setAdapter(customAdapter);

        return inflateView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onConnected() {
        if (mStatus != null) {
            mStatus.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDisconnected() {
        if (mStatus != null) {
            mStatus.setVisibility(View.INVISIBLE);
        }
    }

    private void getLocalRecords() {
        File rootFolder = Settings.getLocalRootFolder();
        File[] records = rootFolder.listFiles();

        mLocalRecords = new ArrayList<Record>();
        for (int i = 0; i < records.length; i++) {
            if (records[i].isFile()) {
                mLocalRecords.add(new Record(records[i]));
            }
        }
    }
}
