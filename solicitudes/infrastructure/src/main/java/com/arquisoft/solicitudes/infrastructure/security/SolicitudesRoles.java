package com.arquisoft.solicitudes.infrastructure.security;

import com.arquisoft.shared.message.constant.UsuariosRoles;

import java.util.Map;

public final class SolicitudesRoles {

    public static final Map<String, String> NEGOCIO_POR_ROL_IDENTIDAD = Map.of(
            UsuariosRoles.ESTUDIANTE, Negocio.ESTUDIANTE,
            UsuariosRoles.COORDINADOR, Negocio.COORDINADOR,
            UsuariosRoles.ASESOR, Negocio.ASESOR,
            UsuariosRoles.ADMINISTRADOR, Negocio.ADMINISTRADOR);

    private SolicitudesRoles() {}

    public static final class Negocio {

        private Negocio() {}

        public static final String ESTUDIANTE = "ESTUDIANTE";
        public static final String COORDINADOR = "COORDINADOR";
        public static final String ASESOR = "ASESOR";
        public static final String ADMINISTRADOR = "ADMINISTRADOR";
    }
}
