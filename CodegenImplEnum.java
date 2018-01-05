package com.jsoniter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jsoniter.spi.ClassInfo;

/**
 * class CodegenImplEnum
 * 
 * @author MaxiBon
 *
 */
class CodegenImplEnum {
	/**
	 * constructor
	 */
	private CodegenImplEnum() {
	}

	/**
	 * sbsize
	 */
	private final static int SBSIZE = 128;

	/**
	 * genEnum.
	 * 
	 * @param classInfo
	 * @return
	 */
	public static String genEnum(ClassInfo classInfo) {
		StringBuilder lines = new StringBuilder(SBSIZE);
		lines.append("if (iter.readNull()) { return null; } \n");
		lines.append("com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readSlice(iter); \n");

		lines.append("switch (field.len()) { \n");
		lines.append(renderTriTree(buildTriTree(Arrays.asList(classInfo.clazz.getEnumConstants()))) + "\n");
		lines.append("} \n"); // end of switch
		lines.append(String.format("throw iter.reportError(\"decode enum\", field + \" is not valid enum for %s\");",
				classInfo.clazz.getName()) + "\n");
		return lines.toString();
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param fromNameBytes
	 * @param current
	 * @return
	 */
	private static Map<Byte, Object> quarto(byte[] fromNameBytes, Map<Byte, Object> current) {
		Map<Byte, Object> temp = current;
		for (int i = 0; i < fromNameBytes.length - 1; i++) {
			byte b = fromNameBytes[i];
			Map<Byte, Object> next = null;

			if (temp.get(b) instanceof Map<?, ?>) {
				next = (Map<Byte, Object>) temp.get(b);
			}

			if (next == null) {
				next = new HashMap<Byte, Object>();
				temp.put(b, next);
			}
			temp = next;
		}
		return temp;
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param allConsts
	 * @return
	 */
	private static Map<Integer, Object> buildTriTree(List<Object> allConsts) {
		Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
		for (Object e : allConsts) {
			byte[] fromNameBytes = e.toString().getBytes();
			Map<Byte, Object> current = null;

			if (trieTree.get(fromNameBytes.length) instanceof Map<?, ?>) {
				current = (Map<Byte, Object>) trieTree.get(fromNameBytes.length);
			}

			if (current == null) {
				current = new HashMap<Byte, Object>();
				trieTree.put(fromNameBytes.length, current);
			}
			current = quarto(fromNameBytes, current);
			current.put(fromNameBytes[fromNameBytes.length - 1], e);
		}
		return trieTree;
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param trieTree
	 * @return
	 */
	private static String renderTriTree(Map<Integer, Object> trieTree) {
		StringBuilder switchBody = new StringBuilder(SBSIZE);
		try {
			for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
				Integer len = entry.getKey();
				switchBody.append("case " + len + ": \n");
				Map<Byte, Object> current = null;

				if (entry.getValue() instanceof Map<?, ?>) {
					current = (Map<Byte, Object>) entry.getValue();
				}

				addFieldDispatch(switchBody, len, 0, current, new ArrayList<Byte>());
				switchBody.append("break; \n");
			}
		} catch (Exception e1) {
			System.out.println("Exception " + e1);
		} finally {
			System.out.print("");
		}
		return switchBody.toString();
	}

	/**
	 * 
	 * @param lines
	 * @param bytesToCompare
	 * @param entry
	 * @param i
	 * @param b
	 * @return
	 */
	private static void primo(StringBuilder lines, List<Byte> bytesToCompare, Map.Entry<Byte, Object> entry, int i,
			Byte b) {
		append(lines, "if (");
		int size = bytesToCompare.size();
		for (int j = 0; j < size; j++) {
			Byte a = bytesToCompare.get(j);
			append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
		}
		append(lines, String.format("field.at(%d)==%s", i, b));
		append(lines, ") {");
		Object e = entry.getValue();
		append(lines, String.format("return %s.%s;", e.getClass().getName(), e.toString()));
		append(lines, "}");
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param entry
	 * @return
	 */
	private static Map<Byte, Object> secondo(Map.Entry<Byte, Object> entry) {
		Map<Byte, Object> next = null;
		if (entry.getValue() instanceof Map<?, ?>) {
			next = (Map<Byte, Object>) entry.getValue();
		}
		return next;
	}

	/**
	 * 
	 * @param nextBytesToCompare
	 * @param bytesToCompare
	 * @param b
	 * @return
	 */
	private static List<Byte> terzo(List<Byte> nextBytesToCompare, List<Byte> bytesToCompare, Byte b) {
		List<Byte> toReturn = nextBytesToCompare;
		toReturn = new ArrayList<Byte>(bytesToCompare);
		toReturn.add(b);
		return toReturn;
	}

	/**
	 * 
	 * @param lines
	 * @param bytesToCompare
	 * @param i
	 * @param b
	 * @param len
	 * @param next
	 */
	private static void multipleAppend(StringBuilder lines, List<Byte> bytesToCompare, int i, Byte b, int len,
			Map<Byte, Object> next) {
		append(lines, "if (");
		int size = bytesToCompare.size();
		for (int j = 0; j < size; j++) {
			Byte a = bytesToCompare.get(j);
			append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
		}
		append(lines, String.format("field.at(%d)==%s", i, b));
		append(lines, ") {");
		addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
		append(lines, "}");
	}

	/**
	 * 
	 * @param lines
	 * @param len
	 * @param i
	 * @param current
	 * @param bytesToCompare
	 */
	private static void addFieldDispatch(StringBuilder lines, int len, int i, Map<Byte, Object> current,
			List<Byte> bytesToCompare) {
		Set<Map.Entry<Byte, Object>> setSize = current.entrySet();
		List<Byte> nextBytesToCompare = null;
		for (Map.Entry<Byte, Object> entry : setSize) {
			Byte b = entry.getKey();
			if (i == len - 1) {
				primo(lines, bytesToCompare, entry, i, b);
				continue;
			}
			Map<Byte, Object> next = secondo(entry);
			if (next.size() == 1) {
				nextBytesToCompare = terzo(nextBytesToCompare, bytesToCompare, b);
				addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
				continue;
			}
			multipleAppend(lines, bytesToCompare, i, b, len, next);
		}
	}

	/**
	 * 
	 * @param lines
	 * @param str
	 */
	private static void append(StringBuilder lines, String str) {
		lines.append(str);
		lines.append("\n");
	}
}
