package com.arquisoft.solicitudes.application.usuario.command.usecase;

import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarUsuarioUseCase extends UseCase<UsuarioDomain, ActualizacionUsuarioResult> {
}
