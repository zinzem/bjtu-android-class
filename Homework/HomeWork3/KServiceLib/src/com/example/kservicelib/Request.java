package com.example.kservicelib;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {

	private long 				value;
	private int 				type;
	
	public static final int 	TYPE_IT = 1;
	public static final int 	TYPE_RE = 2;
	
	public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
		public Request createFromParcel(Parcel in) {
			return new Request(in);
		}

		public Request[] newArray(int size) {
			return new Request[size];
		}
	};

    public Request(long value, int type) {
    	this.value = value;
        if (type != TYPE_IT && type != TYPE_RE) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.type = type;
    }
    
    public long getValue() {
    	return this.value;
    }
    
    public int getType() {
    	return this.type;
    }

    private Request(Parcel src) {
        readFromParcel(src);
    }
			    
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(value);
		dest.writeInt(type);
	}
	
	public void readFromParcel(Parcel src) {
		value = src.readLong();
		type = src.readInt();
	}
}
