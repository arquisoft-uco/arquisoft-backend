package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.security;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.IdentidadAutenticada;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import com.arquisoft.shared.web.identidad.ProveedorIdentidadActual;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class IdentidadActualSeguridadAdapter implements ProveedorIdentidadActual {

    private static final String SIN_IDENTIDAD = "No hay una identidad autenticada";

    @Override
    public IdentidadActual obtener() {
        var identidad = IdentidadAutenticada.desde(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(SIN_IDENTIDAD));
        var id = UtilUUID.generarUUIDDesdeTexto(identidad.sujeto());
        if (UtilObjeto.esNulo(id)) {
            throw new AuthenticationCredentialsNotFoundException(SIN_IDENTIDAD);
        }
        return new IdentidadActual(
                id,
                identidad.correo(),
                identidad.nombreUsuario(),
                identidad.roles(),
                identidad.idToken(),
                identidad.expiraEn());
    }
}
