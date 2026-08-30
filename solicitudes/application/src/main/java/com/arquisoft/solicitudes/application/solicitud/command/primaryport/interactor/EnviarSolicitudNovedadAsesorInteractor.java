package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;

import java.util.UUID;

public interface EnviarSolicitudNovedadAsesorInteractor
        extends Interactor<EnviarSolicitudNovedadAsesorCommand, UUID> {}
