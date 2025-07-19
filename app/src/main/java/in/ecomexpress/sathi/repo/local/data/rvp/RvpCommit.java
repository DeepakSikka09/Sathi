package in.ecomexpress.sathi.repo.local.data.rvp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RvpCommit implements Parcelable {

    @JsonProperty("fe_emp_code")
    private String feEmpCode;

    @JsonProperty("address_type")
    private String addressType;

    @JsonProperty("drs_id")
    private String drsId;

    @JsonProperty("location_lat")
    private String locationLat = "0.0";

    @JsonProperty("location_long")
    private String locationLong = "0.0";

    @JsonProperty("consignee_name")
    private String consigneeName;

    @JsonProperty("call_attempt_count")
    private int call_attempt_count;

    @JsonProperty("map_activity_count")
    private int map_activity_count;

    @JsonProperty("trying_reach")
    private String trying_reach;

    @JsonProperty("techpark")
    private String techpark;

    @JsonProperty("rvp_mps")
    private boolean rvp_mps;

    @JsonProperty("start_qc_lat")
    private String start_qc_lat;

    @JsonProperty("start_qc_lng")
    private String start_qc_lng;

    @JsonProperty("type")
    private String type;

    @JsonProperty("attempt_type")
    private String attemptType;

    @JsonProperty("drs_date")
    private String drsDate;

    @JsonProperty("awb")
    private String awb;

    @JsonProperty("status")
    private String status;

    @JsonProperty("attempt_reason_code")
    private String attemptReasonCode;

    @JsonProperty("drs_commit_date_time")
    private String drsCommitDateTime;

    @JsonProperty("total_item_number")
    private int total_item_number;

    @JsonProperty("packaging_barcode")
    private String packageBarcode;

    @JsonProperty("ref_packaging_barcode")
    private String refPackageBarcode;

    @JsonProperty("fe_comments")
    private String feComment;

    @Embedded
    @JsonProperty("fe_reschedule_info")
    private RescheduleInfo rescheduleInfo;

    @JsonProperty("ofd_otp_verify_status")
    private String ofd_otp_verify_status;

    @JsonProperty("is_otp_verified")
    private String is_otp_verified;

    @JsonProperty("trip_id")
    private String trip_id;

    @JsonProperty("received_by_name")
    private String received_by;

    @JsonProperty("location_verified")
    private boolean location_verified;

    @JsonProperty("flag_of_warning")
    private String flag_of_warning;

    public List<QcItem> getQcItem() {
        return qcItem;
    }

    public void setQcItem(List<QcItem> qcItem) {
        this.qcItem = qcItem;
    }
    public void setFlag_of_warning(String flag_of_warning) {
        this.flag_of_warning = flag_of_warning;
    }

    public String getFlag_of_warning() {
        return flag_of_warning;
    }

    public String getReceived_by_relation() {
        return received_by_relation;
    }

    public void setReceived_by_relation(String received_by_relation) {
        this.received_by_relation = received_by_relation;
    }

    @JsonProperty("received_by_relation")
    private String received_by_relation;

    @JsonProperty("image_response")
    private List<ImageData> imageData = new ArrayList<>();

    @JsonProperty("qc_wizard")
    private List<QcWizard> qcWizard = new ArrayList<>();

    @JsonProperty("qc_item")
    private List<QcItem> qcItem = new ArrayList<>();

    @JsonProperty("ud_otp_verify_status")
    private String ud_otp_verify_status;

    @JsonProperty("rchd_otp_verify_status")
    private String rd_otp_verify_status;
    @JsonProperty("ofd_otp_verified")
    private boolean ofd_otp_verified;

    @JsonProperty("ofd_customer_otp")
    private String ofd_customer_otp;

    public String getOfd_customer_otp() {
        return ofd_customer_otp;
    }

    public void setOfd_customer_otp(String ofd_customer_otp) {
        this.ofd_customer_otp = ofd_customer_otp;
    }

    public String getUd_otp_verify_status() {
        return ud_otp_verify_status;
    }

    public void setUd_otp_verify_status(String ud_otp_verify_status) {
        this.ud_otp_verify_status = ud_otp_verify_status;
    }

    public String getRd_otp_verify_status() {
        return rd_otp_verify_status;
    }

    public void setRd_otp_verify_status(String rd_otp_verify_status) {
        this.rd_otp_verify_status = rd_otp_verify_status;
    }

    public boolean getOfd_otp_verified() {
        return ofd_otp_verified;
    }

    public void setOfd_otp_verified(boolean ofd_otp_verified) {
        this.ofd_otp_verified = ofd_otp_verified;
    }

    public String getFeEmpCode() {
        return feEmpCode;
    }

    public void setFeEmpCode(String feEmpCode) {
        this.feEmpCode = feEmpCode;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getDrsId() {
        return drsId;
    }

    public void setDrsId(String drsId) {
        this.drsId = drsId;
    }

    public String getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(String locationLat) {
        this.locationLat = locationLat;
    }

    public String getLocationLong() {
        return locationLong;
    }

    public void setLocationLong(String locationLong) {
        this.locationLong = locationLong;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(String attemptType) {
        this.attemptType = attemptType;
    }

    public String getDrsDate() {
        return drsDate;
    }

    public void setDrsDate(String drsDate) {
        this.drsDate = drsDate;
    }

    public String getAwb() {
        return awb;
    }

    public void setAwb(String awb) {
        this.awb = awb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RescheduleInfo getRescheduleInfo() {
        return rescheduleInfo;
    }

    public void setRescheduleInfo(RescheduleInfo rescheduleInfo) {
        this.rescheduleInfo = rescheduleInfo;
    }

    public String getAttemptReasonCode() {
        return attemptReasonCode;
    }

    public void setAttemptReasonCode(String attemptReasonCode) {
        this.attemptReasonCode = attemptReasonCode;
    }

    public String getDrsCommitDateTime() {
        return drsCommitDateTime;
    }

    public void setDrsCommitDateTime(String drsCommitDateTime) {
        this.drsCommitDateTime = drsCommitDateTime;
    }

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getRefPackageBarcode() {
        return refPackageBarcode;
    }

    public void setRefPackageBarcode(String refPackageBarcode) {
        this.refPackageBarcode = refPackageBarcode;
    }

    public String getFeComment() {
        return feComment;
    }

    public void setFeComment(String feComment) {
        this.feComment = feComment;
    }


    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getReceived_by() {
        return received_by;
    }

    public void setReceived_by(String received_by) {
        this.received_by = received_by;
    }

    public List<ImageData> getImageData() {
        return imageData;
    }

    public void setImageData(List<ImageData> imageData) {
        this.imageData = imageData;
    }

    public List<QcWizard> getQcWizard() {
        return qcWizard;
    }

    public void setQcWizard(List<QcWizard> qcWizard) {
        this.qcWizard = qcWizard;
    }

    public static Creator<RvpCommit> getCREATOR() {
        return CREATOR;
    }

    public RvpCommit() {
    }

    @JsonProperty("location_verified")
    public void setLocation_verified(boolean location_verified) {
        this.location_verified = location_verified;
    }

    @JsonProperty("location_verified")
    public boolean isLocation_verified() {
        return location_verified;
    }

    public int getCall_attempt_count() {
        return call_attempt_count;
    }

    public void setCall_attempt_count(int call_attempt_count) {
        this.call_attempt_count = call_attempt_count;
    }

    public int getMap_activity_count() {
        return map_activity_count;
    }

    public void setMap_activity_count(int map_activity_count) {
        this.map_activity_count = map_activity_count;
    }

    public String getOfd_otp_verify_status() {
        return ofd_otp_verify_status;
    }

    public void setOfd_otp_verify_status(String ofd_otp_verify_status) {
        this.ofd_otp_verify_status = ofd_otp_verify_status;
    }

    public String getStart_qc_lat() {
        return start_qc_lat;
    }

    public void setStart_qc_lat(String start_qc_lat) {
        this.start_qc_lat = start_qc_lat;
    }

    public String getStart_qc_lng() {
        return start_qc_lng;
    }

    public void setStart_qc_lng(String start_qc_lng) {
        this.start_qc_lng = start_qc_lng;
    }

    public String getIs_otp_verified() {
        return is_otp_verified;
    }

    public void setIs_otp_verified(String is_otp_verified) {
        this.is_otp_verified = is_otp_verified;
    }

    public String getTrying_reach() {
        return trying_reach;
    }

    public void setTrying_reach(String trying_reach) {
        this.trying_reach = trying_reach;
    }

    public String getTechpark() {
        return techpark;
    }

    public void setTechpark(String techpark) {
        this.techpark = techpark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRvp_mps() {
        return rvp_mps;
    }

    public void setRvp_mps(boolean rvp_mps) {
        this.rvp_mps = rvp_mps;
    }

    public int getTotal_item_number() {
        return total_item_number;
    }

    public void setTotal_item_number(int total_item_number) {
        this.total_item_number = total_item_number;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"item_number", "qc_wizard", "image_response"})
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QcItem implements Parcelable {
        @JsonProperty("item_number")
        private String itemNumber;
        @JsonProperty("status")
        private String status = "NA";

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @JsonProperty("qc_wizard")
        private List<QcWizard> qcWizard;

        public String getItemNumber() {
            return itemNumber;
        }

        public void setItemNumber(String itemNumber) {
            this.itemNumber = itemNumber;
        }

        public List<ImageData> getImageData() {
            return imageData;
        }

        public void setImageData(List<ImageData> imageData) {
            this.imageData = imageData;
        }

        @JsonProperty("image_response")
        private List<ImageData> imageData;

        public QcItem() {

        }

        public QcItem(Parcel in) {
            itemNumber = in.readString();
            status = in.readString();
            qcWizard = in.createTypedArrayList(QcWizard.CREATOR);
            imageData = in.createTypedArrayList(ImageData.CREATOR);
        }

        public static final Creator<QcItem> CREATOR = new Creator<QcItem>() {
            @Override
            public QcItem createFromParcel(Parcel in) {
                return new QcItem(in);
            }

            @Override
            public QcItem[] newArray(int size) {
                return new QcItem[size];
            }
        };

        public List<QcWizard> getQcWizard() {
            return qcWizard;
        }

        public void setQcWizard(List<QcWizard> qcWizard) {
            this.qcWizard = qcWizard;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeString(itemNumber);
            parcel.writeString(status);
            parcel.writeTypedList(qcWizard);
            parcel.writeTypedList(imageData);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "reschedule_date",
            "reschedule_slot",
            "reschedule_fe_comments"
    })
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RescheduleInfo implements Parcelable {

        @JsonProperty("reschedule_date")
        private String rescheduleDate = null;
        @JsonProperty("reschedule_slot")
        private String rescheduleSlot = null;
        @JsonProperty("reschedule_fe_comments")
        private String rescheduleFeComments = null;

        public String getRescheduleDate() {
            return rescheduleDate;
        }

        public void setRescheduleDate(String rescheduleDate) {
            this.rescheduleDate = rescheduleDate;
        }

        public String getRescheduleSlot() {
            return rescheduleSlot;
        }

        public void setRescheduleSlot(String rescheduleSlot) {
            this.rescheduleSlot = rescheduleSlot;
        }

        public String getRescheduleFeComments() {
            return rescheduleFeComments;
        }

        public void setRescheduleFeComments(String rescheduleFeComments) {
            this.rescheduleFeComments = rescheduleFeComments;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.rescheduleDate);
            dest.writeString(this.rescheduleSlot);
            dest.writeString(this.rescheduleFeComments);
        }

        public RescheduleInfo() {
        }

        protected RescheduleInfo(Parcel in) {
            this.rescheduleDate = in.readString();
            this.rescheduleSlot = in.readString();
            this.rescheduleFeComments = in.readString();
        }

        public static final Creator<RescheduleInfo> CREATOR = new Creator<>() {
            @Override
            public RescheduleInfo createFromParcel(Parcel source) {
                return new RescheduleInfo(source);
            }

            @Override
            public RescheduleInfo[] newArray(int size) {
                return new RescheduleInfo[size];
            }
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "image_key",
            "image_uri"
    })

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageData implements Parcelable {

        @JsonProperty("image_key")
        private String imageKey;

        @JsonProperty("image_id")
        private String imageId;

        public String getImageKey() {
            return imageKey;
        }

        public void setImageKey(String imageKey) {
            this.imageKey = imageKey;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public ImageData() {
        }

        public ImageData(String imageKey, String imageId) {
            this.imageKey = imageKey;
            this.imageId = imageId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.imageKey);
            dest.writeString(this.imageId);
        }

        protected ImageData(Parcel in) {
            this.imageKey = in.readString();
            this.imageId = in.readString();
        }

        public static final Creator<ImageData> CREATOR = new Creator<ImageData>() {
            @Override
            public ImageData createFromParcel(Parcel source) {
                return new ImageData(source);
            }

            @Override
            public ImageData[] newArray(int size) {
                return new ImageData[size];
            }
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "qccheckcode",
            "remarks",
            "match",
            "qc_value",
            "image",
            "s3_image_key",
            "s3_image_uri"
    })

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QcWizard implements Parcelable {

        private String item_number;

        public String getItem_number() {
            return item_number;
        }

        public void setItem_number(String item_number) {
            this.item_number = item_number;
        }

        @JsonProperty("rvp_qc_code")
        private String qccheckcode;

        @JsonProperty("fe_remarks")
        private String remarks;

        @JsonProperty("qc_check_status")
        private String match;

        @JsonProperty("qc_result_value")
        private String qcValue;

        @JsonProperty("qc_result_name")
        private String qcName;

        @JsonProperty("qc_image_upload")
        private boolean qcImageFlag = false;
        private String imagePathWithWaterMark = null;

        public String getImagePathWithWaterMark() {
            return imagePathWithWaterMark;
        }

        public void setImagePathWithWaterMark(String imagePathWithWaterMark) {
            this.imagePathWithWaterMark = imagePathWithWaterMark;
        }

        public boolean isQcImageFlag() {
            return qcImageFlag;
        }

        public void setQcImageFlag(boolean qcImageFlag) {
            this.qcImageFlag = qcImageFlag;
        }

        public String getQcName() {
            return qcName;
        }

        public void setQcName(String qcName) {
            this.qcName = qcName;
        }

        public String getQccheckcode() {
            return qccheckcode;
        }

        public void setQccheckcode(String qccheckcode) {
            this.qccheckcode = qccheckcode;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getMatch() {
            return match;
        }

        public void setMatch(String match) {
            this.match = match;
        }

        public String getQcValue() {
            return qcValue;
        }

        public void setQcValue(String qcValue) {
            this.qcValue = qcValue;
        }

        public QcWizard() {
        }

        @NonNull
        @Override
        public String toString() {
            return "QcWizard{" +
                    "qccheckcode='" + qccheckcode + '\'' +
                    ", remarks='" + remarks + '\'' +
                    ", match='" + match + '\'' +
                    ", qcValue='" + qcValue + '\'' +
                    ", qcName='" + qcName + '\'' +
                    ", qcImageFlag='" + qcImageFlag + '\'' +
                    ", imageBitmap='" + imagePathWithWaterMark + '\'' +
                    ", item_number='" + item_number + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.qccheckcode);
            dest.writeString(this.remarks);
            dest.writeString(this.match);
            dest.writeString(this.qcValue);
            dest.writeString(this.qcName);
            dest.writeValue(this.qcImageFlag);
            dest.writeString(this.imagePathWithWaterMark);
            dest.writeString(this.item_number);
        }

        protected QcWizard(Parcel in) {
            this.qccheckcode = in.readString();
            this.remarks = in.readString();
            this.match = in.readString();
            this.qcValue = in.readString();
            this.qcName = in.readString();
            this.qcImageFlag = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.imagePathWithWaterMark = in.readString();
            this.item_number = in.readString();
        }

        public static final Creator<QcWizard> CREATOR = new Creator<QcWizard>() {
            @Override
            public QcWizard createFromParcel(Parcel source) {
                return new QcWizard(source);
            }

            @Override
            public QcWizard[] newArray(int size) {
                return new QcWizard[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.feEmpCode);
        dest.writeString(this.addressType);
        dest.writeString(this.drsId);
        dest.writeString(this.locationLat);
        dest.writeString(this.locationLong);
        dest.writeString(this.consigneeName);
        dest.writeString(this.attemptType);
        dest.writeString(this.drsDate);
        dest.writeString(this.awb);
        dest.writeString(this.status);
        dest.writeString(this.attemptReasonCode);
        dest.writeString(this.drsCommitDateTime);
        dest.writeString(this.packageBarcode);
        dest.writeString(String.valueOf(this.total_item_number));
        dest.writeString(this.refPackageBarcode);
        dest.writeString(this.feComment);
        dest.writeParcelable(this.rescheduleInfo, flags);
        dest.writeString(this.trip_id);
        dest.writeString(this.received_by);
        dest.writeString(this.received_by_relation);
        dest.writeString(this.flag_of_warning);
        dest.writeTypedList(this.imageData);
        dest.writeTypedList(this.qcWizard);
        dest.writeInt(this.call_attempt_count);
        dest.writeInt(this.map_activity_count);
        dest.writeString(this.ofd_otp_verify_status);
        dest.writeString(this.start_qc_lat);
        dest.writeString(this.start_qc_lng);
        dest.writeString(this.is_otp_verified);
    }

    protected RvpCommit(Parcel in) {
        this.feEmpCode = in.readString();
        this.addressType = in.readString();
        this.drsId = in.readString();
        this.locationLat = in.readString();
        this.locationLong = in.readString();
        this.consigneeName = in.readString();
        this.attemptType = in.readString();
        this.drsDate = in.readString();
        this.awb = in.readString();
        this.status = in.readString();
        this.attemptReasonCode = in.readString();
        this.drsCommitDateTime = in.readString();
        this.packageBarcode = in.readString();
        this.refPackageBarcode = in.readString();
        this.feComment = in.readString();
        this.rescheduleInfo = in.readParcelable(RescheduleInfo.class.getClassLoader());
        this.trip_id = in.readString();
        this.received_by = in.readString();
        this.received_by_relation = in.readString();
        this.flag_of_warning = in.readString();
        this.imageData = in.createTypedArrayList(ImageData.CREATOR);
        this.qcWizard = in.createTypedArrayList(QcWizard.CREATOR);
        this.call_attempt_count = in.readInt();
        this.map_activity_count = in.readInt();
        this.ofd_otp_verify_status = in.readString();
        this.start_qc_lat = in.readString();
        this.start_qc_lng = in.readString();
        this.is_otp_verified = in.readString();
    }

    public static final Creator<RvpCommit> CREATOR = new Creator<RvpCommit>() {
        @Override
        public RvpCommit createFromParcel(Parcel source) {
            return new RvpCommit(source);
        }

        @Override
        public RvpCommit[] newArray(int size) {
            return new RvpCommit[size];
        }
    };
}