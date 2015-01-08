package com.airsound.airsound.entities;

import java.io.File;

public class Record {

    public String       mTitle = "";
    public File         mLocation;

    public Record(File location) {
        mLocation = location;
        mTitle = mLocation.getName();
    }
}
