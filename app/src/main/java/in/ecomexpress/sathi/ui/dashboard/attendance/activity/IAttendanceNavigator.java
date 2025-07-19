package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import java.util.HashMap;
import java.util.List;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponseList;

public interface IAttendanceNavigator {

    void spinnerClick();

    void sendData(String monthWorkingHours, String intialDate, List<AttendanceResponseList> attendanceResponseLists);

    void onHandleError(String myerror);

    void clearStack();

    void showStringError(String message);

    void showError(String error);

    void getDayData(HashMap<String, String> arrayList);

    void showErrorMessage(boolean b);

}
