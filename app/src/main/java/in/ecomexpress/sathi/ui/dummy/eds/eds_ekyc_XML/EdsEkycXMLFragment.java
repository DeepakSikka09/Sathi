package in.ecomexpress.sathi.ui.dummy.eds.eds_ekyc_XML;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import dagger.hilt.android.AndroidEntryPoint;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentEkycBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.ui.dummy.eds.uid.DeviceInfo;
import in.ecomexpress.sathi.utils.Constants;

@AndroidEntryPoint
public class EdsEkycXMLFragment extends BaseFragment<FragmentEkycBinding, EdsEkycXMLViewModel> implements IEdsEkycXMLFragmentNavigator, ActivityData {
    FragmentEkycBinding fragmentEdsEkycXMLBinding;
    EdsWithActivityList edsWithActivityList;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    List<Map<String, Object>> questionfromfield;
    //  JSONObject pidDataJson;
    @Inject
    EdsEkycXMLViewModel edsEkycXMLViewModel;
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
    private Serializer serializer = null;
    private ArrayList<String> positions;
    String xml_pid;
    JSONObject pidDataJson;
    boolean isKycCompleted;

    public static EdsEkycXMLFragment newInstance() {
        EdsEkycXMLFragment fragment = new EdsEkycXMLFragment();
        return fragment;
    }

    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {
        if (!isKycCompleted){
        return true;
    }
        getBaseActivity().showSnackbar("Ekyc  completed Succesfully ,Please complete or Cancel");
        return false;
    }

    @Override
    public void setImageValidation() {

    }

    @Override
    public EdsEkycXMLViewModel getViewModel() {
        return edsEkycXMLViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_ekyc;
    }

    @Override
    public void ongetPid() {

        try {
            String q = edsActivityWizard.getQuestion_form_dummy();
            // Log.d("santosh", q);
            ObjectMapper Obj = new ObjectMapper();
//
            try {
                // Displaying JSON String
                // System.out.println(q);
                //  Log.d("JSON String", "jsonStr");
                JSONObject jObject = null;

                jObject = new JSONObject(q);


                if (jObject.optString("isAdharVerificationRequired", "").equalsIgnoreCase("true")) {
                    //fragmentEdsEkycXMLBinding.llAdharGroup.setVisibility(View.VISIBLE);
                    if (fragmentEdsEkycXMLBinding.inputAdhar.getText().length() == 12) {
                        EDSDetailActivity.edsDetailActivity.scanMantra();
                    } else {
                        getBaseActivity().showSnackbar(getString(R.string.Invalid_adhaar_entered));
                    }
                } else {
                    EDSDetailActivity.edsDetailActivity.scanMantra();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar("getPIDData():-Data not synced properly.Please sync data and try again..");
        }
        ///fragmentIciciEkycBinding.iciciLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }

    //                FragmentComunicator fragmentCommunicator;
//                IciciResponse Response;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        edsEkycXMLViewModel.setNavigator(this);
        // Log.d(TAG, "onCreate: " + this.toString());
        positions = new ArrayList<>();
        serializer = new Persister();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentEdsEkycXMLBinding = getViewDataBinding();
        fragmentEdsEkycXMLBinding.editUrn.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentEdsEkycXMLBinding.inputAdhar.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        try {
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                edsEkycXMLViewModel.setData(edsActivityWizard, masterActivityData);
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

                    if (jObject.optString("isUrnRequired", "").equalsIgnoreCase("true")) {
                        fragmentEdsEkycXMLBinding.llUrnGroup.setVisibility(View.VISIBLE);
                        fragmentEdsEkycXMLBinding.layoutScan.setVisibility(View.GONE);
                    } else {
                        fragmentEdsEkycXMLBinding.llUrnGroup.setVisibility(View.GONE);
                        fragmentEdsEkycXMLBinding.layoutScan.setVisibility(View.VISIBLE);

                    }
                    if (jObject.optString("isAdharVerificationRequired", "").equalsIgnoreCase("true")) {
                        fragmentEdsEkycXMLBinding.llAdharGroup.setVisibility(View.VISIBLE);
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

    @Override
    public void sendData(String pidData) {
        Log.e("DataValue", pidData);
        try {
            // JsonObject jobj =new JsonObject();
//                    XmlToJson xmlToJson = new XmlToJson.Builder(pidData).build();
            XmlToJson xmlToJson = new XmlToJson.Builder(pidData).build();
            pidDataJson = new JSONObject(String.valueOf(xmlToJson));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream stream = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                stream = new ByteArrayInputStream(pidData.getBytes(StandardCharsets.UTF_8));
            }
            Document oldDoc = builder.parse(stream);
            Node oldRoot = oldDoc.getDocumentElement();
            Document newDoc = builder.newDocument();
            org.w3c.dom.Element newRoot = newDoc.createElement("Data");
            newDoc.appendChild(newRoot);
            // newDoc.appendChild(oldRoot);

            org.w3c.dom.Element element = newDoc.getDocumentElement();
            Node node = newDoc.createElement("EcomData");
            element.appendChild(node);


            org.w3c.dom.Element awb = newDoc.createElement("AWB");
            awb.appendChild(newDoc.createTextNode(String.valueOf(edsWithActivityList.edsResponse.awbNo)));
            node.appendChild(awb);

            org.w3c.dom.Element adhaar = newDoc.createElement("Adhaar");
            adhaar.appendChild(newDoc.createTextNode(fragmentEdsEkycXMLBinding.inputAdhar.getText().toString()));
            node.appendChild(adhaar);

            org.w3c.dom.Element agentUserId = newDoc.createElement("AgentUserID");
            agentUserId.appendChild(newDoc.createTextNode(edsEkycXMLViewModel.getDataManager().getCode()));
            node.appendChild(agentUserId);

            org.w3c.dom.Element RequestId = newDoc.createElement("RequestId");
            RequestId.appendChild(newDoc.createTextNode(String.valueOf(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())));
            node.appendChild(RequestId);

            org.w3c.dom.Element Time_Stamp = newDoc.createElement("Time_Stamp");
            Time_Stamp.appendChild(newDoc.createTextNode(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));
            node.appendChild(Time_Stamp);

            newRoot.appendChild(newDoc.importNode(oldRoot, true));

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer out = new StringWriter();
            tf.transform(new DOMSource(newDoc), new StreamResult(out));
            xml_pid = out.toString();

            Gson gson = new Gson();
            String jsonInString = gson.toJson(edsActivityWizard);
            ObjectMapper oMapper = new ObjectMapper();
            //QuestionFormField q=new QuestionFormField();
            String q = edsActivityWizard.getQuestion_form_dummy();
            //Log.d("santosh", q);
            ObjectMapper Obj = new ObjectMapper();
            // Displaying JSON String
            System.out.println(q);
            // Log.e("JSON String", "jsonStr");
            JSONObject jObject = new JSONObject(q);
            String url=jObject.getString("generic_url");
            HashMap<String, String> webheaderMap = new HashMap<String, String>();
            Iterator<?> keys = jObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();

                if (key.contains("Web_header")) {
                    webheaderMap.put(key, jObject.get(key).toString());
                }
            }
            Log.e("webheaderMap", webheaderMap.toString());
            Log.e("xml_pid", xml_pid);
            JSONObject jobj_piddata = pidDataJson.getJSONObject("PidData");
            JSONObject jobj_resp = jobj_piddata.getJSONObject("Resp");
            if (!jobj_resp.getString("errCode").equalsIgnoreCase("0")) {
                try {
                    getBaseActivity().showSnackbar(jobj_resp.getString("errInfo"));
                } catch (Exception e) {
                    getBaseActivity().showSnackbar("errorInfo():-Data not synced properly.Please sync data and try again..");
                }

            } else {

                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... voids) {
                        return EdsEkycXMLViewModel.getResponseFromJsonURL1(xml_pid, webheaderMap, url);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        Log.e("response", s);
                        edsEkycXMLViewModel.setCheckStatus(s);
                        JSONObject jobj = null;

                        try {
                            jobj = new JSONObject(s);
                            if (jobj.getString("EKYC").equalsIgnoreCase("yes")) {
                                isKycCompleted = true;
                                fragmentEdsEkycXMLBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciSuccessText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciSuccessText.setText(jobj.getString("reason"));

                            } else if ((jobj.getString("EKYC").equalsIgnoreCase("no"))) {
                                fragmentEdsEkycXMLBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciErrorText.setText(jobj.getString("reason"));

                            } else {
                                fragmentEdsEkycXMLBinding.fingureImgLayout.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciErrorText.setVisibility(View.VISIBLE);
                                fragmentEdsEkycXMLBinding.iciciErrorText.setText("Server Error ");
                            }
                        } catch (Exception e) {
                            getBaseActivity().showSnackbar("PostExecute():-Data not synced properly.Please sync data and try again..");
                        }
                    }


                }.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void validateurn() {
        try {
            if (isNetworkConnected()) {
                if (fragmentEdsEkycXMLBinding.editUrn.getText().toString().equals(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())) {

                    fragmentEdsEkycXMLBinding.layoutScan.setVisibility(View.VISIBLE);
                } else {
                    getBaseActivity().showSnackbar(getString(R.string.Invalid_urn_entered));
                }
                // edsEkycXMLViewModel.login(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
            } else {
                getBaseActivity().showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar("validatereturn():-Data not synced properly.Please sync data and try again..");
        }
        // edsEkycXMLViewModel.validateurn();
        //  getBaseActivity().showSnackbar(getString(R.string.check_internet));

    }

    @Override
    public void validateAdhaar() {
        try {
            if (isNetworkConnected()) {
                if (fragmentEdsEkycXMLBinding.inputAdhar.getText().toString().equals(edsWithActivityList.edsResponse.getShipmentDetail().getOrderNo())) {

                    fragmentEdsEkycXMLBinding.layoutScan.setVisibility(View.VISIBLE);
                } else {
                    getBaseActivity().showSnackbar(getString(R.string.Invalid_urn_entered));
                }
                // edsEkycXMLViewModel.login(mActivityLoginBinding.etEmail.getText().toString(), mActivityLoginBinding.etPassword.getText().toString(), deviceDetails);
            } else {
                getBaseActivity().showSnackbar(getString(R.string.check_internet));
            }
        } catch (Exception e) {
            getBaseActivity().showSnackbar("ValidateAdhaar():-Data not synced properly.Please sync data and try again..");
        }
    }
    @Override
    public void getData(BaseFragment fragment) {
        boolean activityStatus = false;
        try {
            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("false");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (!edsEkycXMLViewModel.inputData.get().trim().equalsIgnoreCase("")) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value("true");
             //   edsActivityResponseWizard.setInputRemark(fragmentEdsEkycXMLBinding.etRemarks.getText().toString());
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            EDSDetailActivity.edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);

        } catch (Exception e) {
            getBaseActivity().showSnackbar("getData():-Data not synced properly.Please sync data and try again..");
        }


    }
    @Override
    public boolean validateData() {
        if (isKycCompleted){
            return true;
        }
        getBaseActivity().showSnackbar("Ekyc is not completed Succesfully ,Please complete or Cancel");
        return false;
    }
    public void showMessage(String s) {
        // Log.d(TAG, "showMessage: " + s);
    }
}
