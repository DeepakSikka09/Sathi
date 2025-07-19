package in.ecomexpress.sathi.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * If you pass x mills then this method will return 14:45 if its today time or 12 Sep if its before.
     *
     * @param mills
     * @return
     */
    public static String millsToddMMMorHHmm(long mills) {
        Date date = new Date(mills);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM");
        SimpleDateFormat sdfHours = new SimpleDateFormat("HH:mm");
        Date dateCurrent = new Date();
        if (date.before(dateCurrent)) {
            return sdfDate.format(date);
        } else {
            //to show current time format
            return sdfHours.format(Calendar.getInstance().getTimeInMillis());
        }
    }

    /**
     * If you pass x mills then this method will return 14:45 if its today time or 12 Sep if its before.
     *
     * @param mills
     * @return
     */
    public static String millsToddMMYYYYHHmm(long mills) {
        Date date = new Date(mills);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM YYYY HH:mm");
        return sdfDate.format(date);
    }

    /**
     * this method will return only year month and date milli seconds
     *
     * @return
     */
    public static long getDateYearMonthMillies() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static String getDateTime() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a dd-MMM-yyyy ");
        return dateFormat.format(date);
    }
}
