package net.oxbeef.wake.voter.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import net.oxbeef.wake.voter.model.data.source.ExternalDataSource;

public class ChangedPartyModel {
	private static SimpleDateFormat REG_CHANGE_DATE = new SimpleDateFormat("MM/dd/yyyy");
	private static IPartyChangeFilter dateFilter;

	private Map<String, PartyChange> changes;
	
	public ChangedPartyModel(int countyId) {
		this(getDateAndCountyFilter(countyId));
	}
	
	public ChangedPartyModel() {
		this(getDateFilter());
	}
	

	private static IPartyChangeFilter getDateAndCountyFilter(
			final int id) {
		return new IPartyChangeFilter() {
			
			@Override
			public boolean accepts(PartyChange pc) {
				if( pc.getCountyId() == id && getDateFilter().accepts(pc)) {
					return true;
				}
				return false;
			}
		};
	}
	
	private static IPartyChangeFilter getDateFilter() {
		if( dateFilter == null ) {
			dateFilter = createDateFilter();
		}
		return dateFilter;
	}
	private static IPartyChangeFilter createDateFilter() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1); // to get previous year add -1
		final Date lastYearDate = cal.getTime();

		return new IPartyChangeFilter() {
			@Override
			public boolean accepts(PartyChange pc) {
				String date = pc.getDate();
				Date d;
				try {
					d = REG_CHANGE_DATE.parse(date);
					return d.after(lastYearDate);
				} catch (ParseException e) {
					return false;
				}
			}
			
		};
	}

	public boolean containsVoter(Voter v) {
		return changes.containsKey(v.getVoterRegistrationNumber());
	}

	public PartyChange getChange(Voter v) {
		return changes.get(v.getVoterRegistrationNumber());
	}

	public ChangedPartyModel(IPartyChangeFilter filter) {
		File folder = new File(ExternalDataSource.getInstance().getPartyChangeFolder());
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int lastYear = year-1;
		
		String f1 = year + "_party_change_list.csv";
		String f2 = lastYear + "_party_change_list.csv";
		
		changes = new LinkedHashMap<>();
		
		File lastYearFile = new File(folder, f2);
		File thisYearFile = new File(folder, f1);
		
		load(lastYearFile, changes, filter);
		load(thisYearFile, changes, filter);
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
	
	private interface IPartyChangeFilter {
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
