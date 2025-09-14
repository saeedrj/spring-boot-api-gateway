package com.api_gateway.security;

import com.api_gateway.PermissionEvaluatorUtil;
import com.api_gateway.service.impl.RedisService;
import com.api_gateway.service.impl.UserDetailsServiceImpl;
import com.api_gateway.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        String token = jwtAuth.getToken();
        String[] path = jwtAuth.getRequestPath();

        String username = jwtUtil.extractUsername(token);
        var userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new BadCredentialsException("Invalid JWT");
        }

        var claims = jwtUtil.extractAllClaims(token);
        if (redisService.getIdLogin(claims.get("jti").toString()) == null) {
            throw new BadCredentialsException("Token revoked");
        }

        if (!PermissionEvaluatorUtil.hasPermission(userDetails.getAuthorities(), path)) {
            throw new BadCredentialsException("Access denied for path: " + path[1] + "/" + path[2]);
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
