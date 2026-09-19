package com.arquisoft.solicitudes.application.usuario.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface RegistrarUsuarioUseCase extends UseCase<UsuarioDomain, AgregacionUsuarioResult> {
}
