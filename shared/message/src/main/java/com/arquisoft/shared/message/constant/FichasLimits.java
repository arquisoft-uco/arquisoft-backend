package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud y cardinalidad del contexto fichas.
 *
 * <p>No pueden salir al bundle: se usan en {@code @Size(max = ...)}, y el compilador exige que el
 * valor de un atributo de anotación sea una expresión constante (JLS §9.7.1). Los mensajes que
 * acompañan a esas restricciones sí están externalizados, en
 * {@code ValidationMessages.properties}, donde toman el número del propio {@code {max}} — de modo
 * que cambiar un límite aquí actualiza también el texto que ve el usuario.
 */
public final class FichasLimits {

    private FichasLimits() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        public static final int TITULO_MAX = 100;
        public static final int ESTUDIANTES_MAX = 3;
    }

    public static final class ItemFichaPerfil {

        private ItemFichaPerfil() {}

        public static final int CONTENIDO_MAX = 7000;
    }

    public static final class EstadoEvaluacionFicha {

        private EstadoEvaluacionFicha() {}

        public static final int ESTADO_MAX = 50;
    }
}
