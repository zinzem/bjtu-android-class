package com.airsound.airsound.helper;

import android.util.Log;
import android.widget.Toast;

import com.airsound.airsound.MainActivity;
import com.airsound.airsound.database.entities.Settings;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class DriveContentsHelper {

    private static final String		TAG = "DriveContentsHelper";

    private MainActivity            mContext;
    private GoogleApiClient         mGoogleApiClient;

    private DriveId                 mParentFolderId;
    private File                    mFile;

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(final DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                Toast.makeText(mContext, "Error while trying to create new file contents", Toast.LENGTH_SHORT).show();
                return;
            }
            final DriveContents driveContents = result.getDriveContents();

            new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(Settings.getLocalRootFolder().getAbsolutePath() + "/" + mFile.getName()));
                        String sCurrentLine;
                        OutputStream outputStream = driveContents.getOutputStream();
                        Writer writer = new OutputStreamWriter(outputStream);

                        while ((sCurrentLine = br.readLine()) != null) {
                                writer.write(sCurrentLine);
                                System.out.println(sCurrentLine);
                        }
                        writer.close();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, mParentFolderId);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(mFile.getName())
                            .setMimeType(Settings.getEncoding() == Settings.EEncoding.THREE_GP ? "video/3gpp" : "video/mp4")
                            .build();

                    folder.createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);

                }
            }.start();
        }
    };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {

        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                Toast.makeText(mContext, "Error while trying to create the file", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.e(TAG, "Created file with id " + result.getDriveFile().getDriveId());
        }
    };

    final private ResultCallback<DriveFolder.DriveFolderResult> folderCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {

        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                Toast.makeText(mContext, "Error while trying to create the folder", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.e(TAG, "Created folder with id " + result.getDriveFolder().getDriveId());
        }
    };

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "Problem while retrieving files");
                        return;
                    }
                    if (result.getMetadataBuffer().getCount() > 0) {
                        for (int i = 0; i < result.getMetadataBuffer().getCount(); ++i) {
                            Log.e(TAG, result.getMetadataBuffer().get(i).getTitle());
                        }
                    }
                }
            };

    public DriveContentsHelper(MainActivity context) {
        mContext = context;
        mGoogleApiClient = context.mGoogleApiClient;
    }

    public void createFile(DriveId driveId, File file) {
        mParentFolderId = driveId;
        mFile = file;
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public void createFolder(String name) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name)
                .build();
        Drive.DriveApi.getRootFolder(mGoogleApiClient)
                .createFolder(mGoogleApiClient, changeSet)
                .setResultCallback(folderCallback);
    }

    public void listInFolder(DriveId driveId) {
        DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, driveId);
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();
        folder.queryChildren(mGoogleApiClient, query)
                .setResultCallback(metadataCallback);
    }
}
