package in.ecomexpress.sathi.ui.dummy.eds.cash_collection;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import in.ecomexpress.sathi.BR;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.databinding.FragmentCashCollectionBinding;
import in.ecomexpress.sathi.repo.local.data.eds.EDSActivityResponseWizard;
import in.ecomexpress.sathi.repo.local.db.model.EdsWithActivityList;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseActivity;
import in.ecomexpress.sathi.ui.base.BaseFragment;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.ActivityData;
import in.ecomexpress.sathi.ui.dummy.eds.eds_activity_detail.EDSDetailActivity;
import in.ecomexpress.sathi.utils.Constants;

import static in.ecomexpress.sathi.ui.drs.forward.details.ForwardDetailViewModel.TAG;

/**
 * Created by dhananjayk on 11-11-2018.
 */
@AndroidEntryPoint
public class CashCollectionFragment extends BaseFragment<FragmentCashCollectionBinding, CashCollectionViewModel> implements ICashCollectionNavigation, ActivityData {

    FragmentCashCollectionBinding fragmentCashCollectionBinding;
    EdsWithActivityList edsWithActivityList;
    @Inject
    CashCollectionViewModel cashCollectionViewModel;
    EDSActivityResponseWizard edsActivityResponseWizard = new EDSActivityResponseWizard();
    MasterActivityData masterActivityData;
    EDSActivityWizard edsActivityWizard;
    double collectableAmount;
    double collectedAmount = 0;
    double mincollectableAmount;
    double collectableAmount_with_Decimal;
    private final boolean isImageCapture = false;
    private final String amount = "0";
    private double change;

    public static CashCollectionFragment newInstance() {
        CashCollectionFragment fragment = new CashCollectionFragment();
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_cash_collection;
    }

    @Override
    public CashCollectionViewModel getViewModel() {
        return cashCollectionViewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cashCollectionViewModel.setNavigator(this);
        Log.d(TAG, "onCreate: " + this.toString());
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentCashCollectionBinding = getViewDataBinding();
        fragmentCashCollectionBinding.etRemarks.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        fragmentCashCollectionBinding.etRemarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50) });
        fragmentCashCollectionBinding.etxtEnteredValue.setFilters(new InputFilter[]{BaseActivity.EMOJI_FILTER});
        try {
            if (getArguments() != null) {
                this.masterActivityData = getArguments().getParcelable(Constants.EDS_MASTER_LIST);
                this.edsActivityWizard = getArguments().getParcelable(Constants.EDS_ACTIVITY_LIST);
                this.edsWithActivityList = getArguments().getParcelable(Constants.EDS_DATA);
                try {
                    cashCollectionViewModel.setData(edsActivityWizard, masterActivityData);
                    if (!masterActivityData.getInstructions().isEmpty()) {
                        fragmentCashCollectionBinding.txtActivityInstruction.setClickable(true);
                        fragmentCashCollectionBinding.txtActivityInstruction.setMovementMethod(LinkMovementMethod.getInstance());
                        setTextViewHTML(fragmentCashCollectionBinding.txtActivityInstruction, masterActivityData.getInstructions());

                    }
                    if (!edsActivityWizard.getCustomerRemarks().isEmpty()) {
                        fragmentCashCollectionBinding.txtActivityRemark.setText(edsActivityWizard.getCustomerRemarks());
                    }
                } catch (Exception e) {
                    getBaseActivity().showSnackbar(e.getMessage());
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            getBaseActivity().showSnackbar("CashCollection onCreateView data not set.");
        }
        try {
            inItListener();
            collectableAmount_with_Decimal = Double.parseDouble("0" + edsActivityWizard.getActualValue());
            collectableAmount = Math.round(Double.parseDouble("0" + edsActivityWizard.getActualValue()));
        } catch (Exception e) {
            getBaseActivity().showSnackbar("CashCollection onCreateView inItListener data not set.");
        }
        fragmentCashCollectionBinding.etxtEnteredValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    collectedAmount = Double.parseDouble("0" + fragmentCashCollectionBinding.etxtEnteredValue.getText().toString());

                    fragmentCashCollectionBinding.returnAmountTv.setText(getString(R.string.return_amount_str) + " " + Double.valueOf(String.valueOf(change)).longValue()/*(new BigDecimal(String.valueOf(change)).toBigInteger()).longValue()*/);
                    fragmentCashCollectionBinding.returnAmountTv.setText(getString(R.string.error_collected_amount));
                    fragmentCashCollectionBinding.returnAmountTv.setText(getString(R.string.resheduled_amount) + " " + Double.valueOf(String.valueOf(change)).longValue() /*(new BigDecimal(String.valueOf(change)).toBigInteger()).longValue()*/);
                    fragmentCashCollectionBinding.returnAmountTv.setText(getString(R.string.error_min_amount));


                    int decimal_amt = (Constants.splitdecimal(collectableAmount_with_Decimal) < 49)
                            ? (0 - Constants.splitdecimal(collectableAmount_with_Decimal))
                            : (100 - Constants.splitdecimal(collectableAmount_with_Decimal));

                    if (collectedAmount < collectableAmount) {
                        change = collectableAmount - collectedAmount;
                        //Amount collected is Rs 1990000.0 less than total amount
                        fragmentCashCollectionBinding.returnAmountTv.setText("Amount collected is Rs. " + Double.valueOf(String.valueOf(change)).longValue()/*(new BigDecimal(String.valueOf(change)).toBigInteger()).longValue() */ + " less than total amount");
                        fragmentCashCollectionBinding.returnAmountTv.setTextColor(getResources().getColor(R.color.ecomRed));
                        fragmentCashCollectionBinding.returnAmountTv.setTypeface(fragmentCashCollectionBinding.returnAmountTv.getTypeface(), Typeface.BOLD);

                       /* fragmentCashCollectionBinding.returnAmountTv
                                .setText(getString(R.string.round_off_amount_str) + decimal_amt + " paise");*/
                    } else if (collectedAmount > collectableAmount || collectedAmount == collectableAmount) {
                        change = collectedAmount - collectableAmount;
                        //"Please return amount Rs 1990 to Consignee"
                        fragmentCashCollectionBinding.returnAmountTv.setText("Please return amount Rs." + Double.valueOf(String.valueOf(change)).longValue() /*(new BigDecimal(String.valueOf(change)).toBigInteger()).longValue()*/ + " to Consignee");
                        fragmentCashCollectionBinding.returnAmountTv.setTextColor(getResources().getColor(R.color.blue_ecom));
                        fragmentCashCollectionBinding.returnAmountTv.setTypeface(fragmentCashCollectionBinding.returnAmountTv.getTypeface(), Typeface.BOLD);
                        /*fragmentCashCollectionBinding.returnAmountTv
                                .setText(getString(R.string.round_off_amount_str) + decimal_amt + " paise");*/
                    } /*else if (collectedAmount < mincollectableAmount) {
                        fragmentCashCollectionBinding.etxtEnteredValue.setText(getString(R.string.error_min_amount));
                        fragmentCashCollectionBinding.etxtEnteredValue.setTextColor(getResources().getColor(R.color.ecomRed));
                        fragmentCashCollectionBinding.etxtEnteredValue.setTypeface(fragmentCashCollectionBinding.etxtEnteredValue.getTypeface(), Typeface.BOLD);
                        fragmentCashCollectionBinding.etxtEnteredValue
                                .setText(getString(R.string.round_off_amount_str) + decimal_amt + " paise");
                    }*/


                } catch (Exception e) {
                    getBaseActivity().showSnackbar("CashCollection onTextChange Listner data not set.");
                    // SathiLogger.e(e.getMessage());
                    //   Log.e(Constants.LOGGING_TAG, TAG + e.toString());
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void showMessage(String s) {
        Log.d(TAG, "showMessage: " + s);

    }


    private void inItListener() {
        try {
            fragmentCashCollectionBinding.txtActivityInstruction.setMovementMethod(LinkMovementMethod.getInstance());
            fragmentCashCollectionBinding.txtActivityInstruction.setText(Html.fromHtml(edsActivityWizard.getCustomerRemarks() + "<br>" + masterActivityData.getInstructions()));

            fragmentCashCollectionBinding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroup.findViewById(radioButtonID);
                    String checkValue = (String) radioButton.getText();
                    if (checkValue.equalsIgnoreCase("no")) {
                        fragmentCashCollectionBinding.returnAmountTv.setVisibility(View.GONE);
                        fragmentCashCollectionBinding.etxtEnteredValue.setText("");
                        fragmentCashCollectionBinding.etRemarks.requestFocus();
                    } else {
                        fragmentCashCollectionBinding.returnAmountTv.setText("");
                        fragmentCashCollectionBinding.returnAmountTv.setVisibility(View.VISIBLE);
                        fragmentCashCollectionBinding.etxtEnteredValue.requestFocus();


                    }
                    cashCollectionViewModel.setCheckStatus(checkValue);
                }
            });
        } catch (Exception e) {
            getBaseActivity().showSnackbar("CashCollection intListner data not set ");
        }
    }


    @Override
    public boolean validateData() {
        try {
            if (cashCollectionViewModel.inputData.get() != null && collectedAmount >= Double.valueOf(edsActivityWizard.getActualValue())) {
                Constants.EDS_CASH_COLLECTION=collectedAmount;
                return true;
            } else if (cashCollectionViewModel.inputData.get() == null || cashCollectionViewModel.inputData.get().isEmpty()) {
                getBaseActivity().showSnackbar("Select Yes or No");
            } else if (cashCollectionViewModel.inputData.get().equalsIgnoreCase("no")) {
                getBaseActivity().showSnackbar("Please Select Yes to Move Further");
                return false;
            } else if (collectedAmount < Double.valueOf(edsActivityWizard.getActualValue()) || fragmentCashCollectionBinding.etxtEnteredValue.getText().toString().isEmpty()) {
                if (Double.valueOf(edsActivityWizard.getActualValue()) > collectedAmount) {
                    getBaseActivity().showSnackbar("Amount Entered is less than Collectable amount");
                    return false;
                }


            }
        } catch (Exception ex) {
            getBaseActivity().showSnackbar("Not able to validate data in CashCollection Fragment");
        }

        return false;
    }


    @Override
    public void validate(boolean flag) {

    }

    @Override
    public boolean validateCancelData() {
        try {
            if (cashCollectionViewModel.inputData.get() != null && !cashCollectionViewModel.inputData.get().trim().isEmpty()) {
                if (cashCollectionViewModel.inputData.get().equalsIgnoreCase("NO"))
                    return true;
                else if (cashCollectionViewModel.inputData.get().equalsIgnoreCase("Yes")) {
                    return false;
                }


            } else {
                getBaseActivity().showSnackbar("Please select Yes or No");
                return false;
            }
        } catch (Exception ex) {
            getBaseActivity().showSnackbar("validateCancelData not set Properly..");

        }
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
    public void getData(BaseFragment fragment) {
        try {
            boolean activityStatus = false;

            edsActivityResponseWizard.setCode(edsActivityWizard.getCode());
            edsActivityResponseWizard.setInput_value("0");
            edsActivityResponseWizard.setInputRemark("");
            edsActivityResponseWizard.setIsDone("false");
            edsActivityResponseWizard.setActivityId("0");
            if (collectedAmount >= Double.valueOf(edsActivityWizard.getActualValue())) {
                activityStatus = true;
                edsActivityResponseWizard.setInput_value(edsActivityWizard.getActualValue());
                edsActivityResponseWizard.setInputRemark(fragmentCashCollectionBinding.etRemarks.getText().toString());
                edsActivityResponseWizard.setIsDone("true");
                edsActivityResponseWizard.setActivityId(edsActivityWizard.getActivityId());
                edsActivityResponseWizard.setAdditionalInfos(new ArrayList<>());

            }
            EDSDetailActivity.edsDetailActivity.getFragmentData(activityStatus, edsActivityResponseWizard, fragment);
        } catch (Exception e) {
            getBaseActivity().showSnackbar("getData not set Properly");
        }
    }

    @Override
    public void errorMsg(String message) {
        getBaseActivity().showSnackbar(message);
    }
}