package in.ecomexpress.sathi.repo.remote.model.masterdata;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(tableName = "DocumentListTable")
public class MasterDocumentList {

    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    private int id;
    @JsonProperty("activity_code")
    private String activityCode;
    @JsonProperty("json_data")
    private String jsonData;
    @JsonProperty("shipper")
    private String shipper;
    @JsonProperty("list_type")
    private String listType;

    @JsonProperty("activity_code")
    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("json_data")
    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    @JsonProperty("shipper")
    public String getShipper() {
        return shipper;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    @JsonProperty("list_type")
    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    @Override
    public String toString() {
        return "MasterDocumentList [activityCode=" + activityCode + ", jsonData=" + jsonData + ", shipper=" + shipper
                + ", listType=" + listType + "]";
    }

}
