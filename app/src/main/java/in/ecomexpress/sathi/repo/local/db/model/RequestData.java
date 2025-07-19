package in.ecomexpress.sathi.repo.local.db.model;

public class RequestData
{
    private String fe_emp_code;

    private String location_long;

    private String received_by_relation;

    private String trip_id;

    private String address_type;

    private String received_by_name;

    private String consignee_name;

    private String drs_commit_date_time;

    private String drs_date;

    private String awb;

    private String attempt_reason_code;

    private String drs_id;

    private String location_verified;

    private String attempt_type;

    private String location_lat;

    private String status;

    public String getFe_emp_code ()
    {
        return fe_emp_code;
    }

    public void setFe_emp_code (String fe_emp_code)
    {
        this.fe_emp_code = fe_emp_code;
    }

    public String getLocation_long ()
    {
        return location_long;
    }

    public void setLocation_long (String location_long)
    {
        this.location_long = location_long;
    }

    public String getReceived_by_relation ()
    {
        return received_by_relation;
    }

    public void setReceived_by_relation (String received_by_relation)
    {
        this.received_by_relation = received_by_relation;
    }

    public String getTrip_id ()
    {
        return trip_id;
    }

    public void setTrip_id (String trip_id)
    {
        this.trip_id = trip_id;
    }

    public String getAddress_type ()
    {
        return address_type;
    }

    public void setAddress_type (String address_type)
    {
        this.address_type = address_type;
    }

    public String getReceived_by_name ()
    {
        return received_by_name;
    }

    public void setReceived_by_name (String received_by_name)
    {
        this.received_by_name = received_by_name;
    }

    public String getConsignee_name ()
    {
        return consignee_name;
    }

    public void setConsignee_name (String consignee_name)
    {
        this.consignee_name = consignee_name;
    }

    public String getDrs_commit_date_time ()
    {
        return drs_commit_date_time;
    }

    public void setDrs_commit_date_time (String drs_commit_date_time)
    {
        this.drs_commit_date_time = drs_commit_date_time;
    }

    public String getDrs_date ()
    {
        return drs_date;
    }

    public void setDrs_date (String drs_date)
    {
        this.drs_date = drs_date;
    }

    public String getAwb ()
    {
        return awb;
    }

    public void setAwb (String awb)
    {
        this.awb = awb;
    }

    public String getAttempt_reason_code ()
    {
        return attempt_reason_code;
    }

    public void setAttempt_reason_code (String attempt_reason_code)
    {
        this.attempt_reason_code = attempt_reason_code;
    }


    public String getDrs_id ()
    {
        return drs_id;
    }

    public void setDrs_id (String drs_id)
    {
        this.drs_id = drs_id;
    }

    public String getLocation_verified ()
    {
        return location_verified;
    }

    public void setLocation_verified (String location_verified)
    {
        this.location_verified = location_verified;
    }

    public String getAttempt_type ()
    {
        return attempt_type;
    }

    public void setAttempt_type (String attempt_type)
    {
        this.attempt_type = attempt_type;
    }

    public String getLocation_lat ()
    {
        return location_lat;
    }

    public void setLocation_lat (String location_lat)
    {
        this.location_lat = location_lat;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [fe_emp_code = "+fe_emp_code+", location_long = "+location_long+", received_by_relation = "+received_by_relation+", trip_id = "+trip_id+", address_type = "+address_type+", received_by_name = "+received_by_name+", consignee_name = "+consignee_name+", drs_commit_date_time = "+drs_commit_date_time+", drs_date = "+drs_date+", awb = "+awb+", attempt_reason_code = "+attempt_reason_code+", drs_id = "+drs_id+", location_verified = "+location_verified+", attempt_type = "+attempt_type+", location_lat = "+location_lat+", status = "+status+"]";
    }
}