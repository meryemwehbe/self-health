package com.example.self_health.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.activity.LoginActivity;
import com.example.self_health.activity.MainActivity;
import com.example.self_health.activity.MainActivityDoctor;
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

/**
 * Created by pc on 12/4/2016.
 */

public class AddPatients  extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String id_toadd;
    private EditText i;
    private Button btn_add;
    private GoogleApiClient mClient;
    private String name,Familyname,myid;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AddPatients.OnFragmentInteractionListener mListener;

    public AddPatients() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPatients.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPatients newInstance(String param1, String param2) {
        AddPatients fragment = new AddPatients();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        mClient = ((MainActivityDoctor)getActivity()).mClient;
        name = getArguments().getString("Name");
        Familyname = getArguments().getString("Familyname");
        myid = getArguments().getString("ID");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_add_patients, container, false);
        i = (EditText) view.findViewById(R.id.ID);
        btn_add = (Button) view.findViewById(R.id.btn_add_id);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_toadd = i.getText().toString();
                ADDID();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddPatients.OnFragmentInteractionListener) {
            mListener = (AddPatients.OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
            //      + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*
     * Add Patient Id to Doctor
     */
    private void ADDID(){
       // First time only
        DataTypeCreateRequest request = new DataTypeCreateRequest.Builder()
                .setName("com.example.self_health.Doctor_Patient_Relation")
                .addField("ID_self", Field.FORMAT_STRING)
                .addField("ID_patient", Field.FORMAT_STRING)
                .build();
        ///
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
                                .setStreamName("type")
                                .setType(DataSource.TYPE_RAW)
                                .build();
                        String Type ="0";
                        DataSet dataSet = DataSet.create(datasource);
                        Calendar cal = Calendar.getInstance();
                        Date now = new Date();
                        cal.setTime(now);
                        long endTime = cal.getTimeInMillis();
                        cal.add(Calendar.HOUR_OF_DAY, -1);
                        long startTime = cal.getTimeInMillis();
                        DataPoint dataPoint = DataPoint.create(datasource);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(myid);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setString(id_toadd);
                        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                        dataSet.add(dataPoint);

                        Toast.makeText(getActivity(), "Adding ID", Toast.LENGTH_SHORT).show();
                        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Before querying the data, check to see if the insertion succeeded.
                                if (!status.isSuccess()) {
                                    Toast.makeText(getActivity(), "There was a problem with the adding the ID.", Toast.LENGTH_LONG).show();

                                } else {

                                    // At this point, the data has been inserted and can be read.
                                    Toast.makeText(getActivity(), "Adding ID was successful!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


                    }
                }
        );


        //Always
        PendingResult<DataTypeResult> pendingResult2 =
                Fitness.ConfigApi.readDataType(mClient, "com.example.self_health.UserType");

        // 2. Check the result asynchronously
        // (The result may not be immediately available)
        pendingResult2.setResultCallback(
                new ResultCallback<DataTypeResult>() {
                    @Override
                    public void onResult(DataTypeResult dataTypeResult) {
                        final DataType customType = dataTypeResult.getDataType();
                        //creating a new data source
                        DataSource datasource = new DataSource.Builder()
                                .setAppPackageName(getContext())
                                .setDataType(customType)
                                .setStreamName("type")
                                .setType(DataSource.TYPE_RAW)
                                .build();

                        String Type ="1";
                        DataSet dataSet = DataSet.create(datasource);
                        Calendar cal = Calendar.getInstance();
                        Date now = new Date();
                        cal.setTime(now);
                        long endTime = cal.getTimeInMillis();
                        cal.add(Calendar.HOUR_OF_DAY, -1);
                        long startTime = cal.getTimeInMillis();
                        DataPoint dataPoint = DataPoint.create(datasource);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(0)).setString(id_toadd);
                        dataPoint.getValue(dataPoint.getDataSource().getDataType().getFields().get(1)).setString(Type);
                        dataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
                        dataSet.add(dataPoint);

                        Toast.makeText(getActivity(), "Adding ID to overall database", Toast.LENGTH_SHORT).show();
                        Fitness.HistoryApi.insertData(mClient, dataSet).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                // Before querying the data, check to see if the insertion succeeded.
                                if (!status.isSuccess()) {
                                    Toast.makeText(getActivity(), "There was a problem with the adding the ID to overall database.", Toast.LENGTH_LONG).show();

                                } else {

                                    // At this point, the data has been inserted and can be read.
                                    Toast.makeText(getActivity(), "Adding ID to overall database was successful!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });

    }

}