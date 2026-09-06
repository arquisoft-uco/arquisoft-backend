package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;

import java.util.UUID;

public interface EnviarSolicitudNovedadCoordinadorInteractor
        extends Interactor<EnviarSolicitudNovedadCoordinadorCommand, UUID> {}
