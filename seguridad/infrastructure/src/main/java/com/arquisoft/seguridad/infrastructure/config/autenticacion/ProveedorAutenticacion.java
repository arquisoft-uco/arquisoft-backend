package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

public interface ProveedorAutenticacion {

    String id();

    boolean habilitado();

    Optional<ModoAutorizacion> autorizacion(String emisor);

    boolean exigeProteccionCsrf();

    boolean autenticaPrimero();

    boolean actuaEnRutasNoDeNegocio();

    Optional<String> extraerCredencial(HttpServletRequest request);

    void adjuntarCredencial(HttpHeaders cabeceras, String credencial);

    IdentidadAutenticada autenticar(String credencial);
}
