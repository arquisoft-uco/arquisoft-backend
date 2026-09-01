package com.arquisoft.usuarios.application.usuario.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface CrearUsuarioUseCase extends UseCase<UsuarioDomain, UUID> {}
