package net.oxbeef.wake.voter.model.filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;

public class RecentlyRegisteredFilter implements IVoterFilter {
	private static SimpleDateFormat CSV_DATE = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat TSV_DATE = new SimpleDateFormat("MM/dd/yyyy");
	
	private int months;
	public RecentlyRegisteredFilter(int months) {
		this.months = months;
	}
	@Override
	public boolean accepts(Voter v) {

		String date = v.getRegistrationDate();
        try {
			Date rd = TSV_DATE.parse(date);
			long regTime = rd.getTime();
			long curTime = System.currentTimeMillis();
			long oneMonth = 1000L * 60 * 60 * 24 * 30 * months;
			if( regTime + oneMonth > curTime ) {
				return true;
				
			}
		} catch (ParseException e) {
			// ignore
		}
		return false;
	}

}
