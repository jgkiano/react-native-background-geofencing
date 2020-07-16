package me.kiano.models;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.List;

public class RNHelper {

    public static void startService(Intent intent, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !isAppOnForeground(context)) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
