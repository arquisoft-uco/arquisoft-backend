package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties.Emisor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableConfigurationProperties(AutenticacionProperties.class)
public class AutenticacionPorEmisorConfig {

    @Bean
    public JwtDecoder seguridadJwtDecoder(DecodificadoresPorEmisor decodersPorEmisor) {
        return decodersPorEmisor.decoderMultiEmisor();
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> seguridadAutenticacionPorEmisorResolver(
            DecodificadoresPorEmisor decodersPorEmisor, AutenticacionProperties propiedades) {
        var managers = new ConcurrentHashMap<String, AuthenticationManager>();
        AuthenticationManagerResolver<String> porIssuer = issuer -> propiedades.porIssuer(issuer)
                .map(emisor -> managers.computeIfAbsent(emisor.id(), id -> manager(emisor, decodersPorEmisor)))
                .orElse(null);
        return new JwtIssuerAuthenticationManagerResolver(porIssuer);
    }

    private static AuthenticationManager manager(Emisor emisor, DecodificadoresPorEmisor decodersPorEmisor) {
        var provider = new JwtAuthenticationProvider(decodersPorEmisor.decoderDe(emisor));
        provider.setJwtAuthenticationConverter(jwt -> new JwtAuthenticationToken(
                jwt, List.of(), ExtractorClaims.texto(jwt.getClaims(), emisor.claims().id())));
        return new ProviderManager(provider);
    }
}
