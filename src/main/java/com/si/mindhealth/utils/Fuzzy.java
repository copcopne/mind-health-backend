package com.si.mindhealth.utils;

import java.util.*;

public final class Fuzzy {
  // Map hàng xóm trên QWERTY (lowercase). Có cả số và một ít dấu phổ biến.
  private static final Map<Character, Set<Character>> NEI = new HashMap<>();
  static {
    addRow("`1234567890-=", new String[] {
        "`1", "12", "23", "34", "45", "56", "67", "78", "89", "90", "0-", "-="
    });
    addRow("qwertyuiop[]\\", new String[] {
        "q w", "w qe", "e wr", "r et", "t ry", "y tu", "u yi", "i uo", "o ip", "p o[", "[ p]", " ]\\"
    });
    addRow("asdfghjkl;'", new String[] {
        "a s", "s ad", "d sf", "f dg", "g fh", "h gj", "j hk", "k jl", "l k;", "; l'"
    });
    addRow("zxcvbnm,./", new String[] {
        "z x", "x zc", "c xv", "v cb", "b vn", "n bm", "m n, ", ", m.", ". ,/"
    });
    // thêm hàng xóm theo chiều chéo giữa các hàng (tối giản, đủ xài)
    addDiag("1", "`2");
    addDiag("2", "13");
    addDiag("3", "24");
    addDiag("4", "35");
    addDiag("5", "46");
    addDiag("6", "57");
    addDiag("7", "68");
    addDiag("8", "79");
    addDiag("9", "80");
    addDiag("0", "9-");

    addDiag("q", "12wa");
    addDiag("w", "23qes");
    addDiag("e", "34wrd");
    addDiag("r", "45eft");
    addDiag("t", "56rgy");
    addDiag("y", "67tuh");
    addDiag("u", "78yij");
    addDiag("i", "89uok");
    addDiag("o", "90ipl");
    addDiag("p", "0ol[");
    addDiag("[", "p;]");
    addDiag("]", "['\\");
    addDiag("\\", "]");

    addDiag("a", "qwsz");
    addDiag("s", "weadx");
    addDiag("d", "erfs c");
    addDiag("f", "rtdg v");
    addDiag("g", "tfh b");
    addDiag("h", "y g j n");
    addDiag("j", "u h k m");
    addDiag("k", "i j l ,");
    addDiag("l", "o k ; .");
    addDiag(";", "p l ' /");
    addDiag("'", "[ ;");

    addDiag("z", "asx");
    addDiag("x", "sdzc");
    addDiag("c", "dfxv");
    addDiag("v", "fgcb");
    addDiag("b", "ghvn");
    addDiag("n", "hjbm");
    addDiag("m", "jk n ,");
    addDiag(",", "kl m .");
    addDiag(".", "; , /");
    addDiag("/", "' .");
  }

  private static void addRow(String row, String[] pairs) {
    // neighbors theo trái-phải trong cùng hàng
    for (char c : row.toCharArray())
      NEI.putIfAbsent(c, new HashSet<>());
    for (String p : pairs) {
      String s = p.replace(" ", "");
      char a = s.charAt(0), b = s.charAt(1);
      NEI.get(a).add(b);
      NEI.get(b).add(a);
    }
  }

  private static void addDiag(String center, String neigh) {
    char c = center.charAt(0);
    NEI.putIfAbsent(c, new HashSet<>());
    for (char n : neigh.replace(" ", "").toCharArray()) {
      NEI.putIfAbsent(n, new HashSet<>());
      NEI.get(c).add(n);
      NEI.get(n).add(c);
    }
  }

  private static boolean neighbor(char a, char b) {
    if (a == b)
      return true;
    a = Character.toLowerCase(a);
    b = Character.toLowerCase(b);
    return NEI.getOrDefault(a, Collections.emptySet()).contains(b);
  }

  public static boolean close(String a, String b) {
    if (a == null || b == null)
      return false;
    if (a.equals(b))
      return true;

    int len = Math.max(a.length(), b.length());
    if (len <= 3)
      return false;

    int d = plainLevenshtein(a, b);
    if (d == 0)
      return true;

    if (d == 1)
      return isOneNeighborEdit(a, b);

    // chỉ cho transpose nếu từ ngắn
    if (d == 2 && len <= 6)
      return isTransposeNeighbor(a, b);

    return false;
  }

  // ====== Helpers ======
  private static int plainLevenshtein(String a, String b) {
    int n = a.length(), m = b.length();
    if (n == 0)
      return m;
    if (m == 0)
      return n;
    int[] prev = new int[m + 1], cur = new int[m + 1];
    for (int j = 0; j <= m; j++)
      prev[j] = j;
    for (int i = 1; i <= n; i++) {
      cur[0] = i;
      char ca = a.charAt(i - 1);
      for (int j = 1; j <= m; j++) {
        char cb = b.charAt(j - 1);
        int cost = (ca == cb) ? 0 : 1;
        cur[j] = Math.min(Math.min(
            cur[j - 1] + 1, // insert
            prev[j] + 1), // delete
            prev[j - 1] + cost); // substitute
      }
      int[] tmp = prev;
      prev = cur;
      cur = tmp;
    }
    return prev[m];
  }

  private static boolean isOneNeighborEdit(String a, String b) {
    int n = a.length(), m = b.length();
    int i = 0, j = 0;
    while (i < n && j < m && a.charAt(i) == b.charAt(j)) {
      i++;
      j++;
    }

    // Substitution: same length, 1 khác biệt
    if (n == m && i < n) {
      // phần còn lại sau i phải trùng nhau
      if (a.substring(i + 1).equals(b.substring(i + 1))) {
        return neighbor(a.charAt(i), b.charAt(i));
      }
      return false;
    }

    // Insertion vào b (hoặc deletion ở a): m = n + 1
    if (m == n + 1) {
      // b có thêm 1 ký tự tại vị trí j
      return a.substring(i).equals(b.substring(i + 1)) &&
      // ký tự chèn phải là hàng xóm của ký tự kề bên (trái hoặc phải)
          ((i > 0 && neighbor(b.charAt(i), b.charAt(i - 1))) ||
              (i < m - 1 && neighbor(b.charAt(i), b.charAt(i + 1))));
    }

    // Deletion ở b (hoặc insertion ở a): n = m + 1
    if (n == m + 1) {
      return b.substring(j).equals(a.substring(i + 1)) &&
          ((i > 0 && neighbor(a.charAt(i), a.charAt(i - 1))) ||
              (i < n - 1 && neighbor(a.charAt(i), a.charAt(i + 1))));
    }
    return false;
  }

  // Hoán vị đúng 1 cặp ký tự kề nhau (Damerau), và 2 ký tự đó phải là "neighbor".
  private static boolean isTransposeNeighbor(String a, String b) {
    if (a == null || b == null)
      return false;
    int n = a.length();
    if (n != b.length() || n < 2)
      return false;

    // tìm vị trí khác nhau đầu tiên
    int i = 0;
    while (i < n && a.charAt(i) == b.charAt(i))
      i++;
    // nếu giống hệt hoặc khác nhau ở cuối -> không thể transpose
    if (i >= n - 1)
      return false;

    // kiểm tra mẫu hoán vị: a[i] a[i+1] <-> b[i+1] b[i]
    if (a.charAt(i) != b.charAt(i + 1) || a.charAt(i + 1) != b.charAt(i))
      return false;

    // phần còn lại sau cặp hoán vị phải khớp
    if (i + 2 < n && !a.substring(i + 2).equals(b.substring(i + 2)))
      return false;

    // siết chặt: 2 ký tự hoán vị phải là hàng xóm trên bàn phím
    return neighbor(a.charAt(i), a.charAt(i + 1));
  }
}
