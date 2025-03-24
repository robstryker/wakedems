package net.oxbeef.wake.voter.model;

/*
 * TODO - these indexes change fairly often, and it's very very annoying!
 * This should probably be loaded from the downloaded file, if possible, 
 * and if they don't change the column headers. 
 * 
 * These indexes are 1-based (ie first index is 1).
 * To use these in an array, subtract 1 from them. 
 */
public interface IVoterColumnsNCBOE {
	public static final int county_id = 1;
	public static final int county_desc = 2;
	public static final int voter_reg_num = 3;
	public static final int ncid = 4;
	public static final int last_name = 5;
	public static final int first_name = 6;
	public static final int middle_name = 7;
	public static final int name_suffix_lbl = 8;
	public static final int status_cd = 9;
	public static final int voter_status_desc = 10;
	public static final int reason_cd = 11;
	public static final int voter_status_reason_desc = 12;
	public static final int res_street_address = 13;
	public static final int res_city_desc = 14;
	public static final int state_cd = 15;
	public static final int zip_code = 16;
	public static final int mail_addr1 = 17;
	public static final int mail_addr2 = 18;
	public static final int mail_addr3 = 19;
	public static final int mail_addr4 = 20;
	public static final int mail_city = 21;
	public static final int mail_state = 22;
	public static final int mail_zipcode = 23;
	public static final int full_phone_number = 24;
	public static final int confidential_ind = 25;
	public static final int registr_dt = 26;
	public static final int race_code = 27;
	public static final int ethnic_code = 28;
	public static final int party_cd = 29;
	public static final int gender_code = 30;
	public static final int birth_year = 31;
	public static final int age_at_year_end = 32;
	public static final int birth_state = 33;
	public static final int drivers_lic = 34;
	public static final int precinct_abbrv = 35;
	public static final int precinct_desc = 36;
	public static final int municipality_abbrv = 37;
	public static final int municipality_desc = 38;
	public static final int ward_abbrv = 39;
	public static final int ward_desc = 40;
	public static final int cong_dist_abbrv = 41;
	public static final int super_court_abbrv = 42;
	public static final int judic_dist_abbrv = 43;
	public static final int nc_senate_abbrv = 44;
	public static final int nc_house_abbrv = 45;
	public static final int county_commiss_abbrv = 46;
	public static final int county_commiss_desc = 47;
	public static final int township_abbrv = 48;
	public static final int township_desc = 49;
	public static final int school_dist_abbrv = 50;
	public static final int school_dist_desc = 51;
	public static final int fire_dist_abbrv = 52;
	public static final int fire_dist_desc = 53;
	public static final int water_dist_abbrv = 54;
	public static final int water_dist_desc = 55;
	public static final int sewer_dist_abbrv = 56;
	public static final int sewer_dist_desc = 57;
	public static final int sanit_dist_abbrv = 58;
	public static final int sanit_dist_desc = 59;
	public static final int rescue_dist_abbrv = 60;
	public static final int rescue_dist_desc = 61;
	public static final int munic_dist_abbrv = 62;
	public static final int munic_dist_desc = 63;
	public static final int dist_1_abbrv = 64;
	public static final int dist_1_desc = 65;
	public static final int vtd_abbrv = 66;
	public static final int vtd_desc = 67;
}
