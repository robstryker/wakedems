package net.oxbeef.wake.voter.model.data.source;

import java.io.File;
import java.io.IOException;

public class ExternalDataSource {
	private static ExternalDataSource instance = new ExternalDataSource();
	public static ExternalDataSource getInstance() {
		return instance;
	}
	
	private String precinctDataLoc;
	private String definitionLoc;
	private String partyChangeFolder;
	private String previousVoterDataLoc;
	
	private ExternalDataSource() {
		try {
			String current = new File(".").getCanonicalPath();
			precinctDataLoc = current + "/../resources/precincts/voters/current/";
			previousVoterDataLoc = (current + "/../resources/precincts/voters/previous/");
			definitionLoc = current + "/../resources/precincts/definitions/";
			partyChangeFolder = (current + "/../resources/partyChange/");
		} catch(IOException ioe) {
			throw new RuntimeException("Cannot find source data");
		}
	}

	public String getPrecinctDataLoc() {
		return precinctDataLoc;
	}

	public String getDefinitionLoc() {
		return definitionLoc;
	}

	public String getPartyChangeFolder() {
		return partyChangeFolder;
	}

	public String getPreviousVoterDataLoc() {
		return previousVoterDataLoc;
	}

}
