package com.example.self_health.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.activity.MainActivity;
import com.example.self_health.other.DatabaseHelperInformation;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 12/28/2016.
 */

public class HydrogenFragment extends Fragment {

    private Button save;
    private GoogleApiClient mGoogleApiClient;
    private NumberPicker np;
    private int nb_cups = 0;
    private String ID;
    private DatabaseHelperInformation mdb;
    private Calendar mCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hydration, container, false);

        ID = getArguments().getString("ID");
        mdb = new DatabaseHelperInformation(getContext());
        mCalendar = Calendar.getInstance();

        save = (Button) view.findViewById(R.id.btn_add_water);
        np = (NumberPicker) view.findViewById(R.id.numberPicker);
        mGoogleApiClient = ((MainActivity)getActivity()).mClient;
        np.setMinValue(0);
        np.setMaxValue(25);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
               nb_cups = newVal;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                //Save for doctor
                JSONObject json =new JSONObject();
                try {
                    json.put("VOLUME",0.25*nb_cups);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Date now = new Date();
                mCalendar.setTime(now);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String datetime = dateTimeFormat.format(mCalendar.getTime());

                mdb.open();
                long status = mdb.createInstance(ID,"HYDRATION",json.toString(),datetime);
                mdb.close();
                if(status != -1) {
                    Toast.makeText(getContext(), "Successfully stored", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_LONG).show();
                }

                // Set a start and end time for our data, using a start time of 1 hour before this moment.
                mCalendar.setTime(now);
                long endTime = mCalendar.getTimeInMillis();
                mCalendar.add(Calendar.HOUR_OF_DAY, -1);
                long startTime = mCalendar.getTimeInMillis();

                // Create a data source
                DataSource dataSource = new DataSource.Builder()
                        .setAppPackageName(getContext())
                        .setDataType(DataType.TYPE_HYDRATION)
                        .setStreamName("Hydration in Liter")
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet dataSet = DataSet.create(dataSource);

                DataPoint dataPoint = dataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                dataPoint.getValue(Field.FIELD_VOLUME).setFloat((float)0.25*nb_cups);

                dataSet.add(dataPoint);


                Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // Before querying the data, check to see if the insertion succeeded.

                        if (!status.isSuccess()) {
                            String sta = status.getStatusMessage();
                            Toast.makeText(getContext(), "There was a problem with the Saving.", Toast.LENGTH_LONG).show();

                        } else {

                            // At this point, the data has been inserted and can be read.
                            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                            }
                        });


        return view;
    }
}
