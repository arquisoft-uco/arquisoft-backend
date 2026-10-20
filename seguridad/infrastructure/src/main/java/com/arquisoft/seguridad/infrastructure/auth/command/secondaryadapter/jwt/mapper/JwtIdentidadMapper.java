package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt.mapper;

import com.arquisoft.seguridad.application.auth.command.secondaryport.model.IdentidadProveedor;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ExtractorClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtIdentidadMapper {

    private final AutenticacionProperties propiedadesAutenticacion;

    public IdentidadProveedor toModel(Jwt jwt) {
        var claimsEmisor = propiedadesAutenticacion.porIssuer(String.valueOf(jwt.getIssuer()))
                .orElseThrow(() -> new BadJwtException("Emisor no configurado"))
                .claims();
        var claims = jwt.getClaims();
        return new IdentidadProveedor(
                ExtractorClaims.texto(claims, claimsEmisor.id()),
                ExtractorClaims.texto(claims, claimsEmisor.correo()),
                ExtractorClaims.texto(claims, claimsEmisor.nombre()),
                ExtractorClaims.lista(claims, claimsEmisor.roles()));
    }
}
