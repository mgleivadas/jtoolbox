package com.brontoblocks.json;

import com.brontoblocks.json.core.JsonArray;
import com.brontoblocks.json.core.JsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.brontoblocks.json.JsonConfiguration.OBJECT_MAPPER;

public final class JsonEncoder {

    public static JsonObject newObject() {
        return JsonObject.with(OBJECT_MAPPER);
    }

    public static JsonArray newArray() {
        return JsonArray.with(OBJECT_MAPPER);
    }

    public static <T> String serialize(T object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
