/**
 * This class is the thread helper template class.
 *  Usage
 *  ThreadHelper<String> helper = new ThreadHelper.Builder<String>()
 *         .setTask(() -> {
 *             // ---- background work ----
 *             for (int i = 0; i < 5; i++) {
 *                 if (Thread.currentThread().isInterrupted())
 *                     throw new InterruptedException();
 *
 *                 Thread.sleep(1000); // heavy work
 *             }
 *             return "Complete task!!!";
 *         })
 *         .setCallback(new ThreadCallback<String>() {
 *
 *             @Override
 *             public void onStarted() {
 *                 progressDialog.show();
 *             }
 *
 *             @Override
 *             public void onSuccess(String result) {
 *                 Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
 *             }
 *
 *             @Override
 *             public void onError(Exception e) {
 *                 Log.e("ThreadHelper", "Error = " + e);
 *             }
 *
 *             @Override
 *             public void onCancelled() {
 *                 Toast.makeText(context, "Cancel!!!", Toast.LENGTH_SHORT).show();
 *             }
 *
 *             @Override
 *             public void onFinished() {
 *                 progressDialog.dismiss();
 *             }
 *         })
 *         .build();
 *
 * helper.start();
 * helper.stop();
 * helper.shutDown();
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-14
 */
package com.adam.app.demoset;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadHelper<T> {

    private static final String TAG = "ThreadHelper";

    // Executor service
    private final ExecutorService mExecutorService;
    // Future
    private Future<?> mFuture;
    // Task
    private Callable<T> mTask;
    // thread callback
    private ThreadCallback<T> mThreadCallback;
    // main handler
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Constructor
     */
    private ThreadHelper() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * start thread
     */
    public void start() {
        // check task and callback
        if (mTask == null) {
            throw new IllegalStateException("Task must be set!!!");
        }
        // start callback
        mHandler.post(() -> {
            if (mThreadCallback != null)
                mThreadCallback.onStarted();
        });
        // start thread
        mFuture = mExecutorService.submit(() -> {
            try {
                T result = mTask.call();
                mHandler.post(() -> {
                    if (mThreadCallback != null)
                        mThreadCallback.onSuccess(result);
                });
                return result;
            } catch (InterruptedException e) {
                mHandler.post(() -> {
                    if (mThreadCallback != null)
                        mThreadCallback.onCancelled();
                });
            } catch (Exception e) {
                mHandler.post(() -> {
                    if (mThreadCallback != null)
                        mThreadCallback.onError(e);
                });
            } finally {
                mHandler.post(() -> {
                    if (mThreadCallback != null)
                        mThreadCallback.onFinished();
                });
            }
            return null;
        });
    }

    /**
     * cancel thread
     */
    public void stop() {
        if (mFuture != null) {
            mFuture.cancel(true);
        }
    }

    /**
     * check thread is running
     *
     * @return true if thread is running otherwise false
     */
    public boolean isRunning() {
        return mFuture != null
                && !mFuture.isDone()
                && !mFuture.isCancelled();
    }

    /**
     * shut down thread
     */
    public void shutDown() {
        if (mExecutorService != null) {
            mExecutorService.shutdown();
        }
    }

    /**
     * interface ThreadCallback
     */
    public interface ThreadCallback<T> {
        void onStarted();

        void onSuccess(T result);

        void onError(Exception e);

        void onCancelled();

        void onFinished();
    }

    // --------------------------------------
    // builder
    // --------------------------------------
    public static class Builder<T> {
        private final ThreadHelper<T> mThreadHelper = new ThreadHelper<>();

        public Builder<T> setTask(Callable<T> task) {
            mThreadHelper.mTask = task;
            return this;
        }

        public Builder<T> setCallback(ThreadCallback<T> callback) {
            mThreadHelper.mThreadCallback = callback;
            return this;
        }

        public ThreadHelper<T> build() {
            return mThreadHelper;
        }
    }
}
