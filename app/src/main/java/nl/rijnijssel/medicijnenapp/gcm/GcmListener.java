package nl.rijnijssel.medicijnenapp.gcm;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGetUseFromIdTask;

/**
 * Created by Rik on 21-10-2015.
 */
public class GcmListener extends GcmListenerService
{
    private Alarm[] alarmList;
    private boolean[] doneList;

    /**
     * Called when the device receives a GCM message
     *
     * @param from sender
     * @param data data sent through GCM message
     */
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        try
        {
            Log.d("GCM", data.getString("message"));

            JSONArray jsonIds = new JSONArray(data.getString("message"));
            int[] useIds = new int[jsonIds.length()];
            alarmList = new Alarm[jsonIds.length()];
            doneList = new boolean[jsonIds.length()];
            for(int x = 0; x < doneList.length; x++)
                doneList[x] = false;
            for(int i = 0; i < jsonIds.length(); i++)
            {
                useIds[i] = Integer.parseInt(jsonIds.getString(i));
                MysqlGetUseFromIdTask getUseFromIdTask = new MysqlGetUseFromIdTask(this);
                getUseFromIdTask.setIndex(i);
                getUseFromIdTask.execute(String.valueOf(useIds[i]), String.valueOf(getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
                        .getInt(getString(R.string.preference_patient_id), 0)));
            }
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Set an alarm at a specific index in the list
     *
     * @param index index to set alarm at
     * @param alarm alarm to set
     */
    public void setAlarmAtIndex(int index, Alarm alarm)
    {
        alarmList[index] = alarm;
    }

    /**
     * Set a medicine at a specific index in the list
     *
     * @param index index to set medicine at
     * @param medicine medicine to set
     */
    public void setMedicineAtIndex(int index, Medicine medicine)
    {
        alarmList[index].setMedicine(medicine);
        doneList[index] = true;
        checkIfDone();
    }

    /**
     * Check if all the information is done setting itself
     */
    private void checkIfDone()
    {
        boolean done = true;
        GregorianCalendar cal = new GregorianCalendar();

        for(boolean b:doneList)
            if(!b)
            {
                done = false;
                break;
            }
        if(done)
            for(Alarm alarm:alarmList)
            {
                alarm.setTimeMillis(cal);
                alarm.scheduleAlarm();
            }
    }
}
