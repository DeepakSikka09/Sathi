package in.ecomexpress.sathi.ui.dummy.eds.eds_document_collection;

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
 * Created by dhananjayk on 06-11-2018.
 */

@HiltViewModel
public class DocumentCollectionViewModel extends BaseViewModel<IDocumentCollectionNavigation> {
    public final ObservableField<String> inputData = new ObservableField<>("");
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableField<String> activityName = new ObservableField<>("ActivityName");
    public ObservableField<String> activityQuestion = new ObservableField<>("");
    public ObservableField<String> imageCaptureSetting = new ObservableField<>("Image ( Optional )");
    public ObservableField<String> instructions = new ObservableField<>("Instruction");
    public ArrayList<EdsImageStatus> edsImageStatuses = new ArrayList<>();

    @Inject
    public DocumentCollectionViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }
    public void setCheckStatus(String checkStatus) {
        this.inputData.set(checkStatus);
    }
    public ObservableField<String> getActivityName() {
        try {

        if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityName.set(masterActivityData.get().getActivityName());
        }catch (Exception e){
            e.printStackTrace();
            getNavigator().showError(e.getMessage());
        }
        return activityName;
    }

    public ObservableField<String> getActivityQuestion() {
        try{
        if (masterActivityData.get() != null && masterActivityData.get().getActivityName() != null)
            activityQuestion.set(masterActivityData.get().getActivityQuestion());
    }catch (Exception e){
        e.printStackTrace();
        getNavigator().showError(e.getMessage());
    }
        return activityQuestion;
    }

    public ObservableField<String> getImageSetting() {
        try {
        if (masterActivityData.get() != null/* && masterActivityData.get().getImageSettings().equalsIgnoreCase("M")*/) {
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

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        this.edsActivityWizard.set(edsActivityWizard);
        this.masterActivityData.set(masterActivityData);
        getActivityName();
        getActivityQuestion();
        getInstruction();
        getImageSetting();
    }


    public void passImageView(ImageView imgKycActivityCapture,int pos) {
        getNavigator().captureImage(imgKycActivityCapture,pos);
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
