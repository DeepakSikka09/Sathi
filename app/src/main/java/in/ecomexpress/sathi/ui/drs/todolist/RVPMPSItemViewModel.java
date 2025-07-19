package in.ecomexpress.sathi.ui.drs.todolist;

import androidx.databinding.ObservableField;

import java.util.List;

import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.remote.model.mps.QcItem;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.ShipmentUtils;

public class RVPMPSItemViewModel {
    private final ObservableField<Boolean> indicator = new ObservableField<>();
    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;

    public RVPMPSItemViewModel(CommonDRSListItem commonDRSListItem, DRSItemViewModelListener drsItemViewModelListener) {
        this.mCommonDRSListItem = commonDRSListItem;
        this.drsItemViewModelListener = drsItemViewModelListener;
    }

    public void setImage(Boolean image) {
        this.indicator.set(image);
    }

    public ObservableField<Boolean> getIndicator() {
        return indicator;
    }

    public void onIndicatorClick() {
        drsItemViewModelListener.onIndicatorClick(mCommonDRSListItem);
    }

    public void onItemClick() {
        drsItemViewModelListener.onItemClick(mCommonDRSListItem);
    }

    public void onMapClick() {
        drsItemViewModelListener.onMapClick(mCommonDRSListItem);
    }

    public void onCallClick() {
        drsItemViewModelListener.onCallClick(mCommonDRSListItem);
    }


    public String getName() {
        return mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getName();
    }

    public String getAddress() {
        try {
            String getaddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine2()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine3()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine4()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getCity()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getState()) +
                    ", " + CommonUtils.nullToEmpty(String.valueOf(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getPincode()));


            String finalAddress1 = getaddress.replaceAll(", ,", ",");
            String finalAddress = finalAddress1.replaceAll(",,", ",");

            return finalAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO Address Defined.";
    }

    public String addressline1() {
        try {
            String getaddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine1());
            return getaddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO Address Defined.";
    }

    public String shipper() {
        try {
            return mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getShipper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO Shipper Difined";

    }

    public String getAssingnedDate() {
        return CommonUtils.getTimeStampToDate(mCommonDRSListItem.getDrsRvpQcMpsResponse().getAssignedDate());
    }

    public String getPaymentMode() {

        return mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getShipperCode();
    }

    public String getShipmentType() {
        try {
            return mCommonDRSListItem.getType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO ShipmentType Defined";
    }

    public String getAwbNo() {
        try {
            return mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NO AWB Defined";
    }

    public String getStatus() {

        return ShipmentUtils.getShipmentStatus(mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus());

    }

    public void onTrayClick() {
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }

    public String getSlot() {
        try {
            long start_time = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getSlot_details().getStart_time();
            long end_time = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getSlot_details().getEnd_time();
            if (start_time == 0 && end_time == 0) {
                if (mCommonDRSListItem.getProfileFound().getTime_slot() != null && !mCommonDRSListItem.getProfileFound().getTime_slot().isEmpty()) {
                    return mCommonDRSListItem.getProfileFound().getTime_slot();
                } else {
                    return "";
                }
            } else {
                return CommonUtils.millisecondToAmPm(start_time) + " - " + CommonUtils.millisecondToAmPm(end_time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPin() {
        try {
            String pin = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getPin();
            return pin != null && !pin.isEmpty() ? "PIN: " + pin : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean getRVPType() {
        List<QcItem> list = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getQcItems();
        return list != null && list.size() > 0;

    }

    public boolean isItemSynced() {
        try {
            return mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentSyncStatus() == 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

