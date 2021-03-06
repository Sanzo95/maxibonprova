package com.jsoniter.fuzzy;

import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import com.jsoniter.spi.Decoder;

import java.io.IOException;

/**
 * Public Class MaybeEmptyArrayDecoder.
 * 
 * @author MaxiBon
 *
 */
public class MaybeEmptyArrayDecoder implements Decoder {

    @Override
    /**
     * decode
     * @throws IOException
     */
    public JsonIterator decode(JsonIterator iter) throws IOException {
        if (iter.whatIsNext() == ValueType.ARRAY) {
            if (iter.readArray()) {
                throw iter.reportError("MaybeEmptyArrayDecoder", "this field is object. if input is array, it can only be empty");
            } else {
                // empty array parsed as null
                return null;
            }
        } else {
            return iter.read(iter);
        }
    }
}
