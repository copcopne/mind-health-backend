package com.si.mindhealth.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class SlangRules {
  public static record Rule(Pattern from, String to) {}
  @Getter private final List<Rule> rules;

  public SlangRules(String resourcePath) {
    try (InputStream in = SlangRules.class.getResourceAsStream(resourcePath)) {
      ObjectMapper om = new ObjectMapper();
      JsonNode root = om.readTree(in);
      List<Rule> list = new ArrayList<>();
      for (JsonNode n : root.get("rules")) {
        Pattern p = Pattern.compile(n.get("from").asText());
        String to = n.get("to").asText();
        list.add(new Rule(p, to));
      }
      this.rules = List.copyOf(list);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load slang rules: " + resourcePath, e);
    }
  }
}
