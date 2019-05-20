package net.oxbeef.wake.voter.model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.Street;
import net.oxbeef.wake.voter.model.sort.BestDemocratResidenceComparator;
import net.oxbeef.wake.voter.model.sort.StreetComparator;

public class OutputUtilities {

	private static final int MOST_ACTIVE_TO_FIND_PER_SUBDIVISION = 13;
	
	public static void findMostActiveDemHousesBySubdivision(VoterModel vm, IPrecinct precinct, IOutputFormat format) {
		findMostActiveDemHousesBySubdivision(vm, precinct, format, MOST_ACTIVE_TO_FIND_PER_SUBDIVISION);
	}
	public static void findMostActiveDemHousesBySubdivision(VoterModel vm, IPrecinct precinct, IOutputFormat format, int perSub) {
		findMostActiveDemHousesBySubdivision(vm, precinct, format, perSub, new BestDemocratResidenceComparator());
	}
	
	public static void findMostActiveDemHousesBySubdivision(VoterModel vm, IPrecinct precinct, IOutputFormat format, int perSub, Comparator<Residence> comp) {
		format.begin(precinct);
		IPrecinctSubdivision[] subs = precinct.getSubdivisions();
		for( int i = 0; i < subs.length; i++ ) {
			IPrecinctSubdivision sd = subs[i];
			format.beginSubdivision(precinct, sd);
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			HashMap<String, List<Voter>> addresses = VoterUtility.votersByResidence(sdVoters);
			List<Residence> rs = VoterUtility.toResidences(addresses);
			Collections.sort(rs, comp);
			Iterator<Residence> rit = rs.iterator();
			int c = 0;
			while(rit.hasNext() && c < perSub) {
				Residence r3 = rit.next();
				format.printResidence(precinct, sd, r3);
				c++;
			}
			format.endSubdivision(precinct, sd);
		}
		format.end(precinct);
	}
	
	public static void findMostActiveDemResidences(VoterModel vm, IPrecinct precinct, IOutputFormat format, int total) {
		format.begin(precinct);
		List<Voter> all = vm.getAll();
		HashMap<String, List<Voter>> allByResidence = VoterUtility.votersByResidence(all);
		List<Residence> allResidences = VoterUtility.toResidences(allByResidence);
		Collections.sort(allResidences, new BestDemocratResidenceComparator());
		
		List<Residence> topResidences = allResidences.subList(0,  total);
		
		
		IPrecinctSubdivision[] subs = precinct.getSubdivisions();
		for( int i = 0; i < subs.length; i++ ) {
			IPrecinctSubdivision sd = subs[i];
			format.beginSubdivision(precinct, sd);
			List<Voter> sdVoters = VoterUtility.findVotersInSubdivision(sd, vm.getAll());
			HashMap<String, List<Voter>> addresses = VoterUtility.votersByResidence(sdVoters);
			List<Residence> rs = VoterUtility.toResidences(addresses);
			Collections.sort(rs, new BestDemocratResidenceComparator());
			Iterator<Residence> rit = rs.iterator();
			while(rit.hasNext()) {
				Residence r3 = rit.next();
				if( topResidences.contains(r3)) {
					format.printResidence(precinct, sd, r3);
				}
			}
			format.endSubdivision(precinct, sd);
		}
		format.end(precinct);
	}

    public static void findMissingOrMalformedVoters(VoterModel vm, IPrecinct precinct) {
    	ArrayList<Voter> all = vm.getAll();
    	Iterator<Voter> i = all.iterator();
    	Voter v = null;
    	ArrayList<Voter> wrong = new ArrayList<Voter>();
    	while(i.hasNext()) {
    		v = i.next();
    		if( v.getName().toLowerCase().contains("calub")) {
    			System.out.println("Break");
    		}
    		List<IPrecinctSubdivision> subs = VoterUtility.findSubdivisionsForVoter(precinct, v);
    		if( subs.size() != 1 ) {
    			wrong.add(v);
    		}
    	}
    	
    	
    	Iterator<Voter> vit = wrong.iterator();
    	while(vit.hasNext()) {
    		v = vit.next();
    		List<IPrecinctSubdivision> subs = VoterUtility.findSubdivisionsForVoter(precinct, v);
    		System.out.println(v.getName() + " - " + v.getStreetNumber() + " " + v.getStreet() 
    		+ " matches " + subs.size() + " precincts: ");
    		Iterator<IPrecinctSubdivision> subIt = subs.iterator();
    		while(subIt.hasNext()) {
    			System.out.println("   " + subIt.next().getName());
    		}
    	}
    	
    }
    
    
    
    
    public static void printBySubdivision(VoterModel vm, IPrecinct precinct) {
        ArrayList<Voter> all = vm.getAll();
        ArrayList<Voter> dems = vm.getDems();
        ArrayList<Voter> reps = vm.getReps();
        ArrayList<Voter> other = vm.getOther();
        
    	IPrecinctSubdivision[] subs = precinct.getSubdivisions();
    	for( int i = 0; i < subs.length; i++ ) {
    		System.out.println(subs[i].getName());
    		Street[] streets = subs[i].getStreets();
    		for( int j = 0; j < streets.length; j++ ) {
    			Street s = streets[j];
            	printStreet(s, dems, reps, other, all);
    		}
        	System.out.println("\n\n");
    	}
	}

	public static void printAllByStreet(VoterModel vm) {
        Set<String> streetsSet = vm.getStreetsSet();
        String[] streets = (String[]) streetsSet.toArray(new String[streetsSet.size()]);
        List<String> ordered = Arrays.asList(streets);
        Collections.sort(ordered);
        streets = (String[]) ordered.toArray(new String[ordered.size()]);
        
        ArrayList<Voter> all = vm.getAll();
        ArrayList<Voter> dems = vm.getDems();
        ArrayList<Voter> reps = vm.getReps();
        ArrayList<Voter> other = vm.getOther();
        
        
        printSummary(all, dems, reps, other);
        
        for( int k = 0; k < streets.length; k++ ) {
        	String s = streets[k];
        	printStreet(s, dems, reps, other, all);
        }
	}

	public static void printStreet(Street s, ArrayList<Voter> dems, ArrayList<Voter> reps, ArrayList<Voter> other, ArrayList<Voter> all) {
    	List<Voter> demStreet, repStreet, otherStreet, allStreet;
    	demStreet = VoterUtility.findVotersOnStreet(s, dems);
    	repStreet = VoterUtility.findVotersOnStreet(s, reps);
    	otherStreet = VoterUtility.findVotersOnStreet(s, other);
    	allStreet = VoterUtility.findVotersOnStreet(s, all);

    	Comparator<Voter> vComp = new StreetComparator();
    	allStreet.sort(vComp);
    	
        System.out.println("   " + s.getName() + ":");
        Iterator<Voter> streetIt = allStreet.iterator();
        Voter streetV = null;
        while(streetIt.hasNext()) {
        	streetV = streetIt.next();
        	printVoter(streetV);
        }
	}
	
	public static void printStreet(String s, ArrayList<Voter> dems, ArrayList<Voter> reps, ArrayList<Voter> other, ArrayList<Voter> all) {
    	List<Voter> demStreet, repStreet, otherStreet, allStreet;
    	demStreet = VoterUtility.findVotersOnStreet(s, dems);
    	repStreet = VoterUtility.findVotersOnStreet(s, reps);
    	otherStreet = VoterUtility.findVotersOnStreet(s, other);
    	allStreet = VoterUtility.findVotersOnStreet(s, all);

    	Comparator<Voter> vComp = new StreetComparator();
    	allStreet.sort(vComp);
    	
        System.out.println(s + ": " + allStreet.size() + " registered, " 
        		+ demStreet.size() + "-D, " + repStreet.size() + "-R, " + otherStreet.size() + "-Other");
        Iterator<Voter> streetIt = allStreet.iterator();
        Voter streetV = null;
        while(streetIt.hasNext()) {
        	streetV = streetIt.next();
        	printVoter(streetV);
        }
	}

	public static void printSummary(ArrayList<Voter> all, ArrayList<Voter> dems, ArrayList<Voter> reps,
			ArrayList<Voter> other) {
        System.out.println("Registered Voters: " + all.size());
        System.out.println("Registered Democrats: " + dems.size());
        System.out.println("Registered Republicans: " + reps.size());
        System.out.println("Registered Others: " + other.size());
        System.out.println();
        System.out.println("Avg Age: " + VoterUtility.getAverageAge(all));
        System.out.println("Avg Age (D): " + VoterUtility.getAverageAge(dems));
        System.out.println("Avg Age (R): " + VoterUtility.getAverageAge(reps));
        System.out.println("Avg Age (Other): " + VoterUtility.getAverageAge(other));
        System.out.println();
	}

	public static void printVoter(Voter voter) {
		new StandardOutput().printVoter(voter);
    }
	
	
	

    public static void printAsJSON(IPrecinct p) {
    	IPrecinctSubdivision[] sds = p.getSubdivisions();
    	
    	System.out.println("{");
    	System.out.println("\t\"name\": \"" + p.getName() + "\",");
    	System.out.println("\t\"id\": \"" + p.getId() + "\",");
    	System.out.println("\t\"subdivision\": [");
    	
    	String suffix = null;
    	for( int i = 0; i < sds.length; i++ ) {
    		suffix = ( i == sds.length - 1 ) ? "" : ",";
    		System.out.println("\t\t{ \"name\":\"" + sds[i].getName() + "\", \"streets\": [");
    		
    		Street[] streets = sds[i].getStreets();
    		String suffix2 = null;
    		for( int j = 0; j < streets.length; j++ ) {
    			suffix2 = ( j == streets.length - 1 ) ? "" : ",";
    			
    			StringBuffer streetContent = new StringBuffer(); 
    			streetContent.append("\t\t\t{ ");

    			streetContent.append("\"name\":\"");
    			streetContent.append(streets[j].getName());
    			streetContent.append("\"");

    			if( streets[j].getMin() != Integer.MIN_VALUE) {
	    			streetContent.append(", \"min\":\"");
	    			streetContent.append(streets[j].getMin());
	    			streetContent.append("\"");
    			}
    			
    			if( streets[j].getMax() != Integer.MAX_VALUE) {
	    			streetContent.append(", \"max\":\"");
	    			streetContent.append(streets[j].getMax());
	    			streetContent.append("\"");
    			}
    			
    			int type = streets[j].getType();
    			if( type != Street.TYPE_ALL) {
	    			String typeString = null;
	    			if( type == Street.TYPE_EVEN)
	    				typeString = "even";
	    			else if( type == Street.TYPE_ODD)
	    				typeString = "odd";
	    			else 
	    				typeString = "all";
	
	    			streetContent.append(", \"type\":\"");
	    			streetContent.append(typeString);
	    			streetContent.append("\"");
    			}
    			
    			streetContent.append("}");
    			streetContent.append(suffix2);
    			System.out.println(streetContent.toString());
    		}
    		
    		System.out.println("\t\t] }" + suffix);
    	}
    	
    	System.out.println("\t]");
    	System.out.println("}");
    }
    
}
