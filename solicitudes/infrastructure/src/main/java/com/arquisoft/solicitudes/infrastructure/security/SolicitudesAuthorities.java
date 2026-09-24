package com.arquisoft.solicitudes.infrastructure.security;

public final class SolicitudesAuthorities {

    private SolicitudesAuthorities() {}

    public static final String SOLICITUD_CREATE = "solicitudes:solicitud:create";
    public static final String SOLICITUD_NOVEDAD_ASESOR_CREATE = "solicitudes:solicitud-novedad-asesor:create";
    public static final String SOLICITUD_CAMBIO_ASESOR_CREATE = "solicitudes:solicitud-cambio-asesor:create";
    public static final String SOLICITUD_AMPLIACION_PLAZO_CREATE = "solicitudes:solicitud-ampliacion-plazo:create";
    public static final String SOLICITUD_NOVEDAD_COORDINADOR_DELETE =
            "solicitudes:solicitud-novedad-coordinador:delete";
    public static final String SOLICITUD_NOVEDAD_ASESOR_DELETE =
            "solicitudes:solicitud-novedad-asesor:delete";
    public static final String SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDA_VIEW =
            "solicitudes:solicitud-novedad-coordinador-recibida:view";
    public static final String SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW =
            "solicitudes:solicitud-novedad-coordinador-enviada:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN    = "')";

        public static final String HAS_SOLICITUD_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_ASESOR_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_ASESOR_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_CAMBIO_ASESOR_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_CAMBIO_ASESOR_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_AMPLIACION_PLAZO_CREATE =
                HAS_AUTHORITY_INICIO + SOLICITUD_AMPLIACION_PLAZO_CREATE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_COORDINADOR_DELETE =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_COORDINADOR_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_ASESOR_DELETE =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_ASESOR_DELETE + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDA_VIEW =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDA_VIEW + HAS_AUTHORITY_FIN;

        public static final String HAS_SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW =
                HAS_AUTHORITY_INICIO + SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW + HAS_AUTHORITY_FIN;
    }
}
