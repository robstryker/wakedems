package net.oxbeef.wake.voter.model.precinct;

import java.util.ArrayList;

public class PrecinctSubdivision implements IPrecinctSubdivision {
	private String name;
	private ArrayList<SubdivisionStreet> streets;
	public PrecinctSubdivision(String name) {
		this.name = name;
		streets = new ArrayList<SubdivisionStreet>();
	}
	public void addStreet(String name) {
		streets.add(new SubdivisionStreet(name));
	}
	public void addStreet(String name, int min, int max) {
		streets.add(new SubdivisionStreet(name, min, max));
	}
	public void addStreet(String name, int min, int max, int type) {
		streets.add(new SubdivisionStreet(name, min, max, type));
	}
	public void addStreet(String name, int type) {
		streets.add(new SubdivisionStreet(name, type));
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public SubdivisionStreet[] getStreets() {
		return (SubdivisionStreet[]) streets.toArray(new SubdivisionStreet[streets.size()]);
	}

}
