package com.si.mindhealth.services.nlp;

import java.util.List;
import java.util.regex.Pattern;

public final class CrisisDetector {

  // Idiom loại trừ (không phải ý định tự hại)
  private static final List<Pattern> EXCLUDES = List.of(
      Pattern.compile("\\bchet\\s+cuoi\\b"),
      Pattern.compile("\\bchet\\s+me\\b"), // idiom/chửi miệng
      Pattern.compile("\\bchet\\s+met\\b"),
      Pattern.compile("\\bchet\\s+lang\\b"),
      Pattern.compile("\\bchet\\s+chim\\b")
  );

  // Các mẫu CRISIS
  private static final List<Pattern> PATS = List.of(
      // ——— Ý định muốn chết / tự tử / kết thúc đời mình ———
      Pattern.compile("\\b(minh|toi|tao|em|anh)?\\s*muon\\s+chet(\\s+(qua|lam))?\\b"),
      Pattern.compile("\\b(minh|toi|tao|em|anh)?\\s*muon\\s+chet\\s+di\\b"),
      Pattern.compile("\\buoc\\s+gi\\s+(minh|toi|tao|em|anh)?\\s*chet\\b"),
      Pattern.compile("\\bgia\\s+nhu\\s+(minh|toi|tao|em|anh)?\\s*chet\\b"),
      Pattern.compile("\\bmuon\\s+tu\\s*tu\\b"),
      Pattern.compile("\\b(minh|toi|tao|em|anh)?\\s*muon\\s+ket\\s+thuc(\\s+(tat\\s+ca|cuoc\\s+doi|doi\\s+minh))?\\b"),
      Pattern.compile("\\bkhong\\s+muon\\s+song\\s+nua\\b"),
      Pattern.compile("\\bkhong\\s+con\\s+ly\\s+do\\s+de\\s+song\\b"),
      Pattern.compile("\\bsong\\s+(de\\s+)?lam\\s+gi(\\s+nua)?\\b"),
      Pattern.compile("\\bmuon\\s+bien\\s+mat(\\s+vinh\\s+vien)?\\b"),
      Pattern.compile("\\bmuon\\s+ngu\\s+(mot\\s+)?giac\\s+khong\\s+day\\b"),
      Pattern.compile("\\bchet\\s+cho\\s+xong\\b"),
      Pattern.compile("\\b(minh|toi|tao|em|anh)\\s+chet\\s+di\\b"),

      // ——— Rời xa khỏi thế giới / biến mất ———
      Pattern.compile("\\broi\\s+(xa|khoi)\\s+the\\s+gioi(\\s+nay)?\\b"),
      Pattern.compile("\\bbien\\s+mat\\s+(khoi\\s+)?the\\s+gioi(\\s+nay)?\\b"),

      // ——— Câu hỏi về sự ra đi & ai nhớ đến mình ———
      Pattern.compile("\\bneu\\s+(minh|toi)\\s+(roi(\\s+xa)?|bien\\s+mat)\\s+(khoi\\s+)?the\\s+gioi(\\s+nay)?\\b"),
      Pattern.compile("\\bai\\s+se\\s+nh\\w*\\s+den\\s+(minh|toi)\\b")
  );

  // Phủ định bảo vệ: "khong muon chet/tu tu/ket thuc..." → không tính crisis
  private static boolean protectedNegationLocal(String norm) {
    return norm.matches(".*\\bkhong\\s+muon\\s+(chet|tu\\s*tu|ket\\s+thuc(\\s+(tat\\s+ca|doi\\s+minh))?)\\b.*")
        || norm.matches(".*\\bkhong\\s+phai\\s+muon\\s+chet\\b.*");
  }

  public static boolean hasCrisisSignal(String norm) {
    if (norm == null || norm.isBlank()) return false;

    try {
      if (Sentiment.isProtectedNegation(norm)) return false;
    } catch (Throwable ignore) {}

    if (protectedNegationLocal(norm)) return false;

    // Loại trừ idiom phổ biến
    for (Pattern ex : EXCLUDES) {
      if (ex.matcher(norm).find()) return false;
    }

    // Match bất kỳ mẫu crisis nào
    for (Pattern p : PATS) {
      if (p.matcher(norm).find()) return true;
    }
    return false;
  }

  private CrisisDetector() {}
}
