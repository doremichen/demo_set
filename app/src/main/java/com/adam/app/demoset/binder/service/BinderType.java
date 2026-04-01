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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.binder.IMyAidlCBInterface;
import com.adam.app.demoset.binder.IMyAidlInterface;

public enum BinderType {
    AIDL {
      private IMyAidlInterface mProxy;
      private IMyAidlCBInterface mAidlCB = new AidlServiceCB();
      private Callback mCallback;

      @Override
      public void setCallback(Callback callback) {
          mCallback = callback;
      }

      @Override
      public void execute(int a, int b) {
          // add log
          if (mCallback == null) {
              Utils.info(BinderType.class, "callback is null!!!");
              return;
          }

          mCallback.showLog("execute aidl binder call");

          // add
          try {
              mProxy.add(a, b);
              // send request
              MyBinderData data = new MyBinderData("Binder data");
              mProxy.sendRequest(data);
          } catch (RemoteException e) {
            Utils.info(BinderType.class, "execute aidl error!!!");
            throw new RuntimeException(e);
          }

      }

      @Override
      public ServiceConnection getConnect() {
          ServiceConnection conn = new ServiceConnection() {
              @Override
              public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                     // add log
                      if (mCallback != null) {
                          mCallback.showLog("aidl onServiceConnected");
                      } else {
                          Utils.info(BinderType.class, "callback is null!!!");
                      }

                      mProxy = IMyAidlInterface.Stub.asInterface(iBinder);
                      // register call back
                      try {
                          mProxy.registerServiceCB(mAidlCB);
                      } catch (RemoteException e) {
                          Utils.info(BinderType.class, "register call back error!!!");
                          throw new RuntimeException(e);
                      }
              }

              @Override
              public void onServiceDisconnected(ComponentName componentName) {
                      // add log
                      if (mCallback != null) {
                          mCallback.showLog("aidl onServiceDisconnected");
                      } else {
                          Utils.info(BinderType.class, "callback is null!!!");
                      }

                      // unregister call back
                      try {
                          mProxy.unregisterServiceCB(mAidlCB);
                      } catch (RemoteException e) {
                          throw new RuntimeException(e);
                      }
                      mProxy = null;
              }
          };
        return conn;
      }

        /**
         * aidl callback interface
         */
        private class AidlServiceCB extends IMyAidlCBInterface.Stub {
            @Override
            public void result(int c) throws RemoteException {
                // callback null check
                if (mCallback == null) {
                    Utils.info(BinderType.class, "callback is null!!!");
                    return;
                }

                mCallback.showLog("aidl callback result");

                mCallback.result(c);
            }
        }
    },
    MESSENGER {

        private Messenger mProxy;
        private Messenger mUIMessenger = new Messenger(new CallbackHandler());
        private Callback mCallback;

        @Override
        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void execute(int a, int b) {
            // add log
            if (mCallback == null) {
                Utils.info(BinderType.class, "callback is null!!!");
                return;
            }

            mCallback.showLog("execute messenger binder call");

            // add
            try {
                Message msg = Message.obtain();
                msg.what = MyMessengerService.ACTION_ADD;
                msg.arg1 = a;
                msg.arg2 = b;
                msg.replyTo = mUIMessenger;
                // send
                mProxy.send(msg);
            } catch (RemoteException e) {
                Utils.info(BinderType.class, "execute messenger error!!!");
                throw new RuntimeException(e);
            }
        }

        @Override
        public ServiceConnection getConnect() {
          ServiceConnection conn = new ServiceConnection() {
              @Override
              public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                  // add log
                  if (mCallback != null) {
                      mCallback.showLog("messenger onServiceConnected");
                  } else {
                      Utils.info(BinderType.class, "callback is null!!!");
                  }
                  mProxy = new Messenger(iBinder);
              }

              @Override
              public void onServiceDisconnected(ComponentName componentName) {
                  if (mCallback != null) {
                      mCallback.showLog("messenger onServiceDisconnected");
                  } else {
                      Utils.info(BinderType.class, "callback is null!!!");
                  }

                  mProxy = null;
              }
          };

          return conn;
        }

        /**
         * messenger callback interface
         */
        private class CallbackHandler extends Handler {

            CallbackHandler() {
                super(Looper.getMainLooper());
            }

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                // add log
                if (mCallback == null) {
                    Utils.info(BinderType.class, "callback is null!!!");
                    return;
                }

                mCallback.showLog("messenger callback result");

                int flag = msg.what;

                if (flag == MyMessengerService.ACTION_REPLY_RESULT) {
                    int result = msg.arg1;
                    mCallback.result(result);
                }
            }
        }

    };

    public interface Callback {
        void result(int c);
        void showLog(String msg);
    }

    public abstract void setCallback(Callback callback);
    public abstract void execute(int a, int b);
    public abstract ServiceConnection getConnect();
}
