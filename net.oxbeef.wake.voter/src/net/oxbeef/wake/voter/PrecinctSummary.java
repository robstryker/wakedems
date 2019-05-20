package net.oxbeef.wake.voter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.HTMLOutput;
import net.oxbeef.wake.voter.model.util.OutputUtilities;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class PrecinctSummary {
	public static void main(String[] args) throws IOException {
		new PrecinctSummary().run(new String[] {"20-04"});
	}

	private String precinctDataLoc;
	private String definitionLoc;
	private String templateLoc;

	public void run() throws IOException {
		run(PrecinctCore.PRECINCTS_WITH_SUBDIVISIONS);
	}

	public void run(String[] precincts) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		definitionLoc = current + "/resources/precincts/definitions/";
		templateLoc = current + "/resources/templates/";
		String templateFile = templateLoc + "04-06-template1.txt";
		
		for (int i = 0; i < precincts.length; i++) {
			IPrecinct precinct = getPrecinct(precincts[i], definitionLoc);
			if (precinct == null) {
				System.out.println("Error: Precinct definition does not exist");
				continue;
			} 
			
			VoterModel vm = loadVoterModel(precinct.getId(), precinct);
			OutputUtilities.findMissingOrMalformedVoters(vm, precinct);
			
			printVotersPerSubdivision(vm, precinct, false, false);

		}
	}


	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".csv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}
	public static void printVotersPerSubdivision(VoterModel vm, IPrecinct p, boolean demOnly, boolean verbose) {
		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			System.out.println("\n\n       " + sd.getName() + ": " + sdVoters.size() + " voters.");
			List<Voter> dem = new ArrayList<>();
			List<Voter> rep = new ArrayList<>();
			List<Voter> una = new ArrayList<>();
			
			for( Voter v : sdVoters ) {
				if( v.getParty().equalsIgnoreCase("dem"))
					dem.add(v);
				if( v.getParty().equalsIgnoreCase("rep"))
					rep.add(v);
				if( v.getParty().equalsIgnoreCase("una"))
					una.add(v);
			}
			System.out.println("       Dems: " + dem.size() + ", Rep: " + rep.size() + ", Una: " + una.size());

			if( verbose ) {
				if( demOnly ) {
					System.out.println("       Showing Dems");
				}
				for( Voter v : sdVoters) {
					if( !demOnly || v.getParty().equalsIgnoreCase("dem")) {
						String addon = "";
						if( !demOnly ) {
							addon = ", " + v.getParty();
						}
						addon += ", " + v.getAge() + "y/o";
						addon += ", " + v.getRegistrationDate();
						System.out.println("    " + v.getName() + ", " + v.getFullStreetAddress() + addon);
					}
				}
			}
		}
	}
}
