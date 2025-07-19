package in.ecomexpress.sathi.ui.dummy.eds.eds_bkyc_idfc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentBkycIdfcBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.EncryptContactResponse;
import in.ecomexpress.sathi.repo.remote.model.GenerateToken;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.uid.DeviceInfo;
import in.ecomexpress.sathi.utils.Constants;


@AndroidEntryPoint
public class EdsBkycIdfcFragment extends BaseFragment<FragmentBkycIdfcBinding, EdsBkycIdfcViewModel> implements IEdsBkycIdfcFragmentNavigator, ActivityData {
    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    FragmentBkycIdfcBinding fragmentEdsEkycIdfcBinding;
    @Inject
    EdsBkycIdfcViewModel edsEkycIdfcViewModel;
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
    String bio;
    String idc;
    String lat;
    String lng;
    //String udc;
    String rdsId;
    String rdsVer;
    String dpId;
    String dc;
    String mi;
    String mc;
    List<String> value;
    //JSONObject pidDataJson;
    private String icici_url, icici_token, icici_wadh;
    DeviceInfo info;

    public static EdsBkycIdfcFragment newInstance(){
        EdsBkycIdfcFragment fragment = new EdsBkycIdfcFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        edsEkycIdfcViewModel.setNavigator(this);
        positions = new ArrayList<>();
        serializer = new Persister();
    }

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
                Gson gson = new Gson();
                String jsonInString = gson.toJson(edsActivityWizard);
                ObjectMapper oMapper = new ObjectMapper();
                String q = edsActivityWizard.getQuestion_form_dummy();
                try{
                    System.out.println(q);
                    JSONObject idfccust_infoobj = new JSONObject(q);
                    tspVdrId = idfccust_infoobj.getString("tspVdrId");
                    agntId = idfccust_infoobj.getString("agntId");
                    udc = idfccust_infoobj.getString("udc");
                    trmnlId = idfccust_infoobj.getString("trmnlId");
                    caLocn = idfccust_infoobj.getString("caLocn");
                    caId = idfccust_infoobj.getString("caId");
                    id = idfccust_infoobj.getString("id");
                    ekyc_url = idfccust_infoobj.getString("url");
                } catch(Exception e){
                    e.printStackTrace();
                    getBaseActivity().showSnackbar("Ekyc Customer Info field missing some element");
                }
            }
        } catch(Exception e){
            getBaseActivity().showSnackbar("Master Data Sync Error");
        }
    }

    @Override
    public void ongetPid(){
        if(!isKycCompleted){
            edsEkycIdfcViewModel.getmobile_no(edsActivityWizard.getAwbNo());
            edsEkycIdfcViewModel.checkStatus();
        }
        else{
            getBaseActivity().showSuccessInfo("Kyc Already Completed ");
        }
    }

    @Override
    public void sendData(String pidData){
        try{
            dialog = new ProgressDialog(getActivity(),android.R.style.Theme_Material_Light_Dialog);
            dialog.setMessage("Verifying....");
            dialog.setCancelable(false);
            dialog.show();
            dialog.setIndeterminate(false);
            String ss = pidData.replace("\\n", "");
            String ss1 = ss.replace("\\", "");
            pidDataJson = new JSONObject(pidData);
            String ss2 = getpacket(pidDataJson).toString().replace("\\n", "");
            String ss3 = ss2.replace("\\", "");
            String ss4 = ss3.trim();
            JSONObject request_payload = new JSONObject(ss4);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids){
                    if(token != null){
                        return getResponseFromicicioap(request_payload, token, getActivity(), ekyc_url);
                    } else{
                        return "false_token";
                    }
                }

                @Override
                protected void onPostExecute(String s){
                    super.onPostExecute(s);
                    dialog.dismiss();
                    Log.d("iiddffcc", s);
                    if(s != null){
                        //
                        try{
                            JSONObject response = new JSONObject(s);
                            String ss = response.getJSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getString("rslt");
                            if(ss.equalsIgnoreCase("OK")){
                                isKycCompleted = true;
                                fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.GONE);
                                fragmentEdsEkycIdfcBinding.iciciSuccessText.setText(" Success ");
                            } else if(ss.equalsIgnoreCase("ERROR")){
                                fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciErrorText.setText(response.getJSONObject("enquireGenericCBSAadharBKYCRes").getJSONObject("msgHdr").getJSONArray("error").getJSONObject(0).getString("rsn"));
                            } else{
                                fragmentEdsEkycIdfcBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycIdfcBinding.iciciErrorText.setText("Server Error ");
                            }
                        } catch(Exception e){
                            getBaseActivity().showSnackbar("Server Error");
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
    public void getToken(GenerateToken iciciResponse){
        if(iciciResponse.isStatus()){
            edsEkycIdfcViewModel.getmobile_no(edsActivityWizard.getAwbNo());
            token = iciciResponse.getTodoResponse().getAccess_token();
        } else{
            getBaseActivity().showSnackbar("Token Error ..");
        }
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

    public static String getResponseFromicicioap(JSONObject request_payload, String token, Context con, String url){
        String server_response = null;
        DefaultHttpClient httpClient = null;
        //if (CommonUtility.isNotEmpty(url)) {
        try{
            /************** For getting response from HTTP URL start ***************/
            final Resources resources = con.getResources();
            InputStream in = resources.openRawResource(R.raw.servercertificate);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(in);
            String alias = cert.getSubjectX500Principal().getName();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession){
                    return true;
                }
            });
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustManagers, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("access_token", token);
            URL object = new URL(ub.toString());
            HttpsURLConnection connection = (HttpsURLConnection) object.openConnection();
            if(connection instanceof HttpsURLConnection){
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // connection.setRequestProperty("access_token", token);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(request_payload.toString());
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                server_response = readStream(connection.getInputStream());
                Log.d("IDFC200", server_response);
            } else{
                server_response = connection.getResponseMessage();
                Log.d("IDFCNO200", server_response);
            }
        } catch(Exception e){
            e.printStackTrace();
            Log.d("ecx", e.getMessage());
            return server_response;
        }
        Log.d("IDFCOUT200", server_response);
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
    public EdsBkycIdfcViewModel getViewModel(){
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

    /* public JSONObject getpacket(JSONObject Pid) {
         JSONObject json = new JSONObject();
 //  SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
         try {
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
             try {
                 generatedSecuredPasswordHash = generateStorngPasswordHash(rrn1, date);
             } catch (NoSuchAlgorithmException e) {
                 e.printStackTrace();
             } catch (InvalidKeySpecException e) {
                 e.printStackTrace();
             }
             //004414208313            System.out.println(generatedSecuredPasswordHash);
             Log.d("generated hash", generatedSecuredPasswordHash);
             String jobj = new JSONObject(Pid.getString("PidData")).getString("DeviceInfo");
             return json.put("enquireGenericCBSAadharBKYCReq", new JSONObject().put("msgHdr", new JSONObject().
                     put("frm", new JSONObject()
                             .put("id", id))
                     .put("hdrFlds", new JSONObject()
                             .put("cnvId", date)
                             .put("msgId", date)
                             .put("extRefId", "ucic")
                             .put("bizObjId", date)
                             .put("timestamp", timestamp)))
                     .put("msgBdy", new JSONObject()
                             .put("mobNum", "91" + mobile_no)
                             .put("svcHdr", new JSONObject()
                                     .put("txnId", rrn1 + "20" + id)
                                      .put("tspVdrId", "20"))
                                     //.put("tspVdrId", tspVdrId))
                             .put("txnInfo", new JSONObject()
                                     .put("txnDtTm", date)
                                     .put("rrn", rrn1 + "20" + id)

                                    // .put("trmnlId", trmnlId)
                                     .put("trmnlId", "11262432        ")
                                     .put("caId", caId)
                                     .put("caLocn", caLocn)
                                     .put("agntId", "13320399")
                                   //  .put("agntId", agntId)
                                     .put("txnHmac", generatedSecuredPasswordHash)
                                     .put("unqueNo", "20" + id))
                             .put("authInfo", new JSONObject()
                                     .put("data", new JSONObject().put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content")).put("type", "01"))
                                     .put("fmt", new JSONObject()
                                             .put("type", "P").put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content")))
                                     .put("meta", new JSONObject()
                                             .put("udc", udc)
                                             .put("rdsId", new JSONObject(jobj).getString("rdsId"))
                                             .put("rdsVer", new JSONObject(jobj).getString("rdsVer"))
                                             .put("dpId", new JSONObject(jobj).getString("dpId"))
                                             .put("dc", new JSONObject(jobj).getString("dc"))
                                             .put("mi", new JSONObject(jobj).getString("mi"))
                                             .put("mc", new JSONObject(jobj).getString("mc"))
                                             .put("bav", "")
                                             .put("_content_", ""))
                                     .put("secKey", new JSONObject()
                                             .put("ci", new JSONObject(new JSONObject(Pid.getString("PidData")
                                             ).getString("Skey")).getString("ci")).put("ki", "")
                                             .put("_content_", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("content")))
                                     .put("hmac", new JSONObject(Pid.getString("PidData")).getString("Hmac")))));

         } catch (JSONException e) {
             e.printStackTrace();
         }
         return json;

     }
 */
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
                    // .put("tspVdrId", "20"))
                    .put("tspVdrId", tspVdrId)).put("txnInfo", new JSONObject().put("txnDtTm", date).put("rrn", rrn1 + "20" + id).put("trmnlId", trmnlId)
                    //.put("trmnlId", "11262432        ")
                    .put("caId", caId).put("caLocn", caLocn)
                    //.put("agntId", "13320399")
                    .put("agntId", agntId)
                    // .put("txnHmac", generatedSecuredPasswordHash)
                    .put("unqueNo", "20" + id)).put("authInfo", new JSONObject().put("data", new JSONObject().put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content")).put("type", "01")).put("fmt", new JSONObject().put("type", "P").put("_content_", new JSONObject(new JSONObject((Pid.getString("PidData"))).getString("Data")).getString("content"))).put("meta", new JSONObject().put("udc", "1").put("rdsId", new JSONObject(jobj).getString("rdsId")).put("rdsVer", new JSONObject(jobj).getString("rdsVer")).put("dpId", new JSONObject(jobj).getString("dpId")).put("dc", new JSONObject(jobj).getString("dc")).put("mi", new JSONObject(jobj).getString("mi")).put("mc", new JSONObject(jobj).getString("mc")).put("bav", "").put("_content_", "")).put("secKey", new JSONObject().put("ci", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("ci")).put("ki", "").put("_content_", new JSONObject(new JSONObject(Pid.getString("PidData")).getString("Skey")).getString("content"))).put("hmac", new JSONObject(Pid.getString("PidData")).getString("Hmac")))));
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
}
