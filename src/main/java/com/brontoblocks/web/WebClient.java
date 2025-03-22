package com.brontoblocks.web;

import com.brontoblocks.utils.Try;

import java.net.http.HttpClient;
import java.util.List;

import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpRequest.Builder;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.util.Collections.emptyList;

public class WebClient {

  public static WebClient create() {
    return new WebClient(WebClientConfiguration.create());
  }

  public static WebClient create(WebClientConfiguration configuration) {
    return new WebClient(configuration);
  }

  public WebRequest get(WebUrl webUrl) {
    return get(webUrl, emptyList());
  }

  public WebRequest get(WebUrl webUrl, List<HttpHeader> headers) {
      return toWebRequest(newRequest(webUrl, headers).GET());
  }

  public WebRequest post(WebUrl webUrl, List<HttpHeader> headers) {
    return toWebRequest(newRequest(webUrl, headers).POST(noBody()));
  }

  public WebRequest post(WebUrl webUrl, List<HttpHeader> headers, String body) {
    return toWebRequest(newRequest(webUrl, headers).POST(BodyPublishers.ofString(body)));
  }

  public WebRequest put(WebUrl webUrl, List<HttpHeader> headers) {
    return toWebRequest(newRequest(webUrl, headers).PUT(noBody()));
  }

  public WebRequest put(WebUrl webUrl, List<HttpHeader> headers, String body) {
    return toWebRequest(newRequest(webUrl, headers).PUT(BodyPublishers.ofString(body)));
  }

  public WebRequest delete(WebUrl webUrl, List<HttpHeader> headers) {
    return toWebRequest(newRequest(webUrl, headers).DELETE());
  }

  public WebRequest delete(WebUrl webUrl, List<HttpHeader> headers, String body) {
    return toWebRequest(newRequest(webUrl, headers).PUT(BodyPublishers.ofString(body)));
  }

  private WebRequest toWebRequest(Builder httpRequestBuilder) {
    return new WebRequest(
      Try.ofThrowing(() ->
        new ParsedWebResponse(httpClient.send(httpRequestBuilder.build(), ofString()))));
  }

  private Builder newRequest(WebUrl webUrl, List<HttpHeader> headers) {
    final var builder = newBuilder();
    builder.uri(webUrl.toUri());
    headers.forEach(h -> builder.setHeader(h.name, h.value));
    return builder;
  }

  private WebClient(WebClientConfiguration config) {
    this.httpClient = config.getHttpClient();
  }

  private final HttpClient httpClient;

  public record HttpHeader(String name, String value) {}
}
