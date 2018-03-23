package net.oxbeef.wake.voter.model;
import java.util.ArrayList;
import java.util.Set;

public class VoterModel {
	private ArrayList<Voter> all;
	private ArrayList<Voter> dems;
	private ArrayList<Voter> reps;
	private ArrayList<Voter> other;
	private Set<String> streetsSet;

	public VoterModel(ArrayList<Voter> all, ArrayList<Voter> dems, ArrayList<Voter> reps, ArrayList<Voter> other,
			Set<String> streetsSet) {
				this.all = all;
				this.dems = dems;
				this.reps = reps;
				this.other = other;
				this.streetsSet = streetsSet;
	}

	public ArrayList<Voter> getAll() {
		return all;
	}

	public ArrayList<Voter> getDems() {
		return dems;
	}

	public ArrayList<Voter> getReps() {
		return reps;
	}

	public ArrayList<Voter> getOther() {
		return other;
	}

	public Set<String> getStreetsSet() {
		return streetsSet;
	}
}
