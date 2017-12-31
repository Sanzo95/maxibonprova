package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

/**
 * Public Class StringShortDecoder.
 * 
 * @author MaxiBon
 *
 */
public class StringShortDecoder extends Decoder.ShortDecoder {

    @Override
    /**
     * decodeShort
     */
    public short decodeShort(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringShortDecoder", "expect \", but found: " + Byte.toString(c).charAt(0));
        }
        short val = iter.readShort();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringShortDecoder", "expect \", but found: " + Byte.toString(c).charAt(0));
        }
        return val;
    }
}
