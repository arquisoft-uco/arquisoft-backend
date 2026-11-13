package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto biblioteca. Ver la nota de {@link AppCodes}.
 */
public final class BibliotecaCodes {

    private BibliotecaCodes() {}

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String ID_REQUERIDO = "BIBLIOTECARIO_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "BIBLIOTECARIO_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "BIBLIOTECARIO_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "BIBLIOTECARIO_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "BIBLIOTECARIO_OCURRIDO_EN_REQUERIDO";
    }

    public static final class EstadoDocumentacion {

        private EstadoDocumentacion() {}

        public static final String NO_ENCONTRADO = "ESTADO_DOCUMENTACION_NO_ENCONTRADO";
    }

    public static final class Documentacion {

        private Documentacion() {}

        public static final String ROLES_REQUERIDOS = "DOCUMENTACION_ROLES_REQUERIDOS";
        public static final String DOCUMENTACION_ID_REQUERIDO = "DOCUMENTACION_DOCUMENTACION_ID_REQUERIDO";
        public static final String NO_COMPARTIBLE = "DOCUMENTACION_NO_COMPARTIBLE";
        public static final String NO_DISPONIBLE = "DOCUMENTACION_NO_DISPONIBLE";
    }

    public static final class Autor {

        private Autor() {}

        public static final String ID_REQUERIDO = "AUTOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "AUTOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "AUTOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "AUTOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "AUTOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class AutorDocumentacion {

        private AutorDocumentacion() {}

        public static final String DOCUMENTACION_ID_REQUERIDO = "AUTOR_DOCUMENTACION_DOCUMENTACION_ID_REQUERIDO";
    }
}
