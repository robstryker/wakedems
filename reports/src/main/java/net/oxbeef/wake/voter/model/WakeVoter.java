package net.oxbeef.wake.voter.model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a class representing a line of text from a 
 * wake county voter file's csv spreadsheet. 
 * 
 * The indexes do not match those of the ncboe files. 
 * This file is not currently in use. It is here in case
 * we ever need to use these wake indexes instead. 
 * 
 * @author rob
 *
 */
public class WakeVoter {
	private String[] segments;
	public WakeVoter(String line) {
		segments = line.split(",");
	}
	public String getRegistrationDate() {
		return segments[29];
	}
	public String getAge() {
		return segments[30];
	}
	public int getAgeInt() {
		return Integer.parseInt(segments[30]);
	}
	public String getApartmentNumber() {
		return segments[15];
	}
	
	private static String[] ELECTION_DATES = new String[] {
			"10/11/2011","11/08/2011","05/08/2012","07/17/2012",
			"11/06/2012","03/12/2013","10/08/2013","11/05/2013",
			"05/06/2014","07/15/2014","11/04/2014","10/06/2015",
			"11/03/2015","03/15/2016","06/07/2016","11/08/2016",
			"10/10/2017","11/07/2017"
	};
	private static SimpleDateFormat CSV_DATE = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat HEADING_DATE = new SimpleDateFormat("MM/dd/yyyy");
	
	public int numElectionsBeforeRegistration(String rdate) {
		try {
            Date rd = CSV_DATE.parse(rdate);
            int count = 0;
            Date electDate = null;
            for( int i = 0; i < ELECTION_DATES.length; i++ ) { 
            	electDate = HEADING_DATE.parse(ELECTION_DATES[i]);
            	if( rd.compareTo(electDate) > 0 ) {
            		count++;
            	} else {
            		return count;
            	}
            }
		} catch(ParseException pe) {
			pe.printStackTrace();
		}
		return 0;
	}
	public String getRecentElections(int num) {
		String tmp;
		StringBuffer sb = new StringBuffer();
		for( int i = 61 - num; i < 61; i++ ) {
			if( i < segments.length ) {
				tmp = segments[i];
    			if( tmp != null && !tmp.trim().isEmpty()) {
    				sb.append("✓");
    			} else {
    				sb.append("X");
    			}
			} else {
				sb.append("X");
			}
		}
		return sb.toString();
	}
	public double getVotingPercentage(String rdate) {
		int skip = numElectionsBeforeRegistration(rdate);
		double total = 0;
		double voted = 0;
		String tmp = null;
		for( int i = 43 + skip; i < 61; i++ ) {
			total += 1;
			if( i < segments.length ) {
    			tmp = segments[i];
    			if( tmp != null && !tmp.trim().isEmpty()) {
    				voted += 1;
    			}
			}
		}
		double pctg = voted / total;
		return pctg;
	}
	public String getPctgString(double pctg) {
		pctg *= 100;
		if( pctg == 100 )
			return "100%";
		String doublePctg = new Double(pctg).toString();
		if( pctg < 10 ) {
			doublePctg = "0" + doublePctg;
		}
		return " " + doublePctg.substring(0,2) + "%";
	}
	public String getStreetNumber() {
		return segments[8];
	}
	public int getStreetNumberInt() {
		return Integer.parseInt(segments[8]);
	}
	
	public String getFullStreetAddress() {
		StringBuffer sb = new StringBuffer();
		sb.append(getStreetNumber());
		sb.append(" ");
		if( !segments[10].isEmpty()) {
			sb.append(segments[10]);
			sb.append(" ");
		}
		if( !segments[11].isEmpty()) {
			sb.append(segments[11]);
			sb.append(" ");
		}
		if( !segments[12].isEmpty()) {
			sb.append(segments[12]);
			sb.append(" ");
		}
		if( !getApartmentNumber().isEmpty()) {
			sb.append("#");
			sb.append(getApartmentNumber());
			sb.append(" ");
		}
		String ret = sb.toString().trim();
		return ret.replaceAll("\"", "");
	}
	
	public String getStreet() {
		String ret = null;
		if( segments[12].length() == 0 )
			ret = segments[11];
		else
			ret = segments[11] + " " + segments[12];
		return ret.replaceAll("\"", "");
	}
	public String getParty() {
		return segments[27];
	}
	public String getName() {
		return segments[5] + " " + segments[4];
	}
	public String getPrecinct() {
		return segments[31];
	}
}