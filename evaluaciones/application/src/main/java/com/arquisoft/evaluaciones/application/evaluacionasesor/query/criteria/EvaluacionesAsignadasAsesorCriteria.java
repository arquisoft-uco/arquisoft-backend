package com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EvaluacionesAsignadasAsesorCriteria extends QueryCriteria {

    public enum Campo {
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

    private final UUID asesor;

    private EvaluacionesAsignadasAsesorCriteria(Builder b) {
        super(b);
        this.asesor = b.asesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID asesor;

        public Builder asesor(UUID asesor) {
            this.asesor = asesor;
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

        public EvaluacionesAsignadasAsesorCriteria build() {
            return new EvaluacionesAsignadasAsesorCriteria(this);
        }
    }
}
