package in.ecomexpress.sathi.repo.remote.model.fuel.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {


    @JsonProperty("errors")
    private String[] errors;
    @JsonProperty("description")
    private String description;
    @JsonProperty("code")
    private String code;

    @JsonProperty("reports")
    public List<Reports> getReports() {
        return reports;
    }
    @JsonProperty("reports")
    public void setReports(List<Reports> reports) {
        this.reports = reports;
    }

    @JsonProperty("reports")
    private List<Reports> reports;

    @JsonProperty("errors")
    public String[] getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public String toString() {
        return "Response [reports = " + reports + ", errors = " + errors + ", description = " + description + ", code = " + code + "]";
    }
}