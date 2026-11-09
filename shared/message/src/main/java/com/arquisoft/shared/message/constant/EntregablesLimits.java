package com.arquisoft.shared.message.constant;

public final class EntregablesLimits {

    private EntregablesLimits() {}

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final int ULTIMA_VERSION_MIN = 0;
    }

    public static final class ArtefactoEntregable {

        private ArtefactoEntregable() {}

        public static final int NOMBRE_DOCUMENTO_MAX = 100;
        public static final int NOMBRE_REPOSITORIO_MAX = 100;
        public static final int URI_ARCHIVO_MAX = 2000;
    }
}
