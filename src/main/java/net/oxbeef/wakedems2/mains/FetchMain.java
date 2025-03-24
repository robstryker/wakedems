package net.oxbeef.wakedems2.mains;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.oxbeef.wakedems2.datastore.ExternalDataStore;
import net.oxbeef.wakedems2.datastore.Settings;
import net.oxbeef.wakedems2.util.FileUtils;

public class FetchMain {

	private ExternalDataStore ds;
	private Settings settings;

	public void fetch(ExternalDataStore ds, Settings settings) {
		this.ds = ds;
		this.settings = settings;
		fetchPartyChange();
		fetchPreviousNcboe();
		fetchNcboe();
		fetchVoteHistory();
	}

	private void fetchAndUnzipCountyFile(String fName, File destFolder) {
		String county = settings.getCounty();
		String url = "https://s3.amazonaws.com/dl.ncsbe.gov/data/" + fName;
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		File dest = new File(destFolder, fName);
		try {
			FileUtils.downloadFile(url, dest.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		File countyFolder = new File(destFolder, "" + county);
		FileUtils.unzip(dest.getAbsolutePath(), countyFolder.getAbsolutePath());
	}

	private void fetchVoteHistory() {
		// https://s3.amazonaws.com/dl.ncsbe.gov/data/ncvhis92.zip
		String county = settings.getCounty();
		String fName = "ncvhis" + county + ".zip";
		File voteHistoryFolder = ds.getVoteHistoryFolder();
		File countyFolder = new File(voteHistoryFolder, "" + county);
		fetchAndUnzipCountyFile(fName, voteHistoryFolder);
		File unzipped = new File(countyFolder, "ncvhis" + county + ".txt");
		//String contents = readFile(unzipped, StandardCharsets.UTF_16);
		
		List<String> uniquePrecincts = findUniquePrecinctsFromNcboeFolder();
		uniquePrecincts.sort(null);
		for( int i = 0; i < uniquePrecincts.size(); i++ ) {
			String p1 = uniquePrecincts.get(i);
			System.out.println("Separating voter history for precinct " + p1);
			ArrayList<String> linesForCurrentPrecinct = new ArrayList<>();
			final int[] lineCounter = new int[] { 0 };
			try (Stream<String> lines = Files.lines(unzipped.toPath())) {
	            lines.forEach(line -> {
	                // Process each line here
	            	if( line.contains(p1)) {
		            	String precinct = getPrecinctFromColumn(line.split("\t"), NCBOE_VHIS_PRECINCT_INDEX);
		            	if( p1.equals(precinct)) {
		            		linesForCurrentPrecinct.add(line);
		            	}
	            	}
	            	lineCounter[0]++;
	            	if( lineCounter[0] % 100000 == 0 ) {
	            		System.out.print(".");
	            	}
	            });
	        } catch (IOException e) {
	            System.err.println("Error reading file: " + e.getMessage());
	        }
			System.out.println();
			String newContents = String.join("\n", linesForCurrentPrecinct);
			File precinctFile = new File(countyFolder, p1 + ".tsv");
			try {
				Files.writeString(precinctFile.toPath(), newContents);
			} catch (IOException e) {
				System.err.println("Error writing to file: " + e.getMessage());
			}

		}
		
		int z = 4;
		
		//splitTsvFileByColumn(contents, NCBOE_VHIS_PRECINCT_INDEX, countyFolder);
	}

	private List<String> findUniquePrecinctsFromNcboeFolder() {
		File f = ds.getCurrentNcboeFolder();
		String county = settings.getCounty();
		return Arrays.stream(new File(f, county).listFiles())
		.map(x -> x.getName())
		.filter(x -> x.endsWith(".tsv"))
		.map(x -> x.replaceAll(".tsv",  ""))
		.collect(Collectors.toList());
	}

//	./fetch_ncboe.sh
	private void fetchNcboe() {
		String county = settings.getCounty();
		String fName = "ncvoter" + county + ".zip";
		File f = ds.getCurrentNcboeFolder();
		fetchAndUnzipCountyFile(fName, f);

		String txtName = "ncvoter" + county + ".txt";
		File countyFolder = new File(f, "" + county);
		File fileToRead = new File(countyFolder, txtName);
		String contents = readFile(fileToRead);

		splitTsvFileByColumn(contents, NCBOE_CURRENT_PRECINCT_INDEX, countyFolder);
	}
	
	private void splitTsvFileByColumn(String contents, int column, File countyFolder) {
		String[] split = contents.split("\n");
		Map<String, ArrayList<String>> separatedPrecinctFiles = new HashMap<>();
		for (int i = 0; i < split.length; i++) {
			String line = split[i];
			String precinct = getPrecinctFromColumn(line.split("\t"), column);
			if (precinct != null) {
				ArrayList<String> precinctFileContents = separatedPrecinctFiles.get(precinct);
				if (precinctFileContents == null) {
					precinctFileContents = new ArrayList<>();
					separatedPrecinctFiles.put(precinct, precinctFileContents);
				}
				precinctFileContents.add(line);
			}
		}
		List<String> precincts = separatedPrecinctFiles.keySet().stream().sorted().collect(Collectors.toList());
		for (String p : precincts) {
			File tsvFile = new File(countyFolder, p + ".tsv");
			ArrayList<String> toWrite = separatedPrecinctFiles.get(p);
			String tsvOut = String.join("\n", toWrite);
			System.out.println("Writing " + toWrite.size() + " voters to " + tsvFile.getName());
			try {
				Files.writeString(tsvFile.toPath(), tsvOut);
			} catch (IOException e) {
				System.err.println("Error writing to file: " + e.getMessage());
			}
		}
	}

	private String readFile(File txtFile) {
		return readFile(txtFile, null);
	}
	
	private String readFile(File txtFile, Charset charset) {
		// Read the file
		String contents = null;
		charset = (charset == null ? Charset.defaultCharset(): charset);
		try {
			contents = new String(Files.readAllBytes(txtFile.toPath()), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return contents;
	}
	
	private static final int NCBOE_VHIS_PRECINCT_INDEX = 8;
	private static int NCBOE_CURRENT_PRECINCT_INDEX = 34;

	private List<String> findUniquePrecinctsFromVoterFile(String[] split) {
		List<String> uniquePrecincts = Arrays.stream(split).map(x -> {
			String[] tmp = x.split("\t");
			return getPrecinctFromColumn(tmp, NCBOE_CURRENT_PRECINCT_INDEX);
		}).filter(x -> x != null).distinct().collect(Collectors.toList());
		uniquePrecincts.sort(null);
		return uniquePrecincts;
	}

	private String getPrecinctFromColumn(String[] fields, int field) {
		String goal = fields == null || fields.length < (field + 1) ? null : fields[field];
		String stripped = goal == null ? null : goal.replaceAll("\"", "");
		String last = "".equals(stripped) ? null : stripped;
		if (last != null && last.length() > 2 && Character.isDigit(last.charAt(0))
				&& Character.isDigit(last.charAt(1))) {
			// correct format
			return last;
		}
		return null;
	}

//	./fetch_previous_ncboe.sh
	private void fetchPreviousNcboe() {
		updatePreviousNcboeHistoricalVersions();

	}

	private void updatePreviousNcboeHistoricalVersions() {
		File f = ds.getPreviousNcboeFolder();
		if (!f.exists()) {
			f.mkdirs();
		}
		File allPreviousRevisions = new File(f, "historical_filenames");
		if (!allPreviousRevisions.exists()) {
			// Download it
			String url = "https://raw.githubusercontent.com/robstryker/wakedems/refs/heads/master/resources/precincts/voters/historical_filenames";
			try {
				FileUtils.downloadFile(url, allPreviousRevisions.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		// Now read the file. We may need to update it
		String contents = null;
		try {
			contents = new String(Files.readAllBytes(allPreviousRevisions.toPath()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String[] split = contents.split("\n");
		List<String> newContents = Arrays.stream(split).filter(x -> !"vr_snapshot_file_layout.txt".equals(x))
				.collect(Collectors.toList());
		String lastLine = newContents.get(newContents.size() - 1);
		// Should be of format VR_Snapshot_20220726.zip
		String prefix = "VR_Snapshot_";
		String suffix = ".zip";
		String lastDate = lastLine.substring(prefix.length(), prefix.length() + 8);
		System.out.println("Last vr snapshot was " + lastDate);
		String[] allDaysSinceLastUpdate = daysSince(lastDate);
		System.out.println(allDaysSinceLastUpdate.length + " days to check");
		for (int i = 0; i < allDaysSinceLastUpdate.length; i++) {
			String fnameForDate = "VR_Snapshot_" + allDaysSinceLastUpdate[i] + ".zip";
			String url = "https://s3.amazonaws.com/dl.ncsbe.gov/data/Snapshots/" + fnameForDate;
			boolean add = headRequestUrl(url);
			if (add) {
				newContents.add(fnameForDate);
			}
		}
		try {
			Files.writeString(allPreviousRevisions.toPath(), String.join("\n", newContents));
		} catch (IOException e) {
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	private String[] daysSince(String yyyyMMdd) {
		ArrayList<String> ret = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate today = LocalDate.now();
		LocalDate ld = LocalDate.parse(yyyyMMdd, formatter);
		LocalDate working = ld;
		boolean done = false;
		while (!done) {
			working = working.plusDays(1);
			String asPattern = working.format(formatter);
			ret.add(asPattern);
			done = !today.isAfter(working);
		}

		return (String[]) ret.toArray(new String[ret.size()]);
	}

	private boolean headRequestUrl(String urlString) {

		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("HEAD");

			int responseCode = connection.getResponseCode();
			if (responseCode == 200)
				System.out.println(urlString + " Response Code: " + responseCode);
			connection.disconnect();
			return responseCode == 200;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void fetchPartyChange() {
		File f = ds.getPartyChangeFolder();
		if (!f.exists()) {
			f.mkdirs();
		}
		String baseUrl = "https://s3.amazonaws.com/dl.ncsbe.gov/data/PartyChange/";
		int year = Year.now().getValue();
		for (int i = 2018; i <= year; i++) {
			String fileName = i + "_party_change_list.csv";
			String url = baseUrl + fileName;
			File dest = new File(f, fileName);
			if (!dest.exists()) {
				System.out.println("Downloading " + fileName);
				try {
					FileUtils.downloadFile(url, dest.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				System.out.println(fileName + " already exists");
			}
		}
		// TODO Auto-generated method stub

	}

}
