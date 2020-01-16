package net.oxbeef.wake.voter.reports;

import java.util.Map;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.IVoterColumnsPreviousNCBOE;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel.OldVoter;

public class PreviousVoterReport {
	private String precinct;
	private MainModel model;

	public PreviousVoterReport(String precinct, MainModel model) {
		this.precinct = precinct;
		this.model = model;
	}

	public String run() {
		PreviousStatewideVoterModel prev = model.getOrCreatePreviousVoterModel(precinct, "WAKE", model.getOrCreateVoterModel(precinct));
		Map<String, OldVoter> val = prev.getOldVotersMissingInCurrentData();
		StringBuffer sb = new StringBuffer();
		sb.append("Listing all voters that have been removed from voter rolls in " + precinct + " in the 6 months:\n");

		for(String k : val.keySet()) {
			OldVoter ov = val.get(k);
			System.out.println("test");
		}
		return sb.toString();
	}

}
