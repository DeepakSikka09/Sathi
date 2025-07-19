package in.ecomexpress.sathi.ui.dummy.eds.ac_document_list_collection;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import android.app.Application;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.ecomexpress.sathi.R;
import in.ecomexpress.sathi.SathiApplication;
import in.ecomexpress.sathi.repo.IDataManager;
import in.ecomexpress.sathi.repo.remote.model.EdsImageStatus;
import in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew.EDSActivityWizard;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterActivityData;
import in.ecomexpress.sathi.repo.remote.model.masterdata.MasterDocumentList;
import in.ecomexpress.sathi.ui.base.BaseViewModel;
import in.ecomexpress.sathi.utils.rx.ISchedulerProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by dhananjayk on 01-05-2019.
 */
@HiltViewModel
public class AcDocumentListViewModel extends BaseViewModel<IAcDocumentListNavigation> {
    public ObservableField<MasterActivityData> masterActivityData = new ObservableField<>();
    public ObservableField<EDSActivityWizard> edsActivityWizard = new ObservableField<>();
    public ObservableArrayList<String> spinnerName = new ObservableArrayList<>();
    public ObservableArrayList<String> spinnerCode = new ObservableArrayList<>();
    public ObservableArrayList<String> spinnerDisc = new ObservableArrayList<>();
    public ObservableField<String> imageCaptureSetting = new ObservableField<>("Image ( Optional )");
    public ObservableField<String> activityName = new ObservableField<>("ActivityName Not Defined");
    public ObservableField<String> activityQuestion = new ObservableField<>("Not Defined");
    public ObservableField<String> instructions = new ObservableField<>("No Instruction");
    private String discription = "select";
    private String code;
    public ArrayList<EdsImageStatus> edsImageStatuses = new ArrayList<>();

    @Inject
    public AcDocumentListViewModel(IDataManager dataManager, ISchedulerProvider schedulerProvider , Application sathiApplication) {
        super(dataManager, schedulerProvider ,sathiApplication);
    }

    public ObservableField<String> getActivityName() {
        try {
            if (edsActivityWizard.get() != null && masterActivityData.get() != null) {
                activityName.set(masterActivityData.get().getActivityName());
            }
        } catch (Exception e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }
        return activityName;
    }

    public ObservableField<String> getImageSetting() {
        if (masterActivityData.get() != null /*&& masterActivityData.get().getImageSettings().equalsIgnoreCase("M")*/) {
            imageCaptureSetting.set("Image ( Mandatory )");
        }
        return imageCaptureSetting;
    }

    public ObservableField<String> getActivityQuestion() {
        try{
        if (edsActivityWizard.get() != null && masterActivityData.get() != null) {
            activityQuestion.set(masterActivityData.get().getActivityQuestion());
        }}catch (Exception e) {
                getNavigator().error(e.getMessage());
                e.printStackTrace();
            }
        return activityQuestion;
    }

    public ObservableField<String> getInstructions() {
        try {
            if (edsActivityWizard.get() != null && masterActivityData.get() != null) {
                if (discription.equalsIgnoreCase("select"))
                    instructions.set(masterActivityData.get().getInstructions());
                else
                    instructions.set(discription);
            }
        }catch (Exception e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }
        return instructions;
    }

    public void onChooseReasonSpinner(AdapterView<?> parent, View view, int pos, long id) {
        try {
            code = spinnerCode.get(pos);
            discription = spinnerDisc.get(pos);
            getInstructions();
            getNavigator().onChooseReasonSpinner(code);
        }catch (Exception e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }
    }

    public String getSpinnerValue() {
        return code;
    }

    public void getListDetail() {
        try {
            if (edsActivityWizard.get().getQuestionFormFields() != null) {
                getCompositeDisposable().add(getDataManager()
                        .doDocumentListMasterCall(edsActivityWizard.get().getQuestionFormFields().getDocList().getShipper(), edsActivityWizard.get().getQuestionFormFields().getDocList().getListType()).subscribeOn
                                (getSchedulerProvider().io()).
                                observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<MasterDocumentList>() {
                            @Override
                            public void accept(MasterDocumentList masterDocumentList) {
                                spinnerName.clear();
                                spinnerCode.clear();
                                spinnerDisc.clear();
                                getsampledata();
                                JSONArray jarray = getStringToJsonArray(masterDocumentList.getJsonData());
                                for (int i = 0; i < jarray.length(); i++) {
                                    try {
                                        JSONObject jobj = jarray.getJSONObject(i);
                                        spinnerName.add(jobj.getString("name"));
                                        spinnerCode.add(jobj.getString("code"));
                                        spinnerDisc.add(jobj.getString("dis"));
                                    } catch (JSONException j) {
                                        j.printStackTrace();
                                    }
                                }
                            }

                        }));
            }
        }catch (Exception e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void getsampledata() {
        spinnerName.add("Select");
        spinnerCode.add("1");
        spinnerDisc.add("");
    }

    public void setData(EDSActivityWizard edsActivityWizard, MasterActivityData masterActivityData) {
        try {
            this.edsActivityWizard.set(edsActivityWizard);
            this.masterActivityData.set(masterActivityData);
            getActivityName();
            getActivityQuestion();
            getInstructions();
            getListDetail();
        }catch (Exception e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }

    }

    public JSONArray getStringToJsonArray(String s) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
        } catch (JSONException e) {
            getNavigator().error(e.getMessage());
            e.printStackTrace();
        }
        return jsonArray;
    }

   /* public void onCaptureImageClick() {
        getNavigator().captureImage();

    }*/

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
