package nl.rijnijssel.medicijnenapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

import nl.rijnijssel.medicijnenapp.edit.EditActivity;

/**
 * Created by Rik on 17-11-2015.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    private Button button;
    private int selectedDay, selectedMonth, selectedYear = -1;

    /**
     * Method to call when the dialog gets created
     *
     * @return the dialog to display
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        int day, month, year;
        if(selectedDay != -1 && selectedMonth != -1 && selectedYear != -1)
        {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
        }
        else
        {
            day = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Called when the user picks a date
     *
     * @param view view of date picker
     * @param year year picked
     * @param monthOfYear month picked
     * @param dayOfMonth day picked
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
    {
        ((EditActivity) getActivity()).setDate(dayOfMonth, monthOfYear, year, button);
    }

    /**
     * The button which opened this dialog
     *
     * @param button button to set
     */
    public void setButton(Button button)
    {
        this.button = button;
    }

    /**
     * Set the current selected day
     *
     * @param day selected day
     * @param month selected month
     * @param year selected year
     */
    public void setDate(int day, int month, int year)
    {
        this.selectedDay = day;
        this.selectedMonth = month;
        this.selectedYear = year;
    }
}
