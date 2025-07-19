package in.ecomexpress.sathi.repo.remote.model.attendance;



import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response
{
    @JsonProperty("code")
    private int code;

    @JsonProperty("description")
    private String description;

    @Embedded
    @JsonProperty("attendance_details")
    private AttendanceDetails attendanceDetails;

    @JsonProperty("code")
    public int getCode ()
    {
        return code;
    }

    @JsonProperty("code")
    public void setCode (int code)
    {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription ()
    {
        return description;
    }

    @JsonProperty("description")
    public void setDescription (String description)
    {
        this.description = description;
    }

    @JsonProperty("attendance_details")
    public AttendanceDetails getAttendance_details ()
    {
        return attendanceDetails;
    }

    @JsonProperty("attendance_details")
    public void setAttendance_details (AttendanceDetails attendanceDetails)
    {
        this.attendanceDetails = attendanceDetails;
    }

    @Override
    public String toString()
    {
        return "Response [code = "+code+", description = "+description+", attendance_details = "+attendanceDetails+"]";
    }
}
