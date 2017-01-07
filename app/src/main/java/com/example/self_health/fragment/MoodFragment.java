package com.example.self_health.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.activity.LoginActivity;
import com.example.self_health.activity.MainActivity;
import com.example.self_health.other.ImageAdapter;
import com.example.self_health.other.ImageAdapterMood;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.result.DataTypeResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.NUTRIENT_POTASSIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_SODIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_TOTAL_FAT;

/**
 * Created by pc on 1/5/2017.
 */

public class MoodFragment extends Fragment {
    // references to our images
    private int[] mThumbIds = {
            R.mipmap.ic_normal,
            R.mipmap.ic_positive,
            R.mipmap.ic_negative,
            R.mipmap.ic_anxious,
            R.mipmap.ic_energized,
            R.mipmap.ic_low_energy,
            R.mipmap.ic_happy,
            R.mipmap.ic_sad,
            R.mipmap.ic_mood_swing,
            R.mipmap.ic_sleepy,


    };
    private String[] names = {
            "Normal",
            "Positive",
            "Negative",
            "Anxious",
            "Energized",
            "Low Energy",
            "Happy",
            "Sad",
            "Mood Swings",
            "Sleepy"
    };
    private String current_mood = "";
    private GoogleApiClient mClient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mood, container, false);
        GridView gridview = (GridView) view.findViewById(R.id.gridviewmood);
        gridview.setAdapter(new ImageAdapter(getContext(),names,mThumbIds,200,200));
        mClient = ((MainActivity)getActivity()).mClient;

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                current_mood = names[position];
                SaveMood();


            }
        });

        return  view;
    }

    private void SaveMood(){
        DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                .setName("com.example.self_health.MyMood")
                .addField("Date", Field.FORMAT_STRING)
                .addField("Mood", Field.FORMAT_STRING)
                .build();
        PendingResult<DataTypeResult> pendingResult =
                Fitness.ConfigApi.createCustomDataType(mClient, request);

        // 3. Check the result asynchronously
        // (The result may not be immediately available)
        pendingResult.setResultCallback(
                new ResultCallback<DataTypeResult>() {
                    @Override
                    public void onResult(DataTypeResult dataTypeResult) {
                        // Retrieve the created data type
                        DataType type = dataTypeResult.getDataType();
                        //creating a new data source
                        DataSource datasource = new DataSource.Builder()
                                .setAppPackageName(getContext())
                                .setDataType(type)
                                .setStreamName("mood")
                                .setType(DataSource.TYPE_RAW)
                                .build();

                        DataSet dataSet = DataSet.create(datasource);
                        Calendar cal = Calendar.getInstance();
                        Date now = new Date();
                        cal.setTime(now);
                        long endTime = cal.getTimeInMillis();
                        cal.add(Calendar.HOUR_OF_DAY, -1);
                        long startTime = cal.getTimeInMillis();
                        DataPoint dataPoint = DataPoint.create(datasource);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(now.toString());
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setString(current_mood);
                        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                        dataSet.add(dataPoint);

                        Toast.makeText(getContext(), "Saving Mood ... ", Toast.LENGTH_SHORT).show();
                        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Before querying the data, check to see if the insertion succeeded.
                                if (!status.isSuccess()) {
                                    Toast.makeText(getContext(), "There was a problem with saving the mood.", Toast.LENGTH_LONG).show();

                                } else {

                                    // At this point, the data has been inserted and can be read.
                                    Toast.makeText(getContext(), "Mood Saved", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }
                }
        );
    }
}
