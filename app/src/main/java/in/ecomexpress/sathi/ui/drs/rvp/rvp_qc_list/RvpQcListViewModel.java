package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_list;

import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.local.db.model.RvpWithQC;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.CommonUtils;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by Ashish Patel on 7/27/2018.
 */
@HiltViewModel
public class RvpQcListViewModel extends BaseViewModel<IRvpQcListNavigator> {
    public final ObservableField<RvpWithQC> rvpWithQC = new ObservableField<>();
    private final ObservableField<String> address = new ObservableField<>("");
    private final ObservableField<String> qcList = new ObservableField<>("");
    private final ObservableField<ArrayList<SampleQuestion>> sampleQuestionList = new ObservableField<>();
    private final ObservableField<String> consigneeName = new ObservableField<>("");
    private final ObservableField<String> itemName = new ObservableField<>("");
    private final ObservableField<String> awbNo = new ObservableField<>("");
    private final ObservableField<String> proceedWithQc = new ObservableField<>("PROCEED");
    private final ObservableField<Boolean> isQC = new ObservableField<>(false);
    public ObservableField<String> QcInstruction = new ObservableField<>("");

    @Inject
    public RvpQcListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void getRvpDataWithQc(String composite_key) {
        try {
            getCompositeDisposable().add(getDataManager().
                    getRvpWithQc(composite_key).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(rvpWithQC -> {
                        RvpQcListViewModel.this.rvpWithQC.set(rvpWithQC);
                        getNavigator().setHeaderVisibility(!rvpWithQC.rvpQualityCheckList.isEmpty());
                        if (rvpWithQC.drsReverseQCTypeResponse.getFlags().flagMap.getIs_mdc_rvp_qc_disabled().equalsIgnoreCase("true")) {
                            getMDCDisbaledCase();
                        } else {
                            getRvpMasterData();
                        }
                    }));
        } catch (Exception e) {
            Logger.e("RvpQcListActivityModule.class.getName()", e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }


    public ObservableField<String> getAddress() {
        try {
            if (rvpWithQC.get() != null && Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse != null)
                address.set(CommonUtils.nullToEmpty(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine1()) + " " + CommonUtils.nullToEmpty(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine2()) + " " + CommonUtils.nullToEmpty(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine3()) + " " + CommonUtils.nullToEmpty(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getLine4()) + " " + CommonUtils.nullToEmpty(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getCity()) + " " + Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getAddress().getPincode());
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return address;
    }


    // change
    private void getMDCDisbaledCase() {
        ArrayList<SampleQuestion> sampleQuestions = new ArrayList<>();
        try {
            for (int i = 0; i < Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.size(); i++) {
                SampleQuestion sampleQuestion = new SampleQuestion();
                sampleQuestion.setCode(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcCode());
                sampleQuestion.setName(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcName());
                sampleQuestion.setImageCaptureSettings(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getImageCaptureSettings());
                sampleQuestion.setInstructions(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getInstructions());
                sampleQuestion.setVerificationMode(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcType());
                sampleQuestions.add(sampleQuestion);
            }
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
        }
        RvpQcListViewModel.this.sampleQuestionList.set(new ArrayList<>(sampleQuestions));
        refreshData();
    }

    private void getRvpMasterData() {
        try {
            getCompositeDisposable().add(getDataManager().getRvpMasterDescriptions(Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList).subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).
                    subscribe(sampleQuestions -> {
                        try {
                            for (int i = 0; i < Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.size(); i++) {
                                for (int j = 0; j < sampleQuestions.size(); j++) {
                                    String code = Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcCode();
                                    if (code.equalsIgnoreCase(sampleQuestions.get(j).getCode())) {
                                        Collections.swap(sampleQuestions, i, j);
                                    }
                                    if (sampleQuestions.get(j).getCode().startsWith("GEN_ITEM_BRAND_CHECK")) {
                                        if (Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcCode().equalsIgnoreCase("GEN_ITEM_BRAND_CHECK")) {
                                            String s = sampleQuestions.get(j).getName().replace("#COLOR#", Objects.requireNonNull(rvpWithQC.get()).rvpQualityCheckList.get(i).getQcValue());
                                            sampleQuestions.get(j).setName(s);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
                        }
                        RvpQcListViewModel.this.sampleQuestionList.set(new ArrayList<>(sampleQuestions));
                        refreshData();
                    }));
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public ObservableField<String> getQCList() {
        try {
            if (sampleQuestionList.get() == null) {
                qcList.set("");
            } else {
                StringBuilder stringBuffer = new StringBuilder();
                int count = 1;
                List<SampleQuestion> questions = sampleQuestionList.get();
                int currentIndex = 0;
                for (SampleQuestion sampleQuestion : questions) {
                    stringBuffer.append(count++).append(". ").append(sampleQuestion.getName());
                    if (++currentIndex < questions.size()) {
                        stringBuffer.append("\n");
                    }
                }
                qcList.set(stringBuffer.toString());
            }
            return qcList;
        } catch (Exception e) {
            getNavigator().showError(e.getMessage());
        }
        return qcList;
    }

    public ObservableField<String> getQcInstruction() {
        try {
            if (sampleQuestionList.get() != null && !Objects.requireNonNull(sampleQuestionList.get()).isEmpty()) {
                QcInstruction.set("Click on “Proceed with QC” button if item is available with the consignee.\n\n" + " If not then go back and select Failed button. Mention the reason for pickup cancellation on next page");
            } else {
                QcInstruction.set("Click on “Proceed” button if item is available with the consignee.\n\n" + " If not then go back and select Failed button. Mention the reason for pickup cancellation on next page");
            }
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return QcInstruction;
    }

    private void refreshData() {
        try {
            getQCList();
            getAddress();
            getProceedWithQc();
            getQcInstruction();
            consigneeName.set(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getConsigneeDetails().getName());
            itemName.set("Product Name: " + Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getShipmentDetails().getItem());
            awbNo.set(Objects.requireNonNull(rvpWithQC.get()).drsReverseQCTypeResponse.getAwbNo().toString());
            getIsQC();
            getNavigator().setLayoutChild2Visibility(!Objects.requireNonNull(qcList.get()).isEmpty());
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public ObservableField<String> getConsigneeName() {
        return consigneeName;
    }

    public ObservableField<String> getItemName() {
        return itemName;
    }

    public ObservableField<String> getAwbNo() {
        return awbNo;
    }

    public ObservableField<String> getProceedWithQc() {
        try {
            if (sampleQuestionList.get() != null && !Objects.requireNonNull(sampleQuestionList.get()).isEmpty()) {
                proceedWithQc.set("Start QC");
            }
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return proceedWithQc;
    }

    public void onUnsuccessfulClick() {
        getNavigator().onUnsuccessful();
    }

    public void onProceedClick() {

        getNavigator().onProceed();
    }

    public RvpWithQC getRvpWithQc() {
        return rvpWithQC.get();
    }

    public ArrayList<SampleQuestion> getSampleQuestions() {
        return sampleQuestionList.get();
    }

    public ObservableField<Boolean> getIsQC() {
        try {
            if (sampleQuestionList.get() != null && !Objects.requireNonNull(sampleQuestionList.get()).isEmpty()) {
                isQC.set(true);
            }
        } catch (Exception e) {
            Logger.e(RvpQcListViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return isQC;
    }

    public void onBackClick() {
        getNavigator().OnBack();
    }
}
