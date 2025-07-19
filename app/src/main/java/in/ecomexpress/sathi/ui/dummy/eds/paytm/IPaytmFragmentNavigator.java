package in.ecomexpress.sathi.ui.dummy.eds.paytm;

import android.app.Dialog;

import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;

/**
 * Created by santosh on 5/9/19.
 */

public interface IPaytmFragmentNavigator {
    void onVodaConnect();



    void showError(String e);

    void getMobile(EncryptContactResponse encryptContactResponse);

    //progress dialog

}
