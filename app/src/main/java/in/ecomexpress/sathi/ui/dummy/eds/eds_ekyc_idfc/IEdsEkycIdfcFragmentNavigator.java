package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc;

import in.ecomexpress.sathi.repo.remote.model.ContatDecryption;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.eds.IDFCToken_Response;

/**
 * Created by santosh on 18/12/19.
 */

public interface IEdsEkycIdfcFragmentNavigator {
    void onActivateScan();

    void sendData(String pidData ,String awb_no, String order_number);
    void getToken(IDFCToken_Response iciciResponse);
    void getMobile(EncryptContactResponse encryptContactResponse);
}
