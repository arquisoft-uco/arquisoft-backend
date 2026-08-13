package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto usuarios. Ver la nota de {@link AppCodes}.
 */
public final class UsuariosCodes {

    private UsuariosCodes() {}

    public static final class Usuario {

        private Usuario() {}

        public static final String USUARIO_EMAIL_DUPLICADO = "USUARIO_EMAIL_DUPLICADO";
        public static final String ROL_NO_ENCONTRADO = "USUARIO_ROL_NO_ENCONTRADO";
    }
}
