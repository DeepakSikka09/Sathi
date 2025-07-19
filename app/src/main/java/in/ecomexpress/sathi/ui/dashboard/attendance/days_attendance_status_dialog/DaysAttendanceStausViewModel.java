package in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog;

import android.app.Application;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class DaysAttendanceStausViewModel extends BaseViewModel<DaysAttendanceCallBack> {

    @Inject
    public DaysAttendanceStausViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public String getEmpCode() {
        return getDataManager().getCode();
    }

    public void onDismissDialog() {
        getNavigator().onDismiss();
    }
}
