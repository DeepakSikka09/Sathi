package in.ecomexpress.sathi.ui.dashboard.attendance.days_attendance_status_dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityDatePopupdialogBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.utils.ImageHandler;

@AndroidEntryPoint
public class DaysAttendanceStatusDialog extends BaseDialog implements DaysAttendanceCallBack {

    private static final String TAG = DaysAttendanceStatusDialog.class.getSimpleName();
    @Inject
    DaysAttendanceStausViewModel daysAttendanceStausViewModel;
    ActivityDatePopupdialogBinding activityDatePopupdialogBinding;
    static Activity context;
    static ImageHandler imageHandler;
    static HashMap<String, String> singleDayData;

    public static DaysAttendanceStatusDialog newInstance(Activity getcontext, HashMap<String, String> singleDaydata) {
        DaysAttendanceStatusDialog fragment = new DaysAttendanceStatusDialog();
        context = getcontext;
        singleDayData = singleDaydata;

        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activityDatePopupdialogBinding = DataBindingUtil.inflate(inflater, R.layout.activity_date_popupdialog, container, false);
        View view = activityDatePopupdialogBinding.getRoot();
        activityDatePopupdialogBinding.setViewModel(daysAttendanceStausViewModel);
        daysAttendanceStausViewModel.setNavigator(this);
        try {

            activityDatePopupdialogBinding.inTime.setText(singleDayData.get("intime"));
            activityDatePopupdialogBinding.outTime.setText(singleDayData.get("outTime"));
            activityDatePopupdialogBinding.status.setText(singleDayData.get("status"));
            activityDatePopupdialogBinding.dayWorkingHours.setText(singleDayData.get("wHours"));
            activityDatePopupdialogBinding.tvDate.setText(singleDayData.get("date"));


        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void onDismiss() {
        dismissDialog(TAG);
        activityDatePopupdialogBinding.daysAttendanceDialog.setVisibility(View.GONE);
    }

}