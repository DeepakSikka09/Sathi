package in.ecomexpress.sathi.ui.dashboard.attendance.attendance_calendar_fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.AttendanceCalendarFragmentBinding;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponseList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceViewModel;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.MonthStatusAdapter;
import in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog.DaysAttendanceStatusDialog;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;

@AndroidEntryPoint
public class AttendanceCalendarFragment extends BaseFragment<AttendanceCalendarFragmentBinding, AttendanceCalendarFragmentViewModel> implements ActivityData {

    AttendanceCalendarFragmentBinding attendanceCalendarFragmentBinding;

    @Inject
    AttendanceCalendarFragmentViewModel attendanceCalendarFragmentViewModel;
    @Inject
    MasterActivityData masterActivityData;
    @Inject
    EDSActivityWizard edsActivityWizard;
    @Inject
    AttendanceViewModel attendanceViewModel;
    @Inject
    MonthStatusAdapter monthStatusAdapter;
    DaysAttendanceStatusDialog daysAttendanceStatusDialog;
    private List<AttendanceResponseList> listData;

    public static AttendanceCalendarFragment newInstance() {
        AttendanceCalendarFragment fragment = new AttendanceCalendarFragment();
        return fragment;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.attendance_calendar_fragment;
    }

    @Override
    public AttendanceCalendarFragmentViewModel getViewModel() {
        return attendanceCalendarFragmentViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceCalendarFragmentViewModel.setNavigator(this);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attendanceCalendarFragmentBinding = getViewDataBinding();
        attendanceCalendarFragmentBinding.monthsRecyclerViewFragment.setLayoutManager(new GridLayoutManager(getActivity(), 7));
        attendanceCalendarFragmentBinding.monthsRecyclerViewFragment.setItemAnimator(new DefaultItemAnimator());
        attendanceCalendarFragmentBinding.monthsRecyclerViewFragment.setAdapter(monthStatusAdapter);

        monthStatusAdapter.setData(listData, attendanceViewModel);

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void getData(BaseFragment fragment) {

    }

    @Override
    public boolean validateData() {
        return false;
    }

    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {
        return false;
    }

    @Override
    public void setImageValidation() {

    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return null;
    }

}
