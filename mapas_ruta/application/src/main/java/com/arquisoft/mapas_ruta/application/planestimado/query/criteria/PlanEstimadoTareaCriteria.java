package com.arquisoft.mapas_ruta.application.planestimado.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PlanEstimadoTareaCriteria extends QueryCriteria {

    public enum Campo {
        TIEMPO_ESTIMADO_DIAS ("tiempoEstimadoDias",  true, true),
        DESCRIPCION_ESTIMADO ("descripcionEstimado", true, true);

        private final String  clave;
        private final boolean filtrable;
        private final boolean ordenable;

        Campo(String clave, boolean filtrable, boolean ordenable) {
            this.clave     = clave;
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

    private final UUID tarea;

    private PlanEstimadoTareaCriteria(Builder b) {
        super(b);
        this.tarea = b.tarea;
    }

    public UUID getTarea() {
        return tarea;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID tarea;

        public Builder tarea(UUID tarea) {
            this.tarea = tarea;
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

        public PlanEstimadoTareaCriteria build() {
            return new PlanEstimadoTareaCriteria(this);
        }
    }
}
