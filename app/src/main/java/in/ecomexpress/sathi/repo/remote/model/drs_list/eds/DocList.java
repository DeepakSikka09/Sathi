
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocList {

    @JsonProperty("shipper")
    private String shipper;

    @JsonProperty("list_type")
    private String listType;

    @JsonProperty("shipper")
    public String getShipper() {
        return shipper;
    }

    @JsonProperty("shipper")
    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    @JsonProperty("list_type")
    public String getListType() {
        return listType;
    }

    @JsonProperty("list_type")
    public void setListType(String listType) {
        this.listType = listType;
    }

    @Override
    public String toString() {
        return "DocList{" +
                "shipper='" + shipper + '\'' +
                ", listType='" + listType + '\'' +
                '}';
    }
}
