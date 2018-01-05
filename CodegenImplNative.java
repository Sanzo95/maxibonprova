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
	 * default private constructor
	 */
	private CodegenImplNative() {
	}

	/**
	 * 
	 */
	final static Map<String, String> NATIVE_READS=new HashMap<String,String>(){{put("float","iter.readFloat()");put("double","iter.readDouble()");put("boolean","iter.readBoolean()");put("byte","iter.readShort()");put("short","iter.readShort()");put("int","iter.readInt()");put("char","iter.readInt()");put("long","iter.readLong()");put(Float.class.getName(),"(iter.readNull() ? null : java.lang.Float.valueOf(iter.readFloat()))");put(Double.class.getName(),"(iter.readNull() ? null : java.lang.Double.valueOf(iter.readDouble()))");put(Boolean.class.getName(),"(iter.readNull() ? null : java.lang.Boolean.valueOf(iter.readBoolean()))");put(Byte.class.getName(),"(iter.readNull() ? null : java.lang.Byte.valueOf((byte)iter.readShort()))");put(Character.class.getName(),"(iter.readNull() ? null : java.lang.Character.valueOf((char)iter.readShort()))");put(Short.class.getName(),"(iter.readNull() ? null : java.lang.Short.valueOf(iter.readShort()))");put(Integer.class.getName(),"(iter.readNull() ? null : java.lang.Integer.valueOf(iter.readInt()))");put(Long.class.getName(),"(iter.readNull() ? null : java.lang.Long.valueOf(iter.readLong()))");put(BigDecimal.class.getName(),"iter.readBigDecimal()");put(BigInteger.class.getName(),"iter.readBigInteger()");put(String.class.getName(),"iter.readString()");put(Object.class.getName(),"iter.read()");put(Any.class.getName(),"iter.readAny()");}};
	/**
	 * 
	 */
	final static Map<Class, Decoder> NATIVE_DECODERS=new HashMap<Class,Decoder>(){{put(float.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readFloat();}});put(Float.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readFloat();}});put(double.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readDouble();}});put(Double.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readDouble();}});put(boolean.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readBoolean();}});put(Boolean.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readBoolean();}});put(byte.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return Byte.valueOf(Short.toString(iter.readShort()).getBytes()[0]);}});put(Byte.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:Short.toString(iter.readShort()).getBytes()[0];}});put(short.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readShort();}});put(Short.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readShort();}});put(int.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readInt();}});put(Integer.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readInt();}});put(char.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return Integer.toString(iter.readInt()).charAt(0);}});put(Character.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:Integer.toString(iter.readInt()).charAt(0);}});put(long.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readLong();}});put(Long.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readNull()?null:iter.readLong();}});put(BigDecimal.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readBigDecimal();}});put(BigInteger.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readBigInteger();}});put(String.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readString();}});put(Object.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.read();}});put(Any.class,new Decoder(){@Override public Object decode(JsonIterator iter)throws IOException{return iter.readAny();}});}};

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
	 * @param vT
	 * @param d
	 * @param cK
	 * @return
	 */
	private static String limitStatements3(Type vT, Decoder d, String cK) {
		String s = (vT == int.class) ? (d instanceof Decoder.IntDecoder) == false ? "err1" : String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", cK) : NULL4;
		if ("err1".equals(s)) {
			throw new JsonException(DECODEFOR + cK + "must implement Decoder.IntDecoder");
		}
		s = (vT == long.class) ? (d instanceof Decoder.LongDecoder) == false ? "err2" : String.format("com.jsoniter.CodegenAccess.readLong(\"%s\", iter)", cK) : NULL4;
		if ("err2".equals(s)) {
			throw new JsonException(DECODEFOR + cK + "must implement Decoder.LongDecoder");
		}
		s = (vT == float.class) ? (d instanceof Decoder.FloatDecoder) == false ? "err3" : String.format("com.jsoniter.CodegenAccess.readFloat(\"%s\", iter)", cK) : NULL4;
		if ("err3".equals(s)) {
			throw new JsonException(DECODEFOR + cK + "must implement Decoder.FloatDecoder");
		}
		s = (vT == double.class) ? (d instanceof Decoder.DoubleDecoder) == false ? "err3" : String.format("com.jsoniter.CodegenAccess.readDouble(\"%s\", iter)", cK) : NULL4;
		if ("err4".equals(s)) {
			throw new JsonException(DECODEFOR + cK + "must implement Decoder.DoubleDecoder");
		}	
		return s;
	}

	private static String genReadOp(String cacheKey, Type valueType) {
		// the field decoder might be registered directly
		Decoder decoder = JsoniterSpi.getDecoder(cacheKey);
		String toReturn1 = "null1";
		String toReturn2 = "null2";
		String cacheKeyCopy = cacheKey;
		if (decoder == null) {
			// if cache key is for field, and there is no field decoder
			// specified
			// update cache key for normal type
			cacheKeyCopy = TypeLiteral.create(valueType).getDecoderCacheKey();
			decoder = JsoniterSpi.getDecoder(cacheKeyCopy);
			toReturn1 = limitStatements(decoder, valueType);
			toReturn2 = limitStatements2(cacheKeyCopy);
		}
		String toReturn3 = (valueType == boolean.class) ? (decoder instanceof Decoder.BooleanDecoder) == false ? "err1" : String.format("com.jsoniter.CodegenAccess.readBoolean(\"%s\", iter)", cacheKeyCopy) : NULL3;
		if ("err1".equals(toReturn3)) {
			throw new JsonException(DECODEFOR + cacheKeyCopy + "must implement Decoder.BooleanDecoder");
		}
		toReturn3 = (valueType == byte.class) ? (decoder instanceof Decoder.ShortDecoder) == false ? "err2" : String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", cacheKeyCopy) : NULL3;	
		if ("err2".equals(toReturn3)) {
			throw new JsonException(DECODEFOR + cacheKeyCopy + "must implement Decoder.ShortDecoder");
		}
		toReturn3 = (valueType == short.class) ? (decoder instanceof Decoder.ShortDecoder) == false ? "err3" : String.format("com.jsoniter.CodegenAccess.readShort(\"%s\", iter)", cacheKeyCopy) : NULL3;
		if ("err3".equals(toReturn3)) {
			throw new JsonException(DECODEFOR + cacheKeyCopy + "must implement Decoder.ShortDecoder");
		}
		toReturn3 = (valueType == char.class) ? (decoder instanceof Decoder.IntDecoder) == false ? "err4" : String.format("com.jsoniter.CodegenAccess.readInt(\"%s\", iter)", cacheKeyCopy) : "null3";
		if ("err4".equals(toReturn3)) {
			throw new JsonException(DECODEFOR + cacheKeyCopy + "must implement Decoder.IntDecoder");
		}
		String toReturn4 = limitStatements3(valueType, decoder, cacheKeyCopy);
		String toReturn5 = String.format("com.jsoniter.CodegenAccess.read(\"%s\", iter)", cacheKeyCopy);
		return "null1".equals(toReturn1) ? "null2".equals(toReturn2) ? "null3".equals(toReturn3) ? "null4".equals(toReturn4) ? toReturn5 : toReturn4 : toReturn3 : toReturn2 : toReturn1;
	}
}
