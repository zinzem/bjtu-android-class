package com.example.kservicelib;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable {

	private long 	result;
	
	public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
    
    public Response(long result) {
        this.result = result;
    }

    public Response(Parcel parcel) {
        this(parcel.readLong());
    }

    public long getResult() {
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.result);
    }
    
    public void readFromParcel(Parcel src) {
		result = src.readLong();
	}
}
