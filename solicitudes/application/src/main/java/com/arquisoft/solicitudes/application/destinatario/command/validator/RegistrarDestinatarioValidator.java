package com.arquisoft.solicitudes.application.destinatario.command.validator;

import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface RegistrarDestinatarioValidator {

    void validar(DestinatarioDomain destinatario, UsuarioDomain usuario);
}
