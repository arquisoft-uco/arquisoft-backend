package com.arquisoft.solicitudes.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class TiposSolicitudEnvioAuthorities {

    private static final String NOVEDAD_PARA_EL_COORDINADOR = "NOVEDAD_PARA_EL_COORDINADOR";
    private static final String NOVEDAD_PARA_EL_ASESOR = "NOVEDAD_PARA_EL_ASESOR";
    private static final String CAMBIO_DE_ASESOR = "CAMBIO_DE_ASESOR";
    private static final String AMPLIACION_DE_PLAZO = "AMPLIACION_DE_PLAZO";
    private static final String REGISTRO_Y_MODIFICACION_DE_USUARIOS = "REGISTRO_Y_MODIFICACION_DE_USUARIOS";

    private static final Map<String, String> AUTHORITY_POR_TIPO = Map.of(
            NOVEDAD_PARA_EL_COORDINADOR, SolicitudesAuthorities.SOLICITUD_CREATE,
            NOVEDAD_PARA_EL_ASESOR, SolicitudesAuthorities.SOLICITUD_NOVEDAD_ASESOR_CREATE,
            CAMBIO_DE_ASESOR, SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE,
            AMPLIACION_DE_PLAZO, SolicitudesAuthorities.SOLICITUD_AMPLIACION_PLAZO_CREATE,
            REGISTRO_Y_MODIFICACION_DE_USUARIOS, SolicitudesAuthorities.SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_CREATE);

    private TiposSolicitudEnvioAuthorities() {}

    public static Set<String> tiposPermitidos(Collection<? extends GrantedAuthority> authorities) {
        var concedidas = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AUTHORITY_POR_TIPO.entrySet().stream()
                .filter(entrada -> concedidas.contains(entrada.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }
}
