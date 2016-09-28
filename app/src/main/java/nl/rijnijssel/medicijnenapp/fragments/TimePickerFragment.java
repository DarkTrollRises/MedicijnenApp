package nl.rijnijssel.medicijnenapp.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import nl.rijnijssel.medicijnenapp.edit.EditActivity;

/**
 * Created by Rik on 17-11-2015.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    private Button button;
    private int selectedHour, selectedMinute = -1;

    /**
     * Method to call when the dialog gets created
     *
     * @return the dialog to display
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour, minute;
        if(selectedHour != -1 && selectedMinute != -1)
        {
            hour = selectedHour;
            minute = selectedMinute;
        }
        else
        {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    /**
     * Called when the user picks a date
     *
     * @param view view of time picker
     * @param hour hour of day picked
     * @param minute minute of hour picked
     */
    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        ((EditActivity) getActivity()).setTime(hour, minute, button);
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
     * Set the current selected time
     *
     * @param hour selected hour of day
     * @param minute selected minute of hour
     */
    public void setTime(int hour, int minute)
    {
        this.selectedHour = hour;
        this.selectedMinute = minute;
    }
}
