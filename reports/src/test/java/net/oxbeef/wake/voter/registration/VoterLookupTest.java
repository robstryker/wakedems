package net.oxbeef.wake.voter.registration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.oxbeef.wake.voter.main.MainModel;
import net.oxbeef.wake.voter.model.IVoterColumnsNCBOE;
import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.model.VoterModel;

public class VoterLookupTest {

	@Test
	public void simpleNameMatching() {
		runSimpleTest("Dwight", "D", "Eisenhower", "Eisenhower, Dwight D");
		runSimpleTest("Dwight", "D", "Eisenhower", "Eisenhower, Dwight David");
		runSimpleTest("Dwight", "D", "Eisenhower", "Eisenhower, Dwight Donald");
		runSimpleTest("Dwight", "D", "Eisenhower", "Eisenhower, Dwight");
		
		runSimpleTest("Dwight", "David", "Eisenhower", "Eisenhower, Dwight D");
		runSimpleTest("Dwight", "David", "Eisenhower", "Eisenhower, Dwight David");
		try {
			runSimpleTest("Dwight", "David", "Eisenhower", "Eisenhower, Dwight Donald");
			fail();
		} catch(AssertionError ae ) {
			// expected
		}
		runSimpleTest("Dwight", "David", "Eisenhower", "Eisenhower, Dwight");
		
		runSimpleTest("Dwight", "Donald", "Eisenhower", "Eisenhower, Dwight D");
		try {
			runSimpleTest("Dwight", "Donald", "Eisenhower", "Eisenhower, Dwight David");
			fail();
		} catch(AssertionError ae) {
			// expected
		}
		runSimpleTest("Dwight", "Donald", "Eisenhower", "Eisenhower, Dwight Donald");
		runSimpleTest("Dwight", "Donald", "Eisenhower", "Eisenhower, Dwight");
		
		runSimpleTest("Dwight", "", "Eisenhower", "Eisenhower, Dwight D");
		runSimpleTest("Dwight", "", "Eisenhower", "Eisenhower, Dwight David");
		runSimpleTest("Dwight", "", "Eisenhower", "Eisenhower, Dwight Donald");
		runSimpleTest("Dwight", "", "Eisenhower", "Eisenhower, Dwight");
		
		runSimpleTest("Sian", "Sarah Rucker", "Blake", "Blake, Sian Sarah R");
	}
	
	private void runSimpleTest(String voterRegFirst, String voterRegMiddle, 
			String voterRegLast, String query) {
		ArrayList<Voter> all = new ArrayList<Voter>();
		Voter v1 = createVoter(voterRegFirst, voterRegMiddle, voterRegLast, "123 any st");
		all.add(v1);
		
		MainModel mm = createMainModel("01-01", all);
		ModelCacher mc = new ModelCacher(mm);
		RegistrationMain rm = new RegistrationMain(mc, null);
		Voter[] arr = rm.findVoters(all, query);
		assertNotNull(arr);
		assertTrue(arr.length == 1);
	}

	private MainModel createMainModel(final String precinct, final ArrayList<Voter> voters) {
		MainModel mm = new MainModel() {
			public String[] listAllPrecincts() {
				return new String[] {precinct};
			}
			public VoterModel getOrCreateVoterModel(String precinctId) {
				Set<String> streetSet = fromVoters(voters);
				return new VoterModel(voters, new ArrayList<Voter>(), new ArrayList<Voter>(), 
						new ArrayList<Voter>(voters), streetSet);
			}
			private Set<String> fromVoters(ArrayList<Voter> all) {
				HashSet<String> ret = new HashSet<String>();
				for( Voter v : all ) {
					ret.add(v.getStreet());
				}
				return ret;
			}
		};
		return mm;
	}

	public static Voter createVoter(String first, String middle, String last, String addr1) {
		String[] arr = new String[71];
		for( int i = 0; i < arr.length; i++ )
			arr[i] = "";
		arr[IVoterColumnsNCBOE.first_name-1] = first;
		arr[IVoterColumnsNCBOE.middle_name-1] = middle;
		arr[IVoterColumnsNCBOE.last_name-1] = last;
		arr[IVoterColumnsNCBOE.res_street_address] = addr1;
		arr[IVoterColumnsNCBOE.party_cd] = "UNA";
		
		return new Voter(String.join("\t", arr));
	}
}
