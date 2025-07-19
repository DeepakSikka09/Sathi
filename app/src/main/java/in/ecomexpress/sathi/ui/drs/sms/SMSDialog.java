package in.ecomexpress.sathi.ui.drs.sms;

import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivitySendSmsDialogBinding;
import in.ecomexpress.sathi.ui.base.BaseDialog;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.dashboard.starttrip.MyDialogCloseListener;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.utils.Logger;

@AndroidEntryPoint
public class SMSDialog extends BaseDialog implements SMSCallBack {

    private static final String TAG = SMSDialog.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    static Activity context;
    static ArrayList<String> awbListItem = new ArrayList<>();
    static ToDoListActivity toDoList;
    String onTime;
    String onLocation;
    MyDialogCloseListener myDialogCloseListener;
    @Inject
    SMSViewModel smsViewModel;
    ActivitySendSmsDialogBinding activitySendSmsDialog;

    public static SMSDialog newInstance(Activity getcontext, ToDoListActivity toDoListActivity, ArrayList<String> awbList) {
        SMSDialog fragment = new SMSDialog();
        context = getcontext;
        toDoList = toDoListActivity;
        awbListItem = awbList;
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setMyDialogCloseListener(MyDialogCloseListener listener) {
        this.myDialogCloseListener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activitySendSmsDialog = DataBindingUtil.inflate(inflater, R.layout.activity_send_sms_dialog, container, false);
        View view = activitySendSmsDialog.getRoot();
        logScreenNameInGoogleAnalytics(TAG, context);
        activitySendSmsDialog.setViewModel(smsViewModel);
        smsViewModel.setNavigator(this);
        return view;
    }

    public void show(FragmentManager fragmentManager) {
        super.show(fragmentManager, TAG);
    }

    @Override
    public void dismissDialog() {
        try {
            dismissDialog(TAG);
            try {
                ((ToDoListActivity) context).handleVisibilityToolbarIcons(true);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            activitySendSmsDialog.popupElement.setVisibility(View.GONE);
            myDialogCloseListener.handleDialogClose();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void cancel() {
        try {
            dismissDialog(TAG);
            try {
                ((ToDoListActivity) context).handleVisibilityToolbarIcons(true);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            activitySendSmsDialog.popupElement.setVisibility(View.GONE);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onHandleError(String errorResponse) {
        context.runOnUiThread(() -> {
            Toast.makeText(context, errorResponse, Toast.LENGTH_SHORT).show();
            try {
                ((ToDoListActivity) context).handleVisibilityToolbarIcons(true);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        });
    }

    @Override
    public void doLogout(String message) {
        ((DashboardActivity) context).doLogout(message);
    }

    @Override
    public void onSendClick() {
        try {
            smsViewModel.getFeRemarks(activitySendSmsDialog.remarks.getText().toString());
            if (awbListItem.isEmpty()) {
                Toast.makeText(context, "No shipment selected", Toast.LENGTH_SHORT).show();
                return;
            }
            smsViewModel.callSmsApi(context, awbListItem);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void time(String onTime) {
        this.onTime = onTime;
    }

    @Override
    public void location(String onLocation) {
        this.onLocation = onLocation;
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            context.runOnUiThread(() -> {
                Toast.makeText(context, getString(R.string.http_500_msg), Toast.LENGTH_SHORT).show();
                try {
                    ((ToDoListActivity) context).handleVisibilityToolbarIcons(true);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            });
        } else {
            context.runOnUiThread(() -> {
                Toast.makeText(context, getString(R.string.server_down_msg), Toast.LENGTH_SHORT).show();
                try {
                    ((ToDoListActivity) context).handleVisibilityToolbarIcons(true);
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            });
        }
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }
}