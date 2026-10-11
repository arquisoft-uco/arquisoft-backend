package com.arquisoft.fichas.infrastructure.security;

public final class FichasRoles {

    private FichasRoles() {}

    public static final class Realm {

        private Realm() {}

        public static final String ASESOR_FICHA = "asesor-ficha";
        public static final String COORDINADOR = "coordinador";
        public static final String REPRESENTANTE_COMITE = "representante-comite";
    }

    public static final class Negocio {

        private Negocio() {}

        public static final String ASESOR_FICHA = "ASESOR_FICHA";
        public static final String COORDINADOR = "COORDINADOR";
        public static final String REPRESENTANTE_COMITE = "REPRESENTANTE_COMITE";
    }
}
