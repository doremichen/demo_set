/**
 * Copyright (C) 2021 Adam Chen
 * <p>
 * This interface is the handler observer.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2021-11-11
 */
package com.adam.app.demoset.myHandlerThread;

public interface HandlerObserver {
    void updateTaskInfo(WorkData data);

    void updateTaskStatus(boolean isActive);
}
