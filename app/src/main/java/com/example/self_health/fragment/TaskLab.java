package com.example.self_health.fragment;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by pc on 11/20/2016.
 */

public class TaskLab {
    private static TaskLab sCrimeLab;
    private List<MyTask> mTaskes;

    public static TaskLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new TaskLab(context);
        }
        return sCrimeLab;
    }
    private TaskLab(Context context) {
        mTaskes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            MyTask crime = new MyTask();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); // Every other one
            mTaskes.add(crime);
        }

    }
    public List<MyTask> getCrimes() {
        return mTaskes;
    }
    public MyTask getCrime(UUID id) {
        for (MyTask task : mTaskes) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }
}
