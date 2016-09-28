package nl.rijnijssel.medicijnenapp.edit;

import java.util.Calendar;

/**
 * Created by Rik on 27-11-2015.
 */
public class Day
{
    /**
     * Convert a DayEnum to the current day as an int
     *
     * @param day DayEnum to convert
     * @return day of the week as an int
     *
     * @see DayEnum
     */
    public static int getNormalDay(DayEnum day)
    {
        switch(day)
        {
            case MONDAY:
                return 1;
            case TUESDAY:
                return 2;
            case WEDNESDAY:
                return 3;
            case THURSDAY:
                return 4;
            case FRIDAY:
                return 5;
            case SATURDAY:
                return 6;
            case SUNDAY:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Convert an int to the current day as a DayEnum
     *
     * @param day int to convert to DayEnum
     * @return day of the week as a DayEnum
     *
     * @see DayEnum
     */
    public static DayEnum getEnumDayFromNormal(int day)
    {
        switch(day)
        {
            case 1:
                return DayEnum.MONDAY;
            case 2:
                return DayEnum.TUESDAY;
            case 3:
                return DayEnum.WEDNESDAY;
            case 4:
                return DayEnum.THURSDAY;
            case 5:
                return DayEnum.FRIDAY;
            case 6:
                return DayEnum.SATURDAY;
            case 0:
                return DayEnum.SUNDAY;
            default:
                return DayEnum.MONDAY;
        }
    }

    /**
     * Convert a DayEnum to a short string
     *
     * @param day DayEnum to convert to short string (Ma. Di. etc.)
     * @return day of the week as a short string
     *
     * @see DayEnum
     */
    public static String getShortStringFromEnum(DayEnum day)
    {
        switch(day)
        {
            case MONDAY:
                return "Ma.";
            case TUESDAY:
                return "Di.";
            case WEDNESDAY:
                return "Wo.";
            case THURSDAY:
                return "Do.";
            case FRIDAY:
                return "Vr.";
            case SATURDAY:
                return "Za.";
            case SUNDAY:
                return "Zo.";
            default:
                return null;
        }
    }
}
