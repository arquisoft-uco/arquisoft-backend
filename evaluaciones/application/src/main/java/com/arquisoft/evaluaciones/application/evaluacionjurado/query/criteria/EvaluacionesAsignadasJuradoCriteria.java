package com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EvaluacionesAsignadasJuradoCriteria extends QueryCriteria {

    public enum Campo {
        ESTADO_JURADO("estadoJurado", true, true),
        ESTADO("estado", true, true),
        PROYECTO("proyecto", true, true),
        VERSION("version", false, true);

        private final String clave;
        private final boolean filtrable;
        private final boolean ordenable;

        Campo(String clave, boolean filtrable, boolean ordenable) {
            this.clave = clave;
            this.filtrable = filtrable;
            this.ordenable = ordenable;
        }

        public String getClave() {
            return clave;
        }

        static final Set<String> CLAVES_FILTRABLES = Arrays.stream(values())
                .filter(c -> c.filtrable)
                .map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        static final Set<String> CLAVES_ORDENABLES = Arrays.stream(values())
                .filter(c -> c.ordenable)
                .map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        public static boolean esValidoParaFiltrar(String clave) {
            return CLAVES_FILTRABLES.contains(clave);
        }

        public static boolean esValidoParaOrdenar(String clave) {
            return CLAVES_ORDENABLES.contains(clave);
        }
    }

    private final UUID jurado;

    private EvaluacionesAsignadasJuradoCriteria(Builder b) {
        super(b);
        this.jurado = b.jurado;
    }

    public UUID getJurado() {
        return jurado;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID jurado;

        public Builder jurado(UUID jurado) {
            this.jurado = jurado;
            return this;
        }

        @Override
        protected Set<String> camposFiltrables() {
            return Campo.CLAVES_FILTRABLES;
        }

        @Override
        protected Set<String> camposOrdenables() {
            return Campo.CLAVES_ORDENABLES;
        }

        public EvaluacionesAsignadasJuradoCriteria build() {
            return new EvaluacionesAsignadasJuradoCriteria(this);
        }
    }
}
