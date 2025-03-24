package net.oxbeef.wake.voter.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.ChangedPartyModel;
import net.oxbeef.wake.voter.model.ChangedPartyModel.PartyChange;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.StreetComparator;
import net.oxbeef.wake.voter.model.util.VoterUtility;
import net.oxbeef.wakedems2.datastore.Settings;

public class PrecinctPartyRegistrationChangeReport {
	private MainModel model;
	private Settings settings;
	public PrecinctPartyRegistrationChangeReport(MainModel model, Settings settings) {
		this.model = model;
		this.settings = settings;
	}

	public String run() throws IOException {
		int lookback = settings.getLookbackPeriodDays();
		String precinctId = settings.getPrecinct();
		String countyId = settings.getCounty();
		IPrecinct precinct = PrecinctCore.getPrecinct(countyId, precinctId);
		VoterModel vm = model.getOrCreateVoterModel(countyId, precinctId);
		ChangedPartyModel changeModel = model.getChangedPartyModel(countyId, lookback);
		List<Voter> precinctVoters = vm.getAll();
		
		StringBuffer sb = new StringBuffer();
		sb.append("Listing all voters that have changed party registration in " + precinctId + " in the past " + lookback + " days:\n");
		
		sb.append(precinctWideStatistics(precinctVoters, changeModel));
		
		if (precinct != null) {
			return sb.append(printVotersPerSubdivision(vm, changeModel, precinct)).toString();
		} else {
			sb.append("Subdivisions have not been declared for precinct " + precinctId);
			sb.append("\nPrinting information by street.");
			List<Voter> all = vm.getAll();
			sb.append(getVotersByStreetOutput(all, changeModel));
			return sb.toString();
		}
	}

	private static class FromTo {
		private String from;
		private String to;
		public FromTo(String f, String t) {
			from = f;
			to = t;
		}
		public String getFrom() {
			return from;
		}
		public String getTo() {
			return to;
		}
		@Override
		public int hashCode() {
			return from.hashCode() + to.hashCode(); 
		}
		@Override
		public boolean equals(Object other2) {
			if( other2 instanceof FromTo) {
				FromTo other = (FromTo)other2;
				return this.from.equals(other.getFrom()) && this.to.equals(other.getTo());
			}
			return false;
		}
	}
	
	private String precinctWideStatistics(List<Voter> precinctVoters, ChangedPartyModel changeModel) {
		HashMap<FromTo, Integer> stats = new HashMap<>();
		for( Voter v : precinctVoters) {
			if( changeModel.containsVoter(v)) {
				PartyChange pc = changeModel.getChange(v);
				FromTo ft = new FromTo(pc.getFrom(), pc.getTo());
				Integer i = stats.get(ft);
				if( i == null ) {
					i = new Integer(0);
				}
				stats.put(ft, new Integer(i.intValue()+1));
			}
		}
		
		StringBuffer sb = new StringBuffer();
		for( FromTo ft : stats.keySet()) {
			sb.append("     ");
			sb.append(ft.getFrom());
			sb.append(" to " );
			sb.append(ft.getTo());
			sb.append(":  ");
			sb.append(stats.get(ft).intValue());
			sb.append("\n");
		}
		
		return sb.toString();
	}

	private String getVotersByStreetOutput(List<Voter> voters, 
			ChangedPartyModel cpm) {
		Map<String, List<Voter>> byStreet = VoterUtility.getVotersByStreet(voters);
		List<String> keys = new ArrayList<String>(byStreet.keySet());
		Collections.sort(keys);
		
		StringBuffer sb = new StringBuffer();
		
		for( String streetName : keys ) {
			sb.append("\n" + streetName + ": ");
			List<Voter> votersOnStreet = byStreet.get(streetName);
			for( Voter v : votersOnStreet ) {
				if( cpm.containsVoter(v)) {
					PartyChange pc = cpm.getChange(v);
					sb.append(printChange(pc, v));
				}
			}

		}
		return sb.toString();
	}

	public String printVotersPerSubdivision(VoterModel vm, 
			ChangedPartyModel cpm, IPrecinct p) {
		StringBuffer sb = new StringBuffer();
		IPrecinctSubdivision[] all = p.getSubdivisions();
		for (int i = 0; i < all.length; i++) {
			IPrecinctSubdivision sd = all[i];
			sb.append("\nSubdivision: " + sd.getName() + ": \n");
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			Collections.sort(sdVoters, new StreetComparator());
			
			for( Voter v : sdVoters ) {
				if( cpm.containsVoter(v)) {
					PartyChange pc = cpm.getChange(v);
					sb.append(printChange(pc, v));
				}
			}
		}
		return sb.toString();
	}
	
	private String printChange(PartyChange pc, Voter v) {
		StringBuffer sb = new StringBuffer();
		sb.append("Voter ");
		sb.append(v.getName());
		sb.append(" at ");
		sb.append(v.getFullStreetAddress().trim());
		sb.append(" (" + v.getAge() + "/" + v.getGenderCode() + ")") ;
		sb.append(" changed party from ");
		sb.append(pc.getFrom());
		sb.append(" to ");
		sb.append(pc.getTo());
		sb.append(" on ");
		sb.append(pc.getDate());
		sb.append("\n");
		return sb.toString();
	}
}
