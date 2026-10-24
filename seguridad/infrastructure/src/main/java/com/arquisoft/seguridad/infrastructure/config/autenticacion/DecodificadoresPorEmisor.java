package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.infrastructure.config.security.AudienciaValidator;
import com.arquisoft.shared.util.UtilTexto;
import com.nimbusds.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class DecodificadoresPorEmisor {

    private static final String EMISOR_DESCONOCIDO = "Emisor del token no configurado o deshabilitado";
    private static final String TOKEN_ILEGIBLE = "Token ilegible";

    private final AutenticacionProperties propiedades;
    private final ConcurrentMap<String, JwtDecoder> decoders = new ConcurrentHashMap<>();

    public JwtDecoder decoderDe(AutenticacionProperties.Emisor emisor) {
        return decoders.computeIfAbsent(emisor.id(), id -> construir(emisor));
    }

    public JwtDecoder decoderMultiEmisor() {
        return token -> {
            var emisor = propiedades.porIssuer(extraerIssuer(token))
                    .orElseThrow(() -> new BadJwtException(EMISOR_DESCONOCIDO));
            return decoderDe(emisor).decode(token);
        };
    }

    private JwtDecoder construir(AutenticacionProperties.Emisor emisor) {
        var decoder = UtilTexto.esVacioONulo(emisor.jwksUri())
                ? NimbusJwtDecoder.withIssuerLocation(emisor.issuer()).build()
                : NimbusJwtDecoder.withJwkSetUri(emisor.jwksUri()).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<Jwt>(
                JwtValidators.createDefaultWithIssuer(emisor.issuer()),
                new AudienciaValidator(emisor.audiences())));
        return decoder;
    }

    private static String extraerIssuer(String token) {
        try {
            return JWTParser.parse(token).getJWTClaimsSet().getIssuer();
        } catch (ParseException e) {
            throw new BadJwtException(TOKEN_ILEGIBLE, e);
        }
    }
}
