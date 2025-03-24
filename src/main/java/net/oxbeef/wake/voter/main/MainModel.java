package net.oxbeef.wake.voter.main;

import java.io.File;
import java.util.HashMap;

import net.oxbeef.wake.voter.model.ChangedPartyModel;
import net.oxbeef.wake.voter.model.PreviousStatewideVoterModel;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.VoterRegistryReaderException;
import net.oxbeef.wake.voter.model.precinct.IPrecinct;
import net.oxbeef.wake.voter.model.precinct.PrecinctCore;
import net.oxbeef.wakedems2.datastore.ExternalDataStore;

public class MainModel {
	private HashMap<String, VoterModel> modelMap;
	private HashMap<String, ChangedPartyModel> changedPartyModel;
	private PreviousStatewideVoterModel previousStateModel;
	private boolean previousStateModelFailed;
	
	public MainModel() {
		modelMap = new HashMap<>();
		changedPartyModel = new HashMap<>();
	}
	
	public ChangedPartyModel getChangedPartyModel(String county, int days) {
		ChangedPartyModel g = changedPartyModel.get(county);
		if( g == null ) {
			ChangedPartyModel cpm = new ChangedPartyModel(county, days);
			changedPartyModel.put(county, cpm);
			return cpm;
		}
		return g;
	}
	
	public void clearChangedPartyModel(String county) {
		changedPartyModel.remove(county);
	}
	
	public VoterModel getOrCreateVoterModel(String countyId, String precinctId) {
		if( modelMap.containsKey(precinctId)) {
			return modelMap.get(precinctId);
		}

		IPrecinct precinct = getPrecinct(countyId, precinctId);
		VoterModel vm = null;
		if (precinct != null) {
			vm = loadVoterModel(countyId, precinctId, precinct);
		}
		if( vm != null ) 
			modelMap.put(precinctId,  vm);
		return vm;
	}

//	
//	public String[] listAllPrecincts() {
//		ArrayList<String> ret = new ArrayList<>();
//		String root = getPrecinctDataLoc();
//		File f = new File(root);
//		File[] children = f.listFiles();
//		for( int i = 0; i < children.length; i++ ) {
//			String name = children[i].getName();
//			if( name.endsWith(".tsv")) {
//				ret.add(name.substring(0, name.length()-4));
//			}
//		}
//		return (String[]) ret.toArray(new String[ret.size()]);
//	}
	
	private VoterModel loadVoterModel(String countyId, String precinctId, IPrecinct precinct) {
		// The name of the file to open.
		File ncboe = ExternalDataStore.getSingleton().getCurrentNcboeFolder();
		File county = new File(ncboe, countyId);
		File precinctFile = new File(county, precinctId + ".tsv");
		return PrecinctCore.loadVoterModel(precinctFile, precinct);
	}

	public static final IPrecinct getPrecinct(String countyId, String precinctId) {
		return PrecinctCore.getPrecinct(countyId, precinctId);
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
