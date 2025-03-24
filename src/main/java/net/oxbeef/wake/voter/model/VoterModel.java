package net.oxbeef.wake.voter.model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VoterModel {
	private ArrayList<Voter> dems;
	private ArrayList<Voter> reps;
	private ArrayList<Voter> other;
	private Set<String> streetsSet;

	public VoterModel(ArrayList<Voter> dems, ArrayList<Voter> reps, ArrayList<Voter> other,
			Set<String> streetsSet) {
				this.dems = dems;
				this.reps = reps;
				this.other = other;
				this.streetsSet = streetsSet;
	}

	public List<Voter> getAll() {
		ArrayList<Voter> tmp = new ArrayList<>();
		tmp.addAll(dems);
		tmp.addAll(reps);
		tmp.addAll(other);
		return Collections.unmodifiableList(tmp);
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
