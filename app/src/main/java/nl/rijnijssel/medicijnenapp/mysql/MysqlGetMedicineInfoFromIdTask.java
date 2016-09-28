package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;

import nl.rijnijssel.medicijnenapp.AuthActivity;
import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.gcm.GcmListener;

/**
 * Created by Rik on 30-11-2015.
 */
public class MysqlGetMedicineInfoFromIdTask extends MysqlGetMedicineInfoTask
{
    private boolean getUse = false;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlGetMedicineInfoFromIdTask(Context ctx)
    {
        super(ctx, "medicationId");
    }

    /**
     * Called when the task is finished
     *
     * @param result the result returned by doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        if(result.equals(ctx.getString(R.string.get_success)))
        {
            if(getUse)
                ((GcmListener) ctx).setMedicineAtIndex(Integer.parseInt(index), list.get(0));
            else
                ((AuthActivity) ctx).setAlarmMedicine(Integer.parseInt(index), list.get(0), selectedAlarmID);
        }
        else
        {
        }
    }

    /**
     * Set if this task is called from the getUseTask
     *
     * @param getUse true if this is called from getUseTask
     */
    public void setIfGetUse(boolean getUse)
    {
        this.getUse = getUse;
    }
}
