package com.airsound.airsound;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.airsound.airsound.database.SettingsDataSource;
import com.airsound.airsound.database.entities.Settings;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

public class MainActivity extends Activity implements DirectoryChooserFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String		TAG = "MainActivity";

    private static final int		POS_RECORD = 0;
    private static final int		POS_BROWSE = 1;
    private static final int		POS_SETTINGS = 2;

    private static final int        REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int        REQUEST_CODE_CREATOR = 2;
    private static final int        REQUEST_CODE_RESOLUTION = 3;

    public GoogleApiClient         mGoogleApiClient;

    private String[] 				mMenuTitles;
    private CharSequence 			mTitle;
    private CharSequence 			mDrawerTitle;

    private DrawerLayout            mDrawerLayout;
    private ListView                mDrawerList;
    private ActionBarDrawerToggle   mDrawerToggle;

    DirectoryChooserFragment        mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawerOpen, R.string.drawerClose) {
            public void onDrawerClosed(View view) {
                if (getActionBar() != null) {
                    getActionBar().setTitle(mTitle);
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (getActionBar() != null) {
                    getActionBar().setTitle(mDrawerTitle);
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        SettingsDataSource datasource = new SettingsDataSource(this);

        datasource.open();
        if (!datasource.loadSettings()) {
            Settings.setLocalRootFolder(Environment.getExternalStorageDirectory().getPath() + "/AirSound");
            Settings.setCloudRootFolder("");
            Settings.setEncoding(Settings.EEncoding.THREE_GP);
            datasource.saveSettings(true);
        }
        datasource.close();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //mDialog = DirectoryChooserFragment.newInstance("DialogSample", null);
        //mDialog.show(getFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case POS_RECORD:
                fragment = FragmentRecord.newInstance();
                break;
            case POS_BROWSE:
                fragment = FragmentBrowse.newInstance();
                break;
            case POS_SETTINGS:
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment, "fragmentContent").commit();
            if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                ((IDriveFragment) fragment).onConnected();
            }
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getActionBar() != null) {
            getActionBar().setTitle(mTitle);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onSelectDirectory(String path) {
        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (android.content.IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        FragmentManager fragmentManager = getFragmentManager();
        DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, Drive.DriveApi.getRootFolder(mGoogleApiClient).getDriveId());
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                   @Override
                   public void onResult(DriveApi.MetadataBufferResult result) {
                       SettingsDataSource datasource = new SettingsDataSource(MainActivity.this);
                       boolean foundRootDir = false;

                       for (int i = 0; i < result.getMetadataBuffer().getCount(); ++i) {
                           Log.e(TAG, result.getMetadataBuffer().get(i).getTitle() + " " + Settings.getCloudRootFolder().contentEquals(result.getMetadataBuffer().get(i).getDriveId().toString()));
                           if (result.getMetadataBuffer().get(i).getTitle().contentEquals("AirSound")) {
                               if (!Settings.getCloudRootFolder().contentEquals(result.getMetadataBuffer().get(i).getDriveId().toString())) {
                                   Settings.setCloudRootFolder(result.getMetadataBuffer().get(i).getDriveId().toString());
                               }
                               foundRootDir = foundRootDir == true ? foundRootDir : true;
                           }
                       }
                       if (Settings.getCloudRootFolder().length() <= 0 ||!foundRootDir) {
                           MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                   .setTitle("AirSound")
                                   .build();
                           Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                   .createFolder(mGoogleApiClient, changeSet)
                                   .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                       @Override
                                       public void onResult(DriveFolder.DriveFolderResult result) {
                                           Settings.setCloudRootFolder(result.getDriveFolder().getDriveId().encodeToString());
                                       }
                                   });
                       }
                       datasource.open();
                       datasource.saveSettings(false);
                       datasource.close();
                   }
               });
        ((IDriveFragment) fragmentManager.findFragmentByTag("fragmentContent")).onConnected();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        FragmentManager fragmentManager = getFragmentManager();
        ((IDriveFragment) fragmentManager.findFragmentById(R.id.content)).onDisconnected();
    }
}
