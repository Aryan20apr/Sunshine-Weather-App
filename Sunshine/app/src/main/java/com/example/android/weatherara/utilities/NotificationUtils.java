package com.example.android.weatherara.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.example.android.weatherara.DetailActivity;
import com.example.android.weatherara.R;
import com.example.android.weatherara.data.SunshinePreferences;
import com.example.android.weatherara.data.WeatherContract;

public class NotificationUtils {
    private static String TAG=NotificationUtils.class.getSimpleName();
    /*
     * The columns of data that we are interested in displaying within our notification to let
     * the user know there is new weather data available.
     */
    public static final String[] WEATHER_NOTIFICATION_PROJECTION={WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    ,WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,};
    /*
     * We store the indices of the values in the array of Strings above to more quickly be able
     * to access the data from our query. If the order of the Strings above changes, these
     * indices must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;
    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 3004 is in no way significant.
     */
//  COMPLETED (1) Create a constant int value to identify the notification
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String WEATHER_NOTIFICATION_CHANNEL_ID ="notification-id" ;

    /**
     * Constructs and displays a notification for the newly updated weather for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyUserOfWeather(Context context)
    {
        Log.i(TAG+"###","Entered notifyUserOfWeather");
        /* Build the URI for today's weather in order to show up to date data in notification */
        Uri todayWeatherUri=WeatherContract.WeatherEntry.buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));
        /*
         * The MAIN_FORECAST_PROJECTION array passed in as the second parameter is defined in our WeatherContract
         * class and is used to limit the columns returned in our cursor.
         */
        Cursor todayWeatherCursor=context.getContentResolver().query(todayWeatherUri,WEATHER_NOTIFICATION_PROJECTION,null,null,null);
        /*
         * If todayWeatherCursor is empty, moveToFirst will return false. If our cursor is not
         * empty, we want to show the notification.
         */
        if(todayWeatherCursor.moveToFirst())
        {
            /* Weather ID as returned by API, used to identify the icon to be used */
            int weatherId=todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            double high=todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low=todayWeatherCursor.getDouble(INDEX_MIN_TEMP);
            Resources resources = context.getResources();
            int largeArtResourceId = SunshineWeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    resources,
                    largeArtResourceId);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, weatherId, high, low);

            /* getSmallArtResourceIdForWeatherCondition returns the proper art to show given an ID */
            int smallArtResourceId = SunshineWeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherId);
            /*
             * NotificationCompat Builder is a very convenient way to build backward-compatible
             * notifications. In order to use it, we provide a context and specify a color for the
             * notification, a couple of different icons, the title for the notification, and
             * finally the text of the notification, which in our case in a summary of today's
             * forecast.
             */
            //COMPLETED (6) Get a reference to the NotificationManager
            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            //Create a notification channel for Android O devices
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                Log.i(TAG+"###","Creating Notification Channel");
                NotificationChannel mChannel=new NotificationChannel(WEATHER_NOTIFICATION_CHANNEL_ID,"Primary",NotificationManager.IMPORTANCE_HIGH);//High importance to pop up the notification on device using Heads-ip display
                notificationManager.createNotificationChannel(mChannel);
            }
//          COMPLETED (2) Use NotificationCompat.Builder to begin building the notification
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,WEATHER_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                    .setAutoCancel(true);

//          COMPLETED (3) Create an Intent with the proper URI to start the DetailActivity
            /*
             * This Intent will be triggered when the user clicks the notification. In our case,
             * we want to open Sunshine to the DetailActivity to display the newly updated weather.
             */
            Intent detailIntentForToday=new Intent(context, DetailActivity.class);
                    detailIntentForToday.setData(todayWeatherUri);
            //Use TaskStackBuilder to create the proper PendingIntent
            //We want to navigate back to the MainActivity from the DetailActivity if the user clicks the Notification and then clicks back, so use TaskStackBuilder for that.
            TaskStackBuilder taskStackBuilder=TaskStackBuilder.create(context);

            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);

            PendingIntent resultPenidngIntent= null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                resultPenidngIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);
            }
            else
                resultPenidngIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


            //Set the content Intent of the NotificationBuilder
            notificationBuilder.setContentIntent(resultPenidngIntent);


            // COMPLETED (11) If the build version is greater than or equal to JELLY_BEAN and less than OREO,
            // set the notification's priority to PRIORITY_HIGH.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            }

//          COMPLETED (7) Notify the user with the ID WEATHER_NOTIFICATION_ID
            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

//          COMPLETED (8) Save the time at which the notification occurred using SunshinePreferences
            /*
             * Since we just showed a notification, save the current time. That way, we can check
             * next time the weather is refreshed if we should show another notification.
             */
            SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }
        todayWeatherCursor.close();
        }


    /**
     * Constructs and returns the summary of a particular day's forecast using various utility
     * methods and resources for formatting. This method is only used to create the text for the
     * notification that appears when the weather is refreshed.
     * <p>
     * The String returned from this method will look something like this:
     * <p>
     * Forecast: Sunny - High: 14°C Low 7°C
     *
     * @param context   Used to access utility methods and resources
     * @param weatherId ID as determined by Open Weather Map
     * @param high      High temperature (either celsius or fahrenheit depending on preferences)
     * @param low       Low temperature (either celsius or fahrenheit depending on preferences)
     * @return Summary of a particular day's forecast
     */
    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        /*
         * Short description of the weather, as provided by the API.
         * e.g "clear" vs "sky is clear".
         */
        String shortDescription = SunshineWeatherUtils
                .getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        /* Using String's format method, we create the forecast summary */
        String notificationText = String.format(notificationFormat,
                shortDescription,
                SunshineWeatherUtils.formatTemperature(context, high),
                SunshineWeatherUtils.formatTemperature(context, low));

        return notificationText;
    }
}

