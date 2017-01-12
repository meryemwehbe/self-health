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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
 * Created by pc on 1/6/2017.
 */

public class StressFragment extends Fragment {
    private float discrete = 0;
    private float start = 0;
    private float end = 0;
    private float start_pos = 0;
    private int start_position = 0;
    private Button save;
    private GoogleApiClient mClient;
    private String reason = "";
    private RadioGroup radiogroup;
    private DatabaseHelperInformation mdb;
    private String ID;
    private Calendar mCalendar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start = 0;           // starting value of SeekBar
        end = 10;            // end value of SeekBar
        start_pos = 5;      //starting position value of SeekBar

        start_position = (int) (((start_pos - start) / (end - start)) * 100);
        discrete = start_pos;
        mClient = ((MainActivity)getActivity()).mClient;
        mdb = new DatabaseHelperInformation(getContext());
        ID = getArguments().getString("ID");
        mCalendar = Calendar.getInstance();

    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stress_level, container, false);

        save = (Button)view.findViewById(R.id.btn_save_stress);
        save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Save in database for doctor
                    JSONObject json =new JSONObject();
                    try {
                        json.put("LEVEL",discrete);
                        json.put("REASON",reason);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Date now = new Date();
                    mCalendar.setTime(now);
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String datetime = dateTimeFormat.format(mCalendar.getTime());

                    mdb.open();
                    // mdb.createtable();
                    long status = mdb.createInstance(ID,"STRESS_LEVEL",json.toString(),datetime);
                    mdb.close();

                    if(status != -1) {
                        Toast.makeText(getContext(), "Successfully Stored", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_LONG).show();
                    }

                    //Save in Googlefit
                    DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                            .setName("com.example.self_health.Stress_level")
                            .addField("Date", Field.FORMAT_STRING)
                            .addField("Level", Field.FORMAT_FLOAT)
                            .addField("Reason", Field.FORMAT_STRING)
                            .build();

                    PendingResult<DataTypeResult> pendingResult =
                            Fitness.ConfigApi.createCustomDataType(mClient, request);


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
                                            .setStreamName("stress_level")
                                            .setType(DataSource.TYPE_RAW)
                                            .build();

                                    DataSet dataSet = DataSet.create(dataSource);

                                    DataPoint dataPoint = dataSet.createDataPoint()
                                            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                                    dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(now.toString());
                                    dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setFloat(discrete);
                                    dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(2)).setString(reason);
                                    dataSet.add(dataPoint);


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
        SeekBar seek = (SeekBar) view.findViewById(R.id.seekBarStressLevel);
        //Get the width of the main view.
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        int width = displaysize.x;

        int seekbarpoints = (int)(width/end);
        //Create a new bitmap that is the width of the screen
        Bitmap bitmap = Bitmap.createBitmap(width, 100, Bitmap.Config.ARGB_8888);
        //A new canvas to draw on.
        Canvas canvas = new Canvas(bitmap);


        //a new style of painting - colour and stoke thickness.
        Paint paint = new Paint();
        paint.setColor(Color.BLUE); //Set the colour to red
        paint.setStyle(Paint.Style.STROKE); //set the style
        paint.setStrokeWidth(5); //Stoke width


        Paint textpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textpaint.setColor(Color.rgb(61, 61, 61));// text color RGB
        textpaint.setTextSize(28);// text size


        //this draws a box around the edge of the bitmap
        canvas.drawRect(0,0,width-1,99,paint);


        int point = 0; //initiate the point variable


        //Start a for loop that will loop seekbarpoints number of times.
        for (int i = 0; i < seekbarpoints; i++  ){


            if ( i == 0 ) {
                canvas.drawText(Integer.toString(i), point, 95, textpaint);
            }else if (i > 9){
                canvas.drawText(Integer.toString(i), point - 14, 95, textpaint);
            }else {
                canvas.drawText(Integer.toString(i), point - 8, 95, textpaint);
            }
            //the modulus operator is make the long and short lines as shown in the image
            //if i can be divided without a remainder then it will draw a short line


            if ((i%2)==0) {
                //short line
                point =  (point  + seekbarpoints );
                canvas.drawLine(point, 30, point, 0, paint);
                //drawLine(startx,startx,endy,endy)
            }else{
                //long line
                point =  (point  + seekbarpoints );
                canvas.drawLine(point, 50, point, 0, paint);
            }
        }


        //Create a new Drawable
        Drawable d = new BitmapDrawable(getResources(),bitmap);


        //Set the seekbar widgets background to the above drawable.
        seek.setProgressDrawable(d);
        seek.setProgress(start_position);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Toast.makeText(getContext(), "discrete = "+String.valueOf(discrete), Toast.LENGTH_SHORT).show();
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
                float dis=end-start;
                discrete=(start+((temp/100)*dis));

            }
        });

            radiogroup = (RadioGroup) view.findViewById(R.id.group);

            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // find which radio button is selected

                    switch(checkedId){
                        case R.id.work:{
                         reason = "Work";
                            break;
                        }
                        case R.id.schedule:{
                        reason = "Schedule";
                            break;
                        }
                        case R.id.social:{
                            reason = "Social";
                            break;
                        }
                        case R.id.unknown:{
                            reason = "Unknown";
                            break;
                        }
                        case R.id.other:{
                            reason = "Other";
                            break;
                        }
                    }

                }

            });


        return view;
    }


}
