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
}
