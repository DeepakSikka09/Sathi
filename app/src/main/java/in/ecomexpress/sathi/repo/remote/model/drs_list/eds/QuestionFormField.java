
package in.ecomexpress.sathi.repo.remote.model.drs_list.eds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionFormField {

    @JsonProperty("doc_list")
    private List<DocList> docList = null;
    
    @JsonProperty("doc_list")
    public List<DocList> getDocList() {
        return docList;
    }
    @JsonProperty("doc_list")
    public void setDocList(List<DocList> docList) {
        this.docList = docList;
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QuestionFormField [docList=");
		builder.append(docList);
		builder.append("]");
		return builder.toString();
	}
    
}
