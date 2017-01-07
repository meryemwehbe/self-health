package com.example.self_health.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

/**
 * Created by pc on 1/6/2017.
 */

public class PillFragment extends Fragment {
    private MyCustomAdapter dataAdapter = null;
    private  ArrayList<String> countryList ;
    private Button save;
    private GoogleApiClient mClient;
    private ListView listView;
    //TODO: must be specified by doctor
    private ArrayList<Integer> checked ;
    private String[] pilllist ={
            "Amlodipine",
            "Oxycodone",
            "OxyContin" ,
            "Atenolol (Tenormin)",
            "Omeprazole (Prilosec)",
            "Atorvastatin (Lipitor)",
            "Cetirizine (Zyrtec)",
            "Citalopram (Celexa)",
            "Clonazepam (Klonopin)",
            "Doxazosin (Cardura)",
            "Blood thinners (Coumadin, warfarin)",
            "Finasteride (Proscar)",
            "Levothyroxine (Synthroid)",
            "Lisinopril (Zestril)",
            "Lovastatin (Mevacor)",
            "Metformin (Glucophage)",
            "Metoprolol (Toprol)",
            "Nefazodone (Serzone)",
            "Olanzapine (Zyprexa)",
            "Paroxetine (Paxil)",
            "Pravastatin (Pravachol)",
            "Quinapril (Accupril)",
            "Rosuvastatin (Crestor)",
            "Sertraline (Zoloft)",
            "Sildenafil (Viagra)",
            "Simvastatin (Zocor)",
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      checked = new ArrayList<Integer>();
        for (int i=0;i < pilllist.length; i++){
            checked.add(0);
        }
        mClient = ((MainActivity)getActivity()).mClient;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pill_fragment, container, false);

        countryList = new ArrayList<String>();
        for (int i =0; i <pilllist.length;i++){
            countryList.add(pilllist[i]);
        }
        listView = (ListView) view.findViewById(R.id.list_pills);
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
                DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                        .setName("com.example.self_health.PillsTaken")
                        .addField("Date", Field.FORMAT_STRING)
                        .addField("PillName", Field.FORMAT_STRING)
                        .build();

                PendingResult<DataTypeResult> pendingResult =
                        Fitness.ConfigApi.createCustomDataType(mClient, request);


                pendingResult.setResultCallback(
                        new ResultCallback<DataTypeResult>() {
                            @Override
                            public void onResult(DataTypeResult dataTypeResult) {
                                // Retrieve the created data type
                                DataType pillType = dataTypeResult.getDataType();
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
                                        .setDataType(pillType)
                                        .setStreamName("pill")
                                        .setType(DataSource.TYPE_RAW)
                                        .build();

                                DataSet dataSet = DataSet.create(dataSource);
                                for (int i=0; i <checked.size();i++) {
                                    if(checked.get(i) == 1) {
                                        DataPoint dataPoint = dataSet.createDataPoint()
                                                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(now.toString());
                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setString(countryList.get(i));
                                        dataSet.add(dataPoint);
                                    }

                                }

                                int i = 0;
                                while(i <checked.size()){
                                    if(checked.get(i) == 1) {
                                        checked.remove(i);
                                        countryList.remove(i);

                                    }else{
                                        i++;
                                    }
                                }
                                dataAdapter.notifyDataSetChanged();






                                Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        // Before querying the data, check to see if the insertion succeeded.
                                        if (!status.isSuccess()) {
                                            Toast.makeText(getContext(), "There was a problem with the Saving.", Toast.LENGTH_LONG).show();

                                        } else {

                                            // At this point, the data has been inserted and can be read.
                                            Toast.makeText(getContext(), "Saved!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            }
                        });



            }

        });
        displayListView();
    return view;
    }

    private void displayListView() {


        //create an ArrayAdaptar from the String Array

        dataAdapter = new MyCustomAdapter(getContext(),
                R.layout.pill_list_layout, countryList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        listView.addFooterView(save);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                String pill = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(),
                        "Clicked on Row: " + pill,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<String> {

        private ArrayList<String> countryList;


        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<String> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<String>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.pill_list_layout, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Toast.makeText(getContext(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        if(cb.isChecked()) {
                            checked.set(position, 1);
                        }else{
                            checked.set(position,0);
                        }

                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.code.setText(pilllist[position] );
            holder.name.setChecked((checked.get(position) == 1));

            return convertView;

        }

    }



}


