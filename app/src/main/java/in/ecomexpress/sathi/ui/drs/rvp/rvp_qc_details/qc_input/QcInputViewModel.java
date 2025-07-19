package in.ecomexpress.sathi.ui.drs.rvp.rvp_qc_details.qc_input;

import android.app.Application;

import androidx.databinding.ObservableField;

import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.drs_list.rvp.RvpQualityCheck;
import in.ecomexpress.sathi.repo.remote.model.masterdata.SampleQuestion;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.Logger;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

@HiltViewModel
public class QcInputViewModel extends BaseViewModel<IQcInputNavigation> {

    public final ObservableField<String> inputData = new ObservableField<>("");
    public final ObservableField<String> inputDataRemark = new ObservableField<>("");
    public final ObservableField<RvpQualityCheck> rvpQualityCheck = new ObservableField<>();
    public final ObservableField<SampleQuestion> sampleQuestions = new ObservableField<>();
    public final ObservableField<String> imageCaptureSetting = new ObservableField<>("Capture Image");
    public final ObservableField<String> qcName = new ObservableField<>("");
    public final ObservableField<String> Name = new ObservableField<>("");
    public final ObservableField<String> instructions = new ObservableField<>("");
    private String starredValue;
    private String starredString;

    @Inject
    public QcInputViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider, Application sathiApplication) {
        super(dataManager, schedulerProvider, sathiApplication);
    }

    public void onCaptureImageClick() {
        getNavigator().captureImage();
    }

    public void getImageSetting() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getImageCaptureSettings().equalsIgnoreCase("M")) {
                imageCaptureSetting.set("Capture Image*");
            }

        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }

    public ObservableField<String> getQcName() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getName() != null) {
                qcName.set(Objects.requireNonNull(sampleQuestions.get()).getName() + "( " + getDetail(rvpQualityCheck, sampleQuestions) + " )");
            }

        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return qcName;
    }

    public ObservableField<String> getName() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getName() != null) {
                Name.set(Objects.requireNonNull(sampleQuestions.get()).getName());
            }
        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return Name;
    }

    public void onScanClick() {
        getNavigator().onScanClick();
    }

    public String getDetail(ObservableField<RvpQualityCheck> rvpQualityCheck, ObservableField<SampleQuestion> sampleQuestions) {
        try {
            if (Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().contains("TAIL")) {
                starredValue = imeiTailStars(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue(), Integer.parseInt(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().substring(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().length() - 1)));
                starredString = getTrailStarredString(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue(), Integer.parseInt(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().substring(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().length() - 1)));
            } else if (Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().contains("HEAD")) {
                starredValue = imeiHeadStars(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue(), Integer.parseInt(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().substring(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().length() - 1)));
                starredString = getHeadStarredString(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue(), Integer.parseInt(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().substring(Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().length() - 1)));
            } else if (Objects.requireNonNull(sampleQuestions.get()).getVerificationMode().contains("ALL")) {
                starredValue = imeiFirstLastFourVisible(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue());
                starredString = Objects.requireNonNull(rvpQualityCheck.get()).getQcValue();
            } else {
                starredValue = imeiFullStars(Objects.requireNonNull(rvpQualityCheck.get()).getQcValue());
                starredString = Objects.requireNonNull(rvpQualityCheck.get()).getQcValue();
            }
        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return starredValue;
    }

    private String imeiTailStars(String imei, int count) {
        if (imei.isEmpty() && imei.length() < count) {
            return "No Data Defined";
        } else {
            String headString = imei.substring(0, imei.length() - count);
            String tailString = imei.substring(imei.length() - count).replaceAll("(?s).", "*");
            return headString + tailString;
        }
    }

    private String imeiHeadStars(String imei, int count) {
        if (imei.isEmpty() && imei.length() < count) {
            return "No Data Defined";
        } else {
            String headString = imei.substring(0, count).replaceAll("(?s).", "*");
            String tailString = imei.substring(count);
            return headString + tailString;
        }
    }

    private String getTrailStarredString(String imei, int count) {
        if (!imei.isEmpty() && imei.length() >= count) {
            return imei.substring(imei.trim().length() - count, imei.trim().length());
        }
        return "0";
    }

    private String getHeadStarredString(String imei, int count) {
        if (!imei.isEmpty() && imei.length() >= count) {
            return imei.substring(0, count);
        }
        return "0";
    }

    private String imeiFirstLastFourVisible(String imei) {
        if (imei.length() >= 8) {
            String firstFour = imei.substring(0, 4);
            String lastFour = imei.substring(imei.length() - 4);
            int numStars = imei.length() - 8;
            String stars = String.join("", Collections.nCopies(numStars, "*"));
            return firstFour + stars + lastFour;
        } else if (imei.length() >= 4) {
            String frontTwo = imei.substring(0, 2);
            String backTwo = imei.substring(imei.length() - 2);
            int numStars = imei.length() - 4;
            String stars = String.join("", Collections.nCopies(numStars, "*"));
            return frontTwo + stars + backTwo;
        } else {
            return imei;
        }
    }

    private String imeiFullStars(String imei) {
        if (!imei.isEmpty()) {
            return imei.replaceAll("(?s).", "*");
        }
        return "*";
    }

    public ObservableField<String> getInstruction() {
        try {
            if (sampleQuestions.get() != null && Objects.requireNonNull(sampleQuestions.get()).getInstructions() != null) {
                instructions.set(Objects.requireNonNull(sampleQuestions.get()).getInstructions());
            }
        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
        return instructions;
    }

    public void setQCData(RvpQualityCheck rvpQualityCheck, SampleQuestion sampleQuestion) {
        this.rvpQualityCheck.set(rvpQualityCheck);
        this.sampleQuestions.set(sampleQuestion);
        getQcName();
        getInstruction();
        getImageSetting();
    }

    public String getValue() {
        return starredString;
    }

    public void getPhonePeShipmentType(String awbNo) {
        try {
            getCompositeDisposable().add(getDataManager().getPhonePeShipmentType(awbNo).
                    subscribeOn(getSchedulerProvider().io()).
                    observeOn(getSchedulerProvider().ui()).subscribe(String -> getNavigator().isPhonePayEnabled(String), throwable -> {
                        writeErrors(Calendar.getInstance().getTimeInMillis(), new Exception(throwable));
                        Logger.e(QcInputViewModel.class.getName(), throwable.getMessage());
                    }));
        } catch (Exception e) {
            Logger.e(QcInputViewModel.class.getName(), e.getMessage());
            getNavigator().showError(e.getMessage());
        }
    }
}