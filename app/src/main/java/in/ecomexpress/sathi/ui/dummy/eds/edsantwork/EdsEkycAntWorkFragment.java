package in.ecomexpress.sathi.ui.dummy.eds.edsantwork;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.client.utils.URIBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.FragmentEkycAntworkBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateTokenNiyo;
import in.ecomexpress.sathi.repo.remote.model.antwork.BioMatricResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.BiomatricRequest;
import in.ecomexpress.sathi.repo.remote.model.antwork.WadhResponse;
import in.ecomexpress.sathi.repo.remote.model.antwork.WathRequest;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML.EdsEkycXMLViewModel;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import okhttp3.Response;

@AndroidEntryPoint
public class EdsEkycAntWorkFragment extends BaseFragment<FragmentEkycAntworkBinding, EdsEkycAntWorkViewModel> implements IEdsEkycAntWorkFragmentNavigator, ActivityData {
    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    FragmentEkycAntworkBinding fragmentEkycAntworkBinding;
    ProgressDialog p;
    @Inject
    EdsEkycAntWorkViewModel edsEkycAntWorkViewModel;
    long mobile_no;
    String tspVdrId, agntId, udc, trmnlId, caLocn, caId, extRefId, id, ekyc_url;
    boolean isKycCompleted = false;
    String final_payload = "";
    //  String username = "ecomexpress";
    // String password = "rgmH3bjf3mnAs1SA";
    String username = "";
    String password = "";
    String url = "https://dev-api-account.freo.app/v1/ecom/biometric-data";
    String server_response = "";
    int responseCode = 0;
    String xml_piddata = "";

    public static EdsEkycAntWorkFragment newInstance(){
        EdsEkycAntWorkFragment fragment = new EdsEkycAntWorkFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        edsEkycAntWorkViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentEkycAntworkBinding = getViewDataBinding();
        try{
            if(getArguments() != null){
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                edsEkycAntWorkViewModel.setData(edsActivityWizard, masterActivityData);

            }
        } catch(Exception e){
            getBaseActivity().showSnackbar("Master Data Sync Error");
        }
    }

    @Override
    public void onActivateSensor(){
        if(!isKycCompleted)
            EDSDetailActivity.edsDetailActivity.scanMantra();
        else{
            getBaseActivity().showSuccessInfo("Kyc Already Completed ");
        }
    }

    @Override
    public void sendBiomatricData(WadhResponse wadhResponse){
        String encrypt_pid_data = android.util.Base64.encodeToString(xml_piddata.getBytes(), android.util.Base64.NO_WRAP);
        BiomatricRequest biomatricRequest = new BiomatricRequest();
        biomatricRequest.setOrder_number(wadhResponse.order_number);
        biomatricRequest.setFingerType("1~RightThumb");
        biomatricRequest.setDeviceType("2");
        biomatricRequest.setDeviceDataXml(encrypt_pid_data);
        String credentials = edsActivityWizard.getQuestionFormFields().getYes_bkyc_url_Username() + ":" + edsActivityWizard.getQuestionFormFields().getYes_bkyc_url_Password();
        final String authorizationBasic =
                "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
        edsEkycAntWorkViewModel.sendBiomaticData(authorizationBasic ,edsActivityWizard.getQuestionFormFields().getYes_bkyc_url(), biomatricRequest);
    }

    @Override
    public void bioMatricResult(BioMatricResponse bioMatricResponse){
        if(bioMatricResponse.status_code.equalsIgnoreCase("SUCCESS")){
            isKycCompleted = true;
            fragmentEkycAntworkBinding.fingureImgLayout.setVisibility(View.VISIBLE);
            fragmentEkycAntworkBinding.iciciSuccessText.setVisibility(View.VISIBLE);
            fragmentEkycAntworkBinding.iciciErrorText.setVisibility(View.GONE);
            fragmentEkycAntworkBinding.iciciSuccessText.setText(bioMatricResponse.message);
        } else{
            isKycCompleted = false;
            fragmentEkycAntworkBinding.fingureImgLayout.setVisibility(View.VISIBLE);
            fragmentEkycAntworkBinding.iciciSuccessText.setVisibility(View.GONE);
            fragmentEkycAntworkBinding.iciciErrorText.setVisibility(View.VISIBLE);
            fragmentEkycAntworkBinding.iciciErrorText.setText(bioMatricResponse.message);
        }
    }

    @Override
    public void sendData(String pidData, String awb_no, String order_id){
        try{
            this.xml_piddata = pidData;
            String credentials = edsActivityWizard.getQuestionFormFields().getYes_wadh_Api_Username() + ":" + edsActivityWizard.getQuestionFormFields().getYes_wadh_Api_Password();
            final String authorizationBasic =
                    "Basic " + android.util.Base64.encodeToString(credentials.getBytes(), android.util.Base64.NO_WRAP);
            WathRequest wathRequest = new WathRequest();
            wathRequest.setOrder_number(order_id);
            edsEkycAntWorkViewModel.getWadhValue(authorizationBasic ,edsActivityWizard.getQuestionFormFields().getYes_wadh_Api_url(), wathRequest);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String getDate(long milliSeconds, String dateFormat){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private SSLContext createCertificate(){
        SSLContext sslContext = null;
        InputStream targetStream = null;
        String cert_file = "";
        try{
            URL cert_url = new URL(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_SERVER_CERT) + "?cert_name=ecomPfx");
            HttpURLConnection con = (HttpURLConnection) cert_url.openConnection();
            con.setRequestProperty("api_key", in.ecomexpress.sathi.BuildConfig.API_KEY);
            con.setRequestProperty("app_ver", in.ecomexpress.sathi.BuildConfig.VERSION_NAME);
            con.setRequestProperty("auth_token", edsEkycAntWorkViewModel.getDataManager().getAuthToken());
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
                cert_file = jsonObject.getString("cert_file");
                byte[] decoded = Base64.decodeBase64(cert_file.getBytes());
                targetStream = new ByteArrayInputStream(decoded);
            }
            //  byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(certificate_data.getBytes());
            // targetStream = new ByteArrayInputStream(decoded);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(targetStream, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, new String((Constants.PFX_PW2.getBytes())).toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, null, new SecureRandom());
            // return sslContext;
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
        } catch(Exception e){
            e.printStackTrace();
        }
        return sslContext;
    }

    @Override
    public void getToken(GenerateTokenNiyo iciciResponse){
    }

    @Override
    public void getMobile(EncryptContactResponse encryptContactResponse){
        if(encryptContactResponse.isStatus()){
            EDSDetailActivity.edsDetailActivity.scanMantra();
            mobile_no = encryptContactResponse.getResponse().getConsignee_mobile_number();
        } else{
            getBaseActivity().showSnackbar("Consignee Contact decryption Error ..");
        }
    }

    @Override
    public void getData(BaseFragment fragment){
    }

    @Override
    public boolean validateData(){
        if(isKycCompleted)
        {
           return true;
        }
        else
        {
            showMessage("Please complete your kyc first.");
            return false;
        }

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
    public EdsEkycAntWorkViewModel getViewModel(){
        return edsEkycAntWorkViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.fragment_ekyc_antwork;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSONObject getpacket(JSONObject Pid){
        JSONObject json = new JSONObject();
        try{
            String originalPassword = "password";
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
                    .put("unqueNo", "20" + id)).put("authInfo", new JSONObject().put("data", new JSONObject().put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content")).put("type", "01")).put("fmt", new JSONObject().put("type", "P").put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content"))).put("meta", new JSONObject().put("udc", "1").put("rdsId", new JSONObject(jobj).getString("rdsId")).put("rdsVer", new JSONObject(jobj).getString("rdsVer")).put("dpId", new JSONObject(jobj).getString("dpId")).put("dc", new JSONObject(jobj).getString("dc")).put("mi", new JSONObject(jobj).getString("mi")).put("mc", new JSONObject(jobj).getString("mc")).put("bav", "").put("_content_", "")).put("secKey", new JSONObject().put("ci", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("ci")).put("ki", "").put("_content_", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("content"))).put("hmac", new JSONObject(Pid.getString("PidData")).getString("Hmac")))).put("agentDetails", new JSONObject().put("orderNumber", edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo()).put("location", new JSONObject().put("lat", String.valueOf(edsEkycAntWorkViewModel.getDataManager().getDCLatitude())).put("lng", String.valueOf(edsEkycAntWorkViewModel.getDataManager().getCurrentLongitude())).put("city", edsWithActivityList.edsResponse.getConsigneeDetail().getAddress().getCity())).put("timeStamp", "").put("id", getViewModel().getDataManager().getCode())));
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

    public class BackgroundTask extends AsyncTask<String, Void, String> {
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
        protected String doInBackground(String... requestbody){
            Response response = null;
            try{
                //String url = "https://ecom.fnpaas.com/global-sundry/v1/ecom/validate_ekyc";
                SSLContext sslContext = null;
                sslContext = createCertificate();
                String authString = edsActivityWizard.getQuestionFormFields().getUsername() + ":" + edsActivityWizard.getQuestionFormFields().getPassword();
                byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
                String authStringEnc = new String(authEncBytes);
                URIBuilder ub = new URIBuilder(edsActivityWizard.getQuestionFormFields().getEkyc_url());
                String urlparam = ub.toString();
                URL object = new URL(urlparam);
                // URL object = new URL("https://apideveloper.rblbank.com/test/sb/rbl/v1/biometric/kyc");
                HttpsURLConnection connection = (HttpsURLConnection) object.openConnection();
                if(connection instanceof HttpsURLConnection){
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                }
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
                connection.setRequestProperty("Content-Type", "application/json");
               connection.setDoOutput(false);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(final_payload);
                wr.flush();
                wr.close();
                responseCode = connection.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK || responseCode == 201){
                    server_response = EdsEkycXMLViewModel.readStream(connection.getInputStream());
                    Log.e("Freo Response", server_response);
                } else{
                    server_response = EdsEkycXMLViewModel.readStream(connection.getErrorStream());
                    Log.e("Freo Response", server_response);
                }
            } catch(IOException | URISyntaxException e){
                e.printStackTrace();
                p.dismiss();
            }
            return server_response;
        }

        @Override
        protected void onPostExecute(String response){
            try{
                p.dismiss();
                super.onPostExecute(response);
                if(response != null && !response.equalsIgnoreCase("")){
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject response_status = jsonObject.getJSONObject("response_status");
                    JSONArray message = response_status.getJSONArray("messages");
                    int status_code = response_status.getInt("status_code");
                    String status = response_status.getString("status");
                    if(status_code == 201){
                        isKycCompleted = true;
                        fragmentEkycAntworkBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                        fragmentEkycAntworkBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                        fragmentEkycAntworkBinding.iciciErrorText.setVisibility(View.GONE);
                        fragmentEkycAntworkBinding.iciciSuccessText.setText(status);
                    } else{
                        isKycCompleted = false;
                        String messages = message.getJSONObject(0).getString("message");
                        fragmentEkycAntworkBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                        fragmentEkycAntworkBinding.iciciSuccessText.setVisibility(View.GONE);
                        fragmentEkycAntworkBinding.iciciErrorText.setVisibility(View.VISIBLE);
                        fragmentEkycAntworkBinding.iciciErrorText.setText(messages);
                    }
                } else{
                    isKycCompleted = false;
                    fragmentEkycAntworkBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                    fragmentEkycAntworkBinding.iciciSuccessText.setVisibility(View.GONE);
                    fragmentEkycAntworkBinding.iciciErrorText.setVisibility(View.VISIBLE);
                    fragmentEkycAntworkBinding.iciciErrorText.setText("No Response from server. Please try again");
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}