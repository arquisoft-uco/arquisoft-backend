package com.arquisoft.solicitudes.infrastructure.security;

public final class SolicitudesRoles {

    private SolicitudesRoles() {}

    public static final class Negocio {

        private Negocio() {}

        public static final String ESTUDIANTE = "ESTUDIANTE";
        public static final String COORDINADOR = "COORDINADOR";
        public static final String ASESOR = "ASESOR";
        public static final String ADMINISTRADOR = "ADMINISTRADOR";
    }
}
