package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud y cardinalidad del contexto fichas.
 *
 * <p>Viajan como argumento plano a {@code ValidatorLongitud}/{@code ValidatorColeccion} dentro de
 * {@code {Command}.crear(...)}, nunca como atributo de una anotación Jakarta ({@code fichas} no
 * anota sus DTO — ver "DTOs" en {@code CLAUDE.md}). El mensaje de error que produce esa llamada
 * sale del catálogo normal ({@code ValidadorKey}), interpolando este mismo valor.
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

    public static final class RevisionItem {

        private RevisionItem() {}

        public static final int ESTADO_MAX = 50;
    }
}
