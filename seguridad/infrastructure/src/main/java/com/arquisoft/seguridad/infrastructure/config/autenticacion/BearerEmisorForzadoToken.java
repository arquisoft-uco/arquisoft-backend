package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

public class BearerEmisorForzadoToken extends BearerTokenAuthenticationToken {

    private final String emisor;

    public BearerEmisorForzadoToken(String token, String emisor) {
        super(token);
        this.emisor = emisor;
    }

    public String emisor() {
        return emisor;
    }
}
