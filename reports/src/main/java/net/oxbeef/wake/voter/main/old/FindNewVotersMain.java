package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReader;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.filter.RecentlyRegisteredFilter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;

public class FindNewVotersMain {
	private static SimpleDateFormat CSV_DATE = new SimpleDateFormat("yyyy/MM/dd");

	private static final String PROP_MONTHS = "voters.duration.months";
	private static final String PROP_DEM_ONLY = "voters.dem.only";
	
	
	public static void main(String[] args) throws IOException {
		String propMonths = System.getProperty(PROP_MONTHS);
		int months = (propMonths == null ? 3 : Integer.parseInt(propMonths));
		String propDems = System.getProperty(PROP_DEM_ONLY);
		boolean demOnly = (propDems == null ? false : Boolean.parseBoolean(propDems));
		new FindNewVotersMain().run(12, false, "20-04", true);
	}

	private String precinctDataLoc;

	public void run(int months, boolean demOnly, String singlePrecinct, boolean verbose) throws IOException {
		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		
		File f = new File(precinctDataLoc);
		File precinctFile = new File(f, singlePrecinct + ".csv");
		run(months, demOnly, precinctFile, current, verbose);
	}
	
	public void run(int months, boolean demOnly, boolean verbose) throws IOException {

		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		
		File f = new File(precinctDataLoc);
		File[] children = f.listFiles();
		Arrays.sort(children, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		
		for( int i = 0; i < children.length; i++ ) {
			if( children[i].getName().endsWith(".csv") && !children[i].getName().startsWith("database")) {
				run(months, demOnly, children[i], current, verbose);
			}
		}
	}
	
	public void run(int months, boolean demOnly, File precinctFile, String root, boolean verbose) throws IOException {
		String precinct = precinctFile.getName().substring(0, precinctFile.getName().indexOf("."));
		if( !precinct.startsWith("vrdb")) {
			System.out.println("Precinct: " + precinct);
			handlePrecinct(precinct, precinctFile, months, root, demOnly, verbose);
		}
	}

	private void handlePrecinct(String precinct, File f, int months, String current, boolean demOnly, boolean verbose) {
		VoterRegistryReader reader = new VoterRegistryReader(f);
		try {
			VoterModel vm = reader.load(new RecentlyRegisteredFilter(months));
			
			System.out.println(" New Dems: " + vm.getDems().size());
			System.out.println(" New Reps: " + vm.getReps().size());
			System.out.println(" New Other: " + vm.getOther().size());
			precinctDataLoc = current + "/resources/precincts/voters/";
			String definitionLoc = current + "/resources/precincts/definitions/";
			IPrecinct p = getPrecinct(precinct, definitionLoc);
			if( p != null ) {
				PrecinctSummary.printVotersPerSubdivision(vm, p, demOnly, verbose);
			} else if( verbose ) {
				List<Voter> all = vm.getAll();
				for( Voter v : all ) {
					if( !demOnly || v.getParty().equalsIgnoreCase("dem")) {
						System.out.println("    " + v.getName() + "," + v.getFullStreetAddress() + "," + v.getParty());
					}
				}
			}
		} catch (VoterRegistryReaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}
}
