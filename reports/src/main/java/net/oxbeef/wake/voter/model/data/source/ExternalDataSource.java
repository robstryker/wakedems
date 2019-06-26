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
	
	private ExternalDataSource() {
		try {
			String current = new File(".").getCanonicalPath();
			precinctDataLoc = current + "/../resources/precincts/voters/current/";
			definitionLoc = current + "/../resources/precincts/definitions/";
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
}
