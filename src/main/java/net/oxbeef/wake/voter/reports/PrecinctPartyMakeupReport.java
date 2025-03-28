package net.oxbeef.wake.voter.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.VoterUtility;
import net.oxbeef.wakedems2.datastore.Settings;

public class PrecinctPartyMakeupReport {
	private MainModel model;
	public PrecinctPartyMakeupReport(MainModel model) {
		this.model = model;
	}

	public String run() throws IOException {
		String precinctId = Settings.getDefault().getPrecinct();
		String countyId = Settings.getDefault().getCounty();
		IPrecinct precinct = PrecinctCore.getPrecinct(countyId, precinctId);
		VoterModel vm = model.getOrCreateVoterModel(countyId, precinctId);
		
		if (precinct != null) {
			return printVotersPerSubdivision(vm, precinct, false, false);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Subdivisions have not been declared for precinct " + precinctId);
			sb.append("\nPrinting information by street.");
			List<Voter> all = vm.getAll();
			sb.append("\nPrecinct-wide: " + VoterUtility.partisanInformation(vm.getAll()) + "\n");
			sb.append(getVotersByStreetOutput(all));
			return sb.toString();
		}
	}

	private String getVotersByStreetOutput(List<Voter> voters) {
		Map<String, List<Voter>> byStreet = VoterUtility.getVotersByStreet(voters);
		List<String> keys = new ArrayList<String>(byStreet.keySet());
		Collections.sort(keys);
		
		StringBuffer sb = new StringBuffer();
		
		for( String streetName : keys ) {
			sb.append("\n" + streetName + ": ");
			List<Voter> votersOnStreet = byStreet.get(streetName);
			List<String> parties = VoterUtility.getUniqueParties(votersOnStreet);
			Collections.sort(parties);
			for( String party : parties ) {
				List<Voter> votersOnStreetParty = VoterUtility.getVotersOfParty(votersOnStreet,  party);
				sb.append(party + "=" + votersOnStreetParty.size() + "  ");
			}
		}
		return sb.toString();
	}

	public String printVotersPerSubdivision(VoterModel vm, IPrecinct p, boolean demOnly, boolean verbose) {
		StringBuffer sb = new StringBuffer();
		sb.append("\nPrecinct-wide: " + VoterUtility.partisanInformation(vm.getAll(), true));

		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			sb.append("\nSubdivision: " + sd.getName() + ": ");
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			List<String> parties = VoterUtility.getUniqueParties(sdVoters);
			Collections.sort(parties);
			for( String party : parties ) {
				List<Voter> sdPartyVoters = VoterUtility.getVotersOfParty(sdVoters,  party);
				sb.append(party + "=" + sdPartyVoters.size() + "  ");
			}
		}
		return sb.toString();
	}

	private String indent(String s, int numSpaces) {
		StringBuffer indent = new StringBuffer();
		for( int i = 0; i < numSpaces; i++ ) {
			indent.append(" ");
		}
		String indent2 = indent.toString();
		String indented = s.replaceAll("(?m)^", indent2);
		return indented;
	}

	private VoterModel loadVoterModel(String precinctId, String precinctDataLoc) {
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName));
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct, String precinctDataLoc) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

}
