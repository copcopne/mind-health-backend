package com.si.mindhealth.filters;

import com.si.mindhealth.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class JwtFilter implements Filter {

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/api/auth/login",
            "/api/auth/refresh",
            "/api/register"
    );

    private boolean isWhitelisted(String path) {
        for (String p : PUBLIC_PREFIXES) {
            if (path.equals(p) || path.startsWith(p)) return true;
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) req;
        HttpServletResponse rsp = (HttpServletResponse) res;

        String path = http.getRequestURI().substring(http.getContextPath().length());

        if (isWhitelisted(path)) {
            chain.doFilter(req, res);
            return;
        }

        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String token = header.substring(7);
        try {
            if (!JwtUtils.isValid(token)) {
                rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc hết hạn");
                return;
            }
            // Chỉ chấp nhận access token
            String typ = JwtUtils.getType(token);
            if (!"access".equals(typ)) {
                rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sai loại token (cần access token).");
                return;
            }

            String username = JwtUtils.getSubject(token);
            String role = JwtUtils.getRole(token);

            var auth = new UsernamePasswordAuthenticationToken(
                    username, null,
                    role != null ? List.of(new SimpleGrantedAuthority(role)) : List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);
        } catch (Exception e) {
            rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc hết hạn");
        }
    }
}
