/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is Service helper that is used to handle service status.
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class ServiceHelper {
    private static final String TAG = "ServiceHelper";
    // check whether is remote service or not
    private boolean mIsRemoteService;

    // local service and remote service reference
    private LocalService mLocalService;
    private Messenger mRemoteMessenger;

    // used to confirm active service connection type: local or remote
    private ServiceConnection mActiveConnection;

    // check whether service is bound
    private boolean mIsBound;

    // local service connection callback
    private final ServiceConnection mLocalConnection;
    // remote service connection callback
    private final ServiceConnection mRemoteConnection;

    private static class SingletonHolder {
        private static final ServiceHelper INSTANCE = new ServiceHelper();
    }

    public static ServiceHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Constructor
     */
    private ServiceHelper() {
        mLocalConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mLocalService = ((LocalService.LocalBinder) service).getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocalService = null;
            }
        };

        mRemoteConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRemoteMessenger = new Messenger(service);
                }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRemoteMessenger = null;
            }
        };

    }

    /**
     * setRemoteServiceMode
     * Set active service connection type
     *
     * @param isRemoteService: boolean
     */
    public void setRemoteServiceMode(boolean isRemoteService) {
        mIsRemoteService = isRemoteService;
    }

    /**
     * isRemoteServiceMode
     * return active service connection type
     *
     * @return boolean
     */
    public boolean isRemoteServiceMode() {
        return mIsRemoteService;
    }

    /**
     * buildServiceIntent
     * build the service intent according to active service connection type
     *
     * @param context: Context
     * @return Intent
     */
    public Intent buildServiceIntent(Context context) {
        Class<?> serviceClass = mIsRemoteService ? RemoteService.class : LocalService.class;
        return new Intent(context, serviceClass);
    }

    /**
     * bindService
     * bind service according to active service connection type
     *
     * @param activity: Activity
     */
    public void bindService(Activity activity) {
        // early return if service is already bound
        if (mIsBound) {
            // show toast
            Utils.showToast(activity, activity.getString(R.string.demo_service_service_is_bound));
            return;
        }

        Intent serviceIntent = buildServiceIntent(activity);
        mActiveConnection = mIsRemoteService ? mRemoteConnection : mLocalConnection;
        activity.bindService(serviceIntent, mActiveConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        // show toast
        Utils.showToast(activity, activity.getString(R.string.demo_service_service_is_bound));
    }

    /**
     * unbindService
     * unbind service according to active service connection type
     *
     * @param activity: Activity
     */
    public void unbindService(Activity activity) {
        // early return if service is not bound
        if (!mIsBound) {
            // show toast
            Utils.showToast(activity, activity.getString(R.string.demo_service_service_is_unbound));
            return;
        }
        activity.unbindService(mActiveConnection);
        mIsBound = false;
        mActiveConnection = null;
        mLocalService = null;
        mRemoteMessenger = null;
        // show toast
        Utils.showToast(activity, activity.getString(R.string.demo_service_service_is_unbound));
    }

    /**
     * isBound
     * check whether service is bound
     *
     * @return boolean
     */
    public boolean isBound() {
        return mIsBound;
    }

    /**
     * getRemoteMessenger
     * get remote service messenger
     *
     * @return Messenger
     */
    public Messenger getRemoteMessenger() {
        return mRemoteMessenger;
    }

    /**
     * getLocalService
     * get local service
     *
     * @return LocalService
     */
    public LocalService getLocalService() {
        return mLocalService;
    }

    /**
     * log
     * log message for debug
     *
     * @param msg: String
     */
    public void log(String msg) {
        Utils.log(TAG, msg);
    }



}
