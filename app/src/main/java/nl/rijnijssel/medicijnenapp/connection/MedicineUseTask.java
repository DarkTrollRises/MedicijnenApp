package nl.rijnijssel.medicijnenapp.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;

/**
 * Created by Rik on 27-11-2015.
 */
public class MedicineUseTask extends AsyncTask<Alarm, Void, String>
{
    protected Context ctx;
    protected String base_url;

    /**
     * Default constructor
     *
     * @param ctx the context of the startingpoing
     */
    public MedicineUseTask(Context ctx)
    {
        this.ctx = ctx;
        base_url = ctx.getString(R.string.base_url);
    }

    /**
     * What happens before the task is executed
     *
     * @see AsyncTask
     */
    @Override
    protected void onPreExecute()
    {
    }

    /**
     * @param params the parameters to be used by the task
     * @return a result which is used to determine if the task was successful
     *
     * @see AsyncTask
     */
    @Override
    protected String doInBackground(Alarm... params) {
        return null;
    }
}
