package com.jsoniter.fuzzy;

import com.jsoniter.CodegenAccess;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

public class StringFloatDecoder extends Decoder.FloatDecoder {

    @Override
    public float decodeFloat(JsonIterator iter) throws IOException {
        byte c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringFloatDecoder", "expect \", but found: " + (char) c);
        }
        float val = iter.readFloat();
        c = CodegenAccess.nextToken(iter);
        if (c != '"') {
            throw iter.reportError("StringFloatDecoder", "expect \", but found: " + (char) c);
        }
        return val;
    }
}
