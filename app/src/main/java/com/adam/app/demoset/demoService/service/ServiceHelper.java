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

package com.adam.app.demoset.demoService.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.demoService.util.ServiceLogBus;

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
     * startService
     * start service
     *
     * @param activity Activity
     */
    public void startService(Activity activity) {
        Intent serviceIntent = buildServiceIntent(activity);
        activity.startService(serviceIntent);
    }

    /**
     * stopService
     * stop service
     *
     * @param activity Activity
     */
    public void stopService(Activity activity) {
        Intent serviceIntent = buildServiceIntent(activity);
        activity.stopService(serviceIntent);
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
            // log
            ServiceLogBus.send(activity, "service is already bound");
            return;
        }

        Intent serviceIntent = buildServiceIntent(activity);
        mActiveConnection = mIsRemoteService ? mRemoteConnection : mLocalConnection;
        activity.bindService(serviceIntent, mActiveConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        // log
        ServiceLogBus.send(activity, "service is bound");
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
            // log
            ServiceLogBus.send(activity, "service is not bound");
            return;
        }
        activity.unbindService(mActiveConnection);
        mIsBound = false;
        mActiveConnection = null;
        mLocalService = null;
        mRemoteMessenger = null;
        // log
        ServiceLogBus.send(activity, "service is unbound");
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
