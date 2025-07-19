package in.ecomexpress.sathi.repo.remote.model;


import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by santosh on 6/1/20.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContatDecryption {



    @Ignore
    @JsonProperty("response")
    private contactResponse todoResponse;


    @Ignore
    @JsonProperty("status")
    private boolean status;

    public contactResponse getTodoResponse() {
        return todoResponse;
    }

    public void setTodoResponse(contactResponse todoResponse) {
        this.todoResponse = todoResponse;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
