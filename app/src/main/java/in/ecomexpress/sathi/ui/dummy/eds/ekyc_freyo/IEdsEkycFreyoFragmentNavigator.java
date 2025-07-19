package in.ecomexpress.sathi.ui.dummy.eds.ekyc_freyo;

import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;

public interface IEdsEkycFreyoFragmentNavigator {
    void onActivateSensor();

    void sendData(String pidData , String awb_no , String order_id);
    void getToken(GenerateTokenNiyo iciciResponse);
    void getMobile(EncryptContactResponse encryptContactResponse);

}
