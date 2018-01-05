package com.jsoniter;

import static com.jsoniter.CodegenImplObjectHash.appendVarDef;
import static com.jsoniter.CodegenImplObjectHash.appendWrappers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jsoniter.spi.Binding;
import com.jsoniter.spi.ClassDescriptor;
import com.jsoniter.spi.ConstructorDescriptor;
import com.jsoniter.spi.GenericsHelper;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.WrapperDescriptor;

/**
 * class CodegenImplObjectStrict
 * 
 * @author MaxiBon
 *
 */
class CodegenImplObjectStrict {
	/**
	 * CONSTRUCTOR
	 */
	private CodegenImplObjectStrict() {
	}

	/**
	 * }
	 */
	static final String PARENTESICHIUSA = "}";
	/**
	 * {
	 */
	static final String PARENTESIAPERTA = "{";
	/**
	 * 0
	 */
	static final String ZERO = "0";
	/**
	 * 128
	 */
	private final static int SBSIZE = 128;
	/**
	 * "throw new com.jsoniter.spi.JsonException('extra property: %s');"
	 */
	static final String QUARTO = "throw new com.jsoniter.spi.JsonException('extra property: %s');";
	/**
	 * %scom.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);obj.%s=%s%s 
	 */
	static final String QUINTO= "%scom.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);obj.%s=%s%s";
	/**
	 * static Map<String, String> DEFAULT_VALUES
	 */
	static final Map<String, String> DEFAULT_VALUES = new HashMap<String, String>() {
		{
			put("float", "0.0f");
			put("double", "0.0d");
			put("boolean", "false");
			String byt = "byte";
			String num = ZERO;
			put(byt, num);
			put("short", ZERO);
			put("int", ZERO);
			put("char", ZERO);
			put("long", ZERO);
		}
	};

	/**
	 * 
	 * @param desc
	 * @param rendered
	 * @return
	 */
	private static String primo(ClassDescriptor desc, String rendered) {
		String toReturn = rendered;
		if (desc.ctor.parameters.isEmpty()) {
			// if not field or setter, the value will set to temp variable
			for (Binding field : desc.fields) {
				toReturn = updateBindingSetOp(rendered, field);
			}
			for (Binding setter : desc.setters) {
				toReturn = updateBindingSetOp(rendered, setter);
			}
		}
		return toReturn;
	}

	/**
	 * 
	 * @param desc
	 * @param lines
	 * @return
	 */
	private static StringBuilder secondo(ClassDescriptor desc, StringBuilder lines) {
		StringBuilder toReturn = lines;
		String temp = "";
		if (!desc.ctor.parameters.isEmpty()) {
			temp = "%s obj = {{newInst}};";
			append(toReturn, String.format(temp, CodegenImplNative.getTypeName(desc.clazz)));
			for (Binding field : desc.fields) {
				temp = "obj.%s = _%s_;";
				append(toReturn, String.format(temp, field.field.getName(), field.name));
			}
			for (Binding setter : desc.setters) {
				temp = "obj.%s(_%s_);";
				append(toReturn, String.format(temp, setter.method.getName(), setter.name));
			}
		}
		System.out.print(temp);
		return toReturn;
	}

	/**
	 * 24 LINES OF CODE
	 * @param allBindings
	 * @param desc
	 * @param lines
	 * @param rendered
	 * @param hasRequiredBinding
	 * @param expectedTracker
	 * @return
	 */
	private static StringBuilder multipleAppend(List<Binding> allBindings, ClassDescriptor desc, StringBuilder lines,
			String rendered, boolean hasRequiredBinding, long expectedTracker) {
		StringBuilder toReturn = lines;
		append(toReturn, "once = false;");
		if (hasAnythingToBindFrom(allBindings)) {
			append(toReturn, "switch (field.len()) {");
			append(toReturn, rendered);
			append(toReturn, PARENTESICHIUSA);
		}
		appendOnUnknownField(toReturn, desc);
		append(toReturn, PARENTESICHIUSA);
		append(toReturn, "while (com.jsoniter.CodegenAccess.nextToken(iter) == ',') {");
		append(toReturn, "field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
		if (hasAnythingToBindFrom(allBindings)) {
			append(toReturn, "switch (field.len()) {");
			append(toReturn, rendered);
			append(toReturn, PARENTESICHIUSA); // end of switch
		}
		appendOnUnknownField(toReturn, desc);
		append(toReturn, PARENTESICHIUSA); // end of while
		if (hasRequiredBinding) {
			append(toReturn, "if (tracker != " + expectedTracker + "L) {");
			appendMissingRequiredProperties(toReturn, desc);
			append(toReturn, PARENTESICHIUSA);
		}
		return toReturn;
	}

	/**
	 * 19 LOC
	 * @param desc
	 * @param lines
	 * @param b
	 * @return
	 */
	private static StringBuilder multipleAppend2(ClassDescriptor desc, StringBuilder lines, boolean b) {
		StringBuilder toReturn = lines;
		for (Binding parameter : desc.ctor.parameters) {
			appendVarDef(toReturn, parameter);
		}
		append(toReturn, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
		if (b) {
			appendMissingRequiredProperties(toReturn, desc);
		} else {
			append(toReturn, "return {{newInst}};");
		}
		append(toReturn, PARENTESICHIUSA);
		for (Binding field : desc.fields) {
			appendVarDef(toReturn, field);
		}
		for (Binding setter : desc.setters) {
			appendVarDef(toReturn, setter);
		}
		return toReturn;
	}

	/**
	 * 23 LOC
	 * @param bin
	 * @param cD
	 * @param s
	 * @param b
	 * @param l
	 * @param m
	 * @return
	 */
	private static StringBuilder terzo(List<Binding> bin, ClassDescriptor cD, StringBuilder s, boolean b, long l, Map<Integer, Object> m) {
		StringBuilder toReturn = s;
		for (WrapperDescriptor wrapper : cD.bindingTypeWrappers) {
			for (Binding param : wrapper.parameters) {
				appendVarDef(toReturn, param);
			}
		}
		if (cD.onExtraProperties != null || !cD.keyValueTypeWrappers.isEmpty()) {
			append(toReturn, "java.util.Map extra = null;");
		}
		append(toReturn, "com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
		append(toReturn, "boolean once = true;");
		append(toReturn, "while (once) {");
		toReturn = multipleAppend(bin, cD, toReturn, primo(cD, renderTriTree(m)), b, l);
		if (cD.onExtraProperties != null) {
			appendSetExtraProperteis(toReturn, cD);
		}
		if (!cD.keyValueTypeWrappers.isEmpty()) {
			appendSetExtraToKeyValueTypeWrappers(toReturn, cD);
		}
		toReturn = secondo(cD, toReturn);
		appendWrappers(cD.bindingTypeWrappers, toReturn);
		append(toReturn, "return obj;");
		return toReturn;
	}

	/**
	 * genObjectUsingStrict
	 * 25 LOC
	 * @param desc
	 * @return
	 */
	public static String genObjectUsingStrict(ClassDescriptor desc) {
		List<Binding> allBindings = desc.allDecoderBindings();
		int lastRequiredIdx = assignMaskForRequiredProperties(allBindings);
		boolean hasRequiredBinding = lastRequiredIdx > 0;
		long expectedTracker = Long.MAX_VALUE >> (63 - lastRequiredIdx);
		Map<Integer, Object> trieTree = buildTriTree(allBindings);
		StringBuilder lines = new StringBuilder(SBSIZE);
		lines.append("");
		append(lines, "java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);");
		append(lines, "if (iter.readNull()) { return null; }");
		if (hasRequiredBinding) {
			append(lines, "long tracker = 0;");
		}
		if (desc.ctor.parameters.isEmpty()) {
			append(lines, "{{clazz}} obj = {{newInst}};");
			append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
			if (hasRequiredBinding) {
				appendMissingRequiredProperties(lines, desc);
			}
			append(lines, "return obj;");
			append(lines, PARENTESIAPERTA);
		} else {
			lines = multipleAppend2(desc, lines, hasRequiredBinding);
		}
		lines = terzo(allBindings, desc, lines, hasRequiredBinding, expectedTracker, trieTree);
		return lines.toString().replace("{{clazz}}", desc.clazz.getCanonicalName()).replace("{{newInst}}", CodegenImplObjectHash.genNewInstCode(desc.clazz, desc.ctor));
	}

	/**
	 * 
	 * @param lines
	 * @param desc
	 */
	private static void appendSetExtraToKeyValueTypeWrappers(StringBuilder lines, ClassDescriptor desc) {
		append(lines, "java.util.Iterator extraIter = extra.entrySet().iterator();");
		append(lines, "while(extraIter.hasNext()) {");
		for (Method wrapper : desc.keyValueTypeWrappers) {
			append(lines, "java.util.Map.Entry entry = (java.util.Map.Entry)extraIter.next();");
			append(lines, "String key = entry.getKey().toString();");
			append(lines, "com.jsoniter.any.Any value = (com.jsoniter.any.Any)entry.getValue();");
			append(lines, String.format("obj.%s(key, value.object());", wrapper.getName()));
		}
		append(lines, PARENTESICHIUSA);
	}

	/**
	 * 
	 * @param lines
	 * @param desc
	 */
	private static void appendSetExtraProperteis(StringBuilder lines, ClassDescriptor desc) {
		Binding onExtraProperties = desc.onExtraProperties;
		if (GenericsHelper.isSameClass(onExtraProperties.valueType, Map.class)) {
			if (onExtraProperties.field != null) {
				append(lines, String.format("obj.%s = extra;", onExtraProperties.field.getName()));
			} else {
				append(lines, String.format("obj.%s(extra);", onExtraProperties.method.getName()));
			}
			return;
		}
		throw new JsonException("extra properties can only be Map");
	}

	/**
	 * 
	 * @param allBindings
	 * @return
	 */
	private static boolean hasAnythingToBindFrom(List<Binding> allBindings) {
		boolean flag = false;
		for (Binding binding : allBindings) {
			if (binding.fromNames.length > 0) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 
	 * @param allBindings
	 * @return
	 */
	private static int assignMaskForRequiredProperties(List<Binding> allBindings) {
		int requiredIdx = 0;
		for (Binding binding : allBindings) {
			if (binding.asMissingWhenNotPresent) {
				// one bit represent one field
				binding.mask = 1L << requiredIdx;
				requiredIdx++;
			}
		}
		if (requiredIdx > 63) {
			throw new JsonException("too many required properties to track");
		}
		return requiredIdx;
	}

	private static String quinto(String rendered, Binding binding, String marker, int start) {
		int middle = rendered.indexOf('=', start);
		if (middle == -1) {
			throw new JsonException("can not find = in: " + rendered + " ,at " + start);
		}
		middle += 1;
		int end = rendered.indexOf(';', start);
		if (end == -1) {
			throw new JsonException("can not find ; in: " + rendered + " ,at " + start);
		}
		String op = rendered.substring(middle, end);
		if (binding.field != null) {
			if (binding.valueCanReuse) {
				// reuse; then field set
				return String.format(QUINTO, rendered.substring(0, start), binding.field.getName(), binding.field.getName(), op, rendered.substring(end));
			} else {
				// just field set
				return String.format("%sobj.%s=%s%s", rendered.substring(0, start), binding.field.getName(), op, rendered.substring(end));
			}
		} else {
			// method set
			return String.format("%sobj.%s(%s)%s", rendered.substring(0, start), binding.method.getName(), op, rendered.substring(end));
		}
	}

	/**
	 * 
	 * @param rendered
	 * @param binding
	 * @return
	 */
	private static String updateBindingSetOp(String rendered, Binding binding) {
		boolean flag = false;
		String toReturn = rendered;
		if (binding.fromNames.length == 0) {
			flag = false;
		} else {
			flag = true;
		}
		while (flag) {
			String marker = "_" + binding.name + "_";
			int start = toReturn.indexOf(marker);
			if (start == -1) {
				toReturn = rendered;
				flag = false;
				break;
			}
			toReturn = quinto(toReturn, binding, marker, start);
		}
		return toReturn;
	}

	/**
	 * 
	 * @param lines
	 * @param desc
	 */
	private static void appendMissingRequiredProperties(StringBuilder lines, ClassDescriptor desc) {
		append(lines, "java.util.List missingFields = new java.util.ArrayList();");
		for (Binding binding : desc.allDecoderBindings()) {
			if (binding.asMissingWhenNotPresent) {
				long mask = binding.mask;
				append(lines, String.format("com.jsoniter.CodegenAccess.addMissingField(missingFields, tracker, %sL, \"%s\");", mask, binding.name));
			}
		}
		if (desc.onMissingProperties == null || !desc.ctor.parameters.isEmpty()) {
			append(lines,
					"throw new com.jsoniter.spi.JsonException(\"missing required properties: \" + missingFields);");
		} else {
			if (desc.onMissingProperties.field != null) {
				append(lines, String.format("obj.%s = missingFields;", desc.onMissingProperties.field.getName()));
			} else {
				append(lines, String.format("obj.%s(missingFields);", desc.onMissingProperties.method.getName()));
			}
		}
	}
	/**
	 * 
	 * @param lines
	 * @param desc
	 */
	private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
		if (desc.asExtraForUnknownProperties && desc.onExtraProperties == null) {
			append(lines, "throw new com.jsoniter.spi.JsonException('extra property: ' + field.toString());".replace('\'', '"'));
		} else {
			if (desc.asExtraForUnknownProperties || !desc.keyValueTypeWrappers.isEmpty()) {
				append(lines, "if (extra == null) { extra = new java.util.HashMap(); }");
				append(lines, "extra.put(field.toString(), iter.readAny());");
			} else {
				append(lines, "iter.skip();");
			}
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param allBindings
	 * @return
	 */
	private static Map<Integer, Object> buildTriTree(List<Binding> allBindings) {
		Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
		for (Binding field : allBindings) {
			for (String fromName : field.fromNames) {
				byte[] fromNameBytes = fromName.getBytes();
				Map<Byte, Object> current = null;
				if (trieTree.get(fromNameBytes.length) instanceof Map<?, ?>) {
					current = (Map<Byte, Object>) trieTree.get(fromNameBytes.length);
				}
				if (current == null) {
					current = new HashMap<Byte, Object>();
					trieTree.put(fromNameBytes.length, current);
				}
				current = sesto(fromNameBytes, current);
				current.put(fromNameBytes[fromNameBytes.length - 1], field);
			}
		}
		return trieTree;
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param fromNameBytes
	 * @param current
	 * @return
	 */
	private static Map<Byte, Object> sesto(byte[] fromNameBytes, Map<Byte, Object> current) {
		Map<Byte, Object> newMap = current;
		for (int i = 0; i < fromNameBytes.length - 1; i++) {
			byte b = fromNameBytes[i];
			Map<Byte, Object> next = null;
			if (newMap.get(b) instanceof Map<?, ?>) {
				next = (Map<Byte, Object>) newMap.get(b);
			}
			if (next == null) {
				next = new HashMap<Byte, Object>();
				newMap.put(b, next);
			}
			newMap = next;
		}
		return newMap;
	}

	/**
	 * 
	 * @param trieTree
	 * @return
	 */
	private static String renderTriTree(Map<Integer, Object> trieTree) {
		StringBuilder switchBody = new StringBuilder(SBSIZE);
		for (Map.Entry<Integer, Object> entry : trieTree.entrySet()) {
			Integer len = entry.getKey();
			switchBody.append("case " + len + ": \n");
			Map<Byte, Object> current = null;
			if (entry.getValue() instanceof Map<?, ?>) {
				current = (Map<Byte, Object>) entry.getValue();
			}
			addFieldDispatch(switchBody, len, 0, current, new ArrayList<Byte>());
			switchBody.append("break;");
		}
		return switchBody.toString();
	}

	/**
	 * 
	 * @param entry
	 * @param lines
	 * @return
	 */
	private static StringBuilder quarto(Map.Entry<Byte, Object> entry, StringBuilder lines) {
		StringBuilder toReturn = lines;
		Binding field = null;
		boolean support = false;
		if (entry.getValue() instanceof Binding) {
			field = (Binding) entry.getValue();
		}
		if (field.asExtraWhenPresent) {
			support = true;
			append(toReturn, String.format(QUARTO.replace('\'', '"'), field.name));
		} else if (field.shouldSkip) {
			support = true;
			append(toReturn, "iter.skip();");
			append(toReturn, "continue;");
		} else if (!support) {
			support = true;
			append(toReturn, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field)));
		}
		if (field.asMissingWhenNotPresent && support) {
			append(toReturn, "tracker = tracker | " + field.mask + "L;");
		}
		if (support)
			append(toReturn, "continue;");
		return toReturn;
	}

	/**
	 * 
	 * @param i
	 * @param len
	 * @param lines
	 * @param bytesToCompare
	 * @param entry
	 * @param b
	 * @return
	 */
	private static StringBuilder settimo(int i, int len, StringBuilder lines, List<Byte> bytesToCompare,
			Map.Entry<Byte, Object> entry, Byte b) {
		StringBuilder toReturn = lines;
		int size = 0;
		if (i == len - 1) {
			append(toReturn, "if (");
			size = bytesToCompare.size();
			for (int j = 0; j < size; j++) {
				Byte a = bytesToCompare.get(j);
				append(toReturn, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
			}
			append(toReturn, String.format("field.at(%d)==%s", i, b));
			append(toReturn, ") {");
			toReturn = quarto(entry, toReturn);
			append(toReturn, PARENTESICHIUSA);
		}
		return toReturn;
	}

	/**
	 * 
	 * @param lines
	 * @param i
	 * @param len
	 * @param bytesToCompare
	 * @param b
	 * @param next
	 * @return
	 */
	private static StringBuilder ottavo(StringBuilder lines, int i, int len, List<Byte> bytesToCompare, Byte b,
			Map<Byte, Object> next) {
		StringBuilder toReturn = lines;
		append(lines, "if (");
		int size = bytesToCompare.size();
		for (int j = 0; j < size; j++) {
			Byte a = bytesToCompare.get(j);
			append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
		}
		append(lines, String.format("field.at(%d)==%s", i, b));
		append(lines, ") {");
		addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
		append(lines, PARENTESICHIUSA);
		return toReturn;
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
		int size = 0;
		Set<Entry<Byte, Object>> setSize = current.entrySet();
		List<Byte> nextBytesToCompare = null;
		for (Map.Entry<Byte, Object> entry : setSize) {
			Map<Byte, Object> next = null;
			Byte b = entry.getKey();
			lines = settimo(i, len, lines, bytesToCompare, entry, b);
			if (entry.getValue() instanceof Map<?, ?>) {
				next = (Map<Byte, Object>) entry.getValue();
			}
			if (next.size() == 1) {
				nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
				nextBytesToCompare.add(b);
				addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
				continue;
			}
			lines = ottavo(lines, i, len, bytesToCompare, b, next);
		}
	}

	/**
	 * 
	 * @param clazz
	 * @param ctor
	 * @return
	 */
	public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
		StringBuilder lines = new StringBuilder(SBSIZE);
		append(lines, "if (iter.readNull()) { return null; }");
		append(lines, "{{clazz}} obj = {{newInst}};");
		append(lines, "iter.skip();");
		append(lines, "return obj;");
		return lines.toString().replace("{{clazz}}", clazz.getCanonicalName()).replace("{{newInst}}",
				CodegenImplObjectHash.genNewInstCode(clazz, ctor));
	}

	/**
	 * 
	 * @param lines
	 * @param str
	 */
	static void append(StringBuilder lines, String str) {
		lines.append(str);
		lines.append("\n");
	}
}
