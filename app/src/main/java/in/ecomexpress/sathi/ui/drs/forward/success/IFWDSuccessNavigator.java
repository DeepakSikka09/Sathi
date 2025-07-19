package in.ecomexpress.sathi.ui.drs.forward.success;

import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;

public interface IFWDSuccessNavigator {


    void onhomeclick();

    void onError(String e);

    void showEarnedDialog(DRSForwardTypeResponse drsForwardTypeResponse);
}
