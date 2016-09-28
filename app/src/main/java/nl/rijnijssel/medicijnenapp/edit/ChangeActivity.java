package nl.rijnijssel.medicijnenapp.edit;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGetMedicineInfoTask;
import nl.rijnijssel.medicijnenapp.mysql.MysqlUpdateUseTask;

/**
 * Created by Rik on 10-11-2015.
 */
public class ChangeActivity extends EditActivity
{
    private Alarm alarm;
    private boolean first;

    /**
     * Method to call when the activity is being created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.init(R.layout.activity_change, this, R.id.cButtonChoose);
        super.onCreate(savedInstanceState);

        alarm = getIntent().getParcelableExtra("EXTRA_ALARM");
        first = true;

        setSubmit(R.id.buttonSubmitChange);
        setAmountPick(R.id.cButtonAmount);
        setDaysPick(R.id.cButtonDays);
        setTimePick(R.id.cButtonTime);
        setDatePick(R.id.cButtonStartDate, R.id.cButtonEndDate, R.id.cButtonReset);

        MysqlGetMedicineInfoTask task = new MysqlGetMedicineInfoTask(context);
        task.execute("medicationId,medicationName,unit", "all");
    }

    /**
     * Set the medicine to be displayed
     *
     * @param list the list of medicine to display
     *
     * @see EditActivity
     */
    @Override
    public void setNameAdapter(final ArrayList<Medicine> list)
    {
        setMedicineDialog(list);
        first = false;
        super.setNameAdapter(list);
    }

    /**
     * Set the medicine to be displayed
     *
     * @param list the list of medicine to display
     *
     * @see EditActivity
     */
    @Override
    protected void setMedicineDialog(ArrayList<Medicine> list)
    {
        super.setMedicineDialog(list);
        if(first)
        {
            currentItem = alarm.getMedicine();
            first = false;
        }
        buttonChoose.setText(currentItem.getName());
    }

    /**
     * Set the properties for the selector
     *
     * @param amountButtonId the id of the button which shows the selector
     *
     * @see EditActivity
     */
    @Override
    protected void setAmountPick(int amountButtonId)
    {
        super.setAmountPick(amountButtonId);
        amount = alarm.getAmount();
        selectedAmount = amount;
        buttonAmount.setText(String.valueOf(amount));
    }

    /**
     * Set the properties for the selector
     *
     * @param daysButtonId the id of the button which shows the selector
     *
     * @see EditActivity
     */
    @Override
    protected void setDaysPick(int daysButtonId)
    {
        super.setDaysPick(daysButtonId);
        days = alarm.getDaysEnum();
        selectedDays = new ArrayList<>();
        for(DayEnum day:days)
            selectedDays.add(Day.getNormalDay(day));

        StringBuilder dayString = new StringBuilder();
        int i = 0;
        for(DayEnum day : days)
        {
            if(i > 0)
                dayString.append(" ");
            dayString.append(Day.getShortStringFromEnum(day));
            i++;
        }
        buttonDays.setText(dayString.toString());
    }

    /**
     * Set the properties for the time selector
     *
     * @param timeButtonId the id of the button which shows the time selector
     *
     * @see EditActivity
     */
    @Override
    protected void setTimePick(int timeButtonId)
    {
        super.setTimePick(timeButtonId);
        selectedHour = alarm.getHour();
        selectedMinute = alarm.getMinute();
        setTime(alarm.getHour(), alarm.getMinute(), buttonTime);
    }

    /**
     * Set the properties for the date selectors
     *
     * @param startDateButtonId the id of the button which shows the start date selector
     * @param endDateButtonId the id of the button which shows the end date selector
     * @param resetEndDateButtonId the id of the button which resets the end date
     *
     * @see EditActivity
     */
    @Override
    protected void setDatePick(int startDateButtonId, int endDateButtonId, int resetEndDateButtonId)
    {
        super.setDatePick(startDateButtonId, endDateButtonId, resetEndDateButtonId);
        String[] startDateArray = alarm.getStartDate().split("-");
        String[] endDateArray = alarm.getEndDate().split("-");

        sSelectedDay = Integer.parseInt(startDateArray[2]);
        sSelectedMonth = Integer.parseInt(startDateArray[1])-1;
        sSelectedYear = Integer.parseInt(startDateArray[0]);

        setDate(Integer.parseInt(startDateArray[2]), Integer.parseInt(startDateArray[1])-1, Integer.parseInt(startDateArray[0]), buttonStartDate);

        if(!endDateArray[0].equals("1970"))
        {
            eSelectedDay = Integer.parseInt(endDateArray[2]);
            eSelectedMonth = Integer.parseInt(endDateArray[1]) - 1;
            eSelectedYear = Integer.parseInt(endDateArray[0]);

            setDate(Integer.parseInt(endDateArray[2]), Integer.parseInt(endDateArray[1])-1, Integer.parseInt(endDateArray[0]), buttonEndDate);
        }
    }

    /**
     * Set the listener for the submit button
     *
     * @param submitButtonId the id of the button which submits the change
     *
     * @see EditActivity
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
                if(medicine != null)
                    alarm.setMedicine(medicine);
                else
                    alarm.setMedicine(currentItem);
                if(endDate == null)
                    endDate = new GregorianCalendar(1970, 0, 1);
                alarm.setAmount(amount);
                alarm.setDays(days);
                alarm.setTime(hour, minute);
                alarm.setStartDate(startDate);
                alarm.setEndDate(endDate);

                MysqlUpdateUseTask updateUseTask = new MysqlUpdateUseTask(context);
                updateUseTask.execute(alarm);
            }
        });
    }
}
