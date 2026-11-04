package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;

import java.util.UUID;

public interface ResponderSolicitudCambioAsesorInteractor
        extends Interactor<ResponderSolicitudCambioAsesorCommand, UUID> {}
