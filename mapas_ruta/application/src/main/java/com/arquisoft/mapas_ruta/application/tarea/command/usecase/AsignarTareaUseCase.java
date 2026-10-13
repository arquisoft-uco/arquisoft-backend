package com.arquisoft.mapas_ruta.application.tarea.command.usecase;

import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AsignarTareaUseCase extends UseCase<TareaDomain, UUID> {}
