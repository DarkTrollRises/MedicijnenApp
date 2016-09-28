package nl.rijnijssel.medicijnenapp.alarm;

import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Rik on 23-11-2015.
 */
public class RequestIntent
{
    /**
     * List of request codes that are currently being used
     */
    public static ArrayList<RequestIntent> list = new ArrayList<>();

    private Intent intent;
    private int requestCode;

    /**
     * Default constructor
     *
     * @param intent intent to go with the alarm
     * @param requestCode unique request code of the alarm
     */
    public RequestIntent(Intent intent, int requestCode)
    {
        this.intent = intent;
        this.requestCode = requestCode;
    }

    /**
     * Get the intent to go with the alarm
     *
     * @return the intent to go with the alarm
     */
    public Intent getIntent()
    {
        return intent;
    }

    /**
     * Set the intent to go with the alarm
     *
     * @param intent the intent to go with the alarm
     */
    public void setIntent(Intent intent)
    {
        this.intent = intent;
    }

    /**
     * Get the unique request code of the alarm
     *
     * @return the unique request code of the alarm
     */
    public int getRequestCode()
    {
        return requestCode;
    }
}
