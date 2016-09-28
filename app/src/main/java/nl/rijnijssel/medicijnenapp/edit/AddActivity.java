package nl.rijnijssel.medicijnenapp.edit;

import android.os.Bundle;
import android.view.View;

import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.mysql.MysqlInsertUseTask;

/**
 * Created by Rik on 19-11-2015.
 */
public class AddActivity extends EditActivity
{
    /**
     * Method to call when the activity is being created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.init(R.layout.activity_add, this, R.id.aButtonChoose);
        super.onCreate(savedInstanceState);

        setAmountPick(R.id.aButtonAmount);
        setDaysPick(R.id.aButtonDays);
        setTimePick(R.id.aButtonTime);
        setDatePick(R.id.aButtonStartDate, R.id.aButtonEndDate, R.id.aButtonReset);
        setSubmit(R.id.buttonSubmitAdd);
    }

    /**
     * Set the listener for the submit button
     *
     * @param submitButtonId the id of the submit button
     */
    @Override
    protected void setSubmit(int submitButtonId)
    {
        super.setSubmit(submitButtonId);
        buttonSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(endDate == null)
                    endDate = new GregorianCalendar(1970, 0, 1);
                MysqlInsertUseTask insertUseTask = new MysqlInsertUseTask(context);
                insertUseTask.execute(new Alarm(context, medicine, amount, days, hour, minute, startDate, endDate));
            }
        });
    }
}
