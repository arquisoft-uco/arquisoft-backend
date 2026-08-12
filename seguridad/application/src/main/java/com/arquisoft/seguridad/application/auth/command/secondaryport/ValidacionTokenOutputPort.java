package com.arquisoft.seguridad.application.auth.command.secondaryport;

import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;

public interface ValidacionTokenOutputPort {

    boolean validarToken(String token);

    IdentidadToken extraerInfo(String token);
}
