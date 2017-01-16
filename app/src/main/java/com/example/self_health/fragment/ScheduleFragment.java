package com.example.self_health.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.self_health.R;
import com.example.self_health.other.DataBaseHelperAssignTasks;
import com.example.self_health.other.DataBaseHelperRelations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView mTaskRecyclerView;
    private  TaskAdapter mAdapter;
    private DataBaseHelperAssignTasks mdbassign;
    private String myid;
    private Calendar myCalendar;

    private ArrayList<Boolean> done =  new ArrayList<Boolean>();
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
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

        mdbassign = new DataBaseHelperAssignTasks(getContext());

        myid = getArguments().getString("ID");
        myCalendar = Calendar.getInstance();

        mdbassign = new DataBaseHelperAssignTasks(getContext());
        mdbassign.open();
        //mdbassign.recreatetable();

        myCalendar.add(Calendar.WEEK_OF_YEAR ,- 1);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datetime = dateTimeFormat.format(myCalendar.getTime());
        Cursor cursor = mdbassign.getTasks(myid,datetime);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String task = cursor.getString(1) + " "+ cursor.getString(2)+ " "+cursor.getString(3) ;
            titles.add(task);
            String y = cursor.getString(4);
            if (y.equals("0")){
                done.add(false);
            }else if(y.equals("1")){
                done.add(true);
            }
            String p = cursor.getString(5);
            dates.add(p);




            cursor.moveToNext();

        }
        mdbassign.close();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_schedule, container, false);

        mTaskRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return  view;
    }
    private void updateUI() {
        TaskLab taskLab = new TaskLab(getContext(),titles,dates,done);
        List<MyTask> tasks = taskLab.gettaskes();
        mAdapter = new TaskAdapter(tasks);
        mTaskRecyclerView.setAdapter(mAdapter);
    }
    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitleTextView;
        private  TextView mDateTextView;
        private CheckBox mSolvedTask;
        private MyTask mtask;

        public TaskHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_task_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_task_date_view);
            mSolvedTask = (CheckBox)
                    itemView.findViewById(R.id.list_item_task_solved_check_box);
        }
        @Override
        public void onClick(View v) {
                //Ask user if task is done
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getContext());

                // set title
                alertDialogBuilder.setTitle("Are you sure you have completed: " + mtask.getTitle());

                // set dialog message
                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            String title = mtask.getTitle();
                            String[] stuff = title.split(" ");
                            String action = stuff[0];
                            String date = mtask.getDate();
                            mdbassign.open();
                            int status = mdbassign.assignTaskDone(myid,action,date);
                            mdbassign.close();
                            if (status != -1){
                                Toast.makeText(getContext(),"Sucessfully removed",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
        }

        public void bindTask(MyTask task) {
            mtask = task;
            mTitleTextView.setText(mtask.getTitle());
            mDateTextView.setText(mtask.getDate().toString());
            mSolvedTask.setChecked(mtask.isDone());
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<MyTask> mTask;
        public TaskAdapter(List<MyTask> taskes) {
            mTask = taskes;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_task, parent, false);
            return new TaskHolder(view);
        }
        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            MyTask task = mTask.get(position);
            holder.bindTask(task);
        }
        @Override
        public int getItemCount() {
            return mTask.size();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
