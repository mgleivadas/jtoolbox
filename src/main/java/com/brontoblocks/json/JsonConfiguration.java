package com.brontoblocks.json;

import com.brontoblocks.common.Money;
import com.brontoblocks.exception.functional.ThrowingBiConsumer;
import com.brontoblocks.exception.functional.ThrowingBiFunction;
import com.brontoblocks.json.deserializer.MoneyDeserializer;
import com.brontoblocks.utils.Tuples;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.brontoblocks.utils.Tuples.t2;

public class JsonConfiguration {
    public final static ObjectMapper OBJECT_MAPPER = defaultConfiguration();

    private JsonConfiguration() {
    }

    /**
     * The following classes make use of some unsafe conversion between type parameters. They should not be exposed
     * outside their package as direct instantiation can be pose issues.
     *
     * However, if they are used with deserializerFor() & serializerFor() methods above then they are safe since the
     * type checking is being taken care be them.
     */
    @SuppressWarnings("unchecked")
    private static final class GenericSerializer<T> extends StdSerializer<T> {

        static <T> GenericSerializer<T> getNew(Class<?> clazz, ThrowingBiConsumer<JsonGenerator, ?> b) {
            return new GenericSerializer(clazz, b);
        }

        GenericSerializer(Class<T> t, ThrowingBiConsumer<JsonGenerator, T> b) {
            super(t);
            serializationFunction = b;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider provider) {
            try {
                serializationFunction.accept(gen, value);
            } catch (Exception e) {
                throw new RuntimeException("Error while serialization", e);
            }
        }

        private final ThrowingBiConsumer<JsonGenerator, T> serializationFunction;
    }


    @SuppressWarnings("unchecked")
    private static final class GenericDeserializer<T> extends JsonDeserializer<T> {

        static <T> GenericDeserializer<T> getNew(Class<?> clazz,
                                                             ThrowingBiFunction<JsonParser, DeserializationContext, ?> d) {
            return new GenericDeserializer(clazz, d);
        }

        GenericDeserializer(Class<T> t, ThrowingBiFunction<JsonParser, DeserializationContext, T> d) {
            deserializationFunction = d;
        }

        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            try {
                return deserializationFunction.apply(jp, ctxt);
            } catch (Exception e) {
                throw new RuntimeException("Error while deserialization", e);
            }
        }

        private final ThrowingBiFunction<JsonParser, DeserializationContext, T> deserializationFunction;
    }

    private static ObjectMapper defaultConfiguration() {

        List<Tuples.T2<Class<?>, ThrowingBiConsumer<JsonGenerator, ?>>> serializers = new ArrayList<>();
        List<Tuples.T2<Class<?>, ThrowingBiFunction<JsonParser, DeserializationContext, ?>>> deserializers = new ArrayList<>();

        serializers.add(t2(Instant.class, (gen, value) -> gen.writeString(value.toString())));
        deserializers.add(t2(Money.class, MoneyDeserializer::deserialize));

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        serializers.forEach(t2 -> module.addSerializer(t2.getFirst(),
                GenericSerializer.getNew(t2.getFirst(), t2.getSecond())));

        deserializers.forEach(t2 ->
                module.addDeserializer(t2.getFirst(),
                        GenericDeserializer.getNew(t2.getFirst(), t2.getSecond())));

        objectMapper.registerModule(module);

        return objectMapper;
    }
}
