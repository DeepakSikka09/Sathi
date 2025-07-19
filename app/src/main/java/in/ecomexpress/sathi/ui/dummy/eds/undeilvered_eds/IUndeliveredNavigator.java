package in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds;

import android.app.Activity;
import android.view.View;

import java.util.List;

import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.EDSReasonCodeMaster;
import in.ecomexpress.sathi.repo.remote.model.reschedule.ReshceduleDetailsResponse;

/**
 * Created by dhananjayk on 22-11-2018.
 */

public interface IUndeliveredNavigator {
    void onChooseReasonSpinner(EDSReasonCodeMaster edsReasonCodeMaster, boolean rescheduleFlag, boolean callFlag);

    void onChooseSlotSpinner(String slotId);

    void onCaptureImage();

    void OnSubmitClick();

    void captureDate();

    void openFail();

    void onRescheduleClick();

    void onHandleError(ErrorResponse callApiResponse);

    void onBackClick();

    void onChooseGroupSpinner(String s);


    void onChooseChildSpinner(EDSReasonCodeMaster edsReasonCodeMaster, boolean b, boolean b1);

    void visible(boolean b);

    void showError(String error);

    void setParentGroupSpinnerValues(List<String> parentGroupSpinnerValues);

    void setChildSpinnerValues(List<String> childGroupSpinnerValues);

    void setVehicleTypeSpinnerValues(List<String> vehicleTypeSpinnerValues);

    void showErrorMessage(boolean status);

    void doLogout(String description);

    void clearStack();

    void onEDSItemFetched(EDSResponse edsResponse);

    void setConsigneeDistance(int meter);

    void setConsingeeProfiling(boolean enable);

    void showProgress();

    void setBitmap();

    void onProgressFinishCall();

    void callNotAttemptedDialog();

    void undeliverShipment(boolean b);

    void getEDSRescheduleFlag(Boolean aBoolean);

    Activity getActivityContext();

    void setRescheduleResponse(ReshceduleDetailsResponse reshceduleDetailsResponse);
    void otpLayout(boolean uD_OTP, String otp_key);
    void onResendClick();
    void onGenerateOtpClick();
    void onVerifyClick();
    void onSkipClick(View view);
    void onOtpResendSuccess(String str, String description);
    void resendSms(Boolean alternateclick);
    void voiceTimer();
    void VoiceCallOtp();
    void onCallBridgeCheckStatus();
}
