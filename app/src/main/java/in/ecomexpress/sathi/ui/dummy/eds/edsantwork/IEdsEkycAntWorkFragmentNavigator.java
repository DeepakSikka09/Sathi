package in.ecomexpress.sathi.ui.dummy.eds.edsantwork;

import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.antwork.BioMatricResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.WadhResponse;

public interface IEdsEkycAntWorkFragmentNavigator {
    void sendData(String pidData , String awb_no , String order_id);
    void getToken(GenerateTokenNiyo iciciResponse);
    void getMobile(EncryptContactResponse encryptContactResponse);
    void onActivateSensor();
    void sendBiomatricData(WadhResponse wadhResponse);
    void bioMatricResult(BioMatricResponse bioMatricResponse);
}
