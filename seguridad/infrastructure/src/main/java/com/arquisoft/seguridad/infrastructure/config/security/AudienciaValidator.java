package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Set;

public class AudienciaValidator implements OAuth2TokenValidator<Jwt> {

    private final Set<String> expectedAudiences;
    private final OAuth2Error error;

    public AudienciaValidator(Collection<String> expectedAudiences) {
        this.expectedAudiences = Set.copyOf(expectedAudiences);
        this.error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_TOKEN,
                Mensajes.formatear(IniciarSesionKey.ERROR_AUDIENCIA_INVALIDA, String.join(",", expectedAudiences)),
                null);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (expectedAudiences.isEmpty()) {
            return OAuth2TokenValidatorResult.success();
        }
        if (UtilObjeto.noEsNulo(jwt.getAudience())
                && jwt.getAudience().stream().anyMatch(expectedAudiences::contains)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(error);
    }
}
