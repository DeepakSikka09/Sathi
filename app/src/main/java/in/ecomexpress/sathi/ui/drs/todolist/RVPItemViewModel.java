package in.ecomexpress.sathi.ui.drs.todolist;

import androidx.databinding.ObservableField;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.ShipmentUtils;

public class RVPItemViewModel {

    private static final String TAG = RVPItemViewModel.class.getSimpleName();
    private final ObservableField<Boolean> indicator = new ObservableField<>();
    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;

    public RVPItemViewModel(CommonDRSListItem commonDRSListItem, DRSItemViewModelListener drsItemViewModelListener) {
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
        return mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getName();
    }

    public String getAddress() {
        try {
            String getAddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine2()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine3()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine4()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getCity()) +
                    ", " + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getState()) +
                    ", " + CommonUtils.nullToEmpty(String.valueOf(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getPincode()));
            String finalAddress1 = getAddress.replaceAll(", ,", ",");
            return finalAddress1.replaceAll(",,", ",");
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No Address Defined.";
    }

    public String addressline1() {
        try {
            return CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine1());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No Address Defined.";
    }

    public String shipper() {
        try {
            return mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getShipper();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No Shipper Defined";

    }

    public String getShipmentType() {
        try {
            return mCommonDRSListItem.getType();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No ShipmentType Defined";
    }

    public String getAwbNo() {
        try {
            return mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No AWB Defined";
    }

    public String getStatus() {
        return ShipmentUtils.getShipmentStatus(mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus());
    }

    public void onTrayClick() {
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }

    public String getSlot() {
        try {
            long start_time = mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getSlot_details().getStart_time();
            long end_time = mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getSlot_details().getEnd_time();
            if (start_time == 0 && end_time == 0) {
                if (mCommonDRSListItem.getProfileFound() != null && mCommonDRSListItem.getProfileFound().getTime_slot() != null && !mCommonDRSListItem.getProfileFound().getTime_slot().isEmpty()) {
                    return mCommonDRSListItem.getProfileFound().getTime_slot();
                } else {
                    return "";
                }
            } else {
                return CommonUtils.millisecondToAmPm(start_time) + " - " + CommonUtils.millisecondToAmPm(end_time);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
            return "";
        }
    }

    public String getPin() {
        try {
            if (mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details()!=null) {
                String pin = String.valueOf(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin());
                return pin != null && !pin.isEmpty() ? "PIN: " + pin : "";
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public boolean isItemSynced() {
        try {
            return mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentSyncStatus() == 2;
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return false;
    }
}