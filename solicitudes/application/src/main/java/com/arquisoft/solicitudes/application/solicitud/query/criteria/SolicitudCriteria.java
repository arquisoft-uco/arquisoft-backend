package com.arquisoft.solicitudes.application.solicitud.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class SolicitudCriteria extends QueryCriteria {

    public enum Campo {
        REMITENTE_IDENTIFICADOR("remitenteIdentificador", true,  false),
        REMITENTE_NOMBRE       ("remitenteNombre",        true,  true),
        REMITENTE_EMAIL        ("remitenteEmail",         true,  false),
        FECHA_CREACION         ("fechaCreacion",          false, true),
        DESTINATARIO_USUARIO_ID("destinatarioUsuarioId",  true,  false),
        TIPO_SOLICITUD_ID      ("tipoSolicitudId",        true,  false),
        REMITENTE_USUARIO_ID   ("remitenteUsuarioId",     true,  false);

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

    private SolicitudCriteria(Builder b) {
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

        public SolicitudCriteria build() {
            return new SolicitudCriteria(this);
        }
    }
}
