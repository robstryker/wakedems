package net.oxbeef.wakedems2.mains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wake.voter.model.util.VoterUtility;
import net.oxbeef.wakedems2.datastore.ExternalDataStore;
import net.oxbeef.wakedems2.datastore.Settings;

public class CheckMain {

	public void check(ExternalDataStore dataStore, Settings settings) {
    	String county = settings.getCounty();
    	String precinctId = settings.getPrecinct();
		System.out.println("Checking county=" + county + ", precinct=" + precinctId);
    	
    	IPrecinct precinct = PrecinctCore.getPrecinct(county, precinctId);
    	
    	MainModel mm = new MainModel();
    	VoterModel vm = mm.getOrCreateVoterModel(county,  precinctId);
    	HashMap<Voter, List<IPrecinctSubdivision>> errors = VoterUtility.findMissingOrMalformedVoters(vm, precinct);
    	List<Voter> keys = new ArrayList<>(errors.keySet()).stream()
    			.filter(x -> !x.getStreet().equals("CONFIDENTIAL")).collect(Collectors.toList());
    	Collections.sort(keys, new Comparator<Voter>() {
			@Override
			public int compare(Voter o1, Voter o2) {
				return o1.getStreet().compareTo(o2.getStreet());
			}});
    	
    	System.out.println("There are " + keys.size() + " voters not assigned to a specific subdivision...");
    	
    	String s = "\n   " + String.join("\n   ", new ArrayList<String>(keys.stream().map(x -> x.getStreet()).collect(Collectors.toSet())));
    	System.out.println("Streets with problems: " + s);
    	System.out.println();
    	for( Voter v : keys) {
    		List<IPrecinctSubdivision> subs = errors.get(v);
        	System.out.println(v.getName() + " - " + v.getStreetNumber() + " " + v.getStreet() 
        	+ " matches " + subs.size() + " precincts: ");
        	Iterator<IPrecinctSubdivision> subIt = subs.iterator();
        	while(subIt.hasNext()) {
        		System.out.println("   " + subIt.next().getName());
        	}
    	}
    	
    	if( keys.size() == 0 ) {
    		System.out.println("Voters / residences per subdivision: ");
    		IPrecinctSubdivision[] subs = precinct.getSubdivisions();
    		for( int i = 0; i < subs.length; i++ ) {
        		List<Voter> inSub = VoterUtility.findVotersInSubdivision(subs[i], vm.getAll());
        		HashMap<String, List<Voter>> byRes = VoterUtility.votersByResidence(inSub);
        		System.out.println("   " + subs[i].getName() + ": " + inSub.size() + " / " + byRes.size());
    		}
    	}
	}
}
