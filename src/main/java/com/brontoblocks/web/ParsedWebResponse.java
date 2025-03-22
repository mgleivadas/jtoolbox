package com.brontoblocks.web;

import java.net.http.HttpResponse;
import java.util.Optional;

public class ParsedWebResponse {

  public int statusCode() {
    return response.statusCode();
  }

  public String body() {
    return response.body();
  }

  public Optional<String> getHeader(String key) {
    return response.headers().firstValue(key);
  }

  public ParsedWebResponse(HttpResponse<String> response) {
    this.response = response;
  }

  private final HttpResponse<String> response;
}
