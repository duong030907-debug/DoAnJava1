package com.tdc.nhom6.doanjava1.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Băm mật khẩu bằng BCrypt (thay SHA-256 thuần).
 *
 * BCrypt tự sinh salt ngẫu nhiên mỗi lần hash → cùng một mật khẩu
 * sẽ tạo ra chuỗi hash khác nhau, chống rainbow-table attack.
 * Đây là thuật toán chuẩn cho lưu trữ mật khẩu trong thực tế.
 *
 * Dependency: spring-security-crypto (không cần cài toàn bộ Spring Security).
 */
public final class PasswordUtil {

    // BCryptPasswordEncoder là thread-safe, có thể dùng chung một instance
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private PasswordUtil() {}

    /** Băm mật khẩu thô thành chuỗi BCrypt 60 ký tự. */
    public static String hash(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * Kiểm tra mật khẩu thô với chuỗi đã băm.
     * BCrypt tự extract salt từ chuỗi hash nên không cần lưu salt riêng.
     */
    public static boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) return false;
        return ENCODER.matches(rawPassword, hashedPassword);
    }
}
