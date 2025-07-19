package in.ecomexpress.sathi.ui.dashboard.attendance.custom_dialog;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.CustomDialogBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivity;
import in.ecomexpress.sathi.ui.dashboard.attendance.activity.AttendanceActivity1;

@AndroidEntryPoint
public class CustomDialog extends BaseDialog implements CustomDialogCallBack {

    private static final String TAG = CustomDialog.class.getSimpleName();
    @Inject
    CustumDialogViewModel custumDialogViewModel;
    CustomDialogBinding customDialogBinding;
    AttendanceActivity context;
    static CustomDialog fragment;
    @Inject
    CustomDialogAdapter customDialogAdapter;
    ArrayList<String> monthsList;
    int currentYear, currentMonth, tempYear;
    private AttendanceActivity1 attendanceActivity1;
    private AttendanceActivity attendanceActivity;

    public static CustomDialog newInstance() {
        fragment = new CustomDialog();
        return fragment;
    }

    public void setAttendanceActivity1(AttendanceActivity1 attendanceActivity1) {
        this.attendanceActivity1 = attendanceActivity1;
    }

    public void setAttendanceActivity(AttendanceActivity attendanceActivity) {
        this.attendanceActivity = attendanceActivity;
    }

    @Override
    public void setCancelable(boolean cancelable) {
        cancelable = true;
        super.setCancelable(cancelable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customDialogBinding = DataBindingUtil.inflate(inflater, R.layout.custom_dialog, container, false);
        View view = customDialogBinding.getRoot();
        customDialogBinding.setViewModel(custumDialogViewModel);
        custumDialogViewModel.setNavigator(this);
        try {
            setUp();
            setMonthYear();
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setMonthYear() {
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        customDialogBinding.year.setText(Integer.toString(currentYear));
        customDialogAdapter.getYear(currentYear);
        if (currentMonth > 2)
            customDialogBinding.back.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUp() {
        customDialogBinding.monthsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        customDialogBinding.monthsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        customDialogBinding.monthsRecyclerView.setAdapter(customDialogAdapter);
        monthsList = new ArrayList<>();
        monthsList.add("Jan");
        monthsList.add("Feb");
        monthsList.add("Mar");
        monthsList.add("Apr");
        monthsList.add("May");
        monthsList.add("Jun");
        monthsList.add("Jul");
        monthsList.add("Aug");
        monthsList.add("Sept");
        monthsList.add("Oct");
        monthsList.add("Nov");
        monthsList.add("Dec");
        customDialogAdapter.setData(monthsList);
        customDialogAdapter.notifyDataSetChanged();
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @SuppressLint("SetTextI18n")
    public void yearIncrease() {
        if (Integer.parseInt(customDialogBinding.year.getText().toString()) < currentYear) {
            tempYear = Integer.parseInt(customDialogBinding.year.getText().toString());
            tempYear++;
            int year = tempYear;
            customDialogAdapter.getYear(year);
            customDialogBinding.year.setText(Integer.toString(tempYear));
            if (Integer.parseInt(customDialogBinding.year.getText().toString()) == currentYear) {
                customDialogBinding.next.setVisibility(View.GONE);
                customDialogBinding.back.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onBackClick() {
        customDialogBinding.year.setText(Integer.parseInt(customDialogBinding.year.getText().toString()) - 1);
    }

    public void cancelClick() {
        dismissDialog(TAG);
        customDialogBinding.popupElement.setVisibility(View.GONE);

    }

    public void okClick() {
        String myYear = customDialogBinding.year.getText().toString();
        String getMonth = customDialogAdapter.getMonth();
        try {
            if (getMonth != null && myYear != null) {
                if (customDialogAdapter.selectedMonth != -1) {
                    dismissDialog(TAG);
                    if (attendanceActivity != null) {
                        attendanceActivity.getSelectedData(getMonth, myYear);
                    } else if (attendanceActivity1 != null) {
                        attendanceActivity1.getSelectedData(getMonth, myYear);
                    }
                } else
                    Toast.makeText(getActivity(), R.string.toast_to_select_month, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.toast_to_select_month, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void yearDecrease() {
        if (currentMonth <= 2) {
            tempYear = currentYear;
            tempYear--;
            int year = tempYear;
            customDialogAdapter.getYear(year);
            customDialogBinding.year.setText(Integer.toString(tempYear));
            customDialogBinding.next.setVisibility(View.VISIBLE);
            if (Integer.parseInt(customDialogBinding.year.getText().toString()) < currentYear) {
                customDialogBinding.back.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onHandleError(String myerror) {
        Toast.makeText(context, "exception : " + myerror, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dis() {
        customDialogBinding.popupElement.setVisibility(View.GONE);
    }
}