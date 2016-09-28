package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.gcm.GcmListener;

/**
 * Created by Rik on 27-11-2015.
 */
public class MysqlGetUseFromIdTask extends MysqlGetUseTask
{
    private int useIndex;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlGetUseFromIdTask(Context ctx)
    {
        super(ctx);
    }

    /**
     * Called when the task is finished
     *
     * @param result the result returned by doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        if (result.equals(ctx.getString(R.string.get_success)))
        {
            Alarm alarm = list.get(0);
            ((GcmListener) ctx).setAlarmAtIndex(useIndex, alarm);
            MysqlGetMedicineInfoFromIdTask getMedicineInfoFromIdTask = new MysqlGetMedicineInfoFromIdTask(ctx);
            getMedicineInfoFromIdTask.setIfGetUse(true);
            getMedicineInfoFromIdTask.execute("medicationId,medicationName,unit", String.valueOf(alarm.getMedicine().getId()), String.valueOf(useIndex));
        }
        else
        {
        }
    }

    /**
     * Set the index to use
     *
     * @param index index to use
     */
    public void setIndex(int index)
    {
        this.useIndex = index;
    }
}
