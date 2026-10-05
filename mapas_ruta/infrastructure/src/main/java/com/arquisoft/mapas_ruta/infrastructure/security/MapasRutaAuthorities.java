package com.arquisoft.mapas_ruta.infrastructure.security;

public final class MapasRutaAuthorities {

    private MapasRutaAuthorities() {}

    public static final String MAPA_RUTA_CREATE = "mapas-ruta:mapa-ruta:create";
    public static final String MAPA_RUTA_ESTUDIANTE_VIEW = "mapas-ruta:mapa-ruta-estudiante:view";
    public static final String MAPA_RUTA_COORDINADOR_VIEW = "mapas-ruta:mapa-ruta-coordinador:view";

    public static final class Expresiones {

        private Expresiones() {}

        private static final String HAS_AUTHORITY_INICIO = "hasAuthority('";
        private static final String HAS_AUTHORITY_FIN = "')";

        public static final String HAS_MAPA_RUTA_CREATE =
                HAS_AUTHORITY_INICIO + MAPA_RUTA_CREATE + HAS_AUTHORITY_FIN;
        public static final String HAS_MAPA_RUTA_ESTUDIANTE_VIEW =
                HAS_AUTHORITY_INICIO + MAPA_RUTA_ESTUDIANTE_VIEW + HAS_AUTHORITY_FIN;
        public static final String HAS_MAPA_RUTA_COORDINADOR_VIEW =
                HAS_AUTHORITY_INICIO + MAPA_RUTA_COORDINADOR_VIEW + HAS_AUTHORITY_FIN;
    }
}
