package in.ecomexpress.sathi.ui.dashboard.switchnumber;

import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CbPstnOptions;

public interface SwitchNumberCallBack {

    void dismissDialog();

    void cancel();

    void OnSetFuelAdapter(List<CbPstnOptions> cbPstnOptionsList);

    void onSubmitNumber();

    void showError(String e);
}