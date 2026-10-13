package com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ObservacionItemJuradoCriteria extends QueryCriteria {

    public enum Campo {
        DESCRIPCION("descripcion", true, true);

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

    private final UUID evaluacionCuantitativaJurado;

    private ObservacionItemJuradoCriteria(Builder b) {
        super(b);
        this.evaluacionCuantitativaJurado = b.evaluacionCuantitativaJurado;
    }

    public UUID getEvaluacionCuantitativaJurado() {
        return evaluacionCuantitativaJurado;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID evaluacionCuantitativaJurado;

        public Builder evaluacionCuantitativaJurado(UUID evaluacionCuantitativaJurado) {
            this.evaluacionCuantitativaJurado = evaluacionCuantitativaJurado;
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

        public ObservacionItemJuradoCriteria build() {
            return new ObservacionItemJuradoCriteria(this);
        }
    }
}
