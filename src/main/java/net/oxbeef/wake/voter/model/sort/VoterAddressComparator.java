package net.oxbeef.wake.voter.model.sort;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Voter;

public class VoterAddressComparator implements Comparator<Voter> {
	@Override
	public int compare(Voter o1, Voter o2) {
		String street1 = o1.getStreet();
		String street2 = o2.getStreet();
		if( street1.equals(street2)) {
			return o1.getStreetNumberInt() - o2.getStreetNumberInt();
		} else {
			return street1.compareTo(street2);
		}
	}
}

