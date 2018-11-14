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
        Utils.inFo(this, "onBind enter");
        return mSvrStub.asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.inFo(this, "onBind enter");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.inFo(this, "onStartCommand enter");
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Utils.inFo(this, "onDestroy enter");
        super.onDestroy();
    }

    public int add(int a, int b) {
        Utils.inFo(this, "add method is executed.");
        // Overflow check
        if (a > 0 && b > 0 && a > Integer.MAX_VALUE - b) return -1;


        int c = a + b;

        return c;
    }

    private ServiceStub mSvrStub = new ServiceStub(this);

    private static class ServiceStub extends IMyAidlInterface.Stub {

        private WeakReference<MyAidlService> mRef_svr;

        private RemoteCallbackList<IMyAidlCBInterface> mCallbacks = new RemoteCallbackList<IMyAidlCBInterface>();

        public ServiceStub(MyAidlService svr) {
            Utils.inFo(this, "constructor enter");
            mRef_svr = new WeakReference<MyAidlService>(svr);
        }

        @Override
        public void registerServiceCB(IMyAidlCBInterface callBack) throws RemoteException {
            Utils.inFo(this, "registerServiceCB enter");
            if (callBack != null) {
                mCallbacks.register(callBack);
            }
        }

        @Override
        public void unregisterServiceCB(IMyAidlCBInterface callBack) throws RemoteException {
            Utils.inFo(this, "unregisterServiceCB enter");
            if (callBack != null) {
                mCallbacks.unregister(callBack);
            }
        }

        @Override
        public void add(int a, int b) throws RemoteException {
            Utils.inFo(this, "add enter");
            Utils.inFo(this, "a = " + a);
            Utils.inFo(this, "b = " + b);

            // Set service uid pid
            long tokenId = Binder.clearCallingIdentity();
            // service operation process
            int ret = mRef_svr.get().add(a, b);

            final int N = mCallbacks.beginBroadcast();
            // Call back to UI
            for (int i = 0; i < N; i++) {
                mCallbacks.getBroadcastItem(i).result(ret);
            }
            mCallbacks.finishBroadcast();

            Binder.restoreCallingIdentity(tokenId);

        }
    }
}
