package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface ProveedorAutenticacion {

    String id();

    boolean habilitado();

    ModoAutorizacion autorizacion();

    boolean exigeProteccionCsrf();

    Optional<String> extraerCredencial(HttpServletRequest request);

    void adjuntarCredencial(HttpHeaders cabeceras, String credencial);

    Authentication autenticar(String credencial);
}
