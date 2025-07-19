package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "image_id",
        "drs_id",
        "awb",
        "activity_code",
        "image_key",
        "image_uri",
        "image_status",
        "image_quality",
        "text_match",
        "date"
})
public class ImageResponse {

    @JsonProperty("image_id")
    private Integer image_id;
    @JsonProperty("drs_id")
    private Integer drsId;
    @JsonProperty("awb")
    private Integer awb;
    @JsonProperty("activity_code")
    private String activityCode;
    @JsonProperty("image_key")
    private String imageKey;
    @JsonProperty("image_uri")
    private String imageUri;
    @JsonProperty("image_status")
    private String imageStatus;
    @JsonProperty("image_quality")
    private String imageQuality;
    @JsonProperty("text_match")
    private Integer textMatch;
    @JsonProperty("date")
    private Long date;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("drs_id")
    public Integer getDrsId() {
        return drsId;
    }

    @JsonProperty("drs_id")
    public void setDrsId(Integer drsId) {
        this.drsId = drsId;
    }

    @JsonProperty("awb")
    public Integer getAwb() {
        return awb;
    }

    @JsonProperty("awb")
    public void setAwb(Integer awb) {
        this.awb = awb;
    }

    @JsonProperty("activity_code")
    public String getActivityCode() {
        return activityCode;
    }

    @JsonProperty("activity_code")
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    @JsonProperty("image_key")
    public String getImageKey() {
        return imageKey;
    }

    @JsonProperty("image_key")
    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    @JsonProperty("image_uri")
    public String getImageUri() {
        return imageUri;
    }

    @JsonProperty("image_uri")
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @JsonProperty("image_status")
    public String getImageStatus() {
        return imageStatus;
    }

    @JsonProperty("image_status")
    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    @JsonProperty("image_quality")
    public String getImageQuality() {
        return imageQuality;
    }

    @JsonProperty("image_quality")
    public void setImageQuality(String imageQuality) {
        this.imageQuality = imageQuality;
    }

    @JsonProperty("text_match")
    public Integer getTextMatch() {
        return textMatch;
    }

    @JsonProperty("text_match")
    public void setTextMatch(Integer textMatch) {
        this.textMatch = textMatch;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Integer getImage_id() {
        return image_id;
    }

    public void setImage_id(Integer image_id) {
        this.image_id = image_id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}