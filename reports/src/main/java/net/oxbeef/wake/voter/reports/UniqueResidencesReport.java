package net.oxbeef.wake.voter.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class UniqueResidencesReport {
	private String precinctId;
	public UniqueResidencesReport(String precinct) {
		this.precinctId = precinct;
	}

	public String run() throws IOException {

		String current = new File(".").getCanonicalPath();
		String precinctDataLoc = current + "/../resources/precincts/voters/";
		String definitionLoc = current + "/../resources/precincts/definitions/";

		IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
		VoterModel vm = null;
		if (precinct != null) {
			vm = loadVoterModel(precinct.getId(), precinct, precinctDataLoc);
		} else {
			vm = loadVoterModel(precinctId, precinctDataLoc);
		}
		
		StringBuffer sb = new StringBuffer();
		HashMap<String, List<Voter>> addresses = VoterUtility.votersByResidence(vm.getAll());
		sb.append("Precinct " + precinctId + " has " + addresses.keySet().size() + " unique residences\n\n");
		
		if( precinct != null ) {
			IPrecinctSubdivision[] subs = precinct.getSubdivisions();
			for( int j = 0; j < subs.length; j++ ) {
				IPrecinctSubdivision sd = subs[j];
				List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
				HashMap<String, List<Voter>> sdAddresses = VoterUtility.votersByResidence(sdVoters);
				sb.append("Subdivision " + sd.getName() + " has " + sdAddresses.keySet().size() + " unique residences\n");
			}
		} else {
			Map<String, List<Voter>> byStreet = VoterUtility.getVotersByStreet(vm.getAll());
			List<String> keys = new ArrayList<String>(byStreet.keySet());
			Collections.sort(keys);
			for( String streetName : keys ) {
				sb.append("\n" + streetName + ": ");
				List<Voter> votersOnStreet = byStreet.get(streetName);
				sb.append("Street " + streetName + " has " + votersOnStreet.size() + " unique residences");
			}
			sb.append("\n");
		}
		return sb.toString();
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
		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			sb.append("\n\nSubdivision: " + sd.getName() + "\n");
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			List<String> parties = VoterUtility.getUniqueParties(sdVoters);
			Collections.sort(parties);
			for( String party : parties ) {
				List<Voter> sdPartyVoters = VoterUtility.getVotersOfParty(sdVoters,  party);
				sb.append(party + "=" + sdPartyVoters.size() + "  ");
				
			}
			sb.append("\n");
			sb.append(indent(getVotersByStreetOutput(sdVoters), 5));
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
