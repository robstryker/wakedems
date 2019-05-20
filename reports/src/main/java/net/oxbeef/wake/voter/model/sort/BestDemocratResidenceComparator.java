package net.oxbeef.wake.voter.model.sort;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;

public class BestDemocratResidenceComparator implements Comparator<Residence>{
	@Override
	public int compare(Residence o1, Residence o2) {
		return getScore(o2) - getScore(o1);
	}
	
	private static final int DEM_TOTAL_PCTG_WEIGHT = 25;
	private static final int DEM_AVG_PCTG_WEIGHT = 35;
	private static final int DEM_HIGHEST_PCTG_WEIGHT = 30;
	private static final int NOT_TOO_OLD = 10;
	private static final int PCT_DEM = 10;
	
	
	
	public int getScore(Residence r) {
		int score = 0;
		double votingPctg1 = getDemVotingPercentages(r);
		if( votingPctg1 > 1 ) {
			score += DEM_TOTAL_PCTG_WEIGHT;
		} else {
			score += (DEM_TOTAL_PCTG_WEIGHT*votingPctg1);
		}
		
		double avgVotingPctg = getAvgDemVotingPercentages(r);
		score += (DEM_AVG_PCTG_WEIGHT*avgVotingPctg);
		
		
		double highest = getHighestDemVotingPercentages(r);
		score += (DEM_HIGHEST_PCTG_WEIGHT * highest);
		
		double avgAge = getAvgDemAge(r);
		if( avgAge < 85 ) {
			int dif = (85 - (int)avgAge);
			score += Math.min(dif, NOT_TOO_OLD);
		}
		
		score += (netDems(r) * PCT_DEM);
		
		return score;
	}
	
	
	private double netDems(Residence o) {
		Iterator<Voter> vit = o.getVoters().iterator();
		int count = 0;
		String p = null;
		while(vit.hasNext()) {
			p = vit.next().getParty();
			if( p.equalsIgnoreCase("dem")) {
				count++;
			} else if( !p.equalsIgnoreCase("una")) {
				count--;
			}
		}
		return count / o.getVoters().size();
	}
	
	/**
	 * Active republicans / libertarians count as a negative
	 * Active dems count as a positive
	 * Active unaffiliated have no affect. 
	 * 
	 * @param o
	 * @return
	 */
	private double getDemVotingPercentages(Residence o) {
		List<Voter> l = o.getVoters();
		Iterator<Voter> lit = l.iterator();
		Voter v = null;
		double total = 0;
		while(lit.hasNext()) {
			v = lit.next();
			if( v.getParty().equalsIgnoreCase("dem")) {
				total += v.getVotingPercentage(v.getRegistrationDate());
			} else if( v.getParty().equalsIgnoreCase("rep")) {
				total -= v.getVotingPercentage(v.getRegistrationDate());
			}
		}
		return total;
	}
	
	private double getAvgDemVotingPercentages(Residence o) {
		List<Voter> l = o.getVoters();
		Iterator<Voter> lit = l.iterator();
		Voter v = null;
		double count = 0;
		double total = 0;
		while(lit.hasNext()) {
			v = lit.next();
			if( v.getParty().equalsIgnoreCase("dem")) {
				total += v.getVotingPercentage(v.getRegistrationDate());
				count++;
			} 
		}
		if( count == 0 )
			return -1;
		return total / count;
	}
	
	private double getHighestDemVotingPercentages(Residence o) {
		List<Voter> l = o.getVoters();
		Iterator<Voter> lit = l.iterator();
		Voter v = null;
		double highest = 0;
		double tmp;
		while(lit.hasNext()) {
			v = lit.next();
			if( v.getParty().equalsIgnoreCase("dem")) {
				tmp = v.getVotingPercentage(v.getRegistrationDate());
				if( tmp > highest )
					highest = tmp;
			} 
		}
		return highest;
	}

	
	

	private double getAvgDemAge(Residence o) {
		List<Voter> l = o.getVoters();
		Iterator<Voter> lit = l.iterator();
		Voter v = null;
		double count = 0;
		double total = 0;
		while(lit.hasNext()) {
			v = lit.next();
			if( v.getParty().equalsIgnoreCase("dem")) {
				total += v.getAgeInt();
				count++;
			} 
		}
		if( count == 0 )
			return -1;
		return total / count;
	}

}
