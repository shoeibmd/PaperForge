package com.paperforge.security;

import com.paperforge.model.ApiKey;
import com.paperforge.model.ApiKeyScope;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final ApiKey apiKey;
    private final String principal;

    public ApiKeyAuthenticationToken(ApiKey apiKey) {
        super(extractAuthorities(apiKey));
        this.apiKey = apiKey;
        this.principal = apiKey.getUser().getUsername();
        setAuthenticated(true);
    }

    private static Collection<GrantedAuthority> extractAuthorities(ApiKey apiKey) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Include user roles
        if (apiKey.getUser().getRoles() != null) {
            apiKey.getUser().getRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority(role.getName())));
        }
        // Include scopes prefixed with SCOPE_
        if (apiKey.getScopes() != null) {
            for (ApiKeyScope scope : apiKey.getScopes()) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope.getValue()));
            }
        }
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }
}
