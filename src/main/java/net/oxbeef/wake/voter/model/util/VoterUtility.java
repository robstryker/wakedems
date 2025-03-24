package net.oxbeef.wake.voter.model.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.SubdivisionStreet;

public class VoterUtility {
	
	public static HashMap<String, List<Voter>> votersByResidence(List<Voter> sdVoters) {
		HashMap<String, List<Voter>> addresses = new HashMap<String, List<Voter>>();
		Iterator<Voter> sdVoterIt = sdVoters.iterator();
		Voter v = null;
		while(sdVoterIt.hasNext()) {
			v = sdVoterIt.next();
			String strAddr = v.getFullStreetAddress();
			List<Voter> atAddr = addresses.get(strAddr);
			if( atAddr == null ) {
				atAddr = new ArrayList<Voter>();
				addresses.put(strAddr, atAddr);
			}
			atAddr.add(v);
		}
		return addresses;
	}

	public static ArrayList<Residence> toResidences(HashMap<String, List<Voter>> model) {
		ArrayList<Residence> rs = new ArrayList<Residence>();
		Iterator<String> keys = model.keySet().iterator();
		while(keys.hasNext()) {
			String k = keys.next();
			List<Voter> vals = model.get(k);
			Residence r = new Residence(k, vals);
			rs.add(r);
		}
		return rs;
	}
	
	
	public static List<IPrecinctSubdivision> findSubdivisionsForVoter(IPrecinct precinct, Voter v) {
		IPrecinctSubdivision[] subs = precinct.getSubdivisions();
		
		List<IPrecinctSubdivision> ret = new ArrayList<IPrecinctSubdivision>();
		final List<Voter> singletonList = Arrays.asList(new Voter[] {v});
		
		for( int i = 0; i < subs.length; i++ ) {
			List<Voter> inSub = findVotersInSubdivision(subs[i], singletonList);
			if( inSub.size() > 0 ) {
				ret.add(subs[i]);
			}
		}
		return ret;
	}

	
	public static List<Voter> findVotersInSubdivision(IPrecinctSubdivision sd, List<Voter> voters) {
		SubdivisionStreet[] streets = sd.getStreets();
		Set<Voter> ret = new HashSet<Voter>();
		for( int i = 0; i < streets.length; i++ ) {
			ret.addAll(findVotersOnStreet(streets[i], voters));
		}
		return new ArrayList<Voter>(ret);
	}
	
	
	public static List<Voter> findVotersOnStreet(SubdivisionStreet s, List<Voter> voters) {
		String street = s.getName();
    	Iterator<Voter> i = voters.iterator();
    	Voter v = null;
    	ArrayList<Voter> ret = new ArrayList<Voter>();
    	while(i.hasNext()) {
    		v = i.next();
    		if( v.getStreet().equalsIgnoreCase(street)) {
    			int num = v.getStreetNumberInt();
    			if( num <= s.getMax() && num >= s.getMin()) {
    				if( s.getType() == SubdivisionStreet.TYPE_ALL) { 
    					ret.add(v);
    				} else if( s.getType() == SubdivisionStreet.TYPE_EVEN) {
    					if( num % 2 == 0 ) 
    						ret.add(v);
    				} else if( s.getType() == SubdivisionStreet.TYPE_ODD) {
    					if( num % 2 != 0 )
    						ret.add(v);
    				}
    			}
    		}
    	}
    	return ret;
	}	

	public static void sortResidenceByStreetAndNumber(List<Residence> list) {
		list.sort(new Comparator<Residence>() {
			@Override
			public int compare(Residence o1, Residence o2) {
				String addr1 = o1.getAddr().trim();
				String addr2 = o2.getAddr().trim();
				int firstSpace1 = addr1.indexOf(' ');
				int firstSpace2 = addr2.indexOf(' ');
				String street1 = addr1.substring(firstSpace1+1);
				String street2 = addr2.substring(firstSpace2+1);
				if( !street1.equals(street2)) {
					return street1.compareTo(street2);
				}
				String num1 = addr1.substring(0, firstSpace1).trim();
				String num2 = addr2.substring(0, firstSpace2).trim();
				return Integer.parseInt(num1) - Integer.parseInt(num2);
			}
		});
	}
	
	public static List<Voter> findVotersOnStreet(String street, List<Voter> voters) {
    	Iterator<Voter> i = voters.iterator();
    	Voter v = null;
    	ArrayList<Voter> ret = new ArrayList<Voter>();
    	while(i.hasNext()) {
    		v = i.next();
    		if( v.getStreet().equalsIgnoreCase(street)) {
    			ret.add(v);
    		}
    	}
    	return ret;
    }
    
    public static double getAverageAge(List<Voter> l) {
    	Iterator<Voter> it = l.iterator();
    	Voter v = null;
    	double age, total;
    	total = 0;
    	while(it.hasNext()) {
    		v = it.next();
    		age = Integer.parseInt(v.getAge());
    		total += age;
    	}
    	return total / l.size();
    }
    
    // age = 30
    // rgstr date = 29

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
   }
    
    public static List<Voter> findDemVoters(Residence r) {
    	List<Voter> all = new ArrayList<Voter>(r.getVoters());
    	Iterator<Voter> it = all.iterator();
    	while(it.hasNext()) {
    		Voter v = it.next();
    		if( !v.getParty().equalsIgnoreCase("dem")) {
    			it.remove();
    		}
    	}
    	return all;
    }
    
    public static boolean residenceHasDems(Residence r) {
    	return findDemVoters(r).size() > 0;
    }
    
    public static Map<String, List<Voter>> getVotersByStreet(List<Voter> voters) {
    	HashMap<String, List<Voter>> ret = new HashMap<>();
    	for( Voter v : voters ) {
    		String street = v.getStreet();
    		List<Voter> streetList = ret.get(street);
    		if( streetList == null ) {
    			streetList = new ArrayList<Voter>();
    			ret.put(street,  streetList);
    		}
    		streetList.add(v);
    	}
    	return ret;
    }
    
    public static List<String> getUniqueParties(List<Voter> voters) {
    	ArrayList<String> parties = new ArrayList<String>();
    	for( Voter v : voters ) {
    		if( !parties.contains(v.getParty())) {
    			parties.add(v.getParty());
    		}
    	}
    	return parties;
    }
    
    public static List<Voter> getVotersOfParty(List<Voter> all, String party) {
    	ArrayList<Voter> ret = new ArrayList<Voter>();
    	for( Voter v : all) {
    		if( v.getParty().equals(party)) {
    			ret.add(v);
    		}
    	}
    	return ret;
    }
    
	public static String partisanInformation(List<Voter> list) {
		return partisanInformation(list, false);
	}
	public static String partisanInformation(List<Voter> list, boolean verbose) {
		DecimalFormat df = new DecimalFormat("#.###"); 
		StringBuffer sb = new StringBuffer();
		List<String> parties = VoterUtility.getUniqueParties(list);
		Collections.sort(parties);
		for( String party : parties ) {
			if( verbose ) { sb.append("\n          "); }
			List<Voter> fromParty = VoterUtility.getVotersOfParty(list, party);
			sb.append(party + "=" + fromParty.size() + " ");
			
			if( verbose ) {
				double v = ((double)fromParty.size()) * 100 / list.size();
				sb.append("(" + df.format(v) + "%)");
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
    public static HashMap<Voter, List<IPrecinctSubdivision>> findMissingOrMalformedVoters(VoterModel vm, IPrecinct precinct) {
    	List<Voter> all = vm.getAll();
    	Iterator<Voter> i = all.iterator();
    	Voter v = null;
    	ArrayList<Voter> wrong = new ArrayList<Voter>();
    	while(i.hasNext()) {
    		v = i.next();
//    		if( v.getName().toLowerCase().contains("calub")) {
//    			System.out.println("Break");
//    		}
    		List<IPrecinctSubdivision> subs = VoterUtility.findSubdivisionsForVoter(precinct, v);
    		if( subs.size() != 1 ) {
    			wrong.add(v);
    		}
    	}
    	
    	HashMap<Voter, List<IPrecinctSubdivision>> errors = new HashMap<>();
    	Iterator<Voter> vit = wrong.iterator();
    	while(vit.hasNext()) {
    		v = vit.next();
    		List<IPrecinctSubdivision> subs = VoterUtility.findSubdivisionsForVoter(precinct, v);
    		errors.put(v,  subs);
    	}
    	return errors;
    }
}
