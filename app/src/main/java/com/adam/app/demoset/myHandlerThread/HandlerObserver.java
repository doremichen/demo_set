//
// Handler thread observer interface
//
package com.adam.app.demoset.myHandlerThread;

@FunctionalInterface
public interface HandlerObserver {
    void updateTaskInfo(WorkData data);
}
