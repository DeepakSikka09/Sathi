
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;


public class ImageQualityResponse {

    @JsonProperty("response_code")
    private Integer responseCode;
    @JsonProperty("response_description")
    private String responseDescription;
    @JsonProperty("image_response")
    private ImageResponse imageResponse;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("response_code")
    public Integer getResponseCode() {
        return responseCode;
    }

    @JsonProperty("response_code")
    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("response_description")
    public String getResponseDescription() {
        return responseDescription;
    }

    @JsonProperty("response_description")
    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    @JsonProperty("image_response")
    public ImageResponse getImageResponse() {
        return imageResponse;
    }

    @JsonProperty("image_response")
    public void setImageResponse(ImageResponse imageResponse) {
        this.imageResponse = imageResponse;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}