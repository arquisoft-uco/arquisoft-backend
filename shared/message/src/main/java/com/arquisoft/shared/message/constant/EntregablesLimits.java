package com.arquisoft.shared.message.constant;

public final class EntregablesLimits {

    private EntregablesLimits() {}

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final int ULTIMA_VERSION_MIN = 0;
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final int NOMBRE_MAX = 200;
    }

    public static final class Asesor {

        private Asesor() {}

        public static final int IDENTIFICADOR_MAX = 30;
        public static final int NOMBRE_MAX = 50;
        public static final int EMAIL_MAX = 50;
    }
}
