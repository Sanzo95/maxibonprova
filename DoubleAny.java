package com.jsoniter.any;

import java.io.IOException;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

/**
 * 
 * @author Maxibon
 *
 */
class DoubleAny extends Any {
	/**
	 * double val
	 */
	private double val;

	/**
	 * DoubleAny.
	 * 
	 * @param val
	 */
	DoubleAny(double val) {
		this.val = val;
	}

	@Override
	public ValueType valueType() {
		return ValueType.NUMBER;
	}

	@Override
	public Object object() {
		return val;
	}

	@Override
	public boolean toBoolean() {
		return val != 0;
	}

	@Override
	public int toInt() {
		Double d = val;
		return d.intValue();
	}

	@Override
	public long toLong() {
		Double d = val;
		return d.longValue();
	}

	@Override
	public float toFloat() {
		Double d = val;
		return d.floatValue();
	}

	@Override
	public double toDouble() {
		return val;
	}

	@Override
	public String toString() {
		return String.valueOf(val);
	}

	public Any set(double newVal) {
		this.val = newVal;
		return this;
	}

	@Override
	public void writeTo(JsonStream stream) throws IOException {
		stream.writeVal(val);
	}
}
