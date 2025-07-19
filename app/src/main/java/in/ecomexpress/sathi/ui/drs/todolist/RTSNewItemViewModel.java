package in.ecomexpress.sathi.ui.drs.todolist;

import androidx.databinding.ObservableField;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.ShipmentUtils;

public class RTSNewItemViewModel {

    private static final String TAG = RTSNewItemViewModel.class.getSimpleName();
    public CommonDRSListItem mCommonDRSListItem;
    public DRSItemViewModelListener drsItemViewModelListener;
    public ObservableField<Boolean> getIndicator() {
        return indicator;
    }
    private final ObservableField<Boolean> indicator = new ObservableField<>();

    public void setImage(Boolean image) {
        this.indicator.set(image);
    }

    public RTSNewItemViewModel(CommonDRSListItem commonDRSListItem, DRSItemViewModelListener drsItemViewModelListener) {
        this.mCommonDRSListItem = commonDRSListItem;
        this.drsItemViewModelListener = drsItemViewModelListener;
    }

    public void onItemClick(){
        drsItemViewModelListener.onItemClick(mCommonDRSListItem);
    }

    public String getName(){
        try{
            return mCommonDRSListItem.getIRTSInterface().getDetails().getName();
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return " ";
    }

    public String getTotalShipment(){
        return "Total : " + mCommonDRSListItem.getIRTSInterface().getDetails().getTotalShipmentCount();
    }

    public String getAddress(){
        String finalAddress = "";
        try{
            String getRtsAddress = getRTSMoreAddress(mCommonDRSListItem.getIRTSInterface());
            finalAddress = getRtsAddress.replaceAll(", ,", ",");
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return finalAddress;
    }

    private String getRTSMoreAddress(IRTSBaseInterface response){
        String finalAddress = "";
        try{
            String line2 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine2());
            String line3 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine3());
            String line4 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine4());
            String city = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getCity());
            String pinCode = String.valueOf(response.getDetails().getRtsAddress().getPincode());
            finalAddress = (!line2.isEmpty() ? (line2 + ",\n") : "") + (!line3.isEmpty() ? (line3 + ",\n") : "") + (!line4.isEmpty() ? (line4 + ",\n") : "") + (!city.isEmpty() ? (city + ",\n") : "") + pinCode;
            return finalAddress;
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return finalAddress;
    }

    public String addressline1(){
        String finalAddress = "";
        try{
            String getRtsAddress = CommonUtils.nullToEmpty(mCommonDRSListItem.getIRTSInterface().getDetails().getRtsAddress().getLine1());
            finalAddress = getRtsAddress.replaceAll(",,", ",");
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return finalAddress;
    }

    public String getShipmentType(){
        try{
            return mCommonDRSListItem.getType();
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return "";
    }

    public void onMapClick(){
        drsItemViewModelListener.onMapClick(mCommonDRSListItem);
    }

    public void onCallClick(){
        drsItemViewModelListener.onCallClick(mCommonDRSListItem);
    }

    public void onIndicatorClick(){
        drsItemViewModelListener.onIndicatorClick(mCommonDRSListItem);
    }

    public void onTrayClick(){
        drsItemViewModelListener.onTrayClick(mCommonDRSListItem);
    }

    public String getShipmentStatus(){
        try{
            return ShipmentUtils.getShipmentStatus(mCommonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus());
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return " ";
    }

    public String getDeliveredShipment(){
        return "Delivered : " + mCommonDRSListItem.getIRTSInterface().getDetails().getDelivered();
    }

    public String getUndeliveredShipment(){
        return "Undelivered : " + mCommonDRSListItem.getIRTSInterface().getDetails().getUndelivered();
    }

    public String getManuallyDeliveredShipment(){
        return "Manual : " + mCommonDRSListItem.getIRTSInterface().getDetails().getMnnuallyDelivered();
    }

    public boolean isItemSynced(){
        try{
            return mCommonDRSListItem.getIRTSInterface().getDetails().getShipmentSyncStatus() == 2;
        } catch(Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return false;
    }
}
