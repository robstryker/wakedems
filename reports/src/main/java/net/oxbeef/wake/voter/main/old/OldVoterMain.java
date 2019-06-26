package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;

import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.HTMLOutput;
import net.oxbeef.wake.voter.model.util.OutputUtilities;

public class OldVoterMain {
	public static final String COMMAND_CHECK_PRECINCT_INTEGRITY = "checkDataIntegrity";
	public static final String COMMAND_PRINT_TOP_DEMS_BY_SUBDIV_HTML = "printTopDemsBySubdivHTML";
	public static final String COMMAND_PRINT_TOP_DEMS_HTML = "printTopDemsHTML";

	public static void main(String[] args) throws IOException {
		new OldVoterMain().run(COMMAND_CHECK_PRECINCT_INTEGRITY, new String[] {"20-04"});
	}

	private String precinctDataLoc;
	private String definitionLoc;
	private String templateLoc;

	public void run(String command) throws IOException {
		run(command, PrecinctCore.PRECINCTS_WITH_SUBDIVISIONS);
	}

	public void run(String command, String[] precincts) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		definitionLoc = current + "/resources/precincts/definitions/";
		templateLoc = current + "/resources/templates/";

		if (COMMAND_CHECK_PRECINCT_INTEGRITY.equals(command)) {
			checkAllPrecinctsQE(precincts);
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
