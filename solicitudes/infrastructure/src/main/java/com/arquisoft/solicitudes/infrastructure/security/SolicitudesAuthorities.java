package com.arquisoft.solicitudes.infrastructure.security;

public final class SolicitudesAuthorities {

    private SolicitudesAuthorities() {}

    public static final String SOLICITUD_CREATE = "solicitudes:solicitud:create";
    public static final String SOLICITUD_NOVEDAD_ASESOR_CREATE = "solicitudes:solicitud-novedad-asesor:create";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN    = "')";

        public static final String HAS_SOLICITUD_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_ASESOR_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_ASESOR_CREATE + HAS_AUTHORITY_FIN;
    }
}
