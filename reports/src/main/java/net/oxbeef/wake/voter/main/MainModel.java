package net.oxbeef.wake.voter.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.oxbeef.wake.voter.model.ChangedPartyModel;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.data.source.ExternalDataSource;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;

public class MainModel {
	private HashMap<String, VoterModel> modelMap;
	private HashMap<String, ChangedPartyModel> changedPartyModel;
	private PreviousStatewideVoterModel previousStateModel;
	private boolean previousStateModelFailed;
	
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
		

		String precinctDataLoc = getPrecinctDataLoc();
		String definitionLoc = getPrecinctDefinitionLoc();

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

	protected String getPrecinctDefinitionLoc() {
		return ExternalDataSource.getInstance().getDefinitionLoc();
	}

	protected String getPrecinctDataLoc() {
		return ExternalDataSource.getInstance().getPrecinctDataLoc();
	}
	
	private VoterModel loadVoterModel(String precinctId, String precinctDataLoc) {
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName));
	}
	
	public String[] listAllPrecincts() {
		ArrayList<String> ret = new ArrayList<>();
		String root = getPrecinctDataLoc();
		File f = new File(root);
		File[] children = f.listFiles();
		for( int i = 0; i < children.length; i++ ) {
			String name = children[i].getName();
			if( name.endsWith(".tsv")) {
				ret.add(name.substring(0, name.length()-4));
			}
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}
	
	private VoterModel loadVoterModel(String precinctId, IPrecinct precinct, String precinctDataLoc) {
		// The name of the file to open.
		String fileName = precinctDataLoc + precinctId + ".tsv";
		return PrecinctCore.loadVoterModel(new File(fileName), precinct);
	}

	public static final IPrecinct getPrecinct(String id, String precinctDefinitions) {
		return PrecinctCore.getPrecinct(id, precinctDefinitions);
	}
	
	public PreviousStatewideVoterModel getOrCreatePreviousVoterModel(String precinctId, String county, VoterModel vm) {
		if( previousStateModel == null && !previousStateModelFailed) {
			// create
			try {
				previousStateModel = new PreviousStatewideVoterModel(precinctId, county, vm);
			} catch(VoterRegistryReaderException vrre ) {
				vrre.printStackTrace();
				previousStateModelFailed = true;
			}
		}
		return previousStateModel;
	}

}
