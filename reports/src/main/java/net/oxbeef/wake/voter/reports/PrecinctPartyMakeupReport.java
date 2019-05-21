package net.oxbeef.wake.voter.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class PrecinctPartyMakeupReport {
	private String precinctId;
	public PrecinctPartyMakeupReport(String precinct) {
		this.precinctId = precinct;
	}

	public String run() throws IOException {

		String current = new File(".").getCanonicalPath();
		String precinctDataLoc = current + "/../resources/precincts/voters/";
		String definitionLoc = current + "/../resources/precincts/definitions/";

		IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
		if (precinct != null) {
			VoterModel vm = loadVoterModel(precinct.getId(), precinct, precinctDataLoc);
			return printVotersPerSubdivision(vm, precinct, false, false);
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Subdivisions have not been declared for precinct " + precinctId);
			sb.append("\nPrinting information by street.");
			VoterModel vm = loadVoterModel(precinctId, precinctDataLoc);
			ArrayList<Voter> all = vm.getAll();
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
		sb.append("\nPrecinct-wide: " + VoterUtility.partisanInformation(vm.getAll()));

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
		String fileName = precinctDataLoc + precinctId + ".csv";
		return PrecinctCore.loadVoterModel(new File(fileName));
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct, String precinctDataLoc) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".csv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}
