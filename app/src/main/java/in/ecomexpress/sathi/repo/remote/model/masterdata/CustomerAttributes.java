package in.ecomexpress.sathi.repo.remote.model.masterdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerAttributes {

    @JsonIgnoreProperties("forward")
    public Forward getForward() {
        return this.forward;
    }
    public void setForward(Forward forward) {
        this.forward = forward;
    }
    Forward forward;

    @JsonIgnoreProperties("rvp")
    public Reverse getRVP() {
        return this.reverse;
    }
    public void setRVP(Reverse reverse) {
        this.reverse = reverse;
    }
    Reverse reverse;
}
