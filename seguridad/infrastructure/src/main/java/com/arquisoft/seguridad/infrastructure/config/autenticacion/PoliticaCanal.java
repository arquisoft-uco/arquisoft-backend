package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import java.util.Optional;

public interface PoliticaCanal {

    boolean exigeProteccionCsrf();

    boolean autenticaPrimero();

    boolean actuaEnRutasNoDeNegocio();

    Optional<ModoAutorizacion> autorizacion(String emisor);

    Optional<String> emisorHabilitado(String selector);

    boolean declaraEmisor(String selector);
}
