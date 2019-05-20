package net.oxbeef.wake.voter.model.precinct;

import java.util.ArrayList;

public class PrecinctSubdivision implements IPrecinctSubdivision {
	private String name;
	private ArrayList<Street> streets;
	public PrecinctSubdivision(String name) {
		this.name = name;
		streets = new ArrayList<Street>();
	}
	public void addStreet(String name) {
		streets.add(new Street(name));
	}
	public void addStreet(String name, int min, int max) {
		streets.add(new Street(name, min, max));
	}
	public void addStreet(String name, int min, int max, int type) {
		streets.add(new Street(name, min, max, type));
	}
	public void addStreet(String name, int type) {
		streets.add(new Street(name, type));
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Street[] getStreets() {
		return (Street[]) streets.toArray(new Street[streets.size()]);
	}

}
