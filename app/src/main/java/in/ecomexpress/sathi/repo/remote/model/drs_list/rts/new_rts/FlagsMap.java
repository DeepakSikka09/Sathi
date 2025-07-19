package in.ecomexpress.sathi.repo.remote.model.drs_list.rts.new_rts;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlagsMap implements Serializable {

    @JsonProperty("RET_DEL_IMAGE")
    private String RET_DEL_IMAGE;

    public String getRET_DEL_IMAGE() {
        return RET_DEL_IMAGE;
    }

    public void setRET_DEL_IMAGE(String RET_DEL_IMAGE) {
        this.RET_DEL_IMAGE = RET_DEL_IMAGE;
    }

    @NonNull
    @Override
    public String toString() {
        return "FlagsMap{" + "retDelImage='" + RET_DEL_IMAGE + '\'' + '}';
    }
}