package com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class EvaluacionFichaPerfilAsesorCriteria extends QueryCriteria {

    public enum Campo {
        FICHA_PERFIL        ("fichaPerfil",        true,  false),
        TITULO_PROYECTO     ("tituloProyecto",     true,  true),
        ESTADO_EVALUACION   ("estadoEvaluacion",   true,  false),
        REPRESENTANTE_COMITE("representanteComite", true,  false),
        FECHA_CREACION      ("fechaCreacion",      true,  true),
        ASESOR_FICHA        ("asesorFicha",        true,  false);

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

    private EvaluacionFichaPerfilAsesorCriteria(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        @Override
        protected Set<String> camposFiltrables() {
            return Campo.CLAVES_FILTRABLES;
        }

        @Override
        protected Set<String> camposOrdenables() {
            return Campo.CLAVES_ORDENABLES;
        }

        public EvaluacionFichaPerfilAsesorCriteria build() {
            return new EvaluacionFichaPerfilAsesorCriteria(this);
        }
    }
}
