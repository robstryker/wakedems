package net.oxbeef.wake.voter.model.util;

import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.dems.BestDemocratResidenceComparator;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;

public class StandardOutput implements IOutputFormat {

	public StandardOutput() {
	}

	@Override
	public void beginSubdivision(IPrecinct p, IPrecinctSubdivision sd) {
		System.out.println("\n\n\n" + sd.getName());
	}

	@Override
	public void printResidence(IPrecinct p, IPrecinctSubdivision sd, Residence r) {
		System.out.println(" - " + r.getAddr() + ",   Score=" + new BestDemocratResidenceComparator().getScore(r));
		List<Voter> l = r.getVoters();
		Iterator<Voter> lit = l.iterator();
		Voter v3 = null;
		while(lit.hasNext()) {
			v3 = lit.next();
			printVoterInternal(v3);
		}
	}
	
	protected void printVoterInternal(Voter v) {
		printVoter(v);
	}
	
	public static void printVoter(Voter voter) {
		String n = voter.getName();
		String fullStreet = voter.getFullStreetAddress();
    	String party = voter.getParty().substring(0,1);
    	String vDate = voter.getRegistrationDate();
    	double pct = voter.getVotingPercentage(vDate);
    	String pctg = voter.getPctgString(pct);
    	vDate = vDate.substring(0,4);
    	String apt = voter.getApartmentNumber();
    	String cleanApt = apt.isEmpty() ? "" : " #" + apt + " ";
    	System.out.println("      " + fullStreet + " : " + VoterUtility.padRight(voter.getName(),25) + 
    			" (" + voter.getAge() + "/" + voter.getParty().substring(0,1) + ") reg-"
    			+ vDate + " v%: " + pctg + "  " + voter.getRecentElections(5));
    }

	@Override
	public void begin(IPrecinct p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSubdivision(IPrecinct p, IPrecinctSubdivision sd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void end(IPrecinct p) {
		// TODO Auto-generated method stub
		
	}

}
