package com.hendalqett.app.sunshine;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
            return true;}
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


        public static class DetailFragment extends Fragment {

            static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";
            final String LOG_TAG = DetailFragment.class.getSimpleName();
            private String mForecastStr;

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
            public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                inflater.inflate(R.menu.detailfragment, menu);
                MenuItem item = menu.findItem(R.id.action_share);
                ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
                if (mShareActionProvider != null) {
                    //Attach the intent to the Share Action Provider
                    mShareActionProvider.setShareIntent(createForecastShareIntent());
                } else
                {
                    Log.d(LOG_TAG,"Share Action Provider is null");
                }

            }

            private Intent createForecastShareIntent() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                //Prevents the new opened activity we are sharing to, to be on the activity stack
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr + FORECAST_SHARE_HASHTAG);
                return shareIntent;

            }
        }

    }