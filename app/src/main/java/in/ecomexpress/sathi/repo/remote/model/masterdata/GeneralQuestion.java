package in.ecomexpress.sathi.repo.remote.model.masterdata;



import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.reactivex.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "GeneralQuestionMaster")
public class GeneralQuestion {

    @JsonProperty("value_array")
    public String valueArray;
    @JsonProperty("ans_type")
    public String answerType;
    @JsonProperty("option")
    public String option;
    @JsonProperty("type")
    public String type;
    @PrimaryKey
    @NonNull
    @JsonProperty("id")
    public int id;
    @JsonProperty("qtag")
    public String questionTag;

    @JsonProperty("value_array")
    public String getValueArray() {
        return valueArray;
    }

    @JsonProperty("value_array")
    public void setValueArray(String valueArray) {
        this.valueArray = valueArray;
    }
    @JsonProperty("ans_type")
    public String getAnswerType() {
        return answerType;
    }
    @JsonProperty("ans_type")
    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }
    @JsonProperty("option")
    public String getOption() {
        return option;
    }
    @JsonProperty("option")
    public void setOption(String option) {
        this.option = option;
    }
    @JsonProperty("type")
    public String getType() {
        return type;
    }
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
    @JsonProperty("id")
    public int getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }
    @JsonProperty("qtag")
    public String getQuestionTag() {
        return questionTag;
    }
    @JsonProperty("qtag")
    public void setQuestionTag(String questionTag) {
        this.questionTag = questionTag;
    }
}
