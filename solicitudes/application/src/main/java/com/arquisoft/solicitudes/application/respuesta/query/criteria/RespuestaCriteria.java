package com.arquisoft.solicitudes.application.respuesta.query.criteria;

import com.arquisoft.shared.query.QueryCriteria;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class RespuestaCriteria extends QueryCriteria {

    public enum Campo {
        CONTENIDO                 ("contenido",                 true,  false),
        FECHA_RESPUESTA           ("fechaRespuesta",             false, true),
        ESTADO_RESPUESTA_ID       ("estadoRespuestaId",          true,  false),
        TIPO_SOLICITUD_ID         ("tipoSolicitudId",            true,  false),
        REMITENTE_USUARIO_ID      ("remitenteUsuarioId",         true,  false),
        DESTINATARIO_IDENTIFICADOR("destinatarioIdentificador",  true,  false),
        DESTINATARIO_NOMBRE       ("destinatarioNombre",         true,  true),
        DESTINATARIO_EMAIL        ("destinatarioEmail",          true,  false);

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

    private RespuestaCriteria(Builder b) {
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

        public RespuestaCriteria build() {
            return new RespuestaCriteria(this);
        }
    }
}
