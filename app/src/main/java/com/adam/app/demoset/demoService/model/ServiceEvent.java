/**
 * Copyright (C) Adam demo app Project
 * <p>
 * Description: This class is the service event.
 * <p>
 * Author: Adam Chen
 * Date: 2026/03/11
 */
package com.adam.app.demoset.demoService.model;

public class ServiceEvent {
    private final String mMessage;
    private final long mTime;

    public ServiceEvent(String message) {
        mMessage = message;
        mTime = System.currentTimeMillis();
    }

    public String getMessage() {
        return mMessage;
    }

    public long getTime() {
        return mTime;
    }
}
