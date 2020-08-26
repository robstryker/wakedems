package net.oxbeef.wake.voter.model;

import java.util.regex.Pattern;

public class Voter implements IVoterColumnsNCBOE {
	private static String REMOVE_QUOTES_REGEX = "^\"|\"$";
	private static Pattern REMOVE_QUOTES_PATTERN = Pattern.compile(REMOVE_QUOTES_REGEX);
	
	private String[] segments;
	public Voter(String line) {
		segments = line.split("\t");
		for( int i = 0; i < segments.length; i++ ) {
			segments[i] = REMOVE_QUOTES_PATTERN.matcher(segments[i]).replaceAll("");
		}
	}
	
	// Use the constants here and we'll automatically 
	// remove 1 from it so we can access the correct index
	// Also trim leading and trailing quotes
	private String getSegment(int i) {
		return segments[i-1];
	}
	public String getRegistrationDate() {
		return getSegment(registr_dt);
	}
	public String getAge() {
		return getSegment(birth_age);
	}
	public int getAgeInt() {
		return Integer.parseInt(getAge());
	}
	
	public String getFullStreetAddress() {
		return getSegment(res_street_address);
	}

	public String getApartmentNumber() {
		String fullAddr = getSegment(res_street_address);
		int aptIndex = fullAddr.indexOf("  #");
		String result = aptIndex == -1 ? "" : fullAddr.substring(aptIndex).trim();
		return result;
	}
	public String getStreetNumber() {
		int ind = getFullStreetAddress().indexOf(' ');
		return ind == -1 ? "" : getFullStreetAddress().substring(0, ind).trim();
	}
	public int getStreetNumberInt() {
		return Integer.parseInt(getStreetNumber());
	}
	public String getGenderCode() {
		return getSegment(gender_code);
	}
	
	public String getStreet() {
		int begin = getStreetNumber().length();
		String tmp = getSegment(res_street_address).substring(begin);

		int end = tmp.indexOf("  #");
		if( end != -1 ) {
			tmp = tmp.substring(0, end);
		}
		tmp = tmp.trim();
		return tmp.replaceAll("\"", "");
	}
	public String getParty() {
		return getSegment(party_cd);
	}
	public String getName() {
		return getSegment(first_name) + " " + getSegment(last_name);
	}

	public String getFirstName() {
		return getSegment(first_name);
	}

	public String getMiddleName() {
		return getSegment(middle_name);
	}

	public String getLastName() {
		return getSegment(last_name);
	}

	public String getSuffix() {
		return getSegment(name_suffix_lbl);
	}

	public String getStatus() {
		return getSegment(voter_status_desc);
	}

	public String getPrecinct() {
		return getSegment(precinct_abbrv);
	}
	public String getVoterRegistrationNumber() {
		return getSegment(voter_reg_num);
	}
	public String getNCID() {
		return getSegment(ncid);
	}
	
	public String toString() {
		return getFirstName() + " " + getMiddleName() + " " + getLastName() + " " + getSuffix();
	}
}