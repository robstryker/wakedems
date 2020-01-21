package net.oxbeef.wake.voter.main.old;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;
import net.oxbeef.wake.voter.model.util.VoterUtility;

public class AllVotersMain {
	public static void main(String[] args) {
		System.out.println("Start");
		MainModel model = new MainModel() {
			protected String getPrecinctDataLoc() {
				try {
					String current = new File(".").getCanonicalPath();
					return current + "/../distribution/target/wake.voter.reports.distribution-0.1.0-SNAPSHOT/wakeVoter/resources/precincts/voters/current/";
				} catch(Throwable t) {
					t.printStackTrace();
					return null;
				}
			}
		};
		model.getChangedPartyModel(92);
		VoterModel vm = model.getOrCreateVoterModel("04-19");
		ArrayList<Voter> voters = vm.getAll();
		
		HashMap<String, List<Voter>> byResidence = VoterUtility.votersByResidence(voters);
		Set<String> residences = byResidence.keySet();
		for( String res : residences ) {
			List<Voter> votes = byResidence.get(res);
			System.out.println(res);
			for( Voter v : votes ) {
				System.out.println("   " + voterToString(v));
			}
		}
	}
	
	private static String voterToString(Voter v) {
		StringBuffer sb = new StringBuffer();
		sb.append("    ");
		sb.append(v.getName());
		sb.append(", ");
		sb.append(v.getFullStreetAddress().trim());
		sb.append(", ");
		sb.append(v.getParty());
		sb.append(", ");
		sb.append(v.getAge());
		sb.append("y/o");
		sb.append(", ");
		sb.append(v.getRegistrationDate());
		return sb.toString();
	}

}
