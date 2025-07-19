package in.ecomexpress.sathi.ui.dashboard.starttrip;

import android.content.Context;

public interface StartTripCallBack {
    void dismissDialog();

    void startTripSyncDrs();

    void StartTrip();

    void CameraLaunch();

    void VehicleType(String vehicleType);

    void onHandleError(String errorResponse);

    void TypeOfVehicle(String typeOfVehicle);

    void doLogout(String message);

    void closeDialogopenDrs();

    void showDescription(String description);

    void sendVehicleNumber(String routeName);

    void showErrorMessage(boolean status);

    Context getActivityContext();

    void showError(String e);

    void disableSubmitButton();

    void enableSubmitButton();
    void showSuccess(String msg);

    void openDPDailyEarningDialog(float potential_earning,long fwd_shpmnt_number);

    void FrontCameraLaunch();

    void clearStack();
}