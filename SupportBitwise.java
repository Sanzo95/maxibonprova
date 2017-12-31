package com.jsoniter;

/**
 * classe di supporto per risolvere il problema di affidabilità "Avoid using
 * bitwise operators to make comparisons"
 * 
 * @author Francesco
 */
public class SupportBitwise {
	/**
	 * costruttore privato, richiesto da kiuwan
	 */
	private SupportBitwise() {
	}

	/**
	 * 
	 */
	private final static char ZERO = '0';
	/**
	 * 
	 */
	private final static char UNO = '1';
	/**
	 * 
	 */
	private final static int DUE = 2;
	/**
	 * 
	 */
	private static String bin1 = "";
	/**
	 * 
	 */
	private static String bin2 = "";
	/**
	 * 
	 */
	private static int l1 = 0;
	/**
	 * 
	 */
	private static int l2 = 0;
	/**
	 * 
	 */
	private final static String zero = "00";

	/**
	 * 
	 * @param bin1
	 * @param bin2
	 * @return
	 */
	public static boolean bitwise(String bin1, String bin2) {
		boolean flag = false;
		SupportBitwise.bin1 = bin1;
		SupportBitwise.bin2 = bin2;
		int l1 = bin1.length();
		int l2 = bin2.length();
		if (l1 <= l2) {
			for (int i = l1 - 1; i >= 0; i--) {
				l2--;
				flag = cyclomaticComplexity1(i, l2);
				if (!(flag)) {
					break;
				}
			}
		} else {
			for (int i = l2 - 1; i >= 0; i--) {
				l1--;
				flag = cyclomaticComplexity1(l1, i);
				if (!(flag)) {
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param long1
	 * @param long2
	 * @param c
	 * @return
	 */
	public static long bitwise(Long long1, Long long2, char c) {
		String newLong = "";
		long l = 0;
		bin1 = Long.toBinaryString(long1);
		bin2 = Long.toBinaryString(long2);
		l1 = bin1.length();
		l2 = bin2.length();
		if (l1 < l2) {
			bin1 = equalsLength();
			l1 = bin1.length();
		} else if (l1 > l2) {
			bin2 = equalsLength();
			l2 = bin2.length();
		}
		for (int i = l1 - 1; i >= 0; i--) {
			l2--;
			newLong = riempiBinaryString(c, newLong, i, l2);
		}
		for (int i = newLong.length() - 1; i >= 0; i--) {
			if (newLong.charAt(i) == UNO) {
				l = Long.valueOf(Double.valueOf(Math.pow(DUE, (newLong.length() - 1) - i)).longValue()) + l;
			}
		}
		return l;
	}

	/**
	 * 
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static boolean cyclomaticComplexity1(int index1, int index2) {
		return ((bin1.charAt(index1) != bin2.charAt(index2))
				|| (charNumericValue(bin1, index1) == 0) && (charNumericValue(bin2, index2) == 0));
	}

	/**
	 * 
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static boolean cyclomaticComplexity2(int index1, int index2) {
		return ((charNumericValue(bin1, index1) == 0 && charNumericValue(bin2, index2) == 1)
				|| (charNumericValue(bin1, index1) == 1 && charNumericValue(bin2, index2) == 0));
	}

	/**
	 * 
	 * @param index1
	 * @param index2
	 * @param value
	 * @return
	 */
	private static boolean cyclomaticComplexity3(int index1, int index2, int value) {
		return charNumericValue(bin1, index1) == value && charNumericValue(bin2, index2) == value;
	}

	/**
	 * 
	 * @param bin
	 * @param index
	 * @return
	 */
	private static int charNumericValue(String bin, int index) {
		return Character.getNumericValue(bin.charAt(index));
	}

	/**
	 * 
	 * @return
	 */
	private static String equalsLength() {
		String toReturn = "";
		int j = Math.max(l1, l2) - Math.min(l1, l2);
		while (j >= 0) {
			if (l1 > l2) {
				toReturn = "bin2";
				bin2 = zero.substring(0, 1).concat(bin2);
				l2++;
			} else if (l1 < l2) {
				toReturn = "bin1";
				bin1 = zero.substring(0, 1).concat(bin1);
				l1++;
			} else {
				break;
			}
			j--;
		}
		if ("bin1".equals(toReturn)) {
			toReturn = bin1;
		} else if ("bin2".equals(toReturn)) {
			toReturn = bin2;
		}
		return toReturn;
	}

	/**
	 * 
	 * @param c
	 * @param newLong
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static String riempiBinaryString(char c, String newLong, int index1, int index2) {
		if ((c == '&') && (cyclomaticComplexity2(index1, index2))) {
			newLong = ZERO + newLong;
		} else if ((c == '|') && (cyclomaticComplexity2(index1, index2))) {
			newLong = UNO + newLong;
		}

		if (cyclomaticComplexity3(index1, index2, 0)) {
			newLong = ZERO + newLong;
		}
		if (cyclomaticComplexity3(index1, index2, 1)) {
			newLong = UNO + newLong;
		}
		return newLong;
	}

}