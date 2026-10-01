package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

public record IniciarSesionRequestDTO(String email, String contrasena) {

    private static final String MASCARA_CONTRASENA = "****";
    private static final String FORMATO = "IniciarSesionRequestDTO[email=%s, contrasena=%s]";

    // El toString que genera el compilador para un record incluye todos sus componentes:
    // un log del DTO volcaria la contrasena en claro.
    @Override
    public String toString() {
        return FORMATO.formatted(email, MASCARA_CONTRASENA);
    }
}
