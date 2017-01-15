package com.example.self_health.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_BREAKFAST;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_DINNER;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_LUNCH;
import static com.google.android.gms.fitness.data.Field.MEAL_TYPE_SNACK;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_CALCIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_CALORIES;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_DIETARY_FIBER;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_IRON;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_POTASSIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_PROTEIN;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_SODIUM;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_SUGAR;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_TOTAL_FAT;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_VITAMIN_A;
import static com.google.android.gms.fitness.data.Field.NUTRIENT_VITAMIN_C;

/**
 * Created by pc on 12/28/2016.
 */

public class Food extends Fragment {
    private  ArrayList<String> contentList ;
    private String meal;
    private GoogleApiClient mGoogleApiClient;
    private int type;
    private CustomAdapter dataAdapter = null;
    private Button save;
    private String mytype;
    private Calendar mCalendar;
    private String[] FoodTypes ={
      "Fruits",
      "Vegetable",
       "Meat",
       "Diary",
       "Fish",
       "Nuts or grains",
       "Sweets",
       "Fast food",
    };
    private ListView listView;
    private Map <String,Integer> filled_fields;
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
    private String[] TYPE_contents ={
            NUTRIENT_CALCIUM,
            NUTRIENT_CALORIES,
            NUTRIENT_IRON ,
            NUTRIENT_DIETARY_FIBER,
            NUTRIENT_POTASSIUM,
            NUTRIENT_PROTEIN,
            NUTRIENT_TOTAL_FAT,
            NUTRIENT_SODIUM,
            NUTRIENT_SUGAR,
            NUTRIENT_VITAMIN_A,
            NUTRIENT_VITAMIN_C,

    };

    private DatabaseHelperInformation mdb;
    private String id;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food, container, false);
        filled_fields = new HashMap<String,Integer>();
        id = getArguments().getString("ID");

        mytype = getArguments().get("Type").toString();
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
        mCalendar = Calendar.getInstance();
        mdb = new DatabaseHelperInformation(getContext());

        //spinner
        // Spinner element
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnertype);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                meal =item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                meal ="other";
            }
        });

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        for(int i = 0 ; i < FoodTypes.length;i++) {
            categories.add(FoodTypes[i]);

        }

        // Creating adapter for spinner
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(myAdapter);

        save = new Button(getContext());
        save.setText("Save");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        save.setLayoutParams(params);
        save.setBackgroundResource(R.drawable.bg_circle);
        save.setTextColor(Color.WHITE);



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save in database for doctor
                JSONObject json =new JSONObject();
                try {
                    json.put("FOOD_ITEM",meal);
                    json.put("MEAL_TYPE",mytype);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (Map.Entry<Object, String> entry : dataAdapter.textValues.entrySet()){
                    String str = entry.getValue();
                    int index = (int)entry.getKey();
                       try {
                            json.put(contents[index] , str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }


                Date now = new Date();
                mCalendar.setTime(now);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String datetime = dateTimeFormat.format(mCalendar.getTime());

                mdb.open();
                long status = mdb.createInstance(id,"FOOD",json.toString(),datetime);
                mdb.close();
                if(status != -1) {
                    Toast.makeText(getContext(), "Successfully Stored", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_LONG).show();
                }

                // Set a start and end time for our data, using a start time of 1 hour before this moment.
                long endTime = mCalendar.getTimeInMillis();
                mCalendar.add(Calendar.HOUR_OF_DAY, -1);
                long startTime = mCalendar.getTimeInMillis();

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
                dataPoint.getValue(Field.FIELD_FOOD_ITEM).setString(meal);
                dataPoint.getValue(Field.FIELD_MEAL_TYPE).setInt(type);
                //filled for each food type filled
                for (Map.Entry<Object, String> entry : dataAdapter.textValues.entrySet()){
                    String str = entry.getValue();
                    int index = (int)entry.getKey();

                    dataPoint.getValue(Field.FIELD_NUTRIENTS).setKeyValue(TYPE_contents[index], Float.parseFloat(str));
                    String k = TYPE_contents[index];
                    Float l =Float.parseFloat(str);
                    dataSet.add(dataPoint);
                }


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
        public Map<Object,String> textValues = new HashMap<Object,String>();


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
                holder.edit.setTag(position);
                holder.edit.addTextChangedListener(new GenericTextWatcher(holder.edit));
                convertView.setTag(holder);

            }
            else {
                holder = (CustomAdapter.ViewHolder) convertView.getTag();
            }

            holder.content.setText(contents[position] );

            return convertView;

        }
        private class GenericTextWatcher implements TextWatcher{

            private View view;
            private GenericTextWatcher(View view) {
                this.view = view;
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            public void afterTextChanged(Editable editable) {

                String text = editable.toString();
                //save the value for the given tag :
                CustomAdapter.this.textValues.put(view.getTag(), editable.toString());
            }
        }

    }

}
