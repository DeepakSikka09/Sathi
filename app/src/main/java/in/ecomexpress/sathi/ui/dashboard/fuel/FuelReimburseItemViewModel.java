package in.ecomexpress.sathi.ui.dashboard.fuel;

import in.ecomexpress.sathi.repo.remote.model.fuel.response.Reports;
import in.ecomexpress.sathi.utils.CommonUtils;

public class FuelReimburseItemViewModel {

    public Reports reports;

    public FuelReimburseItemViewModel(Reports reports) {
        this.reports = reports;
    }

    public String TripName() {
        return reports.getTrip_name();
    }

    public String TripDate() {
        return reports.getTrip_date();
    }

    public String TripTime() {
        String millis = reports.getTrip_time();
        long seconds = Long.parseLong(millis) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return hours % 24 + " hours " + minutes % 60 + " min ";
    }

    public String TripDistance() {
        return reports.getTrip_distance() + " km";
    }

    public String Undelivered() {
        return reports.getShipment_details().getUndelivered();
    }

    public String Delivered() {
        return reports.getShipment_details().getSuccess();
    }

    public String Pending() {
        return reports.getShipment_details().getUnattempted();
    }

    public String Claimed() {
        return "₹ "+reports.getReimbursement_status().getClaimed();
    }

    public String Status() {
        return CommonUtils.toTitleCase(reports.getReimbursement_status().getStatus());
    }
}