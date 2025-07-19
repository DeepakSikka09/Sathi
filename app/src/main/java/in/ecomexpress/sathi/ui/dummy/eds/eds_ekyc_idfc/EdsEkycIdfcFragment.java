package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_idfc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.apache.axis.encoding.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.databinding.FragmentEkycIdfcBinding;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.QuestionFormFieldDetail;
import in.ecomexpress.sathi.repo.remote.model.eds.IDFCToken_Response;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.GlobalConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@AndroidEntryPoint
public class EdsEkycIdfcFragment extends BaseFragment<FragmentEkycIdfcBinding, EdsEkycIdfcViewModel> implements IEdsEkycIdfcFragmentNavigator, ActivityData {
    EdsWithActivityList edsWithActivityList;
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    FragmentEkycIdfcBinding fragmentEdsEkycIdfcBinding;
    @Inject
    EdsEkycIdfcViewModel edsEkycIdfcViewModel;
    JSONObject pidDataJson;
    String token = null;
    long consignee_mobile_no;
    String tspVdrId, agntId, udc, trmnlId, caLocn, caId, id, ekyc_url;
    boolean isKycAlreadyCompleted ;
    boolean isKycCompleted = false;

    ProgressDialog dialog;
    QuestionFormFieldDetail questionFormFieldDetail;
    String IDFC_TOKEN = "";
    String IDFC_ACCESS_TOKEN = "";

    public static EdsEkycIdfcFragment newInstance(){
        EdsEkycIdfcFragment fragment = new EdsEkycIdfcFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        edsEkycIdfcViewModel.setNavigator(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        fragmentEdsEkycIdfcBinding = getViewDataBinding();
        try{
            if(getArguments() != null){
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                edsEkycIdfcViewModel.setData(edsActivityWizard, masterActivityData);
                questionFormFieldDetail = edsActivityWizard.getQuestionFormFields();
                ekyc_url = questionFormFieldDetail.getIDFC_URL();
            }
        } catch(Exception e){
            getBaseActivity().showSnackbar(e.getLocalizedMessage());
        }
    }

    public String generateToken(PrivateKey privateKey){
        String jws = "";
        try{
            jws = Jwts.builder().setHeaderParam("alg", "RS256").setHeaderParam("typ", "JWT").setHeaderParam("kid", edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_kid()).claim("jti", System.currentTimeMillis() + "").claim("sub", edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_id()).claim("iss", edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_id()).claim("aud", edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_aud()).claim("exp", System.currentTimeMillis() + 20 * 60 * 1000).signWith(SignatureAlgorithm.RS256, privateKey).compact();
        } catch(Exception e){
            e.printStackTrace();
        }
        return jws;
    }

    public String createTransactionID() throws Exception{
        String correlationId = "";
        try{
            if(edsActivityWizard.awbNo != null)
            {
                 DateFormat simple = new SimpleDateFormat("ddMMyyyy");
                 Date result = new Date(System.currentTimeMillis());
                 String time = simple.format(result);
                 correlationId =  edsActivityWizard.awbNo + "_"  + time;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return correlationId;
        //return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    private String generateRRNValue(){
        String Year = new SimpleDateFormat("Y").format(new Date());
        String julianDateString = new SimpleDateFormat("DDD").format(new Date());
        String Hour = new SimpleDateFormat("HH").format(new Date());
        String system_trace = new SimpleDateFormat("SSSSSS").format(new Date());
        return Year + julianDateString + Hour + system_trace;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivateScan(){
        if(!isKycAlreadyCompleted){
            PrivateKey privateKey = null;
            try{
                privateKey = readPrivateKey();
          /*      IDFC_TOKEN = generateToken(privateKey);
                Log.e("idfc url", edsActivityWizard.getQuestionFormFields().getIDFC_AUTH());
                edsEkycIdfcViewModel.generateToken(edsActivityWizard.getQuestionFormFields().getIDFC_AUTH(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_scope(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_grant_type(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_id(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_assertion_type(), IDFC_TOKEN);
          */  } catch(Exception e){
                e.printStackTrace();
            }
        } else{
            getBaseActivity().showSuccessInfo("Kyc Already Completed");
        }
    }

    @Override
    public void sendData(String pidData, String awb_no, String order_number){
        try{
            String pid = android.util.Base64.encodeToString(pidData.getBytes(), android.util.Base64.NO_WRAP);
            dialog = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog);
            dialog.setMessage("Verifying....");
            dialog.setCancelable(false);
            dialog.show();
            dialog.setIndeterminate(false);
            Log.e("pidDataJson", pid + "");
            String ss2 = getRequestpacket(pid, awb_no, order_number).toString().replace("\\n", "");
            String ss3 = ss2.replace("\\", "");
            String ss4 = ss3.trim();
            JSONObject request_payload = new JSONObject(ss4);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids){
                    return getResponseFromIDFC(request_payload, token, ekyc_url);
                }

                @Override
                protected void onPostExecute(String s){
                    super.onPostExecute(s);
                    dialog.dismiss();
                    String message = "";
                    if(s != null){
                        //
                        try{
                            JSONObject response = new JSONObject(s);
                            if(response.getJSONObject("aadharBKYCResp") != null){
                                String status = response.getJSONObject("aadharBKYCResp").getJSONObject("metadata").getString("status");
                                message = response.getJSONObject("aadharBKYCResp").getJSONObject("metadata").getString("message");
                                if(status.equalsIgnoreCase("SUCCESS")){
                                    isKycAlreadyCompleted = true;
                                    isKycCompleted = true;
                                    fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.GONE);
                                    fragmentEdsEkycIdfcBinding.iciciSuccessText.setText(message);
                                } else if(status.equalsIgnoreCase("ERROR")){
                                    isKycCompleted = false;
                                    fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciErrorText.setText(message);
                                } else{
                                    fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                    fragmentEdsEkycIdfcBinding.iciciErrorText.setText("Server Error");
                                }
                            }
                            else if(response.getString("code").equalsIgnoreCase("400"))
                            {
                                isKycCompleted = false;
                                showMessage(response.getString("details"));
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    } else{
                        getBaseActivity().showSnackbar("Server Error");
                    }
                }
            }.execute();
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void getToken(IDFCToken_Response idfcToken_response){
        try{
            IDFC_ACCESS_TOKEN = idfcToken_response.access_token;
            EDSDetailActivity.edsDetailActivity.scanMantra();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void getMobile(EncryptContactResponse encryptContactResponse){
        if(encryptContactResponse.isStatus()){
            EDSDetailActivity.edsDetailActivity.scanMantra();
            consignee_mobile_no = encryptContactResponse.getResponse().getConsignee_mobile_number();
        } else{
            getBaseActivity().showSnackbar("Consignee Contact decryption Error ..");
        }
    }

    public String getResponseFromIDFC(JSONObject request_payload, String token, String url){
        String server_response = null;
        HttpURLConnection urlConnection = null;
        try{
            URL request_url = new URL(url);
            urlConnection = (HttpURLConnection) request_url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("correlationId", createTransactionID());
            urlConnection.setRequestProperty("source", questionFormFieldDetail.getIDFC_source());
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + IDFC_ACCESS_TOKEN);
            Log.e("request", request_payload.toString());
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(request_payload.toString());
            writer.flush();
            int code = urlConnection.getResponseCode();
            if(code != 200){
                throw new IOException("Invalid response from server: " + code);
            }
            server_response = readStream(urlConnection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = rd.readLine()) != null){
                Log.i("data", line);
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
        return server_response;
    }

    public static String readStream(InputStream in){
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try{
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                response.append(line);
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            if(reader != null){
                try{
                    reader.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    public void getData(BaseFragment fragment){
    }

    @Override
    public boolean validateData(){
        return isKycAlreadyCompleted;
    }

    @Override
    public void validate(boolean flag){
    }

    @Override
    public boolean validateCancelData(){
        return !isKycAlreadyCompleted;
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
    public EdsEkycIdfcViewModel getViewModel(){
        return edsEkycIdfcViewModel;
    }

    @Override
    public int getBindingVariable(){
        return BR.viewModel;
    }

    @Override
    public int getLayoutId(){
        return R.layout.fragment_ekyc_idfc;
    }

    public JSONObject getRequestpacket(String Pid, String awb_no, String order_number){
        JSONObject json = new JSONObject();
        try{
            String RRN = generateRRNValue();
            DateFormat df = new SimpleDateFormat("YYMMDD", Locale.getDefault());
            String actionDate = df.format(Calendar.getInstance().getTime());
            DateFormat df1 = new SimpleDateFormat("MMDDhhmmss", Locale.getDefault());
            String txtDateTime = df1.format(Calendar.getInstance().getTime());
            JSONObject aadharBKYCReq = new JSONObject();
            json.put("aadharBKYCReq", aadharBKYCReq);
            aadharBKYCReq.put("actionDate", actionDate);
            aadharBKYCReq.put("agentInfo", edsActivityWizard.getQuestionFormFields().getIDFC_agentInfo());
            aadharBKYCReq.put("caMID", edsActivityWizard.getQuestionFormFields().getIDFC_caMID());
            aadharBKYCReq.put("kycInfo", Pid);
            aadharBKYCReq.put("RRN", RRN);
            aadharBKYCReq.put("txnDateTime", txtDateTime);
            aadharBKYCReq.put("orderNumber", order_number);
            aadharBKYCReq.put("mobileNumber", "");
            aadharBKYCReq.put("consentToDwnld", "Y");
            aadharBKYCReq.put("referenceNumber", edsActivityWizard.customerRemarks);
            aadharBKYCReq.put("referenceInd", edsActivityWizard.getQuestionFormFields().getIDFC_referenceInd());
            aadharBKYCReq.put("productType", edsActivityWizard.getQuestionFormFields().getIDFC_productType());
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

    public PublicKey getFromString() throws Exception{
        InputStream XmlFileInputStream = getResources().openRawResource(R.raw.ecom_pubkey);
        String key = readTextFile(XmlFileInputStream);
        String pubKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----\n", "");
        pubKeyPEM = pubKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = android.util.Base64.decode(pubKeyPEM, android.util.Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubkey = kf.generatePublic(keySpec);
        return pubkey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey readPrivateKey() throws Exception{
        /*InputStream XmlFileInputStream = getResources().openRawResource(R.raw.key);
        String key = readTextFile(XmlFileInputStream);
        String publicKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "").replace("-----END PRIVATE KEY-----", "");
        byte[] encodedKey = Base64.decode(publicKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;*/

        SSLContext sslContext = null;
        InputStream targetStream = null;
        PrivateKey privateKey = null;
        try{
            URL cert_url = new URL(SathiApplication.hashMapAppUrl.get(GlobalConstant.DynamicAppUrl.GET_SERVER_CERT) + "?cert_name=idfcPem");
            HttpURLConnection con = (HttpURLConnection) cert_url.openConnection();
            con.setRequestProperty("api_key", in.ecomexpress.sathi.BuildConfig.API_KEY);
            con.setRequestProperty("app_ver", in.ecomexpress.sathi.BuildConfig.VERSION_NAME);
            con.setRequestProperty("auth_token",edsEkycIdfcViewModel.getAuthToken());
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
            if(status){
                String cert_file = jsonObject.getString("cert_file_string");
                String encryptText = CommonUtils.decrypt(cert_file, Constants.DECRYPT_IDFC);
                String publicKeyPEM = encryptText.replace("-----BEGIN PRIVATE KEY-----", "").replaceAll(System.lineSeparator(), "").replace("-----END PRIVATE KEY-----", "");
                byte[] encodedKey = Base64.decode(publicKeyPEM);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                privateKey = keyFactory.generatePrivate(keySpec);
                IDFC_TOKEN = generateToken(privateKey);
                edsEkycIdfcViewModel.generateToken(edsActivityWizard.getQuestionFormFields().getIDFC_AUTH(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_scope(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_grant_type(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_id(), edsActivityWizard.getQuestionFormFields().getIDFC_AUTH_client_assertion_type(), IDFC_TOKEN);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return privateKey;


    }

    public String readTextFile(InputStream inputStream){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try{
            while((len = inputStream.read(buf)) != -1){
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch(IOException e){
        }
        return outputStream.toString();
    }
}
