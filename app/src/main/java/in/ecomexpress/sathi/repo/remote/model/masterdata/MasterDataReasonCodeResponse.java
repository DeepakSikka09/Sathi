package in.ecomexpress.sathi.repo.remote.model.masterdata;

import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterDataReasonCodeResponse {

    @JsonProperty("reason_code_master")
    private ReasonCodeMaster reasonCodeMaster;

    @JsonProperty("general_activity_question_master")
    private MasterActivityGeneralQuestionResponse masterManagementDataResponse;

    @Ignore
    @JsonProperty("global_configuration")
    private List<GlobalConfigurationMaster> globalConfigurationResponse;

    @Ignore
    @JsonProperty("callbridge_configuration")
    public CallbridgeConfiguration callbridgeConfiguration;

    @Ignore
    @JsonProperty("banner_configuration")
    private Banner_configuration banner_configuration;

    @Ignore
    @JsonProperty("customer_attributes")
    public CustomerAttributes getCustomer_attributes() {
        return this.customer_attributes;
    }

    public void setCustomer_attributes(CustomerAttributes customer_attributes) {
        this.customer_attributes = customer_attributes;
    }

    CustomerAttributes customer_attributes;

    @JsonProperty("banner_configuration")
    public Banner_configuration getBanner_configuration ()
    {
        return banner_configuration;
    }

    @JsonProperty("banner_configuration")
    public void setBanner_configuration (Banner_configuration banner_configuration) {
        this.banner_configuration = banner_configuration;
    }

    public ReasonCodeMaster getReasonCodeMaster() {
        return reasonCodeMaster;
    }

    public void setReasonCodeMaster(ReasonCodeMaster reasonCodeMaster) {
        this.reasonCodeMaster = reasonCodeMaster;
    }

    public MasterActivityGeneralQuestionResponse getMasterManagementDataResponse() {
        return masterManagementDataResponse;
    }

    public void setMasterManagementDataResponse(MasterActivityGeneralQuestionResponse masterManagementDataResponse) {
        this.masterManagementDataResponse = masterManagementDataResponse;
    }

    public List<GlobalConfigurationMaster> getGlobalConfigurationResponse() {
        return globalConfigurationResponse;
    }

    public void setGlobalConfigurationResponse(List<GlobalConfigurationMaster> globalConfigurationResponse) {
        this.globalConfigurationResponse = globalConfigurationResponse;
    }

    @JsonProperty("callbridge_configuration")
    public CallbridgeConfiguration getCallbridgeConfiguration() {
        return callbridgeConfiguration;
    }

    @JsonProperty("callbridge_configuration")
    public void setCallbridgeConfiguration(CallbridgeConfiguration callbridgeConfiguration) {
        this.callbridgeConfiguration = callbridgeConfiguration;
    }
}