package in.ecomexpress.sathi.ui.dashboard.stoptrip;

import android.content.Context;

public interface StopTripCallBack {

    void dismissDialog();

    void StopTrip();

    void cancel();

    void CameraLaunch();

    void onHandleError(String errorResponse);

    void doLogout(String message);

    void showErrorMessage(boolean status);

    void getSize(int size);

    void setStopMeterReading();

    void disableSubmitButton();

    void enableSubmitButton();

    long getStopMeterReadingXML();

    Context getActivityContext();

    void openEarnDialog(float delvrd_shpmnts, long earned_money);

    void showHandleError(boolean status);

    String getStopVehicleType();

    void setPushApiSize(int pushApisize);

    void clearStack();
}