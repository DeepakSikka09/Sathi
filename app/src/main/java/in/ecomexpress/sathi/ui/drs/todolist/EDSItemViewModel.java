package in.ecomexpress.sathi.ui.drs.todolist;

import androidx.databinding.ObservableField;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.ShipmentUtils;

public class EDSItemViewModel {

    private static final String TAG = EDSItemViewModel.class.getSimpleName();
    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;

    public ObservableField<Boolean> getIndicator() {
        return indicator;
    }

    private final ObservableField<Boolean> indicator = new ObservableField<>();

    public void setImage(Boolean image) {
        this.indicator.set(image);
    }

    public void onIndicatorClick() {
        drsItemViewModelListener.onIndicatorClick(mCommonDRSListItem);
    }

    public EDSItemViewModel(CommonDRSListItem commonDRSListItem, DRSItemViewModelListener drsItemViewModelListener) {
        this.mCommonDRSListItem = commonDRSListItem;
        this.drsItemViewModelListener = drsItemViewModelListener;
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

    public void onTrayClick() {
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }

    public String getName() {
        try {
            return mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getName();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getAddress() {
        try {
            String getAddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine2()) + " "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine3()) + " "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine4()) + " "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getCity()) + ", "
                    + mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getPincode();
            String finalAddress1 = getAddress.replaceAll(", ,", ",");
            return finalAddress1.replaceAll(",,", ",");
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String addressline1() {
        try {
            return CommonUtils.nullToEmpty(mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine1());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String itemDesc() {
        try {
            return "Activity: " +mCommonDRSListItem.getEdsResponse().getShipmentDetail().getItemDescription();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";

    }

    public String shipper() {
        try {
            return mCommonDRSListItem.getEdsResponse().getShipmentDetail().getCustomerName();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getPin() {
        try {
            //return "PIN: " + mCommonDRSListItem.getEdsResponse().getShipmentDetail().getPin();
            if (mCommonDRSListItem.getEdsResponse().getCallbridge_details()!=null)
            {
                String pin = String.valueOf(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin());
                pin = (pin != null && !pin.isEmpty()) ? pin : "";
                pin = "PIN: " + pin;
                return pin;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getShipmentType() {
        try {
            return mCommonDRSListItem.getType();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getAwbNo() {
        try {
            return mCommonDRSListItem.getEdsResponse().getAwbNo().toString();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getStatus() {
        try {
            return ShipmentUtils.getShipmentStatus(mCommonDRSListItem.getEdsResponse().getShipmentStatus());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getSlot() {
        try {
            long start_time = mCommonDRSListItem.getEdsResponse().getShipmentDetail().getSlot_details().getStart_time();
            long end_time = mCommonDRSListItem.getEdsResponse().getShipmentDetail().getSlot_details().getEnd_time();
            if (start_time == 0 && end_time == 0) {
                if (!mCommonDRSListItem.getProfileFound().getTime_slot().isEmpty()) {
                    return mCommonDRSListItem.getProfileFound().getTime_slot();
                } else {
                    return "";
                }
            } else {
                return CommonUtils.millisecondToAmPm(start_time) + " - " + CommonUtils.millisecondToAmPm(end_time);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public boolean isItemSynced() {
        try {
            return mCommonDRSListItem.getEdsResponse().getShipmentSyncStatus() == 2;
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return false;
    }
}