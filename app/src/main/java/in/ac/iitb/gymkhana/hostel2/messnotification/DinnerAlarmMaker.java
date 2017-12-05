package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by bhavesh on 29/09/17.
 */

public class DinnerAlarmMaker {

    public static void makeAlarm(Context context) {

        Calendar dinnerTime = Calendar.getInstance();
        dinnerTime.set(Calendar.HOUR_OF_DAY,19);
        dinnerTime.set(Calendar.MINUTE,30);
        dinnerTime.set(Calendar.SECOND,0);
        if (Calendar.getInstance().after(dinnerTime))
            dinnerTime.add(Calendar.DATE, 1);

        Intent alarmIntent = new Intent(context, DinnerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 101, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dinnerTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
