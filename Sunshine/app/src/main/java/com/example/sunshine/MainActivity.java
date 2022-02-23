package com.example.sunshine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunshine.data.SunshinePreferences;
import com.example.sunshine.utilities.NetworkUtils;
import com.example.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;
import java.util.Set;

// #COMPLETED (8) Implement ForecastAdapterOnClickHandler from the MainActivity
//Implement thr proper loaderCallbacks interface and methods--Using loasers insetead of AsynTask
public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //private TextView mWeatherTextView;
    // COMPLETED (33) Delete mWeatherTextView
    // COMPLETED (34) Add a private RecyclerView variable called mRecyclerView
    private RecyclerView mRecyclerView;
    // COMPLETED (35) Add a private ForecastAdapter variable called mForecastAdapter
    private ForecastAdapter mForecastAdapter;

    //TextView variable for error message display
    private TextView mErrorMessageDisplay;

    //Progressbar variable to show and hide the progressbar
    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;

    // COMPLETED (4) Add a private static boolean flag for preference updates and initialize it to false
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        // COMPLETED (36) Delete the line where you get a reference to mWeatherTextView :For using Recyclerview
//        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // COMPLETED (37) Use findViewById to get a reference to the RecyclerView
        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay=(TextView)findViewById(R.id.tv_error_message_display);

        // COMPLETED (38) Create layoutManager, a LinearLayoutManager with VERTICAL orientation and shouldReverseLayout == false
        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // COMPLETED (42) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        // COMPLETED (43) set mForecastAdapter equal to a new ForecastAdapter
        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        //mForecastAdapter = new ForecastAdapter();
        //# COMPLETED (11) Pass in 'this' as the ForecastAdapterOnClickHandler
        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        //mForecastAdapter = new ForecastAdapter();
        mForecastAdapter = new ForecastAdapter(this);
        // COMPLETED (44) Use mRecyclerView.setAdapter and pass in mForecastAdapter
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */

        mLoadingIndicator=findViewById(R.id.pb_loading_indicator);
        //  Create an array of Strings that contain fake weather data
        /*
         * This String array contains dummy weather data. Later in the course, we're going to get
         * real weather data. For now, we want to get something on the screen as quickly as
         * possible, so we'll display this dummy data.
         */
       /* String[] dummyWeatherData = {
                "Today, May 17 - Clear - 17°C / 15°C",
                "Tomorrow - Cloudy - 19°C / 15°C",
                "Thursday - Rainy- 30°C / 11°C",
                "Friday - Thunderstorms - 21°C / 9°C",
                "Saturday - Thunderstorms - 16°C / 7°C",
                "Sunday - Rainy - 16°C / 8°C",
                "Monday - Partly Cloudy - 15°C / 10°C",
                "Tue, May 24 - Meatballs - 16°C / 18°C",
                "Wed, May 25 - Cloudy - 19°C / 15°C",
                "Thu, May 26 - Stormy - 30°C / 11°C",
                "Fri, May 27 - Hurricane - 21°C / 9°C",
                "Sat, May 28 - Meteors - 16°C / 7°C",
                "Sun, May 29 - Apocalypse - 16°C / 8°C",
                "Mon, May 30 - Post Apocalypse - 15°C / 10°C",
        };//Dummy data

          Append each String from the fake weather data array to the TextView
        /*
         * Iterate through the array and append the Strings to the TextView. The reason why we add
         * the "\n\n\n" after the String is to give visual separation between each String in the
         * TextView. Later, we'll learn about a better way to display lists of data.
         */
        /*for (String dummyWeatherDay : dummyWeatherData) {
            mWeatherTextView.append(dummyWeatherDay + "\n\n\n");
        }*/
        /* Once all of our views are setup, we can load the weather data. */
/**         Remove the code for the AsyncTask and initialize the AsyncTaskLoader*/
//        loadWeatherData();

        /*
         * This ID will uniquely identify the Loader. We can use it, for example, to get a handle
         * on our Loader at a later point in time through the support LoaderManager.
         */
        int loaderId = FORECAST_LOADER_ID;

        /*
         * From MainActivity, we have implemented the LoaderCallbacks interface with the type of
         * String array. (implements LoaderCallbacks<String[]>) The variable callback is passed
         * to the call to initLoader below. This means that whenever the loaderManager has
         * something to notify us of, it will do so through this callback.
         */
        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;

        /*
         * The second parameter of the initLoader method below is a Bundle. Optionally, you can
         * pass a Bundle to initLoader that you can then access from within the onCreateLoader
         * callback. In our case, we don't actually use the Bundle, but it's here in case we wanted
         * to.
         */
        Bundle bundleForLoader = null;

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

        Log.d(TAG, "onCreate: registering preference changed listener");


        // COMPLETED (6) Register MainActivity as a OnSharedPreferenceChangedListener in onCreate
        /*
         * Register MainActivity as an OnPreferenceChangedListener to receive a callback when a
         * SharedPreference has changed. Please note that we must unregister MainActivity as an
         * OnSharedPreferenceChanged listener in onDestroy to avoid any memory leaks.
         */
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }
    // Create a method that will get the user's preferred location and execute your new AsyncTask and call it loadWeatherData
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
//    private void loadWeatherData()
//    {
//        //Call showWeatherDataView before executing the AsyncTask
//        showWeatherDataView();
//        String location= SunshinePreferences.getPreferredWeatherLocation(this);
//        new FetchWeatherTask().execute(location);
//    }
    // COMPLETED (8) Create a method called showWeatherDataView that will hide the error message and show the weather data
    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        //mWeatherTextView.setVisibility(View.VISIBLE);
        // COMPLETED (44) Show mRecyclerView, not mWeatherTextView
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    // COMPLETED (9) Create a method called showErrorMessage that will hide the weather data and show the error message
    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
//        /* First, hide the currently visible data */
//        mWeatherTextView.setVisibility(View.INVISIBLE);
        // COMPLETED (44) Hide mRecyclerView, not mWeatherTextView
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
//    // COMPLETED (5) Create a class that extends AsyncTask to perform network requests
//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//        // COMPLETED (18) Within your AsyncTask, override the method onPreExecute and show the loading indicator
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
//        }
//        /**
//         * @param weatherData
//         * @deprecated
//         */
//        @Override
//        protected void onPostExecute(String[] weatherData) {
//            //  As soon as the data is finished loading, hide the loading indicator
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (weatherData != null)
//            {//  If the weather data was not null, make sure the data view is visible
//                showWeatherDataView();
//                /*
//                 * Iterate through the array and append the Strings to the TextView. The reason why we add
//                 * the "\n\n\n" after the String is to give visual separation between each String in the
//                 * TextView. Later, we'll learn about a better way to display lists of data.
//                 */
//               /* for (String weatherString : weatherData) {
//                    mWeatherTextView.append((weatherString) + "\n\n\n");
//                }*/
//                // COMPLETED (45) Instead of iterating through every string, use mForecastAdapter.setWeatherData and pass in the weather data
//                mForecastAdapter.setWeatherData(weatherData);
//        }
//            else
//                //If the weather data was null, show the error message
//            showErrorMessage();
//        }
//
//
//        /**
//         * @param params
//         * @deprecated
//         */
//        @Override
//        protected String[] doInBackground(String... params) {
//            /*If there's no zip code, there's nothing to look up*/
//            if (params.length == 0) {
//                return null;
//            }
//            String location = params[0];
//            URL weatherRequestUrl = NetworkUtils.buildUrl(location);
//            try {
//                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
//                String[] simpleJsonWeatherData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
//                return simpleJsonWeatherData;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//    }
        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
            MenuInflater inflater = getMenuInflater();
            /* Use the inflater's inflate method to inflate our menu layout to this menu */
            inflater.inflate(R.menu.forecast, menu);
            /* Return true so that the menu is displayed in the Toolbar */
            return true;

        }
    //Override onOptionsItemSelected to handle clicks on the refresh button or map action
        @Override
    public boolean onOptionsItemSelected(MenuItem item)
        {
//            int id=item.getItemId();
//            if(id==R.id.action_refresh)
//            {//Clear the weather textview and calling the loadweatherData
////                mWeatherTextView.setText("");
//                // COMPLETED (46) Instead of setting the text to "", set the adapter to null before refreshing
//                mForecastAdapter.setWeatherData(null);
//                loadWeatherData();
//                return true;
//            }
//            // COMPLETED (2) Launch the map when the map menu item is clicked
//            if (id == R.id.action_map) {
//                openLocationInMap();
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
            //Refactor the refresh functionality to work with our AsyncTaskLoader
            int id=item.getItemId();
            if (id == R.id.action_refresh) {
                invalidateData();
                getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
                return true;
            }

            if (id == R.id.action_map) {
                openLocationInMap();
                return true;
            }
            if(id==R.id.action_settings)
            {
                Intent startSettingsActivity=new Intent(this,SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    //# COMPLETED (9) Override ForecastAdapterOnClickHandler's onClick method
    //# COMPLETED (10) Show a Toast when an item is clicked, displaying that item's weather data
    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param weatherForDay The weather for the day that was clicked
     */
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
//        Toast.makeText(context, weatherForDay, Toast.LENGTH_SHORT)
//                .show();
        // Remove the Toast and launch the DetailActivity using an explicit Intent
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        //Pass the weather to the DetailActivity
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT,weatherForDay);
        startActivity(intentToStartDetailActivity);
    }
    /**
     * This method uses the URI scheme for showing a location found on a
     * map. This super-handy intent is detailed in the "Common Intents"
     * page of Android's developer site:
     *
     * @see <a"http://developer.android.com/guide/components/intents-common.html#Maps">
     *
     * Hint: Hold Command on Mac or Control on Windows and click that link
     * to automagically open the Common Intents page
     */
    private void openLocationInMap()
    {
        //String addressString="1600 Ampitheatre Parkway, CA  ";
        String addressString=SunshinePreferences.getPreferredWeatherLocation(this);
        //Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);
        Uri.Builder builder=new Uri.Builder();
        builder.scheme("geo")
                .path("0,0")
                .appendQueryParameter("q", addressString);
        Uri geoLocation = builder.build();

        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivity(intent);
        }
        else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }
        }

    // COMPLETED (2) Within onCreateLoader, return a new AsyncTaskLoader that looks a lot like the existing FetchWeatherTask.
    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs Any arguments supplied by the caller.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<String[]> onCreateLoader(int id,final Bundle loaderArgs)
    {
        return new AsyncTaskLoader<String[]>(this) {
            /* This String array will hold and help cache our weather data */
            String[] mWeatherData = null;
            // COMPLETED (3) Cache the weather data in a member variable and deliver it in onStartLoading.
            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }
            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from OpenWeatherMap in the background.
             *
             * @return Weather data from OpenWeatherMap as an array of Strings.
             *         null if an error occurs
             */
            @Override
            public String[] loadInBackground()
            {
                String locationQuery=SunshinePreferences.getPreferredWeatherLocation((MainActivity.this));
                URL weatherRequestUri=NetworkUtils.buildUrl(locationQuery);
                try{
                    String jsonWeatherResponse=NetworkUtils.getResponseFromHttpUrl(weatherRequestUri);
                    String[] simpleJsonWeatherData=OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                    return simpleJsonWeatherData;
                } catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }
    // COMPLETED (4) When the load is finished, show either the data or an error message if there is no data
    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (null == data) {
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }
    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        /*
         * We aren't using this method in our example application, but we are required to Override
         * it to implement the LoaderCallbacks<String> interface
         */
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mForecastAdapter.setWeatherData(null);
    }

    //Called before the onStart() method when we return to the activity and the preference has changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        /**
         *Set this flag to true so that when control returns to MainActivity, it can refresh the
                * data.
                *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
                * job done for now. Later in this course, we are going to show you more elegant ways to
                * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
                */
        Log.e("MainActivity","Called onSharedPreferenceChanged()");
        PREFERENCES_HAVE_BEEN_UPDATED=true;

    }
    // COMPLETED (7) In onStart, if preferences have been changed, refresh the data and set the flag to false
    /**
     * OnStart is called when the Activity is coming into view. This happens when the Activity is
     * first created, but also happens when the Activity is returned to from another Activity. We
     * are going to use the fact that onStart is called when the user returns to this Activity to
     * check if the location setting or the preferred units setting has changed. If it has changed,
     * we are going to perform a new query.
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        /*
         * If the preferences for location or units have changed since the user was last in
         * MainActivity, perform another query and set the flag to false.
         *
         * This isn't the ideal solution because there really isn't a need to perform another
         * GET request just to change the units, but this is the simplest solution that gets the
         * job done for now. Later in this course, we are going to show you more elegant ways to
         * handle converting the units from celsius to fahrenheit and back without hitting the
         * network again by keeping a copy of the data in a manageable format.
         */
        Log.e("MainActivity","Called onStart()");
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            // getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            LoaderManager.getInstance(this).restartLoader(FORECAST_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }
    // COMPLETED (8) Override onDestroy and unregister MainActivity as a SharedPreferenceChangedListener
    @Override
    protected void onDestroy() {
        super.onDestroy();


        /* Unregister MainActivity as an OnPreferenceChangedListener to avoid any memory leaks. */
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}