package in.ecomexpress.sathi.ui.dashboard.landing;

import android.app.Activity;
import android.content.Context;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.DashboardBanner;

public interface IDashboardNavigator {

    void openStatisticsActivity();

    void openTodo();

    void openStartTrip();

    void openStopTrip();

    void noToDo();

    void onHandleError(ErrorResponse errorDetails);

    void doLogout(String message);

    void openFuelReimburse();

    void onSyncClick();

    void onAttendanceClick();

    void onCampaignClick();

    void onTrainingClick();

    void onODHClick();

    void showError(String error);

    void showStringError(String message);

    void clearStack();

    Context getActivityContext();

    Activity getActivityActivity();

    void dashboardBannerList(List<DashboardBanner> mydashboardBannerList);

    void sendMsg();

    void showErrorMessage(boolean status);

    void enableSyncButton();
    void disableSyncButton();

    void showAlertWarning(String msg , String mode);

    void callSync();

    Context setContext();

    void showSuccess(String msg);

    void scanQRCode();

    void callCheckAttendanceApi();
}