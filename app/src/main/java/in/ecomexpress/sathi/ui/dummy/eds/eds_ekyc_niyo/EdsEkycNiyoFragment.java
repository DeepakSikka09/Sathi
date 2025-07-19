package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_niyo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.FragmentEkycNiyoBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@AndroidEntryPoint
public class EdsEkycNiyoFragment extends BaseFragment<FragmentEkycNiyoBinding, EdsEkycNiyoViewModel> implements IEdsEkycNiyoFragmentNavigator, ActivityData {
    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    FragmentEkycNiyoBinding fragmentEdsEkycNiyoBinding;
    ProgressDialog p;
    @Inject
    EdsEkycNiyoViewModel edsEkycNiyoViewModel;
    String xml_pid;
    JSONObject pidDataJson;
    private Persister serializer = null;
    private ArrayList<String> positions;
    String token = null;
    long mobile_no;
    JSONObject idfccust_infoobj;
    String tspVdrId, agntId, udc, trmnlId, caLocn, caId, extRefId, id, ekyc_url;
    boolean isKycCompleted = false;
    ProgressDialog dialog;
    // RequestBody body;

    public static EdsEkycNiyoFragment newInstance(){
        EdsEkycNiyoFragment fragment = new EdsEkycNiyoFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        edsEkycNiyoViewModel.setNavigator(this);
        //Log.d(TAG, "onCreate: " + this.toString());
        positions = new ArrayList<>();
        serializer = new Persister();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentEdsEkycNiyoBinding = getViewDataBinding();
        //        fragmentEdsEkycIdfcBinding.editUrn.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        //        fragmentEdsEkycIdfcBinding.inputAdhar.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        try{
            if(getArguments() != null){
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                edsEkycNiyoViewModel.setData(edsActivityWizard, masterActivityData);
                //getpacket();
                // Log.d("pidjson",getpacket().toString());
                Gson gson = new Gson();
                String jsonInString = gson.toJson(edsActivityWizard);
                ObjectMapper oMapper = new ObjectMapper();
                //QuestionFormField q=new QuestionFormField();
                String q = edsActivityWizard.getQuestion_form_dummy();
                //            Log.d("santosh", q);
                // ObjectMapper Obj = new ObjectMapper();
                //
                try{
                    // Displaying JSON String
                    System.out.println(q);
                    // Log.d("JSON String", "jsonStr");
                    JSONObject idfccust_infoobj = new JSONObject(q);
                    tspVdrId = idfccust_infoobj.getString("tspVdrId");
                    agntId = idfccust_infoobj.getString("agntId");
                    udc = idfccust_infoobj.getString("udc");
                    trmnlId = idfccust_infoobj.getString("trmnlId");
                    caLocn = idfccust_infoobj.getString("caLocn");
                    caId = idfccust_infoobj.getString("caId");
                    //  extRefId=idfccust_infoobj.getString("extRefId");
                    id = idfccust_infoobj.getString("id");
                    ekyc_url = idfccust_infoobj.getString("url");
                    //                    {
                    //                        "tspVdrId": "13",
                    //                            "agntId": "14018885",
                    //                            "udc": "1",
                    //                            "trmnlId": "47458076",
                    //                            "caLocn": "nationalpark MUMBAI SUBURBMHIN",
                    //                            "caId": "IDF130047458076",
                    //                            "id": "ecom",
                    //                            "url": "https:\/\/api.idfcfirstbank.com\/ecom\/getGenericbiometricAPIAuthentication"
                    //                    }
                    //                    {
                    //                        "tspVdrId": "13",
                    //                            "agntId": "17000085",
                    //                            "udc": "1",
                    //                            "trmnlId": "20937555        ",
                    //                            "caLocn": "Delhi",
                    //                            "caId": "IDF090020937555",
                    //                            "extRefId": "ucic",
                    //                            "id": "ecom",
                    //                            "url": "https:\/\/api.idfcfirstbank.com\/ecom\/getGenericbiometricAPIAuthentication"
                    //                    }
                } catch(Exception e){
                    e.printStackTrace();
                    //  getBaseActivity().showSnackbar("Ekyc Customer Info field missing some element");
                }
            }
        } catch(Exception e){
            getBaseActivity().showSnackbar("Master Data Sync Error");
        }
    }

    @Override
    public void ongetPid(){
        // EDSDetailActivity.edsDetailActivity.scanMantra();
        if(!isKycCompleted)
            edsEkycNiyoViewModel.getmobile_no(edsActivityWizard.getAwbNo());
            //  EDSDetailActivity.edsDetailActivity.scanMantra();
            //  getMobile();
            //  sendData();
            // edsEkycNiyoViewModel.checkStatus();
        else{
            getBaseActivity().showSuccessInfo("Kyc Already Completed ");
        }
    }

    @Override
    public void sendData(String pidData){
        try{
            String ss = pidData.replace("\\n", "");
            //      String ss1 = ss.replace("\\", "");
            pidDataJson = new JSONObject(pidData);
            String ss2 = getpacket(pidDataJson).toString().replace("\\n", "");
            String ss3 = ss2.replace("\\", "");
            String ss4 = ss3.trim();
            JSONObject request_payload = new JSONObject(ss4);
            Log.d("PPIINIYO", ss4);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), request_payload.toString().replaceAll("\\\\", ""));
            new BackgroundTask().execute(body);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    //            new AsyncTask<Void, Void, Response>() {
    //
    //                @Override
    //                protected Response doInBackground(Void... voids) {
    //
    //                    try {
    //                        SSLContext sslContext = createCertificate(getActivity().getResources().openRawResource(R.raw.cerificate1));
    //
    //                        String url = "https://ecom.fnpaas.com/global-sundry/v1/ecom/validate_ekyc";
    //                        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().sslSocketFactory(sslContext.getSocketFactory()).addInterceptor(new PayloadIdfcEncryptNetworkInterceptor1()).build();
    //                        //    OkHttpClient client =       new OkHttpClient().newBuilder().sslSocketFactory(sslContext.getSocketFactory()).addInterceptor(new PayloadIdfcEncryptNetworkInterceptor1()).build();
    //                        //initSSL(context);
    //                        // String url = "https://ecom.fnpaas.com/global-sundry/v1/ecom/validate_ekyc";
    //                        Request okHttpRequest = new Request.Builder()
    //                                .url(url).post(body)
    //                                .build();
    //
    //                        Response response = null;
    //
    //                        response = okHttpClient.newCall(okHttpRequest).execute();
    //                        return response;
    //
    //                    } catch (Exception e)
    //                    {
    //                        e.printStackTrace();
    //                    }
    //                       // return null;
    //                   // }
    //                    return null;
    //                }
    //
    //                @Override
    //                protected void onPostExecute(Response s) {
    //                    super.onPostExecute(s);
    //                  //  try {
    //
    ////                        String    ss11 = s.body().string();
    ////                    Log.d("NIYO",ss11);
    ////
    ////
    ////                    if (s.isSuccessful()) {
    ////
    ////                    }
    ////                        else {
    ////
    ////                      //  String ss = null;
    ////
    ////                        String    ss = s.body().string();
    ////
    ////                        fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setText(ss);
    ////
    ////
    ////                    }
    ////                    } catch (IOException e) {
    ////                        e.printStackTrace();
    ////                    }
    ////
    ////                            Response.Builder newResponse = response.newBuilder();
    ////                            String contentType = response.header("Content-Type");
    ////                            if (TextUtils.isEmpty(contentType)) contentType = "application/json";
    ////                        }else {
    ////                            fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setText("error");
    ////
    ////                        }
    ////                    fragmentEdsEkycNiyoBinding.iciciSuccessText.setVisibility(View.VISIBLE);
    ////                  //  fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.GONE);
    ////                    fragmentEdsEkycNiyoBinding.iciciSuccessText.setText(s);
    ////                    //  dialog.dismiss();
    ////                    //getBaseActivity().showSnackbar(s);
    ////                    fragmentEdsEkycNiyoBinding.iciciSuccessText.setVisibility(View.VISIBLE);
    ////                    fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.GONE);
    ////                    fragmentEdsEkycNiyoBinding.iciciSuccessText.setText(s);
    ////                    Log.d("iiddffcc", s);
    ////                    if (s == null) {
    ////                        //
    ////
    ////                        try {
    ////
    ////                            JSONObject response = new JSONObject(s);
    ////                            //       {"code":200,"message":"Biometric Validation Successful"}
    ////                            //      String ss= response.getJSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getString("rslt");
    ////                            int ss = response.getInt("code");
    ////                            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    ////                            //JSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getString("rslt");
    ////                            //response.getJSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getJSONArray("error").getJSONObject(0).getString("rsn");
    ////                            //   if (ss.equalsIgnoreCase("ERROR")) {
    //////                        if (ss.equalsIgnoreCase("ERROR")) {
    //////                            jsonObject = jobj.getJSONObject("errorres");
    //////                            description = jsonObject.getString("description");
    //////                        } else if (jobj.has("ekycbidetailsecomres")) {
    //////                            jsonObject = jobj.getJSONObject(H"ekycbidetailsecomres");
    //////                            description = jsonObject.getString("responsemessage");
    //////                        }
    ////
    ////                            // String status = jsonObject.getString("status");
    ////                            //String responsemessage = jsonObject.getString("description");
    ////                            if (ss == 200) {
    ////
    ////                                isKycCompleted = true;
    ////                                fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciSuccessText.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.GONE);
    ////                                fragmentEdsEkycNiyoBinding.iciciSuccessText.setText(" Success ");
    ////                                // fragmentEdsEkycIdfcBinding.btnScan.setEnabled(false);
    //////                   );
    //////                            isKycCompleted = true;
    ////                            }
    //////                            else if (ss.equalsIgnoreCase("ERROR")) {
    //////                                fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
    //////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
    //////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setText(response.getJSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getJSONArray("error").getJSONObject(0).getString("rsn"));
    //////
    ////////                            kyc_trial_time = kyc_trial_time + 1;
    ////////                            if (kyc_trial_time > 4) {
    ////////                                fragmentRblBinding.iciciErrorText.setText("Your maximum limit has been reached, please CANCEL.");
    ////////                                Toast.makeText(getContext(), "Your maximum limit has been reached, please CANCEL.", Toast.LENGTH_SHORT).show();
    ////////                            } else {
    ////////                                Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
    ////////                            }
    //////                                //isKycCompleted = true;
    //////                            }
    ////                            else {
    ////                                fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
    ////                                fragmentEdsEkycNiyoBinding.iciciErrorText.setText("Server Error ");
    ////                            }
    ////
    ////                        } catch (Exception e) {
    ////                            getBaseActivity().showSnackbar("Server Error");
    ////                        }
    ////                    } else {
    ////                        getBaseActivity().showSnackbar("Server Error");
    //                    }
    //            //  }
    ////
    ////
    //
    //    }

    private SSLContext createCertificate(){
        SSLContext sslContext = null;
        InputStream targetStream = null;
        try{
            URL cert_url = new URL(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_SERVER_CERT) + "?cert_name=ecomPfx");
            HttpURLConnection con = (HttpURLConnection) cert_url.openConnection();
            con.setRequestProperty("api_key", in.ecomexpress.sathi.BuildConfig.API_KEY);
            con.setRequestProperty("app_ver", in.ecomexpress.sathi.BuildConfig.VERSION_NAME);
            con.setRequestProperty("auth_token", edsEkycNiyoViewModel.getDataManager().getAuthToken());
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("api_ver", "v2");

            con.setDoOutput(false);
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //targetStream = new ByteArrayInputStream(data.getBytes());
            String input;
            JSONObject jsonObject = null;
            while((input = br.readLine()) != null){
                Log.e("certificate response", input + "");
                jsonObject = new JSONObject(input);
            }
            br.close();
            boolean status = jsonObject.getBoolean("status");
            //ByteArrayInputStream targetStream = null;
            if(status){
                String cert_file = jsonObject.getString("cert_file");
                byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(cert_file.getBytes());
                targetStream = new ByteArrayInputStream(decoded);
            }
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(targetStream, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());
            return sslContext;
            //return sslContext;
        } catch(UnrecoverableKeyException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch(CertificateException e){
            e.printStackTrace();
        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch(KeyStoreException e){
            e.printStackTrace();
        } catch(KeyManagementException e){
            e.printStackTrace();
        } catch(JSONException e){
            e.printStackTrace();
        }
        return sslContext;
    }

    @Override
    public void getToken(GenerateTokenNiyo iciciResponse){
        //        User user = new User();
        //        user.setName("john");
        //  Response res = edsEkycNiyoViewModel.uploadBiometricData(body, getActivity(), iciciResponse);
    }

    @Override
    public void getMobile(EncryptContactResponse encryptContactResponse){
        if(encryptContactResponse.isStatus()){
            EDSDetailActivity.edsDetailActivity.scanMantra();
            //
            //   EDSDetailActivity.edsDetailActivity.scanMantra();
            mobile_no = encryptContactResponse.getResponse().getConsignee_mobile_number();
        } else{
            getBaseActivity().showSnackbar("Consignee Contact decryption Error ..");
        }
        // mobile_no=encryptContactResponse.getResponse().getConsignee_mobile_number();
    }
    // @Override
    //  public void getMobile() {
    //
    //        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new PayloadIdfcEncryptNetworkInterceptor()).build();
    //        String url = "https://ecom.fnpaas.com/global-sundry/v1/ecom/validate_ekyc";
    //        Request okHttpRequest = new Request.Builder().url(url).cacheControl(CacheControl.FORCE_NETWORK).build();
    //        Response response = null;
    //        try {
    //            response = okHttpClient.newCall(okHttpRequest).execute();
    //            //   System.out.println("Response code: " + response.response.code());
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        }
    //checking the response code
    // System.out.println("Response code: " + response.response.code());
    //        if (encryptContactResponse.isStatus()) {
    //            EDSDetailActivity.edsDetailActivity.scanMantra();
    ////
    //            mobile_no = encryptContactResponse.getResponse().getConsignee_mobile_number();
    //
    //        } else {
    //            getBaseActivity().showSnackbar("Consignee Contact decryption Error ..");
    //
    //        }
    // mobile_no=encryptContactResponse.getResponse().getConsignee_mobile_number();
    // }
    //}
    //idfc1234

    @Override
    public void getData(BaseFragment fragment){
    }

    @Override
    public boolean validateData(){
        return isKycCompleted;
    }

    @Override
    public void validate(boolean flag){
    }

    @Override
    public boolean validateCancelData(){
        return !isKycCompleted;
    }

    @Override
    public void setImageValidation(){
    }

    public void showMessage(String s){
        Log.d("TAG", "showMessage: " + s);
    }

    @Override
    public EDSActivityWizard getActivityWizard(){
        return null;
    }

    @Override
    public EdsEkycNiyoViewModel getViewModel(){
        return edsEkycNiyoViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.fragment_ekyc_niyo;
    }

    public JSONObject getpacket(JSONObject Pid){
        JSONObject json = new JSONObject();
        //  SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
        try{
            // edsEkycIdfcViewModel.checkStatus(edsWithActivityList.edsResponse.getAwbNo());
            String originalPassword = "password";
            // 2020 02 41 042832{  "enquireGenericCBSAadharBKYCRes":{    "msgHdr":{      "rslt":"ERROR",      "error":[        {          "cd":"ER0091",          "rsn":"Invalid Response",          "srvc":{            "nm":"EKYCAdapter",            "cntxt":"EKYCAdapter",            "actn":{              "paradigm":"Reply",              "nm":"BiometricEKYCAuthentication",              "vrsn":"01"            }          }        }      ]    },    "msgBdy":{      "svcHdr":{        "respCd":null      }    }  }}
            String pattern = "yyyyMMddHHmmss";
            String pattern1 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
            String pattern2 = "YYYYDDDHH";
            // String id = String.format("%04d", random.nextInt(10000));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
            String date = simpleDateFormat.format(new Date());
            String timestamp = simpleDateFormat1.format(new Date());
            String rrn2 = simpleDateFormat2.format(new Date());
            String rrn1 = rrn2.substring(3);
            System.out.println(date);
            Random rand = new Random();
            String id = String.format("%04d", rand.nextInt(10000));
            String generatedSecuredPasswordHash = null;
            try{
                generatedSecuredPasswordHash = generateStorngPasswordHash(rrn1, date);
            } catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            } catch(InvalidKeySpecException e){
                e.printStackTrace();
            }
            //     {
            //                        "tspVdrId": "13",
            //                            "agntId": "14018885",
            //                            "udc": "1",
            //                            "trmnlId": "47458076",
            //                            "caLocn": "nationalpark MUMBAI SUBURBMHIN",
            //                            "caId": "IDF130047458076",
            //                            "id": "ecom",
            //                            "url": "https:\/\/api.idfcfirstbank.com\/ecom\/getGenericbiometricAPIAuthentication"
            //                    }
            //004414208313            System.out.println(generatedSecuredPasswordHash);
            Log.d("generated hash", generatedSecuredPasswordHash);
            String jobj = new JSONObject(Pid.getString("PidData")).getString("DeviceInfo");
            return json.put("enquireGenericCBSAadharBKYCReq", new JSONObject().put("msgHdr", new JSONObject().
                    put("frm", new JSONObject().put("id", "ecom")).put("hdrFlds", new JSONObject().put("cnvId", date).put("msgId", date).put("extRefId", "ucic").put("bizObjId", date).put("timestamp", timestamp))).put("msgBdy", new JSONObject().put("mobNum", "91" + mobile_no).put("svcHdr", new JSONObject().put("txnId", rrn1 + "20" + id)
                    //        .put("tspVdrId", "20"))
                    .put("tspVdrId", tspVdrId)).put("txnInfo", new JSONObject().put("txnDtTm", date).put("rrn", rrn1 + "20" + id).put("trmnlId", trmnlId)
                    //.put("trmnlId", "11262432        ")
                    .put("caId", caId).put("caId", caId).put("caLocn", caLocn)
                    //.put("agntId", "13320399")
                    .put("agntId", agntId)
                    // .put("txnHmac", generatedSecuredPasswordHash)
                    .put("unqueNo", "20" + id)).put("authInfo", new JSONObject().put("data", new JSONObject().put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content")).put("type", "01")).put("fmt", new JSONObject().put("type", "P").put("ftype",Constants.TEMP_FYPE_NIYO).put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content"))).put("meta", new JSONObject().put("udc", "1").put("rdsId", new JSONObject(jobj).getString("rdsId")).put("rdsVer", new JSONObject(jobj).getString("rdsVer")).put("dpId", new JSONObject(jobj).getString("dpId")).put("dc", new JSONObject(jobj).getString("dc")).put("mi", new JSONObject(jobj).getString("mi")).put("mc", new JSONObject(jobj).getString("mc")).put("bav", "").put("_content_", "")).put("secKey", new JSONObject().put("ci", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("ci")).put("ki", "").put("_content_", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("content"))).put("hmac", new JSONObject(Pid.getString("PidData")).getString("Hmac")))).put("agentDetails", new JSONObject().put("orderNumber", edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo()).put("location", new JSONObject().put("lat", String.valueOf(edsEkycNiyoViewModel.getDataManager().getDCLatitude())).put("lng", String.valueOf(edsEkycNiyoViewModel.getDataManager().getCurrentLongitude())).put("city", edsWithActivityList.edsResponse.getConsigneeDetail().getAddress().getCity())).put("timeStamp", "").put("id", getViewModel().getDataManager().getCode())));
        } catch(JSONException e){
            e.printStackTrace();
        }
        return json;
    }

    private static String generateStorngPasswordHash(String rrn, String salt1) throws NoSuchAlgorithmException, InvalidKeySpecException{
        int iterations = 1000;
        char[] chars = rrn.toCharArray();
        byte[] salt = getSalt(salt1);
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt(String ss) throws NoSuchAlgorithmException{
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = ss.getBytes();
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException{
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0){
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else{
            return hex;
        }
    }

    public class BackgroundTask extends AsyncTask<RequestBody, Void, Response> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            p = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog);
            p.setMessage("Please wait...Verifying");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Response doInBackground(RequestBody... requestbody){
            Response response = null;
            try{
                SSLContext sslContext = createCertificate();
                RequestBody body = requestbody[0];
                long time = System.currentTimeMillis();
                //String url = "https://ecom.fnpaas.com/global-sundry/v1/ecom/validate_ekyc";
                OkHttpClient client = new OkHttpClient().newBuilder().sslSocketFactory(sslContext.getSocketFactory()).connectTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).build();
                Request request = new Request.Builder().url(ekyc_url).method("POST", body).addHeader("x-correlation-id", "ecom_" + time).addHeader("x-api-key", "w7pd2xy7WwRdh381Lssm3StDhrU8DlBf").addHeader("Content-Type", "application/json").build();
                response = client.newCall(request).execute();
            } catch(IOException e){
                e.printStackTrace();
                p.dismiss();
            }
            return response;
            // return Response;
        }

        @Override
        protected void onPostExecute(Response response){
            try{
                p.dismiss();
                super.onPostExecute(response);
                if(response != null){
                    if(response.isSuccessful() && response.code() == 200){
                        isKycCompleted = true;
                        //                {
                        //                    "code": 200,
                        //                        "message": "Biometric Validation Successful"
                        //                }
                        // String ss1 =response.body().string();
                        //                    Log.d("SSS!",ss1);
                        String ss = response.body().string();
                        JSONObject json = new JSONObject(ss);
                        fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                        fragmentEdsEkycNiyoBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                        fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.GONE);
                        fragmentEdsEkycNiyoBinding.iciciSuccessText.setText(response.code() + " : " + json.getString("message"));
                        // fragmentEdsEkycIdfcBinding.btnScan.setEnabled(false);
                    } else if(!response.isSuccessful()){
                        //    {"message":"Internal Server Error"}
                        //                    String ss = response.body().toString();
                        //    JSONObject post = new JSONObject().getJSONObject(response.body().string());
                        // String ss = post.getString("message");
                        String ss = response.body().string();
                        JSONObject json = new JSONObject(ss);
                        //Log.d("resfaol",ss.toString());
                        // JSONObject messagobj= new JSONObject(json.getString("message"));
                        //  String  msg=messagobj.getString("message");
                        fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                        fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
                        fragmentEdsEkycNiyoBinding.iciciErrorText.setText(response.code() + " : " + json.getString("message"));
                    }
                } else{
                    fragmentEdsEkycNiyoBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                    fragmentEdsEkycNiyoBinding.iciciErrorText.setVisibility(View.VISIBLE);
                    fragmentEdsEkycNiyoBinding.iciciErrorText.setText("Server Error Contact to adnim");
                }
            }
            //
            //            catch (JSONException e) {
            //                e.printStackTrace();
            //            }
            catch(IOException | JSONException e){
                e.printStackTrace();
            }
        }
    }
}