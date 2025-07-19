package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponseList;

public class MonthStatusItemViewModel{

    public AttendanceResponseList attendanceResponseList;

    public MonthStatusItemViewModel(AttendanceResponseList attendanceResponseList) {
        this.attendanceResponseList = attendanceResponseList;
    }

    public String Date() {
        return attendanceResponseList.getDate();
    }

    public String status() {
        return attendanceResponseList.getAttendance_status();
    }

    public String FormattedDate() {
        return attendanceResponseList.getFormatted_date();
    }
    public String WorkingHours() {
        return attendanceResponseList.getDaily_working_hours();
    }

    public String Intime() {
        return attendanceResponseList.getIn_time();
    }

    public String outTime() {
        return attendanceResponseList.getOut_time();
    }
}