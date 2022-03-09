package com.example.android.sunshine.sync;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class SyncViewModel extends AndroidViewModel
{

    public SyncViewModel(@NonNull Application application) {
        super(application);
    }
    public static void syncWeather(Context context)
    {
        Log.i("SyncViewModel"+"###","Inside syncWeather method and calling SunshineSyncUtils.startImmediateSync(context);");
        SunshineSyncUtils.startImmediateSync(context);
    }
}
