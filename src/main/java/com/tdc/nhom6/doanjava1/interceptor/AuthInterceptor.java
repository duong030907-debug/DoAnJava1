package com.tdc.nhom6.doanjava1.interceptor;

import com.tdc.nhom6.doanjava1.entity.Account;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Kiểm tra đăng nhập tập trung cho toàn bộ ứng dụng.
 *
 * Thay vì kiểm tra session thủ công trong từng controller method,
 * interceptor này chặn request TRƯỚC KHI vào controller:
 *   - Path trong PUBLIC_PATHS → cho qua không cần đăng nhập.
 *   - Path còn lại → phải có session "currentUser", nếu không redirect /login.
 *
 * Kiểm tra quyền theo role (canManage, isAdmin) vẫn nằm trong controller
 * vì logic đó phụ thuộc vào từng hành động cụ thể.
 */
public class AuthInterceptor implements HandlerInterceptor {

    /** Các path GET không cần đăng nhập */
    private static final Set<String> PUBLIC_GET = Set.of(
            "/login", "/register", "/logout",
            "/books", "/documents"
    );

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path   = request.getServletPath();
        String method = request.getMethod();

        // ── Luôn cho qua ────────────────────────────────────────────
        if ("GET".equals(method) && PUBLIC_GET.contains(path))           return true;
        if ("GET".equals(method) && path.endsWith("/cover"))             return true;
        if (path.equals("/login") || path.equals("/register"))           return true;

        // ── Kiểm tra đăng nhập ──────────────────────────────────────
        HttpSession session = request.getSession(false);
        Account user = (session != null)
                ? (Account) session.getAttribute("currentUser")
                : null;

        if (user == null) {
            // Lưu lại URL định đến để redirect sau khi đăng nhập (UX tốt hơn)
            String target = request.getServletPath();
            String query  = request.getQueryString();
            if (query != null) target += "?" + query;
            request.getSession().setAttribute("redirectAfterLogin", target);
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        return true;
    }
}
