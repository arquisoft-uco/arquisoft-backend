package com.arquisoft.mapas_ruta.application.maparuta.command.usecase;

import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarMapaRutaUseCase extends UseCase<AgregacionMapaRutaDomain, UUID> {}
