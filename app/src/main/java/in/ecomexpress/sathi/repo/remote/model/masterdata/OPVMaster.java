package in.ecomexpress.sathi.repo.remote.model.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by dhananjayk on 26-07-2018.
 */

public class OPVMaster {
    @JsonProperty("general_question")
    public List<GeneralQuestion> generalQuestions;

    @JsonProperty("general_question")
    public List<GeneralQuestion> getGeneralQuestions() {
        return generalQuestions;
    }

    public void setGeneralQuestions(List<GeneralQuestion> generalQuestions) {
        this.generalQuestions = generalQuestions;
    }

    @Override
    public String toString() {
        return "OPVMaster [general_question=" + generalQuestions + "]";
    }

}


