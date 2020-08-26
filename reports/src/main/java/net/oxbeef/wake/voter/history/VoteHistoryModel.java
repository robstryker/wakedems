package net.oxbeef.wake.voter.history;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.oxbeef.wake.voter.registration.RegistrationUtil;

public class VoteHistoryModel {
	public static final int county_id 			= 0;
	public static final int county_desc 		= 1;	
	public static final int voter_reg_num		= 2;
	public static final int election_lbl		= 3;
	public static final int election_desc		= 4;
	public static final int voting_method		= 5;
	public static final int voted_party_cd	 	= 6;
	public static final int voted_party_desc	= 7;
	public static final int pct_label	 		= 8;
	public static final int pct_description	 	= 9;
	public static final int ncid				= 10;
	public static final int voted_county_id		= 11;	
	public static final int voted_county_desc	= 12;
	public static final int vtd_label			= 13;
	public static final int vtd_description		= 14;


	
	private String file;
	private String countyId;
	private HashMap<String, ArrayList<OneVote>> model;

	public VoteHistoryModel(String file, String countyId) throws IOException {
		this.file = file;
		this.countyId = countyId;
		model = new HashMap<>();
		loadModel();
	}
	
	private void loadModel() throws IOException {
		String prefix = "\"" + countyId + "\"";
		List<String> allLines = Files.readAllLines(Paths.get(file));
		int i = 0;
		for(String oneLine : allLines ) {
			i++;
			if( !oneLine.startsWith(prefix))
				continue;
			String[] fields = oneLine.split("\t");
			if( !fields[election_desc].replaceAll("\"", "").contains("PRIMARY")) {
				continue;
			}

			String voterReg = fields[voter_reg_num].replaceAll("\"", "");
			
			ArrayList<OneVote> voteList = model.get(voterReg);
			if( voteList == null ) {
				voteList = new ArrayList<OneVote>();
				model.put(voterReg, voteList);
			}
			voteList.add(new OneVote(fields));
		}
	}
	
	public static class OneVote {
		private static String REMOVE_QUOTES_REGEX = "^\"|\"$";
		private static Pattern REMOVE_QUOTES_PATTERN = Pattern.compile(REMOVE_QUOTES_REGEX);

		String[] segments;
		public OneVote(String[] segments) {
			this.segments = segments;
			for( int i = 0; i < segments.length; i++ ) {
				segments[i] = REMOVE_QUOTES_PATTERN.matcher(segments[i]).replaceAll("");
			}
		}
	}
	
	public ArrayList<OneVote> getVotes(String voterReg) {
		return model.get(voterReg);
	}
	
	public double getVoteAnalysis(String voterReg) {
		ArrayList<OneVote> list = model.get(voterReg);
		if( list == null || list.size() == 0)
			return -1;
		double total = 0;
		double divisor = 0;
		for( OneVote ov : list ) {
			if( ov.segments[4].contains("PRIMARY")) {
				String party = ov.segments[6];
				divisor += 1;
				if( RegistrationUtil.leansDem(party)) {
					total += 1;
				} else if( RegistrationUtil.isUnaffiliated(party)) {
					total += 0.5;
				}
			}
		}
		if( divisor == 0 ) {
			return -1;
		}
		return total / divisor;
	}
}
