package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by bhavesh on 29/09/17.
 */

public class TiffinAlarmMaker {

    public static void makeAlarm(Context context) {

        Calendar tiffinTime = Calendar.getInstance();
        tiffinTime.set(Calendar.HOUR_OF_DAY,16);
        tiffinTime.set(Calendar.MINUTE,30);
        tiffinTime.set(Calendar.SECOND,0);

        Intent alarmIntent = new Intent(context, TiffinAlarmMaker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 103, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, tiffinTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
