package com.arquisoft.usuarios.application.usuario.command.usecase;

import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarUsuarioUseCase extends UseCase<RegistroUsuarioDomain, UUID> {}
