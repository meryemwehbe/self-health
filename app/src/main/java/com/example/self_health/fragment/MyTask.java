package com.example.self_health.fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by pc on 11/20/2016.
 */

public class MyTask {
    private String mTitle;
    private String mDate;
    private boolean mDone;

    public MyTask() {

    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean mSolved) {
        this.mDone = mSolved;
    }
}
