package com.adam.app.demoset.workmanager;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MyViewModel extends ViewModel {

    private Uri mImageUri;


    public void setImageUri(@NonNull Uri uri) {
        mImageUri = uri;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

}
