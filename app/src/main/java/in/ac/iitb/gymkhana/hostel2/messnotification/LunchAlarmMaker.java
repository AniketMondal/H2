package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by bhavesh on 29/09/17.
 */

public class LunchAlarmMaker {

    public static void makeAlarm(Context context) {

        Calendar lunchTime = Calendar.getInstance();
        lunchTime.set(Calendar.HOUR_OF_DAY,12);
        lunchTime.set(Calendar.MINUTE,30);
        lunchTime.set(Calendar.SECOND,0);

        Intent alarmIntent = new Intent(context, LunchAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 102, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, lunchTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}