package com.brontoblocks.json;

import com.brontoblocks.utils.Try;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.brontoblocks.utils.ArgCheck.nonNull;
import static com.brontoblocks.utils.Try.ofThrowing;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * A utility class that allows a plain java application to serialise and deserialize objects. It uses Jackson library
 * under the hood. The main purpose is to provide convenient methods that streamline the usage of the aforementioned
 * library in an application while it shields from tight coupling with it.
 * This class IS THREAD SAFE provided that ALL configuration of the instance occurs before ANY read or write calls.
 */
public final class JsonEngine {

  /**
   * Creates an instance of JsonEngine using the default configuration.
   * @return A default configured JsonEngine instance.
   */
  public static JsonEngine create() {
    return new JsonEngine(
      new JsonEngineConfiguration()
        .activateAbsentAsNull()
        .enableInstantEncoding()
        .failOnUnknownProperties(false)
        .build()
    );
  }

  /**
   * Configures and creates a new instance using the specified {@code JsonEngineConfiguration}.
   *
   * <p>This method allows users to configure JsonEngine with custom configurations provided through a
   * JsonEngineConfiguration object.
   *
   * <p>Usage Example:</p>
   * <pre>
   * JsonEngineConfiguration configuration = new JsonEngineConfiguration()
   *     .failOnUnknownProperties(true)
   *     .activateAbsentAsNull();
   *
   * JsonEngine jsonEngine = JsonEngine.configure(configuration);
   * </pre>
   *
   * @param configuration the {@code JsonEngineConfiguration} object containing custom configurations
   * @return a new {@code JsonEngine} instance configured according to the provided {@code JsonEngineConfiguration}
   */
  public static JsonEngine configure(JsonEngineConfiguration configuration) {
    return new JsonEngine(configuration.build());
  }

  /**
   * Encodes a given Java object into its JSON string representation.
   *
   * <p>Example usage:</p>
   * <pre>
   * MyObject myObject = new MyObject();
   * String jsonString = jsonEngine.encode(myObject);
   * </pre>
   *
   * @param <T> the type of the object to encode
   * @param object the object to encode into a JSON string
   * @return the JSON string representation of the provided object
   * @throws RuntimeException if an error occurs during JSON serialization
   */
  public <T> String encode(T object) {
    return ofThrowing(() -> objectMapper.writeValueAsString(object)).getOrThrow();
  }

  /**
   * Creates and returns a new {@code JsonObject} instance, (which is also a {@code JsonNodeWrapper} object).
   * A JsonObject can be used to build custom JSON structures without forcing the creation for a similar class
   * hierarchy.
   **/
  public JsonObject createObject() {
    return new JsonObject(objectMapper);
  }

  /**
   * Creates and returns a new {@code JsonArray} instance, (which is also a {@code JsonNodeWrapper} object).
   * A JsonArray can be used to build custom JSON arrays without forcing the creation for a similar class
   * hierarchy.
   **/
  public JsonArray createArray() {
    return new JsonArray(objectMapper);
  }


  /**
   * Decodes a JSON string into a Java object of the specified type.
   *
   * <p>This method uses the currently configured {@code JsonEngine} instance, to convert the provided JSON string
   * into an instance of the specified Java class. Any potential mismatches or unexpected tokens will cause this method
   * to throw a RuntimeException containing the root cause internally.</p>
   *
   * <p>Example usage:</p>
   * <pre>
   * String jsonString = "{\"name\":\"John\",\"age\":30}";
   * Employee e = jsonEngine.decode(Employee, Employee.class);
   * </pre>
   *
   * @param <T> the type of the object to be returned
   * @param jsonStr the JSON string to decode
   * @param clazz the class of the object to be returned
   * @return an instance of the specified class representing the decoded JSON string
   * @throws RuntimeException if an error occurs during JSON deserialization
   */
  public <T> T decode(String jsonStr, Class<T> clazz) {
    return ofThrowing(() -> objectMapper.readValue(jsonStr, clazz)).getOrThrow();
  }

  /**
   * Decodes a JSON string into an {@code ArrayList} of Java objects of the specified type.
   * Except for the return type, it is otherwise semantically identical
   * to {@link JsonEngine#decode(String jsonStr, Class<T> clazz)}
   */
  public <T> List<T> decodeToArrayList(String jsonStr, Class<T> clazz) {
    final Try<List<T>> result = tryReadToCollectionAndCast(jsonStr, ArrayList.class, clazz);
    return result.getOrThrow();
  }

  /**
   * Decodes a JSON string into an {@code HashSet} of Java objects of the specified type.
   * Except for the return type, it is otherwise semantically identical
   * to {@link JsonEngine#decode(String jsonStr, Class<T> clazz)}
   */
  public <T> Set<T> decodeToHashSet(String jsonStr, Class<T> clazz) {
    final Try<Set<T>> result = tryReadToCollectionAndCast(jsonStr, HashSet.class, clazz);
    return result.getOrThrow();
  }

  /**
   * Decodes a JSON string into an {@code HashMap} of Java objects of the specified type.
   * Except for the return type, it is otherwise semantically identical
   * to {@link JsonEngine#decode(String jsonStr, Class<T> clazz)}
   */
  public <K, V> Map<K, V> decodeToHashMap(String jsonStr, Class<K> key, Class<V> value) {
    final Try<Map<K, V>> result = tryReadToMapAndCast(jsonStr, HashMap.class, key, value);
    return result.getOrThrow();
  }

  /**
   * Decodes a JSON string into an {@code ConcurrentHashMap} of Java objects of the specified type.
   * Except for the return type, it is otherwise semantically identical
   * to {@link JsonEngine#decode(String jsonStr, Class<T> clazz)}
   */
  public <K, V> Map<K, V> decodeToConcurrentHashMap(String jsonStr, Class<K> key, Class<V> value) {
    final Try<Map<K, V>> result = tryReadToMapAndCast(jsonStr, ConcurrentHashMap.class, key, value);
    return result.getOrThrow();
  }

  /**
   * Decodes a JSON string into an {@code LinkedHashMap} of Java objects of the specified type.
   * Except for the return type, it is otherwise semantically identical
   * to {@link JsonEngine#decode(String jsonStr, Class<T> clazz)}
   */
  public <K, V> Map<K, V> decodeToLinkedHashMap(String jsonStr, Class<K> key, Class<V> value) {
    final Try<Map<K, V>> result = tryReadToMapAndCast(jsonStr, LinkedHashMap.class, key, value);
    return result.getOrThrow();
  }

  /**
   * Parses a JSON string into a hierarchical Map<String, Object>. Nested JSON objects are also converted into
   * nested Map<String, Object> instances, allowing for a full hierarchical representation of the JSON data in
   * a map structure.
   */
  public Map<String, Object> parseJsonToMapHierarchy(String json) {
    return ofThrowing(() -> objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {})).getOrThrow();
  }

  private <T> Try<T> tryReadToCollectionAndCast(
    String jsonStr,
    Class<? extends Collection> collectionClass,
    Class<?> elementClass) {

    return ofThrowing(() -> objectMapper.readValue(jsonStr, constructCollectionType(collectionClass, elementClass)));
  }

  private CollectionType constructCollectionType(
    Class<? extends Collection> collectionClass,
    Class<?> elementClass) {

    return objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
  }

  private <T> Try<T> tryReadToMapAndCast(
    String jsonStr,
    Class<? extends Map> mapClass,
    Class<?> keyClass,
    Class<?> valueClass) {

    return ofThrowing(() -> objectMapper.readValue(jsonStr, constructMapType(mapClass, keyClass, valueClass)));
  }

  private MapType constructMapType(
    Class<? extends Map> mapClass,
    Class<?> keyClass,
    Class<?> valueClass) {

    return objectMapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
  }

  private JsonEngine(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private final ObjectMapper objectMapper;

  /**
   * This class provides a fluent builder API to allow for customization. It is meant to be a boundary from shielding
   * users of this library from its implementation details. Any customization feature should be implemented in this
   * class without revealing any classes/enums/methods from the layer below.
   *
   * <h2>Usage Example</h2>
   * <pre>
   *   new JsonEngineConfiguration()
   *     .failOnUnknownProperties(true)
   *     .activateAbsentAsNull()
   *     .build();
   * </pre>
   *
   */
  public static final class JsonEngineConfiguration {

    /**
     * This setting determines the behaviour of the deserialization process upon encountering unknown properties.
     * The default behavior is to ignore any properties for which there is no designated handling.
     * @param flag A boolean param, where true activates the exception mechanism in the cases described above and false
     *             ignores them. Default value: false
     * @return Returns {@code JsonEngineConfiguration} to continue the configuration in a builder pattern style.
     */
    public JsonEngineConfiguration failOnUnknownProperties(boolean flag) {
      jsonMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, flag);
      return this;
    }

    /**
     * This setting activates the custom behaviour when serializing/deserializing the {@code Optional<T>} type.
     * By default, java's Optional<T> type is serialized like any other object. However, this might not be the desired
     * behavior since Optionals have unique semantics.
     * Invoking this method during the configuration phase, activates a mechanism which handles Optional
     * like nullable types.
     * @return Returns {@code JsonEngineConfiguration} to continue the configuration in a builder pattern style.
     */
    public JsonEngineConfiguration activateAbsentAsNull() {
      jsonMapper.addModule(new Jdk8Module());
      return this;
    }

    /**
     * This setting enhances the serializing/deserializing capabilities of the serializer in order to be able to handle
     * Java's new time classes.
     * @return Returns {@code JsonEngineConfiguration} to continue the configuration in a builder pattern style.
     */
    public JsonEngineConfiguration enableInstantEncoding() {
      jsonMapper.addModule(new JavaTimeModule());
      return this;
    }

    /**
     * Invoking this method allows for the addition of special handling upon serializing a specific type.
     *
     * <pre>
     *  Passwords should not be serialized since its sensitive data:
     *
     *   record TestPassword(String username, String password) {}
     *
     * final var jsonEngine = JsonEngine.configure(
     *       new JsonEngineConfiguration()
     *         .addCustomSerializer(TestPassword.class, (jsonGenerator, value) -> {
     *           jsonGenerator.writeStartObject();
     *           jsonGenerator.writeObjectField("username", value.username);
     *           jsonGenerator.writeObjectField("password", "****");
     *           jsonGenerator.writeEndObject();
     *       }));
     *
     * </pre>
     * @param clazz The class for which the custom serializer must be invoked
     * @param serializer An implementation of {@code CustomSerializer<T>} interface
     * @return Returns {@code JsonEngineConfiguration} to continue the configuration in a builder pattern style.
     */
    public <T> JsonEngineConfiguration addCustomSerializer(Class<T> clazz, CustomSerializer<T> serializer) {
      customModule.addSerializer(clazz, new GenericSerializer<>(clazz, serializer));
      return this;
    }

    /**
     * Invoking this method allows for the addition of special handling upon deserializing a specific type.
     *
     * <pre>
     *  Passwords should not be serialized since its sensitive data:
     *
     *   record TestPassword(String username, String password) {}
     *
     * final var jsonEngine = JsonEngine.configure(
     *       new JsonEngineConfiguration()
     *         .addCustomDeserializer(TestPassword.class, (jsonParser, deserializationContext) -> {
     *             final var name = retrievedFromContext();
     *             final var password = lookupPasswordService(name);
     *             return new TestPassword(name, password)));
     *           });
     *
     * </pre>
     * @param clazz The class for which the custom deserializer must be invoked
     * @param deserializer An implementation of {@code CustomDeserializer<T>} interface
     * @return Returns {@code JsonEngineConfiguration} to continue the configuration in a builder pattern style.
     */
    public <T> JsonEngineConfiguration addCustomDeserializer(Class<T> clazz, CustomDeserializer<T> deserializer) {
      customModule.addDeserializer(clazz, new GenericDeSerializer<>(clazz, deserializer));
      return this;
    }

    private ObjectMapper build() {
      jsonMapper.addModule(customModule);
      return jsonMapper.build();
    }

    public JsonEngineConfiguration() {
      this.jsonMapper = JsonMapper.builder();
      this.customModule = new SimpleModule();
    }

    private final SimpleModule customModule;
    private final JsonMapper.Builder jsonMapper;
  }

  public static abstract class JsonNodeWrapper {

    public String toJson() {
      return Try.ofThrowing(() -> objectMapper().writeValueAsString(rootNode())).getOrThrow();
    }

    protected abstract ObjectMapper objectMapper();
    protected abstract JsonNode rootNode();
  }

  public static final class JsonArray extends JsonNodeWrapper {

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

    public JsonArray addShorts(List<Short> shortsList) {
      shortsList.forEach(arrayNode::add);
      return this;
    }

    public JsonArray addShorts(Set<Short> shortSet) {
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

    public JsonArray addDoubles(List<Double> doublesList) {
      doublesList.forEach(arrayNode::add);
      return this;
    }

    public JsonArray addDoubles(Set<Double> doublesSet) {
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

    public JsonArray add(JsonNodeWrapper jsonNodeWrapper) {
      arrayNode.add(jsonNodeWrapper.rootNode());
      return this;
    }

    @Override
    protected ObjectMapper objectMapper() {
      return objectMapper;
    }

    @Override
    protected JsonNode rootNode() {
      return arrayNode;
    }

    private JsonArray(ObjectMapper objectMapper) {
      this.arrayNode = objectMapper.createArrayNode();
      this.objectMapper = objectMapper;
    }

    private final ArrayNode arrayNode;
    private final ObjectMapper objectMapper;
  }

  public static final class JsonObject extends JsonNodeWrapper {

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

    public JsonObject add(String propertyName, JsonNodeWrapper jsonNodeWrapper) {
      objectNode.set(propertyName, jsonNodeWrapper.rootNode());
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
    protected ObjectMapper objectMapper() {
      return objectMapper;
    }

    @Override
    protected JsonNode rootNode() {
      return objectNode;
    }

    public JsonObject(ObjectMapper objectMapper) {
      this.objectNode = objectMapper.createObjectNode();
      this.objectMapper = objectMapper;
    }

    private final ObjectNode objectNode;
    private final ObjectMapper objectMapper;
  }

  @FunctionalInterface
  public interface CustomSerializer<T> {
    void encode(JsonGenerator jsonGenerator, T t) throws IOException;
  }

  @FunctionalInterface
  public interface CustomDeserializer<T> {
    T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException;
  }

  private static final class GenericSerializer<T> extends StdSerializer<T> {

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      customSerializer.encode(gen, value);
    }

    private GenericSerializer(Class<T> clazz, CustomSerializer<T> customSerializer) {
      super(clazz);
      this.customSerializer = customSerializer;
    }

    private final CustomSerializer<T> customSerializer;
  }

  private static final class GenericDeSerializer<T> extends StdDeserializer<T> {

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
      return customDeserializer.deserialize(jsonParser, context);
    }

    private GenericDeSerializer(Class<?> clazz, CustomDeserializer<T> customDeserializer) {
      super(clazz);
      this.customDeserializer = customDeserializer;
    }

    private final CustomDeserializer<T> customDeserializer;
  }
}
