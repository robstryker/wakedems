package net.oxbeef.wake.voter.model.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;

public class RecentlyRegisteredFilter implements IVoterFilter {
	private static SimpleDateFormat CSV_DATE = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat TSV_DATE = new SimpleDateFormat("MM/dd/yyyy");
	
	private int days;
	public RecentlyRegisteredFilter(int days) {
		this.days = days;
	}
	@Override
	public boolean accepts(Voter v) {
		String s = v.getVoterRegistrationNumber();
		String date = v.getRegistrationDate();
        try {
			Date rd = TSV_DATE.parse(date);
			long regTime = rd.getTime();
			long curTime = System.currentTimeMillis();
			long duration = 1000L * 60 * 60 * 24 * days;
			if( regTime + duration > curTime ) {
				return true;
				
			}
		} catch (ParseException e) {
			// ignore
		}
		return false;
	}

}
