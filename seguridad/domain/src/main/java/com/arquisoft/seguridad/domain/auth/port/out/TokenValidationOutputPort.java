package com.arquisoft.seguridad.domain.auth.port.out;

import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;

public interface TokenValidationOutputPort {

    boolean validarToken(String token);

    IdentidadToken extraerInfo(String token);
}
