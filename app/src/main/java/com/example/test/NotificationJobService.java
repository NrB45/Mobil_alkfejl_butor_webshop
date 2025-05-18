package com.example.test;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotificationJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new NotificationHandler(getApplicationContext()).send("Itt az idő vásárolni!");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
