package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trip_details {

    @JsonProperty("created_date")
    private String created_date;
    @JsonProperty("status")
    private String status;
    @JsonProperty("cmr")
    private String cmr;
    @JsonProperty("trip_date")
    private String trip_date;
    @JsonProperty("voucher_num")
    private String voucher_num;
    @JsonProperty("justification")
    private String justification;
    @JsonProperty("other_exp")
    private String other_exp;
    @JsonProperty("f_executive_emp_code")
    private String f_executive_emp_code;
    @JsonProperty("parking_exp")
    private String parking_exp;
    @JsonProperty("id")
    private String id;
    @JsonProperty("voucher_status")
    private String voucher_status;
    @JsonProperty("route_name")
    private String route_name;
    @JsonProperty("omr")
    private String omr;
    @JsonProperty("approval_date")
    private String approval_date;
    @JsonProperty("total_exp")
    private String total_exp;
    @JsonProperty("total_km")
    private String total_km;
    @JsonProperty("day")
    private String day;
    @JsonProperty("fuel_exp")
    private String fuel_exp;

    @JsonProperty("created_date")
    public String getCreated_date() {
        return created_date;
    }

    @JsonProperty("created_date")
    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("cmr")
    public String getCmr() {
        return cmr;
    }

    @JsonProperty("cmr")
    public void setCmr(String cmr) {
        this.cmr = cmr;
    }
    @JsonProperty("trip_date")
    public String getTrip_date() {
        return trip_date;
    }
    @JsonProperty("trip_date")
    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }
    @JsonProperty("voucher_num")
    public String getVoucher_num() {
        return voucher_num;
    }
    @JsonProperty("voucher_num")
    public void setVoucher_num(String voucher_num) {
        this.voucher_num = voucher_num;
    }
    @JsonProperty("justification")
    public String getJustification() {
        return justification;
    }
    @JsonProperty("justification")
    public void setJustification(String justification) {
        this.justification = justification;
    }
    @JsonProperty("other_exp")
    public String getOther_exp() {
        return other_exp;
    }
    @JsonProperty("other_exp")
    public void setOther_exp(String other_exp) {
        this.other_exp = other_exp;
    }
    @JsonProperty("f_executive_emp_code")
    public String getF_executive_emp_code() {
        return f_executive_emp_code;
    }
    @JsonProperty("f_executive_emp_code")
    public void setF_executive_emp_code(String f_executive_emp_code) {
        this.f_executive_emp_code = f_executive_emp_code;
    }
    @JsonProperty("parking_exp")
    public String getParking_exp() {
        return parking_exp;
    }
    @JsonProperty("parking_exp")
    public void setParking_exp(String parking_exp) {
        this.parking_exp = parking_exp;
    }
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("voucher_status")
    public String getVoucher_status() {
        return voucher_status;
    }
    @JsonProperty("voucher_status")
    public void setVoucher_status(String voucher_status) {
        this.voucher_status = voucher_status;
    }
    @JsonProperty("route_name")
    public String getRoute_name() {
        return route_name;
    }
    @JsonProperty("route_name")
    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }
    @JsonProperty("omr")
    public String getOmr() {
        return omr;
    }
    @JsonProperty("omr")
    public void setOmr(String omr) {
        this.omr = omr;
    }
    @JsonProperty("approval_date")
    public String getApproval_date() {
        return approval_date;
    }
    @JsonProperty("approval_date")
    public void setApproval_date(String approval_date) {
        this.approval_date = approval_date;
    }
    @JsonProperty("total_exp")
    public String getTotal_exp() {
        return total_exp;
    }
    @JsonProperty("total_exp")
    public void setTotal_exp(String total_exp) {
        this.total_exp = total_exp;
    }
    @JsonProperty("total_km")
    public String getTotal_km() {
        return total_km;
    }
    @JsonProperty("total_km")
    public void setTotal_km(String total_km) {
        this.total_km = total_km;
    }
    @JsonProperty("day")
    public String getDay() {
        return day;
    }
    @JsonProperty("day")
    public void setDay(String day) {
        this.day = day;
    }
    @JsonProperty("fuel_exp")
    public String getFuel_exp() {
        return fuel_exp;
    }
    @JsonProperty("fuel_exp")
    public void setFuel_exp(String fuel_exp) {
        this.fuel_exp = fuel_exp;
    }

    @Override
    public String toString() {
        return "Trip_details [created_date = " + created_date + ", status = " + status + ", cmr = " + cmr + ", trip_date = " + trip_date + ", voucher_num = " + voucher_num + ", justification = " + justification + ", other_exp = " + other_exp + ", f_executive_emp_code = " + f_executive_emp_code + ", parking_exp = " + parking_exp + ", id = " + id + ", voucher_status = " + voucher_status + ", route_name = " + route_name + ", omr = " + omr + ", approval_date = " + approval_date + ", total_exp = " + total_exp + ", total_km = " + total_km + ", day = " + day + ", fuel_exp = " + fuel_exp + "]";
    }

}
