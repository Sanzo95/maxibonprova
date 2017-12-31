package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

/**
 * Public Class MaybeStringIntDecoder.
 * 
 * @author MaxiBon
 *
 */
public class MaybeStringIntDecoder extends Decoder.IntDecoder {

    @Override
    /**
     * decodeInt
     */
    public int decodeInt(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            CodegenAccess.unreadByte(iter);
            return iter.readInt();
        }
        int val = iter.readInt();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringIntDecoder", "expect \", but found: " + Byte.toString(c).charAt(0));
        }
        return val;
    }
}
