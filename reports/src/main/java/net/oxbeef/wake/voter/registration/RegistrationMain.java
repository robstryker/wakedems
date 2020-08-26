package net.oxbeef.wake.voter.registration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.history.VoteHistoryModel;
import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.Voter;

public class RegistrationMain {
	public  final String REPORT_DIR = "report.output.dir";
	
	public  enum COLOR { GREY, GREEN, BLUE, ORANGE };
	private static final double oneThird = ((double)1)/3;
	private static final double twoThird = ((double)2)/3;
	
	private static final String VOTE_HISTORY_FILE = "/home/rob/tmp/waketemp/ncvhis92.txt";
	private static final String MOVED_VOTERS_FILE = "/home/rob/WakeMovedVoters.tsv";
	
	public static void main(String[] args) throws IOException {
		MainModel model = new MainModel();
		model.getChangedPartyModel(92);
		ModelCacher mc = new ModelCacher(model);
		String voteHistory = VOTE_HISTORY_FILE;
		VoteHistoryModel vhm = new VoteHistoryModel(voteHistory, "92");
		new RegistrationMain(mc, vhm).run();
	}
	
	private ModelCacher mc;
	private VoteHistoryModel vhm;
	public RegistrationMain(ModelCacher mc, VoteHistoryModel vhm) {
		this.mc = mc;
		this.vhm = vhm;
	}
	
	public void run() throws IOException {
		List<MovedVoter> toCheck = readList();
		postLoad(toCheck);
	}
	
	
	
	public void postLoad(List<MovedVoter> toCheck) {
		ColorComment c = null;
		
		// Headings
		System.out.println("Owner\tAddr1\tAddr2\tDeed_date\tVolunteer-Grey\tVolunteer-Green\tVolunteer-Blue\tVolunteer-Orange\t"
				+ "Algo-Color\tAlgo-Match-Volunteer\tConcat\tParty\tNameScore\tAddrScore\tAvgScore\tNameComment\tAddrComment\tOther-Comment");
		
		
		for( MovedVoter mv : toCheck ) {
			c = getColorForVoter(mv.getName(), mv.getAddr1(), mv.getAddr2(), mv.getDeed());
			// TODO remove these 
			
			StringBuffer sb = new StringBuffer();
			sb.append(mv.getName());
			sb.append("\t");
			sb.append(mv.getAddr1());
			sb.append("\t");
			sb.append(mv.getAddr2());
			sb.append("\t");
			sb.append(mv.getDeed());
			sb.append("\t");
			
			sb.append(mv.getGrey());
			sb.append("\t");
			sb.append(mv.getGreen());
			sb.append("\t");
			sb.append(mv.getBlue());
			sb.append("\t");
			sb.append(mv.getOrange());
			sb.append("\t");
			
			// algo color
			sb.append(c.getC());
			sb.append("\t");
			// algo match volunteer
			if( mv.getVolunteerColor() != null )
				sb.append(c.getC().equals(mv.getVolunteerColor()));
			sb.append("\t");
			// concat
			if( mv.getVolunteerColor() != null ) {
				sb.append(c.getC().equals(mv.getVolunteerColor()));
				sb.append(c.getC());
				sb.append((mv.getVolunteerColor()));
			}
			sb.append("\t");

			// party
			sb.append(c.getParty());
			sb.append("\t");

			// name score
			sb.append(c.getNameScore() == -1 ? "" : c.getNameScore());
			sb.append("\t");

			sb.append(c.getAddrScore()== -1 ? "" : c.getAddrScore());
			sb.append("\t");

			sb.append(c.getAvgScore()== -1 ? "" : c.getAvgScore());
			sb.append("\t");

			sb.append(c.getNameComment());
			sb.append("\t");

			sb.append(c.getAddrComment());
			sb.append("\t");

			
			sb.append(c.getComment());
			System.out.println(sb.toString());
		}
		
		// About to return, can restart now if we want
		int z = 5; z++; 
		z++;
	}

	public ArrayList<MovedVoter> readList() throws IOException {
		File f = new File(MOVED_VOTERS_FILE);
		ArrayList<MovedVoter> ret = new ArrayList<>();
		byte[] all = Files.readAllBytes(f.toPath());
		String asString = new String(all);
		String[] byLine = asString.split("\n");
		for( int i = 1; i < byLine.length; i++ ) {
			String[] lineSplit = byLine[i].split("\t");
			
			ret.add(new MovedVoter( 
					RegistrationUtil.safeIndex(lineSplit, 0),
					RegistrationUtil.safeIndex(lineSplit, 1),
					RegistrationUtil.safeIndex(lineSplit, 2),
					RegistrationUtil.safeIndex(lineSplit, 3),
					RegistrationUtil.safeIndex(lineSplit, 4),
					RegistrationUtil.safeIndex(lineSplit, 5),
					RegistrationUtil.safeIndex(lineSplit, 6),
					RegistrationUtil.safeIndex(lineSplit, 7)));
		}
		return ret;
	}
	public ColorComment getColorForVoter(String name, String addr1, String addr2, String deedDate) {
		
		// First find if one is already registered at the given address
		ArrayList<Voter> allMatches = findAddressAndNameMatches(name, addr1, addr2);
		if( allMatches.size() == 1 ) {
			// We have a match. Let's just make sure they are ACTIVE
			Voter v = allMatches.get(0);
			String status = allMatches.get(0).getStatus();
			if( "ACTIVE".equals(status))
				return new ColorComment(COLOR.GREEN, -1,-1,-1,v.getParty(), "", "", "Direct match found; registration status " + status);
			else {
				ColorComment cc = getColorCommentForVoter(v);
				if( cc != null )
					return cc;
			}
		}
		
		// More than one FirstName M LastName lives at that address
		// This isn't too bad though. It's a match.
		if( allMatches.size() > 1 ) {
			return new ColorComment(COLOR.GREEN, -1, -1, -1, "", "", "", "Multiple people with identical name at given address");
		}
		
		Voter[] allWithName = findAllVotersWithName(name);
		allWithName = filterActiveOnly(allWithName);
		if( allWithName.length == 1 ) {
			ColorComment cc = getColorCommentForVoter(allWithName[0]);
			if( cc != null )
				return cc;
		}
		
		// The individual is NOT registered. We need to send a flier (maybe). 
		// First let's see if the people at that address that *are* registered are likely dem?
		String lastName = name.substring(0, name.indexOf(",")).trim();
		DoubleFraction houseMatesLikelyDemDF = areHouseMatesLikelyDem(lastName, addr1, deedDate);
		// Or if people with those names are likely dem
		DoubleFraction isNameLikelyDemDF = isNameLikelyDem(name, allWithName);

		double addrScore = houseMatesLikelyDemDF.d;
		double nameScore = isNameLikelyDemDF.d;
		
		if(addrScore == -1 && nameScore == -1 ) {
			// We have no info here at all. No housemates, no people iwth same name. Ugh
			return new ColorComment(COLOR.GREY,-1, -1, -1, "", "", "",  
					"No housemates, no name match in county");
		}
		
		double avgScore = average(addrScore, nameScore);

		String msg = "";
		if( addrScore != -1 ) {
			msg += "Address: " + houseMatesLikelyDemDF.comment + ". ";
		}
		if( nameScore != -1 ) {
			msg += "Name: " + isNameLikelyDemDF.comment + ". ";
		}
		
		msg += " Avg Score: " + avgScore + ". ";
		
		if( avgScore >= twoThird ) {
			return new ColorComment(COLOR.BLUE, nameScore, addrScore, avgScore, "", isNameLikelyDemDF.comment, houseMatesLikelyDemDF.comment, msg);
		}
		if( avgScore <= oneThird) {
			return new ColorComment(COLOR.ORANGE, nameScore, addrScore, avgScore, "", isNameLikelyDemDF.comment, houseMatesLikelyDemDF.comment, msg);
		}
		
		msg += "Manual verification required";
		return new ColorComment(COLOR.GREY, nameScore, addrScore, avgScore, "", isNameLikelyDemDF.comment, houseMatesLikelyDemDF.comment, msg);
	}
	
	private ColorComment getColorCommentForVoter(Voter v) {
		// Only one voter with that name in the whole county. 
		// Let's return based on party affiliation
		if( RegistrationUtil.leansDem(v)) {
			return new ColorComment(COLOR.BLUE, 1.0, -1, 1.0, v.getParty(), "", "", "Single name match; registered blueish");
		} else if( RegistrationUtil.leansRep(v)) {
			return new ColorComment(COLOR.ORANGE, 0, -1, 0, v.getParty(), "", "", "Single name match; registered redish");
		} else if( RegistrationUtil.isUnaffiliated(v)) {
			double d = this.vhm.getVoteAnalysis(v.getVoterRegistrationNumber());
			if( d != -1 ) {
				if( d >= twoThird )
					return new ColorComment(COLOR.BLUE, 0.75, -1, 0.75, v.getParty(), "", "", "Single name match; UNA w/ blue-ish primary voting history");
				if( d <= oneThird )
					return new ColorComment(COLOR.ORANGE, 0.25, -1, 0.25, v.getParty(), "", "", "Single name match; UNA w/ red-ish primary voting history");
			}
		}
		return null;
	}
	
	private double average(double d1, double d2) {
		if( d1 == -1 ) {
			return d2;
		}
		if( d2 == -1 )
			return d1;
		return (d1+d2)/2;
	}
	
	private Voter[] filterActiveOnly(Voter[] all) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( int i = 0; i < all.length; i++ ) {
			if( all[i].getStatus().equals("ACTIVE"))
				ret.add(all[i]);
		}
		return (Voter[]) ret.toArray(new Voter[ret.size()]);
	}

	private Voter[] filterDeedDataOrLastName(Voter[] activeOnly, String yyyymmdd, String lastName) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( int i = 0; i < activeOnly.length; i++ ) {
			String mmddyyyy = activeOnly[i].getRegistrationDate();
			if( RegistrationUtil.isAfter(yyyymmdd, mmddyyyy) || activeOnly[i].getLastName().equals(lastName)) {
				ret.add(activeOnly[i]);
			}
		}
		return (Voter[]) ret.toArray(new Voter[ret.size()]);
	}
	public DoubleFraction isNameLikelyDem(String name, Voter[] allWithName ) {
		Voter[] activeOnly = filterActiveOnly(allWithName);
		return getDemScore(activeOnly);
	}
	
	private DoubleFraction getDemScore(Voter[] all) {
		double totalLeanDem = 0;
		int totalValid = 0;
		StringBuffer members = new StringBuffer();
		for( int i = 0; i < all.length; i++ ) {
			totalValid++;
			String party = all[i].getParty().substring(0,1).toUpperCase();
			if( RegistrationUtil.leansDem(all[i])) {
				totalLeanDem++;
			} else if( RegistrationUtil.isUnaffiliated(all[i])) {
				String vid = all[i].getVoterRegistrationNumber();
				double primaryHistory = vhm.getVoteAnalysis(vid);
				if( primaryHistory == -1 || (primaryHistory > (oneThird) && primaryHistory < (twoThird))) {
					totalLeanDem+= 0.5;
				} else if( primaryHistory >= twoThird) {
					totalLeanDem += 0.75;
					party = "(UD)";
				} else if( primaryHistory <= oneThird) {
					party = "(UR)";
					totalLeanDem += 0.25;
				}			
			}
			members.append(party);
		}
		
		if( totalValid == 0 )
			return new DoubleFraction(-1, "No active voters fit criteria");
		
		double ret = (double)((double)totalLeanDem/totalValid);
		return new DoubleFraction(ret, members.toString());
	}

	public DoubleFraction areHouseMatesLikelyDem(String lastName, String addr1, String deedDate) {
		ArrayList<Voter> houseMates = findAllVotersAtAddress(addr1);
		Voter[] activeOnly = filterActiveOnly((Voter[]) houseMates.toArray(new Voter[houseMates.size()]));
		String yyyymmdd = deedDate.substring(0, 10);
		Voter[] suitable = filterDeedDataOrLastName(activeOnly, yyyymmdd, lastName);
		return getDemScore(suitable);
	}	

	public ArrayList<Voter> findAllVotersAtAddress(String addr1) {
		ArrayList<Voter> ret = findAllVotersAtAddressImpl(addr1);
		if( ret.size() == 0 ) {
			String[] addr1Aliases = getAddr1Aliases(addr1);
			for( int i = 0; i < addr1Aliases.length; i++ ) {
				ret.addAll(findAllVotersAtAddressImpl(addr1Aliases[i]));
			}
		}
		return ret;
	}
	
	public ArrayList<Voter> findAllVotersAtAddressImpl(String addr1) {
		String street = RegistrationUtil.getStreetForAddr1(addr1);
		String houseNum = RegistrationUtil.getStreetNumber(addr1);
		ArrayList<Voter> houseMates = new ArrayList<>();
		List<Voter> allOnStreet = mc.findVotersOnStreet(street);
		for( Voter v : allOnStreet) {
			if( v.getStreetNumber().equals(houseNum)) {
				houseMates.add(v);
			}
		}

		return houseMates;
	}

	public ArrayList<Voter> findStreetAndNameMatches(String name, String addr1, String addr2) {
		String street = RegistrationUtil.getStreetForAddr1(addr1);
		ArrayList<Voter> ret = new ArrayList<>();
		List<Voter> allOnStreet = mc.findVotersOnStreet(street);
		Voter[] matches = findVoters(allOnStreet, name);
		ret.addAll(Arrays.asList(matches));
		return ret;
	}
	
	public ArrayList<Voter> findAddressAndNameMatches(String name, String addr1, String addr2) {
		ArrayList<Voter> ret = findAddressAndNameMatchesImpl(name, addr1, addr2);
		if( ret.size() == 0 ) {
			// check for addr aliases?
			String[] addr1Aliases = getAddr1Aliases(addr1);
			for( int i = 0; i < addr1Aliases.length; i++ ) {
				ret.addAll(findAddressAndNameMatchesImpl(name, addr1Aliases[i], addr2));
			}
		}
		return ret;
	}

	private String[] getAddr1Aliases(String addr1) {
		if( addr1.contains("CROSSING")) {
			return new String[] { addr1.replaceAll("CROSSING",  "XING")};
		}
		return new String[0];
	}

	public ArrayList<Voter> findAddressAndNameMatchesImpl(String name, String addr1, String addr2) {
		ArrayList<Voter> streetAndName = findStreetAndNameMatches(name, addr1, addr2);
		for( Iterator<Voter> it = streetAndName.iterator(); it.hasNext(); ) {
			Voter temp = it.next();
			if( !addr1.startsWith(temp.getStreetNumber()))
				it.remove();
		}
		return streetAndName;
	}

	
	public final Voter[] findAllVotersWithName(String name) {
		String last = name.substring(0, name.indexOf(",")).trim();
		List<Voter> withLastName = mc.findVotersLastName(last);
		Voter[] withName = findVoters(withLastName, name);
		return withName;
	}
	
	public ArrayList<Voter> filterLastNameMatch(List<Voter> voter, String lastName) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getLastName().equals(lastName)) {
				ret.add(v);
			}
		}
		return ret;
	}

	public ArrayList<Voter> filterSuffixMatch(List<Voter> voter, String suffix) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getSuffix().equals(suffix)) {
				ret.add(v);
			}
		}
		return ret;
	}

	public ArrayList<Voter> filterFirstNameMatch(List<Voter> voter, String firstName) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getFirstName().equals(firstName)) {
				ret.add(v);
			}
		}
		return ret;
	}
	

	public ArrayList<Voter> filterMiddleNameInitial(List<Voter> voter, String middleName) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getMiddleName() != null ) {
				if( v.getMiddleName().length() == 1 && middleName.startsWith(""+v.getMiddleName().charAt(0)))
					ret.add(v);
				else if( middleName.length() == 1 && v.getMiddleName().startsWith(""+middleName.charAt(0)))
					ret.add(v);
			}
		}
		return ret;
	}

	public ArrayList<Voter> filterMiddleNameInitials(List<Voter> voter, String initials) {
		String[] initialsSplit = initials.trim().split(" ");
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getMiddleName() != null ) {
				String[] voterMiddleSplit = v.getMiddleName().split(" ");
				if( initialsMatchName(initialsSplit, voterMiddleSplit)) {
					ret.add(v);
				}
			}
		}
		return ret;
	}

	public boolean initialsMatchName(String[] initials, String[] name) {
		for( int i = 0; i < name.length; i++ ) {
			if( initials.length < i )
				return false;
			if( !name[i].startsWith(initials[i]))
				return false;
		}
		return true;
	}
	
	public ArrayList<Voter> filterMiddleNameEmpty(List<Voter> voter) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getMiddleName() == null || v.getMiddleName().isEmpty()) {
				ret.add(v);
			}
		}
		return ret;
	}

	public List<Voter> filterMiddleNameFull(ArrayList<Voter> voter, String middle) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( Voter v : voter ) {
			if( v.getMiddleName() != null && v.getMiddleName().length() == 1 && middle.startsWith(v.getMiddleName())) {
				ret.add(v);
			} else if( v.getMiddleName() != null && v.getMiddleName().trim().equalsIgnoreCase(middle.trim())) {
				ret.add(v);
			}
		}
		return ret;
	}

	public List<Voter> filterMiddleNameMixed(ArrayList<Voter> voter, String middle) {
		ArrayList<Voter> ret = new ArrayList<>();
		String[] middleSegments = middle.trim().split(" ");
		for( Voter v : voter ) {
			String registrationMiddle = v.getMiddleName().trim();
			if( registrationMiddle != null && registrationMiddle.contains(" ")) {
				boolean match = compareNameSegments(middleSegments, registrationMiddle.split(" "));
				if( match )
					ret.add(v);
			}
		}
		return ret;
	}

	
	private boolean compareNameSegments(String[] middleSegments, String[] split) {
		if( middleSegments.length == split.length ) {
			for( int i = 0; i < middleSegments.length; i++ ) {
				boolean midInitial = middleSegments[i].length() == 1;
				boolean splitInitial = split[i].length() == 1;
				if( !midInitial && !splitInitial && middleSegments[i].equals(split[i])) 
					continue;
				if( midInitial && split[i].startsWith(""+middleSegments[i].charAt(0)))
					continue;
				if( splitInitial && middleSegments[i].startsWith(""+split[i].charAt(0)))
					continue;
				return false;
			}
			return true;
		}
		return false;
	}

	public Voter[] findVoters(List<Voter> voters, String name) {
		Voter[] ret = findVoters(voters, name, false);
		if( ret == null || ret.length == 0 ) {
			ret = findVoters(voters, name, true);
		}
		return ret;
	}
	
	public final Voter[] findVoters(List<Voter> voters, String name, boolean ignoreMiddle) {

		ArrayList<Voter> ret = new ArrayList<Voter>();
		ArrayList<FirstMiddleLast> permutations = RegistrationUtil.getNamePermutations(name, ignoreMiddle);
		for( FirstMiddleLast fml : permutations ) {
			String last = fml.last;
			List<Voter> lastMatch = filterLastNameMatch(voters, last);
			ArrayList<Voter> firstMatch = filterFirstNameMatch(lastMatch, fml.first);
			ArrayList<Voter> suffixMatch = firstMatch;
			if( fml.suffix != null ) {
				suffixMatch = filterSuffixMatch(firstMatch, fml.suffix);
			}
			
			String middle = fml.middle;
			if( middle == null ) {
				ret.addAll(suffixMatch);
			} else if( middle.length() == 0 ) {
				List<Voter> withMiddleInitial = filterMiddleNameEmpty(suffixMatch);
				ret.addAll(withMiddleInitial);
			} else if( middle.length() == 1 ) {
				List<Voter> withMiddleInitial = filterMiddleNameInitial(suffixMatch, middle);
				ret.addAll(withMiddleInitial);
			} else if( RegistrationUtil.isInitialsOnly(middle)) {
				List<Voter> withMiddleInitial = filterMiddleNameInitials(suffixMatch, middle);
				ret.addAll(withMiddleInitial);
			} else if( RegistrationUtil.hasAnyInitial(middle)) {
				List<Voter> withMiddleInitial = filterMiddleNameMixed(suffixMatch, middle);
				ret.addAll(withMiddleInitial);
			} else {
				List<Voter> withMiddleName = filterMiddleNameFull(suffixMatch, middle);
				ret.addAll(withMiddleName);
			}
			
		}
		return (Voter[]) ret.toArray(new Voter[ret.size()]);
	}


	public static class FirstMiddleLast {
		private String first;
		private String middle;
		private String last;
		private String suffix;
		public FirstMiddleLast(String f, String m, String l) {
			first = f;
			middle = m;
			last = l;
		}
		public FirstMiddleLast(String f, String m, String l, String s) {
			first = f;
			middle = m;
			last = l;
			suffix = s;
		}
		
	}
	
	public static class MovedVoter {
		
		private String name;
		private String addr1;
		private String addr2;
		private String deed;
		private String grey;
		private String green;
		private String blue;
		private String orange;
		public MovedVoter(String name, String addr1, String addr2, String deed, 
				String grey, String green, String blue, String orange) {
					this.name = name;
					this.addr1 = addr1;
					this.addr2 = addr2;
					this.deed = deed;
					this.grey = grey;
					this.green = green;
					this.blue = blue;
					this.orange = orange;
		}
		
		public String getName() {
			return name.trim();
		}
		public String getAddr1() {
			return addr1.trim();
		}
		public String getAddr2() {
			return addr2.trim();
		}
		public String getDeed() {
			return deed.trim();
		}
		public String getGrey() {
			return grey.trim();
		}

		public String getGreen() {
			return green.trim();
		}

		public String getBlue() {
			return blue.trim();
		}
		public String getOrange() {
			return orange.trim();
		}
		
		public COLOR getVolunteerColor() {
			if( "1".contentEquals(getGrey()))
				return COLOR.GREY;
			if( "1".contentEquals(getGreen()))
				return COLOR.GREEN;
			if( "1".contentEquals(getBlue()))
				return COLOR.BLUE;
			if( "1".contentEquals(getOrange()))
				return COLOR.ORANGE;
			return null;
		}
	}
	public  class ColorComment {
		private COLOR c;
		private String comment;
		double nameScore;
		double addrScore;
		double avgScore;
		String nameComment;
		String addrComment;
		String party;
		public ColorComment(COLOR c, double nameScore, double addrScore, 
				double avgScore, String party, String nameComment, String addrComment, String comment) {
			setC(c);
			setComment(comment);
			setNameScore(nameScore);
			setAddrScore(addrScore);
			setAvgScore(avgScore);
			setNameComment(nameComment);
			setAddrComment(addrComment);
			setParty(party);
		}
		public String getComment() {
			return comment;
		}
		public void setComment(String comment) {
			this.comment = comment;
		}
		public COLOR getC() {
			return c;
		}
		public void setC(COLOR c) {
			this.c = c;
		}
		public double getNameScore() {
			return nameScore;
		}
		public void setNameScore(double nameScore) {
			this.nameScore = nameScore;
		}
		public double getAddrScore() {
			return addrScore;
		}
		public void setAddrScore(double addrScore) {
			this.addrScore = addrScore;
		}
		public double getAvgScore() {
			return avgScore;
		}
		public void setAvgScore(double avgScore) {
			this.avgScore = avgScore;
		}
		public String getNameComment() {
			return nameComment;
		}
		public void setNameComment(String nameComment) {
			this.nameComment = nameComment;
		}
		public String getAddrComment() {
			return addrComment;
		}
		public void setAddrComment(String addrComment) {
			this.addrComment = addrComment;
		}
		public String getParty() {
			return party;
		}
		public void setParty(String party) {
			this.party = party;
		}
		
	}

	public static class DoubleFraction {
		private double d;
		private String comment;
		public DoubleFraction(double d, String comment) {
			this.d = d;
			this.comment = comment;
		}
	}
}
