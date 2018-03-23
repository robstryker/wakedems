package net.oxbeef.wake.voter.model.util;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.IPrecinctSubdivision;

public interface IOutputFormat {
	public void begin(IPrecinct p);
	public void beginSubdivision(IPrecinct p, IPrecinctSubdivision sd);
	public void printResidence(IPrecinct p, IPrecinctSubdivision sd, Residence r);
	public void endSubdivision(IPrecinct p, IPrecinctSubdivision sd);
	public void end(IPrecinct p);
}
