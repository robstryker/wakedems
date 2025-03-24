package net.oxbeef.wake.voter.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.filter.RecentlyRegisteredFilter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.VoterAddressComparator;
import net.oxbeef.wake.voter.model.util.StatisticsUtil;
import net.oxbeef.wake.voter.model.util.VoterUtility;
import net.oxbeef.wakedems2.datastore.Settings;

public class NewlyRegisteredVotersReport {
	private MainModel model;
	private Settings settings;
	public NewlyRegisteredVotersReport(MainModel model, Settings settings) {
		this.model = model;
		this.settings = settings;
	}

	public String run() throws IOException {
		String precinctId = settings.getPrecinct();
		String countyId = settings.getCounty();
		IPrecinct precinct = PrecinctCore.getPrecinct(settings.getCounty(), settings.getPrecinct());
		VoterModel vm = model.getOrCreateVoterModel(countyId, precinctId);
		
		StringBuffer sb = new StringBuffer();
		RecentlyRegisteredFilter filter = new RecentlyRegisteredFilter(settings.getLookbackPeriodDays());
		List<Voter> filtered = filter(vm.getAll(), filter);
		
		sb.append("Precinct " + precinctId + " has " + filtered.size() + " newly registered voters in the past " + 
		settings.getLookbackPeriodDays() + " days\n");
		sb.append(partisanInformation(filtered));
		sb.append("Age Statistics:\n");
		addStatistics(" New Registrants: ", filtered, sb);
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
		StringBuffer sb = new StringBuffer();
		sb.append("    ");
		sb.append(v.getName());
		sb.append(", ");
		sb.append(v.getFullStreetAddress().trim());
		sb.append(", ");
		sb.append(v.getParty());
		sb.append(", ");
		sb.append(v.getAge());
		sb.append("y/o");
		sb.append(", ");
		sb.append(v.getRegistrationDate());
		return sb.toString();
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
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, list);
			List<String> parties = VoterUtility.getUniqueParties(sdVoters);
			if( sdVoters.size() == 0 ) {
				continue;
			}
			Collections.sort(parties);
			sb.append("\n\nSubdivision: " + sd.getName() + "\n");
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
}
