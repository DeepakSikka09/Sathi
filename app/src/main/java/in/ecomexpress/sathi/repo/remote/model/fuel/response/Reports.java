package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reports {

    private String id;

    private Reimbursement_status reimbursement_status;

    private String vehicle_type;

    private String trip_time;

    private String trip_date;

    private String trip_distance;

    private Shipment_details shipment_details;

    private String trip_name;




    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("reimbursement_status")
    public Reimbursement_status getReimbursement_status() {
        return reimbursement_status;
    }

    @JsonProperty("reimbursement_status")
    public void setReimbursement_status(Reimbursement_status reimbursement_status) {
        this.reimbursement_status = reimbursement_status;
    }

    @JsonProperty("vehicle_type")
    public String getVehicle_type() {
        return vehicle_type;
    }

    @JsonProperty("vehicle_type")
    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    @JsonProperty("trip_time")
    public String getTrip_time() {
        return trip_time;
    }

    @JsonProperty("trip_time")
    public void setTrip_time(String trip_time) {
        this.trip_time = trip_time;
    }

    @JsonProperty("trip_date")
    public String getTrip_date() {
        return trip_date;
    }

    @JsonProperty("trip_date")
    public void setTrip_date(String trip_date) {
        this.trip_date = trip_date;
    }

    @JsonProperty("trip_distance")
    public String getTrip_distance() {
        return trip_distance;
    }

    @JsonProperty("trip_distance")
    public void setTrip_distance(String trip_distance) {
        this.trip_distance = trip_distance;
    }

    @JsonProperty("shipment_details")
    public Shipment_details getShipment_details() {
        return shipment_details;
    }

    @JsonProperty("shipment_details")
    public void setShipment_details(Shipment_details shipment_details) {
        this.shipment_details = shipment_details;
    }

    @JsonProperty("trip_name")
    public String getTrip_name() {
        return trip_name;
    }

    @JsonProperty("trip_name")
    public void setTrip_name(String trip_name) {
        this.trip_name = trip_name;
    }

    @Override
    public String toString() {
        return "Reports [id = " + id + ", reimbursement_status = " + reimbursement_status + ", vehicle_type = " + vehicle_type + ", trip_time = " + trip_time + ", trip_date = " + trip_date + ", trip_distance = " + trip_distance + ", shipment_details = " + shipment_details + ", trip_name = " + trip_name + "]";
    }
}
