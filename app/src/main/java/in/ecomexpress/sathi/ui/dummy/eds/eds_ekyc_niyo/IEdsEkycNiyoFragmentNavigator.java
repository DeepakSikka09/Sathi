package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo;

import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;

public interface IEdsEkycNiyoFragmentNavigator {
    void ongetPid();

    void sendData(String pidData);
    void getToken(GenerateTokenNiyo iciciResponse);
    void getMobile(EncryptContactResponse encryptContactResponse);

}
