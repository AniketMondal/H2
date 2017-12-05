package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


/**
 * Created by bhavesh on 29/09/17.
 */

public class BreakfastAlarmMaker {

    public static void makeAlarm(Context context) {

        Calendar breakfastTime = Calendar.getInstance();
        breakfastTime.set(Calendar.HOUR_OF_DAY,7);
        breakfastTime.set(Calendar.MINUTE,30);
        breakfastTime.set(Calendar.SECOND,0);
        if (Calendar.getInstance().after(breakfastTime))
            breakfastTime.add(Calendar.DATE, 1);

        Intent alarmIntent = new Intent(context, BreakfastAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, breakfastTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
