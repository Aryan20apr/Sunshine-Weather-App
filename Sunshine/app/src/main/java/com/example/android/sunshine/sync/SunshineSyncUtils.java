package com.example.android.sunshine.sync;
import android.content.ContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.sync.SunshineSyncTask;

import java.util.concurrent.TimeUnit;

public class SunshineSyncUtils
{
    private static final String TAG=SunshineSyncUtils.class.getSimpleName();
    private static boolean sInitialized;//This will be mainly used as a safeguard to prevent calling the synchronize method more than once.

    synchronized public static void initialize(@NonNull final Context context)
    {
        //Only execute this method body if sInitialized is false
                /*
                 * Only perform initialization once per app lifetime. If initialization has already been
                 * performed, we have nothing to do in this method.
                 */
        Log.i(TAG+"###","Entered initialize");
        if(sInitialized) {
            Log.i(TAG+"###","Returning from  initialize as sInitilaize is true");
            return;
        }
        sInitialized=true;
       // ) Call the method you created to schedule a periodic weather sync
        /*
         * This method call triggers Sunshine to create its task to synchronize weather data
         * periodically.
         */
        scheduleSync(context);
//        // Check to see if our weather ContentProvider is empty
//        /*
//         * We need to check to see if our ContentProvider has data to display in our forecast
//         * list. However, performing a query on the main thread is a bad idea as this may
//         * cause our UI to lag. Therefore, we create a thread in which we will run the query
//         * to check the contents of our ContentProvider.
//         */
//        new AsyncTask<Void,Void,Void>()
//        {
//            @Override
//            public Void doInBackground(Void... voids)
//            {
//                /* /* URI for every row of weather data in our weather table*/
//                Uri forecastQueryUri= WeatherContract.WeatherEntry.CONTENT_URI;
//                /*
//                 * Since this query is going to be used only as a check to see if we have any
//                 * data (rather than to display data), we just need to PROJECT the ID of each
//                 * row. In our queries where we display data, we need to PROJECT more columns
//                 * to determine what weather details need to be displayed.
//                 */
//                /*What is the use of projection in SQL?
//                    Projection: A project operation selects only certain columns (fields)
//                    from a table. The result table has a subset of the available columns
//                    and can include anything from a single column to all available columns.*/
//                String projectionColumns[]={WeatherContract.WeatherEntry._ID};
//                String selectionStatement = WeatherContract.WeatherEntry
//                        .getSqlSelectForTodayOnwards();
//                /* Here, we perform the query to check to see if we have any weather data */
//                Cursor cursor = context.getContentResolver().query(
//                        forecastQueryUri,
//                        projectionColumns,
//                        selectionStatement,
//                        null,
//                        null);
//                /*
//                 * A Cursor object can be null for various different reasons. A few are
//                 * listed below.
//                 *
//                 *   1) Invalid URI
//                 *   2) A certain ContentProvider's query method returns null
//                 *   3) A RemoteException was thrown.
//                 *
//                 * Bottom line, it is generally a good idea to check if a Cursor returned
//                 * from a ContentResolver is null.
//                 *
//                 * If the Cursor was null OR if it was empty, we need to sync immediately to
//                 * be able to display data to the user.
//                 */
//                //  COMPLETED (6) If it is empty or we have a null Cursor, sync the weather now!
//                if (null == cursor || cursor.getCount() == 0) {
//                    startImmediateSync(context);
//                }
//
//                /* Make sure to close the Cursor to avoid memory leaks! */
//                assert cursor!=null;
//                cursor.close();
//                return null;
//            }
//        }.execute();
        AppExecutors.getInstance().diskIO().execute(new Runnable(){
            @Override
                    public void run()
            {
            Log.i(TAG + " ###", "entered the run() method");
            /*URI for every row of weather data in our table
             */
            Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                Log.i(TAG + " ###", "forecastQueryUri: " + forecastQueryUri);
            /*
             * Since this query is going to be used only as a check to see if we have any
             * data (rather than to display data), we just need to PROJECT the ID of each
             * row. In our queries where we display data, we need to PROJECT more columns
             * to determine what weather details need to be displayed.
             */
            String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
            String selectionStatement = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();
            /* Here, we perform the query to check to see if we have any weather data */
            Cursor cursor = context.getContentResolver().query(forecastQueryUri, projectionColumns, selectionStatement, null, null);
            /*
             * A Cursor object can be null for various different reasons. A few are
             * listed below.
             *
             *   1) Invalid URI
             *   2) A certain ContentProvider's query method returns null
             *   3) A RemoteException was thrown.
             *
             * Bottom line, it is generally a good idea to check if a Cursor returned
             * from a ContentResolver is null.
             *
             * If the Cursor was null OR if it was empty, we need to sync immediately to
             * be able to display data to the user.
             */
                Log.i(TAG + " ###", "in run() cursor.getCount(): " + cursor.getCount());
                if (cursor == null || cursor.getCount() == 0)
            startImmediateSync(context);
            //Make sure to close the cursor.
                assert cursor != null;
                cursor.close();

        }
        });

    }
    //Helper method to perform a sync immediately using WorkManager for asynchronous execution
    public static void startImmediateSync(@NonNull final Context context)
    {
        Log.i("SunshineSyncUtils###","Inside startImmediateSync and creating enqueing work");
        WorkManager workManager=WorkManager.getInstance(context);
        OneTimeWorkRequest request=new OneTimeWorkRequest.Builder(SunshineSyncTask.class)
                .build();
        workManager.enqueue(request);
    }


    //  COMPLETED (10) Add constant values to sync Sunshine every 3 - 4 hours
    /*
     * Interval at which to sync with the weather. Use TimeUnit for convenience, rather than
     * writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    static void scheduleSync(@NonNull final Context context)
    {
        Log.i(TAG+"###","Entered scheduleSync ");
        Constraints constraints=new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest request=new PeriodicWorkRequest.Builder(SyncWorker.class,SYNC_INTERVAL_HOURS,TimeUnit.HOURS)
                //Set the Input data for ListenableWorker
                .setConstraints(constraints).build();
        WorkManager.getInstance(context.getApplicationContext())
                // Use ExistingWorkPolicy.REPLACE to cancel and delete any existing pending
                // (uncompleted) work with the same unique name. Then, insert the newly-specified
                // work.
                .enqueueUniquePeriodicWork(SunshineSyncTask.SYNC_WEATHER_TAG, ExistingPeriodicWorkPolicy.KEEP, request);

    }
}