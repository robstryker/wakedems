package net.oxbeef.wake.voter.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;

public class ModelCacher {
	private MainModel model;
	private HashMap<String, PrecinctCacher> map = new HashMap<String, PrecinctCacher>();
	public ModelCacher(MainModel model) {
		this.model = model;
		this.map = new HashMap<>();
		String[] precincts = model.listAllPrecincts();
		for( int i = 0; i < precincts.length; i++ ) {
			PrecinctCacher pc = new PrecinctCacher(precincts[i], model.getOrCreateVoterModel(precincts[i]));
			map.put(precincts[i], pc);
		}
	}
	
	
	public ArrayList<Voter> findVotersOnStreet(String street) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( PrecinctCacher pc : map.values()) {
			ArrayList<Voter> onStreet = pc.streetToVoters.get(street);
			if( onStreet != null )
				ret.addAll(onStreet);
		}
		return ret;
	}

	public ArrayList<Voter> findVotersLastName(String lastName) {
		ArrayList<Voter> ret = new ArrayList<>();
		for( PrecinctCacher pc : map.values()) {
			ArrayList<Voter> match = pc.lastNameToVoters.get(lastName);
			if( match != null )
				ret.addAll(match);
		}
		return ret;
	}

	
	public static class PrecinctCacher {

		private VoterModel vm;
		private String pid;
		private HashMap<String, ArrayList<Voter>> lastNameToVoters;
		private HashMap<String, ArrayList<Voter>> streetToVoters;

		public PrecinctCacher(String pid, VoterModel m) {
			this.vm = m;
			this.pid = pid;
			lastNameToVoters = new HashMap<>();
			this.streetToVoters = new HashMap<>();
			
			List<Voter> all = vm.getAll();
			for( Voter v : all ) {
				String lastName = v.getLastName();
				String street = v.getStreet();
				ArrayList<Voter> withLastName = lastNameToVoters.get(lastName);
				if( withLastName == null ) {
					withLastName = new ArrayList<Voter>();
					lastNameToVoters.put(lastName, withLastName);
				}
				withLastName.add(v);
				
				ArrayList<Voter> onStreet = streetToVoters.get(street);
				if( onStreet == null ) {
					onStreet = new ArrayList<Voter>();
					streetToVoters.put(street, onStreet);
				}
				onStreet.add(v);
			}
		}
		
	}
}
