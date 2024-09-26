// IMyAidlInterface.aidl
package com.adam.app.demoset.binder;

import com.adam.app.demoset.binder.IMyAidlCBInterface;
import com.adam.app.demoset.binder.MyBinderData;

interface IMyAidlInterface {
      // register call back interface
      void registerServiceCB(IMyAidlCBInterface callBack);
      void unregisterServiceCB(IMyAidlCBInterface callBack);
      // service Operation
      void add(int a, int b);

      void sendRequest(in MyBinderData data);
}
