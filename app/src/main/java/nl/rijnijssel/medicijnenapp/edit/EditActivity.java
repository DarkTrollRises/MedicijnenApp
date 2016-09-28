package nl.rijnijssel.medicijnenapp.edit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.R;
import nl.rijnijssel.medicijnenapp.alarm.Alarm;
import nl.rijnijssel.medicijnenapp.fragments.DatePickerFragment;
import nl.rijnijssel.medicijnenapp.fragments.TimePickerFragment;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;
import nl.rijnijssel.medicijnenapp.mysql.MysqlGetMedicineInfoTask;

/**
 * Created by Rik on 19-11-2015.
 */
public class EditActivity extends AppCompatActivity
{
    protected Medicine medicine, currentItem;
    protected DayEnum[] days;
    protected ArrayList<Integer> selectedDays;
    protected int amount, selectedAmount, hour, selectedHour, minute, selectedMinute, sSelectedDay, sSelectedMonth, sSelectedYear,
            eSelectedDay, eSelectedMonth, eSelectedYear, startButtonId;
    protected GregorianCalendar startDate, endDate;
    private int contentView, buttonId;
    protected Context context;
    protected Button buttonChoose, buttonRetry, buttonAmount, buttonDays, buttonTime, buttonStartDate, buttonEndDate, buttonResetEndDate, buttonSubmit;
    protected Dialog medicineDialog;
    protected ProgressBar progressBar;
    protected NumberPicker np;
    protected boolean tryAgain = false;

    /**
     * Method to call when the activity is being created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(contentView);

        buttonChoose = ((Button) findViewById(buttonId));
        selectedHour = -1;
        selectedMinute = -1;
        sSelectedDay = -1;
        sSelectedMonth = -1;
        sSelectedYear = -1;
        eSelectedDay = -1;
        eSelectedMonth = -1;
        eSelectedYear = -1;

        medicineDialog = new Dialog(context);
        medicineDialog.setContentView(R.layout.medicine_dialog_layout);
        medicineDialog.setTitle(getString(R.string.change_choose_title));
        medicineDialog.setCanceledOnTouchOutside(false);
        progressBar = ((ProgressBar) medicineDialog.findViewById(R.id.progressBar));
        buttonRetry = ((Button) medicineDialog.findViewById(R.id.buttonTryAgain));

        buttonChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                buttonRetry.setVisibility(Button.VISIBLE);

                buttonRetry.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        MysqlGetMedicineInfoTask task = new MysqlGetMedicineInfoTask(context);
                        task.execute("medicationId,medicationName,unit", "all");
                        tryAgain = true;
                    }
                });

                Button buttonCancel = ((Button) medicineDialog.findViewById(R.id.buttonCancel));
                buttonCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        medicineDialog.dismiss();
                        tryAgain = false;
                    }
                });

                medicineDialog.show();
            }
        });

        MysqlGetMedicineInfoTask task = new MysqlGetMedicineInfoTask(this);
        task.execute("medicationId,medicationName,unit", "all");
    }

    /**
     * Set properties for current activity
     *
     * @param contentView content view of the activity
     * @param context context from which the activity is started
     * @param buttonId id of the button from which this activity is started
     */
    protected void init(int contentView, Context context, int buttonId)
    {
        this.contentView = contentView;
        this.context = context;
        this.buttonId = buttonId;
    }

    /**
     * Set the medicine to be displayed
     *
     * @param list the list of medicine to display
     */
    protected void setMedicineDialog(ArrayList<Medicine> list)
    {
        progressBar.setVisibility(ProgressBar.GONE);
        buttonRetry.setVisibility(Button.GONE);

        ListView listView = (ListView) medicineDialog.findViewById(R.id.lvMedicine);
        final ArrayAdapter<Medicine> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        EditText etFilter = (EditText) medicineDialog.findViewById(R.id.etFilter);
        etFilter.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                currentItem = ((Medicine) parent.getItemAtPosition(position));
                buttonChoose.setText(currentItem.getName());
                medicine = currentItem;
                checkInputs();
                medicineDialog.dismiss();
            }
        });

        Button buttonCancel = ((Button) medicineDialog.findViewById(R.id.buttonCancel));
        buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                medicineDialog.dismiss();
            }
        });
    }

    /**
     * Set the medicine to be displayed
     *
     * @param list the list of medicine to display
     */
    public void setNameAdapter(final ArrayList<Medicine> list)
    {
        buttonChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setMedicineDialog(list);
                medicineDialog.show();
            }
        });

        if(tryAgain)
        {
            medicineDialog.dismiss();
            setMedicineDialog(list);
            medicineDialog.show();
        }
    }

    /**
     * Set the time to the selected time
     *
     * @param hours the selected hour
     * @param minutes the selected minute
     * @param button the button to change the text in
     */
    public void setTime(int hours, int minutes, Button button)
    {
        button.setText(String.format("%02d:%02d", hours, minutes));
        this.hour = hours;
        this.minute = minutes;
        this.selectedHour = hours;
        this.selectedMinute = minute;
        checkInputs();
    }

    /**
     * Set the time to the selected time
     *
     * @param day the selected day
     * @param month the selected month
     * @param year the selected year
     * @param button the button to change the text in
     */
    public void setDate(int day, int month, int year, Button button)
    {
        button.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
        if(button.getId() == startButtonId)
        {
            startDate = new GregorianCalendar(year, month, day);
            this.sSelectedDay = day;
            this.sSelectedMonth = month;
            this.sSelectedYear = year;
        }
        else
        {
            endDate = new GregorianCalendar(year, month, day);
            this.eSelectedDay = day;
            this.eSelectedMonth = month;
            this.eSelectedYear = year;
            buttonResetEndDate.setEnabled(true);
        }
        checkInputs();
    }

    /**
     * Set the properties for the selector
     *
     * @param buttonId the id of the button which shows the selector
     */
    protected void setAmountPick(int buttonId)
    {
        buttonAmount = ((Button) findViewById(buttonId));
        buttonAmount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.amount_dialog_layout);
                dialog.setTitle(getString(R.string.change_choose_title));
                dialog.setCanceledOnTouchOutside(false);

                np = ((NumberPicker) dialog.findViewById(R.id.amountPicker));
                np.setMinValue(1);
                np.setMaxValue(100);
                if(selectedAmount != 0)
                    np.setValue(selectedAmount);
                np.setWrapSelectorWheel(false);

                Button buttonOk = ((Button) dialog.findViewById(R.id.buttonOk));
                buttonOk.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        buttonAmount.setText(String.valueOf(np.getValue()));
                        amount = np.getValue();
                        selectedAmount = amount;
                        checkInputs();
                        dialog.dismiss();
                    }
                });

                Button buttonCancel = ((Button) dialog.findViewById(R.id.buttonCancel));
                buttonCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    /**
     * Set the properties for the selector
     *
     * @param buttonId the id of the button which shows the selector
     */
    protected void setDaysPick(int buttonId)
    {
        buttonDays = ((Button) findViewById(buttonId));
        buttonDays.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.days_dialog_layout);
                dialog.setTitle(context.getString(R.string.change_choose_title));
                dialog.setCanceledOnTouchOutside(false);

                final CheckBox[] cbArray = new CheckBox[7];
                cbArray[0] = ((CheckBox) dialog.findViewById(R.id.cbSunday));
                cbArray[1] = ((CheckBox) dialog.findViewById(R.id.cbMonday));
                cbArray[2] = ((CheckBox) dialog.findViewById(R.id.cbTuesday));
                cbArray[3] = ((CheckBox) dialog.findViewById(R.id.cbWednesday));
                cbArray[4] = ((CheckBox) dialog.findViewById(R.id.cbThursday));
                cbArray[5] = ((CheckBox) dialog.findViewById(R.id.cbFriday));
                cbArray[6] = ((CheckBox) dialog.findViewById(R.id.cbSaturday));
                if(selectedDays != null)
                    for(int day:selectedDays)
                        cbArray[day].setChecked(true);
                selectedDays = new ArrayList<>();

                Button buttonOk = ((Button) dialog.findViewById(R.id.buttonOk));
                buttonOk.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        for(int i = 1; i <= cbArray.length; i++)
                        {
                            if(i != 7)
                            {
                                if(cbArray[i].isChecked())
                                    selectedDays.add(i);
                            }
                            else
                            {
                                if(cbArray[0].isChecked())
                                    selectedDays.add(0);
                            }
                        }
                        days = new DayEnum[selectedDays.size()];
                        for(int i = 0; i < days.length; i++)
                            days[i] = Day.getEnumDayFromNormal(selectedDays.get(i));

                        StringBuilder dayString = new StringBuilder();
                        if(days.length != 0)
                        {
                            int i = 0;
                            for(DayEnum day : days)
                            {
                                if(i > 0)
                                    dayString.append(" ");
                                dayString.append(Day.getShortStringFromEnum(day));
                                i++;
                            }
                        }
                        else
                            dayString.append(getString(R.string.change_choose_date));
                        buttonDays.setText(dayString.toString());
                        checkInputs();
                        dialog.dismiss();
                    }
                });

                Button buttonCancel = ((Button) dialog.findViewById(R.id.buttonCancel));
                buttonCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    /**
     * Set the properties for the time selector
     *
     * @param buttonId the id of the button which shows the time selector
     *
     * @see EditActivity
     */
    protected void setTimePick(int buttonId)
    {
        buttonTime = ((Button) findViewById(buttonId));
        buttonTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerFragment fragment = new TimePickerFragment();
                fragment.setButton(buttonTime);
                if(selectedHour != -1 && selectedMinute != -1)
                    fragment.setTime(selectedHour, selectedMinute);
                fragment.show(getSupportFragmentManager(), "TimePicker");
            }
        });
    }

    /**
     * Set the properties for the date selectors
     *
     * @param startButtonId the id of the button which shows the start date selector
     * @param endButtonId the id of the button which shows the end date selector
     * @param resetEndButtonId the id of the button which resets the end date
     *
     * @see EditActivity
     */
    protected void setDatePick(int startButtonId, int endButtonId, int resetEndButtonId)
    {
        this.startButtonId = startButtonId;

        buttonStartDate = ((Button) findViewById(startButtonId));
        buttonStartDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setButton(((Button) v));
                if(sSelectedDay != -1 && sSelectedMonth != -1 && sSelectedYear != -1)
                    fragment.setDate(sSelectedDay, sSelectedMonth, sSelectedYear);
                fragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        buttonEndDate = ((Button) findViewById(endButtonId));
        buttonEndDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerFragment fragment = new DatePickerFragment();
                fragment.setButton(((Button) v));
                if(eSelectedDay != -1 && eSelectedMonth != -1 && eSelectedYear != -1)
                    fragment.setDate(eSelectedDay, eSelectedMonth, eSelectedYear);
                fragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        buttonResetEndDate = ((Button) findViewById(resetEndButtonId));
        buttonResetEndDate.setEnabled(false);
        buttonResetEndDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonEndDate.setText(getString(R.string.change_choose_date));
                endDate = null;
                eSelectedDay = -1;
                eSelectedMonth = -1;
                eSelectedYear = -1;
                buttonResetEndDate.setEnabled(false);
                checkInputs();
            }
        });
    }

    /**
     * Check if all needed inputs are filled in
     */
    private void checkInputs()
    {
        if(selectedHour != -1 && selectedMinute != -1 && sSelectedDay != -1 && sSelectedMonth != -1 && sSelectedYear != -1 && selectedDays != null && !selectedDays.isEmpty())
        {
            buttonSubmit.setBackground(getResources().getDrawable(R.drawable.button_add_change));
            buttonSubmit.setEnabled(true);
        }
        else
        {
            buttonSubmit.setBackground(getResources().getDrawable(R.drawable.button_add_change_dim));
            buttonSubmit.setEnabled(false);
        }
    }

    /**
     * Set the listener for the submit button
     *
     * @param submitButtonId the id of the button which submits the change
     */
    protected void setSubmit(int submitButtonId)
    {
        this.buttonSubmit = ((Button) findViewById(submitButtonId));
        this.buttonSubmit.setEnabled(false);
    }

    /**
     * Returns all information to main activity to be processed
     *
     * @param alarm alarm which was edited
     */
    public void returnIntent(Alarm alarm)
    {
        Intent returnIntent = new Intent(this, EditActivity.class);
        returnIntent.putExtra("EXTRA_ALARM", alarm);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
