package com.brontoblocks.json.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.util.Optional;

import static com.brontoblocks.utils.ArgCheck.nonNull;

public final class JsonObject extends JsonNode {

    public static JsonObject with(ObjectMapper objectMapper) {
        return new JsonObject(objectMapper);
    }

    private JsonObject(ObjectMapper objectMapper) {
        super(objectMapper);
        objectNode = objectMapper.createObjectNode();
    }

    public JsonObject add(String propertyName, int intValue) {
        objectNode.set(propertyName, new IntNode(intValue));
        return this;
    }

    public JsonObject add(String propertyName, long longValue) {
        objectNode.set(propertyName, new LongNode(longValue));
        return this;
    }

    public JsonObject add(String propertyName, boolean booleanValue) {
        objectNode.set(propertyName, BooleanNode.valueOf(booleanValue));
        return this;
    }

    public JsonObject add(String propertyName, String stringValue) {
        objectNode.set(propertyName, TextNode.valueOf(stringValue));
        return this;
    }

    public JsonObject add(String propertyName, JsonNode jsonNode) {
        objectNode.set(propertyName, jsonNode.innerNode());
        return this;
    }

    public JsonObject add(String propertyName, Object objValue) {
        objectNode.set(propertyName, new POJONode(nonNull("serializing object", objValue)));
        return this;
    }

    public <T> JsonObject add(String propertyName, Optional<T> optValue) {
        objectNode.set(propertyName, optValue.isPresent() ? new POJONode(optValue.get()) : NullNode.getInstance());
        return this;
    }

    @Override
    public Object getTopContainerNode() {
        return objectNode;
    }

    private final ObjectNode objectNode;
}
