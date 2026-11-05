package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties.Emisor;
import com.arquisoft.shared.util.UtilTexto;
import com.nimbusds.jwt.JWTParser;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableConfigurationProperties(AutenticacionProperties.class)
public class AutenticacionPorEmisorConfig {

    public static final String BEAN_GESTOR_BEARER = "seguridadAutenticacionBearerManager";

    private static final String EMISOR_DESCONOCIDO = "Emisor del token no configurado o deshabilitado";
    private static final String TOKEN_ILEGIBLE = "Token ilegible";

    @Bean
    public JwtDecoder seguridadJwtDecoder(DecodificadoresPorEmisor decodersPorEmisor) {
        return decodersPorEmisor.decoderMultiEmisor();
    }

    @Bean(BEAN_GESTOR_BEARER)
    public AuthenticationManager seguridadAutenticacionBearerManager(
            DecodificadoresPorEmisor decodersPorEmisor, AutenticacionProperties propiedades) {
        var managers = new ConcurrentHashMap<String, AuthenticationManager>();
        return autenticacion -> {
            var emisor = propiedades.porIssuer(issuerDe(((BearerTokenAuthenticationToken) autenticacion).getToken()))
                    .orElseThrow(() -> new InvalidBearerTokenException(EMISOR_DESCONOCIDO));
            return managers.computeIfAbsent(emisor.id(), id -> manager(emisor, decodersPorEmisor))
                    .authenticate(autenticacion);
        };
    }

    private static String issuerDe(String token) {
        try {
            return JWTParser.parse(token).getJWTClaimsSet().getIssuer();
        } catch (ParseException e) {
            throw new InvalidBearerTokenException(TOKEN_ILEGIBLE, e);
        }
    }

    private static AuthenticationManager manager(Emisor emisor, DecodificadoresPorEmisor decodersPorEmisor) {
        var provider = new JwtAuthenticationProvider(decodersPorEmisor.decoderDe(emisor));
        provider.setJwtAuthenticationConverter(jwt -> toToken(jwt, emisor));
        return new ProviderManager(provider);
    }

    private static BearerAuthenticationToken toToken(Jwt jwt, Emisor emisor) {
        var claims = emisor.claims();
        var correo = ExtractorClaims.texto(jwt.getClaims(), claims.correo());
        var nombreUsuario = ExtractorClaims.texto(jwt.getClaims(), claims.nombreUsuario());
        return new BearerAuthenticationToken(
                emisor.id(),
                ExtractorClaims.texto(jwt.getClaims(), claims.id()),
                correo,
                UtilTexto.esVacioONulo(nombreUsuario) ? correo : nombreUsuario,
                ExtractorClaims.lista(jwt.getClaims(), claims.roles()),
                jwt.getId(),
                jwt.getExpiresAt());
    }
}
