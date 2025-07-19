package in.ecomexpress.sathi.repo.remote.model.drs_list;



import androidx.room.Ignore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by shivangis on 2/19/2019.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DRSResponse {

    @Ignore
    @JsonProperty("response")
    private TodoResponse todoResponse;

    @Ignore
    @JsonProperty("status")
    private boolean status;

    @Ignore
    @JsonProperty("description")
    private String description;


    @JsonProperty("response")
    public TodoResponse getTodoResponse() {
        return todoResponse;
    }


    @JsonProperty("response")
    public void setTodoResponse(TodoResponse todoResponse) {
        this.todoResponse = todoResponse;
    }

    @JsonProperty("status")
    public boolean getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DRSResponse [TodoResponse = " + todoResponse + ", status = " + status + "]";
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
