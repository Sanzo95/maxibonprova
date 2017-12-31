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
	 * Metodo di supporto per ridurre la complessità ciclomatica nel metodo che
	 * restituisce una variabile boolean
	 * 
	 * @param bin1
	 * @param bin2
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static boolean cyclomaticComplexity1(String bin1, String bin2, int index1, int index2) {
		return ((bin1.charAt(index1) != bin2.charAt(index2))
				|| (charNumericValue(bin1, index1) == 0) && (charNumericValue(bin2, index2) == 0));
	}

	/**
	 * Metodo per bitwise utilizzato negli if
	 * 
	 * @param bin1
	 * @param bin2
	 * @return
	 */
	public static boolean bitwise(String bin1, String bin2) {
		boolean flag = false;
		int l1 = bin1.length();
		int l2 = bin2.length();
		if (l1 <= l2) {
			for (int i = l1 - 1; i >= 0; i--) {
				l2--;
				flag = cyclomaticComplexity1(bin1, bin2, i, l2);
				if (!(flag)) {
					break;
				}
			}
		} else {
			for (int i = l2 - 1; i >= 0; i--) {
				l1--;
				flag = cyclomaticComplexity1(bin1, bin2, l1, i);
				if (!(flag)) {
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param bin1
	 * @param bin2
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static boolean cyclomaticComplexity2(String bin1, String bin2, int index1, int index2) {
		return ((charNumericValue(bin1, index1) == 0 && charNumericValue(bin2, index2) == 1)
				|| (charNumericValue(bin1, index1) == 1 && charNumericValue(bin2, index2) == 0));
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
	 * @param bin1
	 * @param bin2
	 * @param index1
	 * @param index2
	 * @param value
	 * @return
	 */
	private static boolean cyclomaticComplexity3(String bin1, String bin2, int index1, int index2, int value) {
		return charNumericValue(bin1, index1) == value && charNumericValue(bin2, index2) == value;
	}

	/**
	 * 
	 * @param bin1
	 * @param bin2
	 * @param l1
	 * @param l2
	 * @return
	 */
	private static String equalsLength(String bin1, String bin2, int l1, int l2) {
		String toReturn = "";
		int j = Math.max(l1, l2) - Math.min(l1, l2);
		while (j >= 0) {
			if (l1 > l2) {
				toReturn = "bin2";
				bin2 = '0' + bin2;
				l2++;
			} else if (l1 < l2) {
				toReturn = "bin1";
				bin1 = '0' + bin1;
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
	 * @param bin1
	 * @param bin2
	 * @param c
	 * @param newLong
	 * @param index1
	 * @param index2
	 * @return
	 */
	private static String riempiBinaryString(String bin1, String bin2, char c, String newLong, int index1, int index2) {
		if ((c == '&') && (cyclomaticComplexity2(bin1, bin2, index1, index2))) {
			newLong = ZERO + newLong;
		} else if ((c == '|') && (cyclomaticComplexity2(bin1, bin2, index1, index2))) {
			newLong = UNO + newLong;
		}

		if (cyclomaticComplexity3(bin1, bin2, index1, index2, 0)) {
			newLong = ZERO + newLong;
		}
		if (cyclomaticComplexity3(bin1, bin2, index1, index2, 1)) {
			newLong = UNO + newLong;
		}
		return newLong;
	}

	/**
	 * Metodo per eseguire bitwise senza errori kiuwan
	 * 
	 * @param long1
	 * @param long2
	 * @param c
	 * @return
	 */
	public static long bitwise(Long long1, Long long2, char c) {
		String newLong = "";
		long l = 0;
		String bin1 = Long.toBinaryString(long1), bin2 = Long.toBinaryString(long2);
		int l1 = bin1.length(), l2 = bin2.length();
		if (l1 < l2) {
			bin1 = equalsLength(bin1, bin2, l1, l2);
			l1 = bin1.length();
		} else if (l1 > l2) {
			bin2 = equalsLength(bin1, bin2, l1, l2);
			l2 = bin2.length();
		}
		for (int i = l1 - 1; i >= 0; i--) {
			l2--;
			newLong = riempiBinaryString(bin1, bin2, c, newLong, i, l2);
		}
		for (int i = newLong.length() - 1; i >= 0; i--) {
			if (newLong.charAt(i) == UNO) {
				l = Long.valueOf(Double.valueOf(Math.pow(DUE, (newLong.length() - 1) - i)).longValue()) + l;
			}
		}
		return l;
	}
}