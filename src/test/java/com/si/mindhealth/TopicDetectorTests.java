// package com.si.mindhealth;
// import static org.junit.jupiter.api.Assertions.*;

// import java.util.*;
// import java.util.stream.Stream;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Order;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.junit.jupiter.params.ParameterizedTest;
// import org.junit.jupiter.params.provider.MethodSource;

// import com.si.mindhealth.dtos.TopicMultiResult;
// import com.si.mindhealth.dtos.TopicScore;
// import com.si.mindhealth.entities.enums.SupportTopic;
// import com.si.mindhealth.services.TopicDetector;
// import com.si.mindhealth.services.TopicKeywordStore;
// import com.si.mindhealth.utils.NormalizeInput;

// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class TopicDetectorTests {

//     TopicDetector detector;

//     /** Stub tối thiểu cho từ khóa mỗi topic */
//     static class StubStore extends TopicKeywordStore {
//         private final Map<SupportTopic, List<String>> map = new EnumMap<>(SupportTopic.class);
//         StubStore() {
//             map.put(SupportTopic.STUDY, List.of(
//                 "lam bai", "lam duoc", "giai bai", "bai tap", "on thi", "thi", "kiem tra", "diem"
//             ));
//             map.put(SupportTopic.WORK, List.of(
//                 "cong viec", "deadline", "hop", "kpi", "sep", "ot"
//             ));
//             map.put(SupportTopic.FAMILY, List.of(
//                 "gia dinh", "bo me", "me", "ba", "cha"
//             ));
//             map.put(SupportTopic.MENTAL_HEALTH, List.of(
//                 "ap luc", "lo au", "met moi", "hoang mang", "kiet suc", "stress"
//             ));
//             map.put(SupportTopic.LONELINESS, List.of("co don", "don doc"));
//             map.put(SupportTopic.MYSELF, List.of(
//                 "tu ti", "tu tin", "vo dung", "yeu kem", "gia tri ban than"
//             ));
//             map.put(SupportTopic.HEALTH, List.of("om", "dau bung", "so mui", "cam cum"));
//             map.put(SupportTopic.MONEY, List.of("tien", "hoc phi", "thieu tien", "dong tien"));
//             map.put(SupportTopic.LOVE, List.of("nguoi yeu", "crush", "chia tay"));
//             map.put(SupportTopic.LIFE, List.of("hanh phuc", "tu do", "song vui"));
//             map.put(SupportTopic.SOCIAL, List.of("ban be", "ban lop", "dong nghiep"));

//             // Các topic khác nếu có
//             map.putIfAbsent(SupportTopic.GENERAL, List.of());
//             map.putIfAbsent(SupportTopic.MOTIVATION, List.of("co gang", "no luc", "kien tri"));
//             map.putIfAbsent(SupportTopic.DECISION, List.of("quyet dinh", "chon lua"));
//             map.putIfAbsent(SupportTopic.FUTURE, List.of("tuong lai", "dinh huong"));
//             map.putIfAbsent(SupportTopic.TRAUMA, List.of("chan thuong", "ac mong"));
//         }
//         @Override
//         public Map<SupportTopic, List<String>> all() { return map; }
//     }

//     @BeforeEach
//     void init() {
//         detector = new TopicDetector(new StubStore());
//     }

//     static record Case(String input, SupportTopic expectedPrimary) {}

//     static Stream<Case> cases() {
//         return Stream.of(
//             new Case("Bài hôm nay hơi khó nhưng mình đã làm được", SupportTopic.STUDY),
//             new Case("Tối nay làm bài xong mới ngủ, mai kiểm tra", SupportTopic.STUDY),
//             new Case("Deadline dí sát, họp với sếp cả chiều", SupportTopic.WORK),
//             new Case("Áp lực gia đình quá, bố mẹ không ủng hộ", SupportTopic.FAMILY),
//             new Case("Dạo này áp lực, lo âu, thấy kiệt sức", SupportTopic.MENTAL_HEALTH),
//             new Case("Cảm giác cô đơn, bạn bè xa cách", SupportTopic.LONELINESS),
//             new Case("Mình hơi tự ti nhưng vẫn cố gắng", SupportTopic.MYSELF),
//             new Case("Hôm nay ốm, hơi đau bụng", SupportTopic.HEALTH),
//             new Case("Lo học phí, thiếu tiền đóng", SupportTopic.MONEY),
//             new Case("Cãi nhau với người yêu, buồn quá", SupportTopic.LOVE),
//             new Case("Muốn sống hạnh phúc, tự do hơn", SupportTopic.LIFE),
//             new Case("Chỉ nói về bản thân thôi", SupportTopic.GENERAL),
//             new Case("Hôm nay hơi mệt mỏi, nhưng muốn nghỉ ngơi", SupportTopic.MENTAL_HEALTH),
//             new Case("Không ghét môn này nữa", SupportTopic.GENERAL),
//             new Case("Bài nay lam duoc nhe", SupportTopic.STUDY)
//         );
//     }

//     @Order(1)
//     @DisplayName("DetectMulti: primary topic đúng, không crash khi không có topic")
//     @ParameterizedTest(name = "{index} -> \"{0}\" ⇒ {1}")
//     @MethodSource("cases")
//     void testDetectMulti(Case c) {
//         TopicMultiResult res = detector.detectMulti(c.input);

//         assertNotNull(res, "Result null?");
//         assertNotNull(res.primary(), "Primary null?");
//         // nếu không match topic nào qua ngưỡng, expect GENERAL
//         if (c.expectedPrimary == SupportTopic.GENERAL) {
//             assertEquals(SupportTopic.GENERAL.name(), res.primary());
//         } else {
//             assertEquals(c.expectedPrimary.name(), res.primary(), "Sai primary");
//         }

//         // Không NPE khi không có primary score
//         assertTrue(res.primaryScore() >= 0, "primaryScore phải >= 0 (dùng 0 khi không có)");

//         // Secondary không chứa primary
//         res.topics().forEach(ts -> assertNotEquals(res.primary(), ts.topic().name(), "Secondary không được trùng primary"));

//         // negRatio nằm trong [0..1]
//         assertTrue(res.negRatio() >= 0.0 && res.negRatio() <= 1.0, "negRatio phải [0..1]");
//     }

//     // Optional: test riêng bigram giữ nguyên khi có stopwords như “lam”
//     @Order(2)
//     @DisplayName("Bigram: không mất 'lam' do tách 2 stopword")
//     @ParameterizedTest
//     @MethodSource("bigramCases")
//     void testBigramAnchors(Case c) {
//         TopicMultiResult res = detector.detectMulti(c.input);
//         assertEquals(SupportTopic.STUDY.name(), res.primary(), "Bigram study phải thắng");
//         assertTrue(res.primaryScore() >= 2, "BIGRAM_BONUS=2 ⇒ phải qua ngưỡng");
//     }

//     static Stream<Case> bigramCases() {
//         return Stream.of(
//             new Case("Bài này mình làm được rồi", SupportTopic.STUDY),
//             new Case("Mình đang làm bài tập về nhà", SupportTopic.STUDY)
//         );
//     }
// }