package com.brontoblocks.web;

import com.brontoblocks.utils.Try;

public class WebRequest {

  WebRequest(Try<ParsedWebResponse> webResponseSupplier) {
    this.parsedWebResponseSupplier = webResponseSupplier;
  }

  protected Try<ParsedWebResponse> get() {
    return parsedWebResponseSupplier;
  }

  private final Try<ParsedWebResponse> parsedWebResponseSupplier;
}
