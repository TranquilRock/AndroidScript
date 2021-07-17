package com.example.androidscript.util;

import android.content.Context;

import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.cast.CastDevice;

public abstract class CastService extends CastRemoteDisplayLocalService {
    public void onCreate(){
        String applicationId = "com.example.androidscript";
        Context context = getApplicationContext();
        CastDevice device ;
        CastRemoteDisplayLocalService.NotificationSettings notificationSettings;
        CastRemoteDisplayLocalService.Callbacks callbacks;
        //startService(context, this , applicationId , device , notificationSettings , callbacks);
    }
}
