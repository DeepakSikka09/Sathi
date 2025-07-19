
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ActivityWizard {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @JsonProperty("awb_no")
    private Long awbNo;
    @JsonProperty("term_condition_url")
    private String termConditionUrl;
    @Ignore
    @JsonProperty("is_mandate")
    private Boolean isMandate;

    @JsonProperty("customer_remarks")
    private String customerRemarks;

    @JsonProperty("code")
    private String code;

    @JsonProperty("actual_value")
    private String actualValue;

    @JsonProperty("minimum_amount")
    private Integer minimumAmount;
@Ignore
    @JsonProperty("question_id")
    private List<Object> questionId = null;

    @JsonProperty("manifest_again")
    private String manifestAgain;
@Ignore
    @JsonProperty("question_form_fields")
    private List<Map<String, Object>> questionFormFields = null;

    @JsonProperty("term_condition_url")
    public String getTermConditionUrl() {
        return termConditionUrl;
    }

    @JsonProperty("term_condition_url")
    public void setTermConditionUrl(String termConditionUrl) {
        this.termConditionUrl = termConditionUrl;
    }

    @JsonProperty("is_mandate")
    public Boolean getIsMandate() {
        return isMandate;
    }

    @JsonProperty("is_mandate")
    public void setIsMandate(Boolean isMandate) {
        this.isMandate = isMandate;
    }

    @JsonProperty("customer_remarks")
    public String getCustomerRemarks() {
        return customerRemarks;
    }

    @JsonProperty("customer_remarks")
    public void setCustomerRemarks(String customerRemarks) {
        this.customerRemarks = customerRemarks;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("actual_value")
    public String getActualValue() {
        return actualValue;
    }

    @JsonProperty("actual_value")
    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    @JsonProperty("minimum_amount")
    public Integer getMinimumAmount() {
        return minimumAmount;
    }

    @JsonProperty("minimum_amount")
    public void setMinimumAmount(Integer minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    @JsonProperty("question_id")
    public List<Object> getQuestionId() {
        return questionId;
    }

    @JsonProperty("question_id")
    public void setQuestionId(List<Object> questionId) {
        this.questionId = questionId;
    }

    @JsonProperty("manifest_again")
    public String getManifestAgain() {
        return manifestAgain;
    }

    @JsonProperty("manifest_again")
    public void setManifestAgain(String manifestAgain) {
        this.manifestAgain = manifestAgain;
    }

    @JsonProperty("question_form_fields")
    public List<Map<String, Object>> getQuestionFormFields() {
        return questionFormFields;
    }

    @JsonProperty("question_form_fields")
    public void setQuestionFormFields(List<Map<String, Object>> questionFormFields) {
        this.questionFormFields = questionFormFields;
    }

    public Long getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(Long awbNo) {
        this.awbNo = awbNo;
    }

    public Boolean getMandate() {
        return isMandate;
    }

    public void setMandate(Boolean mandate) {
        isMandate = mandate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ActivityWizard [termConditionUrl=");
        builder.append(termConditionUrl);
        builder.append(", isMandate=");
        builder.append(isMandate);
        builder.append(", awbNo=");
        builder.append(awbNo);
        builder.append(", customerRemarks=");
        builder.append(customerRemarks);
        builder.append(", code=");
        builder.append(code);
        builder.append(", actualValue=");
        builder.append(actualValue);
        builder.append(", minimumAmount=");
        builder.append(minimumAmount);
        builder.append(", questionId=");
        builder.append(questionId);
        builder.append(", manifestAgain=");
        builder.append(manifestAgain);
        builder.append(", questionFormFields=");
        builder.append(questionFormFields);
        builder.append("]");
        return builder.toString();
    }

}
