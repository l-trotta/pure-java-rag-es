package co.elastic;

import java.util.List;
import java.util.Map;

public record VectorData(String text, List<Float> vector, Map<String, Object> metadata) {}
