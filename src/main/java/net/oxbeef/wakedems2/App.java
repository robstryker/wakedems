package net.oxbeef.wakedems2;

import java.io.File;
import java.io.IOException;

import net.oxbeef.wakedems2.datastore.ExternalDataStore;
import net.oxbeef.wakedems2.datastore.Settings;
import net.oxbeef.wakedems2.mains.CheckMain;
import net.oxbeef.wakedems2.mains.FetchMain;
import net.oxbeef.wakedems2.mains.ReportMain;
/**
 *
 */
public class App {
    public static void main( String[] args ) {
    	if( args.length == 0 ) {
    		System.out.println("No command");
    		return;
    	}
    	if( args[0].equals("fetch")) {
    		fetch();
    		return;
    	}
    	if( args[0].equals("check")) {
    		check();
    		return;
    	}
    	
    	if( args[0].equals("report")) {
    		report();
    		return;
    	}
		System.out.println("No command found: " + args[0]);
    }
    
	private static void report() {
		try {
			new ReportMain().report(createDataStore(), createSettings());
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("Done");
	}

	private static ExternalDataStore createDataStore() {
    	return new ExternalDataStore(new File("/home/rob/.wakedems/data/"));
    }

	private static Settings createSettings() {
		return new Settings();
	}
	
    public static void fetch() {
    	new FetchMain().fetch(createDataStore(), createSettings());
    	System.out.println("Done\n");
    }

    private static void check() {
    	new CheckMain().check(createDataStore(), createSettings());
    	System.out.println("Done\n");
	}

}
