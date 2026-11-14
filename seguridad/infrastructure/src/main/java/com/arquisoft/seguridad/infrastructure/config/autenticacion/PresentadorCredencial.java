package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.springframework.http.HttpHeaders;

public interface PresentadorCredencial {

    void adjuntarCredencial(HttpHeaders cabeceras, String credencial);
}
