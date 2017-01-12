package com.example.self_health.fragment;

import android.content.Context;
import android.database.Cursor;
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
import com.example.self_health.other.DataBaseHelperRelations;
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
    private EditText i,n;
    private Button btn_add;
    private GoogleApiClient mClient;
    private String name,Familyname,myid;
    public DataBaseHelperRelations mdb;

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

        mdb = new DataBaseHelperRelations(getContext());
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
        n = (EditText) view.findViewById(R.id.name);
        btn_add = (Button) view.findViewById(R.id.btn_add_id);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_toadd = i.getText().toString();
                mdb.open();
                long status = mdb.insertPatient(myid,id_toadd,n.getText().toString());
                if(status != -1) {
                    Toast.makeText(getContext(), "Patient Added", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Something went wrong try again", Toast.LENGTH_LONG).show();
                }
                mdb.close();
            }
        });

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





}