package in.ac.iitb.gymkhana.hostel2.pushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;

import in.ac.iitb.gymkhana.hostel2.CacheManager;
import in.ac.iitb.gymkhana.hostel2.R;
import in.ac.iitb.gymkhana.hostel2.WelcomeActivity;
import in.ac.iitb.gymkhana.hostel2.notificationsactivity.NotificationsActivity;

/**
 * Created by bhavesh on 30/09/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FCM Service", "From: " + remoteMessage.getFrom());
        Log.e("FCM Service", "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Intent repeatingIntent = new Intent(getApplicationContext(), NotificationsActivity.class);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 104, repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String title;
        if(remoteMessage.getNotification().getTitle() != null)
            title = remoteMessage.getNotification().getTitle();
        else
            title = "Hostel 2";
        String message = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.icon));
        builder.setContentTitle(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        builder.setContentText(message);
        builder.setAutoCancel(true);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(104, builder.build());

        String[] now = Calendar.getInstance().getTime().toString().split(" ");
        String time = now[1] + " " + now[2] + " " + now[3];
        CacheManager cache = new CacheManager(getApplicationContext());
        cache.addNotification(title + "~~" + message + "\n\n" + time);

    }

}