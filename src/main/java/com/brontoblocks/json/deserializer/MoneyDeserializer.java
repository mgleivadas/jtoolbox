package com.brontoblocks.json.deserializer;

import com.brontoblocks.common.Money;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Currency;

public final class MoneyDeserializer {

    public static Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        return Money.of(BigInteger.valueOf(node.get("amount").asLong()),
            Currency.getInstance(node.get("currency").asText()));
    }
}