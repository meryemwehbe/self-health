package com.example.self_health.fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by pc on 11/20/2016.
 */

public class MyTask {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public MyTask() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }
}
