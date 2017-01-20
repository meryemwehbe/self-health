package com.example.self_health.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.activity.DeviceScanActivity;
import com.example.self_health.other.ImageAdapter;
import com.example.self_health.other.ImageAdapterMood;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pc on 11/28/2016.
 */

public class MeasurementFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // references to our images
    private int[] mThumbIds = {
            R.mipmap.ic_unknown,
            R.mipmap.ic_heart,
            R.drawable.image,
            R.mipmap.ic_step,
            R.drawable.pills,
            R.mipmap.ic_mood,
            R.drawable.ic_water,
            R.drawable.ic_food_intake,
            R.drawable.temperature,

    };
    private String[] names ={
            "blood Pressure",
            "Heart Rate",
            "Stress Level",
            "Steps",
            "Pill Intake",
            "Mood",
            "Water Intake",
            "Food Intake",
            "Body Temperature",



    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mid;
    private OnFragmentInteractionListener mListener;

    public MeasurementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DoctorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeasurementFragment newInstance(String param1, String param2) {
        MeasurementFragment fragment = new MeasurementFragment();
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
       mid = getArguments().getString("ID");

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getContext(),names,mThumbIds,300,300));


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {


                switch (position){
                    case 0:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        BloodPressureFragment fragment = new BloodPressureFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    }
                    case 1:{
                        Intent intent = new Intent(MeasurementFragment.this.getActivity(), DeviceScanActivity.class);
                        String str = getResources().getString(R.string.HR_type);
                        intent.putExtra("measurement_type", str);
                        startActivity(intent);
                        break;
                    }
                    case 2:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        StressFragment fragment = new StressFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    }
                    case 3:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        StepFragment fragment = new StepFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;

                    }
                    case 4:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        PillFragment fragment = new PillFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;

                    }
                    case 5:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        MoodFragment fragment = new MoodFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;

                    }
                    case 6:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        HydrogenFragment fragment = new HydrogenFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    }
                    case 7:{
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", mid);
                        FoodFragment fragment = new FoodFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, (Fragment)fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    }
                    case 8:{
                        Intent intent = new Intent(MeasurementFragment.this.getActivity(), DeviceScanActivity.class);
                        String str = getResources().getString(R.string.BODY_TEMP_type);
                        intent.putExtra("measurement_type", str);
                        startActivity(intent);
                        break;
                    }
                }


                Toast.makeText(getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();




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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
         //   throw new RuntimeException(context.toString()
           //         + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
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


}
