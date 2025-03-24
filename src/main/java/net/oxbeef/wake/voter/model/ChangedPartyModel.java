package net.oxbeef.wake.voter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.oxbeef.wakedems2.datastore.ExternalDataStore;

public class ChangedPartyModel {
	private static SimpleDateFormat REG_CHANGE_DATE = new SimpleDateFormat("MM/dd/yyyy");

	private Map<String, PartyChange> changes;
	
	public ChangedPartyModel(String countyId) {
		this(getCountyFilter(countyId));
	}
	
	public ChangedPartyModel(String countyId, int pastDays) {
		this(getCountyAndDateFilter(countyId, pastDays));
	}

	public Map<String, PartyChange> getChanges() {
		Map<String, PartyChange> immutableMap = Collections.unmodifiableMap(changes);
		return immutableMap;
	}
	
	private static IPartyChangeFilter getCountyFilter(String countyId) {
		int asInt = Integer.parseInt(countyId);
		return new IPartyChangeFilter() {
			@Override
			public boolean accepts(PartyChange pc) {
				return pc.getCountyId() == asInt;
			}
		};
	}

	private static IPartyChangeFilter getCountyAndDateFilter(String countyId, int numDays) {
		if( numDays == -1 ) {
			return getCountyFilter(countyId);
		}
		
		int countyInt = Integer.parseInt(countyId);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, numDays * -1);
		final Date previousDate = cal.getTime();

		return new IPartyChangeFilter() {
			@Override
			public boolean accepts(PartyChange pc) {
				String date = pc.getDate();
				Date d;
				try {
					d = REG_CHANGE_DATE.parse(date);
					return pc.getCountyId() == countyInt && d.after(previousDate);
				} catch (ParseException e) {
				}
				return false;
			}
		};
	}

	public ChangedPartyModel(IPartyChangeFilter filter) {
		File folder = ExternalDataStore.getSingleton().getPartyChangeFolder();
		int year = Year.now().getValue();
		int lastYear = year-1;
		
		String f1 = year + "_party_change_list.csv";
		String f2 = lastYear + "_party_change_list.csv";
		
		changes = new LinkedHashMap<>();
		
		File lastYearFile = new File(folder, f2);
		File thisYearFile = new File(folder, f1);
		
		if( lastYearFile.exists()) {
			load(lastYearFile, changes, filter);
		}
		if( thisYearFile.exists()) {
			load(thisYearFile, changes, filter);
		}
	}

	public boolean containsVoter(Voter v) {
		return changes.containsKey(v.getVoterRegistrationNumber());
	}

	public PartyChange getChange(Voter v) {
		return changes.get(v.getVoterRegistrationNumber());
	}


	public void load(File f, Map<String, PartyChange> map, IPartyChangeFilter filter) {

		// This will reference one line at a time
		String line = null;
		BufferedReader bufferedReader = null;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = new FileReader(f);

			// Always wrap FileReader in BufferedReader.
			bufferedReader = new BufferedReader(fileReader);
			
			// read first line and throw away
			line = bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {
				PartyChange pc = new PartyChange(line.split(","));
				if( filter == null || filter.accepts(pc)) {
					map.put(pc.getVoterId(), pc);
				}
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public static interface IPartyChangeFilter {
		public boolean accepts(PartyChange pc);
	}
	
	public static class PartyChange {
		private String[] segments;
		public PartyChange(String[] segments) {
			this.segments = segments;
		}
		public int getCountyId() {
			return Integer.parseInt(segments[0]);
		}
		public String getCountyName() {
			return segments[1];
		}
		public String getVoterId() {
			return segments[3];
		}
		public String getFrom() {
			return segments[4];
		}
		public String getTo() {
			return segments[5];
		}
		public String getDate() {
			return segments[6];
		}
	}
}
