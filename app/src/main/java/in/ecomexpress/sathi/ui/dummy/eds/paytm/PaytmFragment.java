package in.ecomexpress.sathi.ui.dummy.eds.paytm;

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
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentPaytmBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.InstallAPK;
import in.ecomexpress.sathi.utils.PreferenceUtils;

import static android.content.Context.CLIPBOARD_SERVICE;
import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

@AndroidEntryPoint
public class PaytmFragment extends BaseFragment<FragmentPaytmBinding, PaytmViewModel> implements IPaytmFragmentNavigator, ActivityData {
    //ImageView imageView;
    public static final int progressType = 0;
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    FragmentPaytmBinding fragmentPaytmBinding;
    EdsWithActivityList edsWithActivityList;
    long mobile_no;
    @Inject
    PaytmViewModel paytmViewModel;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    String bank_navigation;
    private ProgressDialog pd;
    boolean is_already_kyced = false;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            try{
                if(intent.hasExtra("ORDERID")){
                    String orderid = intent.getStringExtra("ORDERID");
                    if(intent.getStringExtra("ORDERID").equalsIgnoreCase(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())){
                        if(orderid.contains(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())){
                            fragmentPaytmBinding.radioLayout.setVisibility(View.VISIBLE);
                            fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.GONE);
                        } else{
                            fragmentPaytmBinding.radioLayout.setVisibility(View.GONE);
                            fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
                showError(e.getMessage());
            }
        }
    };

    public static PaytmFragment newInstance(){
        PaytmFragment fragment = new PaytmFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.fragment_paytm;
    }

    @Override
    public PaytmViewModel getViewModel(){
        return paytmViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        paytmViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentPaytmBinding = getViewDataBinding();
        // fragmentPaytmBinding.txtScan.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        if(getArguments() != null){
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            paytmViewModel.setData(edsActivityWizard, masterActivityData);
            paytmViewModel.getmobile_no(edsActivityWizard.getAwbNo());
            fragmentPaytmBinding.paytmConnectBtn.setText(edsActivityWizard.getQuestionFormFields().getNeoAppName());
            try{
                if(!masterActivityData.getInstructions().isEmpty()){
                    fragmentPaytmBinding.instructionDetailTv.setClickable(true);
                    fragmentPaytmBinding.instructionDetailTv.setMovementMethod(LinkMovementMethod.getInstance());
                    setTextViewHTML(fragmentPaytmBinding.instructionDetailTv, masterActivityData.getInstructions());
                }
                if(!edsActivityWizard.getCustomerRemarks().isEmpty()){
                    fragmentPaytmBinding.remarkDetailTv.setText(edsActivityWizard.getCustomerRemarks());
                }
            } catch(Exception e){
                e.printStackTrace();
                showError(e.getMessage());
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // Toast.makeText(getActivity(), edsDetailActivity.getSca, Toast.LENGTH_LONG).show();
        try{
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("PAYTM_ACTION"));
            if(edsActivityWizard.getQuestionFormFields().getNeoAppUrl() != null){
                bank_navigation = PreferenceUtils.getSharedPreferences(getActivity()).getString(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo(), "123ABC");
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Paste", edsActivityWizard.getActualValue());
                clipboard.setPrimaryClip(clip);
                if(bank_navigation.contains(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())){
                    // if (bank_navigation.contains("123")) {
                    try{
                        JSONObject jobj = new JSONObject(bank_navigation);
                        fragmentPaytmBinding.radioLayout.setVisibility(View.VISIBLE);
                        fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.GONE);
                        if(jobj.getString("STATUS").equalsIgnoreCase("success")){
                            fragmentPaytmBinding.radioKycTrue.setEnabled(true);
                            fragmentPaytmBinding.radioKycFalse.setEnabled(false);
                            fragmentPaytmBinding.radioKycTrue.setChecked(true);
                        } else if(jobj.getString("STATUS").equalsIgnoreCase("ALREADY_KYCED")){
                            is_already_kyced = true;
                            fragmentPaytmBinding.radioKycTrue.setEnabled(false);
                            fragmentPaytmBinding.radioKycFalse.setEnabled(true);
                            fragmentPaytmBinding.radioKycFalse.setChecked(true);
                        } else{
                            fragmentPaytmBinding.radioKycTrue.setEnabled(false);
                            fragmentPaytmBinding.radioKycFalse.setEnabled(true);
                            fragmentPaytmBinding.radioKycFalse.setChecked(true);
                        }
                    } catch(JSONException j){
                        j.printStackTrace();
                    }
                } else{
                    fragmentPaytmBinding.radioLayout.setVisibility(View.GONE);
                    fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.VISIBLE);
                }
            }
        } catch(Exception e){
            getBaseActivity().showSnackbar("Vodafone onResume():-" + e.getLocalizedMessage());
            fragmentPaytmBinding.radioLayout.setVisibility(View.GONE);
            fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.GONE);
            fragmentPaytmBinding.paytmConnectLayout.setVisibility(View.GONE);
        }
    }

    public void showMessage(String s){
        Log.d(TAG, "showMessage: " + s);
    }

    @Override
    public void getData(BaseFragment fragment){
        try{
            boolean activityStatus = false;
            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if(fragmentPaytmBinding.radioKycTrue.isChecked()){
                activityStatus = true;
                edsActivityResponseWizard.setInputRemark("");
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());
            }
            edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch(Exception e){
            e.printStackTrace();
            getBaseActivity().showSnackbar("Vodafone getData():-" + e.getLocalizedMessage());
        }
    }

    @Override
    public boolean validateData(){
        try{
            if(is_already_kyced){
                getBaseActivity().showSnackbar("This shipment is already kyced, Please proceed to cancel and kindly select the reason (Activity already done as per Addressee).");
                return false;
            } else if(fragmentPaytmBinding.paytmConnectLayout.getVisibility() == View.VISIBLE){
                getBaseActivity().showSnackbar("Cannot Proceed to next without completing e-KYC");
                return false;
            } else if(fragmentPaytmBinding.radioKycFalse.isChecked()){
                getBaseActivity().showSnackbar("Cannot Proceed to next because your e-KYC got Failed..");
                return false;
            } else if(fragmentPaytmBinding.radioLayout.getVisibility() == View.GONE && fragmentPaytmBinding.paytmConnectLayout.getVisibility() == View.GONE){// && fragmentPaytmBinding.layoutScan.getVisibility() == View.GONE) {
                getBaseActivity().showSnackbar("Cannot Proceed to next because Something went wrong.Contact Admin..");
                return false;
            } else
                return true;
        } catch(Exception e){
            getBaseActivity().showSnackbar("Vodafone validateData():-" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void validate(boolean flag){
    }

    @Override
    public boolean validateCancelData(){
        try{
            if(fragmentPaytmBinding.radioKycTrue.isChecked() || Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")){
                getBaseActivity().showSnackbar("Cannot Cancel because e-Kyc is Successful.");
                return false;
            } else if(fragmentPaytmBinding.paytmConnectLayout.getVisibility() == View.VISIBLE){
                return true;
            } else if(fragmentPaytmBinding.radioKycFalse.isChecked()){
                return true;
            } else{
                return true;
            }
        } catch(Exception e){
            getBaseActivity().showSnackbar("Paytm xonResume():-" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setImageValidation(){
    }

    @Override
    public EDSActivityWizard getActivityWizard(){
        return null;
    }

    @Override
    public void onVodaConnect(){
        try{
            Log.e("mobile_no" , mobile_no+"");
            if(String.valueOf(mobile_no).length() == 10){
                if(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName() != null){
                    // PreferenceUtils.writePreferenceValue(LastMile.getInstance().getContext(), "bank", awb);
                    paytmViewModel.setOrderNo(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo());
                    boolean isAppInstalled = appInstalledOrNot(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName().trim());
                    if(isAppInstalled){
                        Uri myAction = Uri.parse("bcapp://ajrsplash?MSISDN=" + String.valueOf(mobile_no) + "&AWB_NUMBER=" + edsWithActivityList.edsResponse.getAwbNo().toString() + "&ORDERID=" + edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo() + "&PACKAGE_NAME=" + getContext().getPackageName());
                        Intent LaunchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName().trim());
                        LaunchIntent.setData(myAction);
                        startActivity(LaunchIntent);
                    } else{
                        if(isOnline()){
                            checkForUpdate();
                        } else{
                            showMessage("No Internet Connection");
                        }
                    }
                } else{
                    //showMessage("Please Contact Server Admin.URL not provided by Admin.");
                    Toast.makeText(getActivity(), "Please Contact Server Admin.URL not provided by Admin.", Toast.LENGTH_LONG).show();
                }
            } else{
                showError("Mobile no length is less than 10");
            }
        } catch(Exception e){
            e.printStackTrace();
            getBaseActivity().showSnackbar("Paytm vodaconnect():-" + e.getLocalizedMessage());
        }
    }

    @Override
    public void showError(String e){
        getBaseActivity().showSnackbar(e);
    }

    @Override
    public void getMobile(EncryptContactResponse encryptContactResponse){
        if(encryptContactResponse.isStatus()){
            mobile_no = encryptContactResponse.getResponse().getConsignee_mobile_number();
        }
    }

    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    public void checkForUpdate(){
        try{
            Intent i;
            PackageManager manager = getActivity().getPackageManager();
            try{
                i = manager.getLaunchIntentForPackage(edsActivityWizard.getQuestionFormFields().getNeoAppPackageName().trim());
                if(i == null)
                    throw new PackageManager.NameNotFoundException();
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            } catch(PackageManager.NameNotFoundException e){
                pd = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog);
                InstallAPK downloadAndInstall = new InstallAPK();
                //  pd = new ProgressDialog(context);
                // UpdateAPKInstaller downloadAndInstall = new UpdateAPKInstaller();
                pd.setCancelable(false);
                pd.setMessage("Downloading APK File");
                pd.setMax(100);
                pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downloadAndInstall.setContext(getActivity(), pd);
                downloadAndInstall.execute(edsActivityWizard.getQuestionFormFields().getNeoAppUrl().trim());
            }
        } catch(Exception e){
            e.printStackTrace();
            getBaseActivity().showSnackbar("Paytm CheckUpdate():-" + e.getLocalizedMessage());
        }
    }

    private boolean appInstalledOrNot(String uri){
        PackageManager pm = getActivity().getPackageManager();
        try{
            pm.getPackageInfo(uri.trim(), PackageManager.GET_ACTIVITIES);
            return true;
        } catch(PackageManager.NameNotFoundException e){
        }
        return false;
    }

    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}