package com.jsoniter.any;

import com.jsoniter.ValueType;
import com.jsoniter.output.JsonStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @author Maxibon
 *
 */
class ListWrapperAny extends Any {

	private final List val;
	private List<Any> cache;

	/**
	 * ListWrapperAny.
	 * 
	 * @param val
	 */
	ListWrapperAny(List val) {
		this.val = val;
	}

	@Override
	public ValueType valueType() {
		return ValueType.ARRAY;
	}

	@Override
	public List<Any> object() {
		fillCache();
		return cache;
	}

	@Override
	public boolean toBoolean() {
		return !val.isEmpty();
	}

	@Override
	public int toInt() {
		return size();
	}

	@Override
	public long toLong() {
		return size();
	}

	@Override
	public float toFloat() {
		return size();
	}

	@Override
	public double toDouble() {
		return size();
	}

	@Override
	public String toString() {
		if (cache == null) {
			return JsonStream.serialize(val);
		} else {
			fillCache();
			return JsonStream.serialize(cache);
		}
	}

	@Override
	public void writeTo(JsonStream stream) throws IOException {
		if (cache == null) {
			stream.writeVal(val);
		} else {
			fillCache();
			stream.writeVal(cache);
		}
	}

	@Override
	public int size() {
		return val.size();
	}

	@Override
	public Any get(int index) {
		return fillCacheUntil(index);
	}

	@Override
	public Any get(Object[] keys, int idx) {
		if (idx == keys.length) {
			return this;
		}
		Object key = keys[idx];
		if (isWildcard(key)) {
			fillCache();
			List<Any> result = new ArrayList<Any>();
			for (Any element : cache) {
				Any mapped = element.get(keys, idx + 1);
				if (mapped.valueType() != ValueType.INVALID) {
					result.add(mapped);
				}
			}
			return Any.rewrap(result);
		}
		try {
			if (key instanceof Integer) {
				return fillCacheUntil((Integer) key).get(keys, idx + 1);
			}

		} catch (IndexOutOfBoundsException e) {
			return new NotFoundAny(keys, idx, object());
		} catch (ClassCastException e) {
			return new NotFoundAny(keys, idx, object());
		}
		return null;
	}

	@Override
	public Iterator<Any> iterator() {
		return new WrapperIterator();
	}

	private void fillCache() {
		if (cache == null) {
			cache = new ArrayList<Any>();
		}
		if (cache.size() == val.size()) {
			return;
		}
		int size = val.size();
		for (int i = cache.size(); i < size; i++) {
			Any element = Any.wrap(val.get(i));
			cache.add(element);
		}
	}

	private Any fillCacheUntil(int index) {
		if (cache == null) {
			cache = new ArrayList<Any>();
		}
		if (index < cache.size()) {
			return cache.get(index);
		}
		int size = val.size();
		for (int i = cache.size(); i < size; i++) {
			Any element = Any.wrap(val.get(i));
			cache.add(element);
			if (index == i) {
				return element;
			}
		}
		return new NotFoundAny(index, val);
	}

	private class WrapperIterator implements Iterator<Any> {

		private int index;

		@Override
		/**
		 * hasNext.
		 */
		public boolean hasNext() {
			return index < val.size();
		}

		@Override
		public Any next() {
			if (cache == null) {
				cache = new ArrayList<Any>();
			}
			if (index == cache.size()) {
				cache.add(Any.wrap(val.get(index)));
			}
			return cache.get(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
