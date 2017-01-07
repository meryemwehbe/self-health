package com.example.self_health.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.self_health.R;
import com.example.self_health.activity.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 12/25/2016.
 */

public class StepFragment extends Fragment {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions mgso;
    private String mid;
    private long daily_count,weekly_count = 0;
    private TextView daily,week, weekwin,daywin;
    private int Goal_day, Goal_week;
    private boolean DailyGoalMet,WeeklyGoalMet =false;


    public StepFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mid = getArguments().getString("ID");

        mGoogleApiClient = ((MainActivity)getActivity()).mClient;

        int delay = 5000; // delay for 5 sec.
        int period = 30000; // repeat 1/2 minute.

        //TODO:Get from doctor schedule
        Goal_day = 8000;
        Goal_week = 49000;

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                VerifyDataTask task1 = new VerifyDataTask();
                ViewWeekStepCountTask task2 = new ViewWeekStepCountTask();
                task1.execute();
                task2.execute();
                checkgoals();
                updateUI();
            }
        },delay,period);

        }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_step, container, false);
        daily = (TextView) view.findViewById(R.id.textView);
        week = (TextView) view.findViewById(R.id.textView2);
        weekwin = (TextView) view.findViewById(R.id.week_win);
        daywin = (TextView) view.findViewById(R.id.day_win);

        return  view;
    }

    /*
     * View todays steps
     */

    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long total = 0;
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            } else {
                Log.w("", "There was a problem getting the step count.");

            }

            daily_count = total;

            return null;
        }
    }

    /*
     * View Steps over the last week
     */

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }


    private void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);
        weekly_count = 0;
        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }


    private void showDataSet(DataSet dataSet) {
        for (DataPoint dp : dataSet.getDataPoints()) {
              for(Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                weekly_count += dp.getValue(field).asInt();
            }
        }
    }

    /*
     * Update UI
     */
    private void updateUI(){
        getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
                daily.setText(String.valueOf(daily_count) + "/ "+Goal_day);
                week.setText(String.valueOf(weekly_count) + "/ "+Goal_week );
            }
    });
     }

    /*
     * Check if goal is met
     * //TODO: make global
     */
    private void checkgoals(){
        if(daily_count >= Goal_day){
            if(!DailyGoalMet){
               DisplayDailyWin();
            }
            DailyGoalMet = true;
        }

        if(weekly_count >= Goal_week){
            if(!WeeklyGoalMet){
                DisplayWeeklyWin();
            }
            WeeklyGoalMet = true;
        }
    }

    /*
     * Display win Daily goal
     */
    private void DisplayDailyWin() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                daywin.setVisibility(View.VISIBLE);
            }
        });
    }
    /*
     * Display win Weekly goal
     */
    private void DisplayWeeklyWin() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weekwin.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


}
