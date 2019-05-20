package net.oxbeef.wake.voter.model;

import java.util.List;

public class Residence {
	private String addr;
	private List<Voter> voters;
	public Residence(String addr, List<Voter> voters) {
		this.addr = addr;
		this.voters = voters;
	}
	
	public String getAddr() {
		return addr;
	}
	public List<Voter> getVoters() {
		return voters;
	}
	public int getNumVoters() {
		return voters.size();
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Residence && addr.equals(((Residence)other).addr);
	}
	
	@Override
	public int hashCode() {
		return addr.hashCode();
	}
}
