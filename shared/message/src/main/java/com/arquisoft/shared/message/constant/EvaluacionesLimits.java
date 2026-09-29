package com.arquisoft.shared.message.constant;

public final class EvaluacionesLimits {

    private EvaluacionesLimits() {}

    public static final class ItemCualitativoJurado {

        private ItemCualitativoJurado() {}

        public static final int NOMBRE_MAX = 100;
        public static final int DESCRIPCION_MAX = 300;
    }

    public static final class CriterioItemCualitativoJurado {

        private CriterioItemCualitativoJurado() {}

        public static final int NOMBRE_MAX = 100;
        public static final int DESCRIPCION_MAX = 300;
    }

    public static final class ItemCuantitativoJurado {

        private ItemCuantitativoJurado() {}

        public static final int NOMBRE_MAX = 100;
        public static final int DESCRIPCION_MAX = 300;
        public static final int VALOR_MIN = 0;
        public static final int VALOR_MAX = 500;
    }
}
