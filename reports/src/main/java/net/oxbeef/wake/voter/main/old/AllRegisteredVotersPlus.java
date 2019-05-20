package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.sort.ResidenceComparator;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class AllRegisteredVotersPlus {
	public static void main(String[] args) throws IOException {
		new AllRegisteredVotersPlus().run(new String[] {"20-04"});
	}

	private String precinctDataLoc;
	private String definitionLoc;
	private String templateLoc;

	private String[] findLines(String[] all, String needle) {
		ArrayList<String> ret = new ArrayList<String>();
		for( int i = 0; i < all.length; i++ ) {
			if( all[i].toUpperCase().contains(needle)) {
				ret.add(all[i]);
			}
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}
	
	private String isVolunteer(String[] lines, String name) {
		return findLines(lines, name.toUpperCase()).length > 0 ? "Volunteer" : "";
	}
	
	private String getEmail(String[] lines, String name) {
		return getLineCol(lines, name.toUpperCase(), 1);
	}
	private String getPhone(String[] lines, String name) {
		return getLineCol(lines, name.toUpperCase(), 5);
	}
	private String getLineCol(String[] lines, String name, int col) {
		String[] match = findLines(lines, name);
		for( int i = 0; i < match.length; i++ ) {
			String[] split = match[i].split(",");
			if( col < split.length) {
				String col2 = split[col];
				if( col2 != null && !col2.isEmpty())
					return col2;
			}
		}
		return "";
	}

	
	public void run(String[] precincts) throws IOException {
		String[] rosterLines = null;
		try {
			File f = new File("/home/rob/Desktop/04-06.roster.csv");
			String content = new String(Files.readAllBytes(f.toPath()));
			rosterLines = content.split("\n");
			List<String> tmp = Arrays.asList(rosterLines);
			tmp = tmp.subList(6, tmp.size()-1);
			rosterLines = (String[]) tmp.toArray(new String[tmp.size()]);
		} catch(IOException ioe) {
			rosterLines = new String[0];
		}

		StringBuffer sb = new StringBuffer();
		
		
		String current = new File(".").getCanonicalPath();
		precinctDataLoc = current + "/resources/precincts/voters/";
		definitionLoc = current + "/resources/precincts/definitions/";
		templateLoc = current + "/resources/templates/";
		for( int i = 0; i < precincts.length; i++ ) {
			IPrecinct precinct = getPrecinct(precincts[i], definitionLoc);
			VoterModel vm = loadVoterModel(precincts[i], precinct);
			IPrecinctSubdivision[] subs = precinct.getSubdivisions();
			sb.append(precinct.getName());
			sb.append("\n");
			for( int j = 0; j < subs.length; j++ ) {
				IPrecinctSubdivision sd = subs[j];
				List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
				HashMap<String, List<Voter>> addresses = VoterUtility.votersByResidence(sdVoters);
				List<Residence> rs = VoterUtility.toResidences(addresses);
				Collections.sort(rs, getAddressComparator());
				sb.append("\n,Subdivision,Address,Voter Name,Age,Party,Voting Record\n,,,Email,Volunteer,,Phone\n");
				sb.append("," );
				sb.append(sd.getName());
				sb.append("\n");
				Iterator<Residence> rit = rs.iterator();
				Residence next = null;
				while(rit.hasNext()) {
					next = rit.next();
					printVoters(rosterLines, next, sb);
				}
			}
		}
		System.out.println(sb.toString());
	}
	
	private void printVoters(String[] rosterLines, Residence next, StringBuffer sb) {
		sb.append(",,");
		sb.append(next.getAddr());
		List<Voter> voters = next.getVoters();
		Iterator<Voter> vit = voters.iterator();
		Voter vitNext = null;
		boolean firstVoter = true;
		while(vit.hasNext()) {
			vitNext = vit.next();
			String name = vitNext.getName().replaceAll("\"", "");
			String[] arr = new String[] {
					name, vitNext.getAge(), vitNext.getParty(), 
					vitNext.getRecentElections(5)
			};
			
			String vol = isVolunteer(rosterLines, name);
			String email = getEmail(rosterLines, name);
			String phone = getPhone(rosterLines, name);
			String[] arr2 = new String[0];
			if( !isEmpty(vol) || !isEmpty(email) || !isEmpty(phone)) {
				arr2 = new String[] {email, vol, "", phone};
			}
			if( firstVoter ) {
				sb.append(",");
				firstVoter = false;
			} else {
				sb.append(",,,");
			}
			sb.append(String.join(",", Arrays.asList(arr)));
			if( arr2.length != 0 ) {
				sb.append("\n,,,");
				sb.append(String.join(",", Arrays.asList(arr2)));
			}
			sb.append("\n");
		}
	}
	
	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}
	
	private Comparator<Residence> getAddressComparator() {
		return new ResidenceComparator();
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".csv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}
