package com.si.mindhealth.services.nlp;

import java.util.*;

/** Sentiment: Lexicon + Negation + Intensifier */
public final class Sentiment {

    public record Result(double neg, double pos, double compound, double negRatio) {
    }

    // ====== TỪ ĐIỂN CẢM XÚC ======
    private static final Map<String, Double> LEXI = new HashMap<>();
    static {
        // ===== Negative (tự ti / lo âu / áp lực) =====
        put("tu ti", -2.6);
        put("yeu kem", -2.4);
        put("khong dam", -1.8);
        put("so", -1.3);
        put("so that bai", -2.5);
        put("that bai", -2.3);
        put("ap luc", -2.0);
        put("ap luc tieu cuc", -3.0);
        put("tieng khoc", -1.8); // nếu có
        put("khoc", -1.6);
        put("tu choi ban than", -2.7);
        put("vo dung", -2.7);
        put("khong co nang luc", -2.6);
        put("khong tin", -1.4);
        put("ganh ti", -1.6); // ghen tị
        put("ghen", -1.6);
        put("keo dai", -0.8); // tùy ngữ cảnh
        put("lo lang", -1.7);
        put("hoang mang", -1.9);
        put("met moi", -1.8);
        put("tham vong qua", -0.6); // nhẹ (phán xét)
        put("chi la hat cat", -1.5);
        put("khong co tai", -2.2);
        put("khong co gi", -1.3);
        put("khong ung ho", -2.0);
        put("hat hui", -2.5);
        put("khong biet", -1.0);
        put("giao nhieu", -0.6);
        put("chua lam gi", -0.8);
        put("chua lam gi het", -0.9);

        // ===== CRISIS / ideation =====
        put("roi xa the gioi", -4.5);
        put("roi khoi the gioi", -4.5);
        put("bien mat khoi the gioi", -4.8);
        put("khong muon song nua", -5.0);
        put("muon ket thuc tat ca", -4.8);
        put("muon ket thuc doi minh", -5.0);
        put("muon bien mat", -4.5);
        put("muon ngu mot giac khong day", -4.5);
        put("tu tu", -5.0);
        put("muon tu tu", -5.0);
        put("ket lieu", -4.8);
        put("khong con ly do de song", -4.6);

        // ===== Positive / phục hồi =====
        put("kien thuc", 0.8);
        put("muon hoc", 0.9);
        put("dong y", 0.6);
        put("co gang", 1.2);
        put("tien bo", 1.4);
        put("gia tri", 1.6);
        put("tinh vao ban than", 1.8);
        put("tu tin", 2.2);
        put("hanh phuc", 2.2);
        put("tu do", 1.6);
        put("song vui", 1.8);
        put("co gang", 0.5);
        put("nghi ngoi", 0.8);
        put("thu gian", 1.0);
        put("di choi", 0.6);
        put("xa stress", 1.0);
        put("cham soc ban than", 1.2); // self-care
        put("nghi ngoi", 0.8);
        put("thu gian", 1.0);
    }

    private static void put(String k, double v) {
        LEXI.put(k, v);
    }

    // Phủ định & tăng/giảm cường độ
    private static final Set<String> NEGATORS = Set.of(
            // đơn từ
            "khong", "chang", "cha", "chua", "deo", "dau",
            // cụm đã được replace thành 1 token
            "khong_he", "dau_co", "chut_nao",
            "khong_the", "khong_tung", "chua_tung",
            "khong_bao_gio", "khong_con", "khong_du", "khong_gi");
    private static final Map<String, Double> INTENSIFIERS = Map.ofEntries(
            // ---- BOOSTERS ( > 1.0 ) ----
            Map.entry("rat", 1.3),
            Map.entry("qua", 1.2),
            Map.entry("sieu", 1.6),
            Map.entry("cuc", 1.4),
            Map.entry("cuc_ky", 1.5),
            Map.entry("vo_cung", 1.5),
            Map.entry("het_suc", 1.4),
            Map.entry("that_su", 1.2),
            Map.entry("kha", 1.1),
            Map.entry("muon", 1.1), // boost nhẹ cho ý hướng tích cực kiểu "muon nghi ngoi/di choi"

            // ---- ATTENUATORS ( < 1.0 ) ----
            Map.entry("hoi", 0.6), // giảm mạnh
            Map.entry("tam_tam", 0.8),
            Map.entry("binh_thuong", 0.8),
            Map.entry("co_ve", 0.9),
            Map.entry("doi_chut", 0.85),
            Map.entry("mot_chut", 0.85));

    /**
     * Phân tích, trả về: neg, pos, compound (pos-neg), negRatio = neg/(pos+neg).
     */
    public static Result analyze(String norm) {

        // Token word-level + giữ mảng words để xét phủ định/nhấn mạnh theo cửa sổ
        String[] words = splitWords(norm);
        if (words.length == 0)
            return new Result(0, 0, 0, 0);

        double pos = 0, neg = 0;

        for (int i = 0; i < words.length; i++) {
            double base = 0;

            // Ưu tiên bigram (w_i + w_{i+1}), sau đó mới unigram
            if (i + 1 < words.length) {
                String bi = words[i] + " " + words[i + 1];
                Double sc2 = LEXI.get(bi);
                if (sc2 != null)
                    base = sc2;
            }
            if (base == 0) {
                Double sc1 = LEXI.get(words[i]);
                if (sc1 != null)
                    base = sc1;
            }
            if (base == 0)
                continue;

            // Phủ định: nhìn 3 từ bên trái
            boolean negated = hasNegatorAround(words, i, 3);
            if (negated)
                base *= -1;

            // Intensifier: nhìn 2 từ xung quanh (trái/phải)
            double factor = 1.0;
            for (int j = Math.max(0, i - 2); j <= Math.min(words.length - 1, i + 2); j++) {
                if (j == i)
                    continue;
                String w = words[j];
                // hỗ trợ “cuc_ky”, “that_su” (đã join underscore trong normalize)
                Double f = INTENSIFIERS.get(w);
                if (f != null)
                    factor *= f;
            }
            base *= factor;

            if (base > 0)
                pos += base;
            else
                neg += -base;
        }

        double compound = pos - neg;
        double total = pos + neg;
        double negRatio = (total == 0) ? 0.0 : (neg / total);
        return new Result(neg, pos, compound, clamp01(negRatio));
    }

    static boolean isProtectedNegation(String norm) {
        // khong muon chet / khong muon ket thuc / khong muon tu tu
        return norm.matches(".*\\bkhong\\s+muon\\s+(chet|ket thuc|tu tu)\\b.*");
    }

    private static String[] splitWords(String norm) {
        if (norm.isBlank())
            return new String[0];
        return norm.split(" ");
    }

    private static boolean hasNegatorAround(String[] words, int idx, int leftWindow) {
        for (int j = Math.max(0, idx - leftWindow); j < idx; j++) {
            if (NEGATORS.contains(words[j]))
                return true;
        }
        return false;
    }

    private static double clamp01(double x) {
        return x < 0 ? 0 : (x > 1 ? 1 : x);
    }
}
