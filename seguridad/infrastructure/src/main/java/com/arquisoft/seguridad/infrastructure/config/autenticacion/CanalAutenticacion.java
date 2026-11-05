package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface CanalAutenticacion {

    String id();

    boolean habilitado();

    Optional<String> extraerCredencial(HttpServletRequest request);

    IdentidadAutenticada autenticar(String credencial);
}
