package net.oxbeef.wake.voter.model.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.oxbeef.wake.voter.model.Voter;

public class StatisticsUtil {

	public static double getAverageAge(List<Voter> voters) {
		if( voters.size() == 0 )
			return -1;
		
		double total = 0;
		int count = 0;
		for( Voter v : voters ) {
			try {
				total += v.getAgeInt();
				count++;
			} catch(NumberFormatException nfe) {
				nfe.printStackTrace();
			}
		}
		return total / count;
	}

	public static double getMedianAge(List<Voter> voters) {
		if( voters.size() == 0 )
			return -1;
		if( voters.size() < 2 ) 
			return voters.get(0).getAgeInt();
		ArrayList<Voter> tmp = new ArrayList<Voter>(voters);
		Collections.sort(tmp, new Comparator<Voter>() {
			@Override
			public int compare(Voter o1, Voter o2) {
				return o1.getAgeInt() - o2.getAgeInt();
			}
		});
		int midIndex = voters.size() / 2;
		return tmp.get(midIndex).getAgeInt();
	}

	public static double getAgeStdev(List<Voter> voters) {
		double total = 0;
		for( Voter v : voters ) {
			total += v.getAgeInt();
		}
		double mean = total / voters.size();
		double total2 = 0;
		for( Voter v : voters ) {
			total2 += (v.getAgeInt() - mean) * (v.getAgeInt() - mean);
		}
		double mean2 = total2 / voters.size();
		return Math.sqrt(mean2);
	}
}
