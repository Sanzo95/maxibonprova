package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.Slice;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * class IterImpl
 * 
 * @author MaxiBon
 *
 */
class IterImpl {

	private IterImpl() {
	}

	/**
	 * readObjectFieldAsHash
	 * 
	 * @param iter
	 * @return
	 * @throws IOException
	 */
	public static final int readObjectFieldAsHash(JsonIterator iter) throws IOException {
		if (readByte(iter) != '"') {
			if (nextToken(iter) != '"') {
				throw iter.reportError("readObjectFieldAsHash", "expect \"");
			}
		}
		long hash = 0x811c9dc5;
		int i = iter.head;
		for (; i < iter.tail; i++) {
			byte c = iter.buf[i];
			if (c == '"') {
				break;
			}
			hash ^= c;
			hash *= 0x1000193;
		}
		iter.head = i + 1;
		if (readByte(iter) != ':') {
			if (nextToken(iter) != ':') {
				throw iter.reportError("readObjectFieldAsHash", "expect :");
			}
		}
		return (int) hash;
	}

	public static final Slice readObjectFieldAsSlice(JsonIterator iter) throws IOException {
		Slice field = readSlice(iter);
		if (nextToken(iter) != ':') {
			throw iter.reportError("readObjectFieldAsSlice", "expect : after object field");
		}
		return field;
	}

	final static void skipArray(JsonIterator iter) throws IOException {
		int level = 1;
		int i = iter.head;
		while (i < iter.tail) {
			switch (iter.buf[i]) {
			case '"': // If inside string, skip it
				iter.head = i + 1;
				skipString(iter);
				i = iter.head - 1; // it will be i++ soon
				break;
			case '[': // If open symbol, increase level
				level++;
				break;
			case ']': // If close symbol, increase level
				level--;

				// If we have returned to the original level, we're done
				if (level == 0) {
					iter.head = i + 1;
					return;
				}
				break;
			default:
				break;
			}
			i++;
		}
		throw iter.reportError("skipArray", "incomplete array");
	}

	final static void skipObject(JsonIterator iter) throws IOException {
		int level = 1;
		int i = iter.head;
		while (i < iter.tail) {
			switch (iter.buf[i]) {
			case '"': // If inside string, skip it
				iter.head = i + 1;
				skipString(iter);
				i = iter.head - 1; // it will be i++ soon
				break;
			case '{': // If open symbol, increase level
				level++;
				break;
			case '}': // If close symbol, increase level
				level--;

				// If we have returned to the original level, we're done
				if (level == 0) {
					iter.head = i + 1;
					return;
				}
				break;
			default:
				break;
			}
			i++;
		}
		throw iter.reportError("skipObject", "incomplete object");
	}

	final static void skipString(JsonIterator iter) throws IOException {
		int end = IterImplSkip.findStringEnd(iter);
		if (end == -1) {
			String stringa1 = "skipString";
			String stringa2 = "incomplete string";
			throw iter.reportError(stringa1, stringa2);
		} else {
			iter.head = end;
		}
	}

	final static void skipUntilBreak(JsonIterator iter) throws IOException {
		// true, false, null, number
		for (int i = iter.head; i < iter.tail; i++) {
			byte c = iter.buf[i];
			if (IterImplSkip.breaks[c]) {
				iter.head = i;
				return;
			}
		}
		iter.head = iter.tail;
	}

	final static boolean skipNumber(JsonIterator iter) throws IOException {
		// true, false, null, number
		boolean dotFound = false;
		for (int i = iter.head; i < iter.tail; i++) {
			byte c = iter.buf[i];
			if (c == '.') {
				dotFound = true;
				continue;
			}
			if (IterImplSkip.breaks[c]) {
				iter.head = i;
				return dotFound;
			}
		}
		iter.head = iter.tail;
		return dotFound;
	}

	// read the bytes between " "
	public final static Slice readSlice(JsonIterator iter) throws IOException {
		if (IterImpl.nextToken(iter) != '"') {
			throw iter.reportError("readSlice", "expect \" for string");
		}
		int end = IterImplString.findSliceEnd(iter);
		if (end == -1) {
			throw iter.reportError("readSlice", "incomplete string");
		} else {
			// reuse current buffer
			iter.reusableSlice.reset(iter.buf, iter.head, end - 1);
			iter.head = end;
			return iter.reusableSlice;
		}
	}

	final static byte nextToken(final JsonIterator iter) throws IOException {
		int i = iter.head;
		for (;;) {
			byte c = iter.buf[i++];
			switch (c) {
			case ' ':
			case '\n':
			case '\r':
			case '\t':
				continue;
			default:
				iter.head = i;
				return c;
			}
		}
	}

	final static byte readByte(JsonIterator iter) throws IOException {
		return iter.buf[iter.head++];
	}

	public static Any readAny(JsonIterator iter) throws IOException {
		int start = iter.head;
		byte c = nextToken(iter);
		switch (c) {
		case '"':
			skipString(iter);
			return Any.lazyString(iter.buf, start, iter.head);
		case 't':
			skipFixedBytes(iter, 3);
			return Any.wrap(true);
		case 'f':
			skipFixedBytes(iter, 4);
			return Any.wrap(false);
		case 'n':
			skipFixedBytes(iter, 3);
			return Any.wrap(0);
		case '[':
			skipArray(iter);
			return Any.lazyArray(iter.buf, start, iter.head);
		case '{':
			skipObject(iter);
			return Any.lazyObject(iter.buf, start, iter.head);
		default:
			if (skipNumber(iter)) {
				return Any.lazyDouble(iter.buf, start, iter.head);
			} else {
				return Any.lazyLong(iter.buf, start, iter.head);
			}
		}
	}

	public static void skipFixedBytes(JsonIterator iter, int n) throws IOException {
		iter.head += n;
	}

	public final static boolean loadMore(JsonIterator iter) throws IOException {
		iter.toString();
		return false;
	}

	public final static int readStringSlowPath(JsonIterator iter, int j) throws IOException {
		try {
			boolean isExpectingLowSurrogate = false;
			for (int i = iter.head; i < iter.tail;) {
				int bc = iter.buf[i++];
				if (bc == '"') {
					iter.head = i;
					return j;
				}
				if (bc == '\\') {
					bc = iter.buf[i++];
					switch (bc) {
					case 'b':
						bc = '\b';
						break;
					case 't':
						bc = '\t';
						break;
					case 'n':
						bc = '\n';
						break;
					case 'f':
						bc = '\f';
						break;
					case 'r':
						bc = '\r';
						break;
					case '"':
					case '/':
					case '\\':
						break;
					case 'u':
						bc = (IterImplString.translateHex(iter.buf[i++]) << 12)
								+ (IterImplString.translateHex(iter.buf[i++]) << 8)
								+ (IterImplString.translateHex(iter.buf[i++]) << 4)
								+ IterImplString.translateHex(iter.buf[i++]);
						if (Character.isHighSurrogate((char) bc)) {
							if (isExpectingLowSurrogate) {
								throw new JsonException("invalid surrogate");
							} else {
								isExpectingLowSurrogate = true;
							}
						} else if (Character.isLowSurrogate((char) bc)) {
							if (isExpectingLowSurrogate) {
								isExpectingLowSurrogate = false;
							} else {
								throw new JsonException("invalid surrogate");
							}
						} else {
							if (isExpectingLowSurrogate) {
								throw new JsonException("invalid surrogate");
							}
						}
						break;
					default:
						String stringa1 = "readStringSlowPath";
						String stringa2 = "invalid escape character: " + bc;
						throw iter.reportError(stringa1, stringa2);
					}
				} else if ((Integer
						.getInteger(Long.toString(SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
								Long.getLong(Integer.toString(0x80)).longValue(), '&')))
						.intValue()) != 0) {
					final int u2 = iter.buf[i++];
					if ((Integer
							.getInteger(
									Long.toString(SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
											Long.getLong(Integer.toString(0xE0)).longValue(), '&')))
							.intValue()) == 0xC0) {
						bc = ((Integer
								.getInteger(Long
										.toString(SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
												Long.getLong(Integer.toString(0x1F)).longValue(), '&')))
								.intValue()) << 6)
								+ (Integer
										.getInteger(Long.toString(
												SupportBitwise.bitwise(Long.getLong(Integer.toString(u2)).longValue(),
														Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
										.intValue());
					} else {
						final int u3 = iter.buf[i++];
						if ((Integer
								.getInteger(Long
										.toString(SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
												Long.getLong(Integer.toString(0xF0)).longValue(), '&')))
								.intValue()) == 0xE0) {
							bc = ((Integer
									.getInteger(Long.toString(
											SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
													Long.getLong(Integer.toString(0x0F)).longValue(), '&')))
									.intValue()) << 12) + ((Integer
											.getInteger(Long.toString(SupportBitwise.bitwise(
													Long.getLong(Integer.toString(u2)).longValue(),
													Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
											.intValue()) << 6)
									+ (Integer
											.getInteger(Long.toString(SupportBitwise.bitwise(
													Long.getLong(Integer.toString(u3)).longValue(),
													Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
											.intValue());
						} else {
							final int u4 = iter.buf[i++];
							if ((Integer
									.getInteger(Long.toString(
											SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
													Long.getLong(Integer.toString(0xF8)).longValue(), '&')))
									.intValue()) == 0xF0) {
								bc = ((Integer
										.getInteger(Long.toString(
												SupportBitwise.bitwise(Long.getLong(Integer.toString(bc)).longValue(),
														Long.getLong(Integer.toString(0x07)).longValue(), '&')))
										.intValue()) << 18)
										+ ((Integer
												.getInteger(Long.toString(SupportBitwise.bitwise(
														Long.getLong(Integer.toString(u2)).longValue(),
														Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
												.intValue()) << 12)
										+ ((Integer
												.getInteger(Long.toString(SupportBitwise.bitwise(
														Long.getLong(Integer.toString(u3)).longValue(),
														Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
												.intValue()) << 6)
										+ (Integer
												.getInteger(Long.toString(SupportBitwise.bitwise(
														Long.getLong(Integer.toString(u4)).longValue(),
														Long.getLong(Integer.toString(0x3F)).longValue(), '&')))
												.intValue());
							} else {
								throw iter.reportError("readStringSlowPath", "invalid unicode character");
							}
							if (bc >= 0x10000) {
								// check if valid unicode
								if (bc >= 0x110000) {
									throw iter.reportError("readStringSlowPath", "invalid unicode character");
								}

								// split surrogates
								final int sup = bc - 0x10000;
								if (iter.reusableChars.length == j) {
									char[] newBuf = new char[iter.reusableChars.length * 2];
									System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
									iter.reusableChars = newBuf;
								}
								Integer a = (Integer
										.getInteger(Long.toString(
												SupportBitwise.bitwise(Long.getLong(Integer.toString(sup)).longValue(),
														Long.getLong(Integer.toString(0x3ff)).longValue(), '&')))
										.intValue() + +0xdc00);
								iter.reusableChars[j++] = a.toString().toCharArray()[0];
								if (iter.reusableChars.length == j) {
									char[] newBuf = new char[iter.reusableChars.length * 2];
									System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
									iter.reusableChars = newBuf;
								}
								Integer b = (Integer
										.getInteger(Long.toString(
												SupportBitwise.bitwise(Long.getLong(Integer.toString(sup)).longValue(),
														Long.getLong(Integer.toString(0x3ff)).longValue(), '&')))
										.intValue() + 0xdc00);
								iter.reusableChars[j++] = b.toString().toCharArray()[0];
								continue;
							}
						}
					}
				}
				if (iter.reusableChars.length == j) {
					char[] newBuf = new char[iter.reusableChars.length * 2];
					System.arraycopy(iter.reusableChars, 0, newBuf, 0, iter.reusableChars.length);
					iter.reusableChars = newBuf;
				}
				iter.reusableChars[j++] = Integer.toString(bc).charAt(0);
			}
			throw iter.reportError("readStringSlowPath", "incomplete string");
		} catch (IndexOutOfBoundsException e) {
			throw iter.reportError("readString", "incomplete string");
		}
	}

	public static int updateStringCopyBound(final int bound) {
		return bound;
	}

	static final int readInt(final JsonIterator iter, final byte c) throws IOException {
		int ind = IterImplNumber.intDigits[c];
		if (ind == 0) {
			IterImplForStreaming.assertNotLeadingZero(iter);
			return 0;
		}
		if (ind == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
			throw iter.reportError("readInt", "expect 0~9");
		}
		if (iter.tail - iter.head > 9) {
			int i = iter.head;
			int ind2 = IterImplNumber.intDigits[iter.buf[i]];
			if (ind2 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				return -ind;
			}
			int ind3 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind3 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 10 + ind2;
				return -ind;
			}
			int ind4 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind4 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 100 + ind2 * 10 + ind3;
				return -ind;
			}
			int ind5 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind5 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 1000 + ind2 * 100 + ind3 * 10 + ind4;
				return -ind;
			}
			int ind6 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind6 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 10000 + ind2 * 1000 + ind3 * 100 + ind4 * 10 + ind5;
				return -ind;
			}
			int ind7 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind7 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 100000 + ind2 * 10000 + ind3 * 1000 + ind4 * 100 + ind5 * 10 + ind6;
				return -ind;
			}
			int ind8 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind8 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 1000000 + ind2 * 100000 + ind3 * 10000 + ind4 * 1000 + ind5 * 100 + ind6 * 10 + ind7;
				return -ind;
			}
			int ind9 = IterImplNumber.intDigits[iter.buf[++i]];
			ind = ind * 10000000 + ind2 * 1000000 + ind3 * 100000 + ind4 * 10000 + ind5 * 1000 + ind6 * 100 + ind7 * 10
					+ ind8;
			iter.head = i;
			if (ind9 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				return -ind;
			}
		}
		return IterImplForStreaming.readIntSlowPath(iter, ind);
	}

	static final long readLong(final JsonIterator iter, final byte c) throws IOException {
		long ind = IterImplNumber.intDigits[c];
		if (ind == 0) {
			IterImplForStreaming.assertNotLeadingZero(iter);
			return 0;
		}
		if (ind == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
			throw iter.reportError("readLong", "expect 0~9");
		}
		if (iter.tail - iter.head > 9) {
			int i = iter.head;
			int ind2 = IterImplNumber.intDigits[iter.buf[i]];
			if (ind2 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				return -ind;
			}
			int ind3 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind3 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 10 + ind2;
				return -ind;
			}
			int ind4 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind4 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 100 + ind2 * 10 + ind3;
				return -ind;
			}
			int ind5 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind5 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 1000 + ind2 * 100 + ind3 * 10 + ind4;
				return -ind;
			}
			int ind6 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind6 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 10000 + ind2 * 1000 + ind3 * 100 + ind4 * 10 + ind5;
				return -ind;
			}
			int ind7 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind7 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 100000 + ind2 * 10000 + ind3 * 1000 + ind4 * 100 + ind5 * 10 + ind6;
				return -ind;
			}
			int ind8 = IterImplNumber.intDigits[iter.buf[++i]];
			if (ind8 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				iter.head = i;
				ind = ind * 1000000 + ind2 * 100000 + ind3 * 10000 + ind4 * 1000 + ind5 * 100 + ind6 * 10 + ind7;
				return -ind;
			}
			int ind9 = IterImplNumber.intDigits[iter.buf[++i]];
			ind = ind * 10000000 + ind2 * 1000000 + ind3 * 100000 + ind4 * 10000 + ind5 * 1000 + ind6 * 100 + ind7 * 10
					+ ind8;
			iter.head = i;
			if (ind9 == IterImplNumber.INVALID_CHAR_FOR_NUMBER) {
				return -ind;
			}
		}
		return IterImplForStreaming.readLongSlowPath(iter, ind);
	}

	static final double readDouble(final JsonIterator iter) throws IOException {
		int oldHead = iter.head;
		try {
			try {
				long value = IterImplNumber.readLong(iter); // without the dot &
															// sign
				if (iter.head == iter.tail) {
					return value;
				}
				byte c = iter.buf[iter.head];
				if (c == '.') {
					iter.head++;
					int start = iter.head;
					c = iter.buf[iter.head++];
					long decimalPart = readLong(iter, c);
					if (decimalPart == Long.MIN_VALUE) {
						return IterImplForStreaming.readDoubleSlowPath(iter);
					}
					decimalPart = -decimalPart;
					int decimalPlaces = iter.head - start;
					if (decimalPlaces > 0 && decimalPlaces < IterImplNumber.POW10.length
							&& (iter.head - oldHead) < 10) {
						BigDecimal number = new BigDecimal(IterImplNumber.POW10[decimalPlaces]);
						return value + (decimalPart / number.floatValue());
					} else {
						iter.head = oldHead;
						return IterImplForStreaming.readDoubleSlowPath(iter);
					}
				} else {
					return value;
				}
			} finally {
				if (iter.head < iter.tail && (iter.buf[iter.head] == 'e' || iter.buf[iter.head] == 'E')) {
					iter.head = oldHead;
					return IterImplForStreaming.readDoubleSlowPath(iter);
				}
			}
		} catch (JsonException e) {
			iter.head = oldHead;
			return IterImplForStreaming.readDoubleSlowPath(iter);
		}
	}
}