package nl.rijnijssel.medicijnenapp.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rik on 14-10-2015.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    /**
     * Run when an alarm goes off
     * Sends the information of the alarm to the alarm screen to be executed
     *
     * @param context context from which the alarm is started
     * @param intent contains information given by the setter of the alarm
     *
     * @see Alarm
     * @see AlarmActivity
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtras(intent.getExtras());
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }
}
