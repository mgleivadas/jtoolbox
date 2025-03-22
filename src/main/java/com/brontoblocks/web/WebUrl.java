package com.brontoblocks.web;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class WebUrl {

  public static WebUrl https(String url) {
    return new WebUrl().setScheme("https").host(url);
  }

  public static WebUrl http(String url) {
    return new WebUrl().setScheme("http").host(url);
  }

  public static WebUrl https(String url, int port) {
    return new WebUrl().setScheme("https").host(url).port(port);
  }

  public static WebUrl http(String url, int port) {
    return new WebUrl().setScheme("http").host(url).port(port);
  }

  public WebUrl addQueryParam(String name, String value) {
    uriBuilder.addParameter(name, value);
    return this;
  }

  public WebUrl addQueryParams(String name, List<String> values) {
    uriBuilder.addParameters(
      values.stream()
        .map(value -> (NameValuePair)new BasicNameValuePair(name, value))
        .toList());

    return this;
  }

  public WebUrl addPath(String name) {
    uriBuilder.appendPath(name);
    return this;
  }

  public WebUrl host(String host) {
    uriBuilder.setHost(host);
    return this;
  }

  public WebUrl port(int port) {
    uriBuilder.setPort(port);
    return this;
  }

  public URL toUrl() {
    try {
      return toUri().toURL();
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

  URI toUri() {
    try {
      return uriBuilder.build();
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String toString() {
    return toUrl().toString();
  }

  private WebUrl setScheme(String scheme) {
    uriBuilder.setScheme(scheme);
    return this;
  }

  private WebUrl() {
    this.uriBuilder = new URIBuilder();
  }

  private final URIBuilder uriBuilder;
}
