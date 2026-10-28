package com.arquisoft.shared.message.constant;

public final class MapasRutaLimits {

    private MapasRutaLimits() {}

    public static final class Tarea {

        private Tarea() {}

        public static final int DESCRIPCION_MAX = 200;
    }

    public static final class PlanEstimado {

        private PlanEstimado() {}

        public static final int TIEMPO_ESTIMADO_DIAS_MIN = 1;
        public static final int DESCRIPCION_ESTIMADO_MAX = 200;
    }
}
