package net.oxbeef.wake.voter.model.precinct;

import java.io.File;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReader;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.precinct.impl.GenericPrecinctLoader;

public class PrecinctCore {

	public static final String[] PRECINCTS_WITH_SUBDIVISIONS = new String[] {
			// "01-49",
			"04-02", "04-03", "04-04", "04-06", "04-07", "04-12", "20-04" };

	public static final String[] SOUTH_CARY_DISTRICT = new String[] { 
			"04-03", "04-06", "04-07", "04-12", "04-16",
			"06-07", "18-03", "18-05", "18-08", "20-05" };

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		GenericPrecinctLoader gpl = new GenericPrecinctLoader(precinctDefinitions);
		if (gpl.canLoad(id)) {
			IPrecinct p = new GenericPrecinctLoader(precinctDefinitions).load(id);
			return p;
		}
		return null;
	}

	public IVoterFilter getPrecinctFilter(final String precinct) {
		return new IVoterFilter() {
			@Override
			public boolean accepts(Voter v) {
				String p = v.getPrecinct();
				return precinct.equals(p);
			}
		};
	};
	
	public static VoterModel loadVoterModel(File precinctCSV, IPrecinct precinct) {

		// The name of the file to open.
		VoterRegistryReader reader = new VoterRegistryReader(precinctCSV);
		VoterModel vm = null;
		try {
			vm = reader.load(precinct.getPrecinctFilter());
			return vm;
		} catch (VoterRegistryReaderException vrre) {
			vrre.printStackTrace();
			return null;
		}
	}

	public static VoterModel loadVoterModel(File precinctCSV) {
		// The name of the file to open.
		VoterRegistryReader reader = new VoterRegistryReader(precinctCSV);
		VoterModel vm = null;
		try {
			vm = reader.load(null);
			return vm;
		} catch (VoterRegistryReaderException vrre) {
			vrre.printStackTrace();
			return null;
		}
	}
}
