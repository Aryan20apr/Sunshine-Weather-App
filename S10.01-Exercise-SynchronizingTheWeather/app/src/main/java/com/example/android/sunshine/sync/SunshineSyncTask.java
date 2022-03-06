package com.example.android.sunshine.sync;//  TODO (1) Create a class called SunshineSyncTask
//  TODO (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//      TODO (3) Within syncWeather, fetch new weather data
//      TODO (4) If we have valid results, delete the old data and insert the new

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.NotificationUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class SunshineSyncTask extends Worker
{
    public static final String SYNC_WEATHER_TAG="sync-weather";
    public static final String SYNC_WEATHER="sync weather now";
    private Context mContext;
    private static final String TAG=SunshineSyncTask.class.getSimpleName();
    public SunshineSyncTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext=context;
        Log.i(TAG+"###","returning from constructor");

    }

    /**
     * Performs the network request for updated weather, parses the JSON from that request, and
     * inserts the new weather information into our ContentProvider. Will notify the user that new
     * weather has been loaded if the user hasn't been notified of the weather within the last day
     * AND they haven't disabled notifications in the preferences screen.
     *
     * @param context Used to access utility methods and the ContentResolver
     */
    synchronized public static void syncWeather(Context context)
    {
        //Fetch new data
        try
        {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL weatherRequestUrl= NetworkUtils.getUrl(context);

            //use the URL to retrieve the JSON
            String jsonWeatherResponse= NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            //Parse the json into a list of weather values

            /*Parse the JSON into list of weather values*/
            ContentValues[] weatherValues= OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context,jsonWeatherResponse);
            /* Get a handle on the ContentResolver to delete and insert data */
            ContentResolver sunshineContentResolver = context.getContentResolver();
            //COMPLETED (4) If we have valid results, delete the old data and insert the new
            /* Delete old weather data because we don't need to keep multiple days' data */
            sunshineContentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,null,null);
            //Insert new weather data into Sunshine's ContentProvider
            sunshineContentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,weatherValues);
            Log.i("SunshineSyncTask###","Inside SunshineSyncTask and inserted new data using sunshineContentResolver");
        }
        catch(Exception e)
        {
            /*Server probably invalid
             */
            e.printStackTrace();
        }
        // Check if notifications are enabled
        /*
         * Finally, after we insert data into the ContentProvider, determine whether or not
         * we should notify the user that the weather has been refreshed.
         */
        boolean notificationsEnabled= SunshinePreferences.areNotificationsEnabled(context);
        Log.i(TAG,"Notifications Enabled"+notificationsEnabled);
        /*
         * If the last notification was shown was more than 1 day ago, we want to send
         * another notification to the user that the weather has been updated. Remember,
         * it's important that you shouldn't spam your users with notifications.
         */
        long timeSinceLastNotification=SunshinePreferences.getEllapsedTimeSinceLastNotification(context);
        boolean oneDayPassedSinceLastNotification=false;
        //COMPLETED (14) Check if a day has passed since the last notification
        Log.i(TAG+"###","Time since last notification="+timeSinceLastNotification);
        Log.i(TAG,"DateUtils.DAY_IN_MILLIS="+DateUtils.DAY_IN_MILLIS);
        if(timeSinceLastNotification>= DateUtils.DAY_IN_MILLIS)
        {
            oneDayPassedSinceLastNotification=true;
        }
        /*
         * We only want to show the notification if the user wants them shown and we
         * haven't shown a notification in the past day.
         */
        //  If more than a day have passed and notifications are enabled, notify the user
        if(notificationsEnabled && oneDayPassedSinceLastNotification)
        {
            Log.i(TAG+"###","Calling notifyUserOfWeather");
            NotificationUtils.notifyUserOfWeather(context);
        }
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
        Log.i(TAG+" ###","entered the doWork() method");
        try {
            syncWeather(mContext);
        }
        catch(Exception e)
        {
            Log.e(TAG+" ###","exception has occured");
            return Result.failure();
        }
        return Result.success();
    }
}