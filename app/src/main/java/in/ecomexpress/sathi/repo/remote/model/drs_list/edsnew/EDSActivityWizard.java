package in.ecomexpress.sathi.repo.remote.model.drs_list.edsnew;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by dhananjayk on 26-10-2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class EDSActivityWizard implements Parcelable, Comparable<EDSActivityWizard> {
    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    private long id;

    @JsonProperty("awb_no")
    public Long awbNo;
    @Embedded
    @JsonProperty("wizard_flag")
    public WizardFlag wizardFlag;
    @JsonProperty("term_condition_url")
    public String termConditionUrl;

    @JsonProperty("activity_id")
    public String activityId = "-1";

    @JsonProperty("customer_remarks")
    public String customerRemarks="";

    @JsonProperty("code")
    public String code;

    @JsonProperty("actual_value")
    public String actualValue;

    @JsonProperty("minimum_amount")
    public int minimumAmount;

    @JsonProperty("question_id")
    public String questionId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Embedded
    @JsonProperty("question_form_fields")
    public QuestionFormFieldDetail questionFormFields = new QuestionFormFieldDetail();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    public WizardFlag getWizardFlag() {
        return wizardFlag;
    }

    public void setWizardFlag(WizardFlag wizardFlag) {
        this.wizardFlag = wizardFlag;
    }

    public String getTermConditionUrl() {
        return termConditionUrl;
    }

    public void setTermConditionUrl(String termConditionUrl) {
        this.termConditionUrl = termConditionUrl;
    }

    public String getCustomerRemarks() {
        return customerRemarks;
    }

    public void setCustomerRemarks(String customerRemarks) {
        this.customerRemarks = customerRemarks;
    }

    @Ignore
    @JsonProperty("ekyc_question_form_field")
    //@TypeConverters(StringMapConverter.class)
    public HashMap<String, String> questionForm;

    public Map<String, String> getQuestionForm() {
        return questionForm;
    }

    public void setQuestionForm(HashMap<String, String> questionForm) {
        this.questionForm = questionForm;
    }

    @JsonIgnore
    public String question_form_dummy;

    public String getQuestion_form_dummy() {
        return question_form_dummy;
    }

    public void setQuestion_form_dummy(String question_form_dummy) {
        Log.e("question_form_dummy" ,question_form_dummy);
        this.question_form_dummy = question_form_dummy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public int getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public QuestionFormFieldDetail getQuestionFormFields() {
        return questionFormFields;
    }

    public void setQuestionFormFields(QuestionFormFieldDetail questionFormFields) {
        this.questionFormFields = questionFormFields;
    }


    @Override
    public int compareTo(@NonNull EDSActivityWizard edsActivityWizard) {
        if (getCode() == null || edsActivityWizard.getCode() == null) {
            return 0;
        }
        return getCode().compareTo(edsActivityWizard.getCode());
    }


    public EDSActivityWizard() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.awbNo);
        dest.writeParcelable(this.wizardFlag, flags);
        dest.writeString(this.termConditionUrl);
        dest.writeString(this.activityId);
        dest.writeString(this.customerRemarks);
        dest.writeString(this.code);
        dest.writeString(this.actualValue);
        dest.writeInt(this.minimumAmount);
        dest.writeString(this.questionId);
        dest.writeString(this.question_form_dummy);
    //    dest.writeSerializable(this.questionForm);
        dest.writeParcelable(this.questionFormFields, flags);
    }

    protected EDSActivityWizard(Parcel in) {
        this.id = in.readLong();
        this.awbNo = (Long) in.readValue(Long.class.getClassLoader());
        this.wizardFlag = in.readParcelable(WizardFlag.class.getClassLoader());
        this.termConditionUrl = in.readString();
        this.activityId = in.readString();
        this.customerRemarks = in.readString();
        this.code = in.readString();
        this.actualValue = in.readString();
        this.minimumAmount = in.readInt();
        this.questionId = in.readString();
//        this.questionForm = (HashMap<String, String>)in.readSerializable();
        this.question_form_dummy = in.readString();
        this.questionFormFields = in.readParcelable(QuestionFormFieldDetail.class.getClassLoader());
    }

    public static final Creator<EDSActivityWizard> CREATOR = new Creator<EDSActivityWizard>() {
        @Override
        public EDSActivityWizard createFromParcel(Parcel source) {
            return new EDSActivityWizard(source);
        }

        @Override
        public EDSActivityWizard[] newArray(int size) {
            return new EDSActivityWizard[size];
        }
    };
}
