package com.arquisoft.seguridad.domain.auth.port.out;

import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;

public interface ValidacionTokenOutputPort {

    boolean validarToken(String token);

    IdentidadToken extraerInfo(String token);
}
