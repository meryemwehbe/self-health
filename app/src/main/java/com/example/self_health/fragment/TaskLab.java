package com.example.self_health.fragment;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by pc on 11/20/2016.
 */

public class TaskLab {
    private static TaskLab Lab;
    private List<MyTask> mTasks;

    public TaskLab(Context context, ArrayList<String> titles, ArrayList<String> dates,ArrayList<Boolean> done) {
        mTasks = new ArrayList<>();

        for (int i = 0; i < titles.size(); i++) {
            MyTask task = new MyTask();
            task.setTitle(titles.get(i));
            task.setDone(done.get(i)); // Every other one
            task.setDate(dates.get(i));
            mTasks.add(task);
        }

    }
    public List<MyTask> gettaskes() {
        return mTasks;
    }
    public MyTask getTask(UUID id) {
        for (MyTask task : mTasks) {
            //if (task.getId().equals(id)) {
              //  return task;
           // }
        }
        return null;
    }
}
