package net.oxbeef.wake.voter.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.data.source.ExternalDataSource;
import net.oxbeef.wake.voter.model.filter.RecentlyRegisteredFilter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.VoterAddressComparator;
import net.oxbeef.wake.voter.model.util.StatisticsUtil;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class NewlyRegisteredVotersReport {
	private String precinctId;
	private int months;
	public NewlyRegisteredVotersReport(String precinct, int months) {
		this.precinctId = precinct;
		this.months = months;
	}

	public String run() throws IOException {

		String precinctDataLoc = ExternalDataSource.getInstance().getPrecinctDataLoc();
		String definitionLoc = ExternalDataSource.getInstance().getDefinitionLoc();

		IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
		VoterModel vm = null;
		if (precinct != null) {
			vm = loadVoterModel(precinct.getId(), precinct, precinctDataLoc);
		} else {
			vm = loadVoterModel(precinctId, precinctDataLoc);
		}
		
		StringBuffer sb = new StringBuffer();
		RecentlyRegisteredFilter filter = new RecentlyRegisteredFilter(months);
		List<Voter> filtered = filter(vm.getAll(), filter);
		
		sb.append("Precinct " + precinctId + " has " + filtered.size() + " newly registered voters in the past " + months + " months\n");
		sb.append(partisanInformation(filtered));
		sb.append("Age Statistics:\n");
		addStatistics(" New Arrivals: ", filtered, sb);
		addStatistics("\n Entire Precinct: ", vm.getAll(), sb);
		sb.append("\n");
		
		
		if( precinct == null ) {
			sb.append(printVoters(filtered));
		} else {
			sb.append(printVotersPerSubdivision(filtered, precinct));
		}
		
		return sb.toString();
	}
	
	private void addStatistics(String string, List<Voter> voters, StringBuffer sb) {
		sb.append(string);
		sb.append("\n   All:");
		sb.append(" mean:" + String.format("%.2f", StatisticsUtil.getAverageAge(voters)));
		sb.append(" median:" + String.format("%.2f", StatisticsUtil.getMedianAge(voters)));
		
		List<String> parties = VoterUtility.getUniqueParties(voters);
		Collections.sort(parties);
		for( String party : parties ) {
			List<Voter> fromParty = VoterUtility.getVotersOfParty(voters, party);
			sb.append("\n   " + party + ": ");
			sb.append(" mean:" + String.format("%.2f", StatisticsUtil.getAverageAge(fromParty)));
			sb.append(" median:" + String.format("%.2f", StatisticsUtil.getMedianAge(fromParty)));
		}
		
		
	}

	private String partisanInformation(List<Voter> list) {
		return VoterUtility.partisanInformation(list);
	}

	private String printVoters(List<Voter> list) {
		StringBuffer sb = new StringBuffer();
		Collections.sort(list, new VoterAddressComparator());
		for( Voter v : list ) {
			sb.append(voterToString(v));
			sb.append("\n");
		}
		return sb.toString();
	}
	private String voterToString(Voter v) {
		String addon = "";
		addon = ", " + v.getParty();
		addon += ", " + v.getAge() + "y/o";
		addon += ", " + v.getRegistrationDate();
		return "    " + v.getName() + ", " + v.getFullStreetAddress() + addon;
	}
	
	private List<Voter> filter(List<Voter> all, IVoterFilter filter) {
		ArrayList<Voter> ret = new ArrayList<Voter>(all);
		Voter v = null;
		for( Iterator<Voter> it = ret.iterator(); it.hasNext(); ) {
			v = it.next(); 
			if( !filter.accepts(v))
				it.remove();
		}
		return ret;
	}
	
	public String printVotersPerSubdivision(List<Voter> list, IPrecinct p) {
		StringBuffer sb = new StringBuffer();
		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			sb.append("\n\nSubdivision: " + sd.getName() + "\n");
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, list);
			List<String> parties = VoterUtility.getUniqueParties(sdVoters);
			Collections.sort(parties);
			for( String party : parties ) {
				List<Voter> sdPartyVoters = VoterUtility.getVotersOfParty(sdVoters,  party);
				sb.append(party + "=" + sdPartyVoters.size() + "  ");
				
			}
			sb.append("\n");
			sb.append(indent(printVoters(sdVoters), 5));
		}
		return sb.toString();
	}
	
	private String indent(String s, int numSpaces) {
		StringBuffer indent = new StringBuffer();
		for( int i = 0; i < numSpaces; i++ ) {
			indent.append(" ");
		}
		String indent2 = indent.toString();
		String indented = s.replaceAll("(?m)^", indent2);
		return indented;
	}

	private VoterModel loadVoterModel(String precinctId, String precinctDataLoc) {
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName));
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct, String precinctDataLoc) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}
