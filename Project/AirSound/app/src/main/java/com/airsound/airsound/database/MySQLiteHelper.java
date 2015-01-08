package com.airsound.airsound.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String 				TAG = "MySQLiteHelper";

    public Map<String, Map<String, String>> 	tables = new HashMap<String, Map<String, String>>();

    private static final String 				DATABASE_NAME = "airsound.db";
    private static final int 					DATABASE_VERSION = 1;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Map<String, String> settings = new HashMap<String, String>();

        settings.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
        settings.put("localRootFolder", "TEXT");
        settings.put("cloudRootFolder", "TEXT");
        settings.put("encoding", "TEXT");
        tables.put("Settings", settings);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Iterator<Map.Entry<String, Map<String, String>>> tablesIterator = tables.entrySet().iterator();
        String dbCreateQuery = "create ";

        while (tablesIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> tablesEntry = (Map.Entry<String, Map<String, String>>)tablesIterator.next();
            Iterator<Map.Entry<String, String>> colummsIterator = tablesEntry.getValue().entrySet().iterator();
            dbCreateQuery += "table " + tablesEntry.getKey().toString() + " ( ";

            while (colummsIterator.hasNext()) {
                Map.Entry<String, String> colummsEntry = (Map.Entry<String, String>)colummsIterator.next();
                Log.e(TAG, colummsEntry.getKey() + " " + colummsEntry.getValue());
                dbCreateQuery += colummsEntry.getKey() + " " + colummsEntry.getValue();
                dbCreateQuery += colummsIterator.hasNext() ? ", " : ")";
            }
            dbCreateQuery += tablesIterator.hasNext() ? ", " : ";";
        }
        Log.e(TAG, dbCreateQuery);
        database.execSQL(dbCreateQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e(TAG, "Upgrading database from version "
                + oldVersion
                + " to "
                + newVersion
                + ", which will destroy all old data");
        for (Map.Entry<String, Map<String, String>> entry : tables.entrySet()) {
            db.execSQL("DROP TABLE IF EXISTS " + entry.getKey());
        }
        onCreate(db);
    }
}
