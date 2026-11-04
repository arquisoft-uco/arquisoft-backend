package com.arquisoft.artefactos.application.artefacto.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class ArtefactoEstudianteCriteria extends QueryCriteria {

    public enum Campo {
        PROYECTO_GRADO                ("proyectoGrado",               true, false),
        NOMBRE_PROYECTO               ("nombreProyecto",              true, true),
        VERSION_REPOSITORIO_ARTEFACTO ("versionRepositorioArtefacto", true, false),
        NOMBRE_DOCUMENTO              ("nombreDocumento",             true, true),
        VERSION_DOCUMENTO             ("versionDocumento",            true, true),
        NOMBRE_REPOSITORIO            ("nombreRepositorio",           true, true),
        ASESOR                        ("asesor",                      true, false),
        NOMBRE_ASESOR                 ("nombreAsesor",                true, true),
        ESTUDIANTE_ID                 ("estudianteId",                true, false);

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

    private ArtefactoEstudianteCriteria(Builder b) {
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

        public ArtefactoEstudianteCriteria build() {
            return new ArtefactoEstudianteCriteria(this);
        }
    }
}
