package nl.rijnijssel.medicijnenapp.mysql;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

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
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.connection.MedicineUseTask;
import nl.rijnijssel.medicijnenapp.edit.EditActivity;

/**
 * Created by Rik on 26-11-2015.
 */
public class MysqlInsertUseTask extends MedicineUseTask
{
    private Alarm alarm;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlInsertUseTask(Context ctx)
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
    protected String doInBackground(Alarm... params)
    {
        alarm = params[0];
        int patientID = ctx.getSharedPreferences(ctx.getString(R.string.preference_name), Context.MODE_PRIVATE).getInt(ctx.getString(R.string.preference_patient_id), 0);
        int medicationID = alarm.getMedicine().getId();
        int amount = alarm.getAmount();
        String[] days = new String[alarm.getDays().length];
        for(int i = 0; i < alarm.getDays().length; i++)
            days[i] = String.valueOf(alarm.getDays()[i]);
        String time = alarm.getTime();
        String startDate = alarm.getStartDate();
        String endDate = alarm.getEndDate();
        try
        {
            URL url = new URL(ctx.getString(R.string.use_insert_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data =
                    URLEncoder.encode("patientID", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(patientID), "UTF-8") + "&" +
                    URLEncoder.encode("medicationID", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(medicationID), "UTF-8") + "&" +
                    URLEncoder.encode("amount", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(amount), "UTF-8") + "&" +
                    URLEncoder.encode("days", "UTF-8") + "=" + URLEncoder.encode(new JSONArray(days).toString(), "UTF-8") + "&" +
                    URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                    URLEncoder.encode("startDate", "UTF-8") + "=" + URLEncoder.encode(startDate, "UTF-8") + "&" +
                    URLEncoder.encode("endDate", "UTF-8") + "=" + URLEncoder.encode(endDate, "UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            out.close();

            InputStream in = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
            String line;
            while((line = bufferedReader.readLine()) != null)
                if(!line.equals(""))
                    alarm.setId(Integer.parseInt(line));
            bufferedReader.close();
            in.close();
            httpURLConnection.disconnect();
            return ctx.getString(R.string.ins_success);
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
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
        if (result.equals(ctx.getString(R.string.ins_success)))
            ((EditActivity) ctx).returnIntent(alarm);
        else
        {
        }
    }
}
