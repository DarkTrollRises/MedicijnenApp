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
import java.util.ArrayList;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.connection.BackgroundTask;
import nl.rijnijssel.medicijnenapp.edit.EditActivity;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;

/**
 * Created by Rik on 10-11-2015.
 */
public class MysqlGetMedicineInfoTask extends BackgroundTask
{
    protected ArrayList<Medicine> list;
    protected String index;
    protected int selectedAlarmID = 0;
    private String filter;

    /**
     * Default constructor
     *
     * @param ctx the context from which the task was called
     */
    public MysqlGetMedicineInfoTask(Context ctx)
    {
        super(ctx);
        this.filter = "medicationName";
    }

    /**
     * Constructor with filter
     *
     * @param ctx the context from which the task was called
     * @param filter the filter with which to sort the result
     */
    public MysqlGetMedicineInfoTask(Context ctx, String filter)
    {
        super(ctx);
        this.filter = filter;
    }

    /**
     * Called before task starts
     */
    @Override
    protected void onPreExecute()
    {
        list = new ArrayList<>();
        super.onPreExecute();
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
        String column = params[0];
        String id = params[1];
        if(params.length > 2 && params[2] != null)
        {
            index = params[2];
            if(params.length > 3 && params[3] != null)
                selectedAlarmID = Integer.parseInt(params[3]);
        }
        try
        {
            URL url = new URL(ctx.getString(R.string.meds_url, base_url));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            String data =
                    URLEncoder.encode("column", "UTF-8") + "=" + URLEncoder.encode(column, "UTF-8") + "&" +
                    URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
                    URLEncoder.encode("filter", "UTF-8") + "=" + URLEncoder.encode(filter, "UTF-8");
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
                    Medicine med = new Medicine(Integer.parseInt(line));
                    if((line = bufferedReader.readLine()) != null)
                        med.setName(line);
                    if((line = bufferedReader.readLine()) != null)
                        med.setUnit(line);
                    list.add(med);
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
        return ctx.getString(R.string.conn_failed);
    }

    /**
     * Called when the task is finished
     *
     * @param result the result returned by doInBackground
     */
    @Override
    protected void onPostExecute(String result) {
        if(result.equals(ctx.getString(R.string.get_success)))
            ((EditActivity) ctx).setNameAdapter(list);
        else
        {
        }
    }
}
