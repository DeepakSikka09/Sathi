package in.ecomexpress.sathi.ui.drs.rvp.navigator;

import android.app.Activity;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.RVPReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.mps.DRSRvpQcMpsResponse;

public interface IRVPUndeliveredNavigator {

    void onBackClick();

    void onSubmitSuccess();

    void OnSubmitClick();

    void onHandleError(ErrorResponse callApiResponse);

    void onChooseGroupSpinner(String s);

    void onChooseChildSpinner(RVPReasonCodeMaster rvpReasonCodeMaster);

    void visible(boolean b);

    void showError(String error);

    void showServerError(String error);

    void setReasonMessageSpinnerValues(List<String> reasonMessageSpinnerValues);

    void showErrorMessage(boolean status);

    void doLogout(String description);

    void clearStack();

    void onRVPItemFetched(DRSReverseQCTypeResponse drsReverseQCTypeResponse, DRSRvpQcMpsResponse drsMpsTypeResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void setBitmap();

    void isCallAttempted(int isCall);

    void undeliverShipment(boolean failFlag, boolean b);

    Activity getActivityContext();

    void onResendClick();

    void onVerifyClick();

    void resendSms(Boolean alternateclick);

    void voiceTimer();

    void onOtpResendSuccess(String str, String description);

    void onGenerateOtpClick();

    void VoiceCallOtp();
}