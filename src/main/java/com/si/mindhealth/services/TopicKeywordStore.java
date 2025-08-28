package com.si.mindhealth.services;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.si.mindhealth.entities.enums.SupportTopic;

@Service
public class TopicKeywordStore {
  private final Map<SupportTopic, List<String>> keywords = new EnumMap<>(SupportTopic.class);

  public TopicKeywordStore() throws IOException {
    try (var is = getClass().getResourceAsStream("/keywords.json")) {
      var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
      var raw = mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<Map<String, List<String>>>() {});
      raw.forEach((k, v) -> keywords.put(SupportTopic.valueOf(k), v));
    }
  }

  public Map<SupportTopic, List<String>> all(){ return keywords; }
}

