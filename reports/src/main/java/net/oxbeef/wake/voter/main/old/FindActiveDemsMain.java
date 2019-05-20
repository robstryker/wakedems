package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.BestDemocratResidenceComparator;
import net.oxbeef.wake.voter.model.util.DemsOnlyStandardOutput;
import net.oxbeef.wake.voter.model.util.IOutputFormat;
import net.oxbeef.wake.voter.model.util.OutputUtilities;

public class FindActiveDemsMain {
	private static final String PROP_BY_SUBDIVISION = "voter.sort.subdivision";
	private static final String PROP_COUNT = "voter.count.max";
	
	public static void main(String[] args) throws IOException {
		new FindActiveDemsMain().run();
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
				if( bySubdivision ) {
					OutputUtilities.findMostActiveDemHousesBySubdivision(vm, precinct, 
							getOutputFormat(), count, getComparator());
				} else {
					OutputUtilities.findMostActiveDemResidences(vm, precinct, 
							getOutputFormat(), count);
				}
			}
		}
		
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
