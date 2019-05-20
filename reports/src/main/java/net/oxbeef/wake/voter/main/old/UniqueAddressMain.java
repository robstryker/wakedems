package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.BestDemocratResidenceComparator;
import net.oxbeef.wake.voter.model.util.DemsOnlyStandardOutput;
import net.oxbeef.wake.voter.model.util.IOutputFormat;
import net.oxbeef.wake.voter.model.util.OutputUtilities;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class UniqueAddressMain {
	private static final String PROP_BY_SUBDIVISION = "voter.sort.subdivision";
	private static final String PROP_COUNT = "voter.count.max";
	
	public static void main(String[] args) throws IOException {
		new UniqueAddressMain().run();
	}

	private String precinctDataLoc;
	private String definitionLoc;
	protected String templateLoc;

	public void run() throws IOException {
		//run(PrecinctCore.PRECINCTS_WITH_SUBDIVISIONS);
		run(new String[] {"20-04"});
	}

	protected IOutputFormat getOutputFormat() {
		return new DemsOnlyStandardOutput();
	}
	
	public void run(String[] precincts) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		definitionLoc = current + "/resources/precincts/definitions/";
		templateLoc = current + "/resources/templates/";
		
		String bySub1 = System.getProperty(PROP_BY_SUBDIVISION);
		boolean bySubdivision = (bySub1 == null ? true : Boolean.parseBoolean(bySub1));
		String count1 = System.getProperty(PROP_COUNT);
		int defaultCount = 400;//(bySubdivision ? 20 : 400);
		int count = (count1 == null ? defaultCount : Integer.parseInt(count1));
		
		
		for (int i = 0; i < precincts.length; i++) {
			IPrecinct precinct = getPrecinct(precincts[i], definitionLoc);
			if (precinct == null) {
				System.out.println("Error: Precinct definition does not exist");
			} else {
				VoterModel vm = loadVoterModel(precincts[i], precinct);
				IPrecinctSubdivision[] subs = precinct.getSubdivisions();
				for( int j = 0; j < subs.length; j++ ) {
					IPrecinctSubdivision sd = subs[j];
					List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
					HashMap<String, List<Voter>> addresses = VoterUtility.votersByResidence(sdVoters);
					System.out.println("Subdivision " + j + ", " + sd.getName() + ": " + addresses.size());
					List<Residence> residences = VoterUtility.toResidences(addresses);
					VoterUtility.sortResidenceByStreetAndNumber(residences);
					for( Residence r : residences ) {
						System.out.println("\t" + r.getAddr() + ", numVoters=" 
								+ r.getVoters().size() + "=" + residenceParty(r) + ", score: " + new BestDemocratResidenceComparator().getScore(r));
					}
				}
			}
		}
	}
	
	private String residenceParty(Residence r) {
		String ret = "";
		for( Voter v : r.getVoters()) {
			ret += v.getParty().substring(0, 1);
		}
		return ret;
	}

	protected Comparator<Residence> getComparator() throws IOException{
		return new BestDemocratResidenceComparator();
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".csv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}
