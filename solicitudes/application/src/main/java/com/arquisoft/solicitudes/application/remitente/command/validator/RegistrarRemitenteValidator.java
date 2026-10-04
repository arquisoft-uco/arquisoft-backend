package com.arquisoft.solicitudes.application.remitente.command.validator;

import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface RegistrarRemitenteValidator {

    void validar(RemitenteDomain remitente, UsuarioDomain usuario);
}
