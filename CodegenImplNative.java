package com.jsoniter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.jsoniter.any.Any;
import com.jsoniter.spi.Binding;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;

/**
 * class CodegenImplNative
 * 
 * @author MaxiBon
 *
 */
class CodegenImplNative {
	/**
	 * String "decoder for "
	 */
	static final String DECODEFOR = "decoder for ";
	/**
	 * String "null3"
	 */
	static final String NULL3 = "null3";

	/**
	 * String "null4"
	 */
	static final String NULL4 = "null4";
	/**
	 * Err1
	 */
	static final String ERR1 = "err1";
	/**
	 * Err2
	 */
	static final String ERR2 = "err2";
	/**
	 * Err3
	 */
	static final String ERR3 = "err3";
	/**
	 * Err4
	 */
	static final String ERR4 = "err4";

	/**
	 * default private constructor
	 */
	private CodegenImplNative() {
	}

	/**
	 * NATIVE_READS = HashMap<String, String>()
	 */
	final static Map<String, String> NATIVE_READS = new HashMap<String, String>() {
		{
			put("float", "iter.readFloat()");
			put("double", "iter.readDouble()");
			put("boolean", "iter.readBoolean()");
			put("byte", "iter.readShort()");
			put("short", "iter.readShort()");
			put("int", "iter.readInt()");
			put("char", "iter.readInt()");
			put("long", "iter.readLong()");
			put(Float.class.getName(), "(iter.readNull() ? null : java.lang.Float.valueOf(iter.readFloat()))");
			put(Double.class.getName(), "(iter.readNull() ? null : java.lang.Double.valueOf(iter.readDouble()))");
			put(Boolean.class.getName(), "(iter.readNull() ? null : java.lang.Boolean.valueOf(iter.readBoolean()))");
			put(Byte.class.getName(), "(iter.readNull() ? null : java.lang.Byte.valueOf((byte)iter.readShort()))");
			put(Character.class.getName(),
					"(iter.readNull() ? null : java.lang.Character.valueOf((char)iter.readShort()))");
			put(Short.class.getName(), "(iter.readNull() ? null : java.lang.Short.valueOf(iter.readShort()))");
			put(Integer.class.getName(), "(iter.readNull() ? null : java.lang.Integer.valueOf(iter.readInt()))");
			put(Long.class.getName(), "(iter.readNull() ? null : java.lang.Long.valueOf(iter.readLong()))");
			put(BigDecimal.class.getName(), "iter.readBigDecimal()");
			put(BigInteger.class.getName(), "iter.readBigInteger()");
			put(String.class.getName(), "iter.readString()");
			put(Object.class.getName(), "iter.read()");
			put(Any.class.getName(), "iter.readAny()");
		}
	};
	/**
	 * NATIVE_DECODERS = HashMap<Class, Decoder>()
	 */
	final static Map<Class, Decoder> NATIVE_DECODERS = new HashMap<Class, Decoder>() {
		{
			put(float.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readFloat();
				}
			});
			put(Float.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readFloat();
				}
			});
			put(double.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readDouble();
				}
			});
			put(Double.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readDouble();
				}
			});
			put(boolean.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readBoolean();
				}
			});
			put(Boolean.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readBoolean();
				}
			});
			put(byte.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return Byte.valueOf(Short.toString(iter.readShort()).getBytes()[0]);
				}
			});
			put(Byte.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : Short.toString(iter.readShort()).getBytes()[0];
				}
			});
			put(short.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readShort();
				}
			});
			put(Short.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readShort();
				}
			});
			put(int.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readInt();
				}
			});
			put(Integer.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readInt();
				}
			});
			put(char.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return Integer.toString(iter.readInt()).charAt(0);
				}
			});
			put(Character.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : Integer.toString(iter.readInt()).charAt(0);
				}
			});
			put(long.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readLong();
				}
			});
			put(Long.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readNull() ? null : iter.readLong();
				}
			});
			put(BigDecimal.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readBigDecimal();
				}
			});
			put(BigInteger.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readBigInteger();
				}
			});
			put(String.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readString();
				}
			});
			put(Object.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.read();
				}
			});
			put(Any.class, new Decoder() {
				@Override
				public Object decode(JsonIterator iter) throws IOException {
					return iter.readAny();
				}
			});
		}
	};

	/**
	 * genReadOp.
	 * 
	 * @param type
	 * @return
	 */
	public static String genReadOp(Type type) {
		String cacheKey = TypeLiteral.create(type).getDecoderCacheKey();
		return String.format("(%s)%s", getTypeName(type), genReadOp(cacheKey, type));
	}

	/**
	 * 
	 * @param fieldType
	 * @return
	 */
	public static String getTypeName(Type fieldType) {
		if (fieldType instanceof Class) {
			Class clazz = (Class) fieldType;
			return clazz.getCanonicalName();
		} else if (fieldType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) fieldType;
			Class clazz = null;
			if (pType.getRawType() instanceof Class) {
				clazz = (Class) pType.getRawType();
			}
			return clazz.getCanonicalName();
		} else if (fieldType instanceof WildcardType) {
			return Object.class.getCanonicalName();
		} else {
			throw new JsonException("unsupported type: " + fieldType);
		}
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	static String genField(Binding field) {
		String fieldCacheKey = field.decoderCacheKey();
		Type fieldType = field.valueType;
		return String.format("(%s)%s", getTypeName(fieldType), genReadOp(fieldCacheKey, fieldType));

	}

	/**
	 * 
	 * @param d
	 * @param t
	 * @return
	 */
	private static String limitStatements(Decoder d, Type t) {
		String s = "null1";
		if (d == null && t instanceof Class) {
			Class clazz = (Class) t;
			String nativeRead = NATIVE_READS.get(clazz.getCanonicalName());
			if (nativeRead != null) {
				s = nativeRead;
			}
		} else if (t instanceof WildcardType) {
			s = NATIVE_READS.get(Object.class.getCanonicalName());
		}
		return s;
	}

	/**
	 * 
	 * @param cK
	 * @return
	 */
	private static String limitStatements2(String cK) {
		String s2 = "null2";
		if (Codegen.canStaticAccess(cK)) {
			s2 = String.format("%s.decode_(iter)", cK);
		} else {
			// can not use static "decode_" method to access, go through
			// codegen cache
			s2 = String.format("com.jsoniter.CodegenAccess.read(\"%s\", iter)", cK);
		}
		return s2;
	}

	/**
	 * 
	 * @param err
	 * @param toReturn
	 * @param cacheKey
	 * @param print
	 */
	private static void limitStatement3If(String err, String toReturn, String cacheKey, String print) {
		if (err.equals(toReturn)) {
			throw new JsonException(DECODEFOR + cacheKey + print);
		}
	}
	/**
	 * 
	 * @param vT
	 * @param d
	 * @param cK
	 * @return
	 */
	private static String limitStatements4(Type vT, Decoder d, String cK) {
		String s = (vT == int.class) ? (d instanceof Decoder.IntDecoder) == false ? ERR1 : String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", cK) : NULL4;
		String err = "must implement Decoder.IntDecoder";
		limitStatement3If(ERR1,s,cK, err);
		s = (vT == long.class) ? (d instanceof Decoder.LongDecoder) == false ? ERR2 : String.format("com.jsoniter.CodegenAccess.readLong(\"%s\", iter)", cK) : NULL4;
		err = "must implement Decoder.LongDecoder";
		limitStatement3If(ERR2,s,cK, err);
		s = (vT == float.class) ? (d instanceof Decoder.FloatDecoder) == false ? ERR3 : String.format("com.jsoniter.CodegenAccess.readFloat(\"%s\", iter)", cK) : NULL4;
		err = "must implement Decoder.FloatDecoder";
		limitStatement3If(ERR3,s,cK, err);
		s = (vT == double.class) ? (d instanceof Decoder.DoubleDecoder) == false ? ERR4 : String.format("com.jsoniter.CodegenAccess.readDouble(\"%s\", iter)", cK) : NULL4;
		err = "must implement Decoder.DoubleDecoder";
		limitStatement3If(ERR4,s,cK, err);	
		return s;
	}

	/**
	 * 
	 * @param cacheKey
	 * @param valueType
	 * @return
	 */
	private static String genReadOp(String cacheKey, Type valueType) {
		// the field decoder might be registered directly
		Decoder decoder = JsoniterSpi.getDecoder(cacheKey);
		String toReturn1 = "null1";
		String toReturn2 = "null2";
		if (decoder == null) {
			// if cache key is for field, and there is no field decoder
			// specified
			// update cache key for normal type
			cacheKey = TypeLiteral.create(valueType).getDecoderCacheKey();
			decoder = JsoniterSpi.getDecoder(cacheKey);
			toReturn1 = limitStatements(decoder, valueType);
			toReturn2 = limitStatements2(cacheKey);
		}
		String toReturn3 = (valueType == boolean.class) ? (decoder instanceof Decoder.BooleanDecoder) == false ? ERR1 : String.format("com.jsoniter.CodegenAccess.readBoolean(\"%s\", iter)", cacheKey) : NULL3;
		String err = "must implement Decoder.BooleanDecoder";
		limitStatement3If(ERR1,toReturn3,cacheKey, err);
		toReturn3 = (valueType == byte.class) ? (decoder instanceof Decoder.ShortDecoder) == false ? ERR2 : String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", cacheKey) : NULL3;	
		err =  "must implement Decoder.ShortDecoder";
		limitStatement3If(ERR2,toReturn3,cacheKey, err);
		toReturn3 = (valueType == short.class) ? (decoder instanceof Decoder.ShortDecoder) == false ? ERR3 : String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", cacheKey) : NULL3;
		limitStatement3If(ERR3,toReturn3,cacheKey, err);
		toReturn3 = (valueType == char.class) ? (decoder instanceof Decoder.IntDecoder) == false ? ERR4 : String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", cacheKey) : NULL3;
		err =  "must implement Decoder.IntDecoder";
		limitStatement3If(ERR4,toReturn3,cacheKey, err);
		return "null1".equals(toReturn1) ? "null2".equals(toReturn2) ? "null3".equals(toReturn3) ? "null4".equals(limitStatements4(valueType, decoder, cacheKey)) ? String.format("com.jsoniter.CodegenAccess.read(\"%s\", iter)", cacheKey) : limitStatements4(valueType, decoder, cacheKey) : toReturn3 : toReturn2 : toReturn1;
	}
}
