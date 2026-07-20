package com.arquisoft.seguridad.infrastructure.config.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;
    private final OAuth2Error error;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
        this.error = new OAuth2Error(
                "invalid_token",
                "El token no contiene la audiencia requerida: " + expectedAudience,
                null);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience() != null && jwt.getAudience().contains(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}
