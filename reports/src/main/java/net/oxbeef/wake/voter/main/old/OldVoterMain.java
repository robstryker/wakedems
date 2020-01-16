package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.OutputUtilities;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class OldVoterMain {
	public static final String COMMAND_CHECK_PRECINCT_INTEGRITY = "checkDataIntegrity";
	public static final String COMMAND_UNIQUE_STREETS = "uniqueStreets";

	public static void main(String[] args) throws IOException {
		new OldVoterMain().run(COMMAND_CHECK_PRECINCT_INTEGRITY, new String[] {"04-15"});
	}

	private String precinctDataLoc;
	private String definitionLoc;

	public void run(String command) throws IOException {
		run(command, PrecinctCore.PRECINCTS_WITH_SUBDIVISIONS);
	}

	public void run(String command, String[] precincts) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/../distribution/target/wake.voter.reports.distribution-0.1.0-SNAPSHOT/wakeVoter/resources/precincts/voters/current/";
		definitionLoc = current + "/../resources/precincts/definitions/";

		if (COMMAND_CHECK_PRECINCT_INTEGRITY.equals(command)) {
			//listUniqueStreets(precincts);
			checkAllPrecinctsQE(precincts);
		}
		if (COMMAND_UNIQUE_STREETS.equals(command)) {
			listUniqueStreets(precincts);
		}
	}

	private void listUniqueStreets(String[] precincts) {
		String precinctId = precincts[0];
		IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
		if (precinct == null) {
			System.out.println("Error: Precinct definition does not exist");
			return;
		}
		VoterModel vm = loadVoterModel(precinctId, precinct);
		Set<String> set = vm.getStreetsSet();
		for( String s : set ) {
			System.out.println(s);
		}
		
	}

	private void checkAllPrecinctsQE(String[] precincts) {

		for (int i = 0; i < precincts.length; i++) {
			String precinctId = precincts[i];
			System.out.println(precinctId);
			System.out.println("--------------------------------");
			IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
			if (precinct == null) {
				System.out.println("Error: Precinct definition does not exist");
			} else {
				VoterModel vm = loadVoterModel(precinctId, precinct);
				OutputUtilities.findMissingOrMalformedVoters(vm, precinct);
				
				IPrecinctSubdivision[] subs = precinct.getSubdivisions();
				for( int j = 0; j < subs.length; j++ ) {
					List<Voter> inSub = VoterUtility.findVotersInSubdivision(subs[j], vm.getAll());
					HashMap<String, List<Voter>> residences = VoterUtility.votersByResidence(inSub);
					System.out.println("Subdivision " + subs[j].getName() + " has " + inSub.size() + " voters in " + residences.keySet().size() + " residences");
				}
			}
		}
	}

	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}
