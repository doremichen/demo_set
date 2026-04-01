/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.adam.app.demoset.binder.service;

import android.os.Parcel;
import android.os.Parcelable;

public class MyBinderData implements Parcelable {

    private String mMsg;

    /**
     * Get message
     * @param
     */
    public String getMessage() {
        return this.mMsg;
    }


    public MyBinderData(String msg) {
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
