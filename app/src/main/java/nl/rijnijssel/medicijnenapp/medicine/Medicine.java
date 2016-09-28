package nl.rijnijssel.medicijnenapp.medicine;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rik on 14-10-2015.
 */
public class Medicine implements Parcelable
{
    private int id;
    private String name, unit;

    /**
     * Default constructor
     *
     * @param id id of the medicine
     */
    public Medicine(int id)
    {
        this.id = id;
    }

    /**
     * Set the id of the medicine
     *
     * @param id id of medicine
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the id of the medicine
     *
     * @return id of the medicine
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the name of the medicine
     *
     * @param name name of medicine
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the name of the medicine
     *
     * @return name of the medicine
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the unit of the medicine
     *
     * @param unit unit of medicine
     */
    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    /**
     * Get the unit of the medicine
     *
     * @return unit of medicine
     */
    public String getUnit()
    {
        return unit;
    }

    /**
     * @return medicine formatted to just name
     */
    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(unit);
    }

    private Medicine(Parcel source)
    {
        this.id = source.readInt();
        this.name = source.readString();
        this.unit = source.readString();
    }

    public static final Parcelable.Creator<Medicine> CREATOR = new Creator<Medicine>() {

        @Override
        public Medicine createFromParcel(Parcel source) {
            return new Medicine(source);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };
}
