package in.ecomexpress.sathi.ui.dummy.eds.icici_ekyc;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentIciciEkycBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.IciciResponse.IciciResponse;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.PopUpClass;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@AndroidEntryPoint
public class IciciEkycFragment extends BaseFragment<FragmentIciciEkycBinding, IciciViewModel> implements IIciciFragmentNavigator, ActivityData {
    FragmentIciciEkycBinding fragmentIciciEkycBinding;
    private final int REQUEST_CODE_SCAN = 1101;
    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    JSONObject pidDataJson;
    @Inject
    IciciViewModel iciciViewModel;
    PopUpClass popUpClass;

    //JSONObject pidDataJson;
    public static IciciEkycFragment newInstance(){
        IciciEkycFragment fragment = new IciciEkycFragment();
        return fragment;
    }

    FragmentManager fragmentManager;
    //   FragmentComunicator fragmentCommunicator;
    IciciResponse Response;

    @Override
    public IciciViewModel getViewModel(){
        return iciciViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        {
            return R.layout.fragment_icici_ekyc;
        }
    }

    @Override
    public void ongetPid(){
        ///fragmentIciciEkycBinding.iciciLayout.setVisibility(View.VISIBLE);
        if(fragmentIciciEkycBinding.radioKycFalse.isEnabled()){
            EDSDetailActivity.edsDetailActivity.scanMantra();
        } else{
            getBaseActivity().showSnackbar(" Ekyc Already Compelete Go for Next");
        }
    }

    public void scanMantra(){
        Intent intent = new Intent();
        intent.setAction("in.gov.uidai.rdservice.fp.INFO");
        startActivityForResult(intent, 1);
    }

    @Override
    public void validateUrn(){
        if(fragmentIciciEkycBinding.radioKycTrue.getText().toString().equalsIgnoreCase(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())){
            getBaseActivity().showSnackbar("URN Match Successfuly");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            //            fragmentIciciEkycBinding.llUrn.setEnabled(false);
            fragmentIciciEkycBinding.layoutUrn.setVisibility(View.VISIBLE);
            fragmentIciciEkycBinding.radioKycTrue.setEnabled(false);
            fragmentIciciEkycBinding.btnScan.setVisibility(View.GONE);
            fragmentIciciEkycBinding.radioKycFalse.setVisibility(View.VISIBLE);
        } else{
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            getBaseActivity().showSnackbar("Invalid URN Number");
        }
    }

    @Override
    public void sendPidDetail(String name){
    }

    @Override
    public EDSActivityWizard getActivityWizard(){
        return edsActivityWizard;
    }

    @Override
    public void sendICICIResponse(IciciResponse iciciResponse){
        EDSDetailActivity.edsDetailActivity.runOnUiThread(new Runnable() {
            @Override
            public void run(){
                Response = iciciResponse;
                // boolean  is =true;
                if(Response.isSuccess()){
                    popUpClass.showPopupWindow(getActivity());
                    fragmentIciciEkycBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                    fragmentIciciEkycBinding.radioKycFalse.setEnabled(false);
                } else{
                    fragmentIciciEkycBinding.iciciErrorResponse.setVisibility(View.VISIBLE);
                    fragmentIciciEkycBinding.iciciErrorText.setText(Response.getStatusCode() + " " + Response.getMessage());
                    popUpClass.closepopup(getActivity());
                }

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        iciciViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentIciciEkycBinding = getViewDataBinding();
        if(getArguments() != null){
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            iciciViewModel.setData(edsActivityWizard, masterActivityData);
            //fragmentIciciEkycBinding.iciciLayout.setVisibility(View.GONE);
            // String strtext = getArguments().getString("edttext");
            //Log.e("String", strtext);
        }
    }

    @Override
    public void sendData(String name){
        Log.d("DataValue", name);
        try{
            // JsonObject jobj =new JsonObject();
            pidDataJson = new JSONObject(name);
            JSONObject jobj_piddata = pidDataJson.getJSONObject("PidData");
            JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
            if(!jobj_resp.getString("errCode").equalsIgnoreCase("0")){
            } else{
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), getpacket().toString().replaceAll("\\\\", ""));
                iciciViewModel.checkStatus(edsActivityWizard.questionFormFields.getIciciBkycHeader(), edsActivityWizard.questionFormFields.getIciciBkycUrl(), body);//"prbi9bH34EkyjaGxaRwklK", "https://imobilepreprod.icicibank.com/ICICIDigital/EcomServiceController", body);
                popUpClass = new PopUpClass();
                popUpClass.showPopupWindow(getActivity());
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        fragmentIciciEkycBinding = getViewDataBinding();
        if(getArguments() != null){
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            iciciViewModel.setData(edsActivityWizard, masterActivityData);
        }
    }

    public JSONObject getpacket(){
        JSONObject json = new JSONObject();
        try{
            return json.put("request_data", new JSONObject().put("lat", iciciViewModel.getDataManager().getCurrentLatitude()).put("lng", iciciViewModel.getDataManager().getCurrentLatitude()).put("PidData", pidDataJson.get("PidData"))).put("awb", edsActivityWizard.awbNo).put("order_number", edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo()).put("requesttype", "ekyc");
        } catch(JSONException e){
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public void getData(BaseFragment fragment){
        boolean activityStatus = false;
        edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
        edsActivityResponseWizard.setInput_value("false");
        edsActivityResponseWizard.setInputRemark("");
        edsActivityResponseWizard.setIsDone("false");
        edsActivityResponseWizard.setActivityId("0");
        //if (fragmentIciciEkycBinding.radioKycTrue.isSelected() && !Constants.SCANNED_DATA.equalsIgnoreCase("Not Found")) {
        if(Response.isSuccess()){
            activityStatus = true;
            edsActivityResponseWizard.setInput_value(Constants.SCANNED_DATA);
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("true");
            edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
            edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());
        }
        EDSDetailActivity.edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
    }

    @Override
    public boolean validateData(){
        if(Response != null){
            if(Response.isSuccess()){
                return true;
            } else
                getBaseActivity().showSnackbar("Cannot Proceed to next without completing e-KYC");
            return false;
        }
        getBaseActivity().showSnackbar("Cannot Proceed to next without completing e-KYC");
        return false;
    }

    @Override
    public void validate(boolean flag){
    }

    @Override
    public boolean validateCancelData(){
        if(Response != null){
            if(!Response.isSuccess()){
                return true;
            } else
                getBaseActivity().showSnackbar("Cannot Cancel because e-Kyc is Successful.");
            return false;
        }
        getBaseActivity().showSnackbar("Cannot Proceed to next without completing e-KYC");
        return true;
    }

    @Override
    public void setImageValidation(){
    }

    public void makeCallDialog(){
        // custom dialog
        try{
            Dialog dialog;
            dialog = new Dialog(getBaseActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.eds_ekyc_succes_dialog);
            //dialog.setTitle("Title...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            // set the custom dialog components - text, image and button
            TextView name = dialog.findViewById(R.id.name);
            // name.setText("Name : " + forwardCommit.getConsignee_name());
            TextView awb = dialog.findViewById(R.id.awb);
            // awb.setText("AWB : " + forwardCommit.getAwb());
            TextView dialogButton = dialog.findViewById(R.id.btn_scan);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    try{
                        //makeCallonClick();
                        if(validateData()){
                            return;
                        }
                        // getData();
                        getBaseActivity().showSnackbar("Cannot Cancel because e-Kyc is Successful.");
                        dialog.dismiss();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void showMessage(String s){
        Log.d(TAG, "showMessage: " + s);
    }
}