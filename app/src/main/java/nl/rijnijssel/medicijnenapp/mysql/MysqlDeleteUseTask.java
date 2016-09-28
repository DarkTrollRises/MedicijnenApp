package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import nl.rijnijssel.medicijnenapp.AuthActivity;
import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.connection.BackgroundTask;

/**
 * Created by Rik on 2-12-2015.
 */
public class MysqlDeleteUseTask extends BackgroundTask
{
    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlDeleteUseTask(Context ctx)
    {
        super(ctx);
    }

    /**
     * The main task
     *
     * @param params the parameters to be used by the task
     * @return response to determine if the task was successful
     */
    @Override
    protected String doInBackground(String... params)
    {
        String useID = params[0];
        String patientID = params[1];
        try
        {
            URL url = new URL(ctx.getString(R.string.use_delete_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data =
                    URLEncoder.encode("useID", "UTF-8") + "=" + URLEncoder.encode(useID, "UTF-8") + "&" +
                    URLEncoder.encode("patientID", "UTF-8") + "=" + URLEncoder.encode(patientID, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            in.close();
            httpURLConnection.disconnect();
            return ctx.getString(R.string.get_success);
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return ctx.getString(R.string.conn_failed);
    }

    /**
     * Called when the task is finished
     *
     * @param result the result returned by doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        if (result.equals(ctx.getString(R.string.get_success)))
            ((AuthActivity) ctx).setSelectedAlarm(null);
        else
        {
        }
    }
}
