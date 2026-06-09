package com.arquisoft.shared.message;

public final class SeguridadMessages {

    private SeguridadMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Usuario
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Usuario {

        private Usuario() {}

        // Logs
        public static final String LOG_CREADO = "Usuario creado: id={} email={} rol={}";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sesion
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Sesion {

        private Sesion() {}

        // (Pendiente — agregar campos, códigos y logs cuando aparezcan strings que centralizar.)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Token
    // ─────────────────────────────────────────────────────────────────────────

    public static final class Token {

        private Token() {}

        // (Pendiente — agregar cuando aparezcan strings que centralizar.)
    }
}
