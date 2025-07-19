package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_hdfc;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
//import org.json.XML;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import dagger.hilt.android.AndroidEntryPoint;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentEkycHdfcBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class EdsEkycHdfcFragment extends BaseFragment<FragmentEkycHdfcBinding, EdsEkycHdfcViewModel> implements IEdsEkycHdfcFragmentNavigator, ActivityData {

    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    FragmentEkycHdfcBinding fragmentEdsEkycHdfcBinding;
    @Inject
    EdsEkycHdfcViewModel edsEkycHdfcViewModel;
    String xml_pid;
    JSONObject pidDataJson;
    private Persister serializer = null;
    private ArrayList<String> positions;
    String REQ_TYPE = "";
    String rc = "";
    String UID_NO, Req_Date_Time, DEVICE_SERIAL_NO, Req_No, DEVICE_IP, PIN, TRANS_DATE, STAN, LOCAL_TIME, LOCAL_DATE, ACQ_ID, RRN,
            ADVappName, ADVappId, PidData, DeviceInfo;
    private static final int a = 'a';
    private static final int z = 'z';
    private static final int A = 'A';
    private static final int Z = 'Z';
    private static final int ZERO = '0';
    private static final int NINE = '9';

    @Override
    public void getData(BaseFragment fragment) {

    }

    @Override
    public boolean validateData() {
        return false;
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

    @Override
    public EDSActivityWizard getActivityWizard() {
        return null;
    }

    @Override
    public EdsEkycHdfcViewModel getViewModel() {

        return edsEkycHdfcViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ekyc_hdfc;
    }

    public static EdsEkycHdfcFragment newInstance() {
        EdsEkycHdfcFragment fragment = new EdsEkycHdfcFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edsEkycHdfcViewModel.setNavigator(this);
        // Log.d(TAG, "onCreate: " + this.toString());
        positions = new ArrayList<>();
        serializer = new Persister();
        //setsoap();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentEdsEkycHdfcBinding = getViewDataBinding();
        //fragmentEdsEkycHdfcBinding.editUrn.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        //fragmentEdsEkycHdfcBinding.inputAdhar.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        try {
            //  getPublicKey();

//            String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur/Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82oXfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+mMQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3bkQIDAQAB";
//            String publicKey1 = "MIIEpAIBAAKCAQEA4BIOjwfiVxPgfbb743Sp07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur/Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82oXfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+mMQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3bkQIDAQABAoIBAGT5/jGa3fdb78j3k2AXjQjpjHCivMrE7rYArBJvYg7k4oqAvSd20q8ykb1RHVFLl2lb9YNRg9OI6Z7WG5eD4AbF2qW4BA8Z/YEHJHXMK9c8cIbOs2uccbocAAyK4ij8orzpXlKrnoY3Ya0FqAcIJADxyVFUwJaacG6BhHjBMC453T3WnTODmF+Mw8lPSQZYmx4pFPYmjO0m6QHNY8hdJvz0OSTlrkhFOuLp+xhmxyBwmn7WxHnIoSSN9V89EmEzHUp08Ko3CLuldKY6yG22kFFDJOWJnG5l4/QvXDqXmoDgAqpG+YYEqVMSHLO5pAgci4uf5sclJ7roFuAr1NUk9AECgYEA/oCU2EwYKzagLrcxXqQCzO30OJk1p44eufhlPwzkreeE+EqFgAS3LsyENVoLmJ45F6TJ65BlMgtgziJ+LJtNB2TrgecK1iNRBEwQGZbt3cNPSRXaqnxyimp7TNEy83Dy1WvGH4087XSFsenqXuypx5c130JqpHW77DUFDS+XnaECgYEA4WOg9G5r/9SIfOYh1C/QIqT/OD5errXOe7/KkZARowTlXeKWNIjpPITFSAHXKSB+zQmXKejTH0/nPALOk4airxe61c6xJfpXlX2V6DfSGROscLu/royo/dSb4g1gpk1/etNyqZ08zUZQIlB+Pw2sq1wV4ZuAQ+tX69edxskpF/ECgYEA59PUwhq+wU5nJ2a0UBNygZ4YIx5co55wGtNL1t01ybvlgg7QAMdK3hRqMfcuLP2j4Ae57xx/MdDvg5Yj/RoSVrP1W0VMt7c/63wmBklXr/RjWp3PrBDkfXV1j4nYsaynZwVfWgpb/6lux5veZE2MckUpS4/CeMDOR+IcEeDSaUECgYAxHg8TdYTfqadc4KH6pbjL8/0SerUTiddHrJIuiqDtT8HUZe6p4DRD1gNQH+aNteEsOazRm0V3C2iu8UWnbK3DTn6O3Y5JYnb3wviKNK/6ewkXQDMTjN+/ATg/WP74/uaLE81nHTcE3Q/ViokYoJhCd7zk/4hCROVSJhjiGzBzYQKBgQCG8BUHPT6W8TNjLFXGiedtubqmll3vjSX1Vboe3C58l9RggOXL+adcqcrQ35BXho59svtP8CT6urkXrfvLH3YOucQkbx9Ji9cG/KlpIiJLGIfo41sGpZgG3ZezfOPA6o8idliIJt3VhiN/jLXabizODJW+Js8+ViOI0eTSqwo8dA==";
//            String hdfc1 = "MIIDxTCCAq2gAwIBAgIQAqxcJmoLQ`JuPC3nyrkYldzANBgkqhkiG9w0BAQUFADBsMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5jZSBFViBSb290IENBMB4XDTA2MTExMDAwMDAwMFoXDTMxMTExMDAwMDAwMFowbDELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTErMCkGA1UEAxMiRGlnaUNlcnQgSGlnaCBBc3N1cmFuY2UgRVYgUm9vdCBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMbM5XPm+9S75S0tMqbf5YE/yc0lSbZxKsPVlDRnogocsF9ppkCxxLeyj9CYpKlBWTrT3JTWPNt0OKRKzE0lgvdKpVMSOO7zSW1xkX5jtqumX8OkhPhPYlG++MXs2ziS4wblCJEMxChBVfvLWokVfnHoNb9Ncgk9vjo4UFt3MRuNs8ckRZqnrG0AFFoEt7oT61EKmEFBIk5lYYeBQVCmeVyJ3hlKV9Uu5l0cUyx+mM0aBhakaHPQNAQTXKFx01p8VdteZOE3hzBWBOURtCmAEvF5OYiiAhF8J2a3iLd48soKqDirCmTCv2ZdlYTBoSUeh10aUAsgEsxBu24LUTi4S8sCAwEAAaNjMGEwDgYDVR0PAQH/BAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFLE+w2kD+L9HAdSYJhoIAu9jZCvDMB8GA1UdIwQYMBaAFLE+w2kD+L9HAdSYJhoIAu9jZCvDMA0GCSqGSIb3DQEBBQUAA4IBAQAcGgaX3NecnzyIZgYIVyHbIUf4KmeqvxgydkAQV8GK83rZEWWONfqe/EW1ntlMMUu4kehDLI6zeM7b41N5cdblIZQB2lWHmiRk9opmzN6cN82oNLFpmyPInngiK3BD41VHMWEZ71jFhS9OMPagMRYjyOfiZRYzy78aG6A9+MpeizGLYAiJLQwGXFK3xPkKmNEVX58Svnw2Yzi9RKR/5CYrCsSXaQ3pjOLAEFe4yHYSkVXySGnYvCoCWw9E1CAx2/S6cCZdkGCevEsXCS+0yx5DaMkHJ8HSXPfqIbloEpw8nL+e/IBcm2PN7EeqJSdnoDfzAIJ9VNep+OkuE6N36B9K";
//            // String hdfc2="MIIEsTCCA5mgAwIBAgIQBOHnpNxc8vNtwCtCuF0VnzANBgkqhkiG9w0BAQsFADBsMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5jZSBFViBSb290IENBMB4XDTEzMTAyMjEyMDAwMFoXDTI4MTAyMjEyMDAwMFowcDELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTEvMC0GA1UEAxMmRGlnaUNlcnQgU0hBMiBIaWdoIEFzc3VyYW5jZSBTZXJ2ZXIgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC24C/CJAbIbQRf1+8KZAayfSImZRauQkCbztyfn3YHPsMwVYcZuU+UDlqUH1VWtMICKq/QmO4LQNfE0DtyyBSe75CxEamu0si4QzrZCwvV1ZX1QK/IHe1NnF9Xt4ZQaJn1itrSxwUfqJfJ3KSxgoQtxq2lnMcZgqaFD15EWCo3j/018QsIJzJa9buLnqS9UdAn4t07QjOjBSjEuyjMmqwrIw14xnvmXnG3Sj4I+4G3FhahnSMSTeXXkgisdaScus0Xsh5ENWV/UyU50RwKmmMbGZJ0aAo3wsJSSMs5WqK24V3B3aAguCGikyZvFEohQcftbZvySC/zA/WiaJJTL17jAgMBAAGjggFJMIIBRTASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBhjAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5kaWdpY2VydC5jb20wSwYDVR0fBEQwQjBAoD6gPIY6aHR0cDovL2NybDQuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0SGlnaEFzc3VyYW5jZUVWUm9vdENBLmNybDA9BgNVHSAENjA0MDIGBFUdIAAwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzAdBgNVHQ4EFgQUUWj/kK8CB3U8zNllZGKiErhZcjswHwYDVR0jBBgwFoAUsT7DaQP4v0cB1JgmGggC72NkK8MwDQYJKoZIhvcNAQELBQADggEBABiKlYkD5m3fXPwdaOpKj4PWUS+Na0QWnqxj9dJubISZi6qBcYRb7TROsLd5kinMLYBq8I4g4Xmk/gNHE+r1hspZcX30BJZr01lYPf7TMSVcGDiEo+afgv2MW5gxTs14nhr9hctJqvIni5ly/D6q1UEL2tU2ob8cbkdJf17ZSHwD2f2LSaCYJkJA69aSEaRkCldUxPUd1gJea6zuxICaEnL6VpPX/78whQYwvwt/Tv9XBZ0k7YXDK/umdaisLRbvfXknsuvCnQsH6qqF0wGjIChBWUMo0oHjqvbsezt3tkBigAVBRQHvFwY+3sAzm2fTYS5yh+Rp/BIAV0AecPUeybQ=";
//            String hdfc3 = "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";
//            String hdfc2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtuAvwiQGyG0EX9fvCmQGsn0iJmUWrkJAm87cn592Bz7DMFWHGblPlA5alB9VVrTCAiqv0JjuC0DXxNA7csgUnu+QsRGprtLIuEM62QsL1dWV9UCvyB3tTZxfV7eGUGiZ9Yra0scFH6iXydyksYKELcatpZzHGYKmhQ9eRFgqN4/9NfELCCcyWvW7i56kvVHQJ+LdO0IzowUoxLsozJqsKyMNeMZ75l5xt0o+CPuBtxYWoZ0jEk3l15IIrHWknLrNF7IeRDVlf1MlOdEcCppjGxmSdGgKN8LCUkjLOVqituFdwd2gILghopMmbxRKIUHH7W2b8kgv8wP1omiSUy9e4wIDAQAB";
//            //            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur/Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82oXfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+mMQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3bkQIDAQAB
////
////                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp
////            07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/
////                    aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur
////                    /Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82o
////            XfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+m
////            MQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3b
////                    kQIDAQAB
////            String ss =SSLPem.replaceAll("\\r\\n", "");
////            String ss1 =SSLPem.replaceAll("\n", "");
//            // PublicKey puc =getpublickey(hdfc2);
//            byte[] key = Base64.getDecoder().decode(hdfc2);
//            PublicKey puc = getPublicKey(key);
//            RSAEncrypterDecrypter rsaEncrypterDecrypter = new RSAEncrypterDecrypter();
//            byte[] bb = rsaEncrypterDecrypter.rsaEncrypt(key, puc);
//            Log.d("BBBYYY", bb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   getPublicKey(getBaseActivity());
//        File fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress);
//        if (!fileDir.exists())
//            fileDir.mkdirs();
//        File file = new File(fileDir, "openapiuat_hdfcbank_com.pem");
//        try {
//            getPublicKey(file.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (!file.exists())
//            try {
//                file.createNewFile();
//


//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                edsEkycHdfcViewModel.setData(edsActivityWizard, masterActivityData);
                Gson gson = new Gson();
                String jsonInString = gson.toJson(edsActivityWizard);
                ObjectMapper oMapper = new ObjectMapper();
                //QuestionFormField q=new QuestionFormField();
                String q = edsActivityWizard.getQuestion_form_dummy();
//            Log.d("santosh", q);
                ObjectMapper Obj = new ObjectMapper();
//
                try {
                    // Displaying JSON String
                    System.out.println(q);
                    // Log.d("JSON String", "jsonStr");
                    JSONObject jObject = new JSONObject(q);

//                    if (jObject.optString("isUrnRequired", "").equalsIgnoreCase("true")) {
//                        fragmentEdsEkycHdfcBinding.llUrnGroup.setVisibility(View.VISIBLE);
//                        fragmentEdsEkycHdfcBinding.layoutScan.setVisibility(View.GONE);
//                    } else {
//                        fragmentEdsEkycHdfcBinding.llUrnGroup.setVisibility(View.GONE);
//                        fragmentEdsEkycHdfcBinding.layoutScan.setVisibility(View.VISIBLE);
//
//                    }
                    if (jObject.optString("isAdharVerificationRequired", "").equalsIgnoreCase("true")) {
                        fragmentEdsEkycHdfcBinding.llAdharGroup.setVisibility(View.VISIBLE);
                    }
                    Map<String, String> webheaderMap = new HashMap<String, String>();
                    Iterator<?> keys = jObject.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (key.contains("neo_app")) {
                            webheaderMap.put(key, jObject.get(key).toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "OnCreateView():-Data not synced Properly.Please sync and try Again..", Toast.LENGTH_LONG).show();
        }

    }

    //    public EasyRsa() throws Exception {
//        this.factory = KeyFactory.getInstance("RSA", "BC");
//        this.priv = factory.generatePrivate(new PKCS8EncodedKeySpec(getPem("rsa_1024_priv.pem").getContent()));
//        this.pub = factory.generatePublic(new X509EncodedKeySpec(getPem("rsa_1024_pub.pem").getContent()));
//        this.cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
//
    @Override
    public void ongetPid() {
        // getBaseActivity().showSnackbar(getString(R.string.check_internet));
        Log.d("onGetPid", "Ongetpidddddddddddddddddddddddddd");

        if (!fragmentEdsEkycHdfcBinding.editUrn.getText().toString().equals(fragmentEdsEkycHdfcBinding.inputAdhar.getText().toString())) {
            EDSDetailActivity.edsDetailActivity.scanMantra();
            getBaseActivity().showSnackbar("Inputed Adhaar no are not Same");
        } else {
            EDSDetailActivity.edsDetailActivity.scanMantra();
        }
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
    @Override

    public void sendData(String pidData) {
        Log.e("DataValue", pidData);
        try {
            // createSOAPRequest();
            Log.d("", "my name is  \"santosh\"");

            WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMDDhhmmss");
            SimpleDateFormat formatter2 = new SimpleDateFormat("MMDDhhmmss");
            SimpleDateFormat formatter3 = new SimpleDateFormat("YDDDHHSSSSSS");
            String Req_Date_Time = formatter.format(date);
            String trans_date = formatter2.format(date);
            String rrn = formatter3.format(date);
              XmlToJson xmlToJson = new XmlToJson.Builder(pidData).build();

            pidDataJson = new JSONObject(xmlToJson.toString());
           // String Hmac = new JSONObject(pidDataJson.getString("PidData")).getString("Hmac");
            String Device_info = new JSONObject(pidDataJson.getString("PidData")).getString("DeviceInfo");
            JSONObject obj = new JSONObject(Device_info);


//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//           // Document doc = dBuilder.parse(pidData);
//
//            InputStream stream = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                stream = new ByteArrayInputStream(pidData.getBytes(StandardCharsets.UTF_8));
//            }
//            Document doc = dBuilder.parse(stream);
//
//
//            Element element=doc.getDocumentElement();
//            element.normalize();
//
//            NodeList nodes = doc.getElementsByTagName("PidData");
//
//
//            for (int i = 0; i < nodes.getLength(); i++) {
//                Element element1 = (Element) nodes.item(i);
//                String ss= getValue("Resp",element1);
//
////                NodeList assignee_ = element1.getElementsByTagName("resp");
////                Element line = (Element) assignee_.item(0);
//               // statuses=(Utility.getCharacterDataFromElement(line));
//
//            }
            //converting json to xml
//             String xml_data = XML.toString(obj);
            char ch = '"';


            String pid = "<soapenv:Envelope xmlns:soapenv=" + ch + "http://schemas.xmlsoap.org/soap/envelope/" + ch + "\n" +
                    "xmlns:uid=" + ch + "uidaiekyc.otp.xsd.hdfcbank.com" + ch + ">" +
                    "<soapenv:Header/>\n" +
                    "<soapenv:Body>\n" +
                    "<uid:eKYCRequest>\n" +
                    "<REQ_TYPE>F</REQ_TYPE>\n" +
                    "<ResidentConsent>\n" +
                    "<rc>Y</rc>\n" +
                    "<mec>Y</mec>\n" +
                    "</ResidentConsent>\n" +
                    "<UID_NO>" + fragmentEdsEkycHdfcBinding.editUrn.getText().toString().trim() + "</UID_NO>\n" +
                    "<Req_Date_Time>" + Req_Date_Time + "</Req_Date_Time>\n" +
                    "<Req_No>" + edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo() + "</Req_No>\n" +
                    "<Cost_Center_No/>\n" +
                    "<Meta>\n" +
                    "<udc>" + "HDF" + DEVICE_SERIAL_NO + "</udc>" +
                    "<fdc>NC</fdc>" +
                    "<idc>NA</idc>\n" +
                    "<pip>" + ip + "</pip>\n" +
                    "<lot>P</lot>\n" +
                    "<lov>" + edsWithActivityList.edsResponse.getConsigneeDetail().getAddress().getPincode() + "</lov>\n" +
                    "</Meta>\n" +
                    "<TransactionInfo>\n" +
                    "<Pan>" + fragmentEdsEkycHdfcBinding.editUrn.getText().toString().trim() + "</Pan>\n" +
                    "<Proc_Code>130000</Proc_Code>\n" +
                    "<TransmDate>" + trans_date + "</TransmDate>\n" +
                    "<Stan>" + trans_date + "</Stan>\n" +
                    "<Local_Trans_Time>" + trans_date.substring(4, 10) + "</Local_Trans_Time>\n" +
                    "<Local_date>" + trans_date.substring(0, 4) + "</Local_date>\n" +
                    "<AcqId>" + "M" + "</AcqId>\n" +
                    "<RRN>" + rrn + "</RRN>\n" +
                    "</TransactionInfo>\n" +
                    "<SOAStandardElements>\n" +
                    "<ADVappId>" + "41" + "</ADVappId>\n" +
                    "<ADVappName>" + "PAYZAPPeComBio" + "</ADVappName>\n" +
                    "<filler1>N</filler1>\n" +
                    "<filler2>\n" +
                    "<![CDATA[" + pidData + "]]></filler2>\n" +
                    "<filler3>" +
                    "<![CDATA[" + "<DeviceInfo " + "xml_data" +"</DeviceInfo>]]></filler3>\n" +
                   // "<![CDATA[" + "<DeviceInfo <Resp errCode=\"0\" errInfo=\"Capture Success\" fCount=\"1\" fType=\"0\" iCount=\"0\" iType=\"0\" nmPoints=\"27\" pCount=\"0\" pType=\"0\" qScore=\"81\"/>" + "]]></filler3>" +
                    "<filler4>?</filler4>\n" +
                    "<!--Optional:-->\n" +
                    "<filler5>?</filler5>\n" +
                    "<!--Optional:-->\n" +
                    "<filler6>?</filler6>\n" +
                    "<!--Optional:-->\n" +
                    "<filler7>?</filler7>\n" +
                    "<!--Optional:-->\n" +
                    "<filler8>?</filler8>\n" +
                    "<!--Optional:-->\n" +
                    "</SOAStandardElements>\n" +
                    "</uid:eKYCRequest>\n" +
                    "</soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            //    pidData.replace("\n", "").replace("\r", "")

            // "<![CDATA[" + "<DeviceInfo" + new JSONObject(pidDataJson.getString("PidData")).getString("DeviceInfo") + "]]></filler3>\n" +
            //
            Log.d("pid", pid.replace("\n", "").replace("\r", ""));
            //new JSONObject(pidDataJson.getString("PidData")).getString("DeviceInfo")
            // "<filler4>"+edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo()+"</filler4>"+

            //32 byte key logic
            String autogenerated32bit = generateAlphaNumericKey(32);
            // Base64 Encoded String of PID


            // String byteArray = Base64.getEncoder().encodeToString(pid.replace("\n", "").replace("\r", "").getBytes());

//            String byteArray = org.apache.axis.encoding.Base64.encode(pid.replace("\n", "").replace("\r", "").getBytes());
//            String byteArray1 = org.apache.axis.encoding.Base64.encode(pid.getBytes());

//            byte[] byteArray = Base64.encodeBase64(pid.replace("\n", "").replace("\r", "").getBytes());
//            System.out.println("encoded value is " + new String(byteArray));
//
////            Base64.Encoder encoder = Base64.getEncoder();
////            String str = encoder.encodeToString("JavaTpoint".getBytes());
//
//            byte[] valueDecoded = Base64.decodeBase64(byteArray);
//            System.out.println("Decoded value is " + new String(valueDecoded));

//            Base64EncoderDecoder bsd=new Base64EncoderDecoder();
//            Base64.Encoder encoder = Base64.getEncoder();
            // String base64encodedPid= bsd.encodeToString(pid.replace("\n", "").replace("\r", ""));

            //  byte[] byteArray = Base64.decodeBase64(pid.replace("\n", "").replace("\r", "").getBytes());


            // Print the decoded array

            // System.out.println(Arrays.toString(byteArray));


            // Print the decoded string

//            String decodedString = new String(byteArray);

            //  String key =  Base64.getEncoder().encodeToString(autogenerated32bit.getBytes());
            String byte32 = generateAlphaNumericKey(32);
            // String key =  Base64.getEncoder().encodeToString(byte32.getBytes());


            //  String base64encodedkey=bsd.encodeToString(autogenerated32bit);
            // String bb1= bsd.encodeToString("OfpBWSiDeP6kIjbyYFDGu3TnBqxTEpLM");
            //   String bb2= bsd.encodeToString(ss);
            // Log.d("key111111",bb1);;
            //   https://sathi2.ecomexpress.in/services/last_mile/v1/login/
//            File fileDir = new File(Environment.getExternalStorageDirectory(), "/" + Constants.EcomExpress);
//            if (!fileDir.exists())
//                fileDir.mkdirs();
//            File file = new File(fileDir, "openapiuat_hdfcbank_com.pem");
//            if (!file.exists())
//                file.createNewFile();
//            BufferedReader br = null;
//            String strLine = "";
//            try {
//                br = new BufferedReader( new FileReader(file));
//                while( (strLine = br.readLine()) != null){
//                    System.out.println(strLine);
//                    Log.d("FILE CONTENT",strLine);
//                }
//            } catch (FileNotFoundException e) {
//                System.err.println("Unable to find the file: fileName");
//            } catch (IOException e) {
//                System.err.println("Unable to read the file: fileName");
//            }
            //  encrypt(ss.getBytes(),getPrivateKey(file.toString()));
//
//            String stringKey = android.util.Base64.encodeToString(secretKey.getEncoded(), android.util.Base64.DEFAULT);
//            Log.d("secretkey", stringKey);
//            String base64 = android.util.Base64.encodeToString(pid.replace("\n", "").replace("\r", "").getBytes("UTF-8"), android.util.Base64.DEFAULT);
//            byte[] encodeValue = Base64.encode(pid.replace("\n", "").replace("\r", "").getBytes(), Base64.DEFAULT);

            // Log.d("qqqqqq", base64);
            //  String b= encrypt_data(base64.getBytes(),stringKey.replace("\n", "").replace("\r", ""));
            AESEncrypterDecrypter asenc = new AESEncrypterDecrypter();
            //  EncryptionDecription getByteKey=new EncryptionDecription();

            // String sss="";
            // String withoutbs64= pid.replace("\n", "").replace("\r", "");
            String ssa = asenc.encrypt(pid, byte32.getBytes());
            // String ssb=asenc.decrypt(ssa,autogenerated32bit.getBytes());
            //  String   encryptedValue=	Base64.getEncoder().encodeToString(ssb);
            // String enc = new String( asenc.encrypt(byteArray,autogenerated32bit.getBytes()), StandardCharsets.UTF_8);

            RSAEncrypterDecrypter rsaEncrypterDecrypter = new RSAEncrypterDecrypter();
//            String content = FileUtils.readFileToString(new File(fileName), StandardCharsets.UTF_8);
//            String contents = new String(Files.readAllBytes(Paths.get("manifest.mf")));
//            System.out.prin
//            tln("Contents (Java 7) : " + contents);

//            String contents = new String(Files.readAllBytes(file.toString()));
//            System.out.println("Contents (Java 7) : " + contents);

            //  String pub = "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";

//            String keycontent=readFile(file.toString());
//            loadStringFromAssets(getActivity(),"openapiuat_hdfcbank_com.pem");

            //   String SSLPem = loadStringFromAssets(getActivity(), "openapiuat_hdfcbank_com.key");
//               // String publicKey = "MIIEsTCCA5mgAwIBAgIQBOHnpNxc8vNtwCtCuF0VnzANBgkqhkiG9w0BAQsFADBsMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5jZSBFViBSb290IENBMB4XDTEzMTAyMjEyMDAwMFoXDTI4MTAyMjEyMDAwMFowcDELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTEvMC0GA1UEAxMmRGlnaUNlcnQgU0hBMiBIaWdoIEFzc3VyYW5jZSBTZXJ2ZXIgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC24C/CJAbIbQRf1+8KZAayfSImZRauQkCbztyfn3YHPsMwVYcZuU+UDlqUH1VWtMICKq/QmO4LQNfE0DtyyBSe75CxEamu0si4QzrZCwvV1ZX1QK/IHe1NnF9Xt4ZQaJn1itrSxwUfqJfJ3KSxgoQtxq2lnMcZgqaFD15EWCo3j/018QsIJzJa9buLnqS9UdAn4t07QjOjBSjEuyjMmqwrIw14xnvmXnG3Sj4I+4G3FhahnSMSTeXXkgisdaScus0Xsh5ENWV/UyU50RwKmmMbGZJ0aAo3wsJSSMs5WqK24V3B3aAguCGikyZvFEohQcftbZvySC/zA/WiaJJTL17jAgMBAAGjggFJMIIBRTASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBhjAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5kaWdpY2VydC5jb20wSwYDVR0fBEQwQjBAoD6gPIY6aHR0cDovL2NybDQuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0SGlnaEFzc3VyYW5jZUVWUm9vdENBLmNybDA9BgNVHSAENjA0MDIGBFUdIAAwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzAdBgNVHQ4EFgQUUWj/kK8CB3U8zNllZGKiErhZcjswHwYDVR0jBBgwFoAUsT7DaQP4v0cB1JgmGggC72NkK8MwDQYJKoZIhvcNAQELBQADggEBABiKlYkD5m3fXPwdaOpKj4PWUS+Na0QWnqxj9dJubISZi6qBcYRb7TROsLd5kinMLYBq8I4g4Xmk/gNHE+r1hspZcX30BJZr01lYPf7TMSVcGDiEo+afgv2MW5gxTs14nhr9hctJqvIni5ly/D6q1UEL2tU2ob8cbkdJf17ZSHwD2f2LSaCYJkJA69aSEaRkCldUxPUd1gJea6zuxICaEnL6VpPX/78whQYwvwt/Tv9XBZ0k7YXDK/umdaisLRbvfXknsuvCnQsH6qqF0wGjIChBWUMo0oHjqvbsezt3tkBigAVBRQHvFwY+3sAzm2fTYS5yh+Rp/BIAV0AecPUeybQ=";
//                 String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur/Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82oXfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+mMQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3bkQIDAQAB";
//
//                 String hdfc1="MIIDxTCCAq2gAwIBAgIQAqxcJmoLQ`JuPC3nyrkYldzANBgkqhkiG9w0BAQUFADBsMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5jZSBFViBSb290IENBMB4XDTA2MTExMDAwMDAwMFoXDTMxMTExMDAwMDAwMFowbDELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTErMCkGA1UEAxMiRGlnaUNlcnQgSGlnaCBBc3N1cmFuY2UgRVYgUm9vdCBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMbM5XPm+9S75S0tMqbf5YE/yc0lSbZxKsPVlDRnogocsF9ppkCxxLeyj9CYpKlBWTrT3JTWPNt0OKRKzE0lgvdKpVMSOO7zSW1xkX5jtqumX8OkhPhPYlG++MXs2ziS4wblCJEMxChBVfvLWokVfnHoNb9Ncgk9vjo4UFt3MRuNs8ckRZqnrG0AFFoEt7oT61EKmEFBIk5lYYeBQVCmeVyJ3hlKV9Uu5l0cUyx+mM0aBhakaHPQNAQTXKFx01p8VdteZOE3hzBWBOURtCmAEvF5OYiiAhF8J2a3iLd48soKqDirCmTCv2ZdlYTBoSUeh10aUAsgEsxBu24LUTi4S8sCAwEAAaNjMGEwDgYDVR0PAQH/BAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFLE+w2kD+L9HAdSYJhoIAu9jZCvDMB8GA1UdIwQYMBaAFLE+w2kD+L9HAdSYJhoIAu9jZCvDMA0GCSqGSIb3DQEBBQUAA4IBAQAcGgaX3NecnzyIZgYIVyHbIUf4KmeqvxgydkAQV8GK83rZEWWONfqe/EW1ntlMMUu4kehDLI6zeM7b41N5cdblIZQB2lWHmiRk9opmzN6cN82oNLFpmyPInngiK3BD41VHMWEZ71jFhS9OMPagMRYjyOfiZRYzy78aG6A9+MpeizGLYAiJLQwGXFK3xPkKmNEVX58Svnw2Yzi9RKR/5CYrCsSXaQ3pjOLAEFe4yHYSkVXySGnYvCoCWw9E1CAx2/S6cCZdkGCevEsXCS+0yx5DaMkHJ8HSXPfqIbloEpw8nL+e/IBcm2PN7EeqJSdnoDfzAIJ9VNep+OkuE6N36B9K";
//                 String hdfc2="MIIEsTCCA5mgAwIBAgIQBOHnpNxc8vNtwCtCuF0VnzANBgkqhkiG9w0BAQsFADBsMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5jZSBFViBSb290IENBMB4XDTEzMTAyMjEyMDAwMFoXDTI4MTAyMjEyMDAwMFowcDELMAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3LmRpZ2ljZXJ0LmNvbTEvMC0GA1UEAxMmRGlnaUNlcnQgU0hBMiBIaWdoIEFzc3VyYW5jZSBTZXJ2ZXIgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC24C/CJAbIbQRf1+8KZAayfSImZRauQkCbztyfn3YHPsMwVYcZuU+UDlqUH1VWtMICKq/QmO4LQNfE0DtyyBSe75CxEamu0si4QzrZCwvV1ZX1QK/IHe1NnF9Xt4ZQaJn1itrSxwUfqJfJ3KSxgoQtxq2lnMcZgqaFD15EWCo3j/018QsIJzJa9buLnqS9UdAn4t07QjOjBSjEuyjMmqwrIw14xnvmXnG3Sj4I+4G3FhahnSMSTeXXkgisdaScus0Xsh5ENWV/UyU50RwKmmMbGZJ0aAo3wsJSSMs5WqK24V3B3aAguCGikyZvFEohQcftbZvySC/zA/WiaJJTL17jAgMBAAGjggFJMIIBRTASBgNVHRMBAf8ECDAGAQH/AgEAMA4GA1UdDwEB/wQEAwIBhjAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5kaWdpY2VydC5jb20wSwYDVR0fBEQwQjBAoD6gPIY6aHR0cDovL2NybDQuZGlnaWNlcnQuY29tL0RpZ2lDZXJ0SGlnaEFzc3VyYW5jZUVWUm9vdENBLmNybDA9BgNVHSAENjA0MDIGBFUdIAAwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzAdBgNVHQ4EFgQUUWj/kK8CB3U8zNllZGKiErhZcjswHwYDVR0jBBgwFoAUsT7DaQP4v0cB1JgmGggC72NkK8MwDQYJKoZIhvcNAQELBQADggEBABiKlYkD5m3fXPwdaOpKj4PWUS+Na0QWnqxj9dJubISZi6qBcYRb7TROsLd5kinMLYBq8I4g4Xmk/gNHE+r1hspZcX30BJZr01lYPf7TMSVcGDiEo+afgv2MW5gxTs14nhr9hctJqvIni5ly/D6q1UEL2tU2ob8cbkdJf17ZSHwD2f2LSaCYJkJA69aSEaRkCldUxPUd1gJea6zuxICaEnL6VpPX/78whQYwvwt/Tv9XBZ0k7YXDK/umdaisLRbvfXknsuvCnQsH6qqF0wGjIChBWUMo0oHjqvbsezt3tkBigAVBRQHvFwY+3sAzm2fTYS5yh+Rp/BIAV0AecPUeybQ=";
//              String hdfc3="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";
            //            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur/Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82oXfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+mMQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3bkQIDAQAB
//
//                    MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4BIOjwfiVxPgfbb743Sp
//            07Yx+yxo5UP8smqQkvgvjg8YP2iel07YXsTo7MO2B/Kfbf28jo/8KRe1UIXsh0j/
//                    aou0lbyCW9Q28kr5OtOMJ/HIT3YYL7UV6t132R+gek/htc2UljcsYOyrYv/1A9Ur
//                    /Z+HF2d1065N/DzXH8qLRaD/JUVOnFLu3PN6WK8U/TOhkaKYe5c07PxCQnHGS82o
//            XfESjvEwv2BoVkUfAK4jW2GIn1ey/iTxuXFH/lCqRjdU9+1/xBX75XlzEuNQDI+m
//            MQEVg7eGIGAMBIYVsn1ip4hprmlXknDZA27wLBwGVpHoiqa8ZzwvrrlC/0JCFK3b
//                    kQIDAQAB
//            String ss =SSLPem.replaceAll("\\r\\n", "");
//            String ss1 =SSLPem.replaceAll("\r\n", "");
//          // PublicKey puc =getpublickey(hdfc2);
//      //    PublicKey puc=getPublicKey(publicKey);
//            PublicKey publicKey1= getpublickey(publicKey);
//            String str=new String( asenc.encrypt(bb,ss.getBytes()));
//            String str2=new String( asenc.encrypt(bb2,ss.getBytes()));
            //   String b= String.valueOf(asenc.encrypt(bb,ss.getBytes()));

            //  Log.d("bbbb",b.toString());

            // public JSONObject getpacket() {r
            //            //  JSONObject json = new JSONObject();


            String hdfc2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmqobFF8ZipqOBWiaPdZ89nw8eouuz1jPFuZFm5rHeemvM80AI8cDItlo2RFfDLDPkXLEyAPZijxDsEm8Jka2Y3O2M1AcEfCCfX5vzxwlIz+GlYCDnMQjf4V2IPmhnQlvV46anWczRA70OLqrBcY7/Cpe4BJdKXQof85aJarDS6jEEg8rWJmMS0i6niQD4CsJu6pPI3YDnRYvcTCvipr9Q4Cmi0WPbJxxLjVJEyVsNxDOGvbk9KXr+yRkOntG0tbBlmMmNhZzzgO+HacbRKrx2SDcDEDz0udJe0MWD9MRKyMUR/7oybyaNpWQTbJLelm6oKe1MpOwpmn6O7xCunt+DQIDAQAB";
            String hdfcdecyptkey = "MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQI8t5EZyrfMXcCAggAMBQGCCqGSIb3DQMHBAhzVRMGvCB1PgSCBMiRDcq/YZd24ukSM30kNRhC3FcMcLLIyy8zvOGquAzqff5Q1Kl03tZWlpzyi4idDJvVTIZWy+SxUZB0GSK7CykdnnhM4Y1BWE5X20eWGJGyRUzgfiJjyn5YuHg05Wh86RoPm/QBOL4rH1DhNP2lLNdMjYZkFc7fKnmzVEQmTfIKZko4CqqUMtmThudMN2Kv8FMmO2ziJ/6Tk58CufV1BSk4A0J8WfS+xVoLxY4kad+G1IuXvku73uAxNCyenkJ4Cs9cXyX8p+F03ZT2w/n7+HJtIC9kjKeY5HdXFvy/T222s78UEp/c96up+BawtYkYwABa5b4ZyD3/4BfT8NY3rdou5X/uxkt2gtcEFebD72JxANPAGsn+Xyc8C/P1I5ayN/PXv5vmxhWE3vcgq2k6XAinS/8pyGIzGUv4w0o7Sk25GmtIt+DHDw78kLjVS77mWXx7jHj0w7OBZJFeA7WcJPR3VHry6BlSgO+dpaRaWmiXIq088C0vbitvhyMIZDNcm4PZb63E+dNFJH/M7DbaZZsQhYVTUgy4pmwL/VkS8BYkZoWlONfqberfJ36urWt7Ghdr5EUgC15y5qXGAuZUJ3TLDPING3PkAyQWvC8USZUbfcnhgn/SQyVriaJqJAkSXNUsbvpjSdKx8KC7h0qquB5wjr3bPOFsTHuKjuW37CO0EtNeYZCqFPZtORi2rWuQwKtqaO4iRrVOlWAIuAiowWw0U5/86iMX08vO++qWLkjZSo9pA8EfNKZgUcEfJCSIiHT4WSiKMs88+kwhp6Zg42/P7LU5erdmEWcFhXEKlTH/bhI0zmkd4ZtD4b/B1YdkBbVpdljergSUbe0KtIQ7SRqKVgcxVhAn8KEk/ZpTNexMaGcPgMYLIRj+gAPkSNcGuOkiJ/okRE+xzhAx070jLMVJix0TfH7ZyqQBPMLu0wsTA5CCrfWEeLOvMdaZl/f2y21W5qURamCknfHz6C0OZUu/OJed3BsZk5kHyKgqjX8vevesoonN22pGpSTxkUa0lY9f2qrz+IeixZmfL4XwPNoLoDaAbwgXG1+X1RDY4vq26W5NRzTrwES/X1j5ju62fAoI795SV4lO6GJ3FCERm8TwfudWpA8XVzL9WJarIcYEC0H0kA9GToMD2ptXktkT6K5jZKMn9rtM7oLA3kJ487RSTLq2sPR8fYC/OTIKWU4Wt2akN3A7gOecKNmpy+SNRIPkSLWrli9DV6u7AOiUIonnio9Cok/v+xIk447BHSEinPpq2mDmZIuoQoiVE+XlkSBX8O+mRAYcvnz0csMXswjKRzDizvju8S3vNpLdhDrn8RxjqMzRsAp99+/2Tb77SxgvscfCjZxLujyWmaYzr7KahDm9gXZdba08Lnb+tZZRxRSB/tIEAMfPokSVKtspgrvg2zi2i1W20jsbsoUc9eZtvqlJpCg7yUw/egrzpQmIrbzeDv6WXvd9MiSFRGuv1puWwgL9VygxavNVb2HPNufXCLogKMN/mGAoyLgFu0SUPg1DLwsi0GgwmevMcbAaf10ATtmcvY21fbxTdtGMY6DmPSgHduF9MzO9ZxwfCOEm7SBJSta6DzChObIRAeI72fh86LUzRyG4uONP2pV7ofwpvt3nkyhErH4=";
            //String hdfcdecyptkey= "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";

            //  byte[] key1 = Base64.getDecoder().decode(hdfc2);
            PublicKey puc = getPublicKey(org.apache.axis.encoding.Base64.decode(hdfc2));
            // PublicKey puc=getPublicKey(key1);
            // byte[] bb=rsaEncrypterDecrypter.rsaEncrypt(key.getBytes(),puc);

            System.out.println("Byte 32 String: " + byte32);
            byte[] getSymmetricKeyEncryptedValue = RSAEncrypterDecrypter.rsaEncrypt(byte32.getBytes(), puc);
            String encryptedItem = org.apache.axis.encoding.Base64.encode(getSymmetricKeyEncryptedValue);

            System.out.println("SymmetricKeyEncryptedValue: " + encryptedItem);

            //
            //   String s=new String(bb);
            //     byte[] b= rsaEncrypterDecrypter.decrypt(bb,key1);

            try {
                JSONObject jobj = new JSONObject()
                        .put("RequestEncryptedValue", ssa)
                        .put("SymmetricKeyEncryptedValue", encryptedItem)
                        .put("Scope", "ECOMEXPRESS")
                        .put("TransactionId", Req_Date_Time)
                        .put("OAuthTokenValue", "");
                Log.d("final payload", jobj.toString());
                //     getResponseFromicicioap(jobj,getActivity());


                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... voids) {
                        String ss=getResponseFromicicioap(jobj,getActivity());
                        return ss;
                        //getResponseFromicicioap(jobj, getActivity());
                        // return edsEkycXMLViewModel.getResponseFromJsonURL1(xml_pid, webheaderMap, url);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {
                            JSONObject jobj = null;

                            jobj = new JSONObject(s);
                            Log.d("HHDDFFCC",s);
                            String encrypteddatakey = jobj.getString("GWSymmetricKeyEncryptedValue");
                            PrivateKey puc = getRsaPrivateKey();
                            byte[] bb = rsaEncrypterDecrypter.decryptkey(encrypteddatakey, puc);
                            String encrypteddata = jobj.getString("ResponseEncryptedValue");
                            AESEncrypterDecrypter asenc = new AESEncrypterDecrypter();
                            String bb1 = AESEncrypterDecrypter.decrypt1(encrypteddata, bb);
                            Log.d("",bb1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {

                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }

//                           Log.e("response", s);
//                            edsEkycXMLViewModel.setCheckStatus(s);
//                            JSONObject jobj = null;

                        try {

                        } catch (Exception e) {
                            getBaseActivity().showSnackbar("PostExecute():-Data not synced properly.Please sync data and try again..");
                        }
                    }


                }.execute();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            //    getResponseFromicicioap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String loadStringFromAssets(Context mActivity, String fileName) {
        String string = null;
        if (mActivity != null) {
            try {
                InputStream is = mActivity.getResources().getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                string = new String(buffer, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                //SathiLogger.e(ex.getMessage());
                return null;
            }
        }
        return string;
    }


//    public void setsoap() {
//        try {
//
//            MessageFactory factory;
//            factory = MessageFactory.newInstance();
//            SOAPMessage soapMsg = factory.createMessage();
//            SOAPPart part = soapMsg.getSOAPPart();
//
//            SOAPEnvelope envelope = part.getEnvelope();
//            SOAPHeader header = envelope.getHeader();
//            SOAPBody body = envelope.getBody();
//
//            header.addTextNode("Training Details");
//
//            SOAPBodyElement element = body.addBodyElement(envelope.createName("JAVA", "training", "https://jitendrazaa.com/blog"));
//            element.addChildElement("WS").addTextNode("Training on Web service");
//
//            SOAPBodyElement element1 = body.addBodyElement(envelope.createName("JAVA", "training", "https://jitendrazaa.com/blog"));
//            element1.addChildElement("Spring").addTextNode("Training on Spring 3.0");
//
//            soapMsg.writeTo(System.out);
//
//            FileOutputStream fOut = new FileOutputStream("SoapMessage.xml");
//            soapMsg.writeTo(fOut);
//
//            System.out.println();
//            System.out.println("SOAP msg created");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    protected void setSecuritySection() throws SOAPException {
//
//        String METHODNAME = "setSecuritySection";
//       // KeyPairGenerator kpg;
//
//        //SOAPFactory soapFactory, SOAPEnvelope envelope
//SOAPFactory soapFactory=SOAPFactory.newInstance();
//
//       // MessageFactory msgFactory = MessageFactory.newInstance();
//        SOAPMessage message = soapFactory.createMessage();
//        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
//
//        SOAPHeader soapHeader = envelope.getHeader();
//        try {
//            Name securityName = soapFactory.createName("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd");
//            SOAPElement securityElement = soapHeader.addHeaderElement(securityName);
//            // SOAPHeaderElement securityElement =
//            // soapHeader.addHeaderElement(securityName);
//            // securityElement.setMustUnderstand(mustUnderstand);
//
//            Name binarySecurityToken = soapFactory.createName("BinarySecurityToken", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd");
//            SOAPElement binarySecurityTokenElement = securityElement.addChildElement(binarySecurityToken);
//
////            Certificate cert;
////
////            String trustStoreLocation = ServerInformation.getValueForWebsphereVariable("EHA_TRUSTSTORE");
////            String trustStorePwd = ServerInformation.getValueForWebsphereVariable("EHA_TRUSTSTORE_PWD");
////
////            InputStream path = new FileInputStream(trustStoreLocation);
////            KeyStore ks = KeyStore.getInstance("JKS");
////            ks.load(path, new String(new BASE64Decoder().decodeBuffer(trustStorePwd)).toCharArray());
////
////            cert = ks.getCertificate("test");
////            binarySecurityTokenElement.addTextNode(new BASE64Encoder().encode(cert.getEncoded()));
////            kpg = KeyPairGenerator.getInstance("DSA");
////
//            Name idToken = soapFactory.createName("Id", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wsswssecurity-secext-1.0.xsd");
//            SOAPElement idElement = binarySecurityTokenElement.addChildElement(idToken);
//            idElement.addTextNode("test");
//
//            Name valueTypeToken = soapFactory.createName("ValueType", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
//            SOAPElement valueTypeElement = binarySecurityTokenElement.addChildElement(valueTypeToken);
//            valueTypeElement.addTextNode("X509v3");
//
//            Name encodingTypeToken = soapFactory.createName("EncodingType", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
//            SOAPElement encodingTypeElement = binarySecurityTokenElement.addChildElement(encodingTypeToken);
//            encodingTypeElement.addTextNode("Base64Binary");
//
//
//            Name signatureToken = soapFactory.createName("Signature", "ds", "http://www.w3.org/2000/09/xmldsig#");
//            SOAPHeaderElement signElement = soapHeader.addHeaderElement(signatureToken);
//
//            Name id1 = soapFactory.createName("Id");
//            signElement.addAttribute(id1, "XWSSGID-13130564207092015610708");
//
//            Name signedInfo = soapFactory.createName("SignedInfo");
//            SOAPElement signInfoElement = signElement.addChildElement(signedInfo);
//            //SOAPHeaderElement signInfoElement = soapHeader.addHeaderElement(signedInfo);
//
//            Name canonicalToken = soapFactory.createName("CanonicalizationMethod");
//            SOAPElement canonicalTokenTokenElement = signInfoElement.addChildElement(canonicalToken);
//
//            Name alg = soapFactory.createName("Algorithm");
//            canonicalTokenTokenElement.addAttribute(alg, "http://www.w3.org/2001/10/xml-exc-c14n#");
//
//            Name InclusiveNamespaceToken = soapFactory.createName("InclusiveNamespaces", "wsse", "http://www.w3.org/2001/10/xml-exc-c14n#");
//            SOAPElement element = canonicalTokenTokenElement.addChildElement(InclusiveNamespaceToken);
//
//            Name prefixList = soapFactory.createName("PrefixList");
//            element.addAttribute(prefixList, "wsse SOAP-ENV");
//
//            Name signatureMethodToken = soapFactory.createName("SignatureMethod", "ds", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
//            SOAPElement signatureMethodTokenElement = signInfoElement.addChildElement(signatureMethodToken);
//            Name alg2 = soapFactory.createName("Algorithm");
//            signatureMethodTokenElement.addAttribute(alg2, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
//
//            Name referenceToken = soapFactory.createName("Reference", "ds", "#XWSSGID-1313056421405-433059543");
//            SOAPElement referenceTokenElement = signatureMethodTokenElement.addChildElement(referenceToken);
//            Name uri = soapFactory.createName("URI");
//            referenceTokenElement.addAttribute(uri, "#XWSSGID-1313056421405-433059543");
//
//            Name digestMethodAlgToken = soapFactory.createName("DigestMethod");
//            SOAPElement digestMethodAlgTokenElement = referenceTokenElement.addChildElement(digestMethodAlgToken);
//            Name alg3 = soapFactory.createName("Algorithm");
//            digestMethodAlgTokenElement.addAttribute(alg3, "http://www.w3.org/2000/09/xmldsig#sha1");
//
//            Name digestValueToken = soapFactory.createName("DigestValue", "ds", "3wCcYA8m7LN0TLchG80s6zUaTJE=");
//            SOAPElement digestValueTokenElement = referenceTokenElement.addChildElement(digestValueToken);
//            digestValueTokenElement.addTextNode("3wCcYA8m7LN0TLchG80s6zUaTJE=");
//
//            Name signValueToken = soapFactory.createName("SignatureValue");
//            SOAPElement signValueElement = signElement.addChildElement(signValueToken);
//            signValueElement.addTextNode("QlYfURFjcYPu41G31bXgP4JbFdg6kWH+8ofrY+oc22FvLqVMUW3zdtvZN==");
//
//            Name keyInfoToken = soapFactory.createName("KeyInfo");
//            SOAPElement keyInfoElement = signElement.addChildElement(keyInfoToken);
//
//            Name securityRefToken = soapFactory.createName("SecurityTokenReference", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
//            SOAPElement securityRefElement = keyInfoElement.addChildElement(securityRefToken);
//            Name id2 = soapFactory.createName("Id", "wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
//            securityRefElement.addAttribute(id2, "XWSSGID-1313056421331317573418");
//
//            Name referenceURIToken = soapFactory.createName("Reference", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-tokenprofile1.0#X509v3");
//            SOAPElement refElement = securityRefElement.addChildElement(referenceURIToken);
//            Name uri1 = soapFactory.createName("URI");
//            refElement.addAttribute(uri1, "#XWSSGID-1313056420712-845854837");
//            Name valType = soapFactory.createName("ValueType");
//            refElement.addAttribute(valType, "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
//
//        } catch (Exception ex) {
//            throw new SOAPException(ex);
//        }
//
//
//    }
//    public static PublicKey getpublickey(String filename)
//            throws Exception {
//
//        //byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
//
////        X509EncodedKeySpec spec =
////                new X509EncodedKeySpec(filename.getBytes());
////        KeyFactory kf = KeyFactory.getInstance("RSA");
////        return kf.generatePublic(spec);
//
//
//        X509EncodedKeySpec ks = new X509EncodedKeySpec(filename.getBytes());
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PublicKey pub = kf.generatePublic(ks);
//        return pub;
//
//    }

//    public static PublicKey getPublicKey(String base64PublicKey){
//        PublicKey publicKey = null;
//        try{
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            publicKey = keyFactory.generatePublic(keySpec);
//            return publicKey;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return publicKey;
//    }

    public String readFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        FileInputStream fileStream = new FileInputStream(new File(path));
        BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.lineSeparator());
        }

        return sb.toString();
    }

    public static PublicKey getPublicKey(byte[] base64PublicKey) {
        try {
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64PublicKey.getBytes());
//        KeyFactory kf = null;
//
//            kf = KeyFactory.getInstance("RSA");
//
//
//       // PrivateKey p= kf.generatePrivate(keySpec);
//        PrivateKey myPrivateKey = readPemRsaPrivateKey(base64PublicKey);
//        RSAPrivateCrtKey privk = (RSAPrivateCrtKey)myPrivateKey;
//
//        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey myPublicKey = keyFactory.generatePublic(publicKeySpec);


//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getEncoder().encode(base64PublicKey.getBytes()));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            PublicKey   publicKey = keyFactory.generatePublic(keySpec);
            // byte[] encoded = base64PublicKey.getBytes();
            // KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(base64PublicKey));
            //  RSAPrivateKey prtKey=(RSAPrivateKey)kf.generatePrivate(new X509EncodedKeySpec(base64PublicKey));
//            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(base64PublicKey);
//            PrivateKey pvtkey =kf.generatePrivate(privKeySpec);
            //  return null;
            //return publicKey;
            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException ine) {
            ine.printStackTrace();

        }


//        PublicKey publicKey = null;
//        try{
//
//            Base64EncoderDecoder bsd=new Base64EncoderDecoder();
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getEncoder().encode(base64PublicKey.getBytes()));
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            publicKey = keyFactory.generatePublic(keySpec);
//            return publicKey;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public static PrivateKey getRsaPrivateKey() throws
            java.io.IOException,
            java.security.NoSuchAlgorithmException,
            java.security.spec.InvalidKeySpecException {
//        String pemString = File2String(pemFilename);
//
//        pemString = pemString.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
//        pemString = pemString.replace("-----END RSA PRIVATE KEY-----", "");
//
//        byte[] decoded = pemFilename.getBytes();
//
//       // PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pemFilename.getBytes()));
//       // RSAPublicKeySpec keySpec = new RSAPublicKeySpec(decoded);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey p=kf.generatePrivate(keySpec);
//
//        return kf.generatePrivate(keySpec);


//        String SSLPem = loadStringFromAssets(EdsTaskListActivity.this, " samplefileencrypted.key");
//        String hdfcdecyptkey ="MIIFDjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQI8t5EZyrfMXcCAggAMBQGCCqGSIb3DQMHBAhzVRMGvCB1PgSCBMiRDcq/YZd24ukSM30kNRhC3FcMcLLIyy8zvOGquAzqff5Q1Kl03tZWlpzyi4idDJvVTIZWy+SxUZB0GSK7CykdnnhM4Y1BWE5X20eWGJGyRUzgfiJjyn5YuHg05Wh86RoPm/QBOL4rH1DhNP2lLNdMjYZkFc7fKnmzVEQmTfIKZko4CqqUMtmThudMN2Kv8FMmO2ziJ/6Tk58CufV1BSk4A0J8WfS+xVoLxY4kad+G1IuXvku73uAxNCyenkJ4Cs9cXyX8p+F03ZT2w/n7+HJtIC9kjKeY5HdXFvy/T222s78UEp/c96up+BawtYkYwABa5b4ZyD3/4BfT8NY3rdou5X/uxkt2gtcEFebD72JxANPAGsn+Xyc8C/P1I5ayN/PXv5vmxhWE3vcgq2k6XAinS/8pyGIzGUv4w0o7Sk25GmtIt+DHDw78kLjVS77mWXx7jHj0w7OBZJFeA7WcJPR3VHry6BlSgO+dpaRaWmiXIq088C0vbitvhyMIZDNcm4PZb63E+dNFJH/M7DbaZZsQhYVTUgy4pmwL/VkS8BYkZoWlONfqberfJ36urWt7Ghdr5EUgC15y5qXGAuZUJ3TLDPING3PkAyQWvC8USZUbfcnhgn/SQyVriaJqJAkSXNUsbvpjSdKx8KC7h0qquB5wjr3bPOFsTHuKjuW37CO0EtNeYZCqFPZtORi2rWuQwKtqaO4iRrVOlWAIuAiowWw0U5/86iMX08vO++qWLkjZSo9pA8EfNKZgUcEfJCSIiHT4WSiKMs88+kwhp6Zg42/P7LU5erdmEWcFhXEKlTH/bhI0zmkd4ZtD4b/B1YdkBbVpdljergSUbe0KtIQ7SRqKVgcxVhAn8KEk/ZpTNexMaGcPgMYLIRj+gAPkSNcGuOkiJ/okRE+xzhAx070jLMVJix0TfH7ZyqQBPMLu0wsTA5CCrfWEeLOvMdaZl/f2y21W5qURamCknfHz6C0OZUu/OJed3BsZk5kHyKgqjX8vevesoonN22pGpSTxkUa0lY9f2qrz+IeixZmfL4XwPNoLoDaAbwgXG1+X1RDY4vq26W5NRzTrwES/X1j5ju62fAoI795SV4lO6GJ3FCERm8TwfudWpA8XVzL9WJarIcYEC0H0kA9GToMD2ptXktkT6K5jZKMn9rtM7oLA3kJ487RSTLq2sPR8fYC/OTIKWU4Wt2akN3A7gOecKNmpy+SNRIPkSLWrli9DV6u7AOiUIonnio9Cok/v+xIk447BHSEinPpq2mDmZIuoQoiVE+XlkSBX8O+mRAYcvnz0csMXswjKRzDizvju8S3vNpLdhDrn8RxjqMzRsAp99+/2Tb77SxgvscfCjZxLujyWmaYzr7KahDm9gXZdba08Lnb+tZZRxRSB/tIEAMfPokSVKtspgrvg2zi2i1W20jsbsoUc9eZtvqlJpCg7yUw/egrzpQmIrbzeDv6WXvd9MiSFRGuv1puWwgL9VygxavNVb2HPNufXCLogKMN/mGAoyLgFu0SUPg1DLwsi0GgwmevMcbAaf10ATtmcvY21fbxTdtGMY6DmPSgHduF9MzO9ZxwfCOEm7SBJSta6DzChObIRAeI72fh86LUzRyG4uONP2pV7ofwpvt3nkyhErH4=";
//        //String hdfcdecyptkey= "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";

        String hdfcrsa = "MIIEogIBAAKCAQEAwa2rAXJ+aeCNVWRkTPTZzQJopaQMu9Rsic8ajrCwW9qU2xj5yMMjaoMd/jjXe3Rx0mHrZ9RRcd1nfZUwg9aZr6MEmEd+aEcQzoRk9CukX/IvEwLBsIx4UNlDtwxYNQvRiVbffax2vZJQXGfnt63WepFC1KdjuhHBuFDuvCK/hlyEdhLGT/dWPCBFwNBEmpiWkSSYqzEFa2rU/XuYdUDa831HP8VfLOPiqkSgxDc3Wc4IFO+EUuqC2fRqDk9w3Fl00vZLo0blszy33QK6ymEP3idmzhAWnLmk10OPvrRqFES9/EwJZgtrHgZT2vDQGR1FDPi7EwAxjxnFqxKOmGzYlwIDAQABAoIBAFwBgVQNrOnlJ57iMxev1Wujck0F0prD/c+1l9sjKpRoEQDIK5jowoFNykjDffICzjNwGuFXJl2eHRS2c7adkqKyIXOpuu4UnHJOyJqJxOtjZGN2ksao5Fsb6yQg9CI2/BJvN3o/HKVPxWcwYQ6LwXC2YvYckq6I0usZX1/2bd+IbfeSMdcngoCQetmOY8AVkCf2Zrctf4tXn8vCJxe5D/sINTp8TyVXVFtfYaQHXwm3H2fj0eEVO9FNoC7Lnrr0ZZ3JmECBQ0xmXLkCXCdXieo/pAlgIRFKLi3vOtkLLYyQlJMgWaX49U2DRoEiUwJXsW9UVWtOncwLveMXMrK1yGECgYEA6pyW9Bum45tw4wbzYAWD1uD2oCUy6N/QnStx+DVVspTwGhsbDVjShB4uopkFq9EO4/nWj60suZq5UEeF9Qayy61atkmhNIKWYeFEWFtf9ATSHXxBnqlPgjXzBKf6pgHhpdJaZ6lUEA7eVREayssXdq5Ls7qINJotmCK/VBUN+mcCgYEA01XFDt6Dyy2hzFbVeTJ0UfvTGnqPqlLr3oJ3U8/KmQ/VS9dmS6oHcE160g7uhUSHcnXuyGr44DmrKkBjIWETVitUPf+rVaghLuwNzptYGHRm8Dm8r++0Pk6dXMR/YRaJ3Robfu+JShVgSc9MH5iD7daXNzi6fCQuxlaCwgzTslECgYAWJ9WFlfrrsak2d1iSb9LkfvgrZXfqUACWZxzrMLY4GCKYhIjIeOPggC0tQ8AqWqLKWrStetAncvofH7IB0rEd1PAlGMz1dYOYGLrokTB9jdr919cmkZylkkyx2t8rwC+8BiJteasrnQvYAraCAi3kEk+p2Gq2dPzrShTBbv+T7wKBgAyePWLS5FmYQuXZdWuEGK/gck1NbhCEXPLvJULK0FTtPhYGKWRxJXqOFbw/CfzJB7FM21H6GRwCNXcTWxfeHPJzKdd90XQO0tRjYbupSMxE/vRu7hYvILrrpLeqP9TDLL4X1fZxROg4eIRPrULhr1bfDi5M482Pt38+IRaG/4jRAoGAHffOE2HdDPSVPTyUe1/QoebyZjJVN3rE5zwUNggFjUIJEbR6BS5lPXjlIKLYmqP7UrlBZc5Wo8IuCY76h3+9DjYJsIDrcEjTJpmHiOrAKXnBSlQfZfJ+wwFuReHwFby7IZ1vMZs43K5rxoiDcW8JKBYzTgFyr05uJMgjEJGUQbU=";
        //  byte[] key1 = Base64.getDecoder().decode(hdfc2);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(org.apache.axis.encoding.Base64.decode(hdfcrsa));

        PrivateKey pvt;
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("RSA");

            pvt = kf.generatePrivate(ks);
            Log.d("PVT Key GEN", pvt.toString());

            return pvt;
        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
        }
        return null;
    }


    public static PublicKey getpublickey(String filename)
            throws Exception {
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        kpg.initialize(2048);
//        KeyPair kp = kpg.generateKeyPair();
//        Key pub = kp.getPublic();

//        byte[] keyBytes = Base64.get.decode(filename, Base64.DEFAULT);
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
//        serveruk=keyFactory.generatePublic(spec);
//        return serveruk;

        //  byte[] keyBytes = Files.readAllBytes(filename.getBytes()));

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(filename.getBytes());

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(spec);
        return kf.generatePublic(spec);
    }
   /* public PublicKey getPrivateKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
//       // RSAPublicKeySpec rsa =new RSAPublicKey()
//      // RSAPublicKey spec = new RSAPublicKey(keyBytes)
//        KeyFactory kf =
//                KeyFactory.getInstance("RSA");
       // PKCS1EncodedKeySpec KeySpec = new PKCS1EncodedKeySpec(keyBytes);
      //  RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic((java.security.spec.KeySpec) KeySpec);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
       // byte[] keyBytes = Base64.getDecoder().decode(pub.getBytes("UTF-8"));
        PKCS1EncodedKeySpec KeySpec = new PKCS1EncodedKeySpec(keyBytes);
        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic((java.security.spec.KeySpec) KeySpec);

        return null;
    }
*/
//    private PublicKey getPublicKey(String publicKeyFilePath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        byte[] keyBytes = new byte[(int) publicKeyFilePath.length()];
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//
//
//        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(keyBytes), new BigInteger(exponentByte));
//        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);
//      //  Log.d("public",kf.generatePublic(spec))
//        return kf.generatePublic(spec);
//    }


//    public static PublicKey getPublicKey(String base64PublicKey){
//        PublicKey publicKey = null;
//        try{
//            byte[] bytesArray = new byte[(int) base64PublicKey.length()];
//            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bytesArray,base64PublicKey);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            publicKey = keyFactory.generatePublic(keySpec);
//            return publicKey;
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//        return publicKey;
//    }

    //
    public PublicKey getPublicKey()
            throws Exception {
        //  byte[] bytesArray = new byte[(int) base64PublicKey.length()];


//        String pub = "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";
//
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        byte[] b = pub.getBytes();
//       // byte[] keyBytes = Base64.getDecoder().decode(pub.getBytes("UTF-8"));
//     //   PKCS1EncodedKeySpec KeySpec = new PKCS1EncodedKeySpec(b);
//
//
//
//        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic((java.security.spec.KeySpec) KeySpec);
        return null;


//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        byte[] keyBytes = Base64.getDecoder().decode(pub.getBytes("UTF-8"));
//        PKCS1EncodedKeySpec KeySpec = new PKCS1EncodedKeySpec(keyBytes);
//        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic((java.security.spec.KeySpec) KeySpec);
//
//
//
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        byte[] keyBytes = Base64().getDecoder.decode(publicKey.getBytes()); //assuming base64 encoded key
//        PKCS1EncodedKeySpec KeySpec = new PKCS1EncodedKeySpec(keyBytes);
//        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(KeySpec);
//
//
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PrivateKey prv_recovered = kf.generatePrivate(new PKCS8EncodedKeySpec(prvBytes));
//        PublicKey  pub_recovered =  kf.generatePublic(new RSAPublicKeySpec(bytesArray));
//        RSAPublicKey pkcs1PublicKey =RSAPublicKey.getInstance(decoded);
//        BigInteger modulus = pkcs1PublicKey.getModulus();
//        BigInteger publicExponent = pkcs1PublicKey.getPublicExponent();
//        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        PublicKey generatedPublic = kf.generatePublic(keySpec);
//        PublicKey fakePublicKey=null;
//        PKCS8EncodedKeySpec rsa -pubin -in openapiuat_hdfcbank_com.pem -text -nooutspec = new PKCS8EncodedKeySpec(bytesArray);
//        KeyFactory factory = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = factory.generatePrivate(spec);
//        Cipher cipher = Cipher.getInstance("RSA");
//
//            //For IBM JDK, 原因请看解密方法中的说明
//            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
//            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
//             fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
//
//       // }
//
//
//        return fakePublicKey;
    }

    //    public static PublicKey getPublicKey(Context ctx) {
//        try {//from  w ww.ja  v a2  s  .  c  o m
//            InputStream is = ctx.getAssets().open("openapiuat_hdfcbank_com.pem");
//            byte[] fileBytes = new byte[is.available()];
//            is.read(fileBytes);
//            is.close();
//            X509EncodedKeySpec spec = new X509EncodedKeySpec(fileBytes);
//            KeyFactory kf = KeyFactory.getInstance("RSA");
//            return kf.generatePublic(spec);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public byte[] encrypt(byte[] data, PublicKey key) {
//        byte[] encryptedValue = null;
//        try {
//            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            cipher.init(Cipher.ENCRYPT_MODE,key);
//            encryptedValue = cipher.doFinal(data);
//        }
//        catch(NoSuchAlgorithmException exp) {
//            //TODO handle exception
//            exp.printStackTrace();
//        }
//        catch(NoSuchPaddingException exp) {
//            //TODO handle exception
//            exp.printStackTrace();
//        }
//        catch(IllegalBlockSizeException exp) {
//            //TODO handle exception
//            exp.printStackTrace();
//        }
//        catch(BadPaddingException exp) {
//            //TODO handle exception
//            exp.printStackTrace();
//        }
//        catch(InvalidKeyException exp) {
//            //TODO handle exception
//            exp.printStackTrace();
//        }
//        return encryptedValue;
//    }
    public String generateAlphaNumericKey(int keySize) {
        //Blob cryptoKey = Crypto.generateAesKey(128);
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        while (key.length() < keySize) {
            int ch = random.nextInt(128);
            //is within range
            if ((ch <= Z && ch >= A) || (ch <= z && ch >= a) || (ch <= NINE && ch >= ZERO)) {
                key.append((char) ch);
            }
        }
        return key.toString();
    }

    private static String encrypt_data(byte[] content, Key key)
            throws Exception {
        // String key = "bad8deadcafef00d";
        byte[] encrypted = null;
        // SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


        cipher.init(Cipher.ENCRYPT_MODE, key);

//        System.out.println("Base64 encoded: "
//                + Base64.encode(data.getBytes()).length);
        encrypted = cipher.doFinal(content);
        // byte[] original = Base64.encode(cipher.doFinal(data.getBytes()));
        return new String(encrypted);
    }


    public static String getResponseFromicicioap(JSONObject url, Context con) {
        String server_response = null;
        DefaultHttpClient httpClient = null;
        //if (CommonUtility.isNotEmpty(url)) {
        try {
            /************** For getting response from HTTP URL start ***************/
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            Log.d("ASYNC","IN");
            final Resources resources = con.getResources();
            InputStream in = resources.openRawResource(R.raw.sathi);
            keyStore.load(in, new String((Constants.PFX_PW.getBytes())).toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(keyStore, Constants.PFX_PW.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagers, null, null);

            URL object = new URL("https://openapiuat.hdfcbank.com:9443/API/AadhaarEKYC/Biometric_Auth_ADV_V2");
            // "https://openapiuat.hdfcbank.com:9443/API/AadhaarEKYC/Biometric_Auth_ADV_V2"
            //URL object = new URL("https://openapiuat.hdfcbank.com:9443/API/API/AadhaarEK
            //
            //
            //
            //
            //
            //
            //
            // YC/Biometric_Auth_ADV_V2");
            //   https://openapiuat.hdfcbank.com:9443/API/AadhaarEKYC/Biometric_Auth_ADV_V2
            HttpsURLConnection connection = (HttpsURLConnection) object
                    .openConnection();
            if (connection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) connection)
                        .setSSLSocketFactory(sslContext.getSocketFactory());
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("apikey", "l72db6f53070e7458b8f40b42350541287");

            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(url.toString());
            wr.flush();
            wr.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(connection.getInputStream());
                Log.d("ecom1", responseCode + server_response);
            } else {
                server_response = connection.getResponseMessage();
                server_response = readStream(connection.getInputStream());
                Log.d("ecom2", responseCode + server_response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ecx", e.getMessage());
            Log.d("ecom3", server_response+e.getMessage());
            return server_response;

        }
        Log.d("ecom4", server_response);
        return server_response;
    }


    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
