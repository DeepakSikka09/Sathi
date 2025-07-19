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
public class GenerateToken {

    @Ignore
    @JsonProperty("token_data")
    private contactResponse todoResponse;


    @Ignore
    @JsonProperty("status")
    private boolean status;


    @Ignore
    @JsonProperty("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
