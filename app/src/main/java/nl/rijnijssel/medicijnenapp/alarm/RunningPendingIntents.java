package nl.rijnijssel.medicijnenapp.alarm;

import java.util.ArrayList;

/**
 * Created by Rik on 14-12-2015.
 */
public class RunningPendingIntents
{
    /**
     * The list of all PendingIntents of all current alarms
     *
     * @see android.app.PendingIntent
     * @see Alarm
     */
    public static ArrayList<Integer> list = new ArrayList<>();
}
