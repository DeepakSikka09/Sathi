package in.ecomexpress.sathi.repo.remote.model.masterdata;



import androidx.room.Embedded;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This POJO  represents consolidated data of Master Documents,Master Activity,Master General Question,Master Sample Question
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterActivityGeneralQuestionResponse {
    @JsonProperty("doc_list")
    List<MasterDocumentList> documentLists;
    @JsonProperty("acitvity")
    List<MasterActivityData> activityDatas;
    @Embedded
    @JsonProperty("Opv_Master")
    public OPVMaster opvMaster;

    private MasterSampleQuestion masterSampleQuestion;


    @JsonProperty("doc_list")
    public List<MasterDocumentList> getDocumentLists() {
        return documentLists;
    }

    public void setDocumentLists(List<MasterDocumentList> documentLists) {
        this.documentLists = documentLists;
    }

    @JsonProperty("acitvity")
    public List<MasterActivityData> getActivityDatas() {
        return activityDatas;
    }

    public void setActivityDatas(List<MasterActivityData> activityDatas) {
        this.activityDatas = activityDatas;
    }

    @JsonProperty("Opv_Master")
    public OPVMaster getOpvMaster() {
        return opvMaster;
    }

    @JsonProperty("Opv_Master")
    public void setOpvMaster(OPVMaster opvMaster) {
        this.opvMaster = opvMaster;
    }

    @JsonProperty("rvp_qc_master")
    public MasterSampleQuestion getMasterSampleQuestion() {
        return masterSampleQuestion;
    }

    public void setMasterSampleQuestion(MasterSampleQuestion masterSampleQuestion) {
        this.masterSampleQuestion = masterSampleQuestion;
    }

}
