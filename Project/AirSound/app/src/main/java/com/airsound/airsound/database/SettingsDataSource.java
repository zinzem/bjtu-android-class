package com.airsound.airsound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.airsound.airsound.database.entities.Settings;

import java.util.HashMap;
import java.util.Map;

public class SettingsDataSource {

    private static final String 	TAG = "SettingsDataSource";

    // Database fields
    private SQLiteDatabase          mDatabase;
    private MySQLiteHelper 			mDbHelper;
    private static final String		TABLE_NAME = "Settings";
    private Map<String, String>     mColumms = new HashMap<String, String>();

    public SettingsDataSource(Context context) {
        mDbHelper = new MySQLiteHelper(context);

        for (Map.Entry<String, String> columm : mDbHelper.tables.get(TABLE_NAME).entrySet()) {
            mColumms.put(columm.getKey(), columm.getKey());
        }
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public void saveSettings(boolean create) {
        ContentValues values = new ContentValues();

        values.put(mColumms.get("localRootFolder") , Settings.getLocalRootFolder().getPath());
        values.put(mColumms.get("cloudRootFolder") , Settings.getCloudRootFolder());
        values.put(mColumms.get("encoding"), Settings.getEncoding().toString());
        if (create) {
            Log.e(TAG, "Creating new settings");
            mDatabase.insert(TABLE_NAME, null, values);
        } else {
            Log.e(TAG, "Saving new settings");
            mDatabase.update(TABLE_NAME, values, null, null);
        }
    }

    public boolean loadSettings() {
        Log.e(TAG, "Getting credentials");
        Cursor cursor = mDatabase.query(TABLE_NAME, mColumms.keySet().toArray(new String[mColumms.size()]), null, null, null, null, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0
                && cursor.getPosition() >= 0
                && !cursor.isNull(0)
                && !cursor.isNull(1)
                && !cursor.isNull(2)) {
            Settings.setLocalRootFolder(cursor.getString(cursor.getColumnIndex("localRootFolder")));
            Settings.setCloudRootFolder(cursor.getString(cursor.getColumnIndex("cloudRootFolder")));
            Settings.setEncoding(Settings.EEncoding.valueOf(cursor.getString(cursor.getColumnIndex("encoding"))));
        } else {
            return false;
        }
        cursor.close();
        return true;
    }
}
