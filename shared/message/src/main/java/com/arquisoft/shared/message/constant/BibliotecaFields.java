package com.arquisoft.shared.message.constant;

/**
 * Nombres de campo del contexto biblioteca. Ver la nota de {@link FichasFields}.
 */
public final class BibliotecaFields {

    private BibliotecaFields() {}

    public static final class Bibliotecario {

        private Bibliotecario() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class Documentacion {

        private Documentacion() {}

        public static final String ROLES = "roles";
        public static final String DOCUMENTACION = "documentacion";
    }

    public static final class Autor {

        private Autor() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class AutorDocumentacion {

        private AutorDocumentacion() {}

        public static final String DOCUMENTACION = "documentacion";
    }
}
