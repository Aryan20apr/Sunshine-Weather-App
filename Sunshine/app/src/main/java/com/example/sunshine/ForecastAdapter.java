package com.example.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//(15) Add a class called ForecastAdapter
// COMPLETED (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>
/**
 * {@link ForecastAdapter} exposes a list of weather forecasts to a
 * {@link androidx.recyclerview.widget.RecyclerView}
 */
public class ForecastAdapter extends  RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // COMPLETED (23) Create a private string array called mWeatherData
    private String[] mWeatherData;

//    // COMPLETED (47) Create the default constructor (we will pass in parameters in a later lesson)
//    public ForecastAdapter() {
//
//    }
//# COMPLETED (3) Create a final private ForecastAdapterOnClickHandler called mClickHandler
/*
 * An on-click handler that we've defined to make it easy for an Activity to interface with
 * our RecyclerView
 */
private final ForecastAdapterOnClickHandler mClickHandler;
    // #COMPLETED (1) Add an interface called ForecastAdapterOnClickHandler
    // #COMPLETED (2) Within that interface, define a void method that access a String as a parameter
    /**
     * The interface that receives onClick messages.
     */
    public interface ForecastAdapterOnClickHandler
{
    void onClick(String weatherForDay);
}
    /**
     * The interface that receives onClick messages.
     */
    // COMPLETED (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // COMPLETED (17) Extend RecyclerView.ViewHolder
    /**
     * Cache of the children views for a forecast list item.
     */

    // COMPLETED (4) Add a ForecastAdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
    /**
     * Creates a ForecastAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
    // # COMPLETED (5) Implement OnClickListener in the ForecastAdapterViewHolder class
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // COMPLETED (18) Create a public final TextView variable called mWeatherTextView
        public final TextView mWeatherTextView;

        // COMPLETED (19) Create a constructor for this class that accepts a View as a parameter
        // COMPLETED (20) Call super(view)
        // COMPLETED (21) Using view.findViewById, get a reference to this layout's TextView and save it to mWeatherTextView
        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            //# COMPLETED (7) Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }
        // # COMPLETED (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }
        // COMPLETED (24) Override onCreateViewHolder
        // COMPLETED (25) Within onCreateViewHolder, inflate the list item xml into a view
        // COMPLETED (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter

        /*
         * This gets called when each new ViewHolder is created. This happens when the RecyclerView
         * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
         *
         * @param viewGroup The ViewGroup that these ViewHolders are contained within.
         * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
         *                  can use this viewType integer to provide a different layout. See
         *                  {@link androidx.recyclerview.widget.RecyclerView#getItemViewType(int)}
         *                  for more details.
         * @return A new ForecastAdapterViewHolder that holds the View for each list item
         */
        @Override
        public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.forecast_list_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            return new ForecastAdapterViewHolder(view);
        }
        // COMPLETED (27) Override onBindViewHolder
        // COMPLETED (28) Set the text of the TextView to the weather for this list item's position

        /**
         * OnBindViewHolder is called by the RecyclerView to display the data at the specified
         * position. In this method, we update the contents of the ViewHolder to display the weather
         * details for this particular position, using the "position" argument that is conveniently
         * passed into us.
         *
         * @param forecastAdapterViewHolder The ViewHolder which should be updated to represent the
         *                                  contents of the item at the given position in the data set.
         * @param position                  The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
            String weatherForThisDay = mWeatherData[position];
            forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
        }
        // COMPLETED (29) Override getItemCount
        // COMPLETED (30) Return 0 if mWeatherData is null, or the size of mWeatherData if it is not null

        /**
         * This method simply returns the number of items to display. It is used behind the scenes
         * to help layout our Views and for animations.
         *
         * @return The number of items available in our forecast
         */

        @Override
        public int getItemCount() {
            if (null == mWeatherData) return 0;
            return mWeatherData.length;
        }
    // COMPLETED (31) Create a setWeatherData method that saves the weatherData to mWeatherData
    // COMPLETED (32) After you save mWeatherData, call notifyDataSetChanged
    /**
     * This method is used to set the weather forecast on a ForecastAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new ForecastAdapter to display it.
     *
     * @param weatherData The new weather data to be displayed.
     */
        public void setWeatherData(String[] weatherData) {
            mWeatherData = weatherData;
            notifyDataSetChanged();
        }

    }


