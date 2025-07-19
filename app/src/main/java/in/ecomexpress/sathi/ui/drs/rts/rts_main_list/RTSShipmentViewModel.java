package in.ecomexpress.sathi.ui.drs.rts.rts_main_list;

import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.Logger;

public class RTSShipmentViewModel {
    ShipmentsDetail shipmentsDetail;
    private final String TAG = RTSShipmentViewModel.class.getSimpleName();

    public RTSShipmentViewModel(ShipmentsDetail shipmentsDetail) {
        this.shipmentsDetail = shipmentsDetail;
    }

    public String Awb() {
        String Awb="";
        try {
         Awb = "AWB : " + shipmentsDetail.getAwbNo();
        }
        catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return Awb;
    }

    public String ParentAwb() {
        String ParentAwb="";
        try {
            ParentAwb = "Parent AWB : " + (((shipmentsDetail.getParentAwbNo()==null)||shipmentsDetail.getParentAwbNo().contains("null"))?"NA":shipmentsDetail.getParentAwbNo());
        }catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return ParentAwb;
    }

    public String Order() {
        String OrderNo="";
        try {
            OrderNo = "Order No : " + shipmentsDetail.getOrderNo();
        }
        catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return OrderNo;
    }

    public String Status() {
        String Status="";
        try {
            Status = shipmentsDetail.getStatus();
        }catch (Exception e){
            Logger.e(TAG, String.valueOf(e));
        }
        return Status;
    }

    public boolean backgroundColor() {
        return shipmentsDetail.getStatus().contains(Constants.RTSUNDELIVERED);
    }
}