package in.ecomexpress.sathi.ui.drs.rts.rts_scan_and_deliver;

import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.ShipmentsDetail;

public interface IRTSScanActivityNavigator {

    void onScannerSeqSaveClicked(List<ShipmentsDetail> newScannedDRSSequenceList);

    void setAwbNoForFlyer(long awbNoForFlyer);

    void onErrorMessage(String message);

    void notifyAdapter();

    void OnSetAdapter(List<ShipmentsDetail> shipmentsDetails);
}