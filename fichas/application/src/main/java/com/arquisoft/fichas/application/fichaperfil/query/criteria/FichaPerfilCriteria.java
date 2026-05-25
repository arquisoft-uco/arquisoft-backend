package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class FichaPerfilCriteria extends QueryCriteria {

    public enum Campo {
        TITULO_PROYECTO("tituloProyecto", true,  true),
        ASESOR_NOMBRE  ("asesorNombre",   true,  true),
        ASESOR_EMAIL   ("asesorEmail",    true,  true),
        ASESOR_ID      ("asesorId",       true,  false);

        private final String  clave;
        private final boolean filtrable;
        private final boolean ordenable;

        Campo(String clave, boolean filtrable, boolean ordenable) {
            this.clave     = clave;
            this.filtrable = filtrable;
            this.ordenable = ordenable;
        }

        public String getClave() { return clave; }

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

    private FichaPerfilCriteria(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        @Override
        protected Set<String> camposFiltrables() { return Campo.CLAVES_FILTRABLES; }

        @Override
        protected Set<String> camposOrdenables() { return Campo.CLAVES_ORDENABLES; }

        public FichaPerfilCriteria build() { return new FichaPerfilCriteria(this); }
    }
}
