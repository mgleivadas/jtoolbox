package com.brontoblocks.web;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class WebResponseHandler<R> {

  public static <R> WebResponseHandler<R> create() {
    return new WebResponseHandler<>();
  }

  @FunctionalInterface
  public interface WebResponseMapper<R> {
    R map(ParsedWebResponse parsedWebResponse);
  }

  public WebResponseHandler<R> if200(Function<ParsedWebResponse, R> mapper) {
    webResponseMappers.put(200, mapper);
    return this;
  }

  public WebResponseHandler<R> if500(Function<ParsedWebResponse, R> mapper) {
    webResponseMappers.put(500, mapper);
    return this;
  }

  public R execute(WebRequest webRequest) {
    return handleRequest(webRequest.get().getOrThrow());
  }

  protected void executeNoResponse(WebRequest webRequest) {
    handleRequest(webRequest.get().getOrThrow());
  }

  protected R handleRequest(ParsedWebResponse pwr) {

    return ofNullable(webResponseMappers.get(pwr.statusCode()))
      .map(mapper -> mapper.apply(pwr))
      .orElseThrow(() -> new RuntimeException("Missing response mapping function for %d"
        .formatted(pwr.statusCode())));
  }

  protected WebResponseHandler() {
    this.webResponseMappers = new HashMap<>();
  }

  protected final Map<Integer, Function<ParsedWebResponse, R>> webResponseMappers;
}
