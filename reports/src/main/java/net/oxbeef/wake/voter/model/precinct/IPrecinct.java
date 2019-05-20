package net.oxbeef.wake.voter.model.precinct;

import net.oxbeef.wake.voter.model.IVoterFilter;

public interface IPrecinct {
	public String getId();
	public String getName();
	public IVoterFilter getPrecinctFilter();
	public IPrecinctSubdivision[] getSubdivisions();
}
