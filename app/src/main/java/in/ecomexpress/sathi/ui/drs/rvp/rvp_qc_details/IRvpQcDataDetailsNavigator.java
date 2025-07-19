package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details;

import in.ecomexpress.sathi.repo.local.data.rvp.RvpCommit;

public interface IRvpQcDataDetailsNavigator {

     void onBack() ;

     void onNext(RvpCommit rvpCommit) ;

     void onProceed() ;

    void upDateCount();

    void showErrorMessage(boolean status);

    void showError(String e);

    void onProgressFinishCall();

    void onHandleError(String errorResponse);

    void showProgress();

    void setBitmap();
}