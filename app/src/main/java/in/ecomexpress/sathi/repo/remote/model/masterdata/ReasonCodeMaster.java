package in.ecomexpress.sathi.repo.remote.model.masterdata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReasonCodeMaster {

	private List<ForwardReasonCodeMaster> forwardReasonCodeMasters;
	private List<RVPReasonCodeMaster> reasonCodeMasters;
	private List<RTSReasonCodeMaster> rtsReasonCodeMasters;
	private List<EDSReasonCodeMaster> edsReasonCodeMasters;

	@JsonProperty("forward")
	public List<ForwardReasonCodeMaster> getForwardReasonCodeMasters() {
		return forwardReasonCodeMasters;
	}

	public void setForwardReasonCodeMasters(List<ForwardReasonCodeMaster> forwardReasonCodeMasters) {
		this.forwardReasonCodeMasters = forwardReasonCodeMasters;
	}

	@JsonProperty("rvp")
	public List<RVPReasonCodeMaster> getReasonCodeMasters() {
		return reasonCodeMasters;
	}

	public void setReasonCodeMasters(List<RVPReasonCodeMaster> reasonCodeMasters) {
		this.reasonCodeMasters = reasonCodeMasters;
	}

	@JsonProperty("rts")
	public List<RTSReasonCodeMaster> getRtsReasonCodeMasters() {
		return rtsReasonCodeMasters;
	}

	public void setRtsReasonCodeMasters(List<RTSReasonCodeMaster> rtsReasonCodeMasters) {
		this.rtsReasonCodeMasters = rtsReasonCodeMasters;
	}

	@JsonProperty("eds")
	public List<EDSReasonCodeMaster> getEdsReasonCodeMasters() {
		return edsReasonCodeMasters;
	}

	public void setEdsReasonCodeMasters(List<EDSReasonCodeMaster> edsReasonCodeMasters) {
		this.edsReasonCodeMasters = edsReasonCodeMasters;
	}

	@Override
	public String toString() {
		return "ReasonCodeMaster [forwardReasonCodeMasters=" + forwardReasonCodeMasters + ", reasonCodeMasters="
				+ reasonCodeMasters + ", rtsReasonCodeMasters=" + rtsReasonCodeMasters + ", edsReasonCodeMasters="
				+ edsReasonCodeMasters + "]";
	}

}
