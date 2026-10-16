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

    public static final class VersionRepositorioArtefacto {

        private VersionRepositorioArtefacto() {}

        public static final String TAG_NAME = "Versiones de repositorios de artefactos";
        public static final String TAG_DESCRIPTION =
                "Carga de nuevas versiones de plantillas en un repositorio de artefactos";
        public static final String SOLICITAR_CARGA_SUMMARY = "Solicitar la carga de una versión";
        public static final String SOLICITAR_CARGA_DESCRIPTION =
                "Entrega un formulario firmado para subir el archivo directamente al almacenamiento de objetos";
        public static final String SOLICITAR_CARGA_RESP_200 = "Formulario de carga generado";
        public static final String SOLICITAR_CARGA_RESP_400 = "Datos de entrada inválidos";
        public static final String SOLICITAR_CARGA_RESP_422 =
                "Administrador no vigente, repositorio inexistente, tipo o tamaño de archivo no permitido";
        public static final String SOLICITAR_CARGA_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String SUBIR_SUMMARY = "Confirmar la subida de una nueva versión";
        public static final String SUBIR_DESCRIPTION =
                "Valida el archivo subido y registra la versión del documento en el repositorio";
        public static final String SUBIR_RESP_201 = "Versión registrada";
        public static final String SUBIR_RESP_400 = "Datos de entrada inválidos";
        public static final String SUBIR_RESP_422 =
                "Carga inexistente o ajena, archivo inválido o incoherencia con el documento declarado";
        public static final String SUBIR_RESP_503 = "Almacenamiento de objetos no disponible";
        public static final String PARAM_REPOSITORIO = "Identificador del repositorio de artefactos";
    }
}
