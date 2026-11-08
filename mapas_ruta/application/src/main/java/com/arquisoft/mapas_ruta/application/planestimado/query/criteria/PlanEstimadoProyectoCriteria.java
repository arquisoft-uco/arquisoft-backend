package com.arquisoft.mapas_ruta.application.planestimado.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PlanEstimadoProyectoCriteria extends QueryCriteria {

    public enum Campo {
        TIEMPO_ESTIMADO_DIAS ("tiempoEstimadoDias",  true, true),
        DESCRIPCION_ESTIMADO ("descripcionEstimado", true, true),
        DESCRIPCION_TAREA    ("descripcionTarea",    true, true);

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

    private final UUID proyectoGrado;

    private PlanEstimadoProyectoCriteria(Builder b) {
        super(b);
        this.proyectoGrado = b.proyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID proyectoGrado;

        public Builder proyectoGrado(UUID proyectoGrado) {
            this.proyectoGrado = proyectoGrado;
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

        public PlanEstimadoProyectoCriteria build() {
            return new PlanEstimadoProyectoCriteria(this);
        }
    }
}
