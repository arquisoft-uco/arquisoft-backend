package com.arquisoft.seguridad.application.autorizacion.command.usecase;

import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AutorizarAccesoUseCase extends UseCase<AccesoDomain, DecisionAccesoResult> {}
