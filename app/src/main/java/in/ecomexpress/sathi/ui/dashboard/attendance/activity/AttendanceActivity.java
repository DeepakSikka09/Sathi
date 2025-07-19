package in.ecomexpress.sathi.ui.dashboard.attendance.activity;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityAttendanceBinding;
import in.ecomexpress.sathi.repo.remote.model.attendance.AttendanceResponseList;
import in.ecomexpress.sathi.ui.auth.login.LoginActivity;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog.CustomDialog;
import in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog.DaysAttendanceStatusDialog;
import in.ecomexpress.sathi.utils.MessageManager;

@AndroidEntryPoint
public class AttendanceActivity extends BaseActivity<ActivityAttendanceBinding, AttendanceViewModel> implements IAttendanceNavigator {

    private static final String SELECTED_MONTH = "selected_month";
    private static final String SELECTED_YEAR = "selected_year";
    @Inject
    AttendanceViewModel mAttendanceViewModel;
    @Inject
    MonthStatusAdapter monthStatusAdapter;
    @Inject
    CalendarDaysAdapter calendarDaysAdapter;
    DaysAttendanceStatusDialog daysAttendanceStatusDialog;
    List<AttendanceResponseList> mList;
    ActivityAttendanceBinding mActivityAttendanceBinding;
    private AttendanceActivity attendanceActivity;
    ArrayList<String> days;
    private int selectedMonth, selectedYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attendanceActivity = this;
        mAttendanceViewModel.setNavigator(this);
        this.mActivityAttendanceBinding = getViewDataBinding();
        try {
            initView();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                selectedMonth = extras.getInt(SELECTED_MONTH);
                selectedYear = extras.getInt(SELECTED_YEAR);
                if (selectedMonth > 0 && selectedYear > 2018) {
                    callAPI();
                }
            } else {
                initAttendanceData();
            }
            setUpDay();
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
        //add toolbar
        mActivityAttendanceBinding.header.awb.setText(R.string.attendance);
        mActivityAttendanceBinding.header.backArrow.setOnClickListener(v -> onBackClick());
    }

    public void onBackClick() {
        finish();
        applyTransitionToBackFromActivity(this);
    }

    private void initView() {
        mActivityAttendanceBinding.monthsRecyclerView.setLayoutManager(new GridLayoutManager(AttendanceActivity.this, 7));
        mActivityAttendanceBinding.monthsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivityAttendanceBinding.monthsRecyclerView.setAdapter(monthStatusAdapter);
    }

    private void initAttendanceData() {
        selectedMonth = getCurrentMonth();
        selectedYear = getCurrentYear();
        callAPI();
    }

    @SuppressLint("SetTextI18n")
    private void callAPI() {
        String stringMonth = "";
        int integerMonth = selectedMonth;
        switch (integerMonth) {
            case 1:
                stringMonth = "JAN";
                break;
            case 2:
                stringMonth = "FEB";
                break;
            case 3:
                stringMonth = "MAR";
                break;
            case 4:
                stringMonth = "APR";
                break;
            case 5:
                stringMonth = "MAY";
                break;
            case 6:
                stringMonth = "JUN";
                break;
            case 7:
                stringMonth = "JUL";
                break;
            case 8:
                stringMonth = "AUG";
                break;
            case 9:
                stringMonth = "SEP";
                break;
            case 10:
                stringMonth = "OCT";
                break;
            case 11:
                stringMonth = "NOV";
                break;
            case 12:
                stringMonth = "DEC";
                break;
        }

        mAttendanceViewModel.callApi(stringMonth, String.valueOf(selectedYear));
        mActivityAttendanceBinding.date.setText(stringMonth + ", " + selectedYear);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpDay() {
        mActivityAttendanceBinding.dayRecyclerView.setLayoutManager(new GridLayoutManager(AttendanceActivity.this, 7));
        mActivityAttendanceBinding.dayRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mActivityAttendanceBinding.dayRecyclerView.setAdapter(calendarDaysAdapter);
        days = new ArrayList<>();
        days.add("Su");
        days.add("Mo");
        days.add("Tu");
        days.add("We");
        days.add("Th");
        days.add("Fr");
        days.add("Sa");
        calendarDaysAdapter.setData(days);
        calendarDaysAdapter.notifyDataSetChanged();
    }

    private int getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    private int getCurrentYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public void getSelectedData(String month, String year) {
        switch (month) {
            case "Jan":
                selectedMonth = 1;
                break;
            case "Feb":
                selectedMonth = 2;
                break;
            case "Mar":
                selectedMonth = 3;
                break;
            case "Apr":
                selectedMonth = 4;
                break;
            case "May":
                selectedMonth = 5;
                break;
            case "Jun":
                selectedMonth = 6;
                break;
            case "Jul":
                selectedMonth = 7;
                break;
            case "Aug":
                selectedMonth = 8;
                break;
            case "Sept":
                selectedMonth = 9;
                break;
            case "Oct":
                selectedMonth = 10;
                break;
            case "Nov":
                selectedMonth = 11;
                break;
            case "Dec":
                selectedMonth = 12;
                break;

        }

        selectedYear = Integer.parseInt(year);
        startAttendanceActivity1();
        mActivityAttendanceBinding.monthsRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mActivityAttendanceBinding.monthsRecyclerView.getRecycledViewPool().clear();
    }

    private void startAttendanceActivity1() {
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_MONTH, selectedMonth);
        bundle.putInt(SELECTED_YEAR, selectedYear);
        Intent intent = new Intent(AttendanceActivity.this, AttendanceActivity1.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        applyTransitionToOpenActivity(this);
    }

    @Override
    public AttendanceViewModel getViewModel() {
        return mAttendanceViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_attendance;
    }


    @Override
    public void spinnerClick() {
        CustomDialog customDialog = CustomDialog.newInstance();
        customDialog.setAttendanceActivity(attendanceActivity);
        customDialog.show(getSupportFragmentManager());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void sendData(String monthWorkingHours, String initialDay, List<AttendanceResponseList> attendanceResponseLists) {
        try {
            for (AttendanceResponseList attendanceResponseList : attendanceResponseLists) {
                if (attendanceResponseList.getAttendance_status().equalsIgnoreCase("PR") || attendanceResponseList.getAttendance_status().equalsIgnoreCase("AB")) {
                    String status = attendanceResponseList.getAttendance_status();
                    attendanceResponseList.setAttendance_status(String.valueOf(status.charAt(0)));
                }
            }


            mList = new ArrayList<>();
            List<AttendanceResponseList> commonlist = new ArrayList<>();
            List<AttendanceResponseList> attendanceListWithStatus = new ArrayList<>();
            int length;
            if (initialDay.equalsIgnoreCase(getString(R.string.monday))) {
                length = 0;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }

            if (initialDay.equalsIgnoreCase(getString(R.string.tuesdayl))) {
                length = 1;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            if (initialDay.equalsIgnoreCase(getString(R.string.wednesday))) {
                length = 2;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            if (initialDay.equalsIgnoreCase(getString(R.string.thursday))) {
                length = 3;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {

                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {

                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            if (initialDay.equalsIgnoreCase(getString(R.string.friday))) {
                length = 4;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            if (initialDay.equalsIgnoreCase(getString(R.string.saturday))) {
                length = 5;
                emptyItemAddition(length);
                commonlist.addAll(mList);
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            if (initialDay.equalsIgnoreCase(getString(R.string.sunday))) {
                for (AttendanceResponseList attendanceResponseItem : attendanceResponseLists) {
                    if (attendanceResponseItem.getAttendance_status() != null && !attendanceResponseItem.getAttendance_status().isEmpty()) {
                        attendanceListWithStatus.add(attendanceResponseItem);
                    }
                }
                commonlist.addAll(attendanceListWithStatus);
            }
            monthStatusAdapter.setData(commonlist, mAttendanceViewModel);
            monthStatusAdapter.notifyDataSetChanged();
            mActivityAttendanceBinding.monthWorkingHours.setText(monthWorkingHours);
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }
    }

    public void emptyItemAddition(int length) {
        for (int i = 0; i <= length; i++) {
            AttendanceResponseList abc = new AttendanceResponseList();
            abc.setAttendance_status("");
            abc.setDate("");
            mList.add(abc);
        }
    }

    @Override
    public void onHandleError(String myError) {
        runOnUiThread(() -> Toast.makeText(AttendanceActivity.this, myError, Toast.LENGTH_LONG).show());
    }

    @Override
    public void clearStack() {
        Intent intent = new Intent(AttendanceActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        applyTransitionToOpenActivity(this);
    }

    @Override
    public void showStringError(String message) {
        MessageManager.showToast(AttendanceActivity.this, message);
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    @Override
    public void getDayData(HashMap<String, String> hashMap) {
        daysAttendanceStatusDialog = DaysAttendanceStatusDialog.newInstance(AttendanceActivity.this, hashMap);
        daysAttendanceStatusDialog.show(getSupportFragmentManager());
    }

    @Override
    public void showErrorMessage(boolean status) {
        try {
            if (status)
                Toast.makeText(AttendanceActivity.this, getResources().getString(R.string.http_500_msg), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(AttendanceActivity.this, getResources().getString(R.string.server_down_msg), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}