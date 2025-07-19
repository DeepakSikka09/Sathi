package in.ecomexpress.sathi.ui.drs.todolist;

import java.util.HashMap;

import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;

public interface DRSCallListener {

    CallbridgeConfiguration getCallbridgeconfiguration();

    String getDefaultPstn();

    void makeCallBridgeApiCall(String callKey, String awb, int drsId, String type);

    void updateForwardCallAttempted(String awb);

    void updateRVPCallAttempted(String awb);

    void updateEDSCallAttempted(String awb);

    void updateRTSCallAttempted(String awb);

    void getCheckedShipmentlist(HashMap<String, Boolean> hashMap);

    void getSearchCheckedShipmentlist(Boolean flag);
}