package in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc;

import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;

/**
 * Created by santosh on 18/12/19.
 */

public interface IEdsBkycIdfcFragmentNavigator {
    void ongetPid();

    void sendData(String pidData);
    void getToken(GenerateToken iciciResponse);
    void getMobile(EncryptContactResponse encryptContactResponse);
}
