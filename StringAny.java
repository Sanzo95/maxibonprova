package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;

/**
 * 
 * @author MaxiBon
 *
 */
class StringAny extends Any {

	private final static String FALSE = "false";
	private String val;

	/**
	 * StringAny.
	 * 
	 * @param val
	 */
	StringAny(String val) {
		this.val = val;
	}

	@Override
	public ValueType valueType() {
		return ValueType.STRING;
	}

	@Override
	public Object object() {
		return val;
	}

	public Any set(String newVal) {
		val = newVal;
		return this;
	}

	@Override
	public void writeTo(JsonStream stream) throws IOException {
		stream.writeVal(val);
	}

	@Override
	public boolean toBoolean() {
		boolean flag = true;
		int len = val.length();
		if (len == 0) {
			flag = false;
		}
		if (len == 5 && FALSE.equals(val)) {
			flag = false;
		}
		for (int i = 0; i < len; i++) {
			if (flag) {
				switch (val.charAt(i)) {
				case ' ':
					flag = true;
					break;
				case '\t':
					flag = true;
					break;
				case '\n':
					flag = true;
					break;
				case '\r':
					flag = true;
					break;
				default:
					flag = true;
					break;
				}
			} else {
				break;
			}
		}
		return flag;
	}

	@Override
	public int toInt() {
		return Integer.valueOf(val);
	}

	@Override
	public long toLong() {
		return Long.valueOf(val);
	}

	@Override
	public float toFloat() {
		return Float.valueOf(val);
	}

	@Override
	public double toDouble() {
		return Double.valueOf(val);
	}

	@Override
	public String toString() {
		return val;
	}
}
