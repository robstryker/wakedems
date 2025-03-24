package net.oxbeef.wakedems2.datastore;

import java.io.File;

public class ExternalDataStore {
	public static ExternalDataStore SINGLETON;
	public static final ExternalDataStore getSingleton() {
		return SINGLETON;
	}
	
	private File f;

	public ExternalDataStore(File f) {
		this.f = f;
		f.mkdirs();
		SINGLETON = this;
		System.out.println("Dirs made");
	}
	
	public File getPartyChangeFolder() {
		return new File(new File(f, "source"), "partyChange");
	}

	public File getCurrentNcboeFolder() {
		return new File(new File(f, "source"), "ncboe");
	}
	
	public File getPreviousNcboeFolder() {
		return new File(new File(f, "source"), "ncboe_prev");
	}
	
	public File getVoteHistoryFolder() {
		return new File(new File(f, "source"), "votehistory");
	}
}
