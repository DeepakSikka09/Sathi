package in.ecomexpress.sathi.ui.dummy.eds.vodafone;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentVodafoneBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.InstallAPK;
import in.ecomexpress.sathi.utils.PreferenceUtils;

import static android.content.Context.CLIPBOARD_SERVICE;
import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

@AndroidEntryPoint
public class VodafoneFragment extends BaseFragment<FragmentVodafoneBinding, VodafoneViewModel> implements IVodafoneFragmentNavigator, ActivityData {

    FragmentVodafoneBinding fragmentVodafoneBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    VodafoneViewModel vodafoneViewModel;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    String bank_navigation;
    private ProgressDialog pd;
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
            String action = intent.getAction();
            Toast.makeText(getActivity(), "recieved data", Toast.LENGTH_LONG).show();
            if (intent.hasExtra("ORDERID")) {
                String orderid = intent.getStringExtra("ORDERID");

                if (intent.getStringExtra("ORDERID").equalsIgnoreCase(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())) {
                    if (orderid.contains(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())) {
                        fragmentVodafoneBinding.radioLayout.setVisibility(View.VISIBLE);
                        fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.GONE);
                    } else {
                        fragmentVodafoneBinding.radioLayout.setVisibility(View.GONE);
                        fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
            }catch (Exception e){
                e.printStackTrace();
                showError(e.getMessage());
            }
        }
    };

    public static VodafoneFragment newInstance() {
        VodafoneFragment fragment = new VodafoneFragment();
        return fragment;
    }


    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vodafone;
    }

    @Override
    public VodafoneViewModel getViewModel() {
        return vodafoneViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vodafoneViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentVodafoneBinding = getViewDataBinding();
        fragmentVodafoneBinding.txtScan.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        if (getArguments() != null) {
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            vodafoneViewModel.setData(edsActivityWizard, masterActivityData);
            try {
                if (!masterActivityData.getInstructions().isEmpty()) {
                    fragmentVodafoneBinding.instructionDetailTv.setClickable(true);
                    fragmentVodafoneBinding.instructionDetailTv.setMovementMethod(LinkMovementMethod.getInstance());
                    setTextViewHTML(fragmentVodafoneBinding.instructionDetailTv, masterActivityData.getInstructions());
                }
                if (!edsActivityWizard.getCustomerRemarks().isEmpty()) {
                    fragmentVodafoneBinding.remarkDetailTv.setText(edsActivityWizard.getCustomerRemarks());
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError(e.getMessage());
            }


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // Toast.makeText(getActivity(), edsDetailActivity.getSca, Toast.LENGTH_LONG).show();
        try {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                    mMessageReceiver, new IntentFilter("VODAFONE_ACTION"));
            if (edsActivityWizard.getQuestionFormFields().getNeoAppUrl() != null) {
                bank_navigation = PreferenceUtils.getSharedPreferences(getActivity()).getString(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo(), "abc");
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Paste", edsActivityWizard.getActualValue());
                clipboard.setPrimaryClip(clip);
                if (bank_navigation.contains(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())) {
                    try {
                        JSONObject jobj = new JSONObject(bank_navigation);
                        fragmentVodafoneBinding.radioLayout.setVisibility(View.VISIBLE);
                        fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.GONE);
                        if (jobj.getString("STATUS").equalsIgnoreCase("success")) {
                            fragmentVodafoneBinding.radioKycTrue.setEnabled(true);
                            fragmentVodafoneBinding.radioKycFalse.setEnabled(false);
                            fragmentVodafoneBinding.radioKycTrue.setChecked(true);
                            fragmentVodafoneBinding.layoutScan.setVisibility(View.VISIBLE);
                            if (Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")) {
                                fragmentVodafoneBinding.txtScan.setHint("Scan product Id");
                            } else
                                fragmentVodafoneBinding.txtScan.setText(Constants.SCANNED_DATA);
                            // Toast.makeText(getActivity(), Constants.SCANNED_DATA, Toast.LENGTH_LONG).show();
                        } else {
                            fragmentVodafoneBinding.radioKycTrue.setEnabled(false);
                            fragmentVodafoneBinding.radioKycFalse.setEnabled(true);
                            fragmentVodafoneBinding.radioKycFalse.setChecked(true);
                            fragmentVodafoneBinding.layoutScan.setVisibility(View.GONE);

                        }
                    } catch (JSONException j) {
                        j.printStackTrace();
                    }
                } else {
                    fragmentVodafoneBinding.radioLayout.setVisibility(View.GONE);
                    fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.VISIBLE);
                }
            /*
            if (!Helper.isPackageInstalled(Constants.VODA_CONNECT_URL, activity)) {
                showMantrainsatll(Constants.VODA_CONNECT_URL, "vodafone ekyc client ..");
            }*/
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar("Vodafone onResume():-" + e.getLocalizedMessage());
            fragmentVodafoneBinding.radioLayout.setVisibility(View.GONE);
            fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.GONE);
            fragmentVodafoneBinding.vodafoneConnectLayout.setVisibility(View.GONE);
            fragmentVodafoneBinding.layoutScan.setVisibility(View.GONE);
            //  getBaseActivity().finish();
        }

    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    @Override
    public void getData(BaseFragment fragment) {
        try {
            boolean activityStatus = false;

            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (fragmentVodafoneBinding.radioKycTrue.isSelected() && !Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value(Constants.SCANNED_DATA);
                edsActivityResponseWizard.setInputRemark("");
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar("Vodafone getData():-" + e.getLocalizedMessage());
        }

    }

    @Override
    public boolean validateData() {
        try {
            if (fragmentVodafoneBinding.vodafoneConnectLayout.getVisibility() == View.VISIBLE) {
                getBaseActivity().showSnackbar("Cannot Proceed to next without completing e-KYC");
                return false;
            } else if (fragmentVodafoneBinding.radioKycFalse.isChecked()) {
                getBaseActivity().showSnackbar("Cannot Proceed to next because your e-KYC got Failed..");
                return false;
            } else if (fragmentVodafoneBinding.radioKycTrue.isChecked() && Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")) {
                getBaseActivity().showSnackbar("Cannot Proceed to next because you didnot scanned..");
                return false;
            } else if (fragmentVodafoneBinding.radioLayout.getVisibility() == View.GONE
                    && fragmentVodafoneBinding.vodafoneConnectLayout.getVisibility() == View.GONE
                    && fragmentVodafoneBinding.layoutScan.getVisibility() == View.GONE) {
                getBaseActivity().showSnackbar("Cannot Proceed to next because Something went wrong.Contact Admin..");
                return false;
            } else
                return true;
        } catch (Exception e) {
            getBaseActivity().showSnackbar("Vodafone validateData():-" + e.getLocalizedMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {
        try {
            if (fragmentVodafoneBinding.vodafoneConnectLayout.getVisibility() == View.VISIBLE) {

                return true;
            } else if (fragmentVodafoneBinding.radioKycFalse.isChecked()) {

                return true;
            } else if (fragmentVodafoneBinding.radioKycTrue.isChecked() || Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")) {
                getBaseActivity().showSnackbar("Cannot Cancel because e-Kyc is Successful.");
                return false;
            } else
                return true;
        } catch (Exception e) {
            getBaseActivity().showSnackbar("Vodafone onResume():-" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setImageValidation() {

    }


    @Override
    public EDSActivityWizard getActivityWizard() {
        return null;
    }

    @Override
    public void onVodaConnect() {
        try {
            if (edsActivityWizard.getQuestionFormFields().getNeoAppPackageName() != null) {
                // PreferenceUtils.writePreferenceValue(LastMile.getInstance().getContext(), "bank", awb);
                vodafoneViewModel.setOrderNo(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo());
                boolean isAppInstalled = appInstalledOrNot(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName().trim());

                if (isAppInstalled) {
                    Intent LaunchIntent = getActivity().getPackageManager()
                            .getLaunchIntentForPackage(Constants.VODA_CONNECT_URL);
                    LaunchIntent.putExtra("LAUNCHEDFROM", "ECOM");
                    LaunchIntent.putExtra("ORDERID", edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo());
                    LaunchIntent.putExtra("AWB_NUMBER", String.valueOf(edsWithActivityList.edsResponse.getAwbNo()));
                    startActivity(LaunchIntent);

                } else {
                    if (isOnline()) {
                        checkForUpdate();
                    } else {
                        showMessage("No Internet Connection");
                    }
                }

            } else {

                //showMessage("Please Contact Server Admin.URL not provided by Admin.");
                Toast.makeText(getActivity(), "Please Contact Server Admin.URL not provided by Admin.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar("Vodafone vodaconnect():-" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onscan() {
        edsDetailActivity.scanBarcode();
    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);

    }

    public void checkForUpdate() {
        try {
            pd = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog);
            Intent i;
            PackageManager manager = getActivity().getPackageManager();
            try {
                i = manager.getLaunchIntentForPackage(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName().trim());
                if (i == null)
                    throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                //SathiLogger.e(e.getMessage());
                InstallAPK downloadAndInstall = new InstallAPK();
                pd.setCancelable(false);
                pd.setMessage("Downloading APK File");
                pd.setMax(100);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

                downloadAndInstall.setContext(getActivity(), pd);
                downloadAndInstall.execute(edsActivityWizard.getQuestionFormFields().getNeoAppUrl().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar("Vodafone CheckUpdate():-" + e.getLocalizedMessage());
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri.trim(), PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

            // SathiLogger.e(e.getMessage());
        }
        return false;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
