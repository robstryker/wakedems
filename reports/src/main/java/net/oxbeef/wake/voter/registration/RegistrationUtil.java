package net.oxbeef.wake.voter.registration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.oxbeef.wake.voter.model.Voter;
import net.oxbeef.wake.voter.registration.RegistrationMain.FirstMiddleLast;

public class RegistrationUtil {

	private static final SimpleDateFormat FORMAT_MDY = new SimpleDateFormat("MM/dd/yyyy");
	private static final SimpleDateFormat FORMAT_YMD = new SimpleDateFormat("yyyy/MM/dd");
	
	/*
	 * Is mmddyyyy  after yyyymmdd ?
	 */
	public static boolean isAfter(String yyyymmdd, String mmddyyyy) {
		try {
			Date d1 = FORMAT_YMD.parse(yyyymmdd);
			Date d2 = FORMAT_MDY.parse(mmddyyyy);
			return d2.after(d1);
		} catch(ParseException pe) {
			return true;		
		}		
	}
	public static boolean isInitialsOnly(String s) {
		if( s.trim().contains(" ") ) {
			String[] split = s.trim().split(" ");
			for( int i = 0; i < split.length; i++ ) {
				if( split[i].trim().length() != 1)
					return false;
			}
			return true;
		}
		return s.trim().length() == 1;
	}

	public static boolean hasAnyInitial(String s) {
		if( s.trim().contains(" ") ) {
			String[] split = s.trim().split(" ");
			for( int i = 0; i < split.length; i++ ) {
				if( split[i].trim().length() == 1)
					return true;
			}
			return false;
		}
		return s.trim().length() == 1;
	}

	public static String getStreetForAddr1(String addr1) {
		int begin = getStreetNumber(addr1).length();
		String tmp = addr1.substring(begin);

		int end = tmp.indexOf("  #");
		if( end != -1 ) {
			tmp = tmp.substring(0, end);
		}
		tmp = tmp.trim();
		return tmp.replaceAll("\"", "");
	}
	public static  String getStreetNumber(String addr1) {
		int ind = addr1.indexOf(' ');
		return ind == -1 ? "" : addr1.substring(0, ind).trim();
	}

	
	public static String safeIndex(String[] arr, int ind) {
		if( arr.length > ind && arr[ind] != null )
			return arr[ind];
		return "";
	}

	public static boolean endsWithPossibleGenerational(String lastCommaRest) {
		if( lastCommaRest.endsWith(" JR") || lastCommaRest.endsWith(" SR") || 
				lastCommaRest.endsWith(" III") || lastCommaRest.endsWith(" IV") || 
				lastCommaRest.endsWith(" V") || lastCommaRest.endsWith(" VI") || 
				lastCommaRest.endsWith(" VII") || lastCommaRest.endsWith(" VIII") || 
				lastCommaRest.endsWith(" IX") || lastCommaRest.endsWith(" X") || 
				lastCommaRest.endsWith(" XI") || lastCommaRest.endsWith(" XII"))
			return true;
		return false;
	}
	
	public static ArrayList<FirstMiddleLast> getNamePermutations(String lastCommaRest, boolean ignoreMiddle){
		String last = lastCommaRest.substring(0, lastCommaRest.indexOf(",")).trim();
		String rest = lastCommaRest.substring(lastCommaRest.indexOf(",")+1).trim();
		String firstMiddleLast = rest.trim() + " " + last;
		String[] asArr = firstMiddleLast.split(" ");
		ArrayList<FirstMiddleLast> ret = new ArrayList<>();
		ret.addAll(getNamePermutationsForArray(asArr, ignoreMiddle));
		
		if( endsWithPossibleGenerational(lastCommaRest)) {
			String generational = lastCommaRest.substring(lastCommaRest.lastIndexOf(" ")).trim();
			String lastCommaRest2 = lastCommaRest.substring(0, lastCommaRest.length() - generational.length()).trim();
			String last2 = lastCommaRest2.substring(0, lastCommaRest2.indexOf(",")).trim();
			String rest2 = lastCommaRest2.substring(lastCommaRest2.indexOf(",")+1).trim();
			String firstMiddleLast2 = rest2.trim() + " " + last2;
			String[] asArr2 = firstMiddleLast2.split(" ");
			ret.addAll(getNamePermutationsForArrayWithGenerationals(asArr2, generational, ignoreMiddle));
		}
		
		return ret;
	}
	public static ArrayList<FirstMiddleLast> getNamePermutationsForArray(String[] asArr, boolean ignoreMiddle) {
		if( asArr.length == 0 || asArr.length == 1 )
			// Nobody has only one name!
			return new ArrayList<>();
		
		if( asArr.length == 2 )	{
			ArrayList<FirstMiddleLast> ret = new ArrayList<>();
			ret.add(new FirstMiddleLast(asArr[0], null, asArr[1]));
			return ret;
		}
		
		ArrayList<FirstMiddleLast> ret = new ArrayList<>();
		for( int i = 1; i < asArr.length; i++ ) {
			// first name is 0 through i
			String firstName = join(asArr, 0, i);
			for( int j = i; j <= asArr.length-1; j++ ) {
				String middleName = join(asArr, i, j);
				String lastName = join(asArr, j, asArr.length);
				if( ignoreMiddle ) {
					ret.add(new FirstMiddleLast(firstName, "", lastName));
				} else {
					ret.add(new FirstMiddleLast(firstName, middleName, lastName));
				}
			}
		}
		
		return ret;
	}

	public static ArrayList<FirstMiddleLast> getNamePermutationsForArrayWithGenerationals(
			String[] asArr, String generational, boolean emptyMiddle) {
		if( asArr.length == 0 || asArr.length == 1 )
			// Nobody has only one name!
			return new ArrayList<>();
		
		if( asArr.length == 2 )	{
			ArrayList<FirstMiddleLast> ret = new ArrayList<>();
			ret.add(new FirstMiddleLast(asArr[0], null, asArr[1]));
			return ret;
		}
		
		ArrayList<FirstMiddleLast> ret = new ArrayList<>();
		for( int i = 1; i < asArr.length-1; i++ ) {
			// first name is 0 through i
			String firstName = join(asArr, 0, i);
			for( int j = i; j <= asArr.length-1; j++ ) {
				String middleName = join(asArr, i, j);
				String lastName = join(asArr, j, asArr.length);
				if( emptyMiddle ) {
					ret.add(new FirstMiddleLast(firstName, "", lastName, generational));
				} else {
					ret.add(new FirstMiddleLast(firstName, middleName, lastName, generational));
				}
			}
		}
		
		return ret;
	}

	public static String join(String[] arr, int start, int end) {
		StringBuffer sb = new StringBuffer();
		for( int i = start; i < end; i++ ) {
			sb.append(arr[i]);
			sb.append(" ");
		}
		String ret = sb.toString().trim();
		if( ret.isEmpty()) 
			return null;
		return ret;
	}

	public static boolean leansDem(Voter v) {
		return leansDem(v.getParty());
	}

	public static boolean leansRep(Voter v) {
		return leansRep(v.getParty());
	}

	public static boolean isUnaffiliated(Voter v) {
		return isUnaffiliated(v.getParty());
	}

	public static boolean leansDem(String s) {
		return s.equalsIgnoreCase("dem") || s.equalsIgnoreCase("grn");
	}

	public static boolean leansRep(String s) {
		return s.equalsIgnoreCase("rep") ||s.equalsIgnoreCase("lib") || s.equalsIgnoreCase("cst");
	}

	public static boolean isUnaffiliated(String s) {
		return s.equalsIgnoreCase("una");
	}

}
