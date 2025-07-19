package in.ecomexpress.sathi.ui.drs.todolist;

import static android.content.Context.TELEPHONY_SERVICE;
import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToOpenActivity;
import static in.ecomexpress.sathi.utils.Constants.ConsigneeDirectAlternateMobileNo;
import static in.ecomexpress.sathi.utils.Constants.eds_call_count;
import static in.ecomexpress.sathi.utils.Constants.forward_call_count;
import static in.ecomexpress.sathi.utils.Constants.rts_call_count;
import static in.ecomexpress.sathi.utils.Constants.rvp_call_count;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ItemEdsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemEmptyViewBinding;
import in.ecomexpress.sathi.databinding.ItemForwardListViewBinding;
import in.ecomexpress.sathi.databinding.ItemNewRtsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpMpsListViewBinding;
import in.ecomexpress.sathi.repo.local.data.activitiesdata.RTSActivitiesData;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.local.db.model.RescheduleEdsD;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.ui.dashboard.drs.list.RecyclerItemTouchCallback;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSScanActivity;
import in.ecomexpress.sathi.ui.drs.forward.obd.activity.FwdOBDProductDetailActivity;
import in.ecomexpress.sathi.ui.drs.mps.activity.MpsPickupActivity;
import in.ecomexpress.sathi.ui.drs.rts.rts_main_list.RTSListActivity;
import in.ecomexpress.sathi.ui.drs.rvp.activity.PickupActivity;
import in.ecomexpress.sathi.ui.drs.secure_delivery.SecureDeliveryActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.SwipeLayout;
import io.reactivex.disposables.CompositeDisposable;

public class DRSListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable, RecyclerItemTouchCallback.ItemTouchHelperListener {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_FORWARD = 1;
    public static final int VIEW_TYPE_RTS = 2;
    public static final int VIEW_TYPE_RVP = 3;
    public static final int VIEW_TYPE_EDS = 4;
    public static final int VIEW_TYPE_RVP_MPS = 5;
    private static final String TAG = DRSListAdapter.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    public static CommonDRSListItem commonDRSListItemEdsClick, commonDRSListItemFWDClick, commonDRSListItemRVPClick;
    private final ItemFilter mFilter = new ItemFilter();
    public List<CommonDRSListItem> filterShipments = new ArrayList<>();
    SecureDelivery isSecureDelivery;
    String getDrsApiKey = null, getDrsPstnKey = null, getDrsPin = null, getOrderId = "", getDrsId = "", getCbConfigCallType = null, Masterpstnformat = null;
    String myRemarks;
    boolean isDigital = false;
    ToDoListActivity toDoListActivity;
    Animation slide_down;
    CallbridgeConfiguration callbridgeConfiguration = null;
    ToDoListViewModel toDoListViewModel;
    boolean isCheckboxVisible = false;
    HashMap<String, Boolean> checkbox_filter = new HashMap<>();
    Context ctx;
    List<RescheduleEdsD> edsRescheduleData;
    boolean forward_is_item_clicked = true;
    boolean rvp_is_item_clicked = true;
    boolean eds_is_item_clicked = true;
    private List<CommonDRSListItem> mCommonDRSListItems;
    private RecyclerView mRecyclerView;
    private RowClickActionsListener clickSwipeListener;
    private int[] itemsOffset;
    private int forward_map_count = 0;
    private int eds_map_count = 0;
    private int rvp_map_count = 0;
    private int rts_map_count = 0;
    private DRSCallListener drsCallListener;

    public DRSListAdapter() {}

    public DRSListAdapter(RowClickActionsListener clickSwipeListener) {
        this.clickSwipeListener = clickSwipeListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    public boolean startCallIntent(CommonDRSListItem mCommonDRSListItem, String nykaCallBridge, String direct_calling_no, String number, Context context, long awb, int drs) {
        try {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            int simState = tMgr.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                Toast.makeText(context, "No Sim Found!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (ContextCompat.checkSelfPermission(toDoListActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(toDoListActivity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            } else {
                toDoListViewModel.saveCallStatus(awb, drs);
                Constants.call_intent_number = number;

                if (nykaCallBridge.equalsIgnoreCase("true")) {

                    if (Constants.shipment_type.equalsIgnoreCase(Constants.FWD) && mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details() != null) {
                        if (mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().size() > 1) {
                            showCallBridgeDialogFWD(mCommonDRSListItem, awb);
                        } else {
                            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
                            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
                            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
                            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    } else if (Constants.shipment_type.equalsIgnoreCase(Constants.RVP) && mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details() != null) {
                        if (mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().size() > 1) {
                            showCallBridgeDialogRVP(mCommonDRSListItem, awb);
                        } else {
                            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
                            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
                            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
                            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    } else if (Constants.shipment_type.equalsIgnoreCase(Constants.EDS) && mCommonDRSListItem.getEdsResponse().getCallbridge_details() != null) {
                        if (mCommonDRSListItem.getEdsResponse().getCallbridge_details().size() > 1) {
                            showCallBridgeDialogEDS(mCommonDRSListItem, awb);
                        } else {
                            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
                            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
                            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
                            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    } else {
                        if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                            showDirectCallDialog(direct_calling_no, awb);
                        } else {
                            toDoListViewModel.consigneeContactNumber.set(direct_calling_no);
                            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
                            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
                            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + direct_calling_no));
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent1);
                        }
                    }


                } else {

                    if (!TextUtils.isEmpty(ConsigneeDirectAlternateMobileNo) && ConsigneeDirectAlternateMobileNo != null && !ConsigneeDirectAlternateMobileNo.equals("0")) {
                        showDirectCallDialog(direct_calling_no, awb);
                    } else {
                        toDoListViewModel.consigneeContactNumber.set(direct_calling_no);
                        toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
                        toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
                        toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
                        toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
                        Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + direct_calling_no));
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }

                }
            }
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Logger.e(TAG, String.valueOf(e));
        }
        return true;
    }

    public void showCallBridgeDialogFWD(CommonDRSListItem mCommonDRSListItem, long awb) {
        Dialog dialog = new Dialog(toDoListActivity, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(1).getPin() + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(1).getPin() + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showCallBridgeDialogRVP(CommonDRSListItem mCommonDRSListItem, long awb) {
        Dialog dialog = new Dialog(toDoListActivity, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(1).getPin() + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(1).getPin() + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showCallBridgeDialogEDS(CommonDRSListItem mCommonDRSListItem, long awb) {
        Dialog dialog = new Dialog(toDoListActivity, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_callbridge);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        call.setText(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number());
        Button altCall = dialog.findViewById(R.id.bt_sms);
        altCall.setText(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(1).getCallbridge_number());
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(4) + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(4) + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(1).getPin() + "#");
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(1).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(1).getPin() + "#"));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showDirectCallDialog(String direct_calling_no, long awb) {
        Dialog dialog = new Dialog(toDoListActivity, R.style.RoundedCornersDialog);
        dialog.setContentView(R.layout.dialog_direct_call);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Button call = dialog.findViewById(R.id.bt_call);
        Button altCall = dialog.findViewById(R.id.bt_sms);
        ImageView crossDialog = dialog.findViewById(R.id.crssdialog);
        crossDialog.setOnClickListener(v -> dialog.dismiss());
        call.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(direct_calling_no);
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + direct_calling_no));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        altCall.setOnClickListener(v -> {
            toDoListViewModel.consigneeContactNumber.set(ConsigneeDirectAlternateMobileNo);
            toDoListViewModel.getDataManager().setForwardCallCount(awb + "FWD", forward_call_count);
            toDoListViewModel.getDataManager().setRVPCallCount(awb + "RVP", rvp_call_count);
            toDoListViewModel.getDataManager().setEDSCallCount(awb + "EDS", eds_call_count);
            toDoListViewModel.getDataManager().setRTSCallCount(awb + "RTS", rts_call_count);
            Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ConsigneeDirectAlternateMobileNo));
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            toDoListActivity.startActivity(intent1);
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void getCallBridgeConfig(DRSCallListener drsCallListener) {
        try {
            this.drsCallListener = drsCallListener;
            callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, "getCallBridgeConfig()" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void clearSmsCheck() {
        checkbox_filter.clear();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CommonDRSListItem> mCommonDRSListItems, ToDoListViewModel toDoListViewModel, ToDoListActivity aContext) {
        try {
            if (!mCommonDRSListItems.isEmpty()) {
                this.mCommonDRSListItems = mCommonDRSListItems;
                this.toDoListActivity = aContext;
                this.toDoListViewModel = toDoListViewModel;
                this.filterShipments.clear();
                this.filterShipments.addAll(this.mCommonDRSListItems);
                itemsOffset = new int[filterShipments.size()];
                checkbox_filter.clear();
                if (drsCallListener != null) {
                    drsCallListener.getSearchCheckedShipmentlist(true);
                }
                for (CommonDRSListItem drsListItem : filterShipments) {
                    boolean isChecked = !toDoListViewModel.getDataManager().getSMSThroughWhatsapp();
                    if (drsListItem.getCommonDrsStatus() == 0) {
                        if (drsListItem.getType().equalsIgnoreCase(Constants.FWD)) {
                            checkbox_filter.put(String.valueOf(drsListItem.getDrsForwardTypeResponse().getAwbNo()), isChecked);
                        }
                        if (drsListItem.getType().equalsIgnoreCase(Constants.RVP)) {
                            checkbox_filter.put(String.valueOf(drsListItem.getDrsReverseQCTypeResponse().getAwbNo()), isChecked);
                        }
                        if (drsListItem.getType().equalsIgnoreCase(Constants.EDS)) {
                            checkbox_filter.put(String.valueOf(drsListItem.getEdsResponse().getAwbNo()), isChecked);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, "setData()" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public List<CommonDRSListItem> getData() {
        return this.mCommonDRSListItems;
    }

    public void setRemarks(String remarks) {
        this.myRemarks = remarks;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshRecyclerView() {
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void handleCheckboxes() {
        try {
            isCheckboxVisible = true;
            for (int i = 0; i < filterShipments.size(); i++) {
                filterShipments.get(i).setSmsCheckFlag(!toDoListViewModel.getDataManager().getSMSThroughWhatsapp());
                filterShipments.get(i).setSmsCheckFlag(toDoListViewModel.getDataManager().getSMSThroughWhatsapp());
            }
            drsCallListener.getCheckedShipmentlist(checkbox_filter);
            notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, "handleCheckBoxes()" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void cancelCheckboxes() {
        isCheckboxVisible = false;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void handleCheckCheckboxes(boolean checkBox_status) {
        if (checkBox_status) {
            try {
                this.filterShipments.clear();
                this.filterShipments.addAll(this.mCommonDRSListItems);
                itemsOffset = new int[filterShipments.size()];
                checkbox_filter.clear();
                for (int i = 0; i < filterShipments.size(); i++) {
                    if (filterShipments.get(i).getCommonDrsStatus() == 0) {
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.FWD)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getDrsForwardTypeResponse().getAwbNo()), true);
                        }
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.RVP)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getDrsReverseQCTypeResponse().getAwbNo()), true);
                        }
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.EDS)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getEdsResponse().getAwbNo()), true);
                        }
                        filterShipments.get(i).setSmsCheckFlag(true);
                    }
                }
                drsCallListener.getCheckedShipmentlist(checkbox_filter);
                notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        } else {
            try {
                this.filterShipments.clear();
                this.filterShipments.addAll(this.mCommonDRSListItems);
                itemsOffset = new int[filterShipments.size()];
                checkbox_filter.clear();
                for (int i = 0; i < filterShipments.size(); i++) {
                    if (filterShipments.get(i).getCommonDrsStatus() == 0) {
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.FWD)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getDrsForwardTypeResponse().getAwbNo()), false);
                        }
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.RVP)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getDrsReverseQCTypeResponse().getAwbNo()), false);
                        }
                        if (filterShipments.get(i).getType().equalsIgnoreCase(Constants.EDS)) {
                            checkbox_filter.put(String.valueOf(filterShipments.get(i).getEdsResponse().getAwbNo()), false);
                        }
                        filterShipments.get(i).setSmsCheckFlag(false);
                    }
                }
                drsCallListener.getCheckedShipmentlist(checkbox_filter);
                notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    /// layouts_item
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FORWARD:
                ItemForwardListViewBinding itemForwardListBinding = ItemForwardListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ForwardViewHolder(itemForwardListBinding);
            case VIEW_TYPE_RTS:
                ItemNewRtsListViewBinding itemRtsListBinding = ItemNewRtsListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new RTSViewHolder(itemRtsListBinding);
            case VIEW_TYPE_RVP:
                ItemRvpListViewBinding itemRvpListBinding = ItemRvpListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new RVPViewHolder(itemRvpListBinding);
            case VIEW_TYPE_EDS:
                ItemEdsListViewBinding itemEdsListViewBinding = ItemEdsListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EDSViewHolder(itemEdsListViewBinding);
            case VIEW_TYPE_RVP_MPS:
                ItemRvpMpsListViewBinding itemRvpMpsListViewBinding = ItemRvpMpsListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new RVPMPSViewHolder(itemRvpMpsListViewBinding);
            default:
                ItemEmptyViewBinding itemEmptyBinding = ItemEmptyViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EmptyViewHolder(itemEmptyBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        try {
            ctx = holder.itemView.getContext();
            holder.onBind(holder.getAdapterPosition());
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        try {
            if (filterShipments != null && !filterShipments.isEmpty()) {
                return filterShipments.size();
            } else {
                return 1;
            }
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return 1;
    }

    public int getCount() {
        return filterShipments.size();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (filterShipments != null && !filterShipments.isEmpty()) {
                switch (filterShipments.get(position).getType()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        return VIEW_TYPE_FORWARD;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        return VIEW_TYPE_RTS;
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        return VIEW_TYPE_RVP;
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        return VIEW_TYPE_EDS;
                    case GlobalConstant.ShipmentTypeConstants.RVP_MPS:
                        return VIEW_TYPE_RVP_MPS;
                    default:
                        return VIEW_TYPE_EMPTY;
                }
            } else {
                return VIEW_TYPE_EMPTY;
            }
        } catch (Exception e) {
            Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return VIEW_TYPE_EMPTY;
    }

    public void resetClickVariables() {
        forward_is_item_clicked = true;
        rvp_is_item_clicked = true;
        eds_is_item_clicked = true;
    }

    private void resetAllOtherItemSwipe(int position) {
        if (mRecyclerView != null) {
            for (int i = 0; i < filterShipments.size(); i++) {
                BaseViewHolder viewHolderForAdapterPosition = (BaseViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                if (i != position) {
                    if (viewHolderForAdapterPosition instanceof ForwardViewHolder) {
                        ((ForwardViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateReset();
                    } else if (viewHolderForAdapterPosition instanceof RVPViewHolder) {
                        ((RVPViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateReset();
                    } else if (viewHolderForAdapterPosition instanceof RTSViewHolder) {
                        ((RTSViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateReset();
                    } else if (viewHolderForAdapterPosition instanceof EDSViewHolder) {
                        ((EDSViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateReset();
                    }
                } else {
                    if (viewHolderForAdapterPosition instanceof ForwardViewHolder) {
                        ((ForwardViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateSwipeRight();
                    } else if (viewHolderForAdapterPosition instanceof RVPViewHolder) {
                        ((RVPViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateSwipeRight();
                    } else if (viewHolderForAdapterPosition instanceof RTSViewHolder) {
                        ((RTSViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateSwipeRight();
                    } else if (viewHolderForAdapterPosition instanceof EDSViewHolder) {
                        ((EDSViewHolder) viewHolderForAdapterPosition).getmBinding().swipeLayout.animateSwipeRight();
                    }
                }
            }
        }
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(filterShipments, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(filterShipments, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        if (viewHolder instanceof DRSListAdapter.EDSViewHolder) {
            DRSListAdapter.EDSViewHolder myViewHolder = (DRSListAdapter.EDSViewHolder) viewHolder;
            if (myViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                myViewHolder.getmBinding().swipeLayout.setOffset(itemsOffset[myViewHolder.getAdapterPosition()]);
            }
        } else if (viewHolder instanceof DRSListAdapter.ForwardViewHolder) {
            DRSListAdapter.ForwardViewHolder myViewHolder = (DRSListAdapter.ForwardViewHolder) viewHolder;
            if (myViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                myViewHolder.getmBinding().swipeLayout.setOffset(itemsOffset[myViewHolder.getAdapterPosition()]);
            }
        } else if (viewHolder instanceof DRSListAdapter.RTSViewHolder) {
            DRSListAdapter.RTSViewHolder myViewHolder = (DRSListAdapter.RTSViewHolder) viewHolder;
            if (myViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                myViewHolder.getmBinding().swipeLayout.setOffset(itemsOffset[myViewHolder.getAdapterPosition()]);
            }
        } else if (viewHolder instanceof DRSListAdapter.RVPViewHolder) {
            DRSListAdapter.RVPViewHolder myViewHolder = (DRSListAdapter.RVPViewHolder) viewHolder;
            if (myViewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                myViewHolder.getmBinding().swipeLayout.setOffset(itemsOffset[myViewHolder.getAdapterPosition()]);
            }
        }
    }

    @Override
    public void onRowSelected(BaseViewHolder viewHolder) {
        if (viewHolder instanceof DRSListAdapter.EDSViewHolder) {
            DRSListAdapter.EDSViewHolder myViewHolder = (DRSListAdapter.EDSViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.primary_grey_400);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.ForwardViewHolder) {
            DRSListAdapter.ForwardViewHolder myViewHolder = (DRSListAdapter.ForwardViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.primary_grey_400);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.RTSViewHolder) {
            DRSListAdapter.RTSViewHolder myViewHolder = (DRSListAdapter.RTSViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.primary_grey_400);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.RVPViewHolder) {
            DRSListAdapter.RVPViewHolder myViewHolder = (DRSListAdapter.RVPViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.primary_grey_400);
            myViewHolder.getmBinding().indicator.performClick();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRowClear(BaseViewHolder viewHolder) {
        if (viewHolder instanceof DRSListAdapter.EDSViewHolder) {
            DRSListAdapter.EDSViewHolder myViewHolder = (DRSListAdapter.EDSViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.offwhite);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.ForwardViewHolder) {
            DRSListAdapter.ForwardViewHolder myViewHolder = (DRSListAdapter.ForwardViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.offwhite);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.RTSViewHolder) {
            DRSListAdapter.RTSViewHolder myViewHolder = (DRSListAdapter.RTSViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.offwhite);
            myViewHolder.getmBinding().indicator.performClick();
        } else if (viewHolder instanceof DRSListAdapter.RVPViewHolder) {
            DRSListAdapter.RVPViewHolder myViewHolder = (DRSListAdapter.RVPViewHolder) viewHolder;
            myViewHolder.getmBinding().color.setBackgroundResource(R.color.offwhite);
            myViewHolder.getmBinding().indicator.performClick();
        }
        try {
            int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                viewHolder.itemView.post(() -> {
                    notifyItemChanged(adapterPosition);
                    updateNewDRSSequence(filterShipments);
                });
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void updateNewDRSSequence(List<CommonDRSListItem> filterShipments) {
        toDoListViewModel.saveDRSSequenceToTable(filterShipments);
    }

    public interface RowClickActionsListener {
        void onRemarksClicked(int position);
    }

    public static class EmptyViewHolder extends BaseViewHolder {
        private final ItemEmptyViewBinding itemEmptyViewBinding;

        public EmptyViewHolder(ItemEmptyViewBinding binding) {
            super(binding.getRoot());
            this.itemEmptyViewBinding = binding;
        }

        public ItemEmptyViewBinding getmBinding() {
            return this.itemEmptyViewBinding;
        }

        @Override
        public void onBind(int position) {
            DRSEmptyItemViewModel emptyItemViewModel = new DRSEmptyItemViewModel();
            itemEmptyViewBinding.setViewModel(emptyItemViewModel);
        }
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<CommonDRSListItem> list = mCommonDRSListItems;
            int count = list.size();
            final ArrayList<CommonDRSListItem> nList = new ArrayList<>(count);
            String filterableString;
            try {
                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).filterValue();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nList.add(list.get(i));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "ItemFilter():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Logger.e(TAG, String.valueOf(e));
            }
            results.values = nList;
            results.count = nList.size();
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                filterShipments.clear();
                checkbox_filter.clear();
                filterShipments.addAll((List<CommonDRSListItem>) results.values);
                itemsOffset = new int[filterShipments.size()];
                if (drsCallListener != null) {
                    drsCallListener.getSearchCheckedShipmentlist(true);
                }
                for (CommonDRSListItem drsListItem : filterShipments) {
                    if (drsListItem.getCommonDrsStatus() == 0) {
                        if (drsListItem.getType().equalsIgnoreCase(Constants.FWD)) {
                            checkbox_filter.put(drsListItem.getDrsForwardTypeResponse().getCompositeKey(), true);
                        }
                        if (drsListItem.getType().equalsIgnoreCase(Constants.RVP)) {
                            checkbox_filter.put(String.valueOf(drsListItem.getDrsReverseQCTypeResponse().getAwbNo()), true);
                        }
                        if (drsListItem.getType().equalsIgnoreCase(Constants.EDS)) {
                            checkbox_filter.put(String.valueOf(drsListItem.getEdsResponse().getAwbNo()), true);
                        }
                    }
                }
                if (drsCallListener != null) {
                    drsCallListener.getCheckedShipmentlist(checkbox_filter);
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    public class ForwardViewHolder extends BaseViewHolder implements DRSItemViewModelListener {

        private final ItemForwardListViewBinding mBinding;
        ForwardItemViewModel mForwardItemViewmodel;

        public ForwardViewHolder(ItemForwardListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public ItemForwardListViewBinding getmBinding() {
            return this.mBinding;
        }

        @Override
        public void onBind(int position) {
            try {
                final CommonDRSListItem commonDRSListItem = filterShipments.get(position);
                mForwardItemViewmodel = new ForwardItemViewModel(commonDRSListItem, this);
                mBinding.setViewModel(mForwardItemViewmodel);
                mBinding.executePendingBindings();
                mBinding.secureDelOpen.setTag(position);
                mBinding.secureDelLock.setTag(position);
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.checkboxCkb.setFocusable(false);
                toDoListViewModel.getDataManager().setAmazonList("");
                setVolumetricWeight(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getVolumetric_weight());

                // Change border color of the icon call and location
                if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_address_updated().equalsIgnoreCase("true")) {
                    mBinding.addressline1.setTextColor(ContextCompat.getColor(toDoListActivity, R.color.highlight_color));
                    mBinding.fullAddressCustomTextBold.setTextColor(ContextCompat.getColor(toDoListActivity, R.color.highlight_color));
                }
                if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_mobile_updated().equalsIgnoreCase("true")) {
                    mBinding.call.setBackground(ContextCompat.getDrawable(toDoListActivity, R.color.highlight_color));
                }
                if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_location_updated().equalsIgnoreCase("true")) {
                    mBinding.map.setBackground(ContextCompat.getDrawable(toDoListActivity, R.color.highlight_color));
                }

                if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed()) {
                    mBinding.call.setImageResource(R.drawable.ic_action_callbridge);
                } else {
                    mBinding.call.setImageResource(R.drawable.ic_phone_call_disabled);
                }
                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().isIs_obd()) {
                    mBinding.obdIcon.setVisibility(View.VISIBLE);
                } else {
                    mBinding.obdIcon.setVisibility(View.GONE);
                }
                mBinding.resechdicon.setOnClickListener(v -> showReassignDialog(commonDRSListItem));
                mBinding.checkboxCkb.setChecked(commonDRSListItem.isSmsCheckFlag());
                try {
                    if (commonDRSListItem.getDrsForwardTypeResponse().getFlags() != null) {
                        isDigital = commonDRSListItem.getDrsForwardTypeResponse().getFlags().getIsDigitalPaymentAllowed();
                        if (isDigital) {
                            mBinding.card.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.card.setVisibility(View.GONE);
                        }
                    } else {
                        mBinding.card.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    isDigital = false;
                    mBinding.card.setVisibility(View.GONE);
                }
                mBinding.viewDivider.setVisibility(!mForwardItemViewmodel.pin().isEmpty() ? View.VISIBLE : View.GONE);
                if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && commonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details() != null) {
                    mBinding.pin.setVisibility(View.VISIBLE);
                    mBinding.viewDivider.setVisibility(View.VISIBLE);
                } else {
                    mBinding.pin.setVisibility(View.INVISIBLE);
                    mBinding.viewDivider.setVisibility(View.INVISIBLE);

                }
                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() == 0) {
                    if (isCheckboxVisible) {
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.checkboxCkb.setGravity(Gravity.CENTER);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    } else {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                    }
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                    mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                }
                mBinding.checkboxCkb.setTag(commonDRSListItem);
                mBinding.checkboxCkb.setOnClickListener(view -> {
                    CommonDRSListItem commonDRSListItemTagged = (CommonDRSListItem) view.getTag();
                    changeCheckboxState(commonDRSListItemTagged);
                });
                IsProfileFound(commonDRSListItem);
                IsRemarksVisible(commonDRSListItem);
                checkShipmentStatus(commonDRSListItem, position);
                shouldShowReassignIcon(commonDRSListItem);
                IsCallAttempted(commonDRSListItem);
                mBinding.imageViewSynced.setVisibility(mForwardItemViewmodel.isItemSynced() ? View.VISIBLE : View.GONE);
                int counter = mForwardItemViewmodel.getMissedCallCounter();
                if (counter > 0) {
                    mBinding.missedcall.setText(String.valueOf(counter));
                    mBinding.missedcall.setVisibility(View.VISIBLE);
                } else {
                    mBinding.missedcall.setVisibility(View.GONE);
                }
                if (commonDRSListItem.isNewDRSAfterSync()) {
                    mBinding.newFlag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.newFlag.setVisibility(View.GONE);
                }
                mBinding.swipeLayout.setOffset(itemsOffset[position]);
                mBinding.swipeLayout.setSwipeEnabled(filterShipments.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS);
                mBinding.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                    @Override
                    public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                        resetAllOtherItemSwipe(position);
                    }

                    @Override
                    public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }
                });
                mBinding.remarksCard.setOnClickListener(v -> {
                    mBinding.swipeLayout.animateReset();
                    clickSwipeListener.onRemarksClicked(position);
                });
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward onBind():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private void showReassignDialog(CommonDRSListItem commonDRSListItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(toDoListActivity, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
            builder.setMessage("Are you sure you want to reassign/reattempt this shipment");
            builder.setPositiveButton("YES", (dialog, which) -> {
                dialog.dismiss();
                toDoListViewModel.callFwdReassingApi(commonDRSListItem);
            });
            builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            Dialog dialog = builder.create();
            dialog.show();
        }

        private void setVolumetricWeight(double shipmentWeight) {
            if (shipmentWeight > 0) {
                mBinding.shipmentWeightText.setVisibility(View.VISIBLE);
                String formattedWeight = String.format(Locale.US, "%.2f kg", Math.floor(shipmentWeight * 100) / 100);
                mBinding.shipmentWeightText.setText(formattedWeight);
            } else {
                mBinding.shipmentWeightText.setVisibility(View.GONE);
            }
        }

        private void shouldShowReassignIcon(CommonDRSListItem commonDRSListItem) {
            if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS && mForwardItemViewmodel.isItemSynced() && !toDoListViewModel.getDataManager().getFWD_UD_RD_OTPVerfied(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + "Forward")) {
                if (toDoListViewModel.getDataManager().getFWDRessign()) {
                    mBinding.resechdicon.setVisibility(View.VISIBLE);
                } else {
                    mBinding.resechdicon.setVisibility(View.GONE);
                }
            } else {
                mBinding.resechdicon.setVisibility(View.GONE);
            }
        }

        private void checkShipmentStatus(CommonDRSListItem commonDRSListItem, int position) {
            try {
                if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.fullview.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_UNDELIVERED);
                    try {
                        if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin() && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.fullview.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    try {
                        if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin() && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.drsiconbg));
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    try {
                        if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin() && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.GONE);
                                    mBinding.secureDelOpen.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.fullview.setBackgroundResource(R.drawable.delivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_DELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward checkshipmentstatus():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                commonDRSListItemFWDClick = commonDRSListItem;
                Constants.TEMP_OFD_OTP = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp();
                Constants.Water_Mark_Awb = String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
                Constants.ConsigneeDirectMobileNo = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getMobile();
                Constants.ConsigneeDirectAlternateMobileNo = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_mobile();

                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                    long awb = commonDRSListItem.getDrsForwardTypeResponse().getAwbNo();
                    try {
                        CompositeDisposable compositeDisposable = new CompositeDisposable();
                        compositeDisposable.add(toDoListViewModel.getDataManager().insetedOrNotinTable(awb).subscribeOn(toDoListViewModel.getSchedulerProvider().io()).observeOn(toDoListViewModel.getSchedulerProvider().io()).subscribe(pushApis -> {
                        }));
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                } else if (toDoListActivity.activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                    changeCheckboxState(commonDRSListItem);
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward onItemClicked():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            onForwardItemClick(commonDRSListItem);
        }

        @SuppressLint("NotifyDataSetChanged")
        private void changeCheckboxState(CommonDRSListItem commonDRSListItem) {
            try {
                commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getDrsForwardTypeResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                if (drsCallListener != null) {
                    drsCallListener.getCheckedShipmentlist(checkbox_filter);
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward changeCheckboxState():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private void onForwardItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                Constants.CancellationEnable = false;
                Constants.RCHDEnable = false;
                Constants.Wrong_Mobile_no = false;
                Constants.ConsigneeDirectMobileNo = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getMobile();
                Constants.ConsigneeDirectAlternateMobileNo = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_mobile();
                if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed()) {
                    Constants.Wrong_Mobile_no = true;
                }
                forward_is_item_clicked = false;
                toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getDrsForwardTypeResponse().getAssignedDate());
                toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id());
                toDoListViewModel.getDataManager().setPaymentType(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getType());
                Constants.CancellationEnable = commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getCancellation_enabled();
                Constants.RCHDEnable = commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getRCHD_enabled();
                in.ecomexpress.geolocations.Constants.latitude = toDoListViewModel.getDataManager().getCurrentLatitude();
                in.ecomexpress.geolocations.Constants.longitude = toDoListViewModel.getDataManager().getCurrentLongitude();
                if (mBinding.tick.getVisibility() == View.VISIBLE) {
                    Constants.CONSIGNEE_PROFILE = true;
                    if (commonDRSListItem.getProfileFound().getDelivery_latitude() == commonDRSListItem.getProfileFound().getDelivery_longitude() || commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0) {
                        CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location coordinates either null or not Found", toDoListActivity);
                        forwardClick(commonDRSListItem, mBinding);
                    } else {
                        double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                        if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            toDoListActivity.runOnUiThread(() -> getRestrictionForward(commonDRSListItem, mBinding, distance));
                        } else {
                            forwardClick(commonDRSListItem, mBinding);
                        }
                    }
                } else {
                    Constants.CONSIGNEE_PROFILE = false;
                    forwardClick(commonDRSListItem, mBinding);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward forwardItemClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        public void getRestrictionForward(CommonDRSListItem commonDRSListItem, ItemForwardListViewBinding mBinding, double distance) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.proceed);
                boolean isRestriction = false;
                String consigneeProfileValue = toDoListViewModel.getDataManager().getConsigneeProfileValue();
                if (consigneeProfileValue.equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.proceed_now);
                } else if (consigneeProfileValue.equalsIgnoreCase("R")) {
                    isRestriction = true;
                    dialog_message = "You are not allowed to commit this shipment as you are not at the consignee's location.\n\n"
                            + "📍 Distance from consignee: " + distance + " meters away\n\n"
                            + "🔹 Your Coordinates: " + toDoListViewModel.getDataManager().getCurrentLatitude()
                            + ", " + toDoListViewModel.getDataManager().getCurrentLongitude() + "\n"
                            + "🔹 Consignee Coordinates: " + commonDRSListItem.getProfileFound().getDelivery_latitude()
                            + ", " + commonDRSListItem.getProfileFound().getDelivery_longitude();
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                builder.setIcon(isRestriction ? R.drawable.restricted_sign : R.drawable.warning_sign);

                SpannableString title = new SpannableString(isRestriction ? "Restricted!" : "Warning!");
                title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(title);

                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (consigneeProfileValue.equalsIgnoreCase("R")) {
                        forward_is_item_clicked = true;
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                        forwardClick(commonDRSListItem, mBinding);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void forwardClick(CommonDRSListItem commonDRSListItem, ItemForwardListViewBinding mBinding) {
            toDoListViewModel.getDataManager().setAmazonOTPStatus("");
            toDoListViewModel.getDataManager().setAmazonOTPTiming(0L);
            toDoListViewModel.getDataManager().setPinBOTPStatus("");
            toDoListViewModel.getDataManager().setPinBOTPTimming(0L);
            toDoListViewModel.getDataManager().setDlightSuccessEncrptedOTPType("");
            isSecureDelivery = new SecureDelivery();
            getDrsPin = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getPin();
            getDrsApiKey = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCallbridgeApi();
            getDrsPstnKey = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCallbridgePstn();
            getOrderId = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOrder();
            getDrsId = String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId());
            String getCompleteAddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine1()) + ", " +
                    CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ", " +
                    CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ", " +
                    CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ", " +
                    CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getCity()) + ", " +
                    commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getPincode();
            try {
                isDigital = commonDRSListItem.getDrsForwardTypeResponse().getFlags().getIsDigitalPaymentAllowed();
            } catch (Exception e) {
                isDigital = false;
                Logger.e(TAG, String.valueOf(e));
            }
            Intent intent = null;
            if (commonDRSListItem.getDrsForwardTypeResponse().mpsShipment != null) {
                if (commonDRSListItem.getDrsForwardTypeResponse().mpsShipment.equalsIgnoreCase("P")) {
                    if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                        if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                            intent = MPSScanActivity.getStartIntent(mBinding.getRoot().getContext());
                            intent.putExtra(Constants.IS_CARD, isDigital);
                            intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                            intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                            intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                            intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                            intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                            intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                            intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                            intent.putExtra(Constants.ORDER_ID, getOrderId);
                            intent.putExtra(Constants.DRS_ID_NUM, commonDRSListItem.getDrsForwardTypeResponse().getDrsId());
                            intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                            intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                            intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                            intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                            intent.putExtra(Constants.MPS, commonDRSListItem.getDrsForwardTypeResponse().mpsShipment);
                            intent.putExtra(Constants.MPS_AWB_NOS, commonDRSListItem.getDrsForwardTypeResponse().mpsAWBs);
                            intent.putExtra(Constants.ORDER_ID, getOrderId);
                            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                            intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                            intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                        } else {
                            intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                            intent.putExtra(Constants.IS_CARD, isDigital);
                            intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                            intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                            intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                            intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                            intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                            intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                            intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                            intent.putExtra(Constants.ORDER_ID, getOrderId);
                            intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                            intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                            intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                            intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                            intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                            intent.putExtra(Constants.MPS, commonDRSListItem.getDrsForwardTypeResponse().mpsShipment);
                            intent.putExtra(Constants.MPS_AWB_NOS, commonDRSListItem.getDrsForwardTypeResponse().mpsAWBs);
                            intent.putExtra(Constants.ORDER_ID, getOrderId);
                            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                            intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                            intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                            intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                        }
                    } else {
                        intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                        intent.putExtra(Constants.IS_CARD, isDigital);
                        intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                        intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                        intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                        intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                        intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                        intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                        intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                        intent.putExtra(Constants.ORDER_ID, getOrderId);
                        intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                        intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                        intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                        intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                        intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                        intent.putExtra(Constants.MPS, commonDRSListItem.getDrsForwardTypeResponse().mpsShipment);
                        intent.putExtra(Constants.MPS_AWB_NOS, commonDRSListItem.getDrsForwardTypeResponse().mpsAWBs);
                        intent.putExtra(Constants.ORDER_ID, getOrderId);
                        intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                        intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                        intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                        intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                    }
                }
            } else if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin() && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false") && !commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().isIs_obd()) {
                    intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                    intent.putExtra(Constants.SHOW_FWD_UNDL_BTN, "Yes");
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                    intent.putExtra(Constants.IS_CARD, isDigital);
                    intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                    intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                    intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                    intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                    intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                    intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                    intent.putExtra(Constants.ORDER_ID, getOrderId);
                    intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                    intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                    intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                    intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                    intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                } else {
                    if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().isIs_obd()) {
                        intent = FwdOBDProductDetailActivity.getStartIntent(mBinding.getRoot().getContext());
                    } else {
                        intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                    }
                    intent.putExtra(Constants.OBD_CONSIGNEE_NAME, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getName());
                    intent.putExtra(Constants.OBD_CONSIGNEE_ADDRESS, getCompleteAddress);
                    intent.putExtra(Constants.OBD_VENDOR_NAME, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getVendorName());
                    intent.putExtra(Constants.OBD_ITEM_NAME, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getItem());
                    intent.putExtra(Constants.OBD_ADDRESS_PROFILED, commonDRSListItem.getDrsForwardTypeResponse().getAddress_profiled());
                    intent.putExtra(Constants.OBD_AWB_NUMBER, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo()));
                    intent.putExtra(Constants.IS_CARD, isDigital);
                    intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                    intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                    intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                    intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                    intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                    intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                    intent.putExtra(Constants.ORDER_ID, getOrderId);
                    intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                    intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                    intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                    intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                    intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
                }
            } else {
                intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                intent.putExtra(Constants.ORDER_ID, getOrderId);
                intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                intent.putExtra(Constants.SHOW_FWD_UNDL_BTN, "Yes");
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                intent.putExtra(Constants.IS_CARD, isDigital);
                intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
                intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                intent.putExtra(Constants.ORDER_ID, getOrderId);
                intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
                intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
                intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
                intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
                intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
            }
            intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId()));
            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
            intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery());
            intent.putExtra(Constants.SHIPMENT_TYPE, Constants.FWD);
            intent.putExtra(Constants.SHIPMENT_TYPE, Constants.FWD);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getOfd_otp());
            intent.putExtra("call_allowed", commonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed());
            intent.putExtra(Constants.ORDER_ID, getOrderId);
            intent.putExtra(Constants.sign_image_required, commonDRSListItem.getDrsForwardTypeResponse().getFlags().sign_image_required);
            intent.putExtra(Constants.return_package_barcode, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getReturn_package_barcode());
            intent.putExtra(Constants.FWD_DEL_IMAGE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().flagMap.getFwd_del_image());
            intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getPhone());
            intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_number());
            intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsForwardTypeResponse().getCompositeKey());
            intent.putExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getIs_amazon_reschedule_enabled());
            intent.putExtra(Constants.IS_CARD, isDigital);
            intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
            toDoListActivity.startActivity(intent);
            applyTransitionToOpenActivity(toDoListActivity);
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsForwardTypeResponse().getIsCallattempted() == Constants.callAttempted) {
                    mBinding.icCallattempted.setVisibility(View.VISIBLE);
                } else {
                    mBinding.icCallattempted.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward IsCallAttempted():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @SuppressLint("SetTextI18n")
        private void IsRemarksVisible(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getType().equalsIgnoreCase("COD")) {
                    mBinding.amount.setText("₹ " + commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCollectableValue().toString());
                } else {
                    mBinding.amount.setVisibility(View.GONE);
                    mBinding.amount.setText("₹ " + commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getDeclaredValue().toString());
                }
                Remark remark = commonDRSListItem.getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonDRSListItem.getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                toDoListViewModel.getRemarkForward(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo(), mBinding);
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward IsRemarksVisible():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void IsProfileFound(CommonDRSListItem commonDRSListItem) {
            try {
                ProfileFound profileFound = commonDRSListItem.getProfileFound();
                if (profileFound != null) {
                    mBinding.tick.setVisibility(View.VISIBLE);
                    mBinding.map.setVisibility(View.GONE);
                    mBinding.navView.setBackground(ctx.getResources().getDrawable(R.drawable.ic_action_trip));
                    if (profileFound.isRed_alert()) {
                        mBinding.imageRedAlert.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.imageRedAlert.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.tick.setVisibility(View.GONE);
                    mBinding.map.setVisibility(View.VISIBLE);
                    mBinding.navView.setBackgroundResource(R.drawable.forward);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onMapClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                    try {
                        Uri uri;
                        String getAddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine1()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getCity()) + ",\n" + commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getPincode();
                        String removeBetweenSpaces = getAddress.replaceAll("\\s+", " ").trim();
                        String finalAddress = removeBetweenSpaces.replaceAll(",", "").trim();
                        double lat = 0;
                        double lng = 0;
                        try {
                            lat = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();
                            lng = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLocation().getLng();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("68695") || String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("28446") || String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("222980")) {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        } else if (lng != 0.0 && lat != 0.0) {
                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                            uri = Uri.parse("http://maps.google.com/maps?saddr=" + toDoListViewModel.getlat() + "," + toDoListViewModel.getlng() + "&daddr=" + lat + "," + lng + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE());
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        }
                        forward_map_count = forward_map_count + 1;
                        toDoListViewModel.getDataManager().setForwardMapCount(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo(), forward_map_count);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", mBinding.getRoot().getContext())) {
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mBinding.getRoot().getContext().startActivity(intent);
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward onMapClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (!mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getCallAllowed()) {
                    Toast.makeText(toDoListActivity, toDoListActivity.getResources().getString(R.string.call_not_allowed), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }


                if (mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") && mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details() != null) {
                    try {
                        String callingFormat;
                        callingFormat = mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#";
                        Constants.call_pin = String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getCallbridge_details().get(0).getPin());
                        Constants.calling_format = callingFormat;
                        Constants.shipment_type = Constants.FWD;
                        if (callingFormat != null) {
                            Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_mobile();
                            forward_call_count = forward_call_count + 1;
                            toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + "ForwardCall", false);
                            startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getMobile(), callingFormat, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo(), mCommonDRSListItem.getDrsForwardTypeResponse().getDrsId());
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Please switch Number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(toDoListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, String.valueOf(e));
                    }
                } else {
                    forward_call_count = forward_call_count + 1;
                    toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo() + "ForwardCall", false);
                    Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAlternate_mobile();
                    startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo(), mCommonDRSListItem.getDrsForwardTypeResponse().getDrsId());
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward CallClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onIndicatorClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.fullview.getVisibility() == View.VISIBLE) {
                    mForwardItemViewmodel.setImage(true);
                    mBinding.fullview.setVisibility(View.GONE);
                    mBinding.moreorless.setText(R.string.more);
                } else {
                    mBinding.moreorless.setText(R.string.less);
                    mBinding.fullview.setVisibility(View.VISIBLE);
                    mForwardItemViewmodel.setImage(false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward onIndicatorClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRemarksAdded(CommonDRSListItem mCommonDRSListItem) {
            mBinding.remarks.setText(myRemarks);
        }

        @Override
        public void onTrayClick(CommonDRSListItem mCommonDRSListItem) {
            showIconDetails(mBinding.getRoot().getContext());
        }

        private void showIconDetails(Context context) {
            try {
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(R.layout.activity_drs_icons_dialog);
                ImageView dialogButton = dialog.findViewById(R.id.cross);
                Objects.requireNonNull(dialogButton).setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class RTSViewHolder extends BaseViewHolder implements DRSItemViewModelListener {

        private final ItemNewRtsListViewBinding mBinding;
        RTSNewItemViewModel mRTSNewItemViewModel;

        public RTSViewHolder(ItemNewRtsListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public ItemNewRtsListViewBinding getmBinding() {
            return this.mBinding;
        }

        @Override
        public void onBind(int position) {
            try {
                HashSet<CommonDRSListItem> rtsSet = new LinkedHashSet<>(filterShipments);
                ArrayList<CommonDRSListItem> rtsList = new ArrayList<>(rtsSet);
                final CommonDRSListItem commonDRSListItem = rtsList.get(position);
                mRTSNewItemViewModel = new RTSNewItemViewModel(commonDRSListItem, this);
                mBinding.setViewModel(mRTSNewItemViewModel);
                mBinding.addressline1.setVisibility(View.VISIBLE);
                mBinding.imageViewSyncStatus.setVisibility(mRTSNewItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                Remark remark = commonDRSListItem.getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonDRSListItem.getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }

                IsCallAttempted(commonDRSListItem);
                if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_UNDELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.rts_light));
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_DELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
                if (commonDRSListItem.isNewDRSAfterSync()) {
                    mBinding.newFlag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.newFlag.setVisibility(View.GONE);
                }
                mBinding.swipeLayout.setSwipeEnabled(filterShipments.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS);
                mBinding.swipeLayout.setOffset(itemsOffset[position]);
                mBinding.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                    @Override
                    public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                        resetAllOtherItemSwipe(position);
                    }

                    @Override
                    public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }
                });
                mBinding.remarksCard.setOnClickListener(v -> {
                    mBinding.swipeLayout.animateReset();
                    clickSwipeListener.onRemarksClicked(position);
                });
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RTS onBind():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                } else if (toDoListActivity.activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                    return;
                }
                String rtsAddress = getRTSMoreAddress(commonDRSListItem.getIRTSInterface());
                String finalAddress = rtsAddress.replaceAll(",,", ",");
                if (!TextUtils.isEmpty(commonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_email())) {
                    Constants.SUB_SHIPPER_EMAIL = commonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_email();
                } else {
                    Constants.SUB_SHIPPER_EMAIL = "";
                }
                RTSActivitiesData rtsActivitiesData = new RTSActivitiesData();
                rtsActivitiesData.setAddress(finalAddress);
                rtsActivitiesData.setConsigneeMobile(commonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_mobile());
                rtsActivitiesData.setRtsVWDetailID(commonDRSListItem.getIRTSInterface().getDetails().getId());
                Intent intent = RTSListActivity.getStartIntent(mBinding.getRoot().getContext());
                intent.putExtra("rtsActivitiesData", rtsActivitiesData);
                toDoListActivity.startActivity(intent);
                applyTransitionToOpenActivity(toDoListActivity);
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RTS onItemClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private String getRTSMoreAddress(IRTSBaseInterface response) {
            try {
                String line1 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine1());
                String line2 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine2());
                String line3 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine3());
                String line4 = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getLine4());
                String city = CommonUtils.nullToEmpty(response.getDetails().getRtsAddress().getCity());
                String pinCode = String.valueOf(response.getDetails().getRtsAddress().getPincode());
                return (!line1.isEmpty() ? (line1 + ",\n") : "") + (!line2.isEmpty() ? (line2 + ",\n") : "") + (!line3.isEmpty() ? (line3 + ",\n") : "") + (!line4.isEmpty() ? (line4 + ",\n") : "") + (!city.isEmpty() ? (city + ",\n") : "") + pinCode;
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward getRTSMoreAddress():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            return "Not Valid Address Defined..";
        }

        @Override
        public void onMapClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                    try {
                        Uri uri;
                        String getAddress = getRTSMoreAddress(commonDRSListItem.getIRTSInterface());
                        String removeBetweenSpaces = getAddress.replaceAll("\\s+", " ").trim();
                        String finalAddress = removeBetweenSpaces.replaceAll(",", "").trim();
                        double lat = 0;
                        double lng = 0;
                        try {
                            lat = commonDRSListItem.getIRTSInterface().getDetails().getLocation().getLat();
                            lng = commonDRSListItem.getIRTSInterface().getDetails().getLocation().getLong();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (lng != 0.0 && lat != 0.0) {
                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                            uri = Uri.parse("http://maps.google.com/maps?saddr=" + toDoListViewModel.getlat() + "," + toDoListViewModel.getlng() + "&daddr=" + lat + "," + lng + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE());
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        }
                        rts_map_count = rts_map_count + 1;
                        toDoListViewModel.getDataManager().setRTSMapCount(commonDRSListItem.getIRTSInterface().getDetails().getId(), rts_map_count);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", mBinding.getRoot().getContext())) {
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mBinding.getRoot().getContext().startActivity(intent);
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward onMapClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getIRTSInterface().getDetails().getIsCallattempted() == Constants.callAttempted) {
                    mBinding.isCallAttempted.setVisibility(View.VISIBLE);
                } else {
                    mBinding.isCallAttempted.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "Forward IsCallAttempted():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (mCommonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!TextUtils.isEmpty(mCommonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_mobile())) {
                    ConsigneeDirectAlternateMobileNo = "0";
                    startCallIntent(mCommonDRSListItem, "false", mCommonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_mobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getIRTSInterface().getDetails().getId(), 0);
                    return;
                }
                try {
                    ConsigneeDirectAlternateMobileNo = "0";
                    if (!TextUtils.isEmpty(mCommonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_mobile())) {
                        rts_call_count = rts_call_count + 1;
                        Constants.call_awb = String.valueOf(mCommonDRSListItem.getIRTSInterface().getDetails().getId());
                        Constants.shipment_type = Constants.RTS;
                        startCallIntent(mCommonDRSListItem, "false", mCommonDRSListItem.getIRTSInterface().getDetails().getSub_shipper_mobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getIRTSInterface().getDetails().getId(), 0);
                        mBinding.call.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.call.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Toast.makeText(toDoListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Logger.e(TAG, String.valueOf(e));
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RTS onCallClick" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onIndicatorClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getIRTSInterface().getDetails().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.fullview.getVisibility() == View.VISIBLE) {
                    mRTSNewItemViewModel.setImage(true);
                    mBinding.fullview.setVisibility(View.GONE);
                    mBinding.moreorless.setText(R.string.more);
                } else {
                    mBinding.moreorless.setText(R.string.less);
                    mBinding.fullview.setVisibility(View.VISIBLE);
                    mRTSNewItemViewModel.setImage(false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RTS onIndicatorClick" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onRemarksAdded(CommonDRSListItem mCommonDRSListItem) {
        }

        @Override
        public void onTrayClick(CommonDRSListItem mCommonDRSListItem) {
            showIconDetails(mBinding.getRoot().getContext());
        }

        private void showIconDetails(Context context) {
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
            Objects.requireNonNull(dialogButton).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    public class RVPViewHolder extends BaseViewHolder implements DRSItemViewModelListener {

        private final ItemRvpListViewBinding mBinding;
        RVPItemViewModel mRVPItemViewModel;

        public RVPViewHolder(ItemRvpListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public ItemRvpListViewBinding getmBinding() {
            return this.mBinding;
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        @Override
        public void onBind(int position) {
            try {
                slide_down = AnimationUtils.loadAnimation(mBinding.getRoot().getContext(), R.anim.bounce_in_down);
                final CommonDRSListItem commonDRSListItem = filterShipments.get(position);
                mRVPItemViewModel = new RVPItemViewModel(commonDRSListItem, this);
                mBinding.setViewModel(mRVPItemViewModel);
                mBinding.viewDivider.setVisibility(!mRVPItemViewModel.getPin().isEmpty() ? View.VISIBLE : View.GONE);
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && commonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details() != null) {
                    mBinding.pin.setVisibility(View.VISIBLE);
                    mBinding.viewDivider.setVisibility(View.VISIBLE);
                } else {
                    mBinding.pin.setVisibility(View.INVISIBLE);
                    mBinding.viewDivider.setVisibility(View.INVISIBLE);
                }
                try {
                    toDoListViewModel.checkRVPImageDownloadedORNot(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }

                //change border color of the icon call and location
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_address_updated().equalsIgnoreCase("true")) {
                    mBinding.addressline1.setTextColor(ContextCompat.getColor(toDoListActivity, R.color.highlight_color));
                    mBinding.fullAddressCustomTextBold.setTextColor(ContextCompat.getColor(toDoListActivity, R.color.highlight_color));
                }
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_mobile_updated().equalsIgnoreCase("true")) {
                    mBinding.call.setBackground(ContextCompat.getDrawable(toDoListActivity, R.color.highlight_color));
                }
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_location_updated().equalsIgnoreCase("true")) {
                    mBinding.map.setBackground(ContextCompat.getDrawable(toDoListActivity, R.color.highlight_color));
                }

                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.secureDelLock.setTag(position);
                mBinding.secureDelOpen.setTag(position);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.checkboxCkb.setChecked(commonDRSListItem.isSmsCheckFlag());
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed()) {
                    mBinding.call.setImageResource(R.drawable.ic_action_callbridge);
                } else {
                    mBinding.call.setImageResource(R.drawable.ic_phone_call_disabled);
                }
                IsProfileFound(commonDRSListItem);
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() == 0) {
                    if (isCheckboxVisible) {
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.checkboxCkb.setGravity(Gravity.CENTER);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    } else {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                    }
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                }
                mBinding.checkboxCkb.setOnClickListener(view -> {
                    commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                    checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getDrsReverseQCTypeResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                    drsCallListener.getCheckedShipmentlist(checkbox_filter);
                    notifyDataSetChanged();
                });
                if (!commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getQualityChecks().isEmpty()) {
                    mBinding.qc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.qc.setVisibility(View.GONE);
                }
                mBinding.imageViewSyncStatus.setVisibility(mRVPItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                mBinding.awb.setText(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString());
                IsCallAttempted(commonDRSListItem);
                Remark remark = commonDRSListItem.getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonDRSListItem.getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                toDoListViewModel.getRemarkRVP(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo(), mBinding);
                if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.moreorless.setText("Pickup Failed");
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));
                    try {
                        if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.rvplight));
                    try {
                        if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    try {
                        if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.GONE);
                                    mBinding.secureDelOpen.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.moreorless.setText("Picked Up");
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
                if (commonDRSListItem.isNewDRSAfterSync()) {
                    mBinding.newFlag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.newFlag.setVisibility(View.GONE);
                }
                mBinding.swipeLayout.setSwipeEnabled(filterShipments.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS);
                mBinding.swipeLayout.setOffset(itemsOffset[position]);
                mBinding.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                    @Override
                    public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                        resetAllOtherItemSwipe(position);
                    }

                    @Override
                    public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }
                });
                mBinding.remarksCard.setOnClickListener(v -> {
                    mBinding.swipeLayout.animateReset();
                    clickSwipeListener.onRemarksClicked(position);
                });
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onBind():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void IsProfileFound(CommonDRSListItem commonDRSListItem) {
            try {
                ProfileFound profileFound = commonDRSListItem.getProfileFound();
                if (profileFound != null) {
                    mBinding.tick.setVisibility(View.VISIBLE);
                    mBinding.map.setVisibility(View.GONE);
                    mBinding.navView.setBackground(ctx.getResources().getDrawable(R.drawable.ic_action_trip));
                    if (profileFound.isRed_alert()) {
                        mBinding.imageRedAlert.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.imageRedAlert.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.tick.setVisibility(View.GONE);
                    mBinding.map.setVisibility(View.VISIBLE);
                    mBinding.navView.setBackgroundResource(R.drawable.rvp);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void changeCheckboxState(CommonDRSListItem commonDRSListItem) {
            try {
                commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getDrsReverseQCTypeResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                drsCallListener.getCheckedShipmentlist(checkbox_filter);
                notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP changeCheckboxState():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            commonDRSListItemRVPClick = commonDRSListItem;
            Constants.TEMP_OFD_OTP = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getOfd_otp();
            Constants.Water_Mark_Awb = String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
            try {
                Constants.CancellationEnable = false;
                Constants.RCHDEnable = false;
                Constants.Wrong_Mobile_no = false;
                Constants.ConsigneeDirectMobileNo = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile();
                Constants.ConsigneeDirectAlternateMobileNo = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAlternate_mobile();
                if (!commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed()) {
                    Constants.Wrong_Mobile_no = true;
                }
                rvp_is_item_clicked = false;
                toDoListViewModel.getDataManager().setOFDOTPVerifiedStatus("NONE");
                toDoListViewModel.getDataManager().setRVPSecureOTPVerified("false");
                toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getDrsReverseQCTypeResponse().getAssignedDate());
                toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getShipper_id());
                Constants.CancellationEnable = commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getCancellation_enabled();
                Constants.RCHDEnable = commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getRCHD_enabled();
                in.ecomexpress.geolocations.Constants.latitude = toDoListViewModel.getDataManager().getCurrentLatitude();
                in.ecomexpress.geolocations.Constants.longitude = toDoListViewModel.getDataManager().getCurrentLongitude();
                if (mBinding.tick.getVisibility() == View.VISIBLE) {
                    Constants.CONSIGNEE_PROFILE = true;
                    if (commonDRSListItem.getProfileFound().getDelivery_latitude() == commonDRSListItem.getProfileFound().getDelivery_longitude() || commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0) {
                        CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location coordinates either null or not Found", toDoListActivity);
                        RVPClick(commonDRSListItem, mBinding, true);
                    } else {
                        double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                        if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            toDoListActivity.runOnUiThread(() -> getRestrictionRVP(commonDRSListItem, mBinding, distance));
                        } else {
                            RVPClick(commonDRSListItem, mBinding, true);
                        }
                    }
                } else {
                    Constants.CONSIGNEE_PROFILE = false;
                    RVPClick(commonDRSListItem, mBinding, false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onItemClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void getRestrictionRVP(CommonDRSListItem commonDRSListItem, ItemRvpListViewBinding mBinding, double distance) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.proceed_now);
                String consigneeProfileValue = toDoListViewModel.getDataManager().getConsigneeProfileValue();
                boolean isRestriction = false;

                if (consigneeProfileValue.equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.proceed_now);
                } else if (consigneeProfileValue.equalsIgnoreCase("R")) {
                    isRestriction = true;
                    dialog_message = "You are not allowed to commit this shipment as you are not at the consignee's location.\n\n"
                            + "📍 Distance from consignee: " + distance + " meters away\n\n"
                            + "🔹 Your Coordinates: " + toDoListViewModel.getDataManager().getCurrentLatitude()
                            + ", " + toDoListViewModel.getDataManager().getCurrentLongitude() + "\n"
                            + "🔹 Consignee Coordinates: " + commonDRSListItem.getProfileFound().getDelivery_latitude()
                            + ", " + commonDRSListItem.getProfileFound().getDelivery_longitude();
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                builder.setIcon(isRestriction ? R.drawable.restricted_sign : R.drawable.warning_sign);

                SpannableString title = new SpannableString(isRestriction ? "Restricted!" : "Warning!");
                title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(title);

                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (consigneeProfileValue.equalsIgnoreCase("R")) {
                        rvp_is_item_clicked = true;
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                        RVPClick(commonDRSListItem, mBinding, true);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void RVPClick(CommonDRSListItem commonDRSListItem, ItemRvpListViewBinding mBinding, boolean isConsigneeLocationVerified) {
            Constants.TEMP_DRSID = commonDRSListItem.getDrsReverseQCTypeResponse().getDrs();
            Constants.TEMP_CONSIGNEE_MOBILE = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getPhone();
            if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                long awb = commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo();
                try {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    compositeDisposable.add(toDoListViewModel.getDataManager().insetedOrNotinTable(awb).subscribeOn(toDoListViewModel.getSchedulerProvider().io()).observeOn(toDoListViewModel.getSchedulerProvider().io()).subscribe(pushApis -> {
                    }));
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            } else if (toDoListActivity.activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                changeCheckboxState(commonDRSListItem);
                return;
            }
            try {
                Constants.RVPCOMMIT = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getType();
                getDrsPin = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getPin();
                getDrsId = String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getDrs());
                getDrsApiKey = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridge_Api();
                getDrsPstnKey = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridgePstn();
                Constants.RVP_Sign_Image_Required = commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().isSign_image_required();
                Intent intent = new Intent(toDoListActivity, PickupActivity.class);
                intent.putExtra(Constants.DRS_ID, String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getDrs()));
                intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery());
                intent.putExtra(Constants.SECURE_DELIVERY_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP());
                intent.putExtra(Constants.SHIPMENT_TYPE, commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getType());
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.ITEM_DESCRIPTION, commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getItem() != null
                        ? commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getItem()
                        : mBinding.getRoot().getContext().getString(R.string.no_item_description_found)
                );
                intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getOfd_otp());
                intent.putExtra(Constants.CALL_ALLOWED, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed());
                intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile());
                intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getAmazonEncryptedOtp());
                intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsReverseQCTypeResponse().getAmazon());
                intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getCompositeKey());
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.AWB_NUMBER, String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo()));
                intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getResend_otp_enable());
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAlternate_number());
                intent.putExtra(Constants.CONSIGNEE_NAME, commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getName());
                intent.putExtra(Constants.CONSIGNEE_LOCATION_VERIFIED, isConsigneeLocationVerified);
                intent.putExtra(Constants.SMART_QC_ENABLED, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getSmart_qc());
                intent.putExtra(Constants.IS_RVP_PHONEPE_SHIPMENT, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_rvp_phone_pe_flow());
                toDoListActivity.startActivity(intent);
                applyTransitionToOpenActivity(toDoListActivity);
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onMapClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                    try {
                        Uri uri;
                        String getAddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine1()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getCity()) + ",\n" + commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getPincode();
                        String removeBetweenSpaces = getAddress.replaceAll("\\s+", " ").trim();
                        String finalAddress = removeBetweenSpaces.replaceAll(",", "").trim();
                        double lat = 0;
                        double lng = 0;
                        try {
                            lat = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();
                            lng = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLocation().getLng();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (lng != 0.0 && lat != 0.0) {
                            uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", toDoListViewModel.getlat(), toDoListViewModel.getlng(), "MyLocation", lat, lng, "Destination"));
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        }
                        rvp_map_count = rvp_map_count + 1;
                        toDoListViewModel.getDataManager().setRVPMapCount(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo(), rvp_map_count);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", mBinding.getRoot().getContext())) {
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mBinding.getRoot().getContext().startActivity(intent);
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP MapClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getIsCallattempted() != 0) {
                    if (commonDRSListItem.getDrsReverseQCTypeResponse().getIsCallattempted() == Constants.callAttempted) {
                        mBinding.icCallattempted.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.icCallattempted.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP IsCallAttempted():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (!mCommonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getCallAllowed()) {
                    Toast.makeText(toDoListActivity, toDoListActivity.getResources().getString(R.string.call_not_allowed), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mCommonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true") && mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details() != null) {
                    try {
                        String callingformat;
                        callingformat = mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin().substring(4) + "#";
                        Constants.call_pin = String.valueOf(mCommonDRSListItem.getDrsReverseQCTypeResponse().getCallbridge_details().get(0).getPin());
                        Constants.calling_format = callingformat;
                        Constants.shipment_type = Constants.RVP;
                        if (callingformat != null) {
                            Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAlternate_mobile();
                            rvp_call_count = rvp_call_count + 1;
                            toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo() + "RVPCall", false);
                            startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile(), callingformat, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getDrs());
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Please switch Number", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(toDoListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    rvp_call_count = rvp_call_count + 1;
                    toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo() + "RVPCall", false);
                    Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAlternate_mobile();
                    startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getDrs());
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP Call Click():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onIndicatorClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.fullview.getVisibility() == View.VISIBLE) {
                    mRVPItemViewModel.setImage(true);
                    mBinding.fullview.setVisibility(View.GONE);
                    mBinding.moreorless.setText(R.string.more);
                } else {
                    mBinding.moreorless.setText(R.string.less);
                    mBinding.fullview.setVisibility(View.VISIBLE);
                    mRVPItemViewModel.setImage(false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onIndicatorClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onRemarksAdded(CommonDRSListItem mCommonDRSListItem) {

        }

        @Override
        public void onTrayClick(CommonDRSListItem mCommonDRSListItem) {
            showIconDetails(mBinding.getRoot().getContext());
        }

        private void showIconDetails(Context context) {
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
            Objects.requireNonNull(dialogButton).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    /*RVP MPS*/
    public class RVPMPSViewHolder extends BaseViewHolder implements DRSItemViewModelListener {
        private final ItemRvpMpsListViewBinding mBinding;
        RVPMPSItemViewModel mRVPMPSItemViewModel;

        public RVPMPSViewHolder(ItemRvpMpsListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public ItemRvpMpsListViewBinding getmBinding() {
            return this.mBinding;
        }

        @Override
        public void onBind(int position) {
            try {
                slide_down = AnimationUtils.loadAnimation(mBinding.getRoot().getContext(), R.anim.bounce_in_down);
                final CommonDRSListItem commonDRSListItem = filterShipments.get(position);
                mRVPMPSItemViewModel = new RVPMPSItemViewModel(commonDRSListItem, this);
                mBinding.setViewModel(mRVPMPSItemViewModel);
                mBinding.viewDivider.setVisibility(!mRVPMPSItemViewModel.getPin().isEmpty() ? View.VISIBLE : View.GONE);
                if (toDoListViewModel.getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true")) {
                    mBinding.pin.setVisibility(View.INVISIBLE);
                    mBinding.viewDivider.setVisibility(View.INVISIBLE);
                } else {
                    mBinding.pin.setVisibility(View.VISIBLE);
                    mBinding.viewDivider.setVisibility(View.VISIBLE);
                }
                try {
                    toDoListViewModel.checkRVPImageDownloadedORNot(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo());
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.secureDelLock.setTag(position);
                mBinding.secureDelOpen.setTag(position);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.checkboxCkb.setChecked(commonDRSListItem.isSmsCheckFlag());
                if (commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getCallAllowed()) {
                    mBinding.call.setImageResource(R.drawable.ic_action_callbridge);
                } else {
                    mBinding.call.setImageResource(R.drawable.ic_phone_call_disabled);
                }
                IsProfileFound(commonDRSListItem);
                if (commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus() == 0) {
                    if (isCheckboxVisible) {
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.checkboxCkb.setGravity(Gravity.CENTER);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    } else {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                    }
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                }
                mBinding.checkboxCkb.setOnClickListener(view -> {
                    commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                    checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getDrsRvpQcMpsResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                    drsCallListener.getCheckedShipmentlist(checkbox_filter);
                    notifyDataSetChanged();
                });
                if (!commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getQcItems().isEmpty()) {
                    mBinding.qc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.qc.setVisibility(View.GONE);
                }
                mBinding.imageViewSyncStatus.setVisibility(mRVPMPSItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                mBinding.awb.setText(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString());
                IsCallAttempted(commonDRSListItem);
                Remark remark = commonDRSListItem.getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonDRSListItem.getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                toDoListViewModel.getRemarkRVPMPS(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo(), mBinding);
                if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.moreorless.setText("Pickup Failed");
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));
                    try {
                        if (commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getPinb() && commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getOTP() == false && commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin() == false) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.rvplight));
                    try {
                        if (commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                } else if (commonDRSListItem.getCommonDrsStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    try {
                        if (commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getPinb() && !commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getOTP() && !commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.GONE);
                                    mBinding.secureDelOpen.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.moreorless.setText("Picked Up");
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
                if (commonDRSListItem.isNewDRSAfterSync()) {
                    mBinding.newFlag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.newFlag.setVisibility(View.GONE);
                }
                //RVP
                mBinding.swipeLayout.setSwipeEnabled(filterShipments.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS);
                mBinding.swipeLayout.setOffset(itemsOffset[position]);
                mBinding.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                    @Override
                    public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {
                        Log.d("", "onBeginSwipe: current item position" + position);
                    }

                    @Override
                    public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                        resetAllOtherItemSwipe(position);
                    }

                    @Override
                    public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                    }

                    @Override
                    public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {
                    }
                });
                mBinding.remarksCard.setOnClickListener(v -> {
                    mBinding.swipeLayout.animateReset();
                    clickSwipeListener.onRemarksClicked(position);
                });
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onBind():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private void IsProfileFound(CommonDRSListItem commonDRSListItem) {
            try {
                ProfileFound profileFound = commonDRSListItem.getProfileFound();
                if (profileFound != null) {
                    mBinding.tick.setVisibility(View.VISIBLE);
                    mBinding.map.setVisibility(View.GONE);
                    mBinding.navView.setBackground(ctx.getResources().getDrawable(R.drawable.ic_action_trip));
                    if (profileFound.isRed_alert()) {
                        mBinding.imageRedAlert.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.imageRedAlert.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.tick.setVisibility(View.GONE);
                    mBinding.map.setVisibility(View.VISIBLE);
                    mBinding.navView.setBackgroundResource(R.drawable.rvp);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void changeCheckboxState(CommonDRSListItem commonDRSListItem) {
            try {
                commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getDrsRvpQcMpsResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                drsCallListener.getCheckedShipmentlist(checkbox_filter);
                notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP changeCheckboxState():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        // RVP-MPS
        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            commonDRSListItemRVPClick = commonDRSListItem;
            Constants.TEMP_OFD_OTP = commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getOfd_otp();
            Constants.Water_Mark_Awb = String.valueOf(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo());
            try {
                //if(rvp_is_item_clicked){
                Constants.CancellationEnable = false;
                Constants.RCHDEnable = false;
                Constants.Wrong_Mobile_no = false;
                Constants.ConsigneeDirectMobileNo = commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getMobile();
                Constants.ConsigneeDirectAlternateMobileNo = commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAlternate_mobile();
                if (!commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getCallAllowed()) {
                    Constants.Wrong_Mobile_no = true;
                }
                rvp_is_item_clicked = false;
                toDoListViewModel.getDataManager().setOFDOTPVerifiedStatus("NONE");
                toDoListViewModel.getDataManager().setRVPSecureOTPVerified("false");
                toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getDrsRvpQcMpsResponse().getAssignedDate());
                toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getShipper_id());
                Constants.CancellationEnable = commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getCancellation_enabled();
                Constants.RCHDEnable = commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getRCHD_enabled();
                in.ecomexpress.geolocations.Constants.latitude = toDoListViewModel.getDataManager().getCurrentLatitude();
                in.ecomexpress.geolocations.Constants.longitude = toDoListViewModel.getDataManager().getCurrentLongitude();
                if (mBinding.tick.getVisibility() == View.VISIBLE) {
                    Constants.CONSIGNEE_PROFILE = true;
                    if (commonDRSListItem.getProfileFound().getDelivery_latitude() == commonDRSListItem.getProfileFound().getDelivery_longitude() || commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0) {
                        CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location coordinates either null or not Found", toDoListActivity);
                        RVPMPSClick(commonDRSListItem, mBinding);
                    } else {
                        double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                        if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            toDoListActivity.runOnUiThread(() -> getRestrictionRVPMPS(commonDRSListItem, mBinding, distance));
                        } else {
                            RVPMPSClick(commonDRSListItem, mBinding);
                        }
                    }
                } else {
                    Constants.CONSIGNEE_PROFILE = false;
                    RVPMPSClick(commonDRSListItem, mBinding);
                }
                //  }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onItemClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void getRestrictionRVPMPS(CommonDRSListItem commonDRSListItem, ItemRvpMpsListViewBinding mBinding, double distance) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.proceed_now);

                String consigneeProfileValue = toDoListViewModel.getDataManager().getConsigneeProfileValue();
                boolean isRestriction = false;

                if (consigneeProfileValue.equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.proceed_now);
                } else if (consigneeProfileValue.equalsIgnoreCase("R")) {
                    isRestriction = true;
                    dialog_message = "You are not allowed to commit this shipment as you are not at the consignee's location.\n\n"
                            + "📍 Distance from consignee: " + distance + " meters away\n\n"
                            + "🔹 Your Coordinates: " + toDoListViewModel.getDataManager().getCurrentLatitude()
                            + ", " + toDoListViewModel.getDataManager().getCurrentLongitude() + "\n"
                            + "🔹 Consignee Coordinates: " + commonDRSListItem.getProfileFound().getDelivery_latitude()
                            + ", " + commonDRSListItem.getProfileFound().getDelivery_longitude();
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                builder.setIcon(isRestriction ? R.drawable.restricted_sign : R.drawable.warning_sign);

                // Format title as bold and black
                SpannableString title = new SpannableString(isRestriction ? "Restricted!" : "Warning!");
                title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(title);

                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (consigneeProfileValue.equalsIgnoreCase("R")) {
                        rvp_is_item_clicked = true;
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                        RVPMPSClick(commonDRSListItem, mBinding);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void RVPMPSClick(CommonDRSListItem commonDRSListItem, ItemRvpMpsListViewBinding mBinding) {
            if (commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus() > 0) {
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            } else if (toDoListActivity.activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                changeCheckboxState(commonDRSListItem);
                return;
            }
            try {
                Constants.TEMP_DRSID = commonDRSListItem.getDrsRvpQcMpsResponse().getDrs();
                getDrsPin = commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getPin();
                getDrsId = String.valueOf(commonDRSListItem.getDrsRvpQcMpsResponse().getDrs());
                getDrsApiKey = commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getCallbridge_Api();
                getDrsPstnKey = commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getCallbridgePstn();
                Constants.RVP_Sign_Image_Required = commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().isSign_image_required();
                Intent intent = new Intent(toDoListActivity, MpsPickupActivity.class);
                intent.putExtra(Constants.DRS_ID, getDrsId);
                intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery());
                intent.putExtra(Constants.SECURE_DELIVERY_OTP, commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getOTP());
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getOfd_otp());
                intent.putExtra(Constants.CALL_ALLOWED, commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getCallAllowed());
                intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getMobile());
                intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsRvpQcMpsResponse().getAmazonEncryptedOtp());
                intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsRvpQcMpsResponse().getAmazon());
                intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsRvpQcMpsResponse().getCompositeKey());
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.AWB_NUMBER, String.valueOf(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo()));
                intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getSecure_delivery().getResend_otp_enable());
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAlternate_number());
                intent.putExtra(Constants.CONSIGNEE_NAME, commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getName());
                toDoListActivity.startActivity(intent);
                applyTransitionToOpenActivity(toDoListActivity);
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // RVP MPS
        @Override
        public void onMapClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                    try {
                        Uri uri = null;
                        String getaddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine1()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine2()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine3()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLine4()) + ",\n" + CommonUtils.nullToEmpty(commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getCity()) + ",\n" + commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getPincode();
                        String removeBetweenSpaces = getaddress.replaceAll("\\s+", " ").trim();
                        String finalAddress = removeBetweenSpaces.replaceAll(",", "").trim();
                        double lat = 0;
                        double lng = 0;
                        try {
                            lat = commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLocation().getLat();
                            lng = commonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAddress().getLocation().getLng();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (lng != 0.0 && lat != 0.0) {
                            uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", toDoListViewModel.getlat(), toDoListViewModel.getlng(), "MyLocation", lat, lng, "Destination"));
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        }
                        rvp_map_count = rvp_map_count + 1;
                        toDoListViewModel.getDataManager().setRVPMapCount(commonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo(), rvp_map_count);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", mBinding.getRoot().getContext())) {
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mBinding.getRoot().getContext().startActivity(intent);
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP MapClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsRvpQcMpsResponse().getIsCallattempted() != 0) {
                    if (commonDRSListItem.getDrsRvpQcMpsResponse().getIsCallattempted() == Constants.callAttempted) {
                        mBinding.icCallattempted.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.icCallattempted.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP IsCallAttempted():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (!mCommonDRSListItem.getDrsRvpQcMpsResponse().getFlags().getCallAllowed()) {
                    Toast.makeText(toDoListActivity, toDoListActivity.getResources().getString(R.string.call_not_allowed), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (toDoListViewModel.getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true")) {
                    rvp_call_count = rvp_call_count + 1;
                    toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo() + "RVPCall", false);
                    Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAlternate_mobile();
                    startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getDrs());
                    return;
                }
                try {
                    getDrsPstnKey = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getCallbridgePstn();
                    getDrsApiKey = mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getCallbridge_Api();
                    if (getDrsPstnKey != null) {
                        Constants.call_awb = mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString();
                        Constants.shipment_type = Constants.RVP;
                        Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAlternate_mobile();
                        rvp_call_count = rvp_call_count + 1;
                        toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo() + "RVPCall", false);
                        startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getDrs());
                    } else if (getDrsApiKey != null) {
                        callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                        if (callbridgeConfiguration != null) {
                            String getApiType = callbridgeConfiguration.getCb_calling_api();
                            if (getApiType != null) {
                                drsCallListener.makeCallBridgeApiCall(getDrsApiKey, mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getDrs(), Constants.RVP);
                            } else {
                                Toast.makeText(toDoListActivity, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "callconfig null", Toast.LENGTH_SHORT).show();
                        }
                    } else if (getDrsApiKey == null && getDrsPstnKey == null) {
                        //                    Toast.makeText(mBinding.getRoot().getContext(), "RVP3", Toast.LENGTH_SHORT).show();
                        try {
                            callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                            if (callbridgeConfiguration != null) {
                                getCbConfigCallType = callbridgeConfiguration.getCb_calling_type();
                                if (getCbConfigCallType != null)
                                    if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_pstn))) {
                                        Masterpstnformat = drsCallListener.getDefaultPstn();
                                        String callingformat = null;
                                        if (Masterpstnformat.contains(mBinding.getRoot().getContext().getString(R.string.patn_awb))) {
                                            callingformat = Masterpstnformat.replaceAll(mBinding.getRoot().getContext().getString(R.string.patn_awb), mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString());
                                            Constants.call_awb = String.valueOf(mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo());
                                            Constants.shipment_type = Constants.RVP;
                                        } else if (Masterpstnformat.contains(mBinding.getRoot().getContext().getString(R.string.pstn_pin))) {
                                            callingformat = Masterpstnformat.replaceAll(mBinding.getRoot().getContext().getString(R.string.pstn_pin), mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getPin());
                                            Constants.call_pin = String.valueOf(mCommonDRSListItem.getDrsRvpQcMpsResponse().getShipmentDetails().getPin());
                                            Constants.shipment_type = Constants.RVP;
                                        }
                                        if (callingformat != null) {
                                            Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getAlternate_mobile();
                                            rvp_call_count = rvp_call_count + 1;
                                            toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo() + "RVPCall", false);
                                            startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getDrsForwardTypeResponse().getFlags().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getConsigneeDetails().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getDrs());
                                        } else {
                                            Toast.makeText(mBinding.getRoot().getContext(), "Please switch Number", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    /*Check 2.2 (Check for API)*/
                                    else if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_api))) {
                                        //call api
                                        String getApiType = callbridgeConfiguration.getCb_calling_api();
                                        if (getApiType != null) {
                                            drsCallListener.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getAwbNo().toString(), mCommonDRSListItem.getDrsRvpQcMpsResponse().getDrs(), Constants.RVP);
                                        } else {
                                            Toast.makeText(toDoListActivity, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            }
                        } catch (Exception e) {
                            Toast.makeText(toDoListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(toDoListActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Logger.e(TAG, String.valueOf(e));
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP Call Click():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onIndicatorClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsRvpQcMpsResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.fullview.getVisibility() == View.VISIBLE) {
                    mRVPMPSItemViewModel.setImage(true);
                    mBinding.fullview.setVisibility(View.GONE);
                    mBinding.moreorless.setText(R.string.more);
                } else {
                    mBinding.moreorless.setText(R.string.less);
                    mBinding.fullview.setVisibility(View.VISIBLE);
                    mRVPMPSItemViewModel.setImage(false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "RVP onIndicatorClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onRemarksAdded(CommonDRSListItem mCommonDRSListItem) {
        }

        @Override
        public void onTrayClick(CommonDRSListItem mCommonDRSListItem) {
            showIconDetails(mBinding.getRoot().getContext());
        }

        private void showIconDetails(Context context) {
            // custom dialog
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }


    public class EDSViewHolder extends BaseViewHolder implements DRSItemViewModelListener {
        private final ItemEdsListViewBinding mBinding;
        EDSItemViewModel mEDSItemViewModel;

        public EDSViewHolder(ItemEdsListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public ItemEdsListViewBinding getmBinding() {
            return this.mBinding;
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        @Override
        public void onBind(int position) {
            try {
                slide_down = AnimationUtils.loadAnimation(mBinding.getRoot().getContext(), R.anim.bounce_in_down);
                final CommonDRSListItem commonDRSListItem = filterShipments.get(position);
                mEDSItemViewModel = new EDSItemViewModel(commonDRSListItem, this);
                mBinding.setViewModel(mEDSItemViewModel);
                mBinding.imageViewSyncStatus.setVisibility(mEDSItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                        && commonDRSListItem.getEdsResponse().getCallbridge_details() != null) {
                    mBinding.pin.setVisibility(View.VISIBLE);
                    mBinding.viewDivider.setVisibility(View.VISIBLE);

                } else {
                    mBinding.pin.setVisibility(View.INVISIBLE);
                    mBinding.viewDivider.setVisibility(View.INVISIBLE);
                }
                IsCallAttempted(commonDRSListItem);
                IsProfileFound(commonDRSListItem);
                IsRescheduleFlagFound(commonDRSListItem);
                mBinding.secureDelLock.setTag(position);
                mBinding.secureDelOpen.setTag(position);
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.checkboxCkb.setChecked(commonDRSListItem.isSmsCheckFlag());
                if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed()) {
                    mBinding.call.setImageResource(R.drawable.ic_action_callbridge);
                } else {
                    mBinding.call.setImageResource(R.drawable.ic_phone_call_disabled);
                }
                if (commonDRSListItem.getEdsResponse().isIs_cash_collection()) {
                    mBinding.imgEdsRs.setVisibility(View.GONE);
                } else {
                    mBinding.imgEdsRs.setVisibility(View.VISIBLE);
                }
                Remark remark = commonDRSListItem.getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonDRSListItem.getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                toDoListViewModel.getRemarkEDS(commonDRSListItem.getEdsResponse().getAwbNo(), mBinding);
                if (commonDRSListItem.getEdsResponse().getShipmentStatus() == 0) {
                    if (isCheckboxVisible) {
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.checkboxCkb.setGravity(Gravity.CENTER);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    } else {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                    }
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                }
                mBinding.checkboxCkb.setOnClickListener(view -> {
                    commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                    checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getEdsResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                    drsCallListener.getCheckedShipmentlist(checkbox_filter);
                    notifyDataSetChanged();
                });
                mBinding.awb.setText(commonDRSListItem.getEdsResponse().getAwbNo().toString());
                if (mEDSItemViewModel.getStatus().equalsIgnoreCase(GlobalConstant.ShipmentStatusConstants.UNDELIVERED)) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    try {
                        if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.moreorless.setText(Constants.SHIPMENT_UNDELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));
                } else if (mEDSItemViewModel.getStatus().equalsIgnoreCase(GlobalConstant.ShipmentStatusConstants.ASSIGNED)) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    try {
                        if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.VISIBLE);
                                    mBinding.secureDelOpen.setVisibility(View.GONE);
                                }
                            }
                        }
                        for (int j = 0; j < edsRescheduleData.size(); j++) {
                            if (mCommonDRSListItems.get(position).getType().equalsIgnoreCase("EDS")) {
                                if (mCommonDRSListItems.get(position).getEdsResponse().awbNo == Long.parseLong(edsRescheduleData.get(j).getAwb_number())) {
                                    if (edsRescheduleData.get(j).getReschedule_status()) {
                                        mBinding.rescheduleFlagIcon.setVisibility(View.GONE);
                                    } else {
                                        mBinding.rescheduleFlagIcon.setVisibility(View.VISIBLE);
                                        break;
                                    }
                                } else {
                                    mBinding.rescheduleFlagIcon.setVisibility(View.GONE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.edslight));
                } else if (mEDSItemViewModel.getStatus().equalsIgnoreCase(GlobalConstant.ShipmentStatusConstants.SYNC)) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_DELIVERED);
                    try {
                        if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else {
                                if ((Integer) mBinding.secureDelOpen.getTag() == position) {
                                    mBinding.secureDelLock.setVisibility(View.GONE);
                                    mBinding.secureDelOpen.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
                if (commonDRSListItem.isNewDRSAfterSync()) {
                    mBinding.newFlag.setVisibility(View.VISIBLE);
                } else {
                    mBinding.newFlag.setVisibility(View.GONE);
                }
                mBinding.swipeLayout.setSwipeEnabled(filterShipments.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS);
                mBinding.swipeLayout.setOffset(itemsOffset[position]);
                mBinding.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                    @Override
                    public void onBeginSwipe(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onSwipeClampReached(SwipeLayout swipeLayout, boolean moveToRight) {
                        resetAllOtherItemSwipe(position);
                    }

                    @Override
                    public void onLeftStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }

                    @Override
                    public void onRightStickyEdge(SwipeLayout swipeLayout, boolean moveToRight) {

                    }
                });
                mBinding.remarksCard.setOnClickListener(v -> {
                    mBinding.swipeLayout.animateReset();
                    clickSwipeListener.onRemarksClicked(position);
                });
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "EDS onBind():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void changeCheckboxState(CommonDRSListItem commonDRSListItem) {
            try {
                commonDRSListItem.setSmsCheckFlag(!commonDRSListItem.isSmsCheckFlag());
                checkbox_filter.put(String.valueOf(filterShipments.get(getAdapterPosition()).getEdsResponse().getAwbNo()), commonDRSListItem.isSmsCheckFlag());
                drsCallListener.getCheckedShipmentlist(checkbox_filter);
                notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "EDS changeCheckboxState():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                Constants.ContactNO = "0";
                Constants.Water_Mark_Awb = String.valueOf(commonDRSListItem.getEdsResponse().getAwbNo());
                Constants.Wrong_Mobile_no = false;
                Constants.ConsigneeDirectMobileNo = commonDRSListItem.getEdsResponse().getConsigneeDetail().getMobile();
                Constants.ContactNO = commonDRSListItem.getEdsResponse().getConsigneeDetail().getContactNo();
                Constants.ConsigneeDirectAlternateMobileNo = commonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile();
                in.ecomexpress.geolocations.Constants.latitude = toDoListViewModel.getDataManager().getCurrentLatitude();
                in.ecomexpress.geolocations.Constants.longitude = toDoListViewModel.getDataManager().getCurrentLongitude();
                Constants.RCHDEnable = commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getRCHD_enabled();
                Constants.CancellationEnable = commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getCancellation_enabled();
                if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed()) {
                    Constants.Wrong_Mobile_no = true;
                }
                eds_is_item_clicked = false;
                toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getEdsResponse().getAssignDate());
                toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getEdsResponse().getShipmentDetail().getShipper_id());
                Constants.CASH_RECEIPT = "false";
                Constants.IS_ICICI_FINKARE = 0;
                toDoListViewModel.getEdsListTask(commonDRSListItem.getEdsResponse().getCompositeKey());
                Constants.IS_CASH_COLLECTION_ENABLE = false;
                if (mBinding.tick.getVisibility() == View.VISIBLE) {
                    Constants.CONSIGNEE_PROFILE = true;
                    if (commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0) {
                        CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location Coordinates Not Found", toDoListActivity);
                        EdsClick(commonDRSListItem, mBinding);
                    } else {
                        double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                        if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                            toDoListActivity.runOnUiThread(() -> getRestrictionEds(commonDRSListItem, mBinding, distance));
                        } else {
                            EdsClick(commonDRSListItem, mBinding);
                        }
                    }
                } else {
                    Constants.CONSIGNEE_PROFILE = false;
                    EdsClick(commonDRSListItem, mBinding);
                }
            } catch (Exception e) {
                eds_is_item_clicked = true;
                Toast.makeText(toDoListActivity, "EDS onItemClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void getRestrictionEds(CommonDRSListItem commonDRSListItem, ItemEdsListViewBinding mBinding, double distance) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.proceed_now);

                String consigneeProfileValue = toDoListViewModel.getDataManager().getConsigneeProfileValue();
                boolean isRestriction = false;

                if (consigneeProfileValue.equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.proceed_now);
                } else if (consigneeProfileValue.equalsIgnoreCase("R")) {
                    isRestriction = true;
                    dialog_message = "You are not allowed to commit this shipment as you are not at the consignee's location.\n\n"
                            + "📍 Distance from consignee: " + distance + " meters away\n\n"
                            + "🔹 Your Coordinates: " + toDoListViewModel.getDataManager().getCurrentLatitude()
                            + ", " + toDoListViewModel.getDataManager().getCurrentLongitude() + "\n"
                            + "🔹 Consignee Coordinates: " + commonDRSListItem.getProfileFound().getDelivery_latitude()
                            + ", " + commonDRSListItem.getProfileFound().getDelivery_longitude();
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ctx, R.style.Theme_MaterialComponents_Light_Dialog_Alert);
                builder.setIcon(isRestriction ? R.drawable.restricted_sign : R.drawable.warning_sign);

                // Format title as bold and black
                SpannableString title = new SpannableString(isRestriction ? "Restricted!" : "Warning!");
                title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, android.R.color.black)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(title);

                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (consigneeProfileValue.equalsIgnoreCase("R")) {
                        eds_is_item_clicked = true;
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                        EdsClick(commonDRSListItem, mBinding);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void EdsClick(CommonDRSListItem commonDRSListItem, ItemEdsListViewBinding mBinding) {
            Constants.SHIPMENT_TYPE = Constants.EDS;
            commonDRSListItemEdsClick = commonDRSListItem;
            Constants.TEMP_DRSID = commonDRSListItem.getEdsResponse().getDrsNo();
            if (commonDRSListItem.getEdsResponse().getShipmentStatus() > 0) {
                long awb = commonDRSListItem.getEdsResponse().getAwbNo();
                try {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    compositeDisposable.add(toDoListViewModel.getDataManager().insetedOrNotinTable(awb).subscribeOn(toDoListViewModel.getSchedulerProvider().io()).observeOn(toDoListViewModel.getSchedulerProvider().io()).subscribe(pushApis -> {}));
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            } else if (toDoListActivity.activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                changeCheckboxState(commonDRSListItem);
                return;
            }
            try {
                getDrsPin = String.valueOf(commonDRSListItem.getEdsResponse().getShipmentDetail().getPin());
                getDrsApiKey = commonDRSListItem.getEdsResponse().getCallbridge_Api();
                getDrsPstnKey = commonDRSListItem.getEdsResponse().getCallbridgePstn();
                toDoListViewModel.getDataManager().setAadharFrontImage("");
                toDoListViewModel.getDataManager().setAadharRearImage("");
                toDoListViewModel.getDataManager().setAadharStatus(false);
                toDoListViewModel.getDataManager().setAadharStatusCode(-1);
                toDoListViewModel.getDataManager().setEdsActivityCodes(null);
            } catch (NullPointerException e) {
                Logger.e(TAG, String.valueOf(e));
            }
            if (commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP() && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
                    Intent intent = EdsTaskListActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getEdsResponse().getDrsNo()));
                    intent.putExtra(Constants.IS_KYC_ACTIVE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isIs_kyc_active());
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isReschedule_enable());
                    intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getEdsResponse().getCompositeKey());
                    intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getEdsResponse().getAwbNo());
                    intent.putExtra(Constants.DRS_ID, commonDRSListItem.getEdsResponse().getDrsNo());
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile());
                    intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getEdsResponse().getShipmentDetail().getOfd_otp());
                    intent.putExtra("call_allowed", commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed());
                    intent.putExtra(Constants.ORDER_ID, commonDRSListItem.getEdsResponse().getShipmentDetail().getOrderNo());
                    mBinding.getRoot().getContext().startActivity(intent);
                } else {
                    Intent intent;
                    intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery());
                    intent.putExtra(Constants.SHIPMENT_TYPE, Constants.EDS);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.IS_KYC_ACTIVE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isIs_kyc_active());
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isReschedule_enable());
                    intent.putExtra(Constants.IS_CASH_COLLECTION, commonDRSListItem.getEdsResponse().isIs_cash_collection());
                    intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getEdsResponse().getAmazonEncryptedOtp());
                    intent.putExtra(Constants.AMAZON, commonDRSListItem.getEdsResponse().getAmazon());
                    intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getEdsResponse().getCompositeKey());
                    intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getEdsResponse().getAwbNo());
                    intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile());
                    intent.putExtra(Constants.DRS_ID, commonDRSListItem.getEdsResponse().getDrsNo());
                    intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getEdsResponse().getDrsNo()));
                    intent.putExtra(Constants.ORDER_ID, commonDRSListItem.getEdsResponse().getShipmentDetail().getOrderNo());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, "");
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, "");
                    intent.putExtra(Constants.ISDELIGHTSHIPMENT, false);
                    intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getEdsResponse().getShipmentDetail().getOfd_otp());
                    intent.putExtra(Constants.CONSIGNEE_MOBILE, commonDRSListItem.getEdsResponse().getConsigneeDetail().getContactNo());
                    intent.putExtra("call_allowed", commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed());
                    intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getResend_otp_enable());
                    mBinding.getRoot().getContext().startActivity(intent);
                }
            } else {
                Intent intent = EdsTaskListActivity.getStartIntent(mBinding.getRoot().getContext());
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.ORDER_ID, commonDRSListItem.getEdsResponse().getShipmentDetail().getOrderNo());
                intent.putExtra(Constants.IS_KYC_ACTIVE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isIs_kyc_active());
                intent.putExtra(Constants.RESCHEDULE_ENABLE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isReschedule_enable());
                intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getEdsResponse().getCompositeKey());
                intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getEdsResponse().getAwbNo());
                intent.putExtra(Constants.DRS_ID, commonDRSListItem.getEdsResponse().getDrsNo());
                intent.putExtra(Constants.CONSIGNEE_ALTERNATE_MOBILE, commonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile());
                intent.putExtra(Constants.DRS_ID_NUM, String.valueOf(commonDRSListItem.getEdsResponse().getDrsNo()));
                intent.putExtra(Constants.OFD_OTP, commonDRSListItem.getEdsResponse().getShipmentDetail().getOfd_otp());
                intent.putExtra("call_allowed", commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed());
                mBinding.getRoot().getContext().startActivity(intent);
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void IsProfileFound(CommonDRSListItem commonDRSListItem) {
            try {
                ProfileFound profileFound = commonDRSListItem.getProfileFound();
                if (profileFound != null) {
                    mBinding.tick.setVisibility(View.VISIBLE);
                    mBinding.map.setVisibility(View.GONE);
                    mBinding.navView.setBackground(ctx.getResources().getDrawable(R.drawable.ic_action_trip));
                    if (profileFound.isRed_alert()) {
                        mBinding.imageRedAlert.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.imageRedAlert.setVisibility(View.GONE);
                    }
                } else {
                    mBinding.tick.setVisibility(View.GONE);
                    mBinding.map.setVisibility(View.VISIBLE);
                    mBinding.navView.setBackgroundResource(R.drawable.eds);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void IsRescheduleFlagFound(CommonDRSListItem commonDRSListItem) {
            try {
                edsRescheduleData = commonDRSListItem.getRescheduleFlagFound();
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onMapClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getEdsResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US).equals(Constants.ZEBRA)) {
                    try {
                        Uri uri;
                        String getAddress = CommonUtils.nullToEmpty(CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine1()) + " " + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine2()) + " " + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine3()) + " " + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine4()) + " " + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getCity()) + ", " + commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getPincode());
                        String removeBetweenSpaces = getAddress.replaceAll("\\s+", " ").trim();
                        String finalAddress = removeBetweenSpaces.replaceAll(",", "").trim();
                        double lat = 0.0, lng = 0.0;
                        try {
                            lat = commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLocation().getLat();
                            lng = commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLocation().getLng();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }
                        if (lng != 0.0 && lat != 0.0) {
                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                            uri = Uri.parse("http://maps.google.com/maps?saddr=" + toDoListViewModel.getlat() + "," + toDoListViewModel.getlng() + "&daddr=" + lat + "," + lng + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE());
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=" + toDoListViewModel.getDataManager().getMAP_DRIVING_MODE() + "&avoid=tf");
                        }
                        eds_map_count = eds_map_count + 1;
                        toDoListViewModel.getDataManager().setEDSMapCount(commonDRSListItem.getEdsResponse().getAwbNo(), eds_map_count);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (CommonUtils.isAppInstalled("com.google.android.apps.maps", mBinding.getRoot().getContext())) {
                            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mBinding.getRoot().getContext().startActivity(intent);
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Google Maps not Supported", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "EDS onMapClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getEdsResponse().getIsCallattempted() != 0)
                    if (commonDRSListItem.getEdsResponse().getIsCallattempted() == Constants.callAttempted) {
                        mBinding.icCallattempted.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.icCallattempted.setVisibility(View.GONE);
                    }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "EDS IsCallAttempted():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            if (!mCommonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isCall_allowed()) {
                Toast.makeText(toDoListActivity, toDoListActivity.getResources().getString(R.string.call_not_allowed), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mCommonDRSListItem.getEdsResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            }
            if (mCommonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled().equalsIgnoreCase("true")
                    && mCommonDRSListItem.getEdsResponse().getCallbridge_details() != null) {
                try {
                    toDoListViewModel.showAlertOfedsAssignment(toDoListActivity, mCommonDRSListItem);
                    Dialog dialog = toDoListViewModel.getEdsAssignmentDialog();
                    Button bt_eds_call = dialog.findViewById(R.id.bt_eds_call);
                    bt_eds_call.setOnClickListener(v -> {
                        try {
                            String callingformat;
                            callingformat = mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getCallbridge_number() + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(0, 4) + "," + mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin().substring(4) + "#";
                            Constants.call_pin = String.valueOf(mCommonDRSListItem.getEdsResponse().getCallbridge_details().get(0).getPin());
                            Constants.calling_format = callingformat;
                            Constants.shipment_type = Constants.EDS;
                            if (callingformat != null) {
                                Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile();
                                eds_call_count = eds_call_count + 1;
                                toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getEdsResponse().getAwbNo() + "EDSCall", false);
                                startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getMobile(), callingformat, mBinding.getRoot().getContext(), mCommonDRSListItem.getEdsResponse().awbNo, mCommonDRSListItem.getEdsResponse().getDrsNo());
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), "Please switch Number", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Logger.e(TAG, String.valueOf(e));
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(toDoListActivity, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                eds_call_count = eds_call_count + 1;
                toDoListViewModel.getDataManager().setCallClicked(mCommonDRSListItem.getEdsResponse().getAwbNo() + "EDSCall", false);
                Constants.ConsigneeDirectAlternateMobileNo = mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getAlternate_mobile();
                startCallIntent(mCommonDRSListItem, mCommonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getFlagMap().getIs_callbridge_enabled(), mCommonDRSListItem.getEdsResponse().getConsigneeDetail().getMobile(), getDrsPstnKey, mBinding.getRoot().getContext(), mCommonDRSListItem.getEdsResponse().getAwbNo(), mCommonDRSListItem.getEdsResponse().getDrsNo());
            }
        }

        @Override
        public void onIndicatorClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (mCommonDRSListItem.getEdsResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                if (mBinding.fullview.getVisibility() == View.VISIBLE) {
                    mEDSItemViewModel.setImage(true);
                    mBinding.fullview.setVisibility(View.GONE);
                    mBinding.moreorless.setText(R.string.more);
                } else {
                    mBinding.moreorless.setText(R.string.less);
                    mBinding.fullview.setVisibility(View.VISIBLE);
                    mEDSItemViewModel.setImage(false);
                }
            } catch (Exception e) {
                Toast.makeText(toDoListActivity, "EDS indicator():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRemarksAdded(CommonDRSListItem mCommonDRSListItem) {
        }

        @Override
        public void onTrayClick(CommonDRSListItem mCommonDRSListItem) {
            showIconDetails(mBinding.getRoot().getContext());
        }

        private void showIconDetails(Context context) {
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
            Objects.requireNonNull(dialogButton).setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }
}