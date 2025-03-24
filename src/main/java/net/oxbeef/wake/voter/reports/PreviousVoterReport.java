package net.oxbeef.wake.voter.reports;

import java.util.Map;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel.OldVoter;
import net.oxbeef.wakedems2.datastore.Settings;

public class PreviousVoterReport {
	private Settings settings;
	private MainModel model;

	public PreviousVoterReport(Settings settings, MainModel model) {
		this.settings = settings;
		this.model = model;
	}

	public String run() {
		String precinctId = this.settings.getPrecinct();
		String countyId = this.settings.getCounty();

		PreviousStatewideVoterModel prev = model.getOrCreatePreviousVoterModel(precinctId, "WAKE", model.getOrCreateVoterModel(countyId, precinctId));
		Map<String, OldVoter> val = prev.getOldVotersMissingInCurrentData();
		StringBuffer sb = new StringBuffer();
		sb.append("Listing all voters that have been removed from voter rolls in " + precinctId + " in the 6 months:\n");

		for(String k : val.keySet()) {
			OldVoter ov = val.get(k);
			System.out.println("test");
		}
		return sb.toString();
	}

}
