package com.hendalqett.app.sunshine;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hendalqett.app.sunshine.data.WeatherContract;


public class DetailActivity extends ActionBarActivity {


    //String forecastData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d("Click", "Click 1");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
//        } else if (id == R.id.action_share) {
//            Log.d("Click", "Click 2");
//            String textToShare = forecastData.concat(" #SunshineApp");
//
//
//            setShareIntent(shareIntent);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
        final String LOG_TAG = DetailFragment.class.getSimpleName();
        private String mForecastStr;
        private String mForecast;
        ShareActionProvider mShareActionProvider;
        private static final int DETAIL_LOADER = 0;
        private static final String[] FORECAST_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP

        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;


        public DetailFragment() {
            //Important
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mForecastStr = intent.getDataString();
            }
//                if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
//                    mForecastData = intent.getStringExtra(Intent.EXTRA_TEXT);
//                }

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            TextView tvForecastData = (TextView) rootView.findViewById(R.id.detail_text);
            tvForecastData.setText(mForecastStr);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem item = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mForecast != null) {
                //Attach the intent to the Share Action Provider
                mShareActionProvider.setShareIntent(createForecastShareIntent());
            } else {
                Log.d(LOG_TAG, "Forecast is null");
            }

        }

        private Intent createForecastShareIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //Prevents the new opened activity we are sharing to, to be on the activity stack
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
            return shareIntent;

        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }
            return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);

        }


        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) {
                return;
            }

            String dateString = Utility.formatDate(
                    data.getLong(COL_WEATHER_DATE));

            String weatherDescription =
                    data.getString(COL_WEATHER_DESC);

            boolean isMetric = Utility.isMetric(getActivity());

            String high = Utility.formatTemperature(getActivity().getApplicationContext(),
                    data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

            String low = Utility.formatTemperature(getActivity().getApplicationContext(),
                    data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

            TextView detailTextView = (TextView) getView().findViewById(R.id.detail_text);
            detailTextView.setText(mForecast);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createForecastShareIntent());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

}