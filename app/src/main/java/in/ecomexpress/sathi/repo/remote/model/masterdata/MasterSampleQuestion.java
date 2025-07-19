package in.ecomexpress.sathi.repo.remote.model.masterdata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This pojo is for Sample Question 
 * Represents sample_qc.json has reference for
 * SampleQuestion.java
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterSampleQuestion {

	List<SampleQuestion> sampleQuestions;

	@JsonProperty("sample_question")
	public List<SampleQuestion> getSampleQuestions() {
		return sampleQuestions;
	}

	public void setSampleQuestions(List<SampleQuestion> sampleQuestions) {
		this.sampleQuestions = sampleQuestions;
	}

	@Override
	public String toString() {
		return "MasterSampleQuestion [sample_question=" + sampleQuestions + "]";
	}

}
