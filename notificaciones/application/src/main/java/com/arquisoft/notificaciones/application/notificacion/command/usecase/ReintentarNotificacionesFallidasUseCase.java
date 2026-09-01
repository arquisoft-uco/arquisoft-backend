package com.arquisoft.notificaciones.application.notificacion.command.usecase;

import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;
import com.arquisoft.notificaciones.domain.notificacion.ReintentoNotificacionesDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ReintentarNotificacionesFallidasUseCase
        extends UseCase<ReintentoNotificacionesDomain, ReintentoNotificacionesResult> {}
