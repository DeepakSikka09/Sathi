package in.ecomexpress.sathi.repo.remote.model.adm;

import java.util.ArrayList;

public class ADMDATA
{
    private String date;

    private String planned_intime;

    private ArrayList<Available_slots> available_slots;

    private String weekly_off;

    private String day_of_week;

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getPlanned_intime ()
    {
        return planned_intime;
    }

    public void setPlanned_intime (String planned_intime)
    {
        this.planned_intime = planned_intime;
    }

    public ArrayList<Available_slots> getAvailable_slots ()
    {
        return available_slots;
    }

    public void setAvailable_slots (ArrayList<Available_slots> available_slots)
    {
        this.available_slots = available_slots;
    }

    public String getWeekly_off ()
    {
        return weekly_off;
    }

    public void setWeekly_off (String weekly_off)
    {
        this.weekly_off = weekly_off;
    }

    public String getDay_of_week ()
    {
        return day_of_week;
    }

    public void setDay_of_week (String day_of_week)
    {
        this.day_of_week = day_of_week;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [date = "+date+", planned_intime = "+planned_intime+", available_slots = "+available_slots+", weekly_off = "+weekly_off+", day_of_week = "+day_of_week+"]";
    }
}