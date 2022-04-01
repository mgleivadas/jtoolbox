package com.brontoblocks.json;

import com.brontoblocks.exception.DeserializationException;
import com.brontoblocks.exception.functional.ThrowingSupplier;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.brontoblocks.json.JsonConfiguration.OBJECT_MAPPER;

public final class JsonDecoder {

    public static <T> T to(String jsonStr, Class<T> clazz) {
        return wrapToDeserializationException(() -> OBJECT_MAPPER.readValue(jsonStr, clazz));
    }

    public static <T> List<T> toList(String jsonStr, Class<T> v) {
        return wrapToDeserializationException(() -> {
            CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, v);
            return OBJECT_MAPPER.readValue(jsonStr, collectionType);
        });
    }

    public static <T> Set<T> toSet(String jsonStr, Class<T> v) {
        return wrapToDeserializationException(() -> {
            CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(HashSet.class, v);
            return OBJECT_MAPPER.readValue(jsonStr, collectionType);
        });
    }

    public static <KEY, VALUE> Map<KEY, VALUE> toMap(String jsonStr, Class<KEY> k, Class<VALUE> v) {
        return wrapToDeserializationException(() -> {
            MapType collectionType = OBJECT_MAPPER.getTypeFactory().constructMapType(HashMap.class, k , v);
            return OBJECT_MAPPER.readValue(jsonStr, collectionType);
        });
    }

    private static <T> T wrapToDeserializationException(ThrowingSupplier<T> mapper) {
        try {
            return mapper.get();
        } catch (Throwable ex) {
            throw new DeserializationException("Deserialization exception", ex);
        }
    }
}
