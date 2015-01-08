package com.airsound.airsound.database.entities;

import android.util.Log;

import java.io.File;

public class Settings {

    private static final String     LOG_TAG = "Settings";

    private static Settings     gSettings = new Settings();

    private String		        mId;
    private File                mLocalFolder;
    private String              mCloudFolder;
    private EEncoding           mEncoding;

    public enum EEncoding {
        THREE_GP, MP4;
    };

    private Settings() {
    }

    public static Settings getInstance() {
        return gSettings;
    }

    public static String getId() {
        return gSettings.mId;
    }

    public static void setId(String id) {
        gSettings.mId = id;
    }

    public static File getLocalRootFolder() {
        return gSettings.mLocalFolder;
    }

    public static String getCloudRootFolder() {
        return gSettings.mCloudFolder;
    }

    public static EEncoding getEncoding() {
        return gSettings.mEncoding;
    }

    public static void setLocalRootFolder(String path) {
        gSettings.mLocalFolder = new File(path);
        if (!gSettings.mLocalFolder.exists()) {
            try {
                gSettings.mLocalFolder.mkdir();
            } catch (SecurityException e) {
                gSettings.mLocalFolder = null;
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    public static void setCloudRootFolder(String id) {
        gSettings.mCloudFolder = id;
    }

    public static void setEncoding(EEncoding encoding) {
        gSettings.mEncoding = encoding;
    }
}

