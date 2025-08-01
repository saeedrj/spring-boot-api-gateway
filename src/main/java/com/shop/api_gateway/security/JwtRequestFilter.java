package com.shop.api_gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.api_gateway.dto.ResponseDto;
import com.shop.api_gateway.dto.enumDto.EnumResult;
import com.shop.api_gateway.service.impl.RedisService;
import com.shop.api_gateway.service.impl.UserDetailsServiceImpl;
import com.shop.api_gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                returnFilter(response);
                return;
            } catch (Exception e) {
                returnFilter(response);
                log.error("Error extracting username from JWT: {}", e.getMessage());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails)) {
                Claims claims = jwtUtil.extractAllClaims(jwt);
                List<String> permissions = (List<String>) claims.get("permissions");
                String jti = (claims.get("jti").toString() == null) ? null : claims.get("jti").toString();
                String getJwi = (redisService.getDeviceIdForJti(claims.get("jti").toString()) == null) ? null : redisService.getDeviceIdForJti(claims.get("jti").toString());

                if (jti == null || getJwi == null || !getJwi.equalsIgnoreCase(request.getHeader("User-Agent"))) {
                    returnFilter(response);
                    return;
                }

                String args[] = request.getRequestURI().split("/");
                String path = "/"+args[1]+"/"+args[2];
                if (permissions == null || permissions.stream().noneMatch(path::equalsIgnoreCase)) {
                    //TODO log Security
                    returnGrantedFilter(response);
                    return;
                }

                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void returnFilter(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ResponseDto errorResponse = new ResponseDto(EnumResult.FORBIDDEN);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private void returnGrantedFilter(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ResponseDto errorResponse = new ResponseDto(EnumResult.UNAUTHORIZED);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}