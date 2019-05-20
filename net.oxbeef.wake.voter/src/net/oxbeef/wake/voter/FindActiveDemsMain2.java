package net.oxbeef.wake.voter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.sort.BestDemocratResidenceComparator;
import net.oxbeef.wake.voter.model.util.DemsOnlyStandardOutput;
import net.oxbeef.wake.voter.model.util.HTMLOutput;
import net.oxbeef.wake.voter.model.util.IOutputFormat;
import net.oxbeef.wake.voter.model.util.OutputUtilities;

public class FindActiveDemsMain2 extends FindActiveDemsMain {
	public static void main(String[] args) throws IOException {
		new FindActiveDemsMain2().run();
	}

	public void run() throws IOException {
		//run(PrecinctCore.PRECINCTS_WITH_SUBDIVISIONS);
		run(new String[] {"20-04"});
	}

	protected IOutputFormat getOutputFormat() {
		String templateFile = templateLoc + "04-06-template1.txt";
		//return new HTMLOutput(templateFile);
		return new DemsOnlyStandardOutput();
	}
	
	protected Comparator<Residence> getComparator() throws IOException {
//		File f = new File("/home/rob/Desktop/04-06.roster.csv");
//		String content = new String(Files.readAllBytes(f.toPath()));
//		final String[] lines = content.split("\n");
		return new RosterWeightedDemResidenceComparator();
	}
	
	public static class RosterWeightedDemResidenceComparator extends BestDemocratResidenceComparator {
		private String[] lines;
		public RosterWeightedDemResidenceComparator() {
			try {
				File f = new File("/home/rob/Desktop/04-06.roster.csv");
				String content = new String(Files.readAllBytes(f.toPath()));
				lines = content.split("\n");
			} catch(IOException ioe) {
				lines = new String[0];
			}
		}
		
		public int getScore(Residence r) {
			int superScore = super.getScore(r);
			superScore += (25*count(lines, r));
			return superScore;
		}

		protected int count(String[] lines, Residence r) {
			String addr = r.getAddr();
			int count = 0;
			for( int i = 0; i < lines.length; i++ ) {
				String[] lSplit = lines[i].split(",");
				if( lSplit[2].equals(addr)) {
					count += 1;
					if( lSplit.length > 5 && lSplit[5] != null && !lSplit[5].isEmpty()) {
						count += 1;
					}
				}
			}
			return count;
		}
	}
	


}
