package net.oxbeef.wake.voter.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VoterMain {
	public static final String REPORT_DIR = "report.output.dir";
	
	public static void main(String[] args) {

		
		File absolute = new File(".");
		System.out.println(absolute.getAbsolutePath());
		
		if( args.length == 0 ) {
			System.err.println("Usage: java VoterMain 20-04");
			System.exit(1);
		}
		
		String precinct = args[0];
		
		
	}
}
