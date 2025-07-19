package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

public class CalendarDaysViewModel {

    public String dayName;

    public CalendarDaysViewModel(String dayName) {
        this.dayName = dayName;
    }

    public String getDay() {
        return dayName;
    }
}