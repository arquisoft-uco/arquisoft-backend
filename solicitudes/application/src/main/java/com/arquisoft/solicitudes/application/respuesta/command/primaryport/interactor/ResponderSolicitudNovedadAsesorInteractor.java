package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;

import java.util.UUID;

public interface ResponderSolicitudNovedadAsesorInteractor
        extends Interactor<ResponderSolicitudNovedadAsesorCommand, UUID> {}
