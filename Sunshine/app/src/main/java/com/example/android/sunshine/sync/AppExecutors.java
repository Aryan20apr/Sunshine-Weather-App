package com.example.android.sunshine.sync;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors
{
    //For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;//this is a singleton class
    private final Executor diskIO; //we will only use the diskIO executor in our app.
    private final Executor mainThread;
    private final Executor networkIO;
    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;}
        public static AppExecutors getInstance()
        {
            if (sInstance == null) {
                synchronized (LOCK) {
                    /*
                     * diskIO: is a Single Thread Executor, this ensures that our
                     * database transactions are done in an order so that we do not have
                     * race conditions.
                     * The networkIO Executor is a subPool of 3 threads. This allows us to run
                     * different network calls simultaneously.
                     * The main thread executor used the MainThreadExecutor class which essentially will
                     * pause the Runnables using a Handler associated with the Main Looper. We do not need
                     * this in an activity because we can use runOnUIThread(). When we do not have this runOnUIThread(),
                     * we can access the main thread using this last executor.
                     */
                    sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(3)
                            , new MainThreadExecutor());
                }
            }
            return sInstance;
        }
            public Executor diskIO()
            {
                return diskIO;
            }
            public Executor networkIO()
            {
                return networkIO;
            }
            public Executor mainThread()
            {
                return mainThread;
            }
            public static class MainThreadExecutor implements Executor
            {
                private Handler mainThreadHandler=new Handler(Looper.getMainLooper());

                @Override
                public void execute(Runnable runnable) {
                    mainThreadHandler.post(runnable);
                }
            }
            }


