package com.example.self_health.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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
import com.google.android.gms.fitness.ConfigApi;
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
import java.util.zip.Inflater;

/**
 * Created by pc on 12/28/2016.
 */

public class BloodPressureFragment extends Fragment{

    private float discrete1 = 0;
    private float start1 = 0;
    private float end1 = 0;
    private float start_pos1 = 0;
    private int start_position1 = 0;

    private float discrete2 = 0;
    private float start2 = 0;
    private float end2 = 0;
    private float start_pos2 = 0;
    private int start_position2 = 0;

    private EditText hb_text;
    private String medication;
    private int heart_beat;
    private Button save;
    private String ID;

    private GoogleApiClient mGoogleApiClient;
    private DatabaseHelperInformation mdb;
    private Calendar mCalendar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start1 = 75;           // starting value of SeekBar
        end1 = 300;            // end value of SeekBar
        start_pos1 = 115;      //starting position value of SeekBar

        start_position1=(int) (((start_pos1-start1)/(end1-start1))*100);
        discrete1=start_pos1;

        start2 = 75;           // starting value of SeekBar
        end2 = 300;            // end value of SeekBar
        start_pos2 = 115;      //starting position value of SeekBar

        start_position2=(int) (((start_pos2-start2)/(end2-start2))*100);
        discrete2=start_pos2;

        mGoogleApiClient = ((MainActivity)getActivity()).mClient;
        ID = getArguments().getString("ID");
        mCalendar = Calendar.getInstance();
        mdb = new DatabaseHelperInformation(getContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure, container, false);

        //[###########Layout Elements ]
        SeekBar seek=(SeekBar) view.findViewById(R.id.seekBar1);
        SeekBar seek2=(SeekBar) view.findViewById(R.id.seekBar2);
        hb_text = (EditText) view.findViewById(R.id.Heart_beat);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.medicine_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               medication = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                medication=spinner.getItemAtPosition(0).toString();
            }


        });

        //save button
        save = (Button)view.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        heart_beat = Integer.parseInt(hb_text.getText().toString());

                                        //Save in database for doctor
                                        JSONObject json =new JSONObject();
                                        try {
                                            json.put("Systolic",discrete1);
                                            json.put("Diatolic",discrete2);
                                            json.put("HEART_BEAT",heart_beat);
                                            json.put("MEDICATION",medication);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Date now = new Date();
                                        mCalendar.setTime(now);
                                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                        String datetime = dateTimeFormat.format(mCalendar.getTime());

                                        mdb.open();
                                        long status = mdb.createInstance(ID,"BLOOD_PRESSURE",json.toString(),datetime);
                                        mdb.close();

                                        if(status != -1) {
                                            Toast.makeText(getContext(), "Successfully Stored", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_LONG).show();
                                        }

                                        //Save in google fit
                                        DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                                                .setName("com.example.self_health.BLOOD_PRESSURE")
                                                .addField("Systolic", Field.FORMAT_FLOAT)
                                                .addField("Diatolic", Field.FORMAT_FLOAT)
                                                .addField("HEART_BEAT", Field.FORMAT_INT32)
                                                .addField("MEDICATION", Field.FORMAT_STRING)
                                                .build();

                                        PendingResult<DataTypeResult> pendingResult =
                                                Fitness.ConfigApi.createCustomDataType(mGoogleApiClient, request);

                                        pendingResult.setResultCallback(
                                                new ResultCallback<DataTypeResult>() {
                                                    @Override
                                                    public void onResult(DataTypeResult dataTypeResult) {
                                                        // Retrieve the created data type
                                                        DataType pressureType = dataTypeResult.getDataType();
                                                        // Set a start and end time for our data, using a start time of 1 hour before this moment.
                                                        Date now = new Date();
                                                        mCalendar.setTime(now);
                                                        long endTime = mCalendar.getTimeInMillis();
                                                        mCalendar.add(Calendar.HOUR_OF_DAY, -1);
                                                        long startTime = mCalendar.getTimeInMillis();

                                                        // Create a data source
                                                        DataSource dataSource = new DataSource.Builder()
                                                                .setAppPackageName(getContext())
                                                                .setDataType(pressureType)
                                                                .setStreamName("Blood_pressure")
                                                                .setType(DataSource.TYPE_RAW)
                                                                .build();

                                                        DataSet dataSet = DataSet.create(dataSource);

                                                        DataPoint dataPoint = dataSet.createDataPoint()
                                                                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setFloat(discrete1);
                                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setFloat(discrete2);
                                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(2)).setInt(heart_beat);
                                                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(3)).setString(medication);

                                                        dataSet.add(dataPoint);


                                                        Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet).setResultCallback(new ResultCallback<Status>() {
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




        //Get the width of the main view.
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        int width = displaysize.x;


        //int seekbarpoints =(int) (width/(end1-start1));
        int seekbarpoints = (int)(end1);
        //Create a new bitmap that is the width of the screen
        Bitmap bitmap = Bitmap.createBitmap(width, 100, Bitmap.Config.ARGB_8888);
        //A new canvas to draw on.
        Canvas canvas = new Canvas(bitmap);


        //a new style of painting - colour and stoke thickness.
        Paint paint = new Paint();
        paint.setColor(Color.BLUE); //Set the colour to red
        paint.setStyle(Paint.Style.STROKE); //set the style
        paint.setStrokeWidth(1); //Stoke width


        Paint textpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textpaint.setColor(Color.rgb(61, 61, 61));// text color RGB
        textpaint.setTextSize(28);// text size


        //this draws a box around the edge of the bitmap
        canvas.drawRect(0,0,width-1,99,paint);


        int point = 0; //initiate the point variable


        //Start a for loop that will loop seekbarpoints number of times.
        for (int i = 75; i < seekbarpoints; i=i+15  ){


            if (i==75) {
                canvas.drawText(Integer.toString(i), point, 95, textpaint);
            }else if (i>299){
                canvas.drawText(Integer.toString(i), point - 14, 95, textpaint);
            }else {
                canvas.drawText(Integer.toString(i), point - 8, 95, textpaint);
            }
            //the modulus operator is make the long and short lines as shown in the image
            //if i can be divided without a remainder then it will draw a short line


            if ((i%2)==0) {
                //short line
                point = (int) (point  + seekbarpoints - 204);
                canvas.drawLine(point, 30, point, 0, paint);
                //drawLine(startx,startx,endy,endy)
            }else{
                //long line
                point = (int) (point  + seekbarpoints - 204);
                canvas.drawLine(point, 50, point, 0, paint);
            }
        }


        //Create a new Drawable
        Drawable d = new BitmapDrawable(getResources(),bitmap);


        //Set the seekbar widgets background to the above drawable.
        seek.setProgressDrawable(d);
        seek.setProgress(start_position1);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "discrete = "+String.valueOf(discrete1), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            // TODO Auto-generated method stub
            // To convert it as discrete value
                float temp=progress;
                float dis=end1-start1;
                discrete1=(start1+((temp/100)*dis));

            }
        });

        //second seekbar
        seek2.setProgressDrawable(d);
        seek2.setProgress(start_position2);
        seek2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "discrete = "+String.valueOf(discrete1), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                // To convert it as discrete value
                float temp=progress;
                float dis=end2-start2;
                discrete2=(start2+((temp/100)*dis));

            }
        });

        return view;
    }



}


