package in.ecomexpress.sathi.ui.drs.todolist;

import static in.ecomexpress.sathi.utils.CommonUtils.applyTransitionToBackFromActivity;
import static in.ecomexpress.sathi.utils.CommonUtils.logScreenNameInGoogleAnalytics;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BadParcelableException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.nlscan.android.scan.ScanManager;
import com.nlscan.android.scan.ScanSettings;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.barcodelistner.BarcodeHandler;
import in.ecomexpress.barcodelistner.BarcodeResult;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityToDoListBinding;
import in.ecomexpress.sathi.databinding.ItemEdsListViewBinding;
import in.ecomexpress.sathi.databinding.ItemForwardListViewBinding;
import in.ecomexpress.sathi.databinding.ItemRvpListViewBinding;
import in.ecomexpress.sathi.repo.local.db.model.CommonDRSListItem;
import in.ecomexpress.sathi.repo.local.db.model.LiveTrackingLogTable;
import in.ecomexpress.sathi.repo.local.db.model.Remark;
import in.ecomexpress.sathi.repo.remote.model.ErrorResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.forward.DRSForwardTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts.IRTSBaseInterface;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.DRSReverseQCTypeResponse;
import in.ecomexpress.sathi.repo.remote.model.masterdata.CallbridgeConfiguration;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dashboard.drs.list.DRSListFragment;
import in.ecomexpress.sathi.ui.dashboard.drs.map.googlemap.GBaseFragment;
import in.ecomexpress.sathi.ui.dashboard.landing.DashboardActivity;
import in.ecomexpress.sathi.ui.dashboard.starttrip.MyDialogCloseListener;
import in.ecomexpress.sathi.ui.dashboard.switchnumber.SwitchNumberDialog;
import in.ecomexpress.sathi.ui.drs.sms.SMSDialog;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.MessageManager;
import in.ecomexpress.sathi.utils.MultiSpinner;

@AndroidEntryPoint
public class ToDoListActivity extends BaseActivity<ActivityToDoListBinding, ToDoListViewModel> implements BarcodeResult, IToDoListNavigator, DRSremarksInterface, MultiSpinner.MultiSpinnerListener, SearchView.OnQueryTextListener, DRSListFragment.DRSListRemarkListener, DRSListFragment.ListListener, DRSCallListener, MyDialogCloseListener {

    private static final String TAG = ToDoListActivity.class.getSimpleName();
    private ScanManager mScanMgr;
    public String device = (Build.MANUFACTURER + ":" + Build.MODEL).toUpperCase(Locale.US);
    public static final String ITEM_MARKED = "item_marked";
    public static final String SHIPMENT_TYPE = "shipment_type";
    public static final String SHIPMENT_STATUS = "shipment_status";
    public static boolean swipeEnabled = true;
    public static int SORTING_SAVED_SEQUENCE = 2;
    public static int SORTING_LOCATION = 1;
    public static int SORTING_DRS_NO = 0;
    public static int SORTING_TECHNIQUE = SORTING_DRS_NO;
    static int FRAGMENT_LIST = 0;
    static int FRAGMENT_VISIBLE = FRAGMENT_LIST;
    @Inject
    public ToDoListViewModel toDoListViewModel;
    public ActivityToDoListBinding activityToDoListBinding;
    public String[] languages = new String[]{GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL, GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER, GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE, GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED};
    public DRSListFragment drsListFragment = DRSListFragment.newInstance();
    public long DELAY_5 = 1000 * 2;
    boolean isFilterApplied = false;
    CallbridgeConfiguration callbridgeConfiguration = null;
    SMSDialog smsDialog;
    SwitchNumberDialog switchNumberDialog;
    String remarks, myRemarks;
    List<CommonDRSListItem> commonDRSListItemList = new ArrayList<>();
    List<String> itemsDelivery;
    List<String> itemCategory;
    List<String> itemRemarks;
    ArrayAdapter<CharSequence> popupAdapter;
    ArrayList<String> listDataHeader;
    LinkedHashMap<String, List<String>> listDataChild;
    ExpListViewAdapterWithCheckbox expListViewAdapterWithCheckbox;
    ListView lv_languages;
    Button cancelButton;
    BottomSheetDialog bottomSheetDialog;
    ArrayAdapter list_adapter;
    PopupWindow popupWindow;
    BarcodeHandler barcodeHandler;
    String awb = null;
    String notify = null;
    private DrawerLayout mDrawer;
    public int filterCount = 0;
    private final GBaseFragment gMapFragment = GBaseFragment.newInstance();
    private final Handler handler = new Handler();
    SearchView.OnQueryTextListener onQueryTextListener;
    private BroadcastReceiver mReceiver;
    private final MapFilterRunnable mapFilterRunnable = new MapFilterRunnable();

    private final BroadcastReceiver mMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = intent.getBooleanExtra("message", false);
            if (flag) {
                try {
                    toDoListViewModel.getAllNewDRS();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
            } else {
                isFilterApplied = true;
            }
        }
    };

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ToDoListActivity.class);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                getSupportFragmentManager().getFragment(savedInstanceState, DRSListFragment.class.getSimpleName());
            } catch (BadParcelableException e) {
                Logger.e("Error restoring fragment state", String.valueOf(e));
            }
        }
        logScreenNameInGoogleAnalytics(TAG, this);
        toDoListViewModel.setNavigator(this);
        this.activityToDoListBinding = getViewDataBinding();
        toDoListViewModel.getAllApiUrl();
        toDoListViewModel.getEdsReschdData();
        onQueryTextListener = this;
        try {
            toDoListViewModel.getCodcount();
            toDoListViewModel.getEcodcount();
            drsListFragment.notifyDataSetChanged();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }

        // WhatsApp icon replace with SMS icon if their config value is true and false:-
        if (toDoListViewModel.getDataManager().getSMSThroughWhatsapp()) {
            activityToDoListBinding.checkAllIv.setImageResource(R.drawable.whatsapp_rback);
        } else {
            activityToDoListBinding.checkAllIv.setImageResource(R.drawable.ic_sms);
        }
        drsListFragment.getCallBridgeConfig(ToDoListActivity.this);
        drsListFragment.setListRemarkListener(this);
        drsListFragment.setListListener(this);
        activityToDoListBinding.noShip.setVisibility(View.GONE);
        TextView et = activityToDoListBinding.searchview.findViewById(R.id.search_src_text);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        try {
            notify = Objects.requireNonNull(getIntent().getExtras()).getString("notify");
            if (notify != null) {
                activityToDoListBinding.searchview.setQuery(String.valueOf(awb), true);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        try {
            toDoListViewModel.getAllCategoryAssignedCount();
            toDoListViewModel.getAllCategoryDeliveredCount();
            toDoListViewModel.getAllCategoryUnDeliveredCount();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        if (!toDoListViewModel.mysetData()) {
            if (commonDRSListItemList.isEmpty()) {
                try {
                    toDoListViewModel.getAllCategoryAssignedCount();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                drsListFragment.notifyDataSetChanged();
            } else {
                isFilterApplied = true;
                drsListFragment.notifyDataSetChanged();
                try {
                    toDoListViewModel.updateCountLayoutVisibility();
                } catch (Exception e) {
                    Logger.e(TAG, String.valueOf(e));
                }
                runOnUiThread(() -> {
                    initDataToSeekbar();
                    expListViewAdapterWithCheckbox.notifyDataSetChanged();
                });
            }
        }
        toDoListViewModel.getRVPQCValues();
        if (isFilterApplied) {
            activityToDoListBinding.isfilter.setText(Constants.with_filter);
            Constants.is_filter_applied = Constants.with_filter;
        } else {
            activityToDoListBinding.isfilter.setText(Constants.no_filter);
            Constants.is_filter_applied = Constants.no_filter;
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.dashboardtool));
        if (activityToDoListBinding.locationSort.getVisibility() == View.VISIBLE) {
            activityToDoListBinding.lltFooter.setVisibility(View.GONE);
            activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
        }
        try {
            toDoListViewModel.getcallConfig();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        activityToDoListBinding.locationSort.setOnClickListener(v -> {
            PopupWindow popUp = popupWindowSort();
            popUp.showAsDropDown(v, 0, 0);
        });
        itemsDelivery = Arrays.asList(getResources().getStringArray(R.array.drs_status));
        itemCategory = Arrays.asList(getResources().getStringArray(R.array.drs_items));
        itemRemarks = Arrays.asList(getResources().getStringArray(R.array.drs_remarksby));
        setUpNavigation();
        activityToDoListBinding.searchview.setOnQueryTextListener(onQueryTextListener);
        gMapFragment.setContext(ToDoListActivity.this);
        swipeEnabled = true;
        barcodeHandler = new BarcodeHandler(this, "ScannerLM", this);
        barcodeHandler.enableScanner();
        activityToDoListBinding.resetSort.setOnClickListener(v -> onResetSequenceClick());
    }

    @Override
    public void onSendClick() {
        try {
            if (toDoListViewModel.awbhashset.isEmpty()) {
                showError(getString(R.string.please_select_shipments));
                return;
            }
            if (toDoListViewModel.getDataManager().getSMSThroughWhatsapp()) {
                if (toDoListViewModel.awbhashset.size() > 1) {
                    showError(getString(R.string.please_select_one_shipment_only_at_a_time));
                } else {
                    CommonDRSListItem commonDRSListItem = null;
                    String phoneNumber = "";
                    String brand_name = "";
                    String awb_no = "";
                    String address = "";
                    for (int i = 0; i < commonDRSListItemList.size(); i++) {
                        if (commonDRSListItemList.get(i).getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                            if (commonDRSListItemList.get(i).getDrsForwardTypeResponse().getAwbNo() == Long.parseLong(toDoListViewModel.myGetSelectedAwb.get(0))) {
                                commonDRSListItem = commonDRSListItemList.get(i);
                                awb_no = String.valueOf(commonDRSListItemList.get(i).getDrsForwardTypeResponse().getAwbNo());
                                brand_name = commonDRSListItemList.get(i).getDrsForwardTypeResponse().getShipmentDetails().getShipper();
                                phoneNumber = commonDRSListItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getMobile();
                                address = commonDRSListItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine1() + commonDRSListItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getLine2() + commonDRSListItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getCity() + commonDRSListItemList.get(i).getDrsForwardTypeResponse().getConsigneeDetails().getAddress().getPincode();
                            }
                        } else if (commonDRSListItemList.get(i).getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                            if (commonDRSListItemList.get(i).getEdsResponse().getAwbNo() == Long.parseLong(toDoListViewModel.myGetSelectedAwb.get(0))) {
                                commonDRSListItem = commonDRSListItemList.get(i);
                                awb_no = String.valueOf(commonDRSListItemList.get(i).getEdsResponse().getAwbNo());
                                brand_name = commonDRSListItemList.get(i).getEdsResponse().getShipmentDetail().getCustomerName();
                                phoneNumber = commonDRSListItemList.get(i).getEdsResponse().getConsigneeDetail().getMobile();
                                address = commonDRSListItemList.get(i).getEdsResponse().getConsigneeDetail().getAddress().getLine1() + commonDRSListItemList.get(i).getEdsResponse().getConsigneeDetail().getAddress().getLine2() + commonDRSListItemList.get(i).getEdsResponse().getConsigneeDetail().getAddress().getCity() + commonDRSListItemList.get(i).getEdsResponse().getConsigneeDetail().getAddress().getPincode();
                            }
                        } else if (commonDRSListItemList.get(i).getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                            if (commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getAwbNo() == Long.parseLong(toDoListViewModel.myGetSelectedAwb.get(0))) {
                                commonDRSListItem = commonDRSListItemList.get(i);
                                awb_no = String.valueOf(commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getAwbNo());
                                brand_name = commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getShipmentDetails().getShipper();
                                phoneNumber = commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile();
                                address = commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine1() + commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getLine2() + commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getCity() + commonDRSListItemList.get(i).getDrsReverseQCTypeResponse().getConsigneeDetails().getAddress().getPincode();
                            }
                        }
                    }
                    try {
                        String template = toDoListViewModel.getWhatsAppTechTemplate(brand_name, awb_no, address);
                        CommonUtils.sendSMSViaWhatsApp(getApplicationContext(), ToDoListActivity.this, phoneNumber, template);
                        String remarks = GlobalConstant.RemarksTypeConstants.I_Am_On_The_Way;
                        toDoListViewModel.getDataManager().setSendSmsCount(awb_no + Constants.TECH_PARK_COUNT, (toDoListViewModel.getDataManager().getSendSmsCount(awb_no + Constants.TECH_PARK_COUNT) + 1));
                        Remark remark = Objects.requireNonNull(commonDRSListItem).setRemark(remarks, toDoListViewModel.getEmployeeCode(), toDoListViewModel.getDataManager().getSendSmsCount(awb_no + Constants.TECH_PARK_COUNT));
                        toDoListViewModel.addRemarks(remark);
                        updateRemarks(commonDRSListItem, remark, "");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                smsDialog = SMSDialog.newInstance(ToDoListActivity.this, new ToDoListActivity(), toDoListViewModel.myGetSelectedAwb);
                smsDialog.setMyDialogCloseListener(ToDoListActivity.this);
                smsDialog.show(getSupportFragmentManager());
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onCancelClick() {
        swipeEnabled = true;
        drsListFragment.cancelCheckboxes();
        activityToDoListBinding.lltFooter.setVisibility(View.GONE);
        activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
        activityToDoListBinding.filter.setVisibility(View.VISIBLE);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void toolbarVisibility(List<CommonDRSListItem> commonDRSListItems) {
        handleVisibilityToolbarIcons(!commonDRSListItems.isEmpty());
    }

    @Override
    public void showErrorMessage(boolean status) {
        if (status) {
            showSnackbar(getString(R.string.http_500_msg));
        } else {
            showSnackbar(getString(R.string.server_down_msg));
        }
    }

    @Override
    public void smsResponseThrowable(boolean status) {
        if (status) {
            drsListFragment.cancelCheckboxes();
            activityToDoListBinding.lltFooter.setVisibility(View.GONE);
            activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
            showInfo(getString(R.string.http_500_msg));
        } else {
            drsListFragment.cancelCheckboxes();
            activityToDoListBinding.lltFooter.setVisibility(View.GONE);
            activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
            showInfo(getString(R.string.server_down_msg));
        }
    }

    @Override
    public void onErrorMsg(String message) {
        showSnackbar(message);
    }

    @Override
    public void mResultReceiver1(String strBarcodeScan) {
        if (strBarcodeScan != null) {
            activityToDoListBinding.searchview.setQuery(strBarcodeScan, false);
            drsListFragment.filter(strBarcodeScan);
        }
    }

    @Override
    public void onSwitchCallBridgeClick() {
        try {
            if (toDoListViewModel.getDataManager().getPstnFormat() != null) {
                openSwitchNumberDialog();
            } else {
                showInfo(getString(R.string.not_available));
            }
        } catch (NullPointerException e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void setDrsData(List<CommonDRSListItem> commonDRSListItemList) {
        this.commonDRSListItemList = commonDRSListItemList;
    }

    @Override
    public void setCount() {
        initDataToSeekbar();
    }

    @Override
    public void OnCheckBoxClick() {
        if (activityToDoListBinding.checkboxTkPrk.isChecked()) {
            drsListFragment.handleCheckCheckboxes(true);
        } else {
            toDoListViewModel.awbhashset.clear();
            drsListFragment.handleCheckCheckboxes(false);
        }
    }

    private void openSwitchNumberDialog() {
        switchNumberDialog = SwitchNumberDialog.newInstance(ToDoListActivity.this);
        switchNumberDialog.show(getSupportFragmentManager());
    }

    private void handleDrsView(List<CommonDRSListItem> commonDRSListItems) {
        if (commonDRSListItems.isEmpty()) {
            activityToDoListBinding.mainLayout.setVisibility(View.GONE);
            activityToDoListBinding.noShip.setVisibility(View.VISIBLE);
            activityToDoListBinding.shipmentStatus.setVisibility(View.GONE);
            activityToDoListBinding.tools.setVisibility(View.GONE);
        } else {
            activityToDoListBinding.noShip.setVisibility(View.GONE);
            activityToDoListBinding.shipmentStatus.setVisibility(View.VISIBLE);
            activityToDoListBinding.mainLayout.setVisibility(View.VISIBLE);
            activityToDoListBinding.tools.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void callConfig(CallbridgeConfiguration myMasterDataReasonCodeResponse) {
        callbridgeConfiguration = myMasterDataReasonCodeResponse;
    }

    @Override
    public void ExpandableList() {
        try {
            runOnUiThread(() -> {
                initDataToSeekbar();
                expListViewAdapterWithCheckbox.notifyDataSetChanged();
            });
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void updateExpandableAdapter() {
        try {
            drsListFragment.notifyDataSetChanged();
            enableExpandableList();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private ArrayAdapter<String> listOptionsSortAdapter() {
        String[] popUpContents = {"Outscan ID"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToDoListActivity.this, android.R.layout.simple_list_item_1, popUpContents) {
            @NonNull
            @SuppressLint("ResourceAsColor")
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String item = getItem(position);
                TextView listItem = new TextView(ToDoListActivity.this);
                listItem.setTextColor(Color.BLACK);
                listItem.setText(item);
                listItem.setTextSize(13);
                listItem.setPadding(20, 20, 20, 20);
                listItem.setBackgroundResource(R.color.drsbg);
                if (position == 0) {
                    if (SORTING_TECHNIQUE == SORTING_DRS_NO) {
                        listItem.setBackgroundResource(R.drawable.border);
                        listItem.setTextColor(Color.BLACK);
                    } else {
                        listItem.setBackgroundResource(R.color.drsbg);
                        listItem.setTextColor(Color.BLACK);
                    }
                }
                if (position == 1) {
                    if (SORTING_TECHNIQUE == SORTING_LOCATION) {
                        listItem.setBackgroundResource(R.drawable.border);
                        listItem.setTextColor(Color.BLACK);
                    } else {
                        listItem.setBackgroundResource(R.color.drsbg);
                        listItem.setTextColor(Color.BLACK);
                    }
                }
                return listItem;
            }
        };
        return adapter;
    }

    private void addListFragment() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = manager.findFragmentByTag(GBaseFragment.class.getSimpleName());
            drsListFragment.setActivityContext(ToDoListActivity.this);
            transaction.replace(activityToDoListBinding.fragmentContainer.getId(), drsListFragment, DRSListFragment.class.getSimpleName());
            transaction.commit();
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            if (fragment != null) {
                transaction.remove(fragment).commit();
            }
            if (!commonDRSListItemList.isEmpty()) {
                updateUI();
            }
            handleDrsView(commonDRSListItemList);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void addMapFragment() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = manager.findFragmentByTag(DRSListFragment.class.getSimpleName());
            transaction.replace(activityToDoListBinding.fragmentContainer.getId(), gMapFragment, GBaseFragment.class.getSimpleName());
            transaction.commit();
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            if (fragment != null) {
                transaction.remove(fragment).commit();
            }
            gMapFragment.updateList(drsListFragment.getFlterShipments());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @SuppressLint("ResourceType")
    private PopupWindow popupWindowSort() {
        popupWindow = new PopupWindow(this);
        popupAdapter = ArrayAdapter.createFromResource(this, R.array.drs_sortby, R.layout.spinner_common);
        popupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ListView listViewSort = new ListView(this);
        listViewSort.setAdapter(listOptionsSortAdapter());
        listViewSort.setOnItemClickListener(onItemClickListener());
        popupWindow.setFocusable(true);
        popupWindow.setWidth(350);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewSort);
        return popupWindow;
    }

    private AdapterView.OnItemClickListener onItemClickListener() {
        return (parent, view, position, id) -> {
            if (position == 0) {
                SORTING_TECHNIQUE = SORTING_DRS_NO;
            } else if (position == 1) {
                SORTING_TECHNIQUE = SORTING_LOCATION;
            }
            try {
                toDoListViewModel.applySort();
                dismissPopup();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        };
    }

    private void dismissPopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initDataToSeekbar() {
    }

    public void enableExpandableList() {
        try {
            listDataHeader = new ArrayList<>();
            listDataChild = new LinkedHashMap<>();
            ExpandableListView expListView = findViewById(R.id.left_drawer);
            prepareListData(listDataHeader, listDataChild);
            expListViewAdapterWithCheckbox = new ExpListViewAdapterWithCheckbox(this, listDataHeader, listDataChild, toDoListViewModel, commonDRSListItemList);
            expListView.setAdapter(expListViewAdapterWithCheckbox);
            for (int i = 0; i < expListViewAdapterWithCheckbox.getGroupCount(); i++) {
                expListView.expandGroup(i);
            }
            expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
            expListView.setOnGroupExpandListener(groupPosition -> {

            });
            expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            activityToDoListBinding.searchview.setQuery(result.getContents(), false);
            drsListFragment.filter(result.getContents());
        }
    });

    @Override
    public void handleDialogClose() {
        drsListFragment.cancelCheckboxes();
        activityToDoListBinding.lltFooter.setVisibility(View.GONE);
        activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
    }


    @Override
    public void handledrsview(List<CommonDRSListItem> commonDRSListItems) {
        handleDrsView(commonDRSListItems);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onScanClick() {
        if (!CommonUtils.isAllPermissionAllow(this)) {
            openSettingActivity();
            return;
        }
        if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
            mScanMgr = ScanManager.getInstance();
            mScanMgr.startScan();
            mScanMgr.disableBeep();
            mScanMgr.setScanEnable(true);
            mScanMgr.setOutpuMode(ScanSettings.Global.VALUE_OUT_PUT_MODE_BROADCAST);
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(mResultReceiver(), intFilter);
        } else {
            ScanOptions options = new ScanOptions();
            options.setPrompt(getString(R.string.scan_a_barcode_or_qr_code));
            options.setBeepEnabled(true);
            options.setTorchEnabled(true);
            options.setOrientationLocked(true);
            options.setCaptureActivity(CaptureActivity.class);
            barLauncher.launch(options);
        }
    }

    private BroadcastReceiver mResultReceiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ScanManager.ACTION_SEND_SCAN_RESULT.equals(action)) {
                    String scannedValue = intent.getStringExtra("SCAN_BARCODE1");
                    if (scannedValue != null && !scannedValue.isEmpty()) {
                        activityToDoListBinding.searchview.setQuery(scannedValue, false);
                        drsListFragment.filter(scannedValue);
                    }
                }
            }
        };
        return mReceiver;
    }

    @Override
    public void hideLayout(String which) {
        if (which.equals(Constants.FWD)) {
            activityToDoListBinding.llFwdColor.setVisibility(View.GONE);
        }
        if (which.equals(Constants.RTS)) {
            activityToDoListBinding.llRtsColor.setVisibility(View.GONE);
        }
        if (which.equals(Constants.RVP)) {
            activityToDoListBinding.llRvpColor.setVisibility(View.GONE);
        }
        if (which.equals(Constants.EDS)) {
            activityToDoListBinding.llEdsColor.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLayout(String which) {
        try {
            if (which.equals(Constants.FWD)) {
                activityToDoListBinding.llFwdColor.setVisibility(View.VISIBLE);
            }
            if (which.equals(Constants.RTS)) {
                activityToDoListBinding.llRtsColor.setVisibility(View.VISIBLE);
            }
            if (which.equals(Constants.RVP)) {
                activityToDoListBinding.llRvpColor.setVisibility(View.VISIBLE);
            }
            if (which.equals(Constants.EDS)) {
                activityToDoListBinding.llEdsColor.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void setDataToDRSFragment(List<CommonDRSListItem> commonDRSListItems) {
        drsListFragment.setData(commonDRSListItems, toDoListViewModel);
    }

    @Override
    public void applyLocationBasedSort(List<CommonDRSListItem> commonDRSListItems) {
        drsListFragment.setFilterString("");
        try {
            if (commonDRSListItems != null && !commonDRSListItems.isEmpty()) {
                this.commonDRSListItemList.clear();
                this.commonDRSListItemList.addAll(commonDRSListItems);
                String filterString = activityToDoListBinding.searchview.getQuery().toString();
                if (filterString != null && !filterString.isEmpty()) {
                    drsListFragment.setFilterString(filterString);
                }
                drsListFragment.setData(commonDRSListItems, toDoListViewModel);
                if (gMapFragment != null && gMapFragment.isVisible()) {
                    gMapFragment.updateList(commonDRSListItems);
                }
            } else {
                showInfo("No Shipment Found");
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private void prepareListData(List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        listDataHeader.add(Constants.SHIPMENT_TYPE);
        listDataHeader.add(Constants.SHIPMENT_STATUS);
        listDataHeader.add(Constants.SHIPMENT_REMARK);
        listDataChild.put(listDataHeader.get(0), itemCategory);
        listDataChild.put(listDataHeader.get(1), itemsDelivery);
        listDataChild.put(listDataHeader.get(2), itemRemarks);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onFilterClick() {
        mDrawer.openDrawer(Gravity.RIGHT);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClearFilterClick() {
        try {
            toDoListViewModel.initFilterObservers();
            toDoListViewModel.getAllNewDRS();
            activityToDoListBinding.isfilter.setText(Constants.no_filter);
            mDrawer.closeDrawer(Gravity.END);
        } catch (Exception ex) {
            Logger.e(TAG, String.valueOf(ex));
        }
    }

    private void setUpNavigation() {
        mDrawer = activityToDoListBinding.drawerView;
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(ToDoListActivity.this);
            }
        };
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        toDoListViewModel.getAllNewDRS();
        if (!commonDRSListItemList.isEmpty()) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            int shipment_status = extras.getInt(SHIPMENT_STATUS);
            String awbNo = extras.getString(ITEM_MARKED);
            String drsNo = extras.getString(Constants.DRS_ID);
            try {
                if (shipment_status > 0 && awbNo != null && !awbNo.isEmpty()) {
                    for (CommonDRSListItem commonDRSListItem : commonDRSListItemList) {
                        if (Objects.equals(commonDRSListItem.getType(), GlobalConstant.ShipmentTypeConstants.FWD)) {
                            DRSForwardTypeResponse response = commonDRSListItem.getDrsForwardTypeResponse();
                            if (awbNo.equalsIgnoreCase(String.valueOf(response.getAwbNo()))) {
                                commonDRSListItem.setCommonDrsStatus(shipment_status);
                                response.setShipmentStatus(shipment_status);
                            }
                        } else if (Objects.equals(commonDRSListItem.getType(), GlobalConstant.ShipmentTypeConstants.RVP)) {
                            DRSReverseQCTypeResponse response = commonDRSListItem.getDrsReverseQCTypeResponse();
                            if (awbNo.equalsIgnoreCase(String.valueOf(response.getAwbNo()))) {
                                commonDRSListItem.setCommonDrsStatus(shipment_status);
                                response.setShipmentStatus(shipment_status);
                            }
                        } else if (Objects.equals(commonDRSListItem.getType(), GlobalConstant.ShipmentTypeConstants.RTS)) {
                            IRTSBaseInterface response = commonDRSListItem.getIRTSInterface();
                            if (awbNo.equalsIgnoreCase(String.valueOf(response.getDetails().getId()))) {
                                commonDRSListItem.setCommonDrsStatus(shipment_status);
                                response.getDetails().setShipmentStatus(shipment_status);
                            }
                        } else if (Objects.equals(commonDRSListItem.getType(), GlobalConstant.ShipmentTypeConstants.EDS)) {
                            EDSResponse response = commonDRSListItem.getEdsResponse();
                            if (Objects.requireNonNull(drsNo).equalsIgnoreCase(String.valueOf(response.getDrsNo())) && awbNo.equalsIgnoreCase(String.valueOf(response.getAwbNo()))) {
                                commonDRSListItem.setCommonDrsStatus(shipment_status);
                                response.setShipmentStatus(shipment_status);
                            }
                        }
                    }
                    try {
                        if (!commonDRSListItemList.isEmpty()) {
                            commonDRSListItemList = toDoListViewModel.getSortedData(commonDRSListItemList);
                            drsListFragment.setData(commonDRSListItemList, toDoListViewModel);
                            expListViewAdapterWithCheckbox.notifyDataSetChanged();
                            toDoListViewModel.setNoRemarkStatus(commonDRSListItemList);
                            toDoListViewModel.getAllCategoryAssignedCount();
                            toDoListViewModel.getAllCategoryDeliveredCount();
                            toDoListViewModel.getAllCategoryUnDeliveredCount();
                            toDoListViewModel.updateCountLayoutVisibility();
                        }
                        toDoListViewModel.getCodcount();
                        toDoListViewModel.getEcodcount();
                    } catch (Exception e) {
                        Logger.e(TAG, String.valueOf(e));
                    }
                    Runnable runnable = () -> {
                        for (int i = 0; i <= 10; i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Logger.e(TAG, String.valueOf(e));
                            }
                            handler.post(this::initDataToSeekbar);
                        }
                    };
                    new Thread(runnable).start();
                }
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        } else {
            try {
                toDoListViewModel.getAllNewDRS();
                toDoListViewModel.getAllCategoryAssignedCount();
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toDoListViewModel.getDataManager().getENABLEDIRECTDIAL().equalsIgnoreCase("true")) {
            activityToDoListBinding.switchCallbridge.setVisibility(View.GONE);
        } else {
            activityToDoListBinding.switchCallbridge.setVisibility(View.GONE);
        }
        if (toDoListViewModel.consigneeContactNumber.get() == null) {
            toDoListViewModel.consigneeContactNumber.set("");
        }
        if (!Objects.requireNonNull(toDoListViewModel.consigneeContactNumber.get()).equalsIgnoreCase("")) {
            CommonUtils.deleteNumberFromCallLogsAsync(toDoListViewModel.consigneeContactNumber.get(), ToDoListActivity.this);
        }
        Constants.BACKTODOLIST = false;
        Constants.PLAIN_OTP = "";
        Constants.OFD_OTP_VERIFIED = false;
        toDoListViewModel.getAllApiUrl();
        toDoListViewModel.getEdsReschdData();
        toDoListViewModel.getAllNewDRS();
        Constants.water_mark_emp_code = toDoListViewModel.getDataManager().getEmp_code();
        Constants.SYNCFLAG = true;
        if (ToDoListViewModel.device.equals(Constants.NEWLAND)) {
            IntentFilter intFilter = new IntentFilter(ScanManager.ACTION_SEND_SCAN_RESULT);
            registerReceiver(toDoListViewModel.mResultReceiver(), intFilter);
        } else {
            barcodeHandler.enableScanner();
        }
        if (isFilterApplied) {
            activityToDoListBinding.isfilter.setText(Constants.with_filter);
            Constants.is_filter_applied = Constants.with_filter;
        } else {
            activityToDoListBinding.isfilter.setText(Constants.no_filter);
            Constants.is_filter_applied = Constants.no_filter;
        }
        addListFragment();
    }

    @Override
    public void onResult(String s) {
        showSnackbar(s);
        if (s != null) {
            activityToDoListBinding.searchview.setQuery(s, false);
            drsListFragment.filter(s);
        }
    }

    @Override
    public ToDoListViewModel getViewModel() {
        return toDoListViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_to_do_list;
    }

    @Override
    public void onHandleError(ErrorResponse errorDetails) {
        showSnackbar(errorDetails.getEResponse().getDescription());
    }

    private void updateUI() {
        if (this.commonDRSListItemList != null && !commonDRSListItemList.isEmpty()) {
            try {
                String filterString = activityToDoListBinding.searchview.getQuery().toString();
                if (filterString != null && !filterString.isEmpty()) {
                    drsListFragment.setFilterString(filterString);
                }
                drsListFragment.getCallBridgeConfig(ToDoListActivity.this);
                drsListFragment.setData(this.commonDRSListItemList, toDoListViewModel);
                swipeEnabled = activityToDoListBinding.lltFooter.getVisibility() == View.GONE;
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
        }
    }

    public DRSCallListener getDRSCallListener() {
        return ToDoListActivity.this;
    }

    @Override
    public void onBackClick() {
        if (gMapFragment.isBottomSheetVisible) {
            gMapFragment.bottomSheetDialog.dismiss();
        }
        onBackPressed();
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    public void onApplyClick() {
        try {
            if (Objects.requireNonNull(toDoListViewModel.totalAssignedCount.get()).equalsIgnoreCase("0") && Objects.requireNonNull(toDoListViewModel.totalDeliveredCount.get()).equalsIgnoreCase("0") && Objects.requireNonNull(toDoListViewModel.totalUndeliveredCount.get()).equalsIgnoreCase("0")) {
                showSnackbar("No shipment found.");
                mDrawer.closeDrawer(Gravity.RIGHT);
            } else {
                drsListFragment.clearsmscheck();
                activityToDoListBinding.searchview.setQuery("", true);
                HashMap<Integer, boolean[]> mChildCheckStates;
                mChildCheckStates = expListViewAdapterWithCheckbox.getmyChildCheckStates();
                boolean[] shipmentTypeOriginal = mChildCheckStates.get(ExpandListAdapter.SHIPMENT_TYPE);
                boolean[] shipmentStatusOriginal = mChildCheckStates.get(ExpandListAdapter.SHIPMENT_STATUS);
                boolean[] shipmentRemarksOriginal = mChildCheckStates.get(ExpandListAdapter.SHIPMENT_REMARKS);
                boolean[] shipmentType = Arrays.copyOf(Objects.requireNonNull(shipmentTypeOriginal), shipmentTypeOriginal.length);
                boolean[] shipmentStatus = Arrays.copyOf(Objects.requireNonNull(shipmentStatusOriginal), shipmentStatusOriginal.length);
                boolean[] shipmentRemarks = Arrays.copyOf(Objects.requireNonNull(shipmentRemarksOriginal), shipmentRemarksOriginal.length);
                filterCount = 0;
                for (boolean b : shipmentType) {
                    if (b) {
                        filterCount++;
                    }
                }
                for (boolean status : shipmentStatus) {
                    if (status) {
                        filterCount++;
                    }
                }
                for (boolean shipmentRemark : shipmentRemarks) {
                    if (shipmentRemark) {
                        filterCount++;
                    }
                }
                if (filterCount == 0) {
                    activityToDoListBinding.tvFilterCount.setVisibility(View.GONE);
                } else {
                    activityToDoListBinding.tvFilterCount.setVisibility(View.VISIBLE);
                    activityToDoListBinding.tvFilterCount.setText(filterCount + "");
                    toDoListViewModel.getDataManager().setFilterCount(filterCount);
                }

                if (!shipmentType[0] && !shipmentType[1] && !shipmentType[2] && !shipmentType[3]) {
                    shipmentType[0] = true;
                    shipmentType[1] = true;
                    shipmentType[2] = true;
                    shipmentType[3] = true;
                }
                //3. if no shipment status is selected than set all is true
                if (!shipmentStatus[0] && !shipmentStatus[1] && !shipmentStatus[2]) {
                    shipmentStatus[0] = true;
                    shipmentStatus[1] = true;
                    shipmentStatus[2] = true;
                }
                //4 if no remark is selected than set all is true.
                if (!shipmentRemarks[0] && !shipmentRemarks[1] && !shipmentRemarks[2] && !shipmentRemarks[3] && !shipmentRemarks[4]) {
                    shipmentRemarks[0] = true;
                    shipmentRemarks[1] = true;
                    shipmentRemarks[2] = true;
                    shipmentRemarks[3] = true;
                    shipmentRemarks[4] = true;
                }
                toDoListViewModel.updateCountLayoutVisibility();
                toDoListViewModel.applyFilterOnDRS(shipmentType, shipmentStatus, shipmentRemarks);
                if (isFilterApplied) {
                    activityToDoListBinding.isfilter.setText(Constants.with_filter);
                    Constants.is_filter_applied = Constants.with_filter;
                } else {
                    activityToDoListBinding.isfilter.setText(Constants.no_filter);
                    Constants.is_filter_applied = Constants.no_filter;
                }
                mDrawer.closeDrawer(Gravity.RIGHT);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void notifyAdapter() {
        drsListFragment.refreshrecyclerview();
        runOnUiThread(() -> expListViewAdapterWithCheckbox.notifyDataSetChanged());
    }

    @Override
    public void showError(String error) {
        showSnackbar(error);
    }

    public void handleVisibilityToolbarIcons(boolean visibility) {
        if (visibility) {
            swipeEnabled = true;
            drsListFragment.cancelCheckboxes();
            activityToDoListBinding.lltFooter.setVisibility(View.GONE);
            activityToDoListBinding.checkboxTkPrk.setVisibility(View.GONE);
            activityToDoListBinding.filter.setVisibility(View.VISIBLE);
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        } else {
            swipeEnabled = false;
            drsListFragment.handleCheckboxes();
            activityToDoListBinding.lltFooter.setVisibility(View.VISIBLE);
            activityToDoListBinding.checkboxTkPrk.setVisibility(View.VISIBLE);
            activityToDoListBinding.checkboxTkPrk.setChecked(true);
            activityToDoListBinding.filter.setVisibility(View.GONE);
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void handleDRSCheckboxes() {
        if (FRAGMENT_VISIBLE == FRAGMENT_LIST) {
            handleVisibilityToolbarIcons(activityToDoListBinding.lltFooter.getVisibility() != View.GONE);
        }
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        try {
            if (selected.length == 4) {
                toDoListViewModel.setItemSelectedDataStatus(selected);
            } else {
                toDoListViewModel.setItemSelectedDataCategory(selected);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onBackPressed() {
        if (notify != null) {
            Intent intent = new Intent(ToDoListActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
        applyTransitionToBackFromActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessage, new IntentFilter(Constants.SYNC_SERVICE));
        try {
            if (!Constants.broad_call_type.isEmpty() && !Constants.broad_shipment_type.isEmpty()) {
                String getCallType = Constants.broad_call_type;
                String getShipmentType = Constants.broad_shipment_type;
                if (getCallType.equalsIgnoreCase(Constants.call_awb)) {
                    if (getShipmentType.equals(Constants.RVP)) {
                        toDoListViewModel.updateRVPCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.FWD)) {
                        toDoListViewModel.updateForwardCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.EDS)) {
                        toDoListViewModel.updateEDSCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.RTS)) {
                        toDoListViewModel.updateRTSCallAttempted(getCallType);
                    }
                } else if (getCallType.equalsIgnoreCase(Constants.call_pin)) {
                    if (getShipmentType.equals(Constants.RVP)) {
                        toDoListViewModel.updateRVPpinCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.FWD)) {
                        toDoListViewModel.updateForwardpinCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.EDS)) {
                        toDoListViewModel.updateEDSpinCallAttempted(getCallType);
                    }
                    if (getShipmentType.equals(Constants.RTS)) {
                        toDoListViewModel.updateRTSCallAttempted(getCallType);
                    }
                }
                Constants.call_awb = "";
                Constants.call_pin = "";
                Constants.shipment_type = "";
                Constants.broad_call_type = "";
                Constants.broad_shipment_type = "";
                Constants.call_intent_number = "";
                drsListFragment.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            if (query != null) drsListFragment.filter(query);
        } catch (Exception ex) {
            Logger.e(TAG, String.valueOf(ex));
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null) try {
            drsListFragment.filter(newText);
            try {
                handler.removeCallbacks(mapFilterRunnable);
            } catch (Exception e) {
                Logger.e(TAG, String.valueOf(e));
            }
            mapFilterRunnable.setCharSequence(newText);
            handler.postDelayed(mapFilterRunnable, DELAY_5);
        } catch (Exception ex) {
            Logger.e(TAG, String.valueOf(ex));
        }
        return false;
    }

    @Override
    public void setRemarks(String remarks) {
        drsListFragment.setRemarks(remarks);
    }

    private void showRemarkWindow(int position) {
        try {
            CommonDRSListItem commonDRSListItem = drsListFragment.getFlterShipments().get(position);
            if (commonDRSListItem.getType().equalsIgnoreCase(Constants.RTS)) {
                showInfo(getString(R.string.remark_disable_for_RTS));
                return;
            }
            if (!isValidToAddRemark(commonDRSListItem)) {
                MessageManager.showToast(ToDoListActivity.this, "Can not add remarks for delivered and undelivered shipments.");
                return;
            }
            if (activityToDoListBinding.lltFooter.getVisibility() == View.VISIBLE) {
                return;
            }
            View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
            lv_languages = view.findViewById(R.id.lv_languages);
            cancelButton = view.findViewById(R.id.cancelbtn);
            list_adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, languages) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    return textView;
                }
            };
            lv_languages.setAdapter(list_adapter);
            lv_languages.setOnItemClickListener((parent, view1, position1, id) -> {
                final String item = (String) parent.getItemAtPosition(position1);
                if (commonDRSListItem.getType().equalsIgnoreCase(Constants.RTS)) {
                    showInfo(getString(R.string.this_remark_is_not_available_for_rts_shipment));
                    bottomSheetDialog.dismiss();
                } else {
                    switch (item) {
                        case GlobalConstant.RemarksTypeConstants.CONSIGNEE_NOT_PICKING_CALL:
                            if (commonDRSListItem.getType().equalsIgnoreCase(Constants.RTS)) {
                                showInfo(getString(R.string.this_remark_is_not_available_for_rts_shipment));
                            } else {
                                Remark remark;
                                remarks = item;
                                awb = getAWB(commonDRSListItem);
                                toDoListViewModel.getRemarkAwb(awb);
                                if (toDoListViewModel.getDataManager().getSMSThroughWhatsapp()) {
                                    String awb_no = "";
                                    String brand_name = "";
                                    if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                                        awb_no = String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
                                        brand_name = commonDRSListItem.getDrsForwardTypeResponse().getShipmentDetails().getShipper();
                                    } else if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                                        awb_no = String.valueOf(commonDRSListItem.getEdsResponse().getAwbNo());
                                        brand_name = commonDRSListItem.getEdsResponse().getShipmentDetail().getCustomerName();
                                    } else if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                                        awb_no = String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
                                        brand_name = commonDRSListItem.getDrsReverseQCTypeResponse().getShipmentDetails().getShipper();
                                    }
                                    String phoneNumber = getPhoneNumber(commonDRSListItem);
                                    try {
                                        String template = toDoListViewModel.getWhatsAppRemarkTemplate(awb_no, brand_name);
                                        CommonUtils.sendSMSViaWhatsApp(getApplicationContext(), ToDoListActivity.this, phoneNumber, template);
                                    } catch (UnsupportedEncodingException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    toDoListViewModel.callSmsApi(ToDoListActivity.this);
                                }
                                toDoListViewModel.getDataManager().setTryReachingCount(awb + Constants.TRY_RECHING_COUNT, toDoListViewModel.getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT) + 1);
                                remark = commonDRSListItem.setRemark(remarks, toDoListViewModel.getEmployeeCode(), toDoListViewModel.getDataManager().getTryReachingCount(awb + Constants.TRY_RECHING_COUNT));
                                toDoListViewModel.addRemarks(remark);
                                updateRemarks(commonDRSListItem, remark, "");
                                bottomSheetDialog.dismiss();
                            }
                            break;
                        case GlobalConstant.RemarksTypeConstants.CONSIGNEE_REQUESTED_TO_CALL_LATER:
                        case GlobalConstant.RemarksTypeConstants.CONSIGNEE_CALLED: {
                            Remark remark;
                            remarks = item;
                            remark = commonDRSListItem.setRemark(remarks, toDoListViewModel.getEmployeeCode(), 0);
                            toDoListViewModel.addRemarks(remark);
                            updateRemarks(commonDRSListItem, remark, "");
                            bottomSheetDialog.dismiss();
                            break;
                        }
                        case GlobalConstant.RemarksTypeConstants.SAME_DAY_RESCHEDULE:
                            TimePickerDialog.OnTimeSetListener timePickerListener = (view11, selectedHour, selectedMinute) -> {
                                bottomSheetDialog.dismiss();
                                if (!isValidTimeSelected(selectedHour, selectedMinute)) {
                                    showSnackbar(getString(R.string.error_same_time_reschedule));
                                    return;
                                }
                                String form;
                                if (selectedHour > 12) {
                                    selectedHour = selectedHour - 12;
                                    form = "PM";
                                } else if (selectedHour == 12) {
                                    form = "PM";
                                } else {
                                    if (selectedHour == 0) {
                                        selectedHour = 12;
                                    }
                                    form = "AM";
                                }
                                Remark remark;
                                remarks = item;
                                myRemarks = remarks + " At " + selectedHour + ":" + selectedMinute + " " + form;
                                remark = commonDRSListItem.setRemark(myRemarks, toDoListViewModel.getEmployeeCode(), 0);
                                toDoListViewModel.addRemarks(remark);
                                updateRemarks(commonDRSListItem, remark, selectedHour + ":" + selectedMinute + " " + form);
                                drsListFragment.notifyDataSetChanged();
                            };
                            Calendar c = Calendar.getInstance();
                            final TimePickerDialog timePickerDialog = new TimePickerDialog(ToDoListActivity.this,R.style.Theme_AppCompat_Light_Dialog, timePickerListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) + 5, false);
                            timePickerDialog.show();
                            bottomSheetDialog.dismiss();
                            break;
                    }
                    drsListFragment.notifyDataSetChanged();
                }
            });
            bottomSheetDialog = new BottomSheetDialog(ToDoListActivity.this);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
            cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    private String getAWB(CommonDRSListItem commonDRSListItem) {
        try {
            String Type = commonDRSListItem.getType();
            if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                return String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getAwbNo());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                return String.valueOf(commonDRSListItem.getEdsResponse().getAwbNo());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                return String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getAwbNo());
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return null;
    }

    private String getPhoneNumber(CommonDRSListItem commonDRSListItem) {
        try {
            String Type = commonDRSListItem.getType();
            if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                return String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getConsigneeDetails().getMobile());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                return String.valueOf(commonDRSListItem.getEdsResponse().getConsigneeDetail().getMobile());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                return String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getConsigneeDetails().getMobile());
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return null;
    }


    private String getDRSID(CommonDRSListItem commonDRSListItem) {
        try {
            String Type = commonDRSListItem.getType();
            if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                return String.valueOf(commonDRSListItem.getDrsForwardTypeResponse().getDrsId());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                return String.valueOf(commonDRSListItem.getEdsResponse().getDrsNo());
            } else if (Type.equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                return String.valueOf(commonDRSListItem.getDrsReverseQCTypeResponse().getDrs());
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return null;
    }

    public boolean isValidTimeSelected(int selectedHour, int selectedMinute) {
        Calendar currentTime = Calendar.getInstance();
        Calendar selectedTime = Calendar.getInstance();
        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
        selectedTime.set(Calendar.MINUTE, selectedMinute);
        return currentTime.getTimeInMillis() < selectedTime.getTimeInMillis();
    }

    public boolean isValidToAddRemark(CommonDRSListItem commonDRSListItem) {
        try {
            if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.FWD)) {
                DRSForwardTypeResponse response = commonDRSListItem.getDrsForwardTypeResponse();
                if (response.getShipmentStatus() == 0) return true;
            } else if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RVP)) {
                DRSReverseQCTypeResponse response = commonDRSListItem.getDrsReverseQCTypeResponse();
                if (response.getShipmentStatus() == 0) {
                    return true;
                }
            } else if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.RTS)) {
                IRTSBaseInterface response = commonDRSListItem.getIRTSInterface();
                if (response.getDetails().getShipmentStatus() == 0) {
                    return true;
                }
            } else if (commonDRSListItem.getType().equalsIgnoreCase(GlobalConstant.ShipmentTypeConstants.EDS)) {
                EDSResponse response = commonDRSListItem.getEdsResponse();
                if (response.getShipmentStatus() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
        return false;
    }

    @Override
    public void mapListtoogle() {
        if (toDoListViewModel.getDataManager().getIsAdmEmp()) {
            if (isNetworkConnected()) {
                if (!commonDRSListItemList.isEmpty()) {
                    if (activityToDoListBinding.toggleButton.isChecked()) {
                        Constants.FRAGMENT_VISIBLE = Constants.FRAGMENT_MAP;
                        addMapFragment();
                    } else if (!activityToDoListBinding.toggleButton.isChecked()) {
                        Constants.FRAGMENT_VISIBLE = Constants.FRAGMENT_LIST;
                        addListFragment();
                    }
                }
            } else {
                MessageManager.showToast(ToDoListActivity.this, getString(R.string.no_network_error));
            }
        }
    }

    @Override
    public void addRemarks(int position) {
        showRemarkWindow(position);
    }

    @Override
    public CallbridgeConfiguration getCallbridgeconfiguration() {
        return callbridgeConfiguration;
    }

    @Override
    public String getDefaultPstn() {
        return toDoListViewModel.getDataManager().getPstnFormat();
    }

    @Override
    public void makeCallBridgeApiCall(String callKey, String awb, int drsId, String type) {
        try {
            toDoListViewModel.makeCallBridgeApiCall(callKey, awb, drsId, type);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void updateForwardCallAttempted(String awb) {
        try {
            toDoListViewModel.updateForwardCallAttempted(awb);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void updateRVPCallAttempted(String awb) {
        try {
            toDoListViewModel.updateRVPCallAttempted(awb);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void updateEDSCallAttempted(String awb) {
        try {
            toDoListViewModel.updateEDSCallAttempted(awb);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void updateRTSCallAttempted(String awb) {
        try {
            toDoListViewModel.updateRTSCallAttempted(awb);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void getCheckedShipmentlist(HashMap<String, Boolean> hashMap) {
        try {
            toDoListViewModel.getAllCheckedShipments(hashMap);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void getSearchCheckedShipmentlist(Boolean flag) {
        try {
            toDoListViewModel.getSearchCheckedShipmentlist(flag);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    class MapFilterRunnable implements Runnable {

        public void setCharSequence(String filterSTR) {
        }

        @Override
        public void run() {
            gMapFragment.updateList(drsListFragment.getFlterShipments());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (device.equalsIgnoreCase(Constants.NEWLAND) || device.equalsIgnoreCase(Constants.NEWLAND_90) || device.equalsIgnoreCase(Constants.NEWLAND_DROI)) {
                mScanMgr.stopScan();
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            toDoListViewModel.getAllCategoryAssignedCount();
            toDoListViewModel.getAllCategoryDeliveredCount();
            toDoListViewModel.getAllCategoryUnDeliveredCount();
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }

    @Override
    public void onExpandableData(ExpandableListView expListView, boolean f, List<String> header, LinkedHashMap<String, List<String>> childList, List<Boolean> option, LinkedHashMap<String, Boolean> childList_optional_flag) {
        in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.ExpandListAdapter listAdapter = new in.ecomexpress.sathi.ui.dummy.eds.eds_task_list.ExpandListAdapter(this, header, childList, option, childList_optional_flag);
        expListView.setAdapter(listAdapter);
    }

    @Override
    public void setResetSequenceVisible(Boolean isVisible) {
        if (isVisible) {
            activityToDoListBinding.resetSort.setVisibility(View.VISIBLE);
        } else {
            activityToDoListBinding.resetSort.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResetSequenceClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        builder.setCancelable(false);
        builder.setMessage(R.string.are_you_sure_to_reset_the_drs_sequence_to_default);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            toDoListViewModel.clearDRSSequenceFromTable();
            finish();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void setForwardRemark(Remark remark, ItemForwardListViewBinding mBinding) {
        if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
            mBinding.layoutRemarks.setVisibility(View.VISIBLE);
            mBinding.remarks.setText(remark.remark);
        } else {
            mBinding.layoutRemarks.setVisibility(View.GONE);
        }
    }

    @Override
    public void setEDSRemark(Remark remark, ItemEdsListViewBinding mBinding) {
        if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
            mBinding.layoutRemarks.setVisibility(View.VISIBLE);
            mBinding.remarks.setText(remark.remark);
        } else {
            mBinding.layoutRemarks.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRVPRemark(Remark remark, ItemRvpListViewBinding mBinding) {
        if (remark != null && remark.remark != null && !remark.remark.isEmpty()) {
            mBinding.layoutRemarks.setVisibility(View.VISIBLE);
            mBinding.remarks.setText(remark.remark);
        } else {
            mBinding.layoutRemarks.setVisibility(View.GONE);
        }
    }

    @Override
    public void UpdateDRSAdapter() {
        runOnUiThread(() -> drsListFragment.notifyDataSetChanged());
    }

    @Override
    public Activity getActivityReference() {
        return ToDoListActivity.this;
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void updateRemarks(CommonDRSListItem commonDRSListItem, Remark updated_remark, String reschedule_time) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("awb", getAWB(commonDRSListItem));
            jsonObject.put("drs_id", getDRSID(commonDRSListItem));
            jsonObject.put("fe_code", toDoListViewModel.getDataManager().getEmp_code());
            jsonObject.put("fe_remarks", updated_remark.remark);
            jsonObject.put("reschedule_time", reschedule_time);
            jsonObject.put("date", getDate(System.currentTimeMillis(), "dd/MM/yyyy kk:mm:ss.SSS"));
            jsonObject.put("lat", String.valueOf(toDoListViewModel.getDataManager().getCurrentLatitude()));
            jsonObject.put("lng", String.valueOf(toDoListViewModel.getDataManager().getCurrentLongitude()));
            String logs = jsonObject.toString();
            List<LiveTrackingLogTable> liveTrackingLogs = new ArrayList<>();
            LiveTrackingLogTable liveTrackingLog = new LiveTrackingLogTable(toDoListViewModel.getDataManager().getEmp_code(), "Remark", logs, null);
            liveTrackingLogs.add(liveTrackingLog);
            toDoListViewModel.updateLogs(liveTrackingLogs);
        } catch (Exception e) {
            Logger.e(TAG, String.valueOf(e));
        }
    }
}