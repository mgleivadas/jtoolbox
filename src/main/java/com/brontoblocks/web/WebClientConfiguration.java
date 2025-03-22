package com.brontoblocks.web;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.TRUE;
import static java.lang.Integer.MAX_VALUE;
import static java.net.http.HttpClient.Redirect.NEVER;
import static java.net.http.HttpClient.Version.HTTP_1_1;

public class WebClientConfiguration {

  public static WebClientConfiguration create() {

    return new WebClientConfiguration(prepareClientWithDefaultConfiguration().build());
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  private WebClientConfiguration(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  private HttpClient httpClient;

  /**
   *   Default http client configuration. In most scenarios, the following values SHOULD NOT be changed.
   *   Make sure you fully understand how each parameter is used before attempting changing any of the following.
   *   If your application has special needs it is better to use WebClientConfigurator class to override the default
   *   values, rather than changing any of the settings below, which would have a knock on effect in other applications
   *   using this library.
   */
  private static HttpClient.Builder prepareClientWithDefaultConfiguration() {

    final var executorService = new ThreadPoolExecutor(
      DEFAULT_CORE_POOL_SIZE,
      DEFAULT_MAX_CORE_POOL_SIZE,
      THREAD_IDLE_TIMEOUT_IN_SECONDS,
      TimeUnit.SECONDS,
      new SynchronousQueue<>(TRUE));

    return HttpClient.newBuilder()
      .version(DEFAULT_HTTP_CONNECTION_VERSION)
      .executor(executorService)
      .followRedirects(NEVER)
      .connectTimeout(DEFAULT_CONNECTION_ESTABLISHMENT_TIMEOUT);
  }

  // Http connection related config
  private static final HttpClient.Version DEFAULT_HTTP_CONNECTION_VERSION = HTTP_1_1;
  private static final Duration DEFAULT_CONNECTION_ESTABLISHMENT_TIMEOUT = Duration.of(10, ChronoUnit.SECONDS);

  // Http client thread pool config
  private static final int DEFAULT_CORE_POOL_SIZE = 2;
  private static final int DEFAULT_MAX_CORE_POOL_SIZE = MAX_VALUE;
  private static final int THREAD_IDLE_TIMEOUT_IN_SECONDS = 30;
}
