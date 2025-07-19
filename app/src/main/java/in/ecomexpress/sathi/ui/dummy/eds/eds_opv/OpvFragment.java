package in.ecomexpress.sathi.ui.dummy.eds.eds_opv;


import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.EdsOpvFragmentBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.data.eds.EDSCommitAdditionalInfo;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.GeneralQuestion;
import in.ecomexpress.sathi.repo.remote.model.masterdata.ImageSetting;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.utils.Constants;
import in.ecomexpress.sathi.utils.ImageHandler;
import in.ecomexpress.sathi.utils.InputFilterMinMax;

import static in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity.edsDetailActivity;
import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;


@AndroidEntryPoint
public class OpvFragment extends BaseFragment<EdsOpvFragmentBinding, OpvFragmentViewModel> implements IOpvFragmentNavigation, ActivityData {

    EdsOpvFragmentBinding edsOpvFragmentBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    OpvFragmentViewModel opvFragmentViewModel;
    @Inject
    MasterActivityData masterActivityData;
    @Inject
    EDSActivityWizard edsActivityWizard;


    JSONArray jsonArray;
    //JSONArray jArray;
    ArrayList<String> value;
    HashMap<Integer, ArrayList<String>> data = new HashMap<>();
    EditText[] input_edt;
    EditText auto_edt;
    EditText[] yyedt;
    EditText[] mmedt;
    int autoEdtCount = 0;
   int yymmCount=0;
        String yymmcount;
    EditText[] fix_edt;
    ImageView[] imagview;
    Spinner[] spinner;
    int img = 0;
    int image_view_id;
    ArrayList<String> inner_value;
    boolean getDataFlag = false;
    List<EDSCommitAdditionalInfo> edsCommitAdditionalInfos = new ArrayList<>();
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    int t_m_count = 0, m_count = 0;
    int input_edit_Count = 0, fix_edit_count = 0, selection_spinner_count = 0, img_image_count = 0, img_mandate = 0,yymm_count=0;
    int inputCount = 0, selectionCount = 0, fixCount = 0, imgCount = 0, autoCount = 0,ymCount=0;
    String[] input_param;
    String[] fix_param;
    String[] selection_param;
    String[] yymm_param;
    String[] img_param;
    String auto_param;

    HashMap<ImageView, Integer> imagevalue = new HashMap<>();
    HashMap<String, String> imageAnswer = new HashMap<>();
    HashMap<ImageView, String> imageMan = new HashMap<>();
    LinearLayout main_layout;
    LinearLayout.LayoutParams params;
    private ImageHandler imageHandler;

    public static OpvFragment newInstance() {

        OpvFragment fragment = new OpvFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.eds_opv_fragment;
    }

    @Override
    public OpvFragmentViewModel getViewModel() {
        return opvFragmentViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        opvFragmentViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edsOpvFragmentBinding = getViewDataBinding();

        if (getArguments() != null) {
            main_layout = getActivity().findViewById(R.id.layout);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            try {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                opvFragmentViewModel.getOpvMasterData();
                opvFragmentViewModel.setData(edsActivityWizard, masterActivityData);

            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }

        }


    }


    @Override
    public void onResume() {
        super.onResume();
        // documentListViewModel.getListDetail();
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
            if (getDataFlag) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value("true");
                edsActivityResponseWizard.setInputRemark("");
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(edsCommitAdditionalInfos);

            }
            edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
    }

    @Override
    public boolean validateData() {
        edsCommitAdditionalInfos.clear();
        m_count = 0;
       // yymmcount                =yyedt.getText().toString()+mmedt.getText().toString();
        if (autoEdtCount != 0) {
            if (auto_edt.getTag().equals("M")) {
                m_count++;
            }
            try {
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(auto_param);
                edsCommitAdditionalInfo.setQuesValue(auto_edt.getText().toString());
                edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);
            } catch (Exception j) {
                j.printStackTrace();
                getBaseActivity().showSnackbar(j.getMessage());
            }
        }
        for (int i = 0; i < input_param.length; i++) {
            try {
                if (input_edt[i].getTag().equals("M") && !input_edt[i].getText().toString().isEmpty()) {
                    m_count++;
                }
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(input_param[i]);
                edsCommitAdditionalInfo.setQuesValue(input_edt[i].getText().toString());
                edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);


            } catch (Exception j) {
                j.printStackTrace();
                getBaseActivity().showSnackbar(j.getMessage());
            }
        }
        for (int i = 0; i < fix_param.length; i++) {
            try {
                if (fix_edt[i].getTag().equals("M") && !fix_edt[i].getText().toString().isEmpty()) {
                    m_count++;
                }
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(fix_param[i]);
                edsCommitAdditionalInfo.setQuesValue(fix_edt[i].getText().toString());
                edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);

            } catch (Exception j) {
                j.printStackTrace();
                getBaseActivity().showSnackbar(j.getMessage());
            }
        }
        for (int i = 0; i < selection_param.length; i++) {
            try {
                if (spinner[i].getTag().equals("M")) {
                    if (!spinner[i].getSelectedItem().toString().contains("SELECT"))
                        m_count++;
                }
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(selection_param[i]);
                edsCommitAdditionalInfo.setQuesValue(spinner[i].getSelectedItem().toString());
                edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);

            } catch (Exception j) {
                j.printStackTrace();
                getBaseActivity().showSnackbar(j.getMessage());
            }
        }
        for (int i = 0; i < yymm_param.length; i++) {
            try {
                if (yyedt[i].getTag().equals("M") && !yyedt[i].getText().toString().isEmpty()|| !mmedt[i].getText().toString().isEmpty()) {
                    m_count++;
                }
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(yymm_param[i]);
                if(yyedt[i].getText().toString().isEmpty()&& !mmedt[i].getText().toString().isEmpty()) {
                    yyedt[i].setText("0");
                    edsCommitAdditionalInfo.setQuesValue(yyedt[i].getText().toString() + "Year(S)" + mmedt[i].getText().toString() + "Month");
                }
               else if(!yyedt[i].getText().toString().isEmpty()&& mmedt[i].getText().toString().isEmpty()) {
                    mmedt[i].setText("0");
                    edsCommitAdditionalInfo.setQuesValue(yyedt[i].getText().toString() + "Year(s)" + mmedt[i].getText().toString() + "Month");
                }
               else {
                    edsCommitAdditionalInfo.setQuesValue(yyedt[i].getText().toString() + "Year(s)" + mmedt[i].getText().toString() + "Month");
                }
               edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);

            } catch (Exception j) {
                j.printStackTrace();
                getBaseActivity().showSnackbar(j.getMessage());
            }
        }
        try {
            Iterator<Integer> iter = imagevalue.values().iterator();
            while (iter.hasNext()) {
                int value = iter.next();
                if (value == 1)
                    m_count++;
            }
            //Get the set
            Set set = (Set) imageAnswer.entrySet();
            //Create iterator on Set
            Iterator iterator = set.iterator();
            System.out.println("Display Values.");

            while (iterator.hasNext()) {
                Map.Entry mapEntry = (Map.Entry) iterator.next();
                // Get Key
                String keyValue = (String) mapEntry.getKey();
                //Get Value
                String value = (String) mapEntry.getValue();
                EDSCommitAdditionalInfo edsCommitAdditionalInfo = new EDSCommitAdditionalInfo();
                edsCommitAdditionalInfo.setQuesId(keyValue);
                edsCommitAdditionalInfo.setQuesValue(value);
                edsCommitAdditionalInfos.add(edsCommitAdditionalInfo);
            }


            } catch (Exception j) {
            j.printStackTrace();
            getBaseActivity().showSnackbar(j.getMessage());

        }

        if (t_m_count == m_count) {
            getDataFlag = true;
            return true;
        } else {
            getDataFlag = false;
            getBaseActivity().showSnackbar("Please fill all mandatory fields");
            return false;
        }
        // return false;
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
        if (imageMan.get(imagview[image_view_id]).equalsIgnoreCase("M"))
            imagevalue.put(imagview[image_view_id], 1);

    }

    @Override
    public EDSActivityWizard getActivityWizard() {
        return edsActivityWizard;
    }

    @Override
    public void shareOPVMasterData(List<GeneralQuestion> generalQuestions) {
        String drsQuestionId = edsActivityWizard.getQuestionId();

        try {
            jsonArray = new JSONArray();
           /* int[] arr = Arrays.stream(drsQuestionId.substring(1, drsQuestionId.length() - 1).split(","))
                    .map(String::trim).mapToInt(Integer::parseInt).toArray();*/
            String[] items = drsQuestionId.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

            int[] arr = new int[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    arr[i] = Integer.parseInt(items[i]);
                    Log.e("Arrr", Arrays.toString(arr));
                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                }
            }

            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < generalQuestions.size(); j++) {
                    if (arr[i] == generalQuestions.get(j).getId()) {
                        jsonArray.put(new JSONObject().put("qtag", generalQuestions.get(j).getQuestionTag()).put("type", generalQuestions.get(j).getType()).
                                put("id", generalQuestions.get(j).getId()).put("option", generalQuestions.get(j).getOption()).put("ans_type", generalQuestions.get(j).getAnswerType())
                                .put("value_array", generalQuestions.get(j).getValueArray()));

                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar(e.getMessage());
        }
        getpacket();

    }

    @Override
    public void showError(String e) {
        getBaseActivity().showSnackbar(e);
    }


    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);
    }

    private void getpacket() {
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                value = new ArrayList<>();
                try {
                    JSONObject j = jsonArray.getJSONObject(i);
                    value.add(j.getString("qtag"));
                    value.add(j.getString("type"));
                    value.add(j.getString("id"));
                    value.add(j.getString("option"));
                    value.add(j.getString("ans_type"));
                    value.add(j.getString("value_array"));
                    data.put(i, value);
                    getCount(j.getString("type"));
                    if (j.getString("option").equalsIgnoreCase("M")) {
                        t_m_count++;
                    }
                } catch (JSONException j) {

                }

            }

        } catch (Exception j) {
            j.printStackTrace();
            getBaseActivity().showSnackbar(j.getMessage());
        }

        //for input
        input_param = new String[inputCount];
        input_edt = new EditText[inputCount];
        //for FIX
        fix_param = new String[fixCount];
        fix_edt = new EditText[fixCount];
        //for Spinner
        selection_param = new String[selectionCount];
        spinner = new Spinner[selectionCount];

        //for ImageView
        img_param = new String[imgCount];
        imagview = new ImageView[imgCount];
        yymm_param = new String[ymCount];
        yyedt = new EditText[ymCount];
        mmedt = new EditText[ymCount];
        for (int j = 0; j < data.size(); j++) {
            inner_value = new ArrayList<>();
            inner_value = data.get(j);


            if (inner_value.get(1).equalsIgnoreCase("SELECTION")) {
                LinearLayout linear_spinner_layout = new LinearLayout(getActivity());

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;

                linear_spinner_layout.setOrientation(LinearLayout.HORIZONTAL);
                selection_param[selection_spinner_count] = inner_value.get(2);
                TextView title_tv = new TextView(getActivity());

                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                    title_tv.setTextColor(getResources().getColor(R.color.black));
                } else {
                    title_tv.setText(inner_value.get(0));
                    title_tv.setTextColor(getResources().getColor(R.color.black));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextSize(16);
                title_tv.setLayoutParams(lp1);


                List<String> status_value = new ArrayList<>();
                //String[] strArray = inner_value.get(5).replaceAll("\\s","").split("[^a-zA-Z0-9+-<> ]+");
                String[] valArr = inner_value.get(5).split(",");
                for (int i = 0; i < valArr.length; i++) {
                    // System.out.println("valArr ==>"+valArr[i]);
                    valArr[i] = valArr[i].replace("[", "").replace("]", "").replaceAll(" ", "").replaceAll("\"", "");
                    // System.out.println("update valArr ==>"+valArr[i].trim());
                }
                List<String> list = new ArrayList<>(Arrays.asList(valArr));
                list.removeAll(Arrays.asList(",", null));
                valArr = list.toArray(new String[list.size()]);
                for (int i = 0; i < valArr.length; i++) {
                    status_value.add(valArr[i]);
                }
                spinner[selection_spinner_count] = new Spinner(getActivity());
                spinner[selection_spinner_count].setTag(inner_value.get(3));

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, status_value);
                spinner[selection_spinner_count].setAdapter(spinnerArrayAdapter);
                spinner[selection_spinner_count].setBackgroundResource(R.drawable.bg_rts_pending);
                spinner[selection_spinner_count].setLayoutParams(lp2);
                // spinner[selection_spinner_count].setOnItemSelectedListener(get);

                linear_spinner_layout.setWeightSum(2f);
                linear_spinner_layout.addView(title_tv);
                linear_spinner_layout.addView(spinner[selection_spinner_count]);

                main_layout.addView(linear_spinner_layout);
                selection_spinner_count++;


            } else if (inner_value.get(1).equalsIgnoreCase("INPUT")) {
                LinearLayout linear_edit_layout = new LinearLayout(getActivity());
                input_param[input_edit_Count] = inner_value.get(2);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;
                linear_edit_layout.setOrientation(LinearLayout.HORIZONTAL);

                TextView title_tv = new TextView(getActivity());
                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                } else {
                    title_tv.setText(inner_value.get(0));
                }

                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);
                title_tv.setLayoutParams(lp1);

                input_edt[input_edit_Count] = new EditText(getActivity());

                if (inner_value.get(4).equalsIgnoreCase("INTEGER")) {
                    input_edt[input_edit_Count].setHint("Mobile No");
                    input_edt[input_edit_Count].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    input_edt[input_edit_Count].setPadding(5, 15, 5, 15);
                    input_edt[input_edit_Count].setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    input_edt[input_edit_Count].setHint("Enter Field..");
                    input_edt[input_edit_Count].setPadding(5, 15, 5, 15);

                }
                input_edt[input_edit_Count].setTag(inner_value.get(3));
                input_edt[input_edit_Count].setBackgroundResource(R.drawable.bg_rts_pending);
                input_edt[input_edit_Count].setLayoutParams(lp2);

                linear_edit_layout.setWeightSum(2f);
                linear_edit_layout.addView(title_tv);
                linear_edit_layout.addView(input_edt[input_edit_Count]);

                main_layout.addView(linear_edit_layout);
                input_edit_Count++;


            } else if (inner_value.get(1).equalsIgnoreCase("FIX")) {
                LinearLayout linear_edit_layout = new LinearLayout(getActivity());

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;
                fix_param[fix_edit_count] = inner_value.get(2);
                linear_edit_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView title_tv = new TextView(getActivity());
                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                } else {
                    title_tv.setText(inner_value.get(0));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);
                title_tv.setLayoutParams(lp1);

                fix_edt[fix_edit_count] = new EditText(getActivity());
                fix_edt[fix_edit_count].setTag(inner_value.get(3));
                fix_edt[fix_edit_count].setText(edsWithActivityList.getEdsResponse().getConsigneeDetail().getName());
                fix_edt[fix_edit_count].setPadding(5, 15, 5, 15);
                // input_edt.setEnabled(false);
                fix_edt[fix_edit_count].setFocusable(false);
                fix_edt[fix_edit_count].setBackgroundResource(R.drawable.bg_rts_pending);
                fix_edt[fix_edit_count].setLayoutParams(lp2);

                linear_edit_layout.setWeightSum(2f);
                linear_edit_layout.addView(title_tv);
                linear_edit_layout.addView(fix_edt[fix_edit_count]);


                main_layout.addView(linear_edit_layout);
                fix_edit_count++;


            } else if (inner_value.get(1).equalsIgnoreCase("IMG")) {
                LinearLayout linear_img_layout = new LinearLayout(getActivity());

                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;
                img_param[img_image_count] = inner_value.get(2);
                linear_img_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView title_tv = new TextView(getActivity());
                imagview[img_image_count] = new ImageView(getActivity());
                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                    imagevalue.put(imagview[img_image_count], 0);
                    imageMan.put(imagview[img_image_count], "M");
                    img_mandate++;
                } else {
                    imagevalue.put(imagview[img_image_count], 0);
                    imageMan.put(imagview[img_image_count], "O");
                    img_mandate++;
                    title_tv.setText(inner_value.get(0));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);
                title_tv.setLayoutParams(lp1);


                imagview[img_image_count].setMaxHeight(Integer.valueOf(inner_value.get(2)));
                imagview[img_image_count].setTag(inner_value.get(2));
                imagview[img_image_count].setId(img_image_count);
                imagview[img_image_count].setBaseline(Integer.valueOf(inner_value.get(2)));
                imagview[img_image_count].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImageSetting imageSetting = new ImageSetting();
                        imageSetting.setWaterMark(opvFragmentViewModel.setWaterMarkDetail(masterActivityData.getImageSettings().getWaterMark()));
                        imageSetting.setMax(masterActivityData.getImageSettings().getMax());
                        imageSetting.setMin(masterActivityData.getImageSettings().getMin());
                        masterActivityData.setImageSettings(imageSetting);
                        imageAnswer.put(Integer.toString(view.getBaseline()),"EDS_" + opvFragmentViewModel.masterActivityData.get().getCode() + "_Q" + view.getId());

                        edsDetailActivity.imageHandler.captureImage(opvFragmentViewModel.edsActivityWizard.get().getAwbNo() + "_" + edsWithActivityList.edsResponse.getDrsNo() + "_" + "EDS_" + opvFragmentViewModel.masterActivityData.get().getCode() + "_Q" + view.getId() + ".png", imagview[view.getId()], "EDS_" + opvFragmentViewModel.masterActivityData.get().getCode() + "_Q" + view.getId(), masterActivityData.imageSettings.getWaterMark());
                        image_view_id = view.getId();
//                        if (imageMan.get(imagview[view.getId()]).equalsIgnoreCase("M"))
//                            imagevalue.put(imagview[view.getId()], 1);

                    }
                });
                // imagview[img_image_count].setImageDrawable(getResources(getDrawable(R.drawable.camera)));
                imagview[img_image_count].setBackgroundResource(R.drawable.bg_rts_pending);
                imagview[img_image_count].setImageResource(R.drawable.cam);
                imagview[img_image_count].setLayoutParams(lp2);

                linear_img_layout.setWeightSum(2f);
                linear_img_layout.addView(title_tv);
                linear_img_layout.addView(imagview[img_image_count]);


                main_layout.addView(linear_img_layout);
                img_image_count++;


            } else if (inner_value.get(1).equalsIgnoreCase("TRUE/FALSE")) {
                LinearLayout linear_radio_layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;
                linear_radio_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView title_tv = new TextView(getActivity());
                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                } else {
                    title_tv.setText(inner_value.get(0));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_HORIZONTAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);
                title_tv.setLayoutParams(lp1);

                RadioGroup rg = new RadioGroup(getActivity());
                rg.setOrientation(RadioGroup.HORIZONTAL);

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rg.setLayoutParams(lp);

                RadioButton rb_yes = new RadioButton(getActivity());
                rb_yes.setText("Yes");
                rb_yes.setTextColor(Color.BLACK);
                rg.addView(rb_yes);

                RadioButton rb_no = new RadioButton(getActivity());
                rb_no.setText("No");

                rb_no.setTextColor(Color.BLACK);
                rg.addView(rb_no);
                rg.setLayoutParams(lp2);

                linear_radio_layout.setWeightSum(2f);
                linear_radio_layout.addView(title_tv);
                linear_radio_layout.addView(rg);

                main_layout.addView(linear_radio_layout);


            } else if (inner_value.get(1).equalsIgnoreCase("AUTO")) {
                final LinearLayout linear_edit_layout = new LinearLayout(getActivity());
                final LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                final LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

                lp1.weight = (float) .8;
                lp2.weight = (float) 1.2;
                autoEdtCount++;

                auto_param = inner_value.get(2);
                linear_edit_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView title_tv = new TextView(getActivity());
                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                } else {
                    title_tv.setText(inner_value.get(0));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);

                title_tv.setLayoutParams(lp1);


                auto_edt = new EditText(getActivity());
                auto_edt.setTag(inner_value.get(3));
                auto_edt.setText(new SimpleDateFormat("yyyy/MM/dd(HH:mm)").format(Calendar.getInstance().getTime()));
                auto_edt.setInputType(InputType.TYPE_CLASS_NUMBER);
                auto_edt.setPadding(5, 15, 5, 15);
                auto_edt.setBackgroundResource(R.drawable.bg_rts_pending);
                //input_edt.setBackground(getDrawable(R.drawable.button_bg));

                auto_edt.setFocusable(false);
                auto_edt.setLayoutParams(lp2);


                linear_edit_layout.setWeightSum(2f);
                linear_edit_layout.addView(title_tv);
                linear_edit_layout.addView(auto_edt);

                main_layout.addView(linear_edit_layout);


            }else if (inner_value.get(1).equalsIgnoreCase("YMINPUT")) {
                final LinearLayout linear_edit_layout = new LinearLayout(getActivity());
                final LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                final LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                final LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

                lp1.weight = (float) .8;
                lp2.weight = (float) .6;
                lp3.weight = (float) .6;
                yymmCount++;

                yymm_param[yymm_count] = inner_value.get(2);
                linear_edit_layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView title_tv = new TextView(getActivity());

                if (inner_value.get(3).equalsIgnoreCase("M")) {
                    SpannableStringBuilder builder = new SpannableStringBuilder(inner_value.get(0) + "*");
                    builder.setSpan(new ForegroundColorSpan(Color.RED), inner_value.get(0).length(), builder.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title_tv.setText(builder);
                } else {
                    title_tv.setText(inner_value.get(0));
                }
                title_tv.setBackgroundResource(R.drawable.bg_rts_pending);
                title_tv.setPadding(5, 15, 5, 15);
                title_tv.setGravity(Gravity.CENTER_VERTICAL);
                title_tv.setTextColor(getResources().getColor(R.color.black));
                title_tv.setTextSize(16);

                title_tv.setLayoutParams(lp1);


                yyedt[yymm_count] = new EditText(getActivity());
                yyedt[yymm_count].setTag(inner_value.get(3));
                yyedt[yymm_count].setHint("Enter Year");
                yyedt[yymm_count].setInputType(InputType.TYPE_CLASS_NUMBER);
                yyedt[yymm_count].setPadding(5, 15, 5, 15);
                yyedt[yymm_count].setBackgroundResource(R.drawable.bg_rts_pending);
                yyedt[yymm_count].setFilters(new InputFilter[]{ new InputFilterMinMax("0", "50")});
                //input_edt.setBackground(getDrawable(R.drawable.button_bg));

               // auto_edt.setFocusable(false);
                yyedt[yymm_count].setLayoutParams(lp2);


                mmedt[yymm_count] = new EditText(getActivity());
                mmedt[yymm_count].setTag(inner_value.get(3));
                mmedt[yymm_count].setHint("Enter Month");
                mmedt[yymm_count].setInputType(InputType.TYPE_CLASS_NUMBER);
                mmedt[yymm_count].setPadding(5, 15, 15, 15);
                mmedt[yymm_count].setBackgroundResource(R.drawable.bg_rts_pending);
                mmedt[yymm_count].setFilters(new InputFilter[]{ new InputFilterMinMax("0", "11")});

                //input_edt.setBackground(getDrawable(R.drawable.button_bg));

                // auto_edt.setFocusable(false)
              //  auto_edt.setLayoutParams(lp2);
                mmedt[yymm_count].setLayoutParams(lp3);



                linear_edit_layout.setWeightSum(2f);
                linear_edit_layout.addView(title_tv);
                linear_edit_layout.addView(yyedt[yymm_count]);
                linear_edit_layout.addView(mmedt[yymm_count]);


                main_layout.addView(linear_edit_layout);
                yymm_count++;

            }

        }
    }

    public void getCount(String data) {
        if (data.equalsIgnoreCase("INPUT")) {
            inputCount++;
        } else if (data.equalsIgnoreCase("FIX")) {
            fixCount++;
        } else if (data.equalsIgnoreCase("SELECTION")) {
            selectionCount++;
        } else if (data.equalsIgnoreCase("IMG")) {
            imgCount++;
        } else if (data.equalsIgnoreCase("AUTO")) {
            autoCount++;
        }else if (data.equalsIgnoreCase("YMINPUT")) {
            ymCount++;

    }
}
}