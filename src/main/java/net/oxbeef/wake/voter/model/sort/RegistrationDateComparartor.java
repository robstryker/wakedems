package net.oxbeef.wake.voter.model.sort;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Voter;

public class RegistrationDateComparartor implements Comparator<Voter> {
	@Override
	public int compare(Voter o1, Voter o2) {
		String reg1 = o1.getRegistrationDate();
		String reg2 = o2.getRegistrationDate();
		return reg2.compareTo(reg1);
	}
}

