package in.ecomexpress.sathi.repo.local.db.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.DRSReturnToShipperTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.TimeUtils;

public class CommonMapDrsListItem implements Serializable {

    private final String TAG = CommonMapDrsListItem.class.getName();
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
            new NullPointerException("Remark can not be null");
        }
        if (remark == null) {
            remark = new Remark();
            if (Type == GlobalConstant.ShipmentTypeConstants.FWD) {
                remark.awbNo = drsForwardTypeResponse.getAwbNo();
            } else if (Type == GlobalConstant.ShipmentTypeConstants.EDS) {
                remark.awbNo = edsResponse.getAwbNo();
            } else if (Type == GlobalConstant.ShipmentTypeConstants.RTS) {
                remark.awbNo = irtsBaseInterface.getDetails().getId();
            } else if (Type == GlobalConstant.ShipmentTypeConstants.RVP) {
                remark.awbNo = drsReverseQCTypeResponse.getAwbNo();
            } else {
                return remark;
            }
            remark.empCode = EmpCode + "";
            remark.sync_status = GlobalConstant.ShipmentStatusConstants.ASSIGNED_INT;
            remark.date = TimeUtils.getDateYearMonthMillies();
            remark.count = count;
        }
        remark.remark = remarkStr;
        Logger.e(TAG, "remark: " + remark.toString());
        return remark;
    }

    public void setRemark(Remark remark) {
        this.remark = remark;
    }


    private DRSForwardTypeResponse drsForwardTypeResponse;
    private DRSReverseQCTypeResponse drsReverseQCTypeResponse;
    private DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse;
    private IRTSBaseInterface irtsBaseInterface;
    private EDSResponse edsResponse;


    public CommonMapDrsListItem() {
    }

    public CommonMapDrsListItem(String type, DRSForwardTypeResponse drsForwardTypeResponse) {
        this.Type = type;
        this.drsForwardTypeResponse = drsForwardTypeResponse;
        this.commonDrsStatus = drsForwardTypeResponse.getShipmentStatus();
        this.distance = drsForwardTypeResponse.getDistance();
    }

    public CommonMapDrsListItem(String type, DRSReverseQCTypeResponse drsReverseQCTypeResponse) {
        this.Type = type;
        this.drsReverseQCTypeResponse = drsReverseQCTypeResponse;
        this.commonDrsStatus = drsReverseQCTypeResponse.getShipmentStatus();
        this.distance = drsReverseQCTypeResponse.getDistance();
    }

    public CommonMapDrsListItem(String type, DRSReturnToShipperTypeResponse drsReturnToShipperTypeResponse) {
        this.Type = type;
        this.drsReturnToShipperTypeResponse = drsReturnToShipperTypeResponse;
        this.commonDrsStatus = drsReturnToShipperTypeResponse.getShipmentStatus();
        this.distance = drsReturnToShipperTypeResponse.getDistance();
    }

    public CommonMapDrsListItem(String type, IRTSBaseInterface irtsBaseInterface) {
        this.Type = type;
        this.irtsBaseInterface = irtsBaseInterface;
        this.commonDrsStatus = irtsBaseInterface.getDetails().getShipmentStatus();
        this.distance = irtsBaseInterface.getDistance();
    }

    public CommonMapDrsListItem(String type, EDSResponse edsResponse) {
        this.Type = type;
        this.edsResponse = edsResponse;
        this.commonDrsStatus = edsResponse.getShipmentStatus();
    }
//    public CommonDRSListItem(String type, RescheduleEdsD edsRescheduleResponse) {
//        this.Type = type;
//        this.rescheduleEdsD = edsRescheduleResponse;
//    }


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

//    public RescheduleEdsD getEdsRescheduleResponse() {
//        return rescheduleEdsD;
//    }
//
//
//    public void setEdsRescheduleResponse(RescheduleEdsD edsRescheduleResponse) {
//        this.rescheduleEdsD = edsRescheduleResponse;
//    }

    @Override
    public String toString() {
        return "CommonMapDrsListItem{" +
                "TAG='" + TAG + '\'' +
                ", Type='" + Type + '\'' +
                ", commonDrsStatus=" + commonDrsStatus +
                ", distance=" + distance +
                ", remark=" + remark +
                ", drsForwardTypeResponse=" + drsForwardTypeResponse +
                ", drsReverseQCTypeResponse=" + drsReverseQCTypeResponse +
                ", drsReturnToShipperTypeResponse=" + drsReturnToShipperTypeResponse +
                ", irtsBaseInterface=" + irtsBaseInterface +
                ", edsResponse=" + edsResponse +
                '}';
    }
    public static Comparator<CommonMapDrsListItem> seqNameComparator = new Comparator<CommonMapDrsListItem>() {

        public int compare(CommonMapDrsListItem sequence1, CommonMapDrsListItem sequence2) {

            String seq1 = sequence1.Type;
            String seq2=sequence2.Type;
            int seqout1,seqout2;
            switch(seq1)
            {
                case "FWD":
                    seqout1=sequence1.getDrsForwardTypeResponse().getSequenceNo();
                    System.out.println("one");
                    break;
                case "RVP":
                    seqout1=sequence1.getDrsReverseQCTypeResponse().getSequenceNo();
                    break;
                case "EDS":
                    seqout1=sequence1.getEdsResponse().getSequenceNo();
                    break;
                default:
                    seqout1=0;
            }
            switch(seq2)
            {
                case "FWD":
                    seqout2=sequence2.getDrsForwardTypeResponse().getSequenceNo();
                    break;
                case "RVP":
                    seqout2=sequence2.getDrsReverseQCTypeResponse().getSequenceNo();
                    break;
                case "EDS":
                    seqout2=sequence2.getEdsResponse().getSequenceNo();
                    break;
                default:
                    seqout2=0;
            }
            //ascending order
            return seqout1-seqout2;
            // if(sequence1!=null&sequence2!=n)){
//            int sequenceNo = sequence1.getDrsForwardTypeResponse().getSequenceNo();
//            int sequenceNo1 = sequence2.getDrsForwardTypeResponse().getSequenceNo();
//            return sequenceNo-sequenceNo1;
//            }
//            if(sequence1.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)){
//                int sequenceNo = sequence1.getDrsReverseQCTypeResponse().getSequenceNo();
//                int sequenceNo1 = sequence2.getDrsReverseQCTypeResponse().getSequenceNo();
//                return sequenceNo-sequenceNo1;
//            }
//            if(sequence1.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)){
//                int sequenceNo = sequence1.getEdsResponse().getSequenceNo();
//                int sequenceNo1 = sequence2.getEdsResponse().getSequenceNo();
//                return sequenceNo-sequenceNo1;
//            }

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};
//    @Override
//    public int compareTo(@NonNull CommonDRSListItem commonDRSListItem) {
//        return this.commonDrsStatus - commonDRSListItem.commonDrsStatus;
//    }

    public static Comparator<CommonMapDrsListItem> DISTANCE = (CommonMapDrsListItem, t1) -> CommonMapDrsListItem.distance >= t1.distance ? 1 : 0;

    public String filterValue() {
        return (drsReverseQCTypeResponse != null ? drsReverseQCTypeResponse.filterValue() : "")
                + (drsForwardTypeResponse != null ? drsForwardTypeResponse.filterValue() : "")
                + (irtsBaseInterface != null ? irtsBaseInterface.filterValue() : "")
                + (edsResponse != null ? edsResponse.filterValue() : "");

    }


}
