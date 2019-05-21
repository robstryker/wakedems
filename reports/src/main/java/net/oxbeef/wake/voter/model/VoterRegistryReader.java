package net.oxbeef.wake.voter.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class VoterRegistryReader {
	private File file;
	private ArrayList<Voter> all;
	private ArrayList<Voter> dems;
	private ArrayList<Voter> reps;
	private ArrayList<Voter> other;
	private Set<String> streetsSet;

	public VoterRegistryReader(File f) {
		this.file = f;
	}

	public VoterModel load() throws VoterRegistryReaderException {
		return load(null);
	}
	
	public VoterModel load(IVoterFilter filter) throws VoterRegistryReaderException {

		// This will reference one line at a time
		String line = null;
		BufferedReader bufferedReader = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(file);

			// Always wrap FileReader in BufferedReader.
			bufferedReader = new BufferedReader(fileReader);

			all = new ArrayList<Voter>();
			dems = new ArrayList<Voter>();
			reps = new ArrayList<Voter>();
			other = new ArrayList<Voter>();
			streetsSet = new HashSet<String>();

			// read first line and throw away
			line = bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {
				Voter v = new Voter(line);
				if( isValid(v) && (filter == null || filter.accepts(v))) {
					all.add(v);
					if (v.getParty().equals("DEM")) {
						dems.add(v);
					} else if (v.getParty().equals("REP")) {
						reps.add(v);
					} else {
						other.add(v);
					}
					streetsSet.add(v.getStreet());
				}
			}
			
			return new VoterModel(all, dems, reps, other, streetsSet);
		} catch (FileNotFoundException ex) {
			throw new VoterRegistryReaderException("Unable to open file '" + file.getAbsolutePath() + "'", ex);
		} catch (IOException ex) {
			throw new VoterRegistryReaderException("Error reading file '" + file.getAbsolutePath() + "'", ex);
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

	private boolean isValid(Voter v) {
		// Try to detect errors in the csv file
		try {
			int i = v.getAgeInt();
		} catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
