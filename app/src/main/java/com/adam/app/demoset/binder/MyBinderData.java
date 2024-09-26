package com.adam.app.demoset.binder;

import android.os.Parcel;
import android.os.Parcelable;

public class MyBinderData implements Parcelable {

    private String mMsg;

    /**
     * Get message
     * @param
     */
    String getMessage() {
        return this.mMsg;
    }


    MyBinderData(String msg) {
        this.mMsg = msg;
    }

    protected MyBinderData(Parcel in) {
        this.mMsg = in.readString();
    }

    public static final Creator<MyBinderData> CREATOR = new Creator<MyBinderData>() {
        @Override
        public MyBinderData createFromParcel(Parcel in) {
            return new MyBinderData(in);
        }

        @Override
        public MyBinderData[] newArray(int size) {
            return new MyBinderData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMsg);
    }
}
