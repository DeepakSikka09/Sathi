package in.ecomexpress.sathi.ui.dummy.eds.eds_task_list;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.ActivityEdsTaskListBinding;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.device_upload.Biometric_response;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.uid.DeviceInfo;
import in.ecomexpress.sathi.ui.dummy.eds.uid.Param;
import in.ecomexpress.sathi.ui.dummy.eds.undeilvered_eds.EDSUndeliveredActivity;
import in.ecomexpress.sathi.utils.Constants;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_EDS_MASTER_LIST;
import static in.ecomexpress.sathi.utils.Constants.INTENT_KEY_EDS_WITH_ACTIVITY;
import com.google.gson.Gson;

@AndroidEntryPoint
public class EdsTaskListActivity extends BaseActivity<ActivityEdsTaskListBinding, EdsTaskListActivityModel> implements IEdsTaskListNavigator {
    private static final String TAG = EdsTaskListActivity.class.getSimpleName();
    @Inject
    EdsTaskListActivityModel edsTaskListActivityModel;
    long awbNo;
    int drs_no;
    String order_id = "";
    String getDrsApiKey = null, getDrsPstnKey = null, getCbConfigCallType = null, Masterpstnformat = null, getDrsPin = null, composite_key = "";
    boolean flag = false, reschedule_enable, is_kyc_active;
    private ActivityEdsTaskListBinding activityEdsTaskListBinding;

    public static Intent getStartIntent(Context context){
        return new Intent(context, EdsTaskListActivity.class);
    }

    //  PID
    DeviceInfo info;
    EditText adhar_no_edt;
    String bio;
    String idc;
    String lat;
    String lng;
    String udc;
    String rdsId;
    String rdsVer;
    String dpId;
    String dc;
    String mi;
    String mc;
    List<String> value;
    JSONObject pidDataJson;
    private ArrayList<String> positions;
    private Serializer serializer = null;
    boolean call_allowed;
    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        try{
            activityEdsTaskListBinding = getViewDataBinding();
            if(getIntent().getStringExtra(Constants.ORDER_ID) != null){
                order_id = getIntent().getStringExtra(Constants.ORDER_ID);
            }
            awbNo = getIntent().getLongExtra(Constants.INTENT_KEY, 0);
            drs_no = getIntent().getIntExtra(Constants.DRS_ID, 0);
            activityEdsTaskListBinding.awb.setText(String.valueOf(awbNo));
            edsTaskListActivityModel.setNavigator(this);
            composite_key = getIntent().getStringExtra(Constants.COMPOSITE_KEY);
            edsTaskListActivityModel.getEdsListTask(composite_key);
            getDrsApiKey = getIntent().getStringExtra(Constants.DRS_API_KEY);
            getDrsPstnKey = getIntent().getStringExtra(Constants.DRS_PSTN_KEY);
            getDrsPin = getIntent().getStringExtra(Constants.DRS_PIN);
            call_allowed = getIntent().getBooleanExtra("call_allowed", false);
            is_kyc_active = getIntent().getBooleanExtra(Constants.IS_KYC_ACTIVE, false);
            reschedule_enable = getIntent().getBooleanExtra(Constants.RESCHEDULE_ENABLE, false);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.eds));
            }
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        positions = new ArrayList<>();
        serializer = new Persister();
        activityEdsTaskListBinding.addressTv.setMovementMethod(new ScrollingMovementMethod());
        activityEdsTaskListBinding.consigneeNameTv.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public EdsTaskListActivityModel getViewModel(){
        return edsTaskListActivityModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.activity_eds_task_list;
    }

    @Override
    public void onExpandableData(boolean f, List<String> header, LinkedHashMap<String, List<String>> childList, List<Boolean> option, LinkedHashMap<String, Boolean> childList_optional_flag){
        if(f){
            ExpandableListView expListView = findViewById(R.id.drawer);
            ExpandListAdapter listAdapter = new ExpandListAdapter(this, header, childList, option, childList_optional_flag);
            expListView.setAdapter(listAdapter);
            for(int i = 0; i < listAdapter.getGroupCount(); i++)
                expListView.expandGroup(i);
        } else{
            flag = true;
            Toast.makeText(getApplicationContext(), "Activity Code are not properly defined.Please contact Server Admin..", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProceed(){
        try{
            if(!flag){
                List<EDSActivityWizard> eds = new ArrayList<>();
                List<MasterActivityData> master = new ArrayList<>();
                int count = 0;
                EdsWithActivityList edsWithActivityList = edsTaskListActivityModel.edsWithActivityList();
                ArrayList<MasterActivityData> masterActivityData = edsTaskListActivityModel.getEdsMasterData();
                Collections.sort(masterActivityData);
                Collections.sort(edsWithActivityList.getEdsActivityWizards());
                try{
                    for(int i = 0; i < edsWithActivityList.getEdsActivityWizards().size(); i++){
                        if(edsWithActivityList.getEdsActivityWizards().get(i).getCode().startsWith("AC")){
                            eds.add(edsWithActivityList.getEdsActivityWizards().get(i));
                            master.add(masterActivityData.get(i));
                            count++;
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
                for(int j = 0; j < count; j++){
                    edsWithActivityList.getEdsActivityWizards().remove(0);
                    masterActivityData.remove(0);
                }
                for(int k = 0; k < count; k++){
                    edsWithActivityList.getEdsActivityWizards().add(eds.get(k));
                    masterActivityData.add(master.get(k));
                }
                if(is_kyc_active){
                    Intent intent = new Intent();
                    intent.setAction("in.gov.uidai.rdservice.fp.INFO");
                    startActivityForResult(intent, 1);
                } else{
                    Intent intent = new Intent(EdsTaskListActivity.this, EDSDetailActivity.class);
                    intent.putExtra(INTENT_KEY_EDS_WITH_ACTIVITY, gson.toJson(edsWithActivityList));
                    intent.putExtra(Constants.INTENT_KEY, awbNo);
                    intent.putExtra(Constants.DRS_ID, drs_no);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra("call_allowed" , call_allowed);
                    intent.putExtra(Constants.ORDER_ID, order_id);
                    intent.putParcelableArrayListExtra(INTENT_KEY_EDS_MASTER_LIST, masterActivityData);
                    startActivity(intent);
                }
            } else{
                Toast.makeText(getApplicationContext(), "You Cannot Proceed.Activity Code Defined is Wrong..Contact Server Admin.", Toast.LENGTH_SHORT).show();
            }

        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(EdsTaskListActivity.this, e.getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
        }
        //throw new NullPointerException();
    }

    @Override
    public void onCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert);
        String AlertText1 = "Attention : ";
        builder.setMessage(AlertText1 + getString(R.string.cancel_alert)).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                if(!flag){
                    Intent intent;
                    intent = new Intent(EdsTaskListActivity.this, EDSUndeliveredActivity.class);
                    intent.putParcelableArrayListExtra(getString(R.string.data), null);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra("call_allowed" , call_allowed);
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra("edsResponse", edsTaskListActivityModel.edsWithActivityList().edsResponse);
                    intent.putExtra("awb", awbNo);
                    intent.putExtra("navigator", "act_list");
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(), "You Cannot Cancel,Activity Code Defined is Wrong.Contact Server Admin..", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public String loadStringFromAssets(Context mActivity, String fileName){
        String string = null;
        if(mActivity != null){
            try{
                InputStream is = mActivity.getResources().getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                string = new String(buffer, StandardCharsets.UTF_8);
            } catch(Exception ex){
                //SathiLogger.e(ex.getMessage());
                return null;
            }
        }
        return string;
    }

    public static PublicKey getPublicKey(byte[] base64PublicKey){
        try{
            KeyFactory kf = KeyFactory.getInstance("RSA");
            // RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(base64PublicKey));
            //  RSAPrivateKey prtKey=(RSAPrivateKey)kf.generatePrivate(new X509EncodedKeySpec(base64PublicKey));
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(base64PublicKey);
            PrivateKey pvtkey = kf.generatePrivate(privKeySpec);
            return null;
            //return publicKey;
            // return myPublicKey;
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(InvalidKeySpecException ine){
            ine.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBack(){
        super.onBackPressed();
    }

    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void showMsg(){
        flag = true;
        Toast.makeText(getApplicationContext(), R.string.no_activity_assigned, Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendBiometricResponse(Biometric_response biometric_response){
        // EdsTaskListActivity edsTaskListActivity;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run(){
                if(biometric_response.isStatus()){
                    EdsWithActivityList edsWithActivityList = edsTaskListActivityModel.edsWithActivityList();
                    ArrayList<MasterActivityData> masterActivityData = edsTaskListActivityModel.getEdsMasterData();
                    Intent intent = new Intent(EdsTaskListActivity.this, EDSDetailActivity.class);
                    intent.putExtra(INTENT_KEY_EDS_WITH_ACTIVITY, gson.toJson(edsWithActivityList));
                    intent.putExtra(Constants.INTENT_KEY, awbNo);
                    intent.putExtra(Constants.DRS_ID, drs_no);
                    intent.putExtra(Constants.DRS_PSTN_KEY, getDrsPstnKey);
                    intent.putExtra(Constants.DRS_API_KEY, getDrsApiKey);
                    intent.putExtra(Constants.COMPOSITE_KEY, composite_key);
                    intent.putExtra(Constants.ORDER_ID, order_id);
                    intent.putExtra(Constants.RESCHEDULE_ENABLE, reschedule_enable);
                    intent.putExtra(Constants.DRS_PIN, getDrsPin);
                    intent.putParcelableArrayListExtra(INTENT_KEY_EDS_MASTER_LIST, masterActivityData);
                    startActivity(intent);
                } else{
                    showError("BioMetric device validation failed");
                }
            }
        });
    }

    @Override
    public void showError(String e){
        showSnackbar(e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == 1){
                if(resultCode == Activity.RESULT_OK){
                    try{
                        if(data != null){
                            value = new ArrayList<>();
                            String result = data.getStringExtra("DEVICE_INFO");
                            String rdService = data.getStringExtra("RD_SERVICE_INFO");
                            String display = "";
                            if(rdService != null){
                                display = "RD Service Info :\n" + rdService + "\n\n";
                            }
                            if(result != null){
                                info = serializer.read(DeviceInfo.class, result);
                                rdsId = info.rdsId;
                                rdsId = info.rdsId;
                                dpId = info.dpId;
                                dc = info.dc;
                                mi = info.mi;
                                mc = info.mc;
                                rdsVer = info.mc;
                                List<Param> data1 = info.add_info.params;
                                for(int i = 0; i < data1.size(); i++){
                                    value.add(data1.get(i).value);
                                }
                                idc = value.get(0);
                            }
                            if(mi.isEmpty()){
                                showSnackbar("Connect Biometric Device");
                            } else{
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                String date_time = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                edsTaskListActivityModel.verifyDevice(value.get(0), mi, dpId, date_time, edsTaskListActivityModel.getDataManager().getCode());
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        Log.e("Error", "Error while deserialze device info", e);
                    }
                }
            } else if(requestCode == 2){
                if(resultCode == Activity.RESULT_OK){
                    try{
                        pidDataJson = new JSONObject();
                        if(data != null){
                            String result_resp = data.getStringExtra("PID_DATA");
                            XmlToJson xmlToJson = new XmlToJson.Builder(result_resp).build();
                            //    Log.e("json", xmlToJson.toFormattedString());
                            pidDataJson = new JSONObject(xmlToJson.toFormattedString());
                            //                                fragment = edsDetailPagerAdapter.getItem(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            //                                // EdsEkycXMLFragment edsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            //
                            //                                if (fragment instanceof EdsEkycXMLFragment) {
                            //                                    EdsEkycXMLFragment edsEkycXMLFragment = (EdsEkycXMLFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            //                                    edsEkycXMLFragment.sendData(result_resp);
                            //                                }
                            //                                if (fragment instanceof IciciEkycFragment) {
                            //                                    IciciEkycFragment iciciEkycFragment = (IciciEkycFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            //                                    iciciEkycFragment.sendData(pidDataJson.toString());
                            //                                }
                            //                                try {
                            ////                                JSONObject jobj_piddata = pidDataJson.getJSONObject("PidData");
                            ////                                JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
                            ////
                            ////                                // Fragment f=new VodafoneFragment();
                            //////                            IciciEkycFragment iciciEkycFragment = (IciciEkycFragment) edsDetailPagerAdapter.getRegisteredFragment(activityEdsDetailBinding.qcViewPager.getCurrentItem());
                            //////                            iciciEkycFragment.sendData(pidDataJson.toString());
                            ////                                //  fragmentCommunicator = (FragmentComunicator) f;
                            ////                                // fragmentCommunicator.sendData(pidDataJson.toString());
                            ////
                            ////                                //  fragmentManager.beginTransaction().replace(R.id.fragmentContainer, argumentFragment).commit();//now replace the argument fragment
                            ////                                if (!jobj_resp.getString("errCode").equalsIgnoreCase("0")) {
                            ////                                    showSnackbar("Error From Server");
                            ////                                    // Toast.makeText(getApplicationContext(), jobj_resp.getString("errInfo"), Toast.LENGTH_LONG).show();
                            ////                                    //   new Helper().showAlert(Icici_Ekyc.this, jobj_resp.getString("errInfo"), "Alert");
                            ////
                            ////                                } else {
                            ////                                    showSnackbar("Ekyc Successfull");
                            //////                                asyncTask_ekyc = new ICICICallTask();
                            //////                                asyncTask_ekyc.execute(Constants.ICICI_CONSTANT_DETAIL);
                            ////                                }
                            //                                } catch (Exception j) {
                            //                                    j.printStackTrace();
                            //                                }
                            //                            }
                            //                        } catch (Exception e) {
                            //                            Log.e("Error", "Error while deserialze pid data", e);
                            //                        }
                            //                    } else {
                            //                        imageHandler.onActivityResult(requestCode, resultCode, data);
                            //                    }
                            //                    super.onActivityResult(requestCode, resultCode, data);
                            //                } else {
                            //                    imageHandler.onActivityResult(requestCode, resultCode, data);
                            //                }
                            //            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        showSnackbar(e.getMessage());
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            showSnackbar(e.getMessage());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

       /* if(Constants.BACKTODOLIST)
        {
            Constants.BACKTODOLIST=false;
            finish();
        }*/
    }
}


