package in.ecomexpress.sathi.repo.remote.model.attendance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceResponseList
{
    @JsonProperty("date")
    private String date;

    @JsonProperty("out_time")
    private String out_time;

    @JsonProperty("in_time")
    private String in_time;

    @JsonProperty("formatted_date")
    private String formatted_date;

    @JsonProperty("attendance_status")
    private String attendance_status;

    @JsonProperty("daily_working_hours")
    private String daily_working_hours;

    @JsonProperty("date")
    public String getDate ()
    {
        return date;
    }

    @JsonProperty("date")
    public void setDate (String date)
    {
        this.date = date;
    }

    @JsonProperty("out_time")
    public String getOut_time ()
    {
        return out_time;
    }

    @JsonProperty("out_time")
    public void setOut_time (String out_time)
    {
        this.out_time = out_time;
    }

    @JsonProperty("in_time")
    public String getIn_time ()
    {
        return in_time;
    }

    @JsonProperty("in_time")
    public void setIn_time (String in_time)
    {
        this.in_time = in_time;
    }

    @JsonProperty("formatted_date")
    public String getFormatted_date ()
    {
        return formatted_date;
    }

    @JsonProperty("formatted_date")
    public void setFormatted_date (String formatted_date)
    {
        this.formatted_date = formatted_date;
    }

    @JsonProperty("attendance_status")
    public String getAttendance_status ()
    {
        return attendance_status;
    }

    @JsonProperty("attendance_status")
    public void setAttendance_status (String attendance_status)
    {
        this.attendance_status = attendance_status;
    }

    @JsonProperty("daily_working_hours")
    public String getDaily_working_hours ()
    {
        return daily_working_hours;
    }

    @JsonProperty("daily_working_hours")
    public void setDaily_working_hours (String daily_working_hours)
    {
        this.daily_working_hours = daily_working_hours;
    }

    @Override
    public String toString()
    {
        return "Attendance_response_list [date = "+date+", out_time = "+out_time+", in_time = "+in_time+", formatted_date = "+formatted_date+", attendance_status = "+attendance_status+", daily_working_hours = "+daily_working_hours+"]";
    }
}
