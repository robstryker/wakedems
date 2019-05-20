package net.oxbeef.wake.voter.model.util;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;

public class DemsOnlyStandardOutput extends StandardOutput {
	public void printResidence(IPrecinct p, IPrecinctSubdivision sd, Residence r) {
		if( !VoterUtility.residenceHasDems(r)) {
			return;
		}
		super.printResidence(p, sd, r);
	}
	protected void printVoterInternal(Voter v) {
		if( v.getParty().equalsIgnoreCase("dem")) {
			super.printVoterInternal(v);
		}
	}

}
