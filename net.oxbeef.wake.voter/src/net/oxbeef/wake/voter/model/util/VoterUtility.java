package net.oxbeef.wake.voter.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.Street;

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
		Street[] streets = sd.getStreets();
		Set<Voter> ret = new HashSet<Voter>();
		for( int i = 0; i < streets.length; i++ ) {
			ret.addAll(findVotersOnStreet(streets[i], voters));
		}
		return new ArrayList<Voter>(ret);
	}
	
	
	public static List<Voter> findVotersOnStreet(Street s, List<Voter> voters) {
		String street = s.getName();
    	Iterator<Voter> i = voters.iterator();
    	Voter v = null;
    	ArrayList<Voter> ret = new ArrayList<Voter>();
    	while(i.hasNext()) {
    		v = i.next();
    		if( v.getStreet().equalsIgnoreCase(street)) {
    			int num = v.getStreetNumberInt();
    			if( num <= s.getMax() && num >= s.getMin()) {
    				if( s.getType() == Street.TYPE_ALL) { 
    					ret.add(v);
    				} else if( s.getType() == Street.TYPE_EVEN) {
    					if( num % 2 == 0 ) 
    						ret.add(v);
    				} else if( s.getType() == Street.TYPE_ODD) {
    					if( num % 2 != 0 )
    						ret.add(v);
    				}
    			}
    		}
    	}
    	return ret;
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
}
