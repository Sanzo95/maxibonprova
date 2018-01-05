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
	 * static Map<String, String> DEFAULT_VALUES
	 */
	final static Map<String, String> DEFAULT_VALUES = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7389282851528177046L;

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
		if (!desc.ctor.parameters.isEmpty()) {
			append(toReturn, String.format("%s obj = {{newInst}};", CodegenImplNative.getTypeName(desc.clazz)));
			for (Binding field : desc.fields) {
				append(toReturn, String.format("obj.%s = _%s_;", field.field.getName(), field.name));
			}
			for (Binding setter : desc.setters) {
				append(toReturn, String.format("obj.%s(_%s_);", setter.method.getName(), setter.name));
			}
		}
		return toReturn;
	}
	/**
	 * 
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
	
	private static void multipleAppend2 () {
		
	}
	
	

	/**
	 * genObjectUsingStrict.
	 * 
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
		append(lines, "java.lang.Object existingObj = com.jsoniter.CodegenAccess.resetExistingObject(iter);");
		append(lines, "if (iter.readNull()) { return null; }");
		if (hasRequiredBinding) {
			append(lines, "long tracker = 0;");
		}//11
		if (desc.ctor.parameters.isEmpty()) {
			append(lines, "{{clazz}} obj = {{newInst}};");
			append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
			if (hasRequiredBinding) {
				appendMissingRequiredProperties(lines, desc);
			}
			append(lines, "return obj;");
			append(lines, PARENTESIAPERTA);
		} else {
			for (Binding parameter : desc.ctor.parameters) {
				appendVarDef(lines, parameter);
			}
			append(lines, "if (!com.jsoniter.CodegenAccess.readObjectStart(iter)) {");
			if (hasRequiredBinding) {
				appendMissingRequiredProperties(lines, desc);
			} else {
				append(lines, "return {{newInst}};");
			}
			append(lines, PARENTESICHIUSA);
			for (Binding field : desc.fields) {
				appendVarDef(lines, field);
			}
			for (Binding setter : desc.setters) {
				appendVarDef(lines, setter);
			}
		}
		for (WrapperDescriptor wrapper : desc.bindingTypeWrappers) {
			for (Binding param : wrapper.parameters) {
				appendVarDef(lines, param);
			}
		}
		if (desc.onExtraProperties != null || !desc.keyValueTypeWrappers.isEmpty()) {
			append(lines, "java.util.Map extra = null;");
		}
		append(lines, "com.jsoniter.spi.Slice field = com.jsoniter.CodegenAccess.readObjectFieldAsSlice(iter);");
		append(lines, "boolean once = true;");
		append(lines, "while (once) {");
		append(lines, "once = false;");
		String rendered = renderTriTree(trieTree);
		rendered = primo(desc, rendered); // prima modifica Follow the limit for number of statements in a method
		lines = multipleAppend(allBindings, desc, lines, rendered, hasRequiredBinding, expectedTracker);
		if (desc.onExtraProperties != null) {
			appendSetExtraProperteis(lines, desc);
		}
		if (!desc.keyValueTypeWrappers.isEmpty()) {
			appendSetExtraToKeyValueTypeWrappers(lines, desc);
		}
		lines = secondo (desc, lines);
		appendWrappers(desc.bindingTypeWrappers, lines);
		append(lines, "return obj;");
		return lines.toString().replace("{{clazz}}", desc.clazz.getCanonicalName()).replace("{{newInst}}",
				CodegenImplObjectHash.genNewInstCode(desc.clazz, desc.ctor));
	}

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

	private static boolean hasAnythingToBindFrom(List<Binding> allBindings) {
		for (Binding binding : allBindings) {
			if (binding.fromNames.length > 0) {
				return true;
			}
		}
		return false;
	}

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

	private static String updateBindingSetOp(String rendered, Binding binding) {
		if (binding.fromNames.length == 0) {
			return rendered;
		}
		while (true) {
			String marker = "_" + binding.name + "_";
			int start = rendered.indexOf(marker);
			if (start == -1) {
				return rendered;
			}
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
					rendered = String.format("%scom.jsoniter.CodegenAccess.setExistingObject(iter, obj.%s);obj.%s=%s%s",
							rendered.substring(0, start), binding.field.getName(), binding.field.getName(), op,
							rendered.substring(end));
				} else {
					// just field set
					rendered = String.format("%sobj.%s=%s%s", rendered.substring(0, start), binding.field.getName(), op,
							rendered.substring(end));
				}
			} else {
				// method set
				rendered = String.format("%sobj.%s(%s)%s", rendered.substring(0, start), binding.method.getName(), op,
						rendered.substring(end));
			}
		}
	}

	private static void appendMissingRequiredProperties(StringBuilder lines, ClassDescriptor desc) {
		append(lines, "java.util.List missingFields = new java.util.ArrayList();");
		for (Binding binding : desc.allDecoderBindings()) {
			if (binding.asMissingWhenNotPresent) {
				long mask = binding.mask;
				append(lines,
						String.format(
								"com.jsoniter.CodegenAccess.addMissingField(missingFields, tracker, %sL, \"%s\");",
								mask, binding.name));
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

	private static void appendOnUnknownField(StringBuilder lines, ClassDescriptor desc) {
		if (desc.asExtraForUnknownProperties && desc.onExtraProperties == null) {
			append(lines, "throw new com.jsoniter.spi.JsonException('extra property: ' + field.toString());"
					.replace('\'', '"'));
		} else {
			if (desc.asExtraForUnknownProperties || !desc.keyValueTypeWrappers.isEmpty()) {
				append(lines, "if (extra == null) { extra = new java.util.HashMap(); }");
				append(lines, "extra.put(field.toString(), iter.readAny());");
			} else {
				append(lines, "iter.skip();");
			}
		}
	}

	private static Map<Integer, Object> buildTriTree(List<Binding> allBindings) {
		Map<Integer, Object> trieTree = new HashMap<Integer, Object>();
		try {
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

					for (int i = 0; i < fromNameBytes.length - 1; i++) {
						byte b = fromNameBytes[i];
						Map<Byte, Object> next = null;

						if (current.get(b) instanceof Map<?, ?>) {
							next = (Map<Byte, Object>) current.get(b);
						}

						if (next == null) {
							next = new HashMap<Byte, Object>();
							current.put(b, next);
						}
						current = next;
					}

					current.put(fromNameBytes[fromNameBytes.length - 1], field);
				}
			}
		} catch (Exception e) {
			e.getMessage();
			System.out.println("Eccezione");
		} finally {
			System.out.print("");
		}
		return trieTree;
	}

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
				switchBody.append("break;");
			}
		} catch (Exception e) {
			e.getMessage();
			System.out.println("Eccezione");
		} finally {
			System.out.print("");
		}
		return switchBody.toString();
	}

	private static void addFieldDispatch(StringBuilder lines, int len, int i, Map<Byte, Object> current,
			List<Byte> bytesToCompare) {
		int size = 0;
		Set<Entry<Byte, Object>> setSize = current.entrySet();
		try {
			List<Byte> nextBytesToCompare = null;
			for (Map.Entry<Byte, Object> entry : setSize) {
				Byte b = entry.getKey();
				if (i == len - 1) {
					append(lines, "if (");
					size = bytesToCompare.size();
					for (int j = 0; j < size; j++) {
						Byte a = bytesToCompare.get(j);
						append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
					}
					append(lines, String.format("field.at(%d)==%s", i, b));
					append(lines, ") {");
					Binding field = null;

					if (entry.getValue() instanceof Binding) {
						field = (Binding) entry.getValue();
					}
					boolean support = false;

					if (field.asExtraWhenPresent) {
						support = true;
						append(lines, String.format(
								"throw new com.jsoniter.spi.JsonException('extra property: %s');".replace('\'', '"'),
								field.name));
					} else if (field.shouldSkip) {
						support = true;
						append(lines, "iter.skip();");
						append(lines, "continue;");
					} else if (!support) {
						support = true;
						append(lines, String.format("_%s_ = %s;", field.name, CodegenImplNative.genField(field)));
					}

					if (field.asMissingWhenNotPresent && support) {
						append(lines, "tracker = tracker | " + field.mask + "L;");
					}

					if (support) {
						append(lines, "continue;");
					}

					append(lines, PARENTESICHIUSA);
					continue;
				}
				Map<Byte, Object> next = null;

				if (entry.getValue() instanceof Map<?, ?>) {
					next = (Map<Byte, Object>) entry.getValue();
				}

				if (next.size() == 1) {
					nextBytesToCompare = new ArrayList<Byte>(bytesToCompare);
					nextBytesToCompare.add(b);
					addFieldDispatch(lines, len, i + 1, next, nextBytesToCompare);
					continue;
				}
				append(lines, "if (");
				size = bytesToCompare.size();
				for (int j = 0; j < size; j++) {
					Byte a = bytesToCompare.get(j);
					append(lines, String.format("field.at(%d)==%s && ", i - bytesToCompare.size() + j, a));
				}
				append(lines, String.format("field.at(%d)==%s", i, b));
				append(lines, ") {");
				addFieldDispatch(lines, len, i + 1, next, new ArrayList<Byte>());
				append(lines, PARENTESICHIUSA);
			}
		} catch (Exception e) {
			e.getMessage();
			System.out.println("Eccezione");
		} finally {
			System.out.print("");
		}
	}

	public static String genObjectUsingSkip(Class clazz, ConstructorDescriptor ctor) {
		StringBuilder lines = new StringBuilder(SBSIZE);
		append(lines, "if (iter.readNull()) { return null; }");
		append(lines, "{{clazz}} obj = {{newInst}};");
		append(lines, "iter.skip();");
		append(lines, "return obj;");
		return lines.toString().replace("{{clazz}}", clazz.getCanonicalName()).replace("{{newInst}}",
				CodegenImplObjectHash.genNewInstCode(clazz, ctor));
	}

	static void append(StringBuilder lines, String str) {
		lines.append(str);
		lines.append("\n");
	}
}
