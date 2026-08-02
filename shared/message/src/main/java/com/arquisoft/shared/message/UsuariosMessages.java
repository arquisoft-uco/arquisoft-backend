package com.arquisoft.shared.message;

public final class UsuariosMessages {

    private UsuariosMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Usuario
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Usuario {

        private Usuario() {}

        // Codigos de error
        public static final String USUARIO_EMAIL_DUPLICADO = "USUARIO_EMAIL_DUPLICADO";

        // Mensajes
        public static final String EMAIL_DUPLICADO_MSG = "Ya existe un usuario registrado con el email %s";

        // Logs
        public static final String LOG_CREADO = "Usuario creado: id={} email={} rol={}";
    }
}
