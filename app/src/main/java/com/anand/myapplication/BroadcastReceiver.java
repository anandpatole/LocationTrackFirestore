package com.anand.myapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BroadcastReceiver extends android.content.BroadcastReceiver
{
    Context context1;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context1=context;
        Log.e("result","receiver");
        if (!isMyServiceRunning(LocationService.class)) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, LocationService.class));

            } else {
                context.startService(new Intent(context, LocationService.class));

            }
        }
       // context.startService(new Intent(context,Block_All_Notification.class));

    }
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context1.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }

        return false;
    }
}
