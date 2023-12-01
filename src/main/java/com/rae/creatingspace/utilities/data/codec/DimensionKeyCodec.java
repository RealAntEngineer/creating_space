package com.rae.creatingspace.utilities.data.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class DimensionKeyCodec implements Codec {
    //for making a cleaner implementation ( the translator methode might not be needed)
    @Override
    public DataResult<Pair> decode(DynamicOps ops, Object input) {
        return null;
    }

    @Override
    public DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return null;
    }
}
