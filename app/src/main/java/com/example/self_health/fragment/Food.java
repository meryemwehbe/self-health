package com.example.self_health.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.activity.MainActivity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_BREAKFAST;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_DINNER;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_LUNCH;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_SNACK;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_POTASSIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_SODIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_TOTAL_FAT;

/**
 * Created by pc on 12/28/2016.
 */

public class Food extends Fragment {
    private  ArrayList<String> contentList ;
    private Button btn_save;
    private GoogleApiClient mGoogleApiClient;
    private int type;
    private CustomAdapter dataAdapter = null;
    private Button save;
    private GoogleApiClient mClient;
    private ListView listView;
    //TODO: must be specified by doctor
    private ArrayList<Integer> checked ;
    private String[] contents ={
            "Calcium",
            "Calories",
            "Iron" ,
            "Fiber",
            "Potassium",
            "Protein",
            "Fat",
            "Sodium",
            "Sugar",
            "Vitamin A",
            "Vitamin C",

    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food, container, false);


        String mytype = getArguments().get("Type").toString();
        switch (mytype) {
            case "Breakfast": {
                type = MEAL_TYPE_BREAKFAST;
                break;
            }
            case "Lunch": {
                type =MEAL_TYPE_LUNCH ;
                break;
            }
            case "Dinner": {
                type =MEAL_TYPE_DINNER ;
                break;
            }
            case "Snack": {
                type =MEAL_TYPE_SNACK ;
                break;
            }
        }
        contentList = new ArrayList<String>();
        for (int i =0; i <contents.length;i++){
            contentList.add(contents[i]);
        }
        listView = (ListView) view.findViewById(R.id.content_list);
        save = new Button(getContext());
        save.setText("Save");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        save.setLayoutParams(params);
        save.setBackgroundResource(R.drawable.bg_circle);
        save.setTextColor(Color.WHITE);

        //save.setBackgroundTintList(getContext().getResources().getColorStateList(R.color.colorPrimary, getContext().getTheme()));
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get all items
                for (int i = 0 ; i <contents.length ; i++ ) {
                    listView.getItemAtPosition(i);
                }
/*
                // Set a start and end time for our data, using a start time of 1 hour before this moment.
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                long endTime = cal.getTimeInMillis();
                cal.add(Calendar.HOUR_OF_DAY, -1);
                long startTime = cal.getTimeInMillis();

                // Create a data source
                DataSource dataSource = new DataSource.Builder()
                        .setAppPackageName(getContext())
                        .setDataType(DataType.TYPE_NUTRITION)
                        .setStreamName("food")
                        .setType(DataSource.TYPE_RAW)
                        .build();

                DataSet dataSet = DataSet.create(dataSource);

                DataPoint dataPoint = dataSet.createDataPoint()
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                dataPoint.getValue(Field.FIELD_FOOD_ITEM).setString(str_name);
                dataPoint.getValue(Field.FIELD_MEAL_TYPE).setInt(type);
                dataPoint.getValue(Field.FIELD_NUTRIENTS).setKeyValue(NUTRIENT_TOTAL_FAT,f_fat);
                dataPoint.getValue(Field.FIELD_NUTRIENTS).setKeyValue(NUTRIENT_POTASSIUM,f_pot);
                dataPoint.getValue(Field.FIELD_NUTRIENTS).setKeyValue(NUTRIENT_SODIUM,f_sod);
                dataPoint.getValue(Field.FIELD_NUTRIENTS).setKeyValue(NUTR,f_sod);
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
*/
            }
             });


        mGoogleApiClient = ((MainActivity)getActivity()).mClient;
        displayListView();



        return view;
    }

    private void displayListView() {


        //create an ArrayAdaptar from the String Array

        dataAdapter = new CustomAdapter(getContext(),
                R.layout.pill_list_layout, contentList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.addFooterView(save);
    }

    private class CustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> contentList;


        public CustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> contentList) {
            super(context, textViewResourceId, contentList);
            this.contentList = new ArrayList<String>();
            this.contentList.addAll(contentList);
        }

        private class ViewHolder {
            TextView content;
            EditText edit;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            CustomAdapter.ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.item_content, null);

                holder = new CustomAdapter.ViewHolder();
                holder.content = (TextView) convertView.findViewById(R.id.text);
                holder.edit = (EditText) convertView.findViewById(R.id.edit);
                convertView.setTag(holder);


            }
            else {
                holder = (CustomAdapter.ViewHolder) convertView.getTag();
            }


            holder.content.setText(contents[position] );

            return convertView;

        }

    }

}
