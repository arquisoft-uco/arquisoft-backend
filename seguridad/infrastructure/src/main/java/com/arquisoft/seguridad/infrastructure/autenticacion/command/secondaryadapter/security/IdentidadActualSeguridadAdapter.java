package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.security;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ExtractorClaims;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.ProveedorIdentidadActual;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdentidadActualSeguridadAdapter implements ProveedorIdentidadActual {

    private static final String SIN_IDENTIDAD = "No hay una identidad autenticada";

    private final AutenticacionProperties propiedadesAutenticacion;

    @Override
    public IdentidadActual obtener() {
        var actual = SecurityContextHolder.getContext().getAuthentication();
        if (!(actual instanceof JwtAuthenticationToken jwtAuth) || !jwtAuth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException(SIN_IDENTIDAD);
        }
        var jwt = jwtAuth.getToken();
        var emisor = propiedadesAutenticacion.porIssuer(String.valueOf(jwt.getIssuer()))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(SIN_IDENTIDAD));
        var claimsEmisor = emisor.claims();
        var claims = jwt.getClaims();

        var id = UtilUUID.generarUUIDDesdeTexto(ExtractorClaims.texto(claims, claimsEmisor.id()));
        if (UtilObjeto.esNulo(id)) {
            throw new AuthenticationCredentialsNotFoundException(SIN_IDENTIDAD);
        }
        var correo = ExtractorClaims.texto(claims, claimsEmisor.correo());
        var nombreUsuario = ExtractorClaims.texto(claims, claimsEmisor.nombreUsuario());
        return new IdentidadActual(
                id,
                correo,
                UtilTexto.esVacioONulo(nombreUsuario) ? correo : nombreUsuario,
                ExtractorClaims.lista(claims, claimsEmisor.roles()),
                jwt.getId(),
                jwt.getExpiresAt());
    }
}
