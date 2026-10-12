package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;

import java.util.UUID;

public interface EnviarSolicitudCambioAsesorInteractor
        extends Interactor<EnviarSolicitudCambioAsesorCommand, UUID> {}
