package net.oxbeef.wake.voter.model;

import java.util.regex.Pattern;

public class Voter implements IVoterColumnsNCBOE {
	private static String REMOVE_QUOTES_REGEX = "^\"|\"$";
	private static Pattern REMOVE_QUOTES_PATTERN = Pattern.compile(REMOVE_QUOTES_REGEX);
	
	private String[] segments;
	public Voter(String line) {
		segments = line.split("\t", -1);
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
		// column was changed; close enough
		return getSegment(age_at_year_end);
	}
	public int getAgeInt() {
		return Integer.parseInt(getAge());
	}
	
	public String getFullStreetAddress() {
		return getSegment(res_street_address);
	}

	
	private String streetName = null;
	private String houseNumber = null;
	private String aptNumber = null;
	private void parseStreetAddress() {
		if( streetName == null ) {
			String fullAddr = getSegment(res_street_address);
			if( "CONFIDENTIAL".equals(fullAddr)) {
				streetName = fullAddr;
				houseNumber = "";
				aptNumber = "";
				return;
			}
			int aptStart = fullAddr.indexOf("  #");
			int streetEnd = aptStart;
			if( aptStart != -1 ) {
				aptNumber = fullAddr.substring(aptStart).trim();
			}
			int firstSpace = fullAddr.indexOf(" ");
			if( firstSpace == -1 ) {
				int z = 5; z++;
			}
			String possibleNumber = fullAddr.substring(0, firstSpace);
			houseNumber = getLeadingDigits(possibleNumber);
			if( houseNumber != null && houseNumber.length() < possibleNumber.length()) {
				aptNumber = possibleNumber.substring(houseNumber.length());
			}
			String tmp = fullAddr.substring(firstSpace, streetEnd == -1 ? fullAddr.length() : streetEnd);
			tmp = tmp.trim();
			streetName = tmp.replaceAll("\"", "");
			if( aptNumber == null ) {
				aptNumber = "";
			}
		}
	}
	
	public String getApartmentNumber() {
		parseStreetAddress();
		return aptNumber;
	}
	
	public String getStreetNumber() {
		parseStreetAddress();
		return houseNumber;
	}
	public int getStreetNumberInt() {
		String n = getStreetNumber();
		if( "".equals(n))
			return -1;
		return Integer.parseInt(n);
	}
	public String getGenderCode() {
		return getSegment(gender_code);
	}
	
	public String getStreet() {
		parseStreetAddress();
		return streetName;
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
	
    private static String getLeadingDigits(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder digits = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c)) {
                digits.append(c);
            } else {
                break; // Stop at the first non-digit character
            }
        }
        return digits.toString();
    }
}