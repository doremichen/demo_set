/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the service action items.
 * <p>
 * Author: Adam Chen
 * Date: 2025/09/17
 */
package com.adam.app.demoset.demoService;

import android.app.Activity;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public enum ServiceActItems {
    START_SERVICE(Utils.ITEM_START_SERVICE) {
        @Override
        public void execute() {
            Activity act = getActivity();
            if (act != null) act.startService(mServiceHelper.buildServiceIntent(act));
        }

        @Override
        public String toString() {
            return Utils.ITEM_START_SERVICE;
        }

    },
    STOP_SERVICE(Utils.ITEM_STOP_SERVICE) {
        @Override
        public void execute() {
            Activity act = getActivity();
            if (act != null) act.stopService(mServiceHelper.buildServiceIntent(act));
        }

        @Override
        public String toString() {
            return Utils.ITEM_STOP_SERVICE;
        }
    },
    BIND_SERVICE(Utils.ITEM_BIND_SERVICE) {
        @Override
        public void execute() {
            Activity act = getActivity();
            if (act != null) mServiceHelper.bindService(act);
        }

        @Override
        public String toString() {
            return Utils.ITEM_BIND_SERVICE;
        }
    },
    UNBIND_SERVICE(Utils.ITEM_UNBIND_SERVICE) {
        @Override
        public void execute() {
            Activity act = getActivity();
            if (act != null) mServiceHelper.unbindService(act);
        }

        @Override
        public String toString() {
            return Utils.ITEM_UNBIND_SERVICE;
        }
    },
    SERVICE_REQ(Utils.ITEM_SERVICE_REQUEST) {
        @Override
        public void execute() {
            if (mServiceHelper.isRemoteServiceMode()) {
                Messenger messenger = mServiceHelper.getRemoteMessenger();
                if (messenger != null) {
                    try {
                        messenger.send(Message.obtain(null, RemoteService.ACTION_ONE));
                    } catch (RemoteException e) {
                        // log error
                        Utils.error(ServiceActItems.class, "send message error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                LocalService localService = mServiceHelper.getLocalService();
                if (localService != null) {
                    localService.action1();
                }
            }
        }

        @Override
        public String toString() {
            return Utils.ITEM_SERVICE_REQUEST;
        }

    };

    private static WeakReference<Activity> sActRef;
    private static final ServiceHelper mServiceHelper = ServiceHelper.getInstance();
    private final String mType;

    ServiceActItems(String type) {
        mType = type;
    }

    public static void setActivityContext(Activity act) {
        sActRef = new WeakReference<>(act);
    }

    /**
     * setRemoteServiceMode
     * set active service connection type
     *
     * @param isRemote: boolean
     */
    public static void setRemoteServiceMode(boolean isRemote) {
        mServiceHelper.setRemoteServiceMode(isRemote);
    }

    public static ServiceActItems getItemBy(String str) {
        return Arrays.stream(values())
                .filter(item -> item.mType.equals(str))
                .findFirst()
                .orElse(null);
    }

    public String getType() {
        return mType;
    }

    public abstract void execute();

    protected static Activity getActivity() {
        return sActRef != null ? sActRef.get() : null;
    }
}
