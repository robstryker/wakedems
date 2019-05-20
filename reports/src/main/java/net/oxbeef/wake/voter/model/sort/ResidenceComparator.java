package net.oxbeef.wake.voter.model.sort;
import java.util.Comparator;

import net.oxbeef.wake.voter.model.Residence;
import net.oxbeef.wake.voter.model.Voter;

public class ResidenceComparator implements Comparator<Residence> {
	@Override
	public int compare(Residence o1, Residence o2) {
		String addr1 = o1.getAddr();
		String addr2 = o2.getAddr();
		String addr1Prefix = addr1.substring(0, addr1.indexOf(" ")).trim();
		String addr2Prefix = addr2.substring(0, addr1.indexOf(" ")).trim();
		String addr1Suffix = addr1.substring(addr1Prefix.length()+1).trim();
		String addr2Suffix = addr2.substring(addr2Prefix.length()+1).trim();
		int ret;
		if( addr1Suffix.equals(addr2Suffix)) {
			try {
				int i1 = Integer.parseInt(addr1Prefix);
				int i2 = Integer.parseInt(addr2Prefix);
				return i1 - i2;
			} catch(NumberFormatException nfe ) {
				ret = addr1Prefix.compareTo(addr2Prefix);
			}
		} else
			ret = addr1Suffix.compareTo(addr2Suffix);
		return ret;
	}
}

