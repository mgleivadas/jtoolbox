package com.brontoblocks.utils.stream;

import java.util.List;

public record Batch<T>(long batchIndex, List<T> elements) { }
