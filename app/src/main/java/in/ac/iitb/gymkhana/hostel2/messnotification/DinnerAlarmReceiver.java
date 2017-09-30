package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.WelcomeActivity;

import static in.ac.iitb.gymkhana.hostel2.WelcomeActivity.messMenu;

/**
 * Created by bhavesh on 29/09/17.
 */

public class DinnerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent repeatingIntent = new Intent(context, WelcomeActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String menu = null;
        try {
            JSONObject jsonObject = new JSONObject(messMenu);
            JSONArray jsonArray = jsonObject.getJSONArray("DAY");
            Calendar now = Calendar.getInstance();
            int day = now.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case Calendar.MONDAY:
                    menu = jsonArray.getJSONObject(0).getString("DINNER").trim();
                    break;
                case Calendar.TUESDAY:
                    menu = jsonArray.getJSONObject(1).getString("DINNER").trim();
                    break;
                case Calendar.WEDNESDAY:
                    menu = jsonArray.getJSONObject(2).getString("DINNER").trim();
                    break;
                case Calendar.THURSDAY:
                    menu = jsonArray.getJSONObject(3).getString("DINNER").trim();
                    break;
                case Calendar.FRIDAY:
                    menu = jsonArray.getJSONObject(4).getString("DINNER").trim();
                    break;
                case Calendar.SATURDAY:
                    menu = jsonArray.getJSONObject(5).getString("DINNER").trim();
                    break;
                case Calendar.SUNDAY:
                    menu = jsonArray.getJSONObject(6).getString("DINNER").trim();
                    break;
            }
        } catch (Exception e) {}

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo));
        builder.setContentTitle("Today's Dinner");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(menu));
        builder.setContentText(menu);
        builder.setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("mess", true))
            notificationManager.notify(101, builder.build());


    }
}