package nl.rijnijssel.medicijnenapp.connection;

import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import nl.rijnijssel.medicijnenapp.R;

/**
 * Created by Rik on 9-10-2015.
 */
public class BackgroundTask extends AsyncTask<String, Void, String>
{
    protected Context ctx;
    protected String base_url;

    /**
     * Default constructor
     *
     * @param ctx the context of the startingpoing
     */
    public BackgroundTask(Context ctx)
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
    protected String doInBackground(String... params) {
        return null;
    }
}
