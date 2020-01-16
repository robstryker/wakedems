package net.oxbeef.wake.voter.main;

import java.io.IOException;

import net.oxbeef.wake.voter.reports.NewlyRegisteredVotersReport;
import net.oxbeef.wake.voter.reports.PrecinctPartyMakeupReport;
import net.oxbeef.wake.voter.reports.PrecinctPartyRegistrationChangeReport;
import net.oxbeef.wake.voter.reports.PreviousVoterReport;
import net.oxbeef.wake.voter.reports.UniqueResidencesReport;

public class VoterMain {
	public static final String REPORT_DIR = "report.output.dir";
	
	public static void main(String[] args) throws IOException {

		
		if( args.length == 0 ) {
			System.err.println("Usage: java VoterMain 20-04");
			System.exit(1);
		}
		
		String precinct = args[0];
//		String precinct = "04-06";
		
		MainModel model = new MainModel();
		model.getChangedPartyModel(92);
		model.getOrCreateVoterModel(precinct);
		//model.getOrCreatePreviousVoterModel(precinct, "WAKE", model.getOrCreateVoterModel(precinct));
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("Report for precinct " + precinct + "\n\n");
		
		appendReportHeader("Newly Registered Voters: past 6 months", sb);
		sb.append(indent(new NewlyRegisteredVotersReport(precinct, model, 6).run(),5));
		
		appendReportHeader("Changed Party Registration", sb);
		sb.append(indent(new PrecinctPartyRegistrationChangeReport(precinct, model).run(), 5));

//		appendReportHeader("Removed / Added Voters and previous info about them", sb);
//		sb.append(indent(new PreviousVoterReport(precinct, model).run(), 5));

		
		appendReportHeader("Partisan makeup report: Counting Voters", sb);
		sb.append(indent(new PrecinctPartyMakeupReport(precinct, model).run(), 5));

		appendReportHeader(new String[] {
				"Unique Residences Report: Counting Doors",
				"For use in planning how to canvas your precinct",
				"Warning: This only counts residences with REGISTERED voters!"
		}, sb);
		sb.append(indent(new UniqueResidencesReport(precinct, model).run(), 5));
		
		System.out.println(sb.toString());
	}

	private static String indent(String s, int numSpaces) {
		StringBuffer indent = new StringBuffer();
		for( int i = 0; i < numSpaces; i++ ) {
			indent.append(" ");
		}
		String indent2 = indent.toString();
		String indented = s.replaceAll("(?m)^", indent2);
		return indented;
	}

	private static void appendReportHeader(String title, StringBuffer sb) {
		appendReportHeader(new String[] {title}, sb);
	}
	private static void appendReportHeader(String[] title, StringBuffer sb) {
		sb.append("\n\n\n********************************************\n");
		for( int i = 0; i < title.length; i++ ) {
			sb.append("***   " + title[i] + "\n");
		}
		sb.append("********************************************\n");
	}
}
