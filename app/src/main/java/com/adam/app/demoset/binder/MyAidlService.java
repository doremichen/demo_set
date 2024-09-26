package com.adam.app.demoset.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.adam.app.demoset.Utils;

import java.lang.ref.WeakReference;

public class MyAidlService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        Utils.info(this, "onBind enter");
        return mSvrStub.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.info(this, "onBind enter");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.info(this, "onStartCommand enter");
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Utils.info(this, "onDestroy enter");
        super.onDestroy();
    }

    public int add(int a, int b) {
        Utils.info(this, "add method is executed.");
        try {
            return Math.addExact(a, b);
        } catch (ArithmeticException e) {
            return -1;
        }
    }

    private ServiceStub mSvrStub = new ServiceStub(this);

    private static class ServiceStub extends IMyAidlInterface.Stub {

        private WeakReference<MyAidlService> mRefSvr;

        private RemoteCallbackList<IMyAidlCBInterface> mCallbacks = new RemoteCallbackList<IMyAidlCBInterface>();

        public ServiceStub(MyAidlService svr) {
            Utils.info(this, "constructor enter");
            mRefSvr = new WeakReference<MyAidlService>(svr);
        }

        @Override
        public void registerServiceCB(IMyAidlCBInterface callBack) throws RemoteException {
            Utils.info(this, "registerServiceCB enter");
            if (callBack != null) {
                mCallbacks.register(callBack);
            }
        }

        @Override
        public void unregisterServiceCB(IMyAidlCBInterface callBack) throws RemoteException {
            Utils.info(this, "unregisterServiceCB enter");
            if (callBack != null) {
                mCallbacks.unregister(callBack);
            }
        }

        @Override
        public void add(int a, int b) throws RemoteException {
            Utils.info(this, "add enter");
            Utils.info(this, "a = " + a);
            Utils.info(this, "b = " + b);

            // Set service uid pid
            long tokenId = Binder.clearCallingIdentity();
            // service operation process
            int ret = mRefSvr.get().add(a, b);

            final int N = mCallbacks.beginBroadcast();
            // Call back to UI
            for (int i = 0; i < N; i++) {
                mCallbacks.getBroadcastItem(i).result(ret);
            }
            mCallbacks.finishBroadcast();

            Binder.restoreCallingIdentity(tokenId);

        }

        @Override
        public void sendRequest(MyBinderData data) throws RemoteException {
            Utils.info(this, "[sendRequest] enter");
            // Set service uid pid
            long tokenId = Binder.clearCallingIdentity();
            String msg = data.getMessage();

            // Show notification to tell user
            Utils.makeStatusNotification("I got the data from UI: " + msg, mRefSvr.get());
            Binder.restoreCallingIdentity(tokenId);
        }
    }
}
