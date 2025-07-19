package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_check;

import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by Ashish Patel on 8/6/2018.
 */
@HiltViewModel
public class QcCheckViewModel extends BaseViewModel<IQcCheckNavigation> {

    public final ObservableField<String> inputData = new ObservableField<>("");
    public final ObservableField<String> remark = new ObservableField<>("");
    public final ObservableField<RvpQualityCheck> rvpQualityCheck = new ObservableField<>();
    public final ObservableField<SampleQuestion> sampleQuestions = new ObservableField<>();
    public final ObservableField<String> imageCaptureSetting = new ObservableField<>("Capture Image");
    public final ObservableField<String> qcName = new ObservableField<>("");
    public final ObservableField<String> Name = new ObservableField<>("");

    public final ObservableField<String> instructions = new ObservableField<>("");


    @Inject
    public QcCheckViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void setCheckStatus(String checkStatus) {
        this.inputData.set(checkStatus);
    }

    public void onCaptureImageClick() {

        getNavigator().captureImage(getDataManager().getRVPRealTimeSync());
    }

    public void getImageSetting() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getImageCaptureSettings().equalsIgnoreCase("M")) {
                imageCaptureSetting.set("Capture Image*");
            }
        } catch (Exception e) {
            Logger.e(QcCheckViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    // get Instruction fro sampleQuestion
    public ObservableField<String> getInstruction() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getInstructions() != null) {
                instructions.set(Objects.requireNonNull(sampleQuestions.get()).getInstructions());
            }
        } catch (Exception e) {
            Logger.e(QcCheckViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return instructions;
    }

    // get QC name
    public ObservableField<String> getQcName() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getName() != null) {
                qcName.set(Objects.requireNonNull(sampleQuestions.get()).getName() + "*");
                if (Objects.requireNonNull(sampleQuestions.get()).getName().contains("#COLOR#")) {
                    String qcN = Objects.requireNonNull(sampleQuestions.get()).getName().replace("#COLOR#", Objects.requireNonNull(rvpQualityCheck.get()).getQcValue() + " ");
                    qcName.set(qcN + "*");
                }
            }
        } catch (Exception e) {
            Logger.e(QcCheckViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return qcName;
    }

    public ObservableField<String> getName() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getName() != null) {
                Name.set(Objects.requireNonNull(sampleQuestions.get()).getName());
                if (Objects.requireNonNull(sampleQuestions.get()).getName().contains("#COLOR#")) {
                    String qcN = Objects.requireNonNull(sampleQuestions.get()).getName().replace("#COLOR#", " ");
                    Name.set(qcN);
                }
            }
        } catch (Exception e) {
            Logger.e(QcCheckViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return Name;
    }

    // set QC data
    public void setQCData(RvpQualityCheck rvpQualityCheck, SampleQuestion sampleQuestion) {
        this.rvpQualityCheck.set(rvpQualityCheck);
        this.sampleQuestions.set(sampleQuestion);
        getQcName();
        getInstruction();
        getImageSetting();
    }
}
