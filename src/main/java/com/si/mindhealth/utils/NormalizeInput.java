package com.si.mindhealth.utils;

import java.text.Normalizer;

// 1) toLowerCase
// 2) Loại emoji nếu muốn (giữ lại cho negativityDetector cũng được)
// 3) Bỏ dấu (optional nhưng anh khuyên bỏ trước để rule ngắn gọn)
// 4) Áp dụng slang rules (regex)
// 5) Chuẩn hoá khoảng trắng
public final class NormalizeInput {
    private static final SlangRules SLANG = new SlangRules("/slangs.json");
    private NormalizeInput() {
    }

    // Chuẩn hóa để hiển thị/lưu DB
    public static String normalizeForStorage(String input) {
        if (input == null)
            return null;
        String n = Normalizer.normalize(input, Normalizer.Form.NFC);
        n = n.replaceAll("\\s+", " ").trim();
        return n;
    }

    // Chuẩn hóa để so khớp keyword (lowercase + bỏ dấu)
    public static String normalizeForMatch(String input) {
        if (input == null)
            return "";

        // Normalize unicode + lowercase
        String n = normalizeForStorage(input).toLowerCase();

        // Tách dấu (dùng NFD rồi bỏ các dấu combining mark)
        n = Normalizer.normalize(n, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        // Thay thế các ký tự đặc biệt không bị tách bởi NFD
        n = n.replace("đ", "d");

        // Thay thế các slang
        for (SlangRules.Rule r : SLANG.getRules()) {
            n = r.from().matcher(n).replaceAll(r.to());
        }

        // Thay thế các cụm từ mang thái độ về thành 1 token duy nhất
        // NEGATORS (phủ định/cực đoan)
        n = n.replaceAll("\\bkhong he\\b", "khong_he");
        n = n.replaceAll("\\bdau co\\b", "dau_co");
        n = n.replaceAll("\\bchut nao\\b", "chut_nao");
        n = n.replaceAll("\\bkhong the\\b", "khong_the");
        n = n.replaceAll("\\bkhong tung\\b", "khong_tung");
        n = n.replaceAll("\\bchua tung\\b", "chua_tung");
        n = n.replaceAll("\\bkhong bao gio\\b", "khong_bao_gio");
        n = n.replaceAll("\\bkhong con\\b", "khong_con");
        n = n.replaceAll("\\bkhong du\\b", "khong_du");
        n = n.replaceAll("\\bkhong gi\\b", "khong_gi");

        // INTENSIFIERS (tăng/giảm cường độ)
        n = n.replaceAll("\\bthat su\\b", "that_su");
        n = n.replaceAll("\\bcuc ky\\b", "cuc_ky");
        n = n.replaceAll("\\bvo cung\\b", "vo_cung");
        n = n.replaceAll("\\bhet suc\\b", "het_suc");
        n = n.replaceAll("\\btam tam\\b", "tam_tam");
        n = n.replaceAll("\\bbinh thuong\\b", "binh_thuong");
        n = n.replaceAll("\\bdoi chut\\b", "doi_chut");
        n = n.replaceAll("\\bmot chut\\b", "mot_chut");

        return n;
    }
}
