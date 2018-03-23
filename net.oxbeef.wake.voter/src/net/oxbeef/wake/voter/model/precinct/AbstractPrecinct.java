package net.oxbeef.wake.voter.model.precinct;

import java.util.ArrayList;

import net.oxbeef.wake.voter.model.IVoterFilter;
import net.oxbeef.wake.voter.model.Voter;

public abstract class AbstractPrecinct implements IPrecinct {
	private String name;
	private String id;
	private ArrayList<IPrecinctSubdivision> divisions;
	public AbstractPrecinct(String name, String id) {
		this.id = id;
		this.name = name;
		divisions = new ArrayList<IPrecinctSubdivision>();
		addSubdivisions();
	}
	
	public String getName() {
		return name;
	}
	
	public IPrecinctSubdivision[] getSubdivisions() {
		return (IPrecinctSubdivision[]) divisions.toArray(new IPrecinctSubdivision[divisions.size()]);
	}
	
	public abstract void addSubdivisions();
	
	public void addSubdivision(IPrecinctSubdivision sub) {
		divisions.add(sub);
	}
	
	public PrecinctSubdivision createSubdivision(String name) {
		return new PrecinctSubdivision(name);
	}

	
	
	public String getId() {
		return id;
	}
	
	private IVoterFilter precinctFilter;
	public IVoterFilter getPrecinctFilter() {
		if( precinctFilter == null ) {
			precinctFilter = new IVoterFilter() {
				@Override
				public boolean accepts(Voter v) {
					String p = v.getPrecinct();
					return getId().equals(p);
				}
			};
		}
		return precinctFilter;
	}
}
