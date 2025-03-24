package net.oxbeef.wake.voter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oxbeef.wakedems2.datastore.ExternalDataStore;

public class PreviousStatewideVoterModel {
	private Map<String, OldVoter> currentVotersInOldData = new HashMap<>();
	private Map<String, OldVoter> oldVotersMissingInCurrentData = new HashMap<>();

	public PreviousStatewideVoterModel(String precinctId, String county, VoterModel vm) 
			throws VoterRegistryReaderException {
		List<Voter> allCurrentVoters = vm.getAll();
		ArrayList<String> currentRegistrationIds = new ArrayList<>();
		for( Voter v : allCurrentVoters ) {
			currentRegistrationIds.add(v.getNCID());
		}
		
		File folder = ExternalDataStore.getSingleton().getPreviousNcboeFolder();
		File previousData = findPreviousData(folder);
		if( previousData != null && previousData.exists() && previousData.isFile()) {
			readFile(previousData, currentRegistrationIds, county, precinctId);
		}
	}

	private void readFile(File previousData, ArrayList<String> currentRegistrationIds,
			String targetCounty, String targetPrecinct) throws VoterRegistryReaderException {
		long start = System.currentTimeMillis();
		// This will reference one line at a time
		String line = null;
		BufferedReader bufferedReader = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(previousData);

			// Always wrap FileReader in BufferedReader.
			bufferedReader = new BufferedReader(fileReader);
			// read first line and throw away
			line = bufferedReader.readLine();
			String[] segments = null;
			String ncid = null;
			String precinct = null;
			String county = null;
			while ((line = bufferedReader.readLine()) != null) {
				segments = line.split("\t");
				int lineSegLength = segments.length;
				if( IVoterColumnsPreviousNCBOE.ncid < lineSegLength ) {
					ncid = segments[IVoterColumnsPreviousNCBOE.ncid-1];
				}
				if( IVoterColumnsPreviousNCBOE.precinct_abbrv < lineSegLength ) {
					precinct = segments[IVoterColumnsPreviousNCBOE.precinct_abbrv-1];
				}
				if( IVoterColumnsPreviousNCBOE.county_desc < lineSegLength ) {
					county = segments[IVoterColumnsPreviousNCBOE.county_desc-1];
				}
				if( currentRegistrationIds.contains(ncid)) {
					getCurrentVotersInOldData().put(ncid, new OldVoter(segments));
				} else {
					if(isEqual(precinct, targetPrecinct) && isEqual(county, targetCounty)) {
						getOldVotersMissingInCurrentData().put(ncid, new OldVoter(segments));					}
				}
			}
			System.out.println(getCurrentVotersInOldData().size());
			System.out.println(getOldVotersMissingInCurrentData().size());
			long end = System.currentTimeMillis();
			long duration = end - start;
			System.out.println("Took " + duration + " ms");
		} catch (FileNotFoundException ex) {
			throw new VoterRegistryReaderException("Unable to open file '" + previousData.getAbsolutePath() + "'", ex);
		} catch (IOException ex) {
			throw new VoterRegistryReaderException("Error reading file '" + previousData.getAbsolutePath() + "'", ex);
		} finally {
			if( bufferedReader != null ) {
				try {
					bufferedReader.close();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	private boolean isEqual(String x, String y) {
		return x == null ? y == null : x.equals(y);
	}
	
	private File findPreviousData(File folder) {
		File[] children = folder.listFiles();
		for( int i = 0; i < children.length; i++ ) {
			if( children[i].getName().endsWith(".tsv")) {
				return children[i];
			}
		}
		return null;
	}

	
	public Map<String, OldVoter> getCurrentVotersInOldData() {
		return currentVotersInOldData;
	}


	public Map<String, OldVoter> getOldVotersMissingInCurrentData() {
		return oldVotersMissingInCurrentData;
	}


	public static class OldVoter {
		private String[] segments;
		public OldVoter(String[] s) {
			segments = s;
		}
		public String get(int index) {
			return segments[index-1];
		}
	}
}
