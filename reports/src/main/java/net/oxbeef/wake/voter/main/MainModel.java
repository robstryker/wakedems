package net.oxbeef.wake.voter.main;

import java.io.File;
import java.util.HashMap;

import net.oxbeef.wake.voter.model.ChangedPartyModel;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.data.source.ExternalDataSource;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;

public class MainModel {
	private HashMap<String, VoterModel> modelMap;
	private HashMap<String, ChangedPartyModel> changedPartyModel;
	
	public MainModel() {
		modelMap = new HashMap<>();
		changedPartyModel = new HashMap<>();
	}
	
	public ChangedPartyModel getChangedPartyModel(int county) {
		String asString = Integer.toString(county);
		if( changedPartyModel.get(asString) == null ) {
			// TODO maybe expand to not hard-code
			ChangedPartyModel cpm = new ChangedPartyModel(county);
			changedPartyModel.put(asString, cpm);
		}
		return changedPartyModel.get(asString);
	}
	
	public VoterModel getOrCreateVoterModel(String precinctId) {
		if( modelMap.containsKey(precinctId)) {
			return modelMap.get(precinctId);
		}
		

		String precinctDataLoc = ExternalDataSource.getInstance().getPrecinctDataLoc();
		String definitionLoc = ExternalDataSource.getInstance().getDefinitionLoc();

		IPrecinct precinct = getPrecinct(precinctId, definitionLoc);
		VoterModel vm = null;
		if (precinct != null) {
			vm = loadVoterModel(precinct.getId(), precinct, precinctDataLoc);
		} else {
			vm = loadVoterModel(precinctId, precinctDataLoc);
		}
		
		modelMap.put(precinctId,  vm);
		return vm;
	}

	private VoterModel loadVoterModel(String precinctId, String precinctDataLoc) {
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName));
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct, String precinctDataLoc) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}

}