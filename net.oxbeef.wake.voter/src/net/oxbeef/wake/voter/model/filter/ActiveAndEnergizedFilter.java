package net.oxbeef.wake.voter.model.filter;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;

public class ActiveAndEnergizedFilter implements IVoterFilter {
	private IVoterFilter delegate;
	public ActiveAndEnergizedFilter(IVoterFilter delegate) {
		this.delegate = delegate;
	}
	@Override
	public boolean accepts(Voter voter) {
    	String party = voter.getParty().substring(0,1);
    	String vDate = voter.getRegistrationDate();
    	double pct = voter.getVotingPercentage(vDate);
    	return delegate.accepts(voter) && "d".equalsIgnoreCase(party) 
    			&& pct >= 0.4 && voter.getAgeInt() < 85;
	}

}
