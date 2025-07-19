package in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import java.util.List;
import java.util.Locale;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ItemEdsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemForwardListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpListViewBinding;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.consignee_profile.ProfileFound;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.SecureDelivery;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.ui.base.BaseViewHolder;
import in.ecomexpress.sathi.ui.drs.todolist.DRSCallListener;
import in.ecomexpress.sathi.ui.drs.todolist.DRSItemViewModelListener;
import in.ecomexpress.sathi.ui.drs.todolist.EDSItemViewModel;
import in.ecomexpress.sathi.ui.drs.todolist.ForwardItemViewModel;
import in.ecomexpress.sathi.ui.drs.todolist.RVPItemViewModel;
import in.ecomexpress.sathi.ui.drs.todolist.ToDoListActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.EdsTaskListActivity;
import in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailActivity;
import in.ecomexpress.sathi.ui.drs.forward.mps.MPSScanActivity;
import in.ecomexpress.sathi.ui.drs.secure_delivery.SecureDeliveryActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.LocationHelper;
import in.ecomexpress.sathi.utils.Logger;

import static android.content.Context.TELEPHONY_SERVICE;
import static in.ecomexpress.sathi.utils.Constants.DISTANCE_API_KEY;

public class CustomAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    SecureDelivery isSecureDelivery;
    boolean isDigital = false;
    ToDoListActivity activity;
    List<CommonDRSListItem> commonMapDrsListItem_set;
    CallbridgeConfiguration callbridgeConfiguration = null;
    private static final String TAG = CustomAdapter.class.getSimpleName();
    Context ctx;
    Animation slide_down;
    public static final int VIEW_TYPE_FORWARD = 1;
    public static final int VIEW_TYPE_RTS = 2;
    public static final int VIEW_TYPE_RVP = 3;
    public static final int VIEW_TYPE_EDS = 4;
    public static final int VIEW_TYPE_EMPTY = 0;
    String getDrsApiKey = null;
    String getDrsPstnKey = null;
    String getCbConfigCallType = null;
    String MasterPSTNFormat = null;
    String getDrsPin = null;
    String getOrderId = "";
    String getDrsId = "";
    boolean forward_is_item_clicked = true;
    boolean rvp_is_item_clicked = true;
    boolean eds_is_item_clicked = true;
    GViewModel toDoListViewModel;
    boolean isCheckboxVisible = false;

    public CustomAdapter() {

    }

    public void setData(Context context, List<CommonDRSListItem> commonMapDrsListItem_set, ToDoListActivity activity, GViewModel toDoListViewModel) {
        this.ctx = context;
        this.commonMapDrsListItem_set = commonMapDrsListItem_set;
        this.activity = activity;
        this.toDoListViewModel = toDoListViewModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FORWARD:
                ItemForwardListViewBinding itemForwardListBinding = ItemForwardListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ForwardViewHolder(itemForwardListBinding);
            case VIEW_TYPE_RVP:
                ItemRvpListViewBinding itemRvpListBinding = ItemRvpListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new RVPViewHolder(itemRvpListBinding);
            case VIEW_TYPE_EDS:
                ItemEdsListViewBinding itemEdsListViewBinding = ItemEdsListViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new EDSViewHolder(itemEdsListViewBinding);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (commonMapDrsListItem_set != null && !commonMapDrsListItem_set.isEmpty()) {
                switch (commonMapDrsListItem_set.get(position).getType()) {
                    case GlobalConstant.ShipmentTypeConstants.FWD:
                        return VIEW_TYPE_FORWARD;
                    case GlobalConstant.ShipmentTypeConstants.RTS:
                        return VIEW_TYPE_RTS;
                    case GlobalConstant.ShipmentTypeConstants.RVP:
                        return VIEW_TYPE_RVP;
                    case GlobalConstant.ShipmentTypeConstants.EDS:
                        return VIEW_TYPE_EDS;
                    default:
                        return VIEW_TYPE_EMPTY;
                }
            } else {
                return VIEW_TYPE_EMPTY;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return VIEW_TYPE_EMPTY;
    }

    private DRSCallListener drsCallListener;

    public class ForwardViewHolder extends BaseViewHolder implements DRSItemViewModelListener {

        private final ItemForwardListViewBinding mBinding;
        ForwardItemViewModel mForwardItemViewmodel;

        public ForwardViewHolder(ItemForwardListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBind(int position) {
            try {
                mForwardItemViewmodel = new ForwardItemViewModel(commonMapDrsListItem_set.get(position), this);
                mBinding.setViewModel(mForwardItemViewmodel);
                mBinding.executePendingBindings();
                mBinding.secureDelOpen.setTag(position);
                mBinding.secureDelLock.setTag(position);
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.viewDivider.setVisibility(!mForwardItemViewmodel.pin().isEmpty() ? View.VISIBLE : View.GONE);
                if (commonMapDrsListItem_set.get(position).getDrsForwardTypeResponse().getShipmentStatus() == 0) {
                    if (isCheckboxVisible) {
                        mBinding.checkboxCkb.setVisibility(View.VISIBLE);
                        mBinding.checkboxCkb.setChecked(true);
                        mBinding.checkboxCkb.setGravity(Gravity.CENTER);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.1f));
                    } else {
                        mBinding.checkboxCkb.setVisibility(View.GONE);
                        mBinding.checkboxCkb.setChecked(false);
                        mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                        mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                    }
                } else {
                    mBinding.checkboxCkb.setVisibility(View.GONE);
                    mBinding.color.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f));
                    mBinding.checkboxCkb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                }
                checkShipmentStatus(commonMapDrsListItem_set.get(position), position);
                IsCallAttempted(commonMapDrsListItem_set.get(position));
                mBinding.imageViewSynced.setVisibility(mForwardItemViewmodel.isItemSynced() ? View.VISIBLE : View.GONE);
                int counter = mForwardItemViewmodel.getMissedCallCounter();
                if (counter > 0) {
                    mBinding.missedcall.setText(String.valueOf(counter));
                    mBinding.missedcall.setVisibility(View.VISIBLE);
                } else {
                    mBinding.missedcall.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
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
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin()
                                    && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
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
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin()
                                    && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
                                mBinding.secureDelLock.setVisibility(View.GONE);
                                mBinding.secureDelOpen.setVisibility(View.GONE);
                            } else { // forward
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
                            if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin()
                                    && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
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
                Logger.e(TAG, String.valueOf(e));
            }
        }

        // forward
        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "Forward onItemClicked():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            onForwardItemClick(commonDRSListItem);
        }

        private void onForwardItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (forward_is_item_clicked) {
                    forward_is_item_clicked = false;
                    toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getDrsForwardTypeResponse().getAssignedDate());
                    toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id());
                    if (mBinding.tick.getVisibility() == View.VISIBLE) {
                        Constants.CONSIGNEE_PROFILE = true;
                        forwardClick(commonDRSListItem, mBinding);
                    } else {
                        Constants.CONSIGNEE_PROFILE = false;
                        forwardClick(commonDRSListItem, mBinding);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "Forward forwardItemClick():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
            try {
                isDigital = commonDRSListItem.getDrsForwardTypeResponse().getFlags().getIsDigitalPaymentAllowed();
            } catch (Exception e) {
                isDigital = false;
                Logger.e(TAG, String.valueOf(e));
            }
            Intent intent = null;

            // Thumb rule if mps and secure delivery both are enabled then first show mps and then secure delivery.
            if (commonDRSListItem.getDrsForwardTypeResponse().mpsShipment != null) {
                if (commonDRSListItem.getDrsForwardTypeResponse().mpsShipment.equalsIgnoreCase("P")) {
                    intent = MPSScanActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.MPS, commonDRSListItem.getDrsForwardTypeResponse().mpsShipment);
                    intent.putExtra(Constants.MPS_AWB_NOS, commonDRSListItem.getDrsForwardTypeResponse().mpsAWBs);
                    intent.putExtra(Constants.ORDER_ID, getOrderId);
                    intent.putExtra(Constants.DRS_ID_NUM, getDrsId);
                }
            } else if (commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery() != null) {
                if (!commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getPinb()
                        && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getOTP()
                        && !commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery().getSecure_pin()
                        && commonDRSListItem.getDrsForwardTypeResponse().getAmazon().equalsIgnoreCase("false")) {
                    intent = ForwardDetailActivity.getStartIntent(mBinding.getRoot().getContext());
                } else {
                    intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsForwardTypeResponse().getAmazonEncryptedOtp());
                    intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsForwardTypeResponse().getAmazon());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp1());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, commonDRSListItem.getDrsForwardTypeResponse().getDlight_encrypted_otp2());
                    intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsForwardTypeResponse().getFlags().secure_delivery.getResend_otp_enable());
                    intent.putExtra(Constants.ISDELIGHTSHIPMENT, commonDRSListItem.getDrsForwardTypeResponse().isDelightShipment());
                    intent.putExtra(Constants.ORDER_ID, getOrderId);
                    intent.putExtra(Constants.DRS_ID_NUM, getDrsId);
                }
            } else {
                intent = ForwardDetailActivity.getStartIntent(mBinding.getRoot().getContext());
                intent.putExtra(Constants.ORDER_ID, getOrderId);
                intent.putExtra(Constants.DRS_ID_NUM, getDrsId);
            }
            assert intent != null;
            intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getSecure_delivery());
            intent.putExtra(Constants.SHIPMENT_TYPE, Constants.FWD);
            intent.putExtra(Constants.SHIPMENT_TYPE, Constants.FWD);
            intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
            intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
            intent.putExtra(Constants.DRS_PIN, getDrsPin);
            intent.putExtra(Constants.DRS_ID_NUM, getDrsId);
            intent.putExtra(Constants.ORDER_ID, getOrderId);
            intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsForwardTypeResponse().compositeKey);
            intent.putExtra(Constants.IS_AMAZON_RESHEDUCLE_ENABLE, commonDRSListItem.getDrsForwardTypeResponse().getFlags().getIs_amazon_reschedule_enabled());
            intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsForwardTypeResponse().getCompositeKey());
            intent.putExtra(Constants.IS_CARD, isDigital);
            intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
            mBinding.getRoot().getContext().startActivity(intent);
        }

        private void IsCallAttempted(CommonDRSListItem commonDRSListItem) {
            try {
                if (commonDRSListItem.getDrsForwardTypeResponse().getIsCallattempted() == Constants.callAttempted) {
                    mBinding.icCallattempted.setVisibility(View.VISIBLE);
                } else {
                    mBinding.icCallattempted.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "Forward IsCallAttempted():-" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                        String getAddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine1()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getCity()) + ",\n"
                                + commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getPincode();
                        String finalAddress = getAddress.replaceAll(",,", ",");
                        double lat = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();
                        double lng = commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLocation().getLng();

                        if (String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("68695")
                                || String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("28446")
                                || String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper_id()).equalsIgnoreCase("222980")) {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=d&avoid=tf");
                        } else if (lng != 0.0 && lat != 0.0) {
                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                            uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", toDoListViewModel.getlat(), toDoListViewModel.getlng(), "MyLocation", lat, lng, "Destination"));

                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=d&avoid=tf");
                        }

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
                Logger.e(TAG, String.valueOf(e));
            }
        }


        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                DRSCallListener drsCallListener = activity.getDRSCallListener();
                getDrsApiKey = mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCallbridgeApi();
                getDrsPstnKey = mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getCallbridgePstn();

                if (getDrsPstnKey != null) {
                    Constants.call_awb = String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
                    Constants.shipment_type = Constants.FWD;
                    startCallIntent(getDrsPstnKey, mBinding.getRoot().getContext());
                }

                else if (getDrsApiKey != null) {
                    CallbridgeConfiguration callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                    if (callbridgeConfiguration != null) {
                        String getApiType = callbridgeConfiguration.getCb_calling_api();
                        if (getApiType != null) {
                            drsCallListener.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo()), mCommonDRSListItem.getDrsForwardTypeResponse().getDrsId(), Constants.FWD);
                        }
                    } else {
                        Toast.makeText(mBinding.getRoot().getContext(), "Call Config null", Toast.LENGTH_SHORT).show();
                    }
                }

                else if (getDrsApiKey == null && getDrsPstnKey == null) {
                    try {
                        callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                        if (callbridgeConfiguration != null){
                            getCbConfigCallType = callbridgeConfiguration.getCb_calling_type();
                        }

                        if (getCbConfigCallType != null)
                            if (getCbConfigCallType.equalsIgnoreCase("PSTN")) {
                                MasterPSTNFormat = drsCallListener.getDefaultPstn();
                                String callingformat = null;
                                if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.patn_awb))) {
                                    callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.patn_awb), mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo().toString());
                                    Constants.call_awb = String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
                                    Constants.shipment_type = Constants.FWD;
                                } else if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.pstn_pin))) {
                                    callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.pstn_pin), mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getPin());
                                    Constants.call_pin = String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getPin());
                                    Constants.shipment_type = Constants.FWD;
                                }
                                if (callingformat != null) {
                                    startCallIntent(callingformat, mBinding.getRoot().getContext());
                                }
                            }

                        if(getCbConfigCallType == null){
                            getCbConfigCallType = "";
                        }

                        if (getCbConfigCallType.equalsIgnoreCase("API")) {
                            callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                            if (callbridgeConfiguration != null) {
                                String getApiType = callbridgeConfiguration.getCb_calling_api();
                                if (getApiType != null) {
                                    drsCallListener.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), String.valueOf(mCommonDRSListItem.getDrsForwardTypeResponse().getAwbNo()), mCommonDRSListItem.getDrsForwardTypeResponse().getDrsId(), Constants.FWD);
                                }
                            } else {
                                Toast.makeText(mBinding.getRoot().getContext(), "callconfig null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, String.valueOf(e));
                    }

                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
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
            try {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_drs_icons_dialog);
                ImageView dialogButton = dialog.findViewById(R.id.cross);
                dialogButton.setOnClickListener(v -> dialog.dismiss());
                dialog.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    // RVP
    public class RVPViewHolder extends BaseViewHolder implements DRSItemViewModelListener {

        private final ItemRvpListViewBinding mBinding;
        RVPItemViewModel mRVPItemViewModel;

        public RVPViewHolder(ItemRvpListViewBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBind(int position) {
            try {
                slide_down = AnimationUtils.loadAnimation(mBinding.getRoot().getContext(), R.anim.bounce_in_down);
                mRVPItemViewModel = new RVPItemViewModel(commonMapDrsListItem_set.get(position), this);
                mBinding.setViewModel(mRVPItemViewModel);
                mBinding.viewDivider.setVisibility(!mRVPItemViewModel.getPin().isEmpty() ? View.VISIBLE : View.GONE);
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.secureDelLock.setTag(position);
                mBinding.secureDelOpen.setTag(position);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.checkboxCkb.setChecked(commonMapDrsListItem_set.get(position).isSmsCheckFlag());
                IsProfileFound(commonMapDrsListItem_set.get(position));
                if (commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getShipmentStatus() == 0) {
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
                if (!commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getShipmentDetails().getQualityChecks().isEmpty()) {
                    mBinding.qc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.qc.setVisibility(View.GONE);
                }
                mBinding.imageViewSyncStatus.setVisibility(mRVPItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                mBinding.awb.setText(commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getAwbNo().toString())/* + ": D: " + commonDRSListItem.getDistance())*/;
                IsCallAttempted(commonMapDrsListItem_set.get(position));
                Remark remark = commonMapDrsListItem_set.get(position).getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonMapDrsListItem_set.get(position).getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                if (commonMapDrsListItem_set.get(position).getCommonDrsStatus() == Constants.SHIPMENT_UNDELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_UNDELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.undel4));

                    try {
                        if (commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
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
                } else if (commonMapDrsListItem_set.get(position).getCommonDrsStatus() == Constants.SHIPMENT_ASSIGNED_STATUS) {
                    mBinding.color.setBackgroundResource(R.color.white);
                    mBinding.moreorless.setText(Constants.MORE);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.rvplight));
                    try {
                        if (commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
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

                } else if (commonMapDrsListItem_set.get(position).getCommonDrsStatus() == Constants.SHIPMENT_DELIVERED_STATUS) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);

                    try {
                        if (commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getSecure_pin()) {
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
                    mBinding.moreorless.setText(Constants.SHIPMENT_DELIVERED);
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.del4));
                }
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(ctx, "RVP onBind():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // RVP Item
        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (rvp_is_item_clicked) {
                    rvp_is_item_clicked = false;
                    toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getDrsReverseQCTypeResponse().getAssignedDate());
                    toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getShipper_id());
                    if (mBinding.tick.getVisibility() == View.VISIBLE) {
                        Constants.CONSIGNEE_PROFILE = true;
                        if(commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0){
                            CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location Coordinates Not Found", activity);
                            RVPClick(commonDRSListItem, mBinding);
                        } else {
                            // double distance = getDistanceBetweenLocations(new com.google.maps.model.LatLng(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude()));
                            double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                            if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                                activity.runOnUiThread(() -> getRestrictionRVP(commonDRSListItem, mBinding));
                            } else {
                                RVPClick(commonDRSListItem, mBinding);
                            }
                        }
                    } else {
                        Constants.CONSIGNEE_PROFILE = false;
                        RVPClick(commonDRSListItem, mBinding);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "RVP onItemClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void getRestrictionRVP(CommonDRSListItem commonDRSListItem, ItemRvpListViewBinding mBinding) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.yes);
                if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.yes);
                } else if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away_mandatory);
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx,android.R.style.Theme_Material_Light_Dialog);
                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                        rvp_is_item_clicked = true;
                        dialog.cancel();
                    } else {
                        dialog.cancel();
                        RVPClick(commonDRSListItem, mBinding);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            toDoListViewModel.getDataManager().getConsigneeProfileValue();
        }

        public void RVPClick(CommonDRSListItem commonDRSListItem, ItemRvpListViewBinding mBinding) {
            Constants.TEMP_DRSID = commonDRSListItem.getDrsReverseQCTypeResponse().getDrs();
            if (commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                getDrsPin = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getPin();
                getDrsApiKey = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridge_Api();
                getDrsPstnKey = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridgePstn();
            } catch (Exception e) {
                Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            if (commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery() != null) {
                Intent intent;
                intent = SecureDeliveryActivity.getStartIntent(mBinding.getRoot().getContext());
                intent.putExtra(Constants.SECURE_DELIVERY, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery());
                intent.putExtra(Constants.SHIPMENT_TYPE, Constants.RVP);
                intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                intent.putExtra(Constants.AMAZON_ENCRYPTED_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getAmazonEncryptedOtp());
                intent.putExtra(Constants.AMAZON, commonDRSListItem.getDrsReverseQCTypeResponse().getAmazon());
                intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getCompositeKey());
                intent.putExtra(Constants.DRS_PIN, getDrsPin);
                intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
                intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getDrsReverseQCTypeResponse().getFlags().getSecure_delivery().getResend_otp_enable());
                intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, "");
                intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, "");
                intent.putExtra(Constants.ISDELIGHTSHIPMENT, false);
                mBinding.getRoot().getContext().startActivity(intent);
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
                        String getAddress = CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine1()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine2()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine3()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine4()) + ",\n"
                                + CommonUtils.nullToEmpty(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getCity()) + ",\n"
                                + commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getPincode();
                        String finalAddress = getAddress.replaceAll(",,", ",");
                        double lat = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLocation().getLat();
                        double lng = commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLocation().getLng();
                        if (lng != 0.0 && lat != 0.0) {
                            uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", toDoListViewModel.getlat(), toDoListViewModel.getlng(), "MyLocation", lat, lng, "Destination"));
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=d&avoid=tf");
                        }

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
                Toast.makeText(ctx, "RVP MapClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ctx, "RVP IsCallAttempted():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            try {
                if (mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentStatus() > 0) {
                    Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    getDrsPstnKey = mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridgePstn();
                    getDrsApiKey = mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getCallbridge_Api();
                    if (getDrsPstnKey != null) {
                        Constants.call_awb = mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString();
                        Constants.shipment_type = Constants.RVP;
                        startCallIntent(getDrsPstnKey, mBinding.getRoot().getContext());
                    } else if (getDrsApiKey != null) {
                        callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                        if (callbridgeConfiguration != null) {
                            String getApiType = callbridgeConfiguration.getCb_calling_api();
                            if (getApiType != null) {
                                drsCallListener.makeCallBridgeApiCall(getDrsApiKey, mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getDrs(), Constants.RVP);
                            } else {
                                Toast.makeText(ctx, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "Call Config null", Toast.LENGTH_SHORT).show();

                        }
                    } else if (getDrsApiKey == null && getDrsPstnKey == null) {
                        try {
                            callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                            if (callbridgeConfiguration != null) {
                                getCbConfigCallType = callbridgeConfiguration.getCb_calling_type();
                                if (getCbConfigCallType != null) {
                                    if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_pstn))) {
                                        MasterPSTNFormat = drsCallListener.getDefaultPstn();
                                        String callingformat = null;
                                        if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.patn_awb))) {
                                            callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.patn_awb), mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString());
                                            Constants.call_awb = String.valueOf(mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
                                            Constants.shipment_type = Constants.RVP;

                                        } else if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.pstn_pin))) {
                                            callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.pstn_pin), mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getPin());
                                            Constants.call_pin = String.valueOf(mCommonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getPin());
                                            Constants.shipment_type = Constants.RVP;
                                        }
                                        if (callingformat != null) {
                                            startCallIntent(callingformat, mBinding.getRoot().getContext());
                                        }
                                    } else if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_api))) {
                                        String getApiType = callbridgeConfiguration.getCb_calling_api();
                                        if (getApiType != null) {
                                            drsCallListener.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo().toString(), mCommonDRSListItem.getDrsReverseQCTypeResponse().getDrs(), Constants.RVP);
                                        } else {
                                            Toast.makeText(ctx, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Logger.e(TAG, String.valueOf(e));
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Logger.e(TAG, String.valueOf(e));
                }
            } catch (Exception e) {
                Toast.makeText(ctx, "RVP Call Click():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
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
                Toast.makeText(ctx, "RVP onIndicatorClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onBind(int position) {
            try {
                slide_down = AnimationUtils.loadAnimation(mBinding.getRoot().getContext(), R.anim.bounce_in_down);
                mEDSItemViewModel = new EDSItemViewModel(commonMapDrsListItem_set.get(position), this);
                mBinding.setViewModel(mEDSItemViewModel);
                mBinding.imageViewSyncStatus.setVisibility(mEDSItemViewModel.isItemSynced() ? View.VISIBLE : View.GONE);
                IsCallAttempted(commonMapDrsListItem_set.get(position));
                IsProfileFound(commonMapDrsListItem_set.get(position));
                mBinding.secureDelLock.setTag(position);
                mBinding.secureDelOpen.setTag(position);
                mBinding.checkboxCkb.setOnCheckedChangeListener(null);
                mBinding.checkboxCkb.setFocusable(false);
                mBinding.checkboxCkb.setChecked(commonMapDrsListItem_set.get(position).isSmsCheckFlag());

                Remark remark = commonMapDrsListItem_set.get(position).getRemark();
                if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
                    mBinding.layoutRemarks.setVisibility(View.VISIBLE);
                    mBinding.remarks.setText(commonMapDrsListItem_set.get(position).getRemark().remark);
                } else {
                    mBinding.layoutRemarks.setVisibility(View.GONE);
                }
                if (commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentStatus() == 0) {
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
                    commonMapDrsListItem_set.get(position).setSmsCheckFlag(!commonMapDrsListItem_set.get(position).isSmsCheckFlag());
                    notifyDataSetChanged();
                });
                mBinding.awb.setText(commonMapDrsListItem_set.get(position).getEdsResponse().getAwbNo().toString());
                if (mEDSItemViewModel.getStatus().equalsIgnoreCase(GlobalConstant.ShipmentStatusConstants.UNDELIVERED)) {
                    mBinding.color.setBackgroundResource(R.drawable.undelivered_gradient);
                    try {
                        if (commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
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
                        if (commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
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
                    mBinding.tray.setBackgroundColor(ContextCompat.getColor(mBinding.getRoot().getContext(), R.color.edslight));
                } else if (mEDSItemViewModel.getStatus().equalsIgnoreCase(GlobalConstant.ShipmentStatusConstants.SYNC)) {
                    mBinding.color.setBackgroundResource(R.drawable.delivered_gradient);
                    mBinding.moreorless.setText(Constants.SHIPMENT_DELIVERED);
                    try {
                        if (commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery() != null) {
                            if (!commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP()
                                    && !commonMapDrsListItem_set.get(position).getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
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
                mBinding.executePendingBindings();
            } catch (Exception e) {
                Toast.makeText(ctx, "EDS onBind():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        // EDS
        @Override
        public void onItemClick(CommonDRSListItem commonDRSListItem) {
            try {
                if (eds_is_item_clicked) {
                    eds_is_item_clicked = false;
                    toDoListViewModel.getDataManager().setDRSTimeStap(commonDRSListItem.getEdsResponse().getAssignDate());
                    toDoListViewModel.getDataManager().setShipperId(commonDRSListItem.getEdsResponse().getShipmentDetail().getShipper_id());
                    Constants.CASH_RECEIPT = "false";
                    Constants.IS_ICICI_FINKARE = 0;
                    toDoListViewModel.getEdsListTask(commonDRSListItem.getEdsResponse().getCompositeKey());
                    Constants.IS_CASH_COLLECTION_ENABLE = false;
                    if (mBinding.tick.getVisibility() == View.VISIBLE) {
                        Constants.CONSIGNEE_PROFILE = true;
                        if(commonDRSListItem.getProfileFound().getDelivery_latitude() == 0 || commonDRSListItem.getProfileFound().getDelivery_longitude() == 0){
                            CommonUtils.showCustomSnackbar(mBinding.getRoot(), "Location Coordinates Not Found", activity);
                            EdsClick(commonDRSListItem, mBinding);
                        } else {
                            //double distance = getDistanceBetweenLocations(new com.google.maps.model.LatLng(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude()));
                            double distance = LocationHelper.getDistanceBetweenPoint(commonDRSListItem.getProfileFound().getDelivery_latitude(), commonDRSListItem.getProfileFound().getDelivery_longitude(), toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude());
                            if (distance > toDoListViewModel.getDataManager().getUndeliverConsigneeRANGE()) {
                                activity.runOnUiThread(() -> getRestrictionEds(commonDRSListItem, mBinding));
                            } else {
                                EdsClick(commonDRSListItem, mBinding);
                            }
                        }
                    } else {
                        Constants.CONSIGNEE_PROFILE = false;
                        EdsClick(commonDRSListItem, mBinding);
                    }
                }
            } catch (Exception e) {
                eds_is_item_clicked = true;
                Toast.makeText(ctx, "EDS onItemClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        public void getRestrictionEds(CommonDRSListItem commonDRSListItem, ItemEdsListViewBinding mBinding) {
            try {
                String dialog_message = ctx.getResources().getString(R.string.commitdialog);
                String positiveButtonText = ctx.getResources().getString(R.string.yes);
                if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("W")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away);
                    positiveButtonText = ctx.getResources().getString(R.string.yes);
                } else if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
                    dialog_message = ctx.getResources().getString(R.string.commitdialog_meter_away_mandatory);
                    positiveButtonText = ctx.getResources().getString(R.string.ok);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
                builder.setCancelable(false);
                builder.setMessage(dialog_message);
                builder.setPositiveButton(positiveButtonText, (dialog, which) -> {
                    if (toDoListViewModel.getDataManager().getConsigneeProfileValue().equalsIgnoreCase("R")) {
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
            toDoListViewModel.getDataManager().getConsigneeProfileValue();
        }

        public void EdsClick(CommonDRSListItem commonDRSListItem, ItemEdsListViewBinding mBinding) {
            Constants.TEMP_DRSID = commonDRSListItem.getEdsResponse().getDrsNo();
            if (commonDRSListItem.getEdsResponse().getShipmentStatus() > 0) {
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
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
                Intent intent;
                if (!commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getPinb()
                        && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getOTP()
                        && !commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getSecure_pin()) {
                    intent = EdsTaskListActivity.getStartIntent(mBinding.getRoot().getContext());
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.IS_KYC_ACTIVE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isIs_kyc_active());
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().isReschedule_enable());
                    intent.putExtra(Constants.COMPOSITE_KEY, commonDRSListItem.getEdsResponse().getCompositeKey());
                    intent.putExtra(Constants.INTENT_KEY, commonDRSListItem.getEdsResponse().getAwbNo());
                    intent.putExtra(Constants.ORDER_ID, commonDRSListItem.getEdsResponse().getShipmentDetail().getOrderNo());
                } else {
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
                    intent.putExtra(Constants.ORDER_ID, commonDRSListItem.getEdsResponse().getShipmentDetail().getOrderNo());
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP1, "");
                    intent.putExtra(Constants.DLIGHT_ENCRYPTED_OTP2, "");
                    intent.putExtra(Constants.ISDELIGHTSHIPMENT, false);
                    intent.putExtra(Constants.RESEND_SECURE_OTP, commonDRSListItem.getEdsResponse().getShipmentDetail().getFlag().getSecure_delivery().getResend_otp_enable());
                }
                mBinding.getRoot().getContext().startActivity(intent);
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
                mBinding.getRoot().getContext().startActivity(intent);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                        String getAddress = CommonUtils.nullToEmpty(CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine1()) + " "
                                + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine2()) + " "
                                + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine3()) + " "
                                + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLine4()) + " "
                                + CommonUtils.nullToEmpty(commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getCity()) + ", "
                                + commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getPincode());
                        String finalAddress = getAddress.replaceAll(",,", ",");
                        double lat = 0.0, lng = 0.0;
                        try {
                            lat = commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLocation().getLat();
                            lng = commonDRSListItem.getEdsResponse().getConsigneeDetail().getAddress().getLocation().getLng();
                        } catch (Exception e) {
                            Logger.e(TAG, String.valueOf(e));
                        }

                        if (lng != 0.0 && lat != 0.0) {
                            String.format(Locale.ENGLISH, "geo:%f,%f", lat, lng);
                            uri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", toDoListViewModel.getlat(), toDoListViewModel.getlng(), "MyLocation", lat, lng, "Destination"));
                        } else {
                            uri = Uri.parse("google.navigation:q=" + finalAddress + "&mode=d&avoid=tf");
                        }

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
                Toast.makeText(ctx, "EDS onMapClick():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ctx, "EDS IsCallAttempted():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
            }
        }

        @Override
        public void onCallClick(CommonDRSListItem mCommonDRSListItem) {
            if (mCommonDRSListItem.getEdsResponse().getShipmentStatus() != Constants.SHIPMENT_ASSIGNED_STATUS) {
                Toast.makeText(mBinding.getRoot().getContext(), mBinding.getRoot().getContext().getString(R.string.shipment_status_done), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                getDrsApiKey = mCommonDRSListItem.getEdsResponse().getCallbridge_Api();
                getDrsPstnKey = mCommonDRSListItem.getEdsResponse().getCallbridgePstn();
                if (getDrsPstnKey != null) {
                    Constants.call_awb = mCommonDRSListItem.getEdsResponse().getAwbNo().toString();
                    Constants.shipment_type = Constants.EDS;
                    startCallIntent(getDrsPstnKey, mBinding.getRoot().getContext());
                }
                else if (getDrsApiKey != null) {
                    callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                    if (callbridgeConfiguration != null) {
                        String getApiType = callbridgeConfiguration.getCb_calling_api();
                        if (getApiType != null) {
                            drsCallListener.makeCallBridgeApiCall(getDrsApiKey, String.valueOf(mCommonDRSListItem.getEdsResponse().getAwbNo()), mCommonDRSListItem.getEdsResponse().getDrsNo(), Constants.EDS);
                        } else {
                            Toast.makeText(ctx, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mBinding.getRoot().getContext(), "Call Config null", Toast.LENGTH_SHORT).show();
                    }
                } else if (getDrsApiKey == null && getDrsPstnKey == null) {
                    try {
                        callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                        if (callbridgeConfiguration != null) {
                            getCbConfigCallType = callbridgeConfiguration.getCb_calling_type();
                        }

                        if (getCbConfigCallType != null) {
                            if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_pstn))) {
                                MasterPSTNFormat = drsCallListener.getDefaultPstn();
                                String callingformat = null;
                                if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.patn_awb))) {
                                    callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.patn_awb), String.valueOf(mCommonDRSListItem.getEdsResponse().getAwbNo()));
                                    Constants.call_awb = String.valueOf(mCommonDRSListItem.getEdsResponse().getAwbNo());
                                    Constants.shipment_type = Constants.EDS;
                                } else if (MasterPSTNFormat.contains(mBinding.getRoot().getContext().getString(R.string.pstn_pin))) {
                                    callingformat = MasterPSTNFormat.replaceAll(mBinding.getRoot().getContext().getString(R.string.pstn_pin), String.valueOf(mCommonDRSListItem.getEdsResponse().getShipmentDetail().getPin()));
                                    Constants.call_pin = String.valueOf(mCommonDRSListItem.getEdsResponse().getShipmentDetail().getPin());
                                    Constants.shipment_type = Constants.EDS;
                                }
                                if (callingformat != null) {
                                    startCallIntent(callingformat, mBinding.getRoot().getContext());
                                }
                            }

                            else if (getCbConfigCallType.equalsIgnoreCase(mBinding.getRoot().getContext().getString(R.string.cb_config_api))) {
                                callbridgeConfiguration = drsCallListener.getCallbridgeconfiguration();
                                if (callbridgeConfiguration != null) {
                                    String getApiType = callbridgeConfiguration.getCb_calling_api();
                                    if (getApiType != null) {
                                        drsCallListener.makeCallBridgeApiCall(callbridgeConfiguration.getCb_calling_api(), String.valueOf(mCommonDRSListItem.getEdsResponse().getAwbNo()), mCommonDRSListItem.getEdsResponse().getDrsNo(), Constants.EDS);
                                    } else {
                                        Toast.makeText(ctx, R.string.null_key_api, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mBinding.getRoot().getContext(), "callconfig null", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(mBinding.getRoot().getContext(), "All Null", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, String.valueOf(e));
                    }
                }

            } catch (Exception e) {
                Toast.makeText(ctx, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Logger.e(TAG, String.valueOf(e));
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
                Toast.makeText(ctx, "EDS indicator():-" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.activity_drs_icons_dialog);
            ImageView dialogButton = dialog.findViewById(R.id.cross);
            dialogButton.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        try {
            ctx = holder.itemView.getContext();
            holder.onBind(position);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    public boolean startCallIntent(String pstn, Context activity) {
        try {
            TelephonyManager tMgr = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            int simState = tMgr.getSimState();
            if (simState == TelephonyManager.SIM_STATE_ABSENT) {
                Toast.makeText(activity, "No Sim Found!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (toDoListViewModel.getDataManager().getIsCallBridgeCheckStatus().equalsIgnoreCase("true")) {
                intent.setData(Uri.parse("tel:" + pstn));
            } else {
                intent.setData(Uri.parse("tel:" + Uri.encode(pstn)));
            }
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            activity.startActivity(intent);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
            return false;
        }
        return true;
    }

    public double getDistanceBetweenLocations(com.google.maps.model.LatLng destination) {
        try {
            double distance;
            GeoApiContext context = new GeoApiContext().setApiKey(DISTANCE_API_KEY);
            DirectionsResult result = DirectionsApi.newRequest(context).mode(TravelMode.DRIVING).units(Unit.METRIC).origin(new com.google.maps.model.LatLng(toDoListViewModel.getDataManager().getCurrentLatitude(), toDoListViewModel.getDataManager().getCurrentLongitude())).optimizeWaypoints(false).destination(destination).awaitIgnoreError();
            String dis = (result.routes[0].legs[0].distance.humanReadable);
            if (dis.endsWith("km")) {
                distance = Double.parseDouble(dis.replaceAll("[^\\d.]", "")) * 1000;
            } else {
                distance = Double.parseDouble(dis.replaceAll("[^\\d.]", ""));
            }
            return distance;
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return 0.0;
    }

    @Override
    public int getItemCount() {
        return commonMapDrsListItem_set.size();
    }

    public void resetClickVariables() {
        forward_is_item_clicked = true;
        rvp_is_item_clicked = true;
        eds_is_item_clicked = true;
    }
}