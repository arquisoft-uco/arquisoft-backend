package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;
    private final OAuth2Error error;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
        this.error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_TOKEN,
                Mensajes.formatear(IniciarSesionKey.ERROR_AUDIENCIA_INVALIDA, expectedAudience),
                null);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (!UtilObjeto.esNulo(jwt.getAudience()) && jwt.getAudience().contains(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}
