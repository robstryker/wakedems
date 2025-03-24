package net.oxbeef.wakedems2.mains;

import java.io.IOException;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.reports.NewlyRegisteredVotersReport;
import net.oxbeef.wake.voter.reports.PrecinctPartyMakeupReport;
import net.oxbeef.wake.voter.reports.PrecinctPartyRegistrationChangeReport;
import net.oxbeef.wake.voter.reports.UniqueResidencesReport;
import net.oxbeef.wakedems2.datastore.ExternalDataStore;
import net.oxbeef.wakedems2.datastore.Settings;

public class ReportMain {

	public void report(ExternalDataStore dataStore, Settings settings) throws IOException {
    	String county = settings.getCounty();
    	String precinctId = settings.getPrecinct();
		System.out.println("Reporting county=" + county + ", precinct=" + precinctId);

		MainModel model = new MainModel();
		model.getChangedPartyModel(county, settings.getLookbackPeriodDays());
		model.getOrCreateVoterModel(county, precinctId);
		//model.getOrCreatePreviousVoterModel(precinct, "WAKE", model.getOrCreateVoterModel(precinct));
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("Report for precinct " + precinctId + "\n\n");
		
		int numDays = settings.getLookbackPeriodDays();
		appendReportHeader("Newly Registered Voters: past " + numDays + " days", sb);
		sb.append(indent(new NewlyRegisteredVotersReport(model, settings).run(),5));
		
		appendReportHeader("Changed Party Registration", sb);
		sb.append(indent(new PrecinctPartyRegistrationChangeReport(model, settings).run(), 5));

//		appendReportHeader("Removed / Added Voters and previous info about them", sb);
//		sb.append(indent(new PreviousVoterReport(precinct, model).run(), 5));

		
		appendReportHeader("Partisan makeup report: Counting Voters", sb);
		sb.append(indent(new PrecinctPartyMakeupReport(model).run(), 5));

		appendReportHeader(new String[] {
				"Unique Residences Report: Counting Doors",
				"For use in planning how to canvas your precinct",
				"Warning: This only counts residences with REGISTERED voters!"
		}, sb);
		sb.append(indent(new UniqueResidencesReport(model, settings).run(), 5));
		
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