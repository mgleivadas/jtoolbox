package com.brontoblocks.json.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;
import java.util.Set;

public class JsonArray extends JsonNode {

    public static JsonArray with(ObjectMapper objectMapper) {
        return new JsonArray(objectMapper);
    }

    private JsonArray(ObjectMapper objectMapper) {
        super(objectMapper);
        arrayNode = objectMapper.createArrayNode();
    }

    public JsonArray addInts(List<Integer> intList) {
        intList.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addInts(Set<Integer> integerSet) {
        integerSet.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addLongs(List<Long> longList) {
        longList.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addLongs(Set<Long> longSet) {
        longSet.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addShorts(List<Long> shortsList) {
        shortsList.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addShorts(Set<Long> shortSet) {
        shortSet.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addBooleans(List<Boolean> booleanList) {
        booleanList.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addBooleans(Set<Boolean> booleanSet) {
        booleanSet.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addDoubles(List<Boolean> doublesList) {
        doublesList.forEach(arrayNode::add);
        return this;
    }

    public JsonArray addDoubles(Set<Boolean> doublesSet) {
        doublesSet.forEach(arrayNode::add);
        return this;
    }

    public JsonArray add(int intValue) {
        arrayNode.add(intValue);
        return this;
    }

    public JsonArray add(long longValue) {
        arrayNode.add(longValue);
        return this;
    }

    public JsonArray add(double doubleValue) {
        arrayNode.add(doubleValue);
        return this;
    }

    public JsonArray add(short shortValue) {
        arrayNode.add(shortValue);
        return this;
    }

    public JsonArray add(boolean booleanValue) {
        arrayNode.add(booleanValue);
        return this;
    }

    public JsonArray add(String stringValue) {
        arrayNode.add(stringValue);
        return this;
    }

    public JsonArray add(JsonNode jsonNode) {
        arrayNode.add(jsonNode.innerNode());
        return this;
    }

    @Override
    public Object getTopContainerNode() {
        return arrayNode;
    }

    private final ArrayNode arrayNode;
}
