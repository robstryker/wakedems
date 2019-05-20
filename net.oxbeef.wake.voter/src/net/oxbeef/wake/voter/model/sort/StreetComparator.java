package net.oxbeef.wake.voter.model.sort;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Voter;

public class StreetComparator implements Comparator<Voter> {
	@Override
	public int compare(Voter o1, Voter o2) {
		String s1 = o1.getStreetNumber();
		String s2 = o2.getStreetNumber();
		int parseInt1 = Integer.parseInt(s1);
		int parseInt2 = Integer.parseInt(s2);
		if( parseInt1 == parseInt2) {
			return o1.getApartmentNumber().compareTo(o2.getApartmentNumber());
		}
		return parseInt1 - parseInt2;
	}
}

