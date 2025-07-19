
package in.ecomexpress.sathi.ui.drs.todolist;

import androidx.databinding.ObservableField;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.ShipmentUtils;

public class ForwardItemViewModel {

    private static final String TAG = ForwardItemViewModel.class.getSimpleName();
    private final ObservableField<Boolean> indicator = new ObservableField<>();

    public ObservableField<Boolean> getIndicator() {
        return indicator;
    }

    public void setImage(Boolean image) {
        this.indicator.set(image);
    }

    public ObservableField<String> getRemarks() {
        return remarks;
    }

    private final ObservableField<String> remarks = new ObservableField<>("");

    public void setRemarks(String remarks) {
        this.remarks.set(remarks);
    }

    public void onIndicatorClick() {
        drsItemViewModelListener.onIndicatorClick(mCommonDRSListItem);
    }

    public void onTrayClick() {
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }

    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;

    public ForwardItemViewModel(CommonDRSListItem mCommonDRSListItem, DRSItemViewModelListener drsItemViewModelListener) {
        this.mCommonDRSListItem = mCommonDRSListItem;
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

    public String getName() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getName();
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getAddress() {
        try {
            String getAddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ", "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ", "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ","
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getCity()) + ", "
                    + CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getState()) + ", "
                    + mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getPincode();
            return getAddress.replaceAll(", ,", ", ");
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String addressline1() {
        try {
            String getAddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine1());
            return getAddress.replaceAll(",,", ",");
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String shipper() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getPaymentMode() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getType() + ((mCommonDRSListItem.getDrsForwardTypeResponse().mpsShipment != null) && (!mCommonDRSListItem.getDrsForwardTypeResponse().mpsShipment.isEmpty()) ? ": MPS" : "");
        }catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getShipmentType() {
        try {
            return mCommonDRSListItem.getType();
        }catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getStatus() {
        try {
            return ShipmentUtils.getShipmentStatus(mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus());
        }catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String getPSTN() {
        return mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCallbridgePstn();
    }

    public String getSlot() {
        try {
            long start_time = mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getSlot_details().getStart_time();
            long end_time = mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getSlot_details().getEnd_time();
            if (start_time == 0 && end_time == 0) {
                ProfileFound profileFound = mCommonDRSListItem.getProfileFound();
                if (profileFound != null) {
                    String timeSlot = mCommonDRSListItem.getProfileFound().getTime_slot();
                    if (timeSlot != null) {
                        if (!timeSlot.isEmpty() && mCommonDRSListItem.getProfileFound().getTime_slot() != null) {
                            return mCommonDRSListItem.getProfileFound().getTime_slot();
                        } else {
                            return "";
                        }
                    }
                }
            } else {
                return CommonUtils.millisecondToAmPm(start_time) + " - " + CommonUtils.millisecondToAmPm(end_time);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String awb() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo().toString();
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public String pin() {
        try {
            //String pin = mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getPin();
            if (mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details()!=null)
            {
                String pin = String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin());
                pin = (pin != null && !pin.isEmpty()) ? pin : "";
                pin = "PIN: " + pin;
                return pin;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }


    public String amount() {
        try {
            String amount;
            if (mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getType().equalsIgnoreCase("COD")) {
                amount = "Rs. " + mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCollectableValue().toString();
            } else {
                amount = "Rs. " + mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getDeclaredValue().toString();
            }
            return amount;
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return "No Amount Defined. Contact Admin";
    }


    public int getMissedCallCounter() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getMissedCalls();
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return 0;
    }

    public boolean isItemSynced() {
        try {
            return mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentSyncStatus() == 2;
        } catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return false;
    }
}