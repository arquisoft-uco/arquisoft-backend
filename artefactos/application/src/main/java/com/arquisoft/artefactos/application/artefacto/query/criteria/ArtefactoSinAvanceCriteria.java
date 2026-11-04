package com.arquisoft.artefactos.application.artefacto.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ArtefactoSinAvanceCriteria extends QueryCriteria {

    public enum Campo {
        PROYECTO_GRADO                ("proyectoGrado",               true,  false),
        NOMBRE_PROYECTO               ("nombreProyecto",              true,  true),
        VERSION_REPOSITORIO_ARTEFACTO ("versionRepositorioArtefacto", true,  false),
        NOMBRE_DOCUMENTO              ("nombreDocumento",             true,  true),
        VERSION_DOCUMENTO             ("versionDocumento",            true,  true),
        NOMBRE_REPOSITORIO            ("nombreRepositorio",           true,  true),
        ESTADO_ULTIMA_REVISION        ("estadoUltimaRevision",        true,  true),
        FECHA_ULTIMA_VERSION          ("fechaUltimaVersion",          false, true);

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

    private final UUID asesor;
    private final Instant fechaLimite;

    private ArtefactoSinAvanceCriteria(Builder b) {
        super(b);
        this.asesor = b.asesor;
        this.fechaLimite = b.fechaLimite;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public Instant getFechaLimite() {
        return fechaLimite;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        private UUID asesor;
        private Instant fechaLimite;

        public Builder asesor(UUID asesor) {
            this.asesor = asesor;
            return this;
        }

        public Builder fechaLimite(Instant fechaLimite) {
            this.fechaLimite = fechaLimite;
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

        public ArtefactoSinAvanceCriteria build() {
            return new ArtefactoSinAvanceCriteria(this);
        }
    }
}
