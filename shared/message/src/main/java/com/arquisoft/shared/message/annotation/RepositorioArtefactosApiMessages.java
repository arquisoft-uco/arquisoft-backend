package com.arquisoft.shared.message.annotation;

public final class RepositorioArtefactosApiMessages {

    private RepositorioArtefactosApiMessages() {}

    public static final class Comun {

        private Comun() {}

        public static final String RESP_401 = "No autenticado";
        public static final String RESP_403 = "Sin permisos para realizar la operación";
    }

    public static final class RepositorioArtefacto {

        private RepositorioArtefacto() {}

        public static final String TAG_NAME = "Repositorios de artefactos";
        public static final String TAG_DESCRIPTION = "Administración de los repositorios de artefactos";
        public static final String GENERAR_SUMMARY = "Generar nuevo repositorio de artefactos";
        public static final String GENERAR_DESCRIPTION =
                "Genera un repositorio de artefactos que queda a cargo del administrador autenticado";
        public static final String GENERAR_RESP_201 = "Repositorio de artefactos generado";
        public static final String GENERAR_RESP_400 = "Datos de entrada inválidos";
        public static final String GENERAR_RESP_422 =
                "Datos inválidos, administrador no vigente o repositorio duplicado";
    }
}
