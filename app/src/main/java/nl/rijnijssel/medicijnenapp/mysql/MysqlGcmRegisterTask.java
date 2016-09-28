package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.connection.BackgroundTask;

/**
 * Created by Rik on 3-11-2015.
 */
public class MysqlGcmRegisterTask extends BackgroundTask
{
    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlGcmRegisterTask(Context ctx)
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
        String patientId = params[0];
        String token = params[1];
        try
        {
            URL url = new URL(ctx.getString(R.string.register_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data =
                    URLEncoder.encode("patientId", "UTF-8") + "=" + URLEncoder.encode(patientId, "UTF-8") + "&" +
                    URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            in.close();
            httpURLConnection.disconnect();
            return ctx.getString(R.string.register_success);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
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
        if (result.equals(ctx.getString(R.string.register_success)))
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        else
        {
        }
    }
}
