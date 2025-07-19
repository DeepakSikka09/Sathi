package in.ecomexpress.sathi.repo.local.data.fwd;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForwardCommit implements Parcelable, Cloneable {

    @JsonProperty("scan_info")
    private ArrayList<Amz_Scan> amz_scan= new ArrayList<>();

    @JsonProperty("payment_mode")
    public String getPayment_mode() {
        return payment_mode;
    }

    @JsonProperty("payment_mode")
    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    @JsonProperty("payment_mode")
    private String payment_mode;

    @JsonProperty("trying_reach")
    private String trying_reach;
    @JsonProperty("techpark")
    private String techpark;

    @JsonProperty("declared_value")
    private String declared_value;

    @JsonProperty("collectable_value")
    private String collectable_value;

    @JsonProperty("ofd_customer_otp")
    private String ofd_customer_otp;

    @JsonProperty("drs_commit_date_time")
    private String drs_commit_date_time;

    @JsonProperty("drs_id")
    private String drs_id;

    @JsonProperty("order_no")
    private String order_no;

    @JsonProperty("location_long")
    private String location_long="0.0";

    @JsonProperty("location_verified")
    private boolean location_verified;

    @JsonProperty("received_by_relation")
    private String received_by_relation;

    @JsonProperty("image_response")
    private List<Image_response> image_response;

    @JsonProperty("consignee_name")
    private String consignee_name;

    @JsonProperty("fe_comments")
    private String fe_comments;

    @JsonProperty("drs_date")
    private String drs_date;

    @JsonProperty("attempt_type")
    private String attempt_type;

    @JsonProperty("consignee_pincode")
    private String consignee_pincode;

    @JsonProperty("parent_awb")
    private String parent_awb;

    @JsonProperty("received_by_name")
    private String received_by_name;

    @JsonProperty("awb")
    private String awb;

    @JsonProperty("call_attempt_count")
    private int call_attempt_count;
    @JsonProperty("map_activity_count")
    private int map_activity_count;

    @JsonProperty("location_lat")
    private String location_lat="0.0";

    @JsonProperty("reschedule_date")
    private String reschedule_date;

    @JsonProperty("cod_amount")
    private String cod_amount;

    @JsonProperty("fe_emp_code")
    private String fe_emp_code;

    @JsonProperty("status")
    private String status;

    @JsonProperty("attempt_reason_code")
    private String attempt_reason_code;

    @JsonProperty("consignee_feedback")
    private String consignee_feedback;

    @JsonProperty("trip_id")
    private String trip_id;

    @JsonProperty("change_received_confirmation")
    private String change_received_confirmation = "N";

    @JsonProperty("shipment_type")
    private String shipment_type;

    @JsonProperty("shipper_id")
    private int shipment_id;

    @JsonProperty("payment_id")
    private String payment_id;

    @JsonProperty("address_type")
    private String address_type;

    @JsonProperty("dlight_secure_pin_option")
    private String dlight_encrption_otp_type;

    @JsonProperty("flag_of_warning")
    private String flag_of_warning;

    @JsonProperty("ud_otp_verify_status")
    private String ud_otp_verify_status;

    @JsonProperty("rchd_otp_verify_status")
    private String rd_otp_verify_status;

    @JsonProperty("ofd_otp_verified")
    private String ofd_otp_verified;

    @JsonProperty("scanned_method")
    private String scanable_by;

    @JsonProperty("return_package_barcode")
    private String return_packaging_barcode;

    @JsonProperty("is_obd")
    private boolean is_obd;

    // Start QC_item work:-
    @JsonProperty("qc_item")
    private List<QCItem> qc_item;

    public ForwardCommit() {}

    public void addImageData(Image_response imageData) {
        this.image_response.add(imageData);
    }

    @JsonProperty("location_verified")
    public boolean getLocation_verified() {
        return location_verified;
    }


    @JsonProperty("location_verified")
    public void setLocation_verified(boolean location_verified) {
        this.location_verified = location_verified;
    }

    @JsonProperty("declared_value")
    public String getDeclared_value() {
        return declared_value;
    }

    @JsonProperty("declared_value")
    public void setDeclared_value(String declared_value) {
        this.declared_value = declared_value;
    }

    @JsonProperty("collectable_value")
    public String getCollectable_value(){
        return collectable_value;
    }

    @JsonProperty("collectable_value")
    public void setCollectable_value(String collectable_value){
        this.collectable_value = collectable_value;
    }

    @JsonProperty("drs_commit_date_time")
    public String getDrs_commit_date_time() {
        return drs_commit_date_time;
    }

    @JsonProperty("drs_commit_date_time")
    public void setDrs_commit_date_time(String drs_commit_date_time) {
        this.drs_commit_date_time = drs_commit_date_time;
    }

    @JsonProperty("drs_id")
    public String getDrs_id() {
        return drs_id;
    }

    @JsonProperty("drs_id")
    public void setDrs_id(String drs_id) {
        this.drs_id = drs_id;
    }

    @JsonProperty("order_no")
    public String getOrder_no() {
        return order_no;
    }

    @JsonProperty("order_no")
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    @JsonProperty("location_long")
    public String getLocation_long() {
        return location_long;
    }

    @JsonProperty("location_long")
    public void setLocation_long(String location_long) {
        this.location_long = location_long;
    }

    @JsonProperty("received_by_relation")
    public String getReceived_by_relation() {
        return received_by_relation;
    }

    @JsonProperty("received_by_relation")
    public void setReceived_by_relation(String received_by_relation) {
        this.received_by_relation = received_by_relation;
    }

    @JsonProperty("image_response")
    public List<Image_response> getImage_response() {
        return image_response;
    }

    @JsonProperty("image_response")
    public void setImage_response(List<Image_response> image_response) {
        this.image_response = image_response;
    }

    @JsonProperty("consignee_name")
    public String getConsignee_name() {
        return consignee_name;
    }

    @JsonProperty("consignee_name")
    public void setConsignee_name(String consignee_name) {
        this.consignee_name = consignee_name;
    }

    @JsonProperty("fe_comments")
    public String getFe_comments() {
        return fe_comments;
    }

    @JsonProperty("fe_comments")
    public void setFe_comments(String fe_comments) {
        this.fe_comments = fe_comments;
    }

    @JsonProperty("drs_date")
    public String getDrs_date() {
        return drs_date;
    }

    @JsonProperty("drs_date")
    public void setDrs_date(String drs_date) {
        this.drs_date = drs_date;
    }

    @JsonProperty("attempt_type")
    public String getAttempt_type() {
        return attempt_type;
    }

    @JsonProperty("attempt_type")
    public void setAttempt_type(String attempt_type) {
        this.attempt_type = attempt_type;
    }

    @JsonProperty("consignee_pincode")
    public String getConsignee_pincode() {
        return consignee_pincode;
    }

    @JsonProperty("consignee_pincode")
    public void setConsignee_pincode(String consignee_pincode) {
        this.consignee_pincode = consignee_pincode;
    }

    @JsonProperty("parent_awb")
    public String getParent_awb() {
        return parent_awb;
    }

    @JsonProperty("parent_awb")
    public void setParent_awb(String parent_awb) {
        this.parent_awb = parent_awb;
    }

    @JsonProperty("received_by_name")
    public String getReceived_by_name() {
        return received_by_name;
    }

    @JsonProperty("received_by_name")
    public void setReceived_by_name(String received_by_name) {
        this.received_by_name = received_by_name;
    }

    @JsonProperty("awb")
    public String getAwb() {
        return awb;
    }

    @JsonProperty("awb")
    public void setAwb(String awb) {
        this.awb = awb;
    }

    @JsonProperty("location_lat")
    public String getLocation_lat() {
        return location_lat;
    }

    @JsonProperty("location_lat")
    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    @JsonProperty("reschedule_date")
    public String getReschedule_date() {
        return reschedule_date;
    }

    @JsonProperty("reschedule_date")
    public void setReschedule_date(String reschedule_date) {
        this.reschedule_date = reschedule_date;
    }

    @JsonProperty("cod_amount")
    public String getCod_amount() {
        return cod_amount;
    }

    @JsonProperty("cod_amount")
    public void setCod_amount(String cod_amount) {
        this.cod_amount = cod_amount;
    }

    @JsonProperty("fe_emp_code")
    public String getFe_emp_code() {
        return fe_emp_code;
    }

    @JsonProperty("fe_emp_code")
    public void setFe_emp_code(String fe_emp_code) {
        this.fe_emp_code = fe_emp_code;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("attempt_reason_code")
    public String getAttempt_reason_code() {
        return attempt_reason_code;
    }

    @JsonProperty("attempt_reason_code")
    public void setAttempt_reason_code(String attempt_reason_code) {
        this.attempt_reason_code = attempt_reason_code;
    }

    @JsonProperty("consignee_feedback")
    public String getConsignee_feedback() {
        return consignee_feedback;
    }

    @JsonProperty("consignee_feedback")
    public void setConsignee_feedback(String consignee_feedback) {
        this.consignee_feedback = consignee_feedback;
    }

    @JsonProperty("trip_id")
    public String getTrip_id() {
        return trip_id;
    }

    @JsonProperty("trip_id")
    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @JsonProperty("change_received_confirmation")
    public String getChange_received_confirmation() {
        return change_received_confirmation;
    }

    @JsonProperty("change_received_confirmation")
    public void setChange_received_confirmation(String change_received_confirmation) {
        this.change_received_confirmation = change_received_confirmation;
    }

    @JsonProperty("shipment_type")
    public String getShipment_type() {
        return shipment_type;
    }

    @JsonProperty("shipment_type")
    public void setShipment_type(String shipment_type) {
        this.shipment_type = shipment_type;
    }

    @JsonProperty("payment_id")
    public String getPayment_id() {
        return payment_id;
    }

    @JsonProperty("payment_id")
    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    @JsonProperty("address_type")
    public String getAddress_type() {
        return address_type;
    }

    @JsonProperty("address_type")
    public void setAddress_type(String address_type) {
        this.address_type = address_type;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getDlight_encrption_otp_type() {
        return dlight_encrption_otp_type;
    }

    public void setDlight_encrption_otp_type(String dlight_encrption_otp_type) {
        this.dlight_encrption_otp_type = dlight_encrption_otp_type;
    }

    public ArrayList<Amz_Scan> getAmz_scan() {
        return amz_scan;
    }

    public void setAmz_scan(ArrayList<Amz_Scan> amz_scan) {
        this.amz_scan = amz_scan;
    }

    public int getShipment_id(){
        return shipment_id;
    }

    public void setShipment_id(int shipment_id){
        this.shipment_id = shipment_id;
    }

    public String getOfd_otp_verified(){
        return ofd_otp_verified;
    }

    public void setOfd_otp_verified(String ofd_otp_verified){
        this.ofd_otp_verified = ofd_otp_verified;
    }

    public String getScanable_by() {
        return scanable_by;
    }

    public void setScanable_by(String scanable_by) {
        this.scanable_by = scanable_by;
    }

    public String getReturn_packaging_barcode() {
        return return_packaging_barcode;
    }

    public void setReturn_packaging_barcode(String return_packaging_barcode) {
        this.return_packaging_barcode = return_packaging_barcode;
    }

    public String getOfd_customer_otp(){
        return ofd_customer_otp;
    }

    public void setOfd_customer_otp(String ofd_customer_otp){
        this.ofd_customer_otp = ofd_customer_otp;
    }

    public int getCall_attempt_count(){
        return call_attempt_count;
    }

    public void setCall_attempt_count(int call_attempt_count){
        this.call_attempt_count = call_attempt_count;
    }

    public int getMap_activity_count(){
        return map_activity_count;
    }

    public void setMap_activity_count(int map_activity_count){
        this.map_activity_count = map_activity_count;
    }

    public String getTrying_reach(){
        return trying_reach;
    }

    public void setTrying_reach(String trying_reach){
        this.trying_reach = trying_reach;
    }

    public String getTechpark(){
        return techpark;
    }

    public void setTechpark(String techpark){
        this.techpark = techpark;
    }

    public boolean isIs_obd() {
        return is_obd;
    }

    public void setIs_obd(boolean is_obd) {
        this.is_obd = is_obd;
    }

    public List<QCItem> getQc_item() {
        return qc_item;
    }

    public void setQc_item(List<QCItem> qc_item) {
        this.qc_item = qc_item;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image_response implements Parcelable {
        @JsonProperty("image_key")
        private String image_key;
        @JsonProperty("image_id")
        private String image_id;

        public Image_response() {}

        public Image_response(String image_key, String image_id) {
            this.image_key = image_key;
            this.image_id = image_id;
        }

        protected Image_response(Parcel in) {
            image_key = in.readString();
            image_id = in.readString();
        }

        public static final Creator<Image_response> CREATOR = new Creator<Image_response>() {
            @Override
            public Image_response createFromParcel(Parcel in) {
                return new Image_response(in);
            }

            @Override
            public Image_response[] newArray(int size) {
                return new Image_response[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(image_key);
            dest.writeString(image_id);
        }

        @JsonProperty("image_key")
        public String getImage_key() {
            return image_key;
        }

        @JsonProperty("image_key")
        public void setImage_key(String image_key) {
            this.image_key = image_key;
        }

        @JsonProperty("image_id")
        public String getImage_id() {
            return image_id;
        }

        @JsonProperty("image_id")
        public void setImage_id(String image_id) {
            this.image_id = image_id;
        }

        @Override
        public String toString() {
            return "Image_response [image_key = " + image_key + ", image_id = " + image_id + "]";
        }

        @Override
        public int describeContents() {
            return 0;
        }

    }

    // QC QCItem Class:-
    public static class QCItem implements Parcelable {
        @JsonProperty("item_number")
        private String item_number;

        @JsonProperty("status")
        private String status;

        @JsonProperty("qc_wizard")
        private List<QcWizard> qc_wizard;

        @JsonProperty("image_response")
        private List<Image_response> image_response;

        public QCItem() {}

        protected QCItem(Parcel in) {
            item_number = in.readString();
            status = in.readString();
            qc_wizard = in.createTypedArrayList(QcWizard.CREATOR);
            image_response = in.createTypedArrayList(Image_response.CREATOR);
        }

        public static final Creator<QCItem> CREATOR = new Creator<QCItem>() {
            @Override
            public QCItem createFromParcel(Parcel in) {
                return new QCItem(in);
            }

            @Override
            public QCItem[] newArray(int size) {
                return new QCItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(item_number);
            dest.writeString(status);
            dest.writeTypedList(qc_wizard);
            dest.writeTypedList(image_response);
        }

        public String getItem_number() {
            return item_number;
        }

        public void setItem_number(String item_number) {
            this.item_number = item_number;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<QcWizard> getQc_wizard() {
            return qc_wizard;
        }

        public void setQc_wizard(List<QcWizard> qc_wizard) {
            this.qc_wizard = qc_wizard;
        }

        public List<Image_response> getImage_response() {
            return image_response;
        }

        public void setImage_response(List<Image_response> image_response) {
            this.image_response = image_response;
        }
    }

    // QC Wizard Class:-
    public static class QcWizard implements Parcelable {
        @JsonProperty("rvp_qc_code")
        private String rvp_qc_code;

        @JsonProperty("fe_remarks")
        private String fe_remarks;

        @JsonProperty("qc_check_status")
        private String qc_check_status;

        @JsonProperty("qc_result_value")
        private String qc_result_value;

        public QcWizard() {}

        protected QcWizard(Parcel in) {
            rvp_qc_code = in.readString();
            fe_remarks = in.readString();
            qc_check_status = in.readString();
            qc_result_value = in.readString();
        }

        public static final Creator<QcWizard> CREATOR = new Creator<QcWizard>() {
            @Override
            public QcWizard createFromParcel(Parcel in) {
                return new QcWizard(in);
            }

            @Override
            public QcWizard[] newArray(int size) {
                return new QcWizard[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(rvp_qc_code);
            dest.writeString(fe_remarks);
            dest.writeString(qc_check_status);
            dest.writeString(qc_result_value);
        }

        public String getRvp_qc_code() {
            return rvp_qc_code;
        }

        public void setRvp_qc_code(String rvp_qc_code) {
            this.rvp_qc_code = rvp_qc_code;
        }

        public String getFe_remarks() {
            return fe_remarks;
        }

        public void setFe_remarks(String fe_remarks) {
            this.fe_remarks = fe_remarks;
        }

        public String getQc_check_status() {
            return qc_check_status;
        }

        public void setQc_check_status(String qc_check_status) {
            this.qc_check_status = qc_check_status;
        }

        public String getQc_result_value() {
            return qc_result_value;
        }

        public void setQc_result_value(String qc_result_value) {
            this.qc_result_value = qc_result_value;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amz_Scan implements Parcelable {
        @JsonProperty("otp_status")
        private String amz_otp_status = "";

        @JsonProperty("otp_verified_time")
        private long otp_verified_time;

        @JsonProperty("pinb_status")
        private String amz_pinb_status ="";

        @JsonProperty("pinb_verified_time")
        private long pinb_verified_time;

        @JsonProperty("otp_value")
        private String amz_otp_value;

        @JsonProperty("pinb_value")
        private String amz_pinb_value;

        public Amz_Scan() {}

        public String isAmz_otp_status() {
            return amz_otp_status;
        }

        public void setAmz_otp_status(String amz_otp_status) {
            this.amz_otp_status = amz_otp_status;
        }

        public long getOtp_verified_time() {
            return otp_verified_time;
        }

        public void setOtp_verified_time(long otp_verified_time) {
            this.otp_verified_time = otp_verified_time;
        }

        public String isAmz_pinb_status() {
            return amz_pinb_status;
        }

        public void setAmz_pinb_status(String amz_pinb_status) {
            this.amz_pinb_status = amz_pinb_status;
        }

        public long getPinb_verified_time() {
            return pinb_verified_time;
        }

        public void setPinb_verified_time(long pinb_verified_time) {
            this.pinb_verified_time = pinb_verified_time;
        }

        public String getAmz_otp_value() {
            return amz_otp_value;
        }

        public void setAmz_otp_value(String amz_otp_value) {
            this.amz_otp_value = amz_otp_value;
        }

        public String getAmz_pinb_value() {
            return amz_pinb_value;
        }

        public void setAmz_pinb_value(String amz_pinb_value) {
            this.amz_pinb_value = amz_pinb_value;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.amz_otp_status);
            dest.writeLong(this.otp_verified_time);
            dest.writeString(this.amz_pinb_status);
            dest.writeLong(this.pinb_verified_time);
            dest.writeString(this.amz_otp_value);
            dest.writeString(this.amz_pinb_value);
        }

        protected Amz_Scan(Parcel in) {
            this.amz_otp_status = in.readString();
            this.otp_verified_time = in.readLong();
            this.amz_pinb_status = in.readString();
            this.pinb_verified_time = in.readLong();
            this.amz_otp_value = in.readString();
            this.amz_pinb_value = in.readString();
        }

        public static final Creator<Amz_Scan> CREATOR = new Creator<Amz_Scan>() {
            @Override
            public Amz_Scan createFromParcel(Parcel source) {
                return new Amz_Scan(source);
            }

            @Override
            public Amz_Scan[] newArray(int size) {
                return new Amz_Scan[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.amz_scan);
        dest.writeString(this.payment_mode);
        dest.writeString(this.declared_value);
        dest.writeString(this.collectable_value);
        dest.writeString(this.drs_commit_date_time);
        dest.writeString(this.drs_id);
        dest.writeString(this.order_no);
        dest.writeString(this.location_long);
        dest.writeByte(this.location_verified ? (byte) 1 : (byte) 0);
        dest.writeString(this.received_by_relation);
        dest.writeTypedList(this.image_response);
        dest.writeString(this.consignee_name);
        dest.writeString(this.fe_comments);
        dest.writeString(this.drs_date);
        dest.writeString(this.attempt_type);
        dest.writeString(this.consignee_pincode);
        dest.writeString(this.parent_awb);
        dest.writeString(this.received_by_name);
        dest.writeString(this.awb);
        dest.writeString(this.location_lat);
        dest.writeString(this.reschedule_date);
        dest.writeString(this.cod_amount);
        dest.writeString(this.fe_emp_code);
        dest.writeString(this.status);
        dest.writeString(this.attempt_reason_code);
        dest.writeString(this.consignee_feedback);
        dest.writeString(this.trip_id);
        dest.writeString(this.change_received_confirmation);
        dest.writeString(this.shipment_type);
        dest.writeString(this.payment_id);
        dest.writeString(this.address_type);
        dest.writeString(this.dlight_encrption_otp_type);
        dest.writeString(this.flag_of_warning);
        dest.writeString(this.ud_otp_verify_status);
        dest.writeString(this.rd_otp_verify_status);
        dest.writeInt(this.call_attempt_count);
        dest.writeInt(this.map_activity_count);
        dest.writeByte((byte) (is_obd ? 1 : 0));
        dest.writeTypedList(qc_item);
    }

    protected ForwardCommit(Parcel in) {
        this.amz_scan = in.createTypedArrayList(Amz_Scan.CREATOR);
        this.payment_mode = in.readString();
        this.declared_value = in.readString();
        this.collectable_value = in.readString();
        this.drs_commit_date_time = in.readString();
        this.drs_id = in.readString();
        this.order_no = in.readString();
        this.location_long = in.readString();
        this.location_verified = in.readByte() != 0;
        this.received_by_relation = in.readString();
        this.image_response = in.createTypedArrayList(Image_response.CREATOR);
        this.consignee_name = in.readString();
        this.fe_comments = in.readString();
        this.drs_date = in.readString();
        this.attempt_type = in.readString();
        this.consignee_pincode = in.readString();
        this.parent_awb = in.readString();
        this.received_by_name = in.readString();
        this.awb = in.readString();
        this.location_lat = in.readString();
        this.reschedule_date = in.readString();
        this.cod_amount = in.readString();
        this.fe_emp_code = in.readString();
        this.status = in.readString();
        this.attempt_reason_code = in.readString();
        this.consignee_feedback = in.readString();
        this.trip_id = in.readString();
        this.change_received_confirmation = in.readString();
        this.shipment_type = in.readString();
        this.payment_id = in.readString();
        this.address_type = in.readString();
        this.dlight_encrption_otp_type = in.readString();
        this.flag_of_warning=in.readString();
        this.ud_otp_verify_status=in.readString();
        this.rd_otp_verify_status=in.readString();
        this.call_attempt_count = in.readInt();
        this.map_activity_count = in.readInt();
        this.is_obd = in.readByte() != 0;
        this.qc_item = in.createTypedArrayList(QCItem.CREATOR);
    }

    public void setUd_otp(String ud_otp) {
        this.ud_otp_verify_status = ud_otp;
    }

    public String getUd_otp() {
        return ud_otp_verify_status;
    }

    public void setRd_otp(String rd_otp) {
        this.rd_otp_verify_status = rd_otp;
    }

    public String getRd_otp() {
        return rd_otp_verify_status;
    }

    public void setFlag_of_warning(String flag_of_warning) {
        this.flag_of_warning = flag_of_warning;
    }

    public String getFlag_of_warning() {
        return flag_of_warning;
    }

    public static final Creator<ForwardCommit> CREATOR = new Creator<ForwardCommit>() {
        @Override
        public ForwardCommit createFromParcel(Parcel source) {
            return new ForwardCommit(source);
        }

        @Override
        public ForwardCommit[] newArray(int size) {
            return new ForwardCommit[size];
        }
    };
}