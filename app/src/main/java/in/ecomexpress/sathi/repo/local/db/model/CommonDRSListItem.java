package in.ecomexpress.sathi.repo.local.db.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.TimeUtils;

public class CommonDRSListItem extends CommonMapDrsListItem implements Serializable {
    private final String TAG = CommonDRSListItem.class.getName();
    private String Type;
    private int commonDrsStatus;
    private double distance = 0;

    public boolean isSmsCheckFlag() {
        return smsCheckFlag;
    }

    public void setSmsCheckFlag(boolean smsCheckFlag) {
        this.smsCheckFlag = smsCheckFlag;
    }

    private boolean smsCheckFlag = false;

    private boolean isNewDRSAfterSync = false;

    public boolean isNewDRSAfterSync() {
        return isNewDRSAfterSync;
    }

    public void setNewDRSAfterSync(boolean newDRSAfterSync) {
        isNewDRSAfterSync = newDRSAfterSync;
    }

    public ProfileFound getProfileFound() {
        return profileFound;
    }

    public List<RescheduleEdsD> getRescheduleFlagFound() {
        return rescheduleEdsD;
    }

    public void setRescheduleFlagFound(List<RescheduleEdsD> rescheduleFlagFound) {
        this.rescheduleEdsD = rescheduleFlagFound;
    }

    public void setProfileFound(ProfileFound profileFound) {
        this.profileFound = profileFound;
    }

    private ProfileFound profileFound;
    private List<RescheduleEdsD> rescheduleEdsD;
    private Remark remark;

    public Remark getRemark() {
        return remark;
    }

    public Remark setRemark(String remarkStr, String EmpCode, int count) {
        if (remarkStr == null) {
            throw new NullPointerException("Remark can not be null");
        }
        if (remark == null) {
            remark = new Remark();
            if (Objects.equals(Type, GlobalConstant.ShipmentTypeConstants.FWD)) {
                remark.awbNo = drsForwardTypeResponse.getAwbNo();
            } else if (Objects.equals(Type, GlobalConstant.ShipmentTypeConstants.EDS)) {
                remark.awbNo = edsResponse.getAwbNo();
            } else if (Objects.equals(Type, GlobalConstant.ShipmentTypeConstants.RTS)) {
                remark.awbNo = irtsBaseInterface.getDetails().getId();
            } else if (Objects.equals(Type, GlobalConstant.ShipmentTypeConstants.RVP)) {
                remark.awbNo = drsReverseQCTypeResponse.getAwbNo();
            } else if (Objects.equals(Type, GlobalConstant.ShipmentTypeConstants.RVP_MPS)) {
                remark.awbNo = drsRvpQcMpsResponse.getAwbNo();
            } else {
                return remark;
            }
            remark.empCode = EmpCode;
            remark.sync_status = GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT;
        }
        remark.remark = remarkStr;
        remark.count = count;
        remark.date = TimeUtils.getDateYearMonthMillies();
        Logger.e(TAG, "remark: " + remark.toString());
        return remark;
    }

    public void setRemark(Remark remark) {
        this.remark = remark;
    }

    private DRSForwardTypeResponse drsForwardTypeResponse;
    private DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    private DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse;

    private DRSRvpQcMpsResponse drsRvpQcMpsResponse;

    public DRSRvpQcMpsResponse getDrsRvpQcMpsResponse() {
        return drsRvpQcMpsResponse;
    }

    public void setDrsRvpQcMpsResponse(DRSRvpQcMpsResponse drsRvpQcMpsResponse) {
        this.drsRvpQcMpsResponse = drsRvpQcMpsResponse;
    }

    private IRTSBaseInterface irtsBaseInterface;
    private EDSResponse edsResponse;

    public CommonDRSListItem() {
    }

    public CommonDRSListItem(String type, DRSForwardTypeResponse drsForwardTypeResponse) {
        this.Type = type;
        this.drsForwardTypeResponse = drsForwardTypeResponse;
        this.commonDrsStatus = drsForwardTypeResponse.getShipmentStatus();
        this.distance = drsForwardTypeResponse.getDistance();
    }

    public CommonDRSListItem(String type, DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        this.Type = type;
        this.drsReverseQCTypeResponse = drsReverseQCTypeResponse;
        this.commonDrsStatus = drsReverseQCTypeResponse.getShipmentStatus();
        this.distance = drsReverseQCTypeResponse.getDistance();
    }

    public CommonDRSListItem(String type, DRSRvpQcMpsResponse drsRvpQcMpsResponse) {
        this.Type = type;
        this.drsRvpQcMpsResponse = drsRvpQcMpsResponse;
        this.commonDrsStatus = drsRvpQcMpsResponse.getShipmentStatus();
        this.distance = drsRvpQcMpsResponse.getDistance();
    }

    public CommonDRSListItem(String type, DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse) {
        this.Type = type;
        this.drsReturnToShipperTypeResponse = drsReturnToShipperTypeResponse;
        this.commonDrsStatus = drsReturnToShipperTypeResponse.getShipmentStatus();
        this.distance = drsReturnToShipperTypeResponse.getDistance();
    }

    public CommonDRSListItem(String type, IRTSBaseInterface irtsBaseInterface) {
        this.Type = type;
        this.irtsBaseInterface = irtsBaseInterface;
        this.commonDrsStatus = irtsBaseInterface.getDetails().getShipmentStatus();
        this.distance = irtsBaseInterface.getDistance();
    }

    public CommonDRSListItem(String type, EDSResponse edsResponse) {
        this.Type = type;
        this.edsResponse = edsResponse;
        this.commonDrsStatus = edsResponse.getShipmentStatus();
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCommonDrsStatus() {
        return commonDrsStatus;
    }

    public void setCommonDrsStatus(int commonDrsStatus) {
        this.commonDrsStatus = commonDrsStatus;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public DRSForwardTypeResponse getDrsForwardTypeResponse() {
        return drsForwardTypeResponse;
    }

    public void setDrsForwardTypeResponse(DRSForwardTypeResponse drsForwardTypeResponse) {
        this.drsForwardTypeResponse = drsForwardTypeResponse;
    }

    public DRSReverseQCTypeResponse getDrsReverseQCTypeResponse() {
        return drsReverseQCTypeResponse;
    }

    public void setDrsReverseQCTypeResponse(DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        this.drsReverseQCTypeResponse = drsReverseQCTypeResponse;
    }

    public DRSReturnToShipperTypeResponse getDrsReturnToShipperTypeResponse() {
        return drsReturnToShipperTypeResponse;
    }

    public IRTSBaseInterface getIRTSInterface() {
        return irtsBaseInterface;
    }

    public EDSResponse getEdsResponse() {
        return edsResponse;
    }

    public void setEdsResponse(EDSResponse edsResponse) {
        this.edsResponse = edsResponse;
    }

    @NonNull
    @Override
    public String toString() {
        return "CommonDRSListItem{" + "TAG='" + TAG + '\'' + ", Type='" + Type + '\'' + ", commonDrsStatus=" + commonDrsStatus + ", distance=" + distance + ", remark=" + remark + ", drsForwardTypeResponse=" + drsForwardTypeResponse + ", drsReverseQCTypeResponse=" + drsReverseQCTypeResponse + ", drsReturnToShipperTypeResponse=" + drsReturnToShipperTypeResponse + ", irtsBaseInterface=" + irtsBaseInterface + ", edsResponse=" + edsResponse + '}';
    }

    public static Comparator<CommonDRSListItem> seqNameComparator = (sequence1, sequence2) -> {
        String seq1 = sequence1.Type;
        String seq2 = sequence2.Type;
        int seqout1, seqout2;
        switch (seq1) {
            case "FWD":
                seqout1 = sequence1.getDrsForwardTypeResponse().getMap_sequence_no();
                break;
            case "RVP":
                seqout1 = sequence1.getDrsReverseQCTypeResponse().getMap_sequence_no();
                break;
            case "EDS":
                seqout1 = sequence1.getEdsResponse().getMap_sequence_no();
                break;
            case "RVP_MPS":
                seqout1 = sequence1.getDrsRvpQcMpsResponse().getMap_sequence_no();
                break;
            default:
                seqout1 = 0;
        }
        switch (seq2) {
            case "FWD":
                seqout2 = sequence2.getDrsForwardTypeResponse().getMap_sequence_no();
                break;
            case "RVP":
                seqout2 = sequence2.getDrsReverseQCTypeResponse().getMap_sequence_no();
                break;
            case "RVP_MPS":
                seqout2 = sequence2.getDrsRvpQcMpsResponse().getMap_sequence_no();
                break;
            case "EDS":
                seqout2 = sequence2.getEdsResponse().getMap_sequence_no();
                break;
            default:
                seqout2 = 0;
        }
        return seqout1 - seqout2;
    };

    public String filterValue() {
        return (drsReverseQCTypeResponse != null ? drsReverseQCTypeResponse.filterValue() : "") + (drsForwardTypeResponse != null ? drsForwardTypeResponse.filterValue() : "") + (irtsBaseInterface != null ? irtsBaseInterface.filterValue() : "") + (edsResponse != null ? edsResponse.filterValue() : "");
    }
}
