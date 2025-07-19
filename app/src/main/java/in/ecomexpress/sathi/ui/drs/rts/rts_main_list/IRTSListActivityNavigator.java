package in.ecomexpress.sathi.ui.drs.rts.rts_main_list;

import java.util.ArrayList;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RTSReasonCodeMaster;

public interface IRTSListActivityNavigator {

    void onNextClick();

    void OnSetAdapter(List<ShipmentsDetail> shipmentsDetails);

    void onErrorMessage(String message);

    void notifyAdapter();

    void showPopupWindowUndelivered(List<RTSReasonCodeMaster> rtsReasonCodeMasterList);

    void getFinalCount(int del, int undelivered);

    void showToast();

    void getShipmentDetail(ShipmentsDetail shipmentsDetail);

    void showMessage(String description);

    void clearStack();

    void setNewAwbList(ArrayList<String> awbs);

    void setFWDCheckBoxStatus(boolean isChecked);

    void setRVPCheckBoxStatus(boolean isChecked);
}