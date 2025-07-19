package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_rbl;//package in.ecomexpress.sathi.ui.eds.eds_ekyc_rbl;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.FragmentRblBinding;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;

import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycXMLViewModel.readStream;

/**
 * Created by santosh on 17/1/20.
 */

@AndroidEntryPoint
public class EdsRblFragment extends BaseFragment<FragmentRblBinding, EdsRblViewModel> implements IEdsRblFragmentNavigator, ActivityData {
    JSONObject pidDataJson;
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    EdsWithActivityList edsWithActivityList;
    FragmentRblBinding fragmentRblBinding;
    @Inject
    EdsRblViewModel edsRblViewModel;
    JSONObject rblJsonObject = null;
    int kyc_trial_time = 0;
    boolean isKycCompleted = false;

    public static EdsRblFragment newInstance() {
        EdsRblFragment fragment = new EdsRblFragment();
        return fragment;
    }

    @Override
    public EdsRblViewModel getViewModel() {
        return edsRblViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edsRblViewModel.setNavigator(this);
        edsRblViewModel.setData(edsActivityWizard, masterActivityData);

    }
    @Override
    public void onResume(){

        super.onResume();
        fragmentRblBinding = getViewDataBinding();
        if (getArguments() != null) {
            this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
            this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
            this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
            edsRblViewModel.setData(edsActivityWizard, masterActivityData);

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentRblBinding = getViewDataBinding();
        try {
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                if(edsWithActivityList.edsActivityWizards.get(0).code.equalsIgnoreCase("AC_RBL_BKYC"))
                EDSDetailActivity.edsDetailActivity.hideFooter();
                edsRblViewModel.setData(edsActivityWizard, masterActivityData);
                String q = edsActivityWizard.getQuestion_form_dummy();
                try {
                    System.out.println(q);
                    rblJsonObject = new JSONObject(edsActivityWizard.question_form_dummy);
                    //Log.e("rbl object" , jObject+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "OnCreateView():-Data not synced Properly.Please sync and try Again..", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rbl;
    }

    @Override
    public void getData(BaseFragment fragment) {

    }

    @Override
    public boolean validateData() {
        return isKycCompleted;
    }

    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {
        return false;
    }

    @Override
    public void setImageValidation() {

    }
   // 261968


    @Override
    public void ongetPid() {
//            edsRblViewModel.getDataManager().getMaxEDSFailAttempt();
//            edsRblViewModel.getDataManager().setEdsActivityCodes(null);
//            Set<String> all_edsactivity_codes = new HashSet<>();
//            all_edsactivity_codes.add("AC_FAIL");
//            edsRblViewModel.getDataManager().setEdsActivityCodes(all_edsactivity_codes);
//            edsDetailActivity.cancelRbLScreen();

        EDSDetailActivity.edsDetailActivity.scanMantra();
    }

    @Override
    public void sendData(String pidData) {
        try {
            pidDataJson = new JSONObject(pidData);
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    Log.e("packet", getpacket().toString());
                    String retStr = "";
                    try {
                        retStr = getResponseFromJsonURL1(getActivity(), rblJsonObject.getString("ekyc_url").trim(),
                                    getpacket().toString().replaceAll("\\\\", ""), rblJsonObject.getString("client_id"),
                                rblJsonObject.getString("client_secret"), rblJsonObject.getString("LDAP_username"),
                                rblJsonObject.getString("LDAP_password"), "", "ecom@123");
        //      retStr="{\"ekycbidetailsecomres\":{\"status\":\"0\",\"responsecode\":\"1156\",\"responsemessage\":\"Freshness factor mismatch\"}}";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return retStr;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    Log.e("rbl response", s + "");
                    JSONObject jobj = null;
                    String description = "";
                    try {
                        jobj = new JSONObject(s);
                        JSONObject jsonObject = null;
                        if (jobj.has("errorres")) {
                            jsonObject = jobj.getJSONObject("errorres");
                            description = jsonObject.getString("description");
                        } else if (jobj.has("ekycbidetailsecomres")) {
                            jsonObject = jobj.getJSONObject("ekycbidetailsecomres");
                            description = jsonObject.getString("responsemessage");
                        }

                        String status = jsonObject.getString("status");
                        //String responsemessage = jsonObject.getString("description");
                        if (status.equalsIgnoreCase("1")) {
                            fragmentRblBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciSuccessText.setText(description);
                            isKycCompleted = true;
                            if(edsWithActivityList.edsActivityWizards.get(0).code.equalsIgnoreCase("AC_RBL_BKYC"))
                                EDSDetailActivity.edsDetailActivity.callSignatureScreen();

                               // edsDetailActivity.hideFooter();
                        } else if (status.equalsIgnoreCase("0")) {
                            fragmentRblBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciErrorText.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciErrorText.setText(description);
                            kyc_trial_time = kyc_trial_time + 1;
                            if (kyc_trial_time > edsRblViewModel.getDataManager().getMaxEDSFailAttempt()) {
                                edsRblViewModel.getDataManager().setEdsActivityCodes(null);
                                Set<String> all_edsactivity_codes = new HashSet<>();
                                all_edsactivity_codes.add("AC_FAIL");
                                edsRblViewModel.getDataManager().setEdsActivityCodes(all_edsactivity_codes);
                                EDSDetailActivity.edsDetailActivity.cancelRbLScreen();
                            } else {
                                Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
                            }
                            //isKycCompleted = true;
                        } else {
                            fragmentRblBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciErrorText.setVisibility(View.VISIBLE);
                            fragmentRblBinding.iciciErrorText.setText("Server Error ");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        kyc_trial_time = kyc_trial_time + 1;
                        if (kyc_trial_time > edsRblViewModel.getDataManager().getMaxEDSFailAttempt()) {
                            edsRblViewModel.getDataManager().setEdsActivityCodes(null);
                            Set<String> all_edsactivity_codes = new HashSet<>();
                            all_edsactivity_codes.add("AC_FAIL");
                            edsRblViewModel.getDataManager().setEdsActivityCodes(all_edsactivity_codes);
                            EDSDetailActivity.edsDetailActivity.cancelRbLScreen();
                        } else {
                            //Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
                        }
                        getBaseActivity().showSnackbar("PostExecute():-Data not synced properly.Please sync data and try again..");
                    }
                }

            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public JSONObject getpacket() {
        JSONObject user = new JSONObject();
        JSONObject json = new JSONObject();
        //   pidDataJson = new JSONObject(xmlToJson.toFormattedString());
        //  ekyc_url,user_name,id,key,consenttext,transtype,channel,ekyctype,authversiontouse,client_secret,client_id,password,user_name;
        try {
            JSONObject jobj_piddata = pidDataJson.getJSONObject("PidData");
            JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
            json.put("id", rblJsonObject.getString("id"));
            json.put("key", rblJsonObject.getString("key"));
            json.put("awbno", EDSDetailActivity.edsDetailActivity.getAwbNo());
            json.put("agentcode", edsRblViewModel.getDataManager().getEmp_code());
            json.put("agentname", edsRblViewModel.getDataManager().getName());
            json.put("agentmobile", edsRblViewModel.getDataManager().getMobile());
            json.put("ekycuniquerefno", EDSDetailActivity.edsDetailActivity.getAwbNo() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            json.put("mobileno", "1111111111");
            json.put("name", edsWithActivityList.edsResponse.getConsigneeDetail().getName());
            json.put("consenttext", rblJsonObject.getString("consenttext"));
            json.put("transtype", rblJsonObject.getString("transtype"));
            json.put("channel", rblJsonObject.getString("channel"));
            json.put("rbluniquerefno", EDSDetailActivity.edsDetailActivity.getOrderId());
            json.put("ekyctype", rblJsonObject.getString("ekyctype"));
            json.put("encryptedpid", pidDataJson.getJSONObject("PidData").getJSONObject("Data").getString("content").replaceAll("\\n", "").trim());
            json.put("encryptedhmac", pidDataJson.getJSONObject("PidData").getString("Hmac").replaceAll("\\n", "").trim());
            json.put("sessionkeyvalue", pidDataJson.getJSONObject("PidData").getJSONObject("Skey").getString("content").replaceAll("\\n", "").trim());
            json.put("certificateidentifier", pidDataJson.getJSONObject("PidData").getJSONObject("Skey").getString("ci"));
            json.put("authversiontouse", rblJsonObject.getString("authversiontouse"));
            json.put("registereddeviceserviceid", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("rdsId"));
            json.put("registereddeviceserviceversion", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("rdsVer"));
            json.put("registereddeviceproviderid", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("dpId"));
            json.put("registereddevicecode", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("dc"));
            json.put("registereddevicemodelid", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("mi"));
            json.put("registereddevicepublickey", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getString("mc"));
            json.put("terminalid", pidDataJson.getJSONObject("PidData").getJSONObject("DeviceInfo").getJSONObject("additional_info").getJSONArray("Param").getJSONObject(1).getString("value"));
            json.put("version", rblJsonObject.getString("version"));
            json.put("typeofdevicemanufacture", rblJsonObject.getString("typeofdevicemanufacture"));
            user.put("ekycbidetailsecomreq", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }

    public String getResponseFromJsonURL1(Context mContext, String url, String postData, String client_id, String clientpass, String username, String password, String certificate_url, String certificate_pass) {
        String server_response = null;
        InputStream targetStream = null;
        DefaultHttpClient httpClient = null;
        //if (CommonUtility.isNotEmpty(url)) {
        try {
            /************** For getting response from HTTP URL start ***************/
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            final Resources resources = mContext.getResources();
            // InputStream targetStream = resources.openRawResource(R.raw.ecomexpressin);


            URL cert_url = new URL(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_SERVER_CERT) + "?cert_name=ecomPfx");
            HttpURLConnection con = (HttpURLConnection) cert_url.openConnection();
            con.setRequestProperty("api_key", in.ecomexpress.sathi.BuildConfig.API_KEY);
            con.setRequestProperty("app_ver", in.ecomexpress.sathi.BuildConfig.VERSION_NAME);
            con.setRequestProperty("auth_token", edsRblViewModel.getDataManager().getAuthToken());
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("api_ver", "v2");

            con.setDoOutput(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //targetStream = new ByteArrayInputStream(data.getBytes());
            String input;
            JSONObject jsonObject = null;
            while ((input = br.readLine()) != null) {
                Log.e("certificate response", input + "");
                jsonObject = new JSONObject(input);
            }
            br.close();
            boolean status = jsonObject.getBoolean("status");
            //ByteArrayInputStream targetStream = null;
            if (status) {
                String cert_file = jsonObject.getString("cert_file");
                byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(cert_file.getBytes());
                targetStream = new ByteArrayInputStream(decoded);
            }

            //  InputStream in = cert_url.openStream();
            Log.e("Stream", targetStream + "");
            keyStore.load(targetStream, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagers, null, null);
//            byte[] decoded = Base64.decodeBase64(certificate_pass.getBytes());
            String authString = username + ":" + password;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("client_id", client_id);
            ub.addParameter("client_secret", clientpass);
            String urlparam = ub.toString();

            URL object = new URL(urlparam);
            // URL object = new URL("https://apideveloper.rblbank.com/test/sb/rbl/v1/biometric/kyc");
            HttpsURLConnection connection = (HttpsURLConnection) object.openConnection();
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(connection.getInputStream());
                Log.e("rbl1", server_response);
            } else {
                server_response = connection.getResponseMessage();
                Log.e("rbl2", server_response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return server_response;
        }
        return server_response;

    }

}
