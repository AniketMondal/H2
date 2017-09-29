package in.ac.iitb.gymkhana.hostel2.messnotification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by bhavesh on 29/09/17.
 */

public class AlarmBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            BreakfastAlarmMaker.makeAlarm(context);
            LunchAlarmMaker.makeAlarm(context);
            TiffinAlarmMaker.makeAlarm(context);
            DinnerAlarmMaker.makeAlarm(context);
            ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

}
