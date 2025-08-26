package com.si.mindhealth.filters;

import com.si.mindhealth.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final AntPathMatcher matcher = new AntPathMatcher();

    // Các URL public
    private static final String[] PUBLIC_PATTERNS = new String[] {
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private boolean isPublic(HttpServletRequest req) {
        String path = req.getServletPath();
        String method = req.getMethod();

        // Chỉ cho phép POST /api/users là public
        if ("POST".equalsIgnoreCase(method) && matcher.match("/api/users", path)) {
            return true;
        }
        // Các pattern khác
        for (String p : PUBLIC_PATTERNS) {
            if (matcher.match(p, path))
                return true;
        }
        return false;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return isPublic(request);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse rsp,
            @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
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
            if (!"access".equals(JwtUtils.getType(token))) {
                rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sai loại token (cần access token).");
                return;
            }

            String username = JwtUtils.getSubject(token);
            String role = JwtUtils.getRole(token);

            var auth = new UsernamePasswordAuthenticationToken(
                    username, null,
                    role != null ? List.of(new SimpleGrantedAuthority(role)) : List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, rsp);
        } catch (Exception e) {
            rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc hết hạn");
        }
    }
}
