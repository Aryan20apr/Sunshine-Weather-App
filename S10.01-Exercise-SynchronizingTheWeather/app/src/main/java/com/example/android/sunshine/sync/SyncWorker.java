package com.example.android.sunshine.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;

public class SyncWorker extends Worker {
    private static final String TAG=SyncWorker.class.getSimpleName();
    private final Context mContext;
    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParameters)
    {
        super(context, workerParameters);
        mContext=context;
        Log.i(TAG+"###","returning from constructor");
    }

    /*
     * Override this method to do your actual background processing.  This method is called on a
     * background thread - you are required to <b>synchronously</b> do your work and return the
     * {@link Result} from this method.  Once you return from this
     * method, the Worker is considered to have finished what its doing and will be destroyed.  If
     * you need to do your work asynchronously on a thread of your own choice, see
     * {@link ListenableWorker}.
     * <p>
     * A Worker has a well defined
     * <a href="https://d.android.com/reference/android/app/job/JobScheduler">execution window</a>
     * to finish its execution and return a {@link Result}.  After
     * this time has expired, the Worker will be signalled to stop.
     *
     * @return The {@link Result} of the computation; note that
     * dependent work will not execute if you use
     * {@link Result#failure()} or
     * {@link Result#failure(Data)}
     */
    @NonNull
    @Override
    public Result doWork() {

        try
        {   Log.i("SyncWorker"+"###","Indside doWork() and calling syncWeather in try block");

            SunshineSyncTask.syncWeather(mContext);
            return Result.success();
        }
        catch(Exception exception)
        {
            Log.e(TAG+" ###","error has occurred in doWork() method");
            return Result.failure();
        }
    }

    /**
     * This method is invoked when this Worker has been told to stop.  At this point, the
     * {@link ListenableFuture} returned by the instance of {@link #startWork()} is
     * also cancelled.  This could happen due to an explicit cancellation signal by the user, or
     * because the system has decided to preempt the task.  In these cases, the results of the
     * work will be ignored by WorkManager.  All processing in this method should be lightweight
     * - there are no contractual guarantees about which thread will invoke this call, so this
     * should not be a long-running or blocking operation.
     */
    @Override
    public void onStopped() {

        WorkManager.getInstance(getApplicationContext()).cancelUniqueWork(SunshineSyncTask.SYNC_WEATHER_TAG);
        Log.i("SyncWorker###","Inside onStopped() and called super.onStopped()");
        super.onStopped();
    }
}
