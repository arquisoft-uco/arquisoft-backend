package com.arquisoft.repositorio_artefactos.application.administrador.command.usecase;

import com.arquisoft.repositorio_artefactos.application.administrador.command.result.RemocionAdministradorResult;
import com.arquisoft.repositorio_artefactos.domain.administrador.AdministradorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverAdministradorUseCase extends UseCase<AdministradorDomain, RemocionAdministradorResult> {
}
