package com.arquisoft.shared.message.constant;

public final class ArtefactosLimits {

    private ArtefactosLimits() {}

    public static final class VersionRepositorioArtefacto {

        private VersionRepositorioArtefacto() {}

        public static final int NOMBRE_DOCUMENTO_MAX = 100;
        public static final int NOMBRE_REPOSITORIO_MAX = 100;
        public static final int NOMBRE_ARCHIVO_MAX = 255;
        public static final int TIPO_CONTENIDO_MAX = 100;
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final int NOMBRE_MAX = 200;
    }
}
