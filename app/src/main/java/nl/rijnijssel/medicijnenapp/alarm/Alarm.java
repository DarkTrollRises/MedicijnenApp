package nl.rijnijssel.medicijnenapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.rijnijssel.medicijnenapp.edit.Day;
import nl.rijnijssel.medicijnenapp.edit.DayEnum;
import nl.rijnijssel.medicijnenapp.medicine.Medicine;

/**
 * Created by Rik on 3-12-2015.
 */
public class Alarm implements Parcelable
{
    /**
     * The list of current alarms
     */
    public static ArrayList<Alarm> list = new ArrayList<>();

    private int id, amount, hour, minute;
    private Context context;
    private Medicine medicine;
    private DayEnum[] days;
    private long time;
    private GregorianCalendar startDate, endDate;


    /**
     * Constructor without alarm ID
     *
     * @param context the context from which the alarm will be executed
     * @param medicine the medicine to display on the alarm
     * @param amount the amount the patient has to take in
     * @param days which days of the week does this patient need to take this medicine in
     * @param hour at which hour of the day does this patient need to take this medicine in
     * @param minute at which minute of the hour does this patient need to take this medicine in
     * @param startDate at which date does the alarm need to start happening
     * @param endDate at which date does the alarm need to stop happening (1-1-1970 to make it go infinitely)
     *
     * @see Medicine
     * @see GregorianCalendar
     */
    public Alarm(Context context, Medicine medicine, int amount, DayEnum[] days, int hour, int minute, GregorianCalendar startDate,
                 GregorianCalendar endDate)
    {
        this.context = context;
        this.medicine = medicine;
        this.amount = amount;
        this.days = days;
        this.hour = hour;
        this.minute = minute;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Constructor with alarm ID
     *
     * @param id the id of the alarm
     * @param context the context from which the alarm will be executed
     * @param medicine the medicine to display on the alarm
     * @param amount the amount the patient has to take in
     * @param days which days of the week does this patient need to take this medicine in
     * @param hour at which hour of the day does this patient need to take this medicine in
     * @param minute at which minute of the hour does this patient need to take this medicine in
     * @param startDate at which date does the alarm need to start happening
     * @param endDate at which date does the alarm need to stop happening (1-1-1970 to make it go infinitely)
     *
     * @see Medicine
     * @see GregorianCalendar
     */
    public Alarm(int id, Context context, Medicine medicine, int amount, DayEnum[] days, int hour, int minute, GregorianCalendar startDate,
                 GregorianCalendar endDate)
    {
        this(context, medicine, amount, days, hour, minute, startDate, endDate);
        this.id = id;
    }


    /**
     * Set the time at which the alarm will go off in milliseconds
     *
     * @param cal the time to set the alarm at in milliseconds
     *
     * @see GregorianCalendar
     */
    public void setTimeMillis(GregorianCalendar cal)
    {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        this.time = cal.getTimeInMillis();
    }

    /**
     * Schedule the alarm with the use of AlarmManager
     *
     * @see AlarmManager
     * @see PendingIntent
     * @see RequestIntent
     */
    public void scheduleAlarm()
    {
        AlarmManager am = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = ((int) time);
        PendingIntent pIntent = null;
        ArrayList<Medicine> meds;
        ArrayList<Integer> amounts;
        boolean reset = false;

        if(PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) == null)
        {
            meds = new ArrayList<>();
            amounts = new ArrayList<>();
            meds.add(medicine);
            amounts.add(amount);
            intent.putExtra("EXTRA_REQUEST", requestCode);
            intent.putExtra("EXTRA_MEDICINE", meds);
            intent.putExtra("EXTRA_AMOUNT", amounts);
            intent.putExtra("EXTRA_TIME", time);
            intent.putExtra("EXTRA_TIMES", 2);
            RequestIntent.list.add(new RequestIntent(intent, requestCode));
            RunningPendingIntents.list.add(((int) time));
            pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else
        {
            for(RequestIntent rIntent:RequestIntent.list)
            {
                if(rIntent.getRequestCode() == requestCode)
                {
                    intent = rIntent.getIntent();
                    Bundle bundleExtras = intent.getExtras();
                    meds = bundleExtras.getParcelableArrayList("EXTRA_MEDICINE");
                    amounts = bundleExtras.getIntegerArrayList("EXTRA_AMOUNT");
                    meds.add(medicine);
                    amounts.add(amount);
                    intent.putExtra("EXTRA_REQUEST", requestCode);
                    intent.putExtra("EXTRA_MEDICINE", meds);
                    intent.putExtra("EXTRA_AMOUNT", amounts);
                    rIntent.setIntent(intent);
                    pIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    reset = true;
                    break;
                }
            }
        }

        if(pIntent != null && !reset)
            am.setExact(AlarmManager.RTC_WAKEUP, time, pIntent);
    }

    /**
     * Add an alarm to the list of alarms
     *
     * @param alarm the alarm to add to the alarm list
     */
    public static void addAlarm(Alarm alarm)
    {
        list.add(alarm);
    }

    /**
     * Get an alarm at a specific index
     *
     * @param index index to get the alarm at
     * @return the alarm at the given index
     */
    public static Alarm getAlarm(int index)
    {
        return list.get(index);
    }

    /**
     * Remove an alarm from the list
     *
     * @param alarm the alarm to remove from the list
     */
    public static void removeAlarm(Alarm alarm)
    {
        list.remove(alarm);
    }

    /**
     * Set the id of this alarm
     *
     * @param id the id to set the current alarm to
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the id of the alarm
     *
     * @return the id of the alarm
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the context of the alarm
     *
     * @param context the context to set the alarm to
     */
    public void setContext(Context context)
    {
        this.context = context;
    }

    /**
     * Get the context of the alarm
     *
     * @return the context of the alarm
     */
    public Context getContext()
    {
        return context;
    }

    /**
     * Set the medicine to display with the alarm
     *
     * @param medicine the medicine to add to the alarm
     *
     * @see Medicine
     */
    public void setMedicine(Medicine medicine)
    {
        this.medicine = medicine;
    }

    /**
     * Get the medicine for the current alarm
     *
     * @return the medicine which will be displayed with the alarm
     *
     * @see Medicine
     */
    public Medicine getMedicine()
    {
        return medicine;
    }

    /**
     * Set the amount of medicine the patient needs to take in
     *
     * @param amount the amount of medicine to take in
     */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    /**
     * Get the amount of medicine to take in
     *
     * @return the amount of medicine to take in
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     * Set the days of the week to take this medicine in
     *
     * @param days an array with the days of the week on which to take this medicine in
     *
     * @see Day
     * @see DayEnum
     */
    public void setDays(DayEnum[] days)
    {
        this.days = days;
    }

    /**
     * Get the days of the week to take this medicine in
     *
     * @return an array with the days of the week on which to take this medicine in
     *
     * @see Day
     */
    public int[] getDays()
    {
        int[] days = new int[this.days.length];
        for(int i = 0; i < this.days.length; i++)
        {
            days[i] = Day.getNormalDay(this.days[i]);
        }
        return days;
    }

    /**
     * Get the days of the week to take this medicine in
     *
     * @return an array with the days of the week on which to take this medicine in
     *
     * @see Day
     * @see DayEnum
     */
    public DayEnum[] getDaysEnum()
    {
        return days;
    }

    /**
     * Set the time of the day at which to take in the medicine
     *
     * @param hour the hour of the day to take this medicine in
     * @param minute the minute of the hour to take this medicine in
     */
    public void setTime(int hour, int minute)
    {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Set the time of the day at which to take in the medicine
     *
     * @return the time of the day at which to take in the medicine
     */
    public String getTime()
    {
        return String.format("%02d:%02d:00", hour, minute);
    }

    /**
     * Get the time of the day in display format (HH:MM)
     *
     * @return the formatted time of the day
     */
    public String timeToString()
    {
        return String.format("%02d:%02d", hour, minute);
    }

    /**
     * Get the hour of the day at which to take this medicine in
     *
     * @return the hour of the day
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * Get the minute of the hour at which to take this medicine in
     *
     * @return the minute of the hour
     */
    public int getMinute()
    {
        return minute;
    }

    /**
     * Set the date at which the alarm will start going off
     *
     * @param startDate the date at which the alarm will start
     *
     * @see GregorianCalendar
     */
    public void setStartDate(GregorianCalendar startDate)
    {
        this.startDate = startDate;
    }

    /**
     * Get the date at which the alarm will start going off
     *
     * @return the date at which the alarm will start
     *
     * @see GregorianCalendar
     */
    public String getStartDate()
    {
        return String.format("%04d-%02d-%02d", startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH)+1, startDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Get the date at which the alarm will start going off in display format (DD/MM/YYYY)
     *
     * @return the formatted starting date
     *
     * @see GregorianCalendar
     */
    public String getRegularStartDate()
    {
        return String.format("%02d-%02d-%04d", startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.MONTH)+1, startDate.get(Calendar.YEAR));
    }

    /**
     * Set the date at which the alarm will stop going off
     *
     * @param endDate the date at which the alarm will stop
     *
     * @see GregorianCalendar
     */
    public void setEndDate(GregorianCalendar endDate)
    {
        this.endDate = endDate;
    }

    /**
     * Get the date at which the alarm will stop going off
     *
     * @return the date at which the alarm will stop
     *
     * @see GregorianCalendar
     */
    public String getEndDate()
    {
        return String.format("%04d-%02d-%02d", endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)+1, endDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Get the date at which the alarm will stop going off in display format (DD/MM/YYYY)
     *
     * @return the formatted ending date
     *
     * @see GregorianCalendar
     */
    public String getRegularEndDate()
    {
        return String.format("%02d-%02d-%04d", endDate.get(Calendar.DAY_OF_MONTH), endDate.get(Calendar.MONTH)+1, endDate.get(Calendar.YEAR));
    }

    /**
     * Format the information of the alarm for display
     *
     * @return the alarm formatted for display
     */
    @Override
    public String toString()
    {
        StringBuilder dayString = new StringBuilder();
        for(DayEnum day:days)
        {
            dayString.append(" ");
            dayString.append(Day.getShortStringFromEnum(day));
        }

        return String.format("%s - %d %s - %02d:%02d -%s", medicine.getName(), amount, medicine.getUnit(), hour, minute, dayString.toString());
    }

    /**
     * @see Parcelable
     */
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * @param dest parcel to write to
     * @param flags flags to go along with the parcel
     *
     * @see Parcel
     * @see Parcelable
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeParcelable(medicine, 0);
        dest.writeInt(amount);
        dest.writeSerializable(days);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeString(String.format("%04d-%02d-%02d", startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)));
        dest.writeString(String.format("%04d-%02d-%02d", endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH)));
    }

    /**
     * Constructor to take care of the alarm being converted back from a parcel
     *
     * @param source the parcel with the information about the alarm
     *
     * @see Parcel
     * @see Parcelable
     */
    private Alarm(Parcel source)
    {
        this.id = source.readInt();
        this.medicine = source.readParcelable(Medicine.class.getClassLoader());
        this.amount = source.readInt();
        this.days = ((DayEnum[]) source.readSerializable());
        this.hour = source.readInt();
        this.minute = source.readInt();

        String[] startDateArray = source.readString().split("-");
        String[] endDateArray = source.readString().split("-");
        this.startDate = new GregorianCalendar(Integer.parseInt(startDateArray[0]), Integer.parseInt(startDateArray[1]), Integer.parseInt(startDateArray[2]));
        this.endDate = new GregorianCalendar(Integer.parseInt(endDateArray[0]), Integer.parseInt(endDateArray[1]), Integer.parseInt(endDateArray[2]));
    }

    /**
     * The creator
     *
     * @see Parcelable
     */
    public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>() {

        @Override
        public Alarm createFromParcel(Parcel source) {
            return new Alarm(source);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };
}
