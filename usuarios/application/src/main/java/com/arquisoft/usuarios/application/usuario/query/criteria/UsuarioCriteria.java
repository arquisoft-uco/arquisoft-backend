package com.arquisoft.usuarios.application.usuario.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class UsuarioCriteria extends QueryCriteria {

    public enum Campo {
        IDENTIFICADOR  ("identificador", true, true),
        NOMBRE         ("nombre",        true, true),
        EMAIL          ("email",         true, true),
        CONTACTO       ("contacto",      true, false),
        ESTADO         ("estado",        true, false),
        VIGENTE        ("vigente",       true, false),
        ES_ESTUDIANTE  ("esEstudiante",  true, false),
        ES_ASESOR      ("esAsesor",      true, false),
        ES_ASESOR_FICHA("esAsesorFicha", true, false),
        ES_COORDINADOR ("esCoordinador", true, false),
        ES_REPRESENTANTE_COMITE("esRepresentanteComite", true, false),
        ES_ADMINISTRADOR("esAdministrador", true, false);
        // TODO HU242: ES_BIBLIOTECARIO("esBibliotecario", true, false)
        // TODO HU252: ES_JURADO("esJurado", true, false)
        // Cada constante nueva obliga a cubrirla en los switch de UsuarioJpaSpecification y UsuarioSortMapper.

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

    private UsuarioCriteria(Builder b) {
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

        public UsuarioCriteria build() {
            return new UsuarioCriteria(this);
        }
    }
}
