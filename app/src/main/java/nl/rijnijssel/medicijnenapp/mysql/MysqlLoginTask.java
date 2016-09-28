package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.connection.BackgroundTask;
import nl.rijnijssel.medicijnenapp.login.LoginActivity;

/**
 * Created by Rik on 8-12-2015.
 */
public class MysqlLoginTask extends BackgroundTask
{
    private int patientID;
    private String patientName;
    private boolean valid;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlLoginTask(Context ctx)
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
        String username = params[0];
        String password = params[1];
        try
        {
            URL url = new URL(ctx.getString(R.string.login_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data =
                    URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                if(!line.equals(""))
                {
                    if(!line.equals("invalid"))
                    {
                        valid = true;
                        patientID = Integer.parseInt(line);
                        patientName = bufferedReader.readLine();
                    }
                    else
                        valid = false;
                }
            }
            in.close();
            httpURLConnection.disconnect();
            return ctx.getString(R.string.get_success);
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
        if (result.equals(ctx.getString(R.string.get_success)))
        {
            if(valid)
                ((LoginActivity) ctx).login(patientID, patientName);
            else
                ((LoginActivity) ctx).login();
        }
        else
        {
        }
    }
}
