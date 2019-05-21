package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReader;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.filter.RecentlyRegisteredFilter;
import net.oxbeef.wake.voter.model.util.StatisticsUtil;

public class NewVotersMetadataMain {

	private static final String PROP_MONTHS = "voters.duration.months";
	private static final String PROP_DEM_ONLY = "voters.dem.only";
	
	
	public static void main(String[] args) throws IOException {
		String propMonths = System.getProperty(PROP_MONTHS);
		int months = (propMonths == null ? 6 : Integer.parseInt(propMonths));
		String propDems = System.getProperty(PROP_DEM_ONLY);
		boolean demOnly = (propDems == null ? false : Boolean.parseBoolean(propDems));
		new NewVotersMetadataMain().run(months, demOnly);
	}

	private String precinctDataLoc;

	public void run(int months, boolean demOnly) throws IOException {
		String current = new File(".").getCanonicalPath();
		// precinctDataLoc = current + "/resources/precincts/voters/";
		precinctDataLoc = "/home/rob/Documents/politics/wake/tmp";
		File f = new File(precinctDataLoc);
		File[] children = f.listFiles();
		Arrays.sort(children, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		
		ArrayList<PrecinctSummary> sum = new ArrayList<PrecinctSummary>();
		
		for( int i = 0; i < children.length; i++ ) {
			if( children[i].getName().endsWith(".csv") && !children[i].getName().startsWith("database")) {
				String precinct = children[i].getName().substring(0, children[i].getName().indexOf("."));
				VoterRegistryReader reader = new VoterRegistryReader(children[i]);
				try {
					VoterModel vm = reader.load(new RecentlyRegisteredFilter(months));
					StringBuffer sb = new StringBuffer();
					sb.append("precinct ");
					sb.append(precinct);
					sb.append(": ");
					sb.append("dems: ");
					sb.append(vm.getDems().size());
					sb.append(", reps: ");
					sb.append(vm.getReps().size());
					sb.append(", other: ");
					sb.append(vm.getOther().size());
					System.out.println(sb.toString());
					sum.add(new PrecinctSummary(precinct, vm.getDems(), vm.getReps(), vm.getOther()));
				} catch (VoterRegistryReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		printTotals(sum);
		printAvgStats(sum);
		printLargestDemRegPctgs(sum);
		printLargestDemRegs(sum);
	}

	private void printTotals(ArrayList<PrecinctSummary> sum) {
		int totalD, totalRep, totalO;
		totalD = totalRep = totalO = 0;
		for( PrecinctSummary s : sum ) {
			totalD += s.dems.size();
			totalRep += s.reps.size();
			totalO += s.other.size();
		}
		System.out.println("\nTotal democrats added: " 	+ totalD 	+ "    (" + (100*totalD / (totalD + totalRep + totalO)) + "%)");
		System.out.println("Total republicans added: " 	+ totalRep 	+ "    (" + (100*totalRep / (totalD + totalRep + totalO)) + "%)");
		System.out.println("Total other added: " 		+ totalO 	+ "    (" + (100*totalO / (totalD + totalRep + totalO)) + "%)");
		
	}

	private void printAvgStats(ArrayList<PrecinctSummary> sum) {
		List<Voter> allDems = getAllDems(sum);
		List<Voter> allReps = getAllReps(sum);
		List<Voter> allOther = getAllOther(sum);
		List<Voter> allVoters = new ArrayList<Voter>();
		allVoters.addAll(allDems);
		allVoters.addAll(allReps);
		allVoters.addAll(allOther);
		
		
		System.out.println("new voter: ");
		System.out.println("   mean:" + getAverageAge(allVoters));
		System.out.println("   Stdev: " + getAgeStdev(allVoters));
		System.out.println("   median:" + getMedianAge(allVoters));
		System.out.println("new democrat: ");
		System.out.println("   mean:" + getAverageAge(allDems));
		System.out.println("   Stdev: " + getAgeStdev(allDems));
		System.out.println("   median:" + getMedianAge(allDems));
		System.out.println("new republican: ");
		System.out.println("   mean:" + getAverageAge(allReps));
		System.out.println("   Stdev: " + getAgeStdev(allReps));
		System.out.println("   median:" + getMedianAge(allReps));
		System.out.println("new other: ");
		System.out.println("   mean:" + getAverageAge(allOther));
		System.out.println("   Stdev: " + getAgeStdev(allOther));
		System.out.println("   median:" + getMedianAge(allOther));
	}

	private void printLargestDemRegPctgs(ArrayList<PrecinctSummary> sum) {
		System.out.println();
		System.out.println("Precincts with strongest Democrat Registration Percentages: ");
		ArrayList<PrecinctSummary> presumList = new ArrayList<>(sum);
		Collections.sort(presumList, new Comparator<PrecinctSummary>() {
			@Override
			public int compare(PrecinctSummary o1, PrecinctSummary o2) {
				int o1Pct = (o1.dems.size()*100)/(o1.countAll());
				int o2Pct = (o2.dems.size()*100)/(o2.countAll());
				return ((o2Pct) - (o1Pct));
			}
		});
		for( int ix = 0; ix < 20; ix++ ) {
			PrecinctSummary sum111 = presumList.get(ix);
			StringBuffer sb = new StringBuffer();
			sb.append("precinct ");
			sb.append(sum111.pid);
			sb.append(": ");
			sb.append("dems: ");
			sb.append(sum111.dems.size());
			sb.append(", reps: ");
			sb.append(sum111.reps.size());
			sb.append(", other: ");
			sb.append(sum111.other.size());
			System.out.println(sb.toString());
		}
	}
	

	private void printLargestDemRegs(ArrayList<PrecinctSummary> sum) {
		System.out.println();
		System.out.println("Precincts with strongest Democrat Registrations Raw Numbers: ");
		ArrayList<PrecinctSummary> presumList = new ArrayList<>(sum);
		Collections.sort(presumList, new Comparator<PrecinctSummary>() {
			@Override
			public int compare(PrecinctSummary o1, PrecinctSummary o2) {
				return o2.dems.size() - o1.dems.size();
			}
		});
		for( int ix = 0; ix < 20; ix++ ) {
			PrecinctSummary sum111 = presumList.get(ix);
			StringBuffer sb = new StringBuffer();
			sb.append("precinct ");
			sb.append(sum111.pid);
			sb.append(": ");
			sb.append("dems: ");
			sb.append(sum111.dems.size());
			sb.append(", reps: ");
			sb.append(sum111.reps.size());
			sb.append(", other: ");
			sb.append(sum111.other.size());
			System.out.println(sb.toString());
		}
	}

	private double getAverageAge(List<Voter> voters) {
		return StatisticsUtil.getAverageAge(voters);
	}

	private double getMedianAge(List<Voter> voters) {
		return StatisticsUtil.getMedianAge(voters);
	}

	private double getAgeStdev(List<Voter> voters) {
		return StatisticsUtil.getAgeStdev(voters);
	}

	private List<Voter> getAllDems(List<PrecinctSummary> l) {
		List<Voter> ret = new ArrayList<Voter>();
		for( PrecinctSummary ps : l) {
			ret.addAll(ps.dems);
		}
		return ret;
	}

	private List<Voter> getAllReps(List<PrecinctSummary> l) {
		List<Voter> ret = new ArrayList<Voter>();
		for( PrecinctSummary ps : l) {
			ret.addAll(ps.reps);
		}
		return ret;
	}

	private List<Voter> getAllOther(List<PrecinctSummary> l) {
		List<Voter> ret = new ArrayList<Voter>();
		for( PrecinctSummary ps : l) {
			ret.addAll(ps.other);
		}
		return ret;
	}

	private class PrecinctSummary {
		private String pid;
		private ArrayList<Voter> dems;
		private ArrayList<Voter> reps;
		private ArrayList<Voter> other;

		public PrecinctSummary(String pid, ArrayList<Voter> arrayList, ArrayList<Voter> arrayList2, ArrayList<Voter> arrayList3) {
			this.pid = pid;
			this.dems = arrayList;
			this.reps = arrayList2;
			this.other = arrayList3;
		}
		
		public int countAll() {
			return dems.size() + reps.size() + other.size();
		}
	}
}
