package net.oxbeef.wake.voter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReader;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.impl.GenericPrecinctLoader;
import net.oxbeef.wake.voter.model.util.HTMLOutput;
import net.oxbeef.wake.voter.model.util.OutputUtilities;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class MyMain {
	public static final String COMMAND_CHECK_PRECINCT_INTEGRITY = "checkDataIntegrity";
	public static final String COMMAND_PRINT_TOP_DEMS = "printTopDems";
	public static final String COMMAND_PRINT_TOP_DEMS_HTML = "printTopDemsHTML";

	private static final String[] ALL_PRECINCTS = new String[] {
			// "01-49",
			"04-02", "04-03", "04-04", "04-06", "04-07", "04-12" };

	private static final String[] SOUTH_CARY_DISTRICT = new String[] { "04-03", "04-06", "04-07", "04-12", "04-16",
			"06-07", "18-03", "18-05", "18-08", "20-05" };

	public static void main(String[] args) throws IOException {
		new MyMain().run(COMMAND_CHECK_PRECINCT_INTEGRITY, ALL_PRECINCTS);
	}

	private String precinctDataLoc;
	private String definitionLoc;

	public void run(String command) throws IOException {
	}

	public void run(String command, String[] precincts) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		definitionLoc = current + "/resources/precincts/definitions/";

		if (COMMAND_CHECK_PRECINCT_INTEGRITY.equals(command)) {
			checkAllPrecinctsQE(precincts);
		} else if (COMMAND_PRINT_TOP_DEMS.equals(command)) {
			for (int i = 0; i < precincts.length; i++) {
				IPrecinct precinct = getPrecinct(precincts[i], definitionLoc);
				if (precinct == null) {
					System.out.println("Error: Precinct definition does not exist");
				} else {
					VoterModel vm = loadVoterModel(precincts[i], precinct);
					OutputUtilities.findMostActiveDemHousesBySubdivision(vm, precinct);
				}
			}
		} else if (COMMAND_PRINT_TOP_DEMS_HTML.equals(command)) {
			String templateFile = "/home/rob/apps/eclipse/workspaces/photontest/Voters/resources/templates/StrongDemocratTemplate.txt";
			for (int i = 0; i < precincts.length; i++) {
				IPrecinct precinct = getPrecinct(precincts[i], definitionLoc);
				if (precinct == null) {
					System.out.println("Error: Precinct definition does not exist");
				} else {
					VoterModel vm = loadVoterModel(precincts[i], precinct);
					OutputUtilities.findMostActiveDemHousesBySubdivision(vm, precinct, new HTMLOutput(templateFile));
				}
			}
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
		String fileName = precinctDataLoc + precinctId + ".csv";
		VoterRegistryReader reader = new VoterRegistryReader(new File(fileName));
		VoterModel vm = null;
		try {
			vm = reader.load(precinct.getPrecinctFilter());
			return vm;
		} catch (VoterRegistryReaderException vrre) {
			vrre.printStackTrace();
			return null;
		}
	}

	private static void printVotersPerSubdivision(VoterModel vm, IPrecinct p) {
		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			System.out.println(sd.getName() + ": " + sdVoters.size());
		}
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		GenericPrecinctLoader gpl = new GenericPrecinctLoader(precinctDefinitions);
		if (gpl.canLoad(id)) {
			IPrecinct p = new GenericPrecinctLoader(precinctDefinitions).load(id);
			return p;
		}
		return null;
	}

}
