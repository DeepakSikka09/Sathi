
package in.ecomexpress.sathi.repo.remote.model.drs_list.gmap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "encodedPath"
})
public class OverviewPolyline {

    @JsonProperty("encodedPath")
    private String encodedPath;
	
    @JsonProperty("encodedPath")
    public String getEncodedPath() {
        return encodedPath;
    }

    @JsonProperty("encodedPath")
    public void setEncodedPath(String encodedPath) {
        this.encodedPath = encodedPath;
    }

}
