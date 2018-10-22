// IMyAidlInterface.aidl
package com.adam.app.demoset.biner;

import com.adam.app.demoset.biner.IMyAidlCBInterface;

interface IMyAidlInterface {
      // register call back interface
      void registerServiceCB(IMyAidlCBInterface callBack);
      void unregisterServiceCB(IMyAidlCBInterface callBack);
      // service Operation
      void add(int a, int b);
}
