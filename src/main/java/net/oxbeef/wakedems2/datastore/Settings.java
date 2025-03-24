package net.oxbeef.wakedems2.datastore;

// TODO mostly settings from sysprop, todo
public class Settings {
	private static final Settings SINGLETON = new Settings();
	public static Settings getDefault() {
		return SINGLETON;
	}
	
	public String getCounty() {
		return "92";
	}
	public String getPrecinct() {
		return "04-04";
	}
	public int getLookbackPeriodDays() {
		return 120;
	}
}
