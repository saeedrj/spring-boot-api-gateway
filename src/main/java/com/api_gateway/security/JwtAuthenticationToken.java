package com.api_gateway.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String token;
    private final String[] requestPath;


    public JwtAuthenticationToken(String token , String[] requestPath) {
        super(null);
        this.principal = null;
        this.token = token;
        this.requestPath = requestPath;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal, String token, String[] requestPath, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        this.requestPath = requestPath;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
