package com.jsoniter.any;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.JsonIteratorPool;
import com.jsoniter.ValueType;
import com.jsoniter.spi.JsonException;

import java.io.IOException;

/**
 * 
 * @author MaxiBon
 *
 */
class StringLazyAny extends LazyAny {
	private final static String FALSE = "false";
	private String cache;

	/**
	 * StringLazyAny.
	 * 
	 * @param data
	 * @param head
	 * @param tail
	 */
	StringLazyAny(byte[] data, int head, int tail) {
		super(data, head, tail);
	}

	@Override
	public ValueType valueType() {
		return ValueType.STRING;
	}

	@Override
	public Object object() {
		fillCache();
		return cache;
	}

	@Override
	public boolean toBoolean() {
		fillCache();
		boolean flag = true;
		int len = cache.length();
		if (len == 0) {
			flag = false;
		}
		if (len == 5 && FALSE.equals(cache)) {
			flag = false;
		}
		for (int i = 0; i < len; i++) {
			if (flag) {
				switch (cache.charAt(i)) {
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
		JsonIterator iter = parse();
		try {
			CodegenAccess.nextToken(iter);
			return iter.readInt();
		} catch (IOException e) {
			System.err.println("Error: IOException");
			throw new JsonException();
		} finally {
			JsonIteratorPool.returnJsonIterator(iter);
		}
	}

	@Override
	public long toLong() {
		JsonIterator iter = parse();
		try {
			CodegenAccess.nextToken(iter);
			return iter.readLong();
		} catch (IOException e) {
			throw new JsonException("Error: IOException");
		} finally {
			JsonIteratorPool.returnJsonIterator(iter);
		}
	}

	@Override
	public float toFloat() {
		JsonIterator iter = parse();
		try {
			CodegenAccess.nextToken(iter);
			return iter.readFloat();
		} catch (IOException e) {
			throw new JsonException("Error: IOException");
		} finally {
			JsonIteratorPool.returnJsonIterator(iter);
		}
	}

	@Override
	public double toDouble() {
		JsonIterator iter = parse();
		try {
			CodegenAccess.nextToken(iter);
			return iter.readDouble();
		} catch (IOException e) {
			throw new JsonException("Error: IOException");
		} finally {
			JsonIteratorPool.returnJsonIterator(iter);
		}
	}

	@Override
	public String toString() {
		fillCache();
		return cache;
	}

	private void fillCache() {
		if (cache == null) {
			JsonIterator iter = parse();
			try {
				cache = iter.readString();
			} catch (IOException e) {
				throw new JsonException();
			} finally {
				JsonIteratorPool.returnJsonIterator(iter);
			}
		}
	}
}
