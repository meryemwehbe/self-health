package com.example.self_health.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.other.DataBaseHelperAssignTasks;
import com.example.self_health.other.DataBaseHelperRelations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;

/**
 * Created by pc on 12/4/2016.
 */

public class AssignTasks  extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner spname,spaction,sptype;
    private String message="";
    private EditText editmessage;
    private String Patientid,myid;
    private DataBaseHelperRelations mdb;
    private Calendar mCalendar;
    private ArrayList<String> patientnames = new ArrayList<String>();
    private ArrayList<String> patientids = new ArrayList<String>();
    private String mAction="";
    private String measuretype,intaketype="";
    private  TableRow invisiblerow;
    private EditText editpills;
    private Button btn_edit;
    private AssignTasks.OnFragmentInteractionListener mListener;
    private DataBaseHelperAssignTasks mdbassign;


    private String[] actions = {
            "None",
            "Measure",
            "Take Pills",
            "InTake",
    };

    public AssignTasks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AssignTasks.
     */
    // TODO: Rename and change types and number of parameters
    public static AssignTasks newInstance(String param1, String param2) {
        AssignTasks fragment = new AssignTasks();
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
        myid = getArguments().getString("ID");
        patientids.add("NONE");
        patientnames.add("NONE");
        mCalendar = Calendar.getInstance();
        //get patients
        mdb = new DataBaseHelperRelations(getContext());
        mdbassign = new DataBaseHelperAssignTasks(getContext());


        mdb.open();
        Cursor cursor = mdb.getPatients(myid);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){

            patientids.add(cursor.getString(2));
            patientnames.add(cursor.getString(3));
            cursor.moveToNext();

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assign_tasks, container, false);

        invisiblerow = (TableRow)view.findViewById(R.id.pillrow);
        editpills = (EditText)view.findViewById(R.id.pill_to_take);
        editmessage = (EditText)view.findViewById(R.id.message);
        btn_edit = (Button) view.findViewById(R.id.btn_assign);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editmessage.getText().toString();
                Date now = new Date();
                mCalendar.setTime(now);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String datetime = dateTimeFormat.format(mCalendar.getTime());

                switch(mAction){
                    case "Measure":{
                        if(!Patientid.equals("NONE")) {
                            mdbassign.open();
                            mdbassign.createInstance(Patientid, mAction, measuretype, message, "0", datetime);
                            mdbassign.close();
                            Toast.makeText(getContext(),"Assignement Assigned!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(),"No Patient was selected",Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case "Take Pills":{
                        if(!Patientid.equals("NONE")) {
                            String pill = editpills.getText().toString();
                            mdbassign.open();
                            mdbassign.createInstance(Patientid, mAction, pill, message, "0", datetime);
                            mdbassign.close();
                            Toast.makeText(getContext(),"Assignement Assigned!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(),"No Patient was selected",Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case "InTake":{
                        if(!Patientid.equals("NONE")) {
                            mdbassign.open();
                            mdbassign.createInstance(Patientid, mAction, intaketype, message, "0", datetime);
                            mdbassign.close();
                            Toast.makeText(getContext(),"Assignement Assigned!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getContext(),"No Patient was selected",Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    default:{
                        Toast.makeText(getContext(),"No Action was selected",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        spname = (Spinner) view.findViewById(R.id.spinnername);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapternames = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,patientnames);
        // Specify the layout to use when the list of choices appears
        adapternames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spname.setAdapter(adapternames);

        spname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String patient = parent.getItemAtPosition(position).toString();
                int index = patientnames.indexOf(patient);
                Patientid = patientids.get(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //medication=spinner.getItemAtPosition(0).toString();
                Patientid = "0";
            }


        });


        //action spinner
        spaction = (Spinner) view.findViewById(R.id.spinneraction);
        sptype = (Spinner) view.findViewById(R.id.spinnertype);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapteractions = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item,actions);
        // Specify the layout to use when the list of choices appears
        adapteractions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spaction.setAdapter(adapteractions);

        spaction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String action = parent.getItemAtPosition(position).toString();
                mAction = action;
                switch(action){
                    case "Measure":{

                        ArrayAdapter<CharSequence> adaptertypes1 = new ArrayAdapter(getActivity(),
                                android.R.layout.simple_spinner_item,new String[]{"Heart Beat","Blood Pressure",
                                "Temperature","Stress Level", "Mood","Steps"});
                        sptype.setAdapter(adaptertypes1);
                        sptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String type = parent.getItemAtPosition(position).toString();
                                measuretype = type;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    }
                    case "Take Pills":{
                        invisiblerow.setVisibility(View.VISIBLE);
                        break;
                    }
                    case "InTake":{
                        ArrayAdapter<CharSequence> adaptertypes2 = new ArrayAdapter(getActivity(),
                                android.R.layout.simple_spinner_item,new String[]{"Food","Water",
                                });
                        sptype.setAdapter(adaptertypes2);
                        sptype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String type = parent.getItemAtPosition(position).toString();
                                intaketype = type;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //medication=spinner.getItemAtPosition(0).toString();
                Patientid = "0";
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
        if (context instanceof AssignTasks.OnFragmentInteractionListener) {
            mListener = (AssignTasks.OnFragmentInteractionListener) context;
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