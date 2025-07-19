package in.ecomexpress.sathi.ui.dummy.eds.eds_document_verification;

import androidx.databinding.ObservableField;

import android.app.Application;
import android.widget.ImageView;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EdsImageStatus;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;

/**
 * Created by dhananjayk on 07-11-2018.
 */
@HiltViewModel
public class DocumentVerificationViewModel extends BaseViewModel<IDocumentVerificationNavigation> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("Activity not Defined");
    public ObservableField<String> activityQuestion = new ObservableField<>("Not Defined");
    public ObservableField<String> imageCaptureSetting = new ObservableField<>("Image ( Optional )");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");
    private String starredIMEI, starredString;
    public ArrayList<EdsImageStatus> edsImageStatuses = new ArrayList<>();


    @Inject
    public DocumentVerificationViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public ObservableField<String> getActivityName() {
        try {

        if (masterActivityData.get() != null) {
            activityName.set(masterActivityData.get().getActivityName());
        }

        }catch (Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return activityName;

    }

    public ObservableField<EDSActivityWizard> getEdsActivityWizard() {
        return edsActivityWizard;
    }
    /* public ObservableField<String> getVarifiedValue()
    {
        if()
    }*/

    public ObservableField<String> getImageCaptureSetting() {
        try {
        if (masterActivityData.get() != null /*&& masterActivityData.get().getImageSettings().equalsIgnoreCase("M")*/) {
            imageCaptureSetting.set("Image ( Mandatory )");
        }

    }catch (Exception e){
        e.printStackTrace();
        getNavigator().showError(e.getMessage());
    }
        return imageCaptureSetting;
    }

    public ObservableField<String> getInstruction() {
        try {
        if ((masterActivityData.get() != null)) {
            instructions.set(edsActivityWizard.get().getCustomerRemarks() + "\n" /*+ masterActivityData.get().getInstructions()==null ? "" :masterActivityData.get().getInstructions()*/);
        }

    }catch (Exception e){
        e.printStackTrace();
        getNavigator().showError(e.getMessage());
    }
        return instructions;
    }


    public ObservableField<String> getActivityQuestion() {
        try {
        if (masterActivityData.get() != null) {
            if (masterActivityData.get().getVerificationMode().contains("TAIL")) {
                starredIMEI = imeiTailStars(edsActivityWizard.get().getActualValue(), Integer.parseInt(
                        masterActivityData.get().getVerificationMode().substring(masterActivityData.get().getVerificationMode().length() - 1)));

                starredString = getTrailStarredString(edsActivityWizard.get().getActualValue(), Integer.parseInt(
                        masterActivityData.get().getVerificationMode().substring(masterActivityData.get().getVerificationMode().length() - 1)));
            } else if (masterActivityData.get().getVerificationMode().contains("HEAD")) {

                starredIMEI = imeiHeadStars(edsActivityWizard.get().getActualValue(), Integer.parseInt(
                        masterActivityData.get().getVerificationMode().substring(masterActivityData.get().getVerificationMode().length() - 1)));

                starredString = getHeadStarredString(edsActivityWizard.get().getActualValue(), Integer.parseInt(
                        masterActivityData.get().getVerificationMode().substring(masterActivityData.get().getVerificationMode().length() - 1)));

            } else {
                starredIMEI = imeiFullStars(edsActivityWizard.get().getActualValue());
                starredString = edsActivityWizard.get().getActualValue();

            }

            activityQuestion.set(masterActivityData.get().getActivityQuestion() + "(" + starredIMEI + ")");
        }

    }catch (Exception e){
        e.printStackTrace();
        getNavigator().showError(e.getMessage());
    }
        return activityQuestion;
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();
        getActivityQuestion();
        getInstruction();
        getImageCaptureSetting();

    }

    private String imeiTailStars(String imei, int count) {
        try {
            if (imei.isEmpty() && imei.length() < count) {
                return "No Data Defined";
            } else {
                String headString = imei.substring(0, imei.length() - count);
                String tailString = imei.substring(imei.length() - count).replaceAll("(?s).", "*");
                return headString + tailString;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());

        }
        return "No Data Defined";
    }

    private String imeiHeadStars(String imei, int count) {
        try {
            if (imei.isEmpty() && imei.length() < count) {
                return "No Data Defined";
            } else {
                String headString = imei.substring(0, count).replaceAll("(?s).", "*");
                String tailString = imei.substring(count);
                return headString + tailString;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());

        }
        return "No Data Defined";
    }

    private String getTrailStarredString(String imei, int count) {
        try {
            if (!imei.isEmpty() && imei.length() >= count)
                return imei.substring(imei.trim().length() - count, imei.trim().length());
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return "No Data Defined";
    }

    private String getHeadStarredString(String imei, int count) {
        try {
            if (!imei.isEmpty() && imei.length() >= count)
                return imei.substring(0, count);
            return "0";
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return "No Data Defined";

    }

    private String imeiFullStars(String imei) {
        try {
            if (!imei.isEmpty())
                return imei.replaceAll("(?s).", "*");
            return "*";
        } catch (Exception e) {
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return "No Data Defined";
    }

    /* public void onCaptureImageClick() {
         getNavigator().captureImage();

     }
 */
    public void onVerifyClick() {

        getNavigator().onVerify(starredString);
    }

    public void passImageView(ImageView imgKycActivityCapture, int position) {
        getNavigator().captureImage(imgKycActivityCapture, position);
    }

    public void setImageStatus(ArrayList<EdsImageStatus> imageStatus)
    {
        this.edsImageStatuses = imageStatus;
    }

    public ArrayList<EdsImageStatus> getEdsImageStatuses()
    {
        return edsImageStatuses;
    }

    public void captureFrontImage() {
        getNavigator().captureFrontImage();
    }

    public void captureRearImage() {
        getNavigator().captureRearImage();
    }

    public void uploadAAdharImage() {
        if (getDataManager().getAadharStatusCode() != 0) {
            getNavigator().uploadAAdharImage();
        } else {
            getNavigator().showError("Aadhar Images Already Uploaded.");
        }
    }

    public void getHDFCMaskingStatus() {
        if (!getDataManager().getAadharFrontImage().equalsIgnoreCase("")) {
            getNavigator().getHDFCMaskingStatus();
        } else {
            getNavigator().showError(getNavigator().getContextProvider().getString(R.string.aadhar_status_error));
        }

    }

}
