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
import java.util.ArrayList;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.AuthActivity;
import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.connection.BackgroundTask;
import nl.rijnijssel.medicijnenapp.edit.Day;
import nl.rijnijssel.medicijnenapp.edit.DayEnum;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;

/**
 * Created by Rik on 27-11-2015.
 */
public class MysqlGetUseTask extends BackgroundTask
{
    protected ArrayList<Alarm> list = new ArrayList<>();
    protected String useID;
    protected int selectedAlarmID;
    protected boolean setSelectedAlarm = false;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlGetUseTask(Context ctx)
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
        useID = params[0];
        String patientID = params[1];
        if(params.length > 2 && params[2] != null)
        {
            selectedAlarmID = Integer.parseInt(params[2]);
            setSelectedAlarm = true;
        }
        try
        {
            URL url = new URL(ctx.getString(R.string.use_get_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
            String line;
            while((line = bufferedReader.readLine()) != null)
                if(!line.equals(""))
                {
                    int id = Integer.parseInt(line);
                    Medicine medicine = new Medicine(Integer.parseInt(bufferedReader.readLine()));
                    int amount = Integer.parseInt(bufferedReader.readLine());

                    JSONArray jsonDays = new JSONArray(bufferedReader.readLine());
                    DayEnum[] days = new DayEnum[jsonDays.length()];
                    for(int i = 0; i < jsonDays.length(); i++)
                        days[i] = Day.getEnumDayFromNormal(Integer.parseInt(jsonDays.getString(i)));

                    String[] timeArray = bufferedReader.readLine().split(":");
                    int hour = Integer.parseInt(timeArray[0]);
                    int minute = Integer.parseInt(timeArray[1]);

                    String[] startDateArray = bufferedReader.readLine().split("-");
                    GregorianCalendar startDate = new GregorianCalendar(Integer.parseInt(startDateArray[0]), Integer.parseInt(startDateArray[1])-1,
                            Integer.parseInt(startDateArray[2]));
                    String[] endDateArray = bufferedReader.readLine().split("-");
                    GregorianCalendar endDate = new GregorianCalendar(Integer.parseInt(endDateArray[0]), Integer.parseInt(endDateArray[1])-1,
                            Integer.parseInt(endDateArray[2]));

                    list.add(new Alarm(id, ctx, medicine, amount, days, hour, minute, startDate, endDate));
                }
            bufferedReader.close();
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
        if (result.equals(ctx.getString(R.string.get_success)))
        {
            if(list.size() > 0)
                for(int i = 0; i < list.size(); i++)
                {
                    Alarm alarm = list.get(i);
                    Alarm.addAlarm(alarm);
                    MysqlGetMedicineInfoFromIdTask getMedicineInfoFromIdTask = new MysqlGetMedicineInfoFromIdTask(ctx);
                    if(setSelectedAlarm)
                        getMedicineInfoFromIdTask.execute("medicationId,medicationName,unit", String.valueOf(alarm.getMedicine().getId()), String.valueOf(i),
                                String.valueOf(selectedAlarmID));
                    else
                        getMedicineInfoFromIdTask.execute("medicationId,medicationName,unit", String.valueOf(alarm.getMedicine().getId()), String.valueOf(i));
                }
            else
                ((AuthActivity) ctx).setAlarmMedicine(0, null, 0);
        }
        else
        {
        }
    }
}
