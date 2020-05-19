package com.ivanbakach.endlesssample.endless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootUpReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
    if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //log("Starting the service in >=26 Mode from a BroadcastReceiver")
            context.startForegroundService(new Intent(context, EndlessService.class));
            return;
        }
        //log("Starting the service in < 26 Mode from a BroadcastReceiver")
        context.startService(new Intent(context, EndlessService.class));
    }

}
}
