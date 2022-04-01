package com.brontoblocks.json.core;

import com.brontoblocks.exception.SerializationException;
import com.brontoblocks.exception.functional.ThrowingSupplier;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonNode {

    public JsonNode(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public abstract Object getTopContainerNode();

    public String toJson() {
         return wrapToSerializationException(() -> objectMapper.writeValueAsString(getTopContainerNode()));
    }

    public com.fasterxml.jackson.databind.JsonNode innerNode() {
        return (com.fasterxml.jackson.databind.JsonNode) getTopContainerNode();
    }

    private <T> T wrapToSerializationException(ThrowingSupplier<T> mapper) {
        try {
            return mapper.get();
        } catch (Throwable ex) {
            throw new SerializationException("Serialization exception", ex);
        }
    }

    private final ObjectMapper objectMapper;
}
